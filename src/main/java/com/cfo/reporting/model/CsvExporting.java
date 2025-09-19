package com.cfo.reporting.model;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CsvExporting {
    @EmbeddedId
    CsvExportingKey Id;
    @Column(name="entity_id")
    private String entityId;
    @Column(name="report_amount")
    private double reportAmount;
    @Column(name="status_cd")
    private String statusCd;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        CsvExporting that = (CsvExporting) o;
        return Double.compare(reportAmount, that.reportAmount) == 0 && Objects.equals(Id, that.Id) && Objects.equals(entityId, that.entityId) && Objects.equals(statusCd, that.statusCd);
    }

    @Override
    public int hashCode() {
        return Objects.hash(Id, entityId, reportAmount, statusCd);
    }

    @Override
    public String toString() {
        return "CsvExporting{" +
                "Id=" + Id +
                ", entityId='" + entityId + '\'' +
                ", reportAmount=" + reportAmount +
                ", statusCd='" + statusCd + '\'' +
                '}';
    }
}
