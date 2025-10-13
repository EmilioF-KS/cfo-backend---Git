package com.cfo.reporting.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EmbeddedId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ScreensReportCategoryKey implements Serializable {
    @Column(name = "screen_id")
    private String screenId;
    @Column(name = "reptype_id")
    private String repTypeId;
    @Column(name = "category_id")
    private String categoryId;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ScreensReportCategoryKey that = (ScreensReportCategoryKey) o;
        return Objects.equals(screenId, that.screenId) && Objects.equals(repTypeId, that.repTypeId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(screenId, repTypeId);
    }
}
