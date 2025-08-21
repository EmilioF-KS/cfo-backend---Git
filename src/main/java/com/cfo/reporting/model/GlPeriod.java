package com.cfo.reporting.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "tbl_cfo_glperiod")
@Data
public class GlPeriod {

    @Id
    private String gl_period;
    private String period_description;
    private int period_status;

}