package com.cfo.reporting.repository;

import com.cfo.reporting.model.Header;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HeadersRepository extends JpaRepository<Header,Long> {
}
