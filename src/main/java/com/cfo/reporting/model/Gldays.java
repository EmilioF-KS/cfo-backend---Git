package com.cfo.reporting.model;

import jakarta.persistence.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "tbl_cfo_gldays")
@Data
public class Gldays {

    @EmbeddedId
    GLDaysId Id;

    @Embeddable
    public static class GLDaysId implements Serializable {
        private String glPeriod;
        private String forBranch;

        // Constructor
        public GLDaysId() {}

        public GLDaysId(String glPeriod, String forBranch) {
            this.glPeriod = glPeriod;
            this.forBranch = forBranch;
        }

        // Equals y HashCode
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            GLDaysId that = (GLDaysId) o;
            return Objects.equals(glPeriod, that.glPeriod) &&
                    Objects.equals(forBranch, that.forBranch);
        }

        @Override
        public int hashCode() {
            return Objects.hash(glPeriod, forBranch);
        }
    }

    private String head_offic_OF;
    private double tot_current_balance;
    private double month_change;
    private double mtd_average;
    private double ytd_average;
    private double lstyr_thru;
    private double cur_yr_budget;
    private double prv_mth_end;
}

