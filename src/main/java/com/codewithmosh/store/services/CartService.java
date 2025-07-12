package com.codewithmosh.store.services;

// The controller is responsible for handling the HTTP request and response
// The Service class contains the application logic
// Database -> Entity -> Service -> Controller

import com.codewithmosh.store.DTO.CartDto;
import com.codewithmosh.store.DTO.CartItemDto;
import com.codewithmosh.store.Mappers.CartMapper;
import com.codewithmosh.store.entities.Cart;
import com.codewithmosh.store.exceptions.CartNotFoundException;
import com.codewithmosh.store.exceptions.ProductNotFoundException;
import com.codewithmosh.store.repositories.CartRepository;
import com.codewithmosh.store.repositories.ProductRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@AllArgsConstructor
public class CartService {

    // MARK: Repos
    private CartRepository cartRepository;
    private ProductRepository productRepository;

    // MARK: Mappers
    private CartMapper cartMapper;

    // MARK: Methods
    public CartDto createCart() {
        var cart = new Cart();
        cartRepository.save(cart);
        return cartMapper.toDto(cart);
    }

    public CartItemDto addToCart(UUID cartId, Long productID) {
        var cart = cartRepository.getCartWithItems(cartId).orElse(null);
        if (cart == null) throw new CartNotFoundException();

        var product = productRepository.findById(productID).orElse(null);
        if (product == null) throw new ProductNotFoundException();

        var cartItem = cart.addItem(product);

        cartRepository.save(cart);
        return cartMapper.toCartItemDto(cartItem);
    }

    public Cart getCartById(UUID cartId) {
        var cart = cartRepository.getCartWithItems(cartId).orElse(null);
        if (cart == null) throw new CartNotFoundException();
        return cart;
    }

    public CartDto getCart(UUID cartId) {
        var cart = getCartById(cartId);
        return cartMapper.toDto(cart);
    }

    public CartItemDto updateItem(UUID cartId, Long productId, Integer quantity) {
        var cart = cartRepository.getCartWithItems(cartId).orElse(null);
        if (cart == null) throw new CartNotFoundException();

        var cartItem = cart.getItem(productId);

        if (cartItem == null) throw new ProductNotFoundException();

        cartItem.setQuantity(quantity);
        cartRepository.save(cart);

        return cartMapper.toCartItemDto(cartItem);
    }

    public void removeItem(UUID cartId, Long productId) {
        var cart = cartRepository.getCartWithItems(cartId).orElse(null);
        if (cart == null) throw new CartNotFoundException();

        cart.removeItem(productId);
        cartRepository.save(cart);
    }

    public void clearCart(UUID cartId) {
        var cart = cartRepository.getCartWithItems(cartId).orElse(null);
        if (cart == null) throw new CartNotFoundException();
        cart.clear();
        cartRepository.save(cart);
    }

}
