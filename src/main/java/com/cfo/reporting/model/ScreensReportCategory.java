package com.cfo.reporting.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;



@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="tbl_cfo_screen_reports")
public class ScreensReportCategory {
    @EmbeddedId
    private ScreensReportCategoryKey Id;

    @Column(name = "status")
    private int status;
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    @MapsId("reptypeId")
//    @JoinColumn(name = "reptype_id")
//    private ReptypeScreen reptypeScreen;
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    @MapsId("screenId")
//    @JoinColumn(name = "screenId")
//    private Screen screen;
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    @MapsId("categoryId")
//    @JoinColumn(name = "categoryId")
//    private Screen CategoryScreen;
}
