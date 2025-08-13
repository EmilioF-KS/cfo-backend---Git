package com.cfo.reporting.model;

import jakarta.persistence.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

@Entity
@Table(name = "tbl_cfo_ddalst")
@Data
public class Ddalst {
    @EmbeddedId
    DdaLstId  Id;

    @Embeddable
    public static class DdaLstId implements Serializable {
        private String glPeriod;
        private String accountId;

        // Constructor
        public DdaLstId() {}

        public DdaLstId(String glPeriod, String accountId) {
            this.glPeriod = glPeriod;
            this.accountId = accountId;
        }

        // Equals y HashCode
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Ddalst.DdaLstId that = (Ddalst.DdaLstId) o;
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
    private String account_number;
    private String first_name;
    private String last_name;
    private double current_balance;
    private double e_c__equivalent;
    private String product_definiti;
    private double interest_earned;
    private double interest_rate;
    private String account_status;
    private double ytd_interest_pai;
    private double eccb_s1;
    private double eccb_s2_code;
    private Date date_opened;
    private String foreign_curr_cod;
    private double int_accrued_toda;
    private String address;
    private String city;
    private String country;
    private String zip_code;
    private double cif_number;
    private double classification_c;
    private String service_charge_c;
    private Date date_last_check;
    private double last_check_amoun;
    private Date date_last_deposi;
    private double last_deposit_amo;
    private double g_l_code;
    private double new_bs1_code;
    private double new_bs2_code;
    private double ec_equiv_int_ear;
    private double o_d_interest_ear;
    private double branch_number;
    private String charged_off;
    private double related_party_ty;
    private double ytd_interest_acc;
    private double int_accrued_mtd;
    private double ytd_o_d_int_paid;
    private double ytd_o_d_int_earn;
    private String second_name;
}
