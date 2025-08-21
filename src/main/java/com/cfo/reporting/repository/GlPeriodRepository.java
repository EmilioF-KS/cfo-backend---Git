package com.cfo.reporting.repository;

import com.cfo.reporting.model.GlPeriod;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GlPeriodRepository extends JpaRepository<GlPeriod,Long> {
    }
