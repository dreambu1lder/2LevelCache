package ru.dreambu1lder;

import org.hibernate.HibernateException;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.stat.Statistics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.dreambu1lder.entities.Order;
import ru.dreambu1lder.entities.Product;

public class HibernateSessionFactoryUtil {

    private static final Logger logger = LoggerFactory.getLogger(HibernateSessionFactoryUtil.class);
    private static SessionFactory sessionFactory;

    public static SessionFactory getSessionFactory() {
        if (sessionFactory == null || sessionFactory.isClosed()) {
            try {
                Configuration configuration = new Configuration();
                configuration.addProperties(HibernateUtil.getHibernateProperties());
                configuration.addAnnotatedClass(MyEntity.class);  // Добавление класса сущности
                configuration.addAnnotatedClass(Order.class);
                configuration.addAnnotatedClass(Product.class);

                // Создаем фабрику с учетом кэша второго уровня
                sessionFactory = configuration.buildSessionFactory();

                // Включение статистики для логирования
                sessionFactory.getStatistics()
                              .setStatisticsEnabled(true);
                logger.info("SessionFactory инициализирована");
            } catch (HibernateException e) {
                logger.error("Ошибка при инициализации SessionFactory", e);
            }
        }
        return sessionFactory;
    }

    public static void shutdown() {
        if (sessionFactory != null) {
            try {
                if (!sessionFactory.isClosed()) {
                    sessionFactory.close();
                }
            } catch (HibernateException e) {
                logger.error("Ошибка при закрытии SessionFactory", e);
            }
        }
    }

    public static Statistics getStatistics() {
        if (sessionFactory != null) {
            return sessionFactory.getStatistics();
        }
        return null;
    }
}
