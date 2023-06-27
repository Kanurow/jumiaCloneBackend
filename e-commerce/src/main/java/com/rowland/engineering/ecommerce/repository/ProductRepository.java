package com.rowland.engineering.ecommerce.repository;

import com.rowland.engineering.ecommerce.model.Category;
import com.rowland.engineering.ecommerce.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {


    Optional<Product> findByProductName(String name);
    List<Product> findAllByCategory(Category categoryName);


}
