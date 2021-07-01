package edu.poly.pk01572.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import edu.poly.pk01572.domain.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
	Page<Product> findByNameContaining(String name, Pageable pageable);

	Page<Product> findByNameContainingIgnoreCase(String name, Pageable pageable);

	Page<Product> findByNameIgnoreCaseContainingAndCategoryCategoryId(String name, Long categoryId, Pageable pageable);
}
