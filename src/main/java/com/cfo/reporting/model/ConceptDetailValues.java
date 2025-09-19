package com.cfo.reporting.model;



import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.EmbeddedColumnNaming;


@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name="tbl_cfo_column_details_values")
//, uniqueConstraints = @UniqueConstraint(
//        columnNames={"concept_detail_id","concept_id","gl_period"}))
public class ConceptDetailValues {

//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    @Column(name = "column_value_id", insertable = true,updatable = true)
    //private Long columnValueId;
    @EmbeddedId
    ConceptDetailValuesKey Id;
    @Column(name="column_value")
    private double columnValue;

    @Override
    public String toString() {
        return "ConceptDetailValues{" +
                "Id=" + Id +
                ", columnValue=" + columnValue +
                '}';
    }
}