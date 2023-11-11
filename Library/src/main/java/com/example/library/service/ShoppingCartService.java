package com.example.library.service;

import com.example.library.dto.ProductDto;
import com.example.library.dto.ShoppingCartDto;
import com.example.library.model.CartItem;
import com.example.library.model.ShoppingCart;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface ShoppingCartService {

    ShoppingCart addItemToCart(ProductDto productDto,int quantity,String username);
    ShoppingCart updateCart(ProductDto productDto,int quantity,String username) throws Exception;
    ShoppingCart removeItemFromCart(ProductDto productDto,String username);


    ShoppingCart combineCart(ShoppingCartDto shoppingCartDto,ShoppingCart shoppingCart);



    @Transactional
    void deleteCartById(long id);

    ShoppingCart getCart(String username);





    ShoppingCart updateTotalPrice(Double newTotalPrice, String username);


    public double getShoppingCartTotal(List<CartItem> cartItems);



}
