package ru.dreambu1lder.entities;

import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "orders_products", joinColumns = @JoinColumn(name = "order_id"), inverseJoinColumns = @JoinColumn(name = "product_id"))
    @BatchSize(size = 10)
    @Fetch(FetchMode.SELECT)
    //@Fetch(FetchMode.JOIN)
    private List<Product> orderProducts = new ArrayList<>();

    public Order() {
    }

    public Order(List<Product> orderProducts) {
        this.orderProducts = orderProducts;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<Product> getOrderProducts() {
        return orderProducts;
    }

    public void setOrderProducts(List<Product> orderProducts) {
        this.orderProducts.clear();
        if (orderProducts != null) {
            this.orderProducts.addAll(orderProducts);
        }
    }

    @Override
    public String toString() {
        return "Order{" + "id=" + id + ", orderProducts=" + orderProducts + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Order order = (Order) o;
        return Objects.equals(id, order.id) && Objects.equals(orderProducts, order.orderProducts);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, orderProducts);
    }

    public static class Builder {
        private Long id;
        private List<Product> orderProducts = new ArrayList<>();

        public Builder withId(Long id) {
            this.id = id;
            return this;
        }

        public Builder withOrderProducts(List<Product> orderProducts) {
            this.orderProducts = orderProducts;
            return this;
        }

        public Order build() {
            Order order = new Order();
            order.setId(this.id);
            order.setOrderProducts(this.orderProducts);
            return order;
        }
    }
}

