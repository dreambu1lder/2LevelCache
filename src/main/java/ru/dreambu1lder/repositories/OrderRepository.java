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

    public List<Order> findAllOrdersWithProducts() {
        System.out.println("-".repeat(50));
        System.out.println("Demonstrating @BatchSize");

        List<Order> ordersWithProducts = null;

        try (Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession()) {
            Transaction transaction = null;
            transaction = session.beginTransaction();

            try {
                // Запрос для загрузки всех заказов
                Query<Order> query = session.createQuery("FROM Order", Order.class);
                ordersWithProducts = query.getResultList(); // Загружаем все заказы

                // При доступе к связанным продуктам будет задействован @BatchSize
                for (Order order : ordersWithProducts) {
                    System.out.println("Order ID: " + order.getId());
                    List<Product> products = order.getOrderProducts(); // Продукты будут загружены пакетами
                    System.out.println("Products for Order: " + products);
                }

                transaction.commit();
            } catch (Exception e) {
                if (transaction != null) {
                    transaction.rollback();
                }
                e.printStackTrace();
            }
        }

        System.out.println("End of @BatchSize demonstration");
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

        System.out.println("End of FetchMode.SUBSELECT demonstration");
        System.out.println("-".repeat(50));
        return ordersWithProducts;
    }

    public List<Order> findAllOrdersWithProductsUsingJOIN() {
        List<Order> ordersWithProducts;

        try (Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();

            try {
                // Устанавливаем режим FetchMode.JOIN для коллекции orderProducts
                Query<Order> query = session.createQuery("SELECT DISTINCT o FROM Order o JOIN FETCH o.orderProducts", Order.class);
                ordersWithProducts = query.getResultList();

                transaction.commit();
            } catch (Exception e) {
                if (transaction != null) {
                    transaction.rollback();
                }
                throw e;
            }
        }

        System.out.println("End of FetchMode.JOIN demonstration");
        System.out.println("-".repeat(50));
        return ordersWithProducts;
    }

}
