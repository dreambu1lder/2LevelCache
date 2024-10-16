package ru.dreambu1lder.repositories;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.annotations.FetchMode;
import org.hibernate.query.Query;
import ru.dreambu1lder.HibernateSessionFactoryUtil;
import ru.dreambu1lder.entities.Order;
import ru.dreambu1lder.entities.Product;

import java.util.ArrayList;
import java.util.List;

public class OrderRepository {
    public List<Order> findAll() {
        System.out.println("-".repeat(50));
        System.out.println("N+1");
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

                Product product3 = new Product("Product 3", 3.0);
                session.save(product3);

                Product product4 = new Product("Product 4", 4.0);
                session.save(product4);

                Product product5 = new Product("Product 5", 5.0);
                session.save(product5);

                List<Product> products = new ArrayList<>(List.of(product1, product2, product3, product4, product5));
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

        System.out.println("N+1 End");
        System.out.println("-".repeat(50));
        return ordersWithProducts;
    }

        public List<Order> findAllOrdersWithProductsUsingSUBSELECT() {
            List<Order> ordersWithProducts;

            try (Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession()) {
                Transaction transaction = session.beginTransaction();

                try {

                    Criteria criteria = session.createCriteria(Order.class);
                    // Устанавливаем режим FetchMode.SUBSELECT для коллекции products
                    criteria.setFetchMode("products", FetchMode.SUBSELECT.getHibernateFetchMode());
                    ordersWithProducts = criteria.list();

                    transaction.commit();
                } catch (Exception e) {
                    if (transaction != null) {
                        transaction.rollback();
                    }
                    throw e;
                }
            }

            return ordersWithProducts;
        }
}
