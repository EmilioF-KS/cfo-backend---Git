package com.cfo.reporting.cache;

import com.cfo.reporting.model.Screen;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Pageable;

import java.util.Map;
import java.util.Objects;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class QueryParams {
    private Screen screen;
    private String glPeriod;
    private Pageable page;
    private int pageNumber;
    private int pageSize;
    private String reportType;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        QueryParams that = (QueryParams) o;
        return pageNumber == that.pageNumber && pageSize == that.pageSize && Objects.equals(screen, that.screen) && Objects.equals(glPeriod, that.glPeriod) && Objects.equals(page, that.page) && Objects.equals(reportType, that.reportType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(screen, glPeriod, page, pageNumber, pageSize, reportType);
    }
}
