package com.cfo.reporting.utils;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.stream.Collectors;

public class PageStreamUtil {
    public static <T> Page<T> createPageWithStream(List<T> listItems, int page, int size) {
        Pageable pageable = PageRequest.of(page,size);
        List<T> content = listItems.stream()
                .skip(pageable.getOffset())
                .limit(pageable.getPageSize())
                .collect(Collectors.toList());
        return new PageImpl<>(content,pageable,listItems.size());
    }
}
