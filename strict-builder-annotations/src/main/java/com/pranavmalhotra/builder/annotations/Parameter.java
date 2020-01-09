package com.pranavmalhotra.builder.annotations;

public @interface Parameter {

    boolean mandatory() default true;

    String name() default "";
}
