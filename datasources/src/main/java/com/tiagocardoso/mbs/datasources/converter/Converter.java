package com.tiagocardoso.mbs.datasources.converter;

public interface Converter<A, B> {

    B convertFrom(A a);

    A convertTo(B b);
}
