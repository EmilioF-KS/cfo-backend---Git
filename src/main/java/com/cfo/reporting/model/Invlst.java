package com.cfo.reporting.model;

import jakarta.persistence.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

@Entity
@Table(name = "tbl_cfo_invlst")
@Data
public class Invlst {
    @EmbeddedId
    InvlstId Id;

    @Embeddable
    public static class InvlstId implements Serializable {
        private String glPeriod;
        private String accountId;

        // Constructor
        public InvlstId() {}

        public InvlstId(String glPeriod, String accountId) {
            this.glPeriod = glPeriod;
            this.accountId = accountId;
        }

        // Equals y HashCode
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Invlst.InvlstId that = (Invlst.InvlstId) o;
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
    private double account_number;
    private String first_name;
    private String last_name;
    private String additional_names;
    private String country;
    private String cusip;
    private double branch_number;
    private double g_l_code;
    private double orig_par;
    private double foreign_amount;
    private double curr_par;
    private Date issue_date;
    private Date purch_date;
    private Date settle_date;
    private Date mature_date;
    private double intr_coup_rate;
    private double purch_yld;
    private String intr_freq;
    private double ienc;
    private double int_accrued_toda;
    private double orig__issue;
    private double tot_accret_disc;
    private double ytd_interest_acc;
    private double tot_intr_recd;
    private Date date_opened;
    private String amort_accret_cd;
    private String use_purch_yield;
    private double tot_prem_amort;
    private double tot_accret_disc_2;
    private double call_price;
    private double cif_number;
    private double orig_premium;
    private double orig_disc;
    private double type_of_investme;
    private double bought_from;
    private Date dummy_col;
    private double dte_nxt_intr;
    private double new_bs1_code;
    private String new_bs2_code;
    private String dte_lst_intr;
}
