package ru.dreambu1lder;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.stat.Statistics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {

    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        LoggerConfig.configureLogger();

        // Удаление всех сущностей из базы данных перед началом работы
        deleteAllEntities();

        // Сброс автоинкремента для таблицы перед созданием сущностей
        resetAutoIncrement();

        // Сохранение нескольких сущностей
        try (Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            try {
                for (int i = 1; i <= 5; i++) {
                    MyEntity newEntity = new MyEntity();
                    session.save(newEntity);
                    logger.info("Сущность с ID {} успешно сохранена в базу данных", newEntity.getId());
                }
                transaction.commit();
            } catch (Exception e) {
                if (transaction != null) {
                    transaction.rollback();
                }
                logger.error("Ошибка при сохранении сущностей", e);
            }
        }
        // Загрузка сущностей из базы для добавления их в кэш
        try (Session session2 = HibernateSessionFactoryUtil.getSessionFactory().openSession()) {
            Transaction transaction2 = session2.beginTransaction();
            try {
                for (int i = 1; i <= 5; i++) {
                    MyEntity entityFromDb = session2.get(MyEntity.class, (long) i);
                    if (entityFromDb != null) {
                        logger.info("Сущность с ID {} загружена из базы данных", entityFromDb.getId());
                    }
                }
                transaction2.commit();
            } catch (Exception e) {
                if (transaction2 != null) {
                    transaction2.rollback();
                }
                logger.error("Ошибка при загрузке сущностей из базы данных", e);
            }
        }
        // Загрузка сущностей из кэша второго уровня
        try (Session session3 = HibernateSessionFactoryUtil.getSessionFactory().openSession()) {
            Transaction transaction3 = session3.beginTransaction();
            try {
                for (int i = 1; i <= 5; i++) {
                    MyEntity cachedEntity = session3.get(MyEntity.class, (long) i);
                    if (cachedEntity != null) {
                        logger.info("Сущность с ID {} загружена из кэша второго уровня", cachedEntity.getId());
                    }
                }
                transaction3.commit();
            } catch (Exception e) {
                if (transaction3 != null) {
                    transaction3.rollback();
                }
                logger.error("Ошибка при загрузке сущностей из кэша второго уровня", e);
            }
        }

        // Логирование статистики второго уровня кэша
        Statistics stats = HibernateSessionFactoryUtil.getStatistics();
        if (stats != null) {
            logCacheStatistics(stats);
        }

        // Завершение работы программы
        HibernateSessionFactoryUtil.shutdown();
    }

    public static void logCacheStatistics(Statistics stats) {
        logger.info("Second level cache hit count: {}", stats.getSecondLevelCacheHitCount());
        logger.info("Second level cache miss count: {}", stats.getSecondLevelCacheMissCount());
        logger.info("Second level cache put count: {}", stats.getSecondLevelCachePutCount());
        logger.info("Entities in second level cache: {}", stats.getSecondLevelCacheRegionNames().length);
    }

    public static void resetAutoIncrement() {
        try (Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            try {
                // Выполняем SQL-запрос для сброса последовательности
                session.createNativeQuery("ALTER SEQUENCE myentity_id_seq RESTART WITH 1").executeUpdate();
                transaction.commit();
                logger.info("Автоинкремент для таблицы 'MyEntity' успешно сброшен.");
            } catch (Exception e) {
                if (transaction != null) {
                    transaction.rollback();
                }
                logger.error("Ошибка при сбросе автоинкремента", e);
            }
        }
    }

    // Метод для удаления всех сущностей
    public static void deleteAllEntities() {
        try (Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            try {
                // Выполняем HQL-запрос для удаления всех сущностей MyEntity
                session.createQuery("DELETE FROM MyEntity").executeUpdate();
                transaction.commit();
                logger.info("Все сущности MyEntity были успешно удалены из базы данных.");
            } catch (Exception e) {
                if (transaction != null) {
                    transaction.rollback();
                }
                logger.error("Ошибка при удалении сущностей MyEntity", e);
            }
        }
    }
}