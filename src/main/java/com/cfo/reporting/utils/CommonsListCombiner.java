package com.cfo.reporting.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

public class CommonsListCombiner {
    public static <T, U, R> List<R> combineLists(
            List<T> list1,
            List<U> list2,
            BiFunction<T, U, R> combiner) {

        List<R> result = new ArrayList<>();
        int minSize = Math.min(list1.size(), list1.size());
        for (int i= 0;  i < minSize; i++) {
            result.add(combiner.apply(list1.get(i),list2.get(i)));
        }
        return result;
    }
}
