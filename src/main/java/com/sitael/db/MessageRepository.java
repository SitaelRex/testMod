package com.sitael.db;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import java.util.List;

public class MessageRepository {
    private static EntityManagerFactory entityManagerFactory;
    private static EntityManager entityManager;

    public static void initialize(DatabaseManager dm) {


        try {
           // entityManagerFactory = Persistence.createEntityManagerFactory("chat-persistence-unit");
            entityManager =  dm.getEntityManager(); //entityManagerFactory.createEntityManager();
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize database connection", e);
        }
    }

    public static void saveMessage(MessageEntity message) {
        try {
            entityManager.getTransaction().begin();
            entityManager.persist(message);
            entityManager.getTransaction().commit();
        } catch (Exception e) {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            throw new RuntimeException("Failed to save message", e);
        }
    }

    public static List<MessageEntity> getAllMessages() {
        return entityManager.createQuery("SELECT m FROM MessageEntity m", MessageEntity.class)
                .getResultList();
    }

    public static void shutdown() {
        if (entityManager != null) {
            entityManager.close();
        }
        if (entityManagerFactory != null) {
            entityManagerFactory.close();
        }
    }
}
