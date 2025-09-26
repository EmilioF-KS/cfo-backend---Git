package com.cfo.reporting.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Table(name = "tbl_cfo_screens")
@Data
public class Screen {
    @Id
    @Column(name="screen_id")
    private String screenId;
    @Column(name="screen_name")
    private String screenName;
    @Column(name="description")
    private String description;
    @Column(name="screen_order")
    private int screen_order;
    @Column(name="screen_save")
    private boolean screen_save;
    @Column(name="screen_formid")
    private String screen_formid;

}