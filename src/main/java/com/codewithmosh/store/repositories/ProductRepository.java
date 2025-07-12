package com.codewithmosh.store.repositories;

import com.codewithmosh.store.entities.Product;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    @EntityGraph(attributePaths = "category")
    List<Product> findAllByCategoryId(Byte categoryId);

    @EntityGraph(attributePaths = "category")
    @Query("select p from Product p ")
    List<Product> findAllWithCategory();

    Object findProductById(@NotNull Long productId);
}