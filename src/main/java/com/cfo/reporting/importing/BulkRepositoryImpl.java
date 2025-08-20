package com.cfo.reporting.importing;

import com.cfo.reporting.dto.EntidadValueDTO;
import com.cfo.reporting.exception.DataProcessingException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.persistence.Tuple;
import jakarta.transaction.Transactional;

import org.hibernate.Session;
import org.hibernate.query.NativeQuery;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
public class BulkRepositoryImpl implements BulkRepository {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional
    public void bulkInsert(String sqlStatment, List<Object[]> data) {
        Session session = entityManager.unwrap(Session.class);
        Query query = entityManager.createNativeQuery(sqlStatment);
        session.doWork(connection -> {
            try(PreparedStatement ps =
                        connection.prepareStatement(sqlStatment))
            {
                for (Object[] row: data) {
                    for(int i = 0; i < row.length; i++){
                        ps.setObject(i+1,row[i]);
                    }
                    ps.addBatch();
                }
                ps.executeBatch();
                ps.clearBatch();
            }
        });
    }

    public long recordsProcessedByTable(String query) {
        Session session = entityManager.unwrap(Session.class);
        long count = ((Number)session.createNativeQuery(query).getSingleResult()).longValue();
        return count;
    }

//    public List<Tuple>  recordsFromTable(String queryTable,String glPeriod) {
//        Session session = entityManager.unwrap(Session.class);
//        entityManager.getMetamodel().entity()
//          Query jpaQuery = entityManager.createNativeQuery(queryTable, Tuple.class);
//          jpaQuery.setParameter(1,glPeriod);
//          return jpaQuery.getResultList();
//    }

    public Map<String,Object> valuesForQuery(String query) {
        Session session = entityManager.unwrap(Session.class);

        List<EntidadValueDTO> results = session.createNativeQuery(query,EntidadValueDTO.class)
                .getResultList();
        return results.stream()
                .collect(Collectors.toMap(
                        EntidadValueDTO::getCode,
                        EntidadValueDTO::getValue,
                        (existing,replacement) -> replacement,
                        HashMap::new));

    }



}


