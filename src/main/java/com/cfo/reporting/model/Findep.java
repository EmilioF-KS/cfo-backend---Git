package com.cfo.reporting.model;

import jakarta.persistence.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

@Entity
@Table(name = "tbl_cfo_findep")
@Data
public class Findep {
  @EmbeddedId
  FindepId  Id;


  @Embeddable
  public static class FindepId implements Serializable {
        private String glPeriod;
        private String accountId;

        // Constructor
        public FindepId() {}

        public FindepId(String glPeriod, String accountId) {
            this.glPeriod = glPeriod;
            this.accountId = accountId;
        }

        // Equals y HashCode
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Findep.FindepId that = (Findep.FindepId) o;
            return Objects.equals(glPeriod, that.glPeriod) &&
                    Objects.equals(accountId, that.accountId);
        }

        @Override
        public int hashCode() {
            return Objects.hash(glPeriod, accountId);
        }
    }
    private String type;
    private String account_name;
    private double eccb_s1;
    private double e_c__equivalent;
    private double foreign_curr_cod;
    private double new_bs1_code;
    private double eccb_s2_code;
    private double new_bs2_code;
    private double g_l_code;
    private double accrued_interest;
    private double equiv_int_earned;
    private String country;
    private double interest_rate;
    private double contrib_corres_a;
    private double cif_number;
    private Date date_opened_2;
    private Date date_opened;
    private String product_definiti;
    private double prior_year_int;
    private Date maturity_date;
    private double ytd_interest_pai;
    private double branch_number;
}
