package com.example.ecommerce_nawaz;



import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
    // Add any custom queries if needed, e.g., finding by name
    Product findByProductName(String productName);
}