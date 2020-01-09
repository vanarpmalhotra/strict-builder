package com.pranavmalhotra.builder.generator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.google.auto.service.AutoService;
import com.pranavmalhotra.builder.annotations.Parameter;
import com.pranavmalhotra.builder.annotations.StrictBuilder;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeSpec;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;

import static com.pranavmalhotra.builder.generator.NamingUtils.buildBuilderClassName;
import static com.pranavmalhotra.builder.generator.NamingUtils.buildPackageName;
import static com.pranavmalhotra.builder.generator.NamingUtils.buildSetterName;
import static com.pranavmalhotra.builder.generator.NamingUtils.getEnclosingElementForType;

@SupportedAnnotationTypes("com.pranavmalhotra.builder.annotations.StrictBuilder")
@AutoService(Processor.class)
public final class StrictBuilderProcessor extends AbstractProcessor {

    private static ParameterLists buildParameterLists(final ExecutableElement element) {
        final ParameterLists parameterLists = new ParameterLists();

        for (final VariableElement parameter : element.getParameters()) {
            final String name = parameter.getSimpleName().toString();
            final TypeMirror typeMirror = parameter.asType();
            final Parameter annotation = parameter.getAnnotation(Parameter.class);
            final String setterName = buildSetterName(name, annotation);

            final ParameterInformation information = new ParameterInformation(name, typeMirror, setterName);

            if (annotation == null || annotation.mandatory()) {
                parameterLists.addMandatoryParameter(information);
            } else {
                parameterLists.addOptionalParameter(information);
            }
        }

        return parameterLists;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public boolean process(final Set<? extends TypeElement> annotations, final RoundEnvironment roundEnv) {
        for (final Element element : roundEnv.getElementsAnnotatedWith(StrictBuilder.class)) {
            if (element.getKind() == ElementKind.CONSTRUCTOR) {
                processElement((ExecutableElement) element);
            } else {
                final String errorMsg = "This element is not supported by StrictBuilder.";
                processingEnv
                    .getMessager()
                    .printMessage(Diagnostic.Kind.ERROR, errorMsg, element);
            }
        }

        return roundEnv.errorRaised();
    }

    private void processElement(final ExecutableElement element) {
        final StrictBuilder annotation = element.getAnnotation(StrictBuilder.class);

        final TypeElement enclosingTypeElement = getEnclosingElementForType(TypeElement.class, element);

        final ParameterLists parameterLists = buildParameterLists(element);

        final BuilderFormatterDto builderFormatterDto = new BuilderFormatterDto(
            buildBuilderClassName(enclosingTypeElement, annotation),
            enclosingTypeElement,
            parameterLists.mandatoryParameters,
            parameterLists.optionalParameters
        );

        final TypeSpec format = BuilderClassFormatter.format(builderFormatterDto);

        final String packageName = buildPackageName(element, annotation);
        final JavaFile javaFile = JavaFile
            .builder(packageName, format)
            .indent("    ")
            .build();

        try {
            javaFile.writeTo(processingEnv.getFiler());
        } catch (IOException e) {
            final String errorMsg = "Unable to write javaFile to filer";
            processingEnv
                .getMessager()
                .printMessage(Diagnostic.Kind.ERROR, errorMsg, element);
        }
    }

    private static final class ParameterLists {

        private final List<ParameterInformation> mandatoryParameters = new ArrayList<>();
        private final List<ParameterInformation> optionalParameters = new ArrayList<>();

        void addMandatoryParameter(final ParameterInformation parameterInformation) {
            mandatoryParameters.add(parameterInformation);
        }

        void addOptionalParameter(final ParameterInformation parameterInformation) {
            optionalParameters.add(parameterInformation);
        }
    }
}
