package ru.dreambu1lder.repositories;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import ru.dreambu1lder.HibernateSessionFactoryUtil;
import ru.dreambu1lder.entities.Order;
import ru.dreambu1lder.entities.Product;
import ru.dreambu1lder.services.OrderServiceImpl;

import java.util.ArrayList;
import java.util.List;

public class OrderRepository {
    public List<Order> findAll() {
        System.out.println("-".repeat(50));
        System.out.println("OrderRepository N+1");
        List<Order> ordersWithProducts = null;

        try (Session session = HibernateSessionFactoryUtil.getSessionFactory()
                                                          .openSession();
        ) {
            Transaction transaction = null;
            transaction = session.beginTransaction();

            try {
                Product product1 = new Product("Product 1", 1.0);
                session.save(product1);

                Product product2 = new Product("Product 2", 2.0);
                session.save(product2);

                List<Product> products = new ArrayList<>(List.of(product1, product2));
                Order order1 = new Order(products);
                session.save(order1);

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

        System.out.println("OrderRepository N+1 End");
        System.out.println("-".repeat(50));
        return ordersWithProducts;
    }
}
