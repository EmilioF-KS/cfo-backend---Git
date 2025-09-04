package com.cfo.reporting.model;



import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.EmbeddedColumnNaming;


@Entity
@Table(name = "tbl_cfo_column_details_values")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConceptDetailValues {
    //    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "column_value_id", insertable = true,updatable = false)
    private Long columnValueId;
    @EmbeddedId
    private ConceptDetailValueKey id;
    @Column(name="column_name")
    private String columnName;
    @Column(name="column_value")
    private double columnValue;

    @Transient
    private boolean persisted=false;

    @PostPersist
    private void onPersist() {
        this.persisted = true;
        System.out.println("Entity persisted with columnValueId :"+columnValueId);

    }


}