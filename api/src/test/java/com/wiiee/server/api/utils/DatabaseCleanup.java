package com.wiiee.server.api.utils;

import com.google.common.base.CaseFormat;
import com.google.common.collect.ImmutableMap;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.Entity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class DatabaseCleanup implements InitializingBean {

    private static final String ID_SUFFIX = "_id";

    @PersistenceContext
    private EntityManager entityManager;

    private final Map<String, String> specificTableNames = ImmutableMap.<String, String>builder()
            .put("user", "users")
            .build();

    private List<String> tableNames;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void execute() {
        entityManager.flush();

        // Get list of existing tables from database
        @SuppressWarnings("unchecked")
        List<String> existingTables = entityManager.createNativeQuery(
                "SELECT tablename FROM pg_catalog.pg_tables WHERE schemaname = 'public'"
        ).getResultList();

        for (String tableName : tableNames) {
            String name = specificTableNames.getOrDefault(tableName, tableName);

            // Only truncate if table exists
            if (existingTables.contains(name)) {
                executeTruncateTable(name);
            }
        }
    }

    private void executeTruncateTable(String tableName) {
        entityManager.createNativeQuery("TRUNCATE TABLE " + tableName + " CASCADE").executeUpdate();
    }

    private void executeResetAutoIncrement(String tableName, String column) {
//        entityManager.createNativeQuery("ALTER TABLE " + tableName + " ALTER COLUMN " + column + " RESTART WITH 1")
//                .executeUpdate();
        entityManager.createNativeQuery("RESTART IDENTITY").executeUpdate();
    }

    @Override
    public void afterPropertiesSet() {
        tableNames = entityManager.getMetamodel()
                .getEntities().stream()
                .filter(e -> e.getJavaType().getAnnotation(Entity.class) != null)
                .map(e -> CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, e.getName()))
                .collect(Collectors.toList());
    }

}
