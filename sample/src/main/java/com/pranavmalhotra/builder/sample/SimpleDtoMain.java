package com.pranavmalhotra.builder.sample;

public class SimpleDtoMain {

    public static void main(String[] args) {
        System.out.println(
            SimpleDtoBuilder
                .newInstance()
                .argOne("argOne")
                .argTwo(2)
                .build()
        );
    }
}
