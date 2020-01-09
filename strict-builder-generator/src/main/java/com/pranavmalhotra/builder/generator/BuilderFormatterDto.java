package com.pranavmalhotra.builder.generator;

import java.util.List;

import javax.lang.model.element.TypeElement;
import lombok.Data;

@Data
class BuilderFormatterDto {

    private final String builderClassName;
    private final TypeElement returnType;
    private final List<ParameterInformation> mandatoryParameters;
    private final List<ParameterInformation> optionalParameters;
}
