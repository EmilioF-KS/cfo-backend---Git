package com.cfo.reporting.model;

import jakarta.persistence.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

@Entity
@Table(name = "tbl_cfo_finlns")
@Data
public class Finlns {

    @EmbeddedId
    FinlnsId id;

    @Embeddable
    public static class FinlnsId implements Serializable {
        private String glPeriod;
        private String accountId;

        // Constructor
        public FinlnsId() {}

        public FinlnsId(String glPeriod, String accountId) {
            this.glPeriod = glPeriod;
            this.accountId = accountId;
        }

        // Equals y HashCode
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Finlns.FinlnsId that = (Finlns.FinlnsId) o;
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
    private double current_balance;
    private double original_loan_am;
    private Date date_opened;
    private double interest_rate;
    private double regular_payment;
    private double pmt_prin_frequen;
    private String collateral;
    private String additional_colla;
    private String security_codes;
    private String collateral_type;
    private String security_value;
    private double coll_value_manua;
    private double cash_collateral;
    private double eccb_s2_code;
    private Date maturity_date;
    private double days_late;
    private String non_accr_chg_off;
    private double part_chg_off_bal;
    private double g_l_code;
    private String foreign_curr_cod;
    private double accrued_interest;
    private double e_c__equivalent;
    private double classification_c;
    private double branch_number;
    private double new_bs2_code;
    private double new_bs1_code;
    private String officer_initials;
    private double ec_equiv_acc_int;
    private String product_definiti;
    private double g_l_ienc_amount;
    private double cif_number;
    private double amount_past_due;
    private double fasb91_org_fee_e;
    private double unused_credit_co;
    private Date next_pmt_prin_da;
    private double classification_c2;
    private Date date_last_paymen;
    private double term_in_months;
    private Date date_last_ext;
    private Date date_chgoff;
    private double fasb91_orig_fee;
    private double chgoff_bal;
    private double c57__c47;
    private Date date_int_paid_to;
    private double ytd_interest_pai;
}
