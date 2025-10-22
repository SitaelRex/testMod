package com.sitael.db;

import com.sitael.config.DatabaseConfig;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.SharedCacheMode;
import jakarta.persistence.ValidationMode;
import jakarta.persistence.spi.ClassTransformer;
import jakarta.persistence.spi.PersistenceUnitInfo;
import jakarta.persistence.spi.PersistenceUnitTransactionType;
import org.hibernate.jpa.boot.internal.EntityManagerFactoryBuilderImpl;
import org.hibernate.jpa.boot.internal.PersistenceUnitInfoDescriptor;

import javax.sql.DataSource;
import java.net.URL;
import java.util.*;

public class DatabaseManager {
    private final DatabaseConfig config;
    private EntityManagerFactory entityManagerFactory;

    public DatabaseManager(DatabaseConfig config) {
        this.config = config;
    }

    public void initialize() {
        try {
            Map<String, Object> properties = new HashMap<>();

            properties.put("javax.persistence.jdbc.driver", "org.postgresql.Driver");
            properties.put("javax.persistence.jdbc.url", config.getDatabaseUrl());
            properties.put("javax.persistence.jdbc.user", config.getDatabaseUser());
            properties.put("javax.persistence.jdbc.password", config.getDatabasePassword());

            properties.put("hibernate.dialect", config.getHibernateDialect());
            properties.put("hibernate.hbm2ddl.auto", config.getHbm2ddlAuto());
            properties.put("hibernate.show_sql", String.valueOf(config.showSql()));
            properties.put("hibernate.format_sql", String.valueOf(config.formatSql()));

            properties.put("hibernate.connection.pool_size", "5");
            properties.put("hibernate.jdbc.batch_size", "10");

            PersistenceUnitInfo persistenceUnitInfo = createPersistenceUnitInfo();
            EntityManagerFactoryBuilderImpl builder = new EntityManagerFactoryBuilderImpl(
                    new PersistenceUnitInfoDescriptor(persistenceUnitInfo), properties
            );

            entityManagerFactory = builder.build();
            //testConnection();

        } catch (Exception e) {
            throw new RuntimeException("Не удалось инициализировать БД", e);
        }
    }

    private PersistenceUnitInfo createPersistenceUnitInfo() {
        return new PersistenceUnitInfo() {
            @Override
            public String getPersistenceUnitName() {
                return "minecraft-pu";
            }

            @Override
            public String getPersistenceProviderClassName() {
                return "org.hibernate.jpa.HibernatePersistenceProvider";
            }

            @Override
            public PersistenceUnitTransactionType getTransactionType() {
                return null;
            }

            @Override
            public javax.sql.DataSource getJtaDataSource() {
                return null;
            }

            @Override
            public List<String> getManagedClassNames() {
                List<String> classes = new ArrayList<>();
                classes.add("com.sitael.db.MessageEntity");
                return classes;
            }

            @Override
            public boolean excludeUnlistedClasses() {
                return true;
            }

            @Override
            public SharedCacheMode getSharedCacheMode() {
                return null;
            }

            @Override
            public ValidationMode getValidationMode() {
                return null;
            }

            @Override
            public DataSource getNonJtaDataSource() {
                return null;
            }

            @Override
            public List<String> getMappingFileNames() {
                return new ArrayList<>();
            }

            @Override
            public List<URL> getJarFileUrls() {
                return new ArrayList<>();
            }

            @Override
            public URL getPersistenceUnitRootUrl() {
                return null;
            }

            @Override
            public ClassLoader getClassLoader() {
                return Thread.currentThread().getContextClassLoader();
            }

            @Override
            public void addTransformer(ClassTransformer transformer) {}

            @Override
            public ClassLoader getNewTempClassLoader() {
                return null;
            }

            @Override
            public Properties getProperties() {
                return new Properties();
            }

            @Override
            public String getPersistenceXMLSchemaVersion() {
                return "";
            }
        };
    }

    private void testConnection() {
        EntityManager em = null;
        try {
            em = getEntityManager();
            Object result = em.createNativeQuery("SELECT 1").getSingleResult();
        } catch (Exception e) {
            throw e;
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }

    public EntityManager getEntityManager() {
        if (entityManagerFactory == null) {
            throw new IllegalStateException("DatabaseManager не инициализирован");
        }
        return entityManagerFactory.createEntityManager();
    }

    public void close() {
        if (entityManagerFactory != null && entityManagerFactory.isOpen()) {
            entityManagerFactory.close();
        }
    }
}
