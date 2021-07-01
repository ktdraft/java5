package edu.poly.pk01572.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import edu.poly.pk01572.domain.Order;

public interface OrderRepository extends JpaRepository<Order, Long> {

}
