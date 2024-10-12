package ru.dreambu1lder;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.config.CacheConfiguration;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.AvailableSettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

public class HibernateUtil {

    private static final Logger logger = LoggerFactory.getLogger(HibernateUtil.class);
    private static CacheManager cacheManager;

    static {
        try {
            // Программная настройка CacheManager
            cacheManager = CacheManager.create();  // Создаем новый CacheManager без конфигурационного файла

            // Настройка кэша для сущности MyEntity
            CacheConfiguration myEntityCacheConfig = new CacheConfiguration().name("ru.dreambu1lder.MyEntity")
                                                                             .maxEntriesLocalHeap(1000)
                                                                             .memoryStoreEvictionPolicy("LRU")
                                                                             .timeToIdleSeconds(300)
                                                                             .timeToLiveSeconds(600)
                                                                             .statistics(true);

            // Создание кэша и добавление его в CacheManager
            Cache myEntityCache = new Cache(myEntityCacheConfig);
            cacheManager.addCache(myEntityCache);

            logger.info("CacheManager успешно настроен программно с кэшом для MyEntity");
        } catch (Exception e) {
            logger.error("Ошибка при настройке CacheManager", e);
        }
    }

    public static Properties getHibernateProperties() {
        Properties hibernateProperties = new Properties();

        // Настройки подключения к базе данных
        hibernateProperties.put(AvailableSettings.DIALECT, "org.hibernate.dialect.PostgreSQLDialect");
        hibernateProperties.put(AvailableSettings.DRIVER, "org.postgresql.Driver");
        hibernateProperties.put(AvailableSettings.URL, "jdbc:postgresql://localhost:5432/test");
        hibernateProperties.put(AvailableSettings.USER, "test");
        hibernateProperties.put(AvailableSettings.PASS, "test");
        hibernateProperties.put(AvailableSettings.HBM2DDL_AUTO, "update");

        // Включение кэша второго уровня
        hibernateProperties.put(AvailableSettings.USE_SECOND_LEVEL_CACHE, true);
        hibernateProperties.put(AvailableSettings.CACHE_REGION_FACTORY, "org.hibernate.cache.ehcache.EhCacheRegionFactory");

        // Включение генерации статистики Hibernate
        hibernateProperties.put(AvailableSettings.GENERATE_STATISTICS, true);

        return hibernateProperties;
    }

    public static CacheManager getCacheManager() {
        return cacheManager;
    }

    public static void shutdown() {
        if (cacheManager != null) {
            cacheManager.shutdown();
            logger.info("CacheManager успешно завершил работу.");
        }
    }
}
