package com.cfo.reporting.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;

@Entity
@Table(name = "tbl_cfo_updatedtables")
@Data
@AllArgsConstructor
public class UpdateTables {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String table_name;
    private String table_source;
    private String table_alias;

    public UpdateTables() {
    }
}