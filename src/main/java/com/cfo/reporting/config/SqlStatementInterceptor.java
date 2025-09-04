package com.cfo.reporting.config;

import org.hibernate.EmptyInterceptor;
import org.hibernate.type.Type;
import org.springframework.stereotype.Component;

import java.io.Serializable;

@Component
public class SqlStatementInterceptor extends EmptyInterceptor {

    @Override
    public String onPrepareStatement(String sql) {
        System.out.println("SQL EJECUTADO: " + sql);
        return super.onPrepareStatement(sql);
    }

    @Override
    public boolean onSave(Object entity, Serializable id, Object[] state,
                          String[] propertyNames, Type[] types) {
        System.out.println("GUARDANDO ENTIDAD: " + entity.getClass().getSimpleName());
        return super.onSave(entity, id, state, propertyNames, types);
    }
}