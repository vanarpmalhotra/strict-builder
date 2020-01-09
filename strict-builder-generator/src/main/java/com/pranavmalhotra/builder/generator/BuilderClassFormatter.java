package com.pranavmalhotra.builder.generator;

import java.util.List;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import javax.lang.model.element.Modifier;
import javax.lang.model.type.TypeMirror;

class BuilderClassFormatter {

    static TypeSpec format(final BuilderFormatterDto builderFormatterDto) {

        final List<ParameterInformation> mandatoryParameters = builderFormatterDto.getMandatoryParameters();

        final TypeSpec.Builder builder = TypeSpec
            .classBuilder(builderFormatterDto.getBuilderClassName())
            .addModifiers(Modifier.PUBLIC)
            .addMethod(
                MethodSpec.methodBuilder("newInstance")
                    .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                    .returns(ClassName.bestGuess(mandatoryParameters.get(0).getInterfaceName()))
                    .addStatement("return new BuilderInternal()")
                    .build()
            );

        final TypeSpec.Builder builderInternal = TypeSpec
            .classBuilder("BuilderInternal")
            .addModifiers(Modifier.PRIVATE, Modifier.STATIC);


        final int size = mandatoryParameters.size();
        for (int i = 0; i < size; i++) {
            final ParameterInformation parameterInformation = mandatoryParameters.get(i);
            final String currentInterfaceName = parameterInformation.getInterfaceName();
            final String nextInterfaceName =
                i + 1 < size ?
                    mandatoryParameters.get(i + 1).getInterfaceName() :
                    "OptionalParameters";
            final String parameterName = parameterInformation.getParameterName();
            builder.addType(
                TypeSpec
                    .interfaceBuilder(currentInterfaceName)
                    .addModifiers(Modifier.PUBLIC)
                    .addMethod(
                        MethodSpec
                            .methodBuilder(parameterInformation.getSetterName())
                            .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                            .returns(ClassName.bestGuess(nextInterfaceName))
                            .addParameter(ClassName.get(parameterInformation.getTypeMirror()), parameterName)
                            .build()
                    )
                    .build()
            );
            builderInternal
                .addSuperinterface(ClassName.bestGuess(currentInterfaceName))
                .addField(ClassName.get(parameterInformation.getTypeMirror()), parameterName, Modifier.PRIVATE)
                .addMethod(
                    MethodSpec
                        .methodBuilder(parameterInformation.getSetterName())
                        .addModifiers(Modifier.PUBLIC)
                        .returns(ClassName.bestGuess(nextInterfaceName))
                        .addParameter(ClassName.get(parameterInformation.getTypeMirror()), parameterName)
                        .addStatement("this." + parameterName + " = " + parameterName)
                        .addStatement("return this")
                        .build()
                );
        }

        final TypeSpec.Builder optionalParameters =
            TypeSpec
                .interfaceBuilder("OptionalParameters")
                .addModifiers(Modifier.PUBLIC);

        builderInternal.addSuperinterface(ClassName.bestGuess("OptionalParameters"));

        for (final ParameterInformation parameterInformation : builderFormatterDto.getOptionalParameters()) {
            final String setterName = parameterInformation.getSetterName();
            final Class<? extends TypeMirror> parameterClass = parameterInformation.getTypeMirror().getClass();
            final String parameterName = parameterInformation.getParameterName();
            optionalParameters.addMethod(
                MethodSpec
                    .methodBuilder(setterName)
                    .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                    .returns(ClassName.bestGuess("OptionalParameters"))
                    .addParameter(parameterClass, parameterName)
                    .build()
            );
            builderInternal
                .addField(ClassName.get(parameterInformation.getTypeMirror()), parameterName, Modifier.PRIVATE)
                .addMethod(
                    MethodSpec
                        .methodBuilder(parameterInformation.getSetterName())
                        .addModifiers(Modifier.PUBLIC)
                        .returns(ClassName.bestGuess("OptionalParameters"))
                        .addParameter(ClassName.get(parameterInformation.getTypeMirror()), parameterName)
                        .addStatement("this." + parameterName + " = " + parameterName)
                        .addStatement("return this")
                        .build()
                );
        }

        optionalParameters
            .addMethod(
                MethodSpec
                    .methodBuilder("build")
                    .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                    .returns(ClassName.get(builderFormatterDto.getReturnType()))
                    .build()
            );

        builderInternal.addMethod(
            MethodSpec
                .methodBuilder("build")
                .addModifiers(Modifier.PUBLIC)
                .returns(ClassName.get(builderFormatterDto.getReturnType()))
                .addStatement("return new " + builderFormatterDto.getReturnType() + "(" + "argOne" + "," + "argTwo" + ")")
                .build()
        );

        builder.addType(optionalParameters.build());

        builder.addType(builderInternal.build());

        return builder.build();
    }
}
