package com.cfo.reporting.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="tbl_cfo_screen_reptype")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReptypeScreen {
    @Id
    @Column(name = "reptype_id")
    private String reptypeId;
    @Column(name = "reptype_desc")
    private String reptypeDesc;
    @Column(name = "reptype_order")
    private int reptypeOrder;
}
