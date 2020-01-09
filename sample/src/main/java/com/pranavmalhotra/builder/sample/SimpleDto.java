package com.pranavmalhotra.builder.sample;

import com.pranavmalhotra.builder.annotations.StrictBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

class SimpleDto {

    private final String argOne;
    private final int argTwo;

    @StrictBuilder
    SimpleDto(final String argOne, final int argTwo) {
        this.argOne = argOne;
        this.argTwo = argTwo;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
            .append("argOne", argOne)
            .append("argTwo", argTwo)
            .toString();
    }
}
