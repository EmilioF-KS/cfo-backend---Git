package com.cfo.reporting.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Table(name = "tbl_cfo_screens")
@Data
public class Screen {

    @Id
    private String screenId;
    private String screenName;
    private String description;
    private int screen_order;

}