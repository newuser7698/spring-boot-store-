package com.codewithmosh.store.Mappers;

import com.codewithmosh.store.DTO.ProductDto;
import com.codewithmosh.store.DTO.RegisterProductRequest;
import com.codewithmosh.store.DTO.UpdateProductRequest;
import com.codewithmosh.store.entities.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ProductMapper extends BaseMapper<Product, ProductDto>  {
    @Mapping(source = "category.id", target = "categoryId")
    ProductDto toDto(Product product);

    Product toEntity(RegisterProductRequest request);

    void update(UpdateProductRequest request, @MappingTarget Product product);
}
