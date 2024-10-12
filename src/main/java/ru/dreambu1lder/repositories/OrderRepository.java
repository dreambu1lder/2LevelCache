package ru.dreambu1lder.repositories;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import ru.dreambu1lder.HibernateSessionFactoryUtil;
import ru.dreambu1lder.entities.Order;

import java.util.List;

public class OrderRepository {
    public List<Order> findAll() {
        Session session = HibernateSessionFactoryUtil.getSessionFactory()
                                                     .openSession();
        Transaction transaction = null;
        List<Order> ordersWithProducts = null;

        try {
            transaction = session.beginTransaction();

            Query<Order> query = session.createQuery("FROM Order", Order.class);
            ordersWithProducts = query.getResultList();

            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }

            e.printStackTrace();
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }

        return ordersWithProducts;
    }
}
