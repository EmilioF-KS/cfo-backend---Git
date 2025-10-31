package com.cfo.reporting.cache;

import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.stream.Collectors;

@Component("multParamKeyGenerator")
public class MultipleParamKeyGenerator  implements KeyGenerator {
    @Override
    public Object generate(Object target, Method method, Object ... params) {
        return Arrays.stream(params)
                .map(Object::toString)
                .collect(Collectors.joining("_"));
    }
}
