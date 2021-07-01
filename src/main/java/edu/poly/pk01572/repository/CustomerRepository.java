package edu.poly.pk01572.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import edu.poly.pk01572.domain.Customer;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
	@Query(value = "select * from customer c where c.email =:email", nativeQuery = true)
	public Customer findByEmail(@Param("email") String email);

}
