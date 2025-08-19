package com.cfo.reporting.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "tbl_cfo_screen_headers")
@Data
public class Header {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long headerId;
    private String header_text;
    private long header_order;
    private String screens_screen_id;



}