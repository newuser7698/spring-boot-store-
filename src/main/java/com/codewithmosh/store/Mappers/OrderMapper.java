package com.codewithmosh.store.Mappers;

import com.codewithmosh.store.DTO.OrderDto;
import com.codewithmosh.store.entities.Order;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface OrderMapper {
    OrderDto toDto(Order order);
    List<OrderDto> toDtoList(List<Order> entities);
}
