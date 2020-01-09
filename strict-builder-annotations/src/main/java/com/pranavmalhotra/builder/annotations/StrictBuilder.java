package com.pranavmalhotra.builder.annotations;


public @interface StrictBuilder {

    String name() default "";

    String packageName() default "";
}
