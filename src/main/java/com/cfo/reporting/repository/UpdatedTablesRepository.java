package com.cfo.reporting.repository;

import com.cfo.reporting.model.UpdateTables;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UpdatedTablesRepository extends JpaRepository<UpdateTables,Long> {

    @Query(value="Select * from tbl_cfo_updatedtables where table_alias = :table_alias",nativeQuery=true)
    String findByTableAlias(@Param("table_alias") String table_alias);
}
