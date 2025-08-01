package com.codewithmosh.store.services;

import com.codewithmosh.store.DTO.OrderDto;
import com.codewithmosh.store.Mappers.OrderMapper;
import com.codewithmosh.store.exceptions.OrderNotFoundException;
import com.codewithmosh.store.repositories.OrderRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;

@AllArgsConstructor
@Service
public class OrderService {
    private final AuthService authService;
    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;

    public List<OrderDto> getAllOrders() {
        var user = authService.getCurrentUser();
        var orders = orderRepository.getOrdersByCustomer(user);
        return orderMapper.toDtoList(orders);
    }

    public OrderDto getOrderById(Long id) {
        var order = orderRepository.getOrderWithItems(id).orElseThrow(OrderNotFoundException::new);
        var user = authService.getCurrentUser();
        if (!order.isPlacedBy(user)) {
            throw new AccessDeniedException("You do not have permission to access this order");
        }
        return orderMapper.toDto(order);
    }
}
