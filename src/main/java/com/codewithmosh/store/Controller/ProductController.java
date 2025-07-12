package com.codewithmosh.store.Controller;

import com.codewithmosh.store.DTO.ProductDto;
import com.codewithmosh.store.DTO.RegisterProductRequest;
import com.codewithmosh.store.DTO.UpdateProductRequest;
import com.codewithmosh.store.Mappers.ProductMapper;
import com.codewithmosh.store.repositories.CategoryRepository;
import com.codewithmosh.store.repositories.ProductRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/products")
public class ProductController {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final CategoryRepository categoryRepository;


    @GetMapping("")
    public List<ProductDto> getProducts(
            @RequestParam(required = false, name = "categoryId") Byte categoryId
    ) {
        if (categoryId == null) {
            return productMapper.toDtoList(productRepository.findAllWithCategory());
        }
        return productMapper.toDtoList(productRepository.findAllByCategoryId(categoryId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductDto> getProductsByCategory(@PathVariable Long id) {
        var product = productMapper.toDto(productRepository.findById(id).orElse(null));
        if (product == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(product);
    }

    @PostMapping("")
    public ResponseEntity<ProductDto> createProduct(
            @RequestBody RegisterProductRequest request,
            UriComponentsBuilder uriBuilder

    ) {
        var product = productMapper.toEntity(request);
        var category = categoryRepository.findById(request.getCategoryId()).orElse(null);
        if (category == null) {
            return ResponseEntity.notFound().build();
        }
        product.setCategory(category);
        productRepository.save(product);

        var productDto = productMapper.toDto(product);
        var uri = uriBuilder.path("/products/{id}").buildAndExpand(product.getId()).toUri();
        return ResponseEntity.created(uri).body(productDto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateProduct(
            @PathVariable(name = "id") Long id,
            @RequestBody UpdateProductRequest request
    ) {
        var product = productRepository.findById(id).orElse(null);
        if (product == null) {
            return ResponseEntity.notFound().build();
        }

        var category = categoryRepository.findById(request.getCategoryId()).orElse(null);
        if (category == null) {
            return ResponseEntity.notFound().build();
        }

        productMapper.update(request, product);

        product.setCategory(category);

        productRepository.save(product);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(
            @PathVariable Long id
    ) {
        var product = productRepository.findById(id).orElse(null);
        if (product == null) {
            return ResponseEntity.notFound().build();
        }
        productRepository.delete(product);
        return ResponseEntity.noContent().build();
    }


}
