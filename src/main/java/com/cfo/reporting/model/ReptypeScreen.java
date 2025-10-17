package com.cfo.reporting.model;

import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.Table;



@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="tbl_cfo_screen_reptype")
public class ReptypeScreen {
    @Id
    @Column(name = "reptype_id")
    private String reptypeId;
    @Column(name = "reptype_desc")
    private String reptypeDesc;
    @Column(name = "reptype_order")
    private int reptypeOrder;
    @Column(name = "screen_id")
    private String screenId;

}
