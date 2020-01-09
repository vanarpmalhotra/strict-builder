package com.pranavmalhotra.builder.generator;

import com.pranavmalhotra.builder.annotations.Parameter;
import com.pranavmalhotra.builder.annotations.StrictBuilder;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;

class NamingUtils {

    static String buildPackageName(final ExecutableElement element, final StrictBuilder annotation) {
        if (annotation.packageName().isEmpty()) {
            return getEnclosingElementForType(PackageElement.class, element).getQualifiedName().toString();
        } else {
            return annotation.packageName();
        }
    }

    static String buildBuilderClassName(final TypeElement element, final StrictBuilder annotation) {
        if (annotation.name().isEmpty()) {
            return element.getSimpleName().toString() + "Builder";
        } else {
            return annotation.name();
        }
    }

    static String buildSetterName(final String name, final Parameter annotation) {
        if (annotation == null || annotation.name().isEmpty()) {
            return name;
        } else {
            return annotation.name();
        }
    }

    static <T> T getEnclosingElementForType(final Class<T> type, Element element) {
        while (!type.isAssignableFrom(element.getClass())) {
            element = element.getEnclosingElement();
        }
        return type.cast(element);
    }
}
