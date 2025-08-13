package com.cfo.reporting.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "tbl_cfo_tablestoinsert")
@Data
public class UpdateTables {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String table_name;
    private String source_name;
    private int status;

}