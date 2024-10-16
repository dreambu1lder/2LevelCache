package ru.dreambu1lder.services;

import ru.dreambu1lder.entities.Order;
import ru.dreambu1lder.entities.Product;
import ru.dreambu1lder.repositories.OrderRepository;

import java.util.List;

public class OrderServiceImpl {
    private final OrderRepository orderRepository = new OrderRepository();

    public List<Order> getAllOrdersWithProducts() {
        List<Order> orders = orderRepository.findAll();

        for (Order order : orders) {
            List<Product> products = order.getOrderProducts();
        }

        return orders;
    }

    public List<Order> getAllOrdersWithProductsUsingSUBSELECT(){
        return orderRepository.findAllOrdersWithProductsUsingSUBSELECT();
    }
}
