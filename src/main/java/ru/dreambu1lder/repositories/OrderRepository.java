package ru.dreambu1lder.repositories;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import ru.dreambu1lder.HibernateSessionFactoryUtil;
import ru.dreambu1lder.entities.Order;

import java.util.List;

public class OrderRepository {
    public List<Order> findAll() {
        List<Order> ordersWithProducts = null;

        try (Session session = HibernateSessionFactoryUtil.getSessionFactory()
                                                          .openSession();
        ) {
            Transaction transaction = null;
            transaction = session.beginTransaction();

            try {
                Query<Order> query = session.createQuery("FROM Order", Order.class);
                ordersWithProducts = query.getResultList();

                transaction.commit();
            } catch (Exception e) {
                if (transaction != null) {
                    transaction.rollback();
                }
                e.printStackTrace();
            }
        }

        return ordersWithProducts;
    }
}
