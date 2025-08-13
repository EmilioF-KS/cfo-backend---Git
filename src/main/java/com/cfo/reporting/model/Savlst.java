package com.cfo.reporting.model;

import jakarta.persistence.*;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

@Entity
@Table(name = "tbl_cfo_savlst")
public class Savlst {

    @EmbeddedId
    SavlstId Id;

    @Embeddable
    public static class SavlstId implements Serializable {
        private String glPeriod;
        private String accountId;

        // Constructor
        public SavlstId() {}

        public SavlstId(String glPeriod, String accountId) {
            this.glPeriod = glPeriod;
            this.accountId = accountId;
        }

        // Equals y HashCode
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Savlst.SavlstId that = (Savlst.SavlstId) o;
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
    private double g_l_code;
    private double e_c__equivalent;
    private double current_balance;
    private String product_definiti;
    private double ytd_interest_pai;
    private double interest_rate;
    private String account_status;
    private double last_interest_pa;
    private Date date_int_last_pa;
    private double eccb_s1;
    private double eccb_s2_code;
    private String foreign_curr_cod;
    private Date date_opened;
    private String first_name;
    private String address;
    private String country;
    private String last_name;
    private String zip_code;
    private double int_accrued_toda;
    private double low_collected_ba;
    private double cycle_code;
    private String second_name;
    private String city;
    private double cif_number;
    private Date date_last_withdr;
    private double last_withdrawal;
    private Date date_last_deposi;
    private double last_deposit_amo;
    private double interest_earned;
    private double branch_number;
    private double new_bs1_code;
    private double new_bs2_code;
    private double ec_equiv_int_ear;
}
