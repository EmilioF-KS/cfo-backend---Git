package com.cfo.reporting.model;

import jakarta.persistence.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

@Entity
@Table(name = "tbl_cfo_cdslst")
@Data
public class Cdslst {

    @EmbeddedId
    private CdsLstId id;

    @Embeddable
    public static class CdsLstId implements Serializable {
        private String glPeriod;
        private String accountId;

        // Constructor
        public CdsLstId() {}

        public CdsLstId(String glPeriod, String accountId) {
            this.glPeriod = glPeriod;
            this.accountId = accountId;
        }

        // Equals y HashCode
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Cdslst.CdsLstId that = (Cdslst.CdsLstId) o;
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
    private double employee_code;
    private double e_c__equivalent;
    private String ccount_status;
    private Date maturity_date;
    private double interest_rate;
    private double int_frequency_co;
    private double accrued_interest;
    private Date date_last_paid;
    private double interest_paid_yt;
    private double eccb_s1;
    private Date date_opened;
    private double term;
    private double variable_rate_co;
    private double foreign_curr_cod;
    private double eccb_s2_code;
    private double int_accrued_toda;
    private double delete_code;
    private double renew_code;
    private String product_definiti;
    private String officer_initials;
    private double cif_number;
    private String first_name;
    private String last_name;
    private String second_name;
    private String address;
    private String city;
    private String country;
    private String zip_code;
    private String month_day_flag;
    private double renewal_balance;
    private double branch_number;
    private double c2_c7;
    private double new_bs1_code;
    private double new_bs2_code;
    private String salutation;
    private double apply_interest_c;
    private double corresponding_ac;
    private double prev_interest_ra;
    private double g_l_code;
}
