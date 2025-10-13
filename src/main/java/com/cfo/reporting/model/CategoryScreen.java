package com.cfo.reporting.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="tbl_cfo_screen_category")
public class CategoryScreen {
    @Id
    @Column(name = "category_id")
    private String categoryId;
    @Column(name = "category_desc")
    private String categoryDesc;
    @Column(name = "category_order")
    private int categoryOrder;
}
