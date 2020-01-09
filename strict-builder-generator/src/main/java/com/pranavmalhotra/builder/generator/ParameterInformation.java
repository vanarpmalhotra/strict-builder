package com.pranavmalhotra.builder.generator;

import javax.lang.model.type.TypeMirror;
import lombok.Getter;

import static com.google.common.base.CaseFormat.LOWER_CAMEL;
import static com.google.common.base.CaseFormat.UPPER_CAMEL;

@Getter
class ParameterInformation {

    private final String parameterName;
    private final TypeMirror typeMirror;
    private final String setterName;
    private final String interfaceName;

    ParameterInformation(final String parameterName, final TypeMirror typeMirror, final String setterName) {
        this.parameterName = parameterName;
        this.typeMirror = typeMirror;
        this.setterName = setterName;
        this.interfaceName = "MandatoryParameter" + LOWER_CAMEL.to(UPPER_CAMEL, setterName);
    }
}
