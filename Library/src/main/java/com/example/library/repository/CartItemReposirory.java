package com.example.library.repository;

import com.example.library.dto.CustomerDto;
import com.example.library.dto.ProductDto;
import com.example.library.model.CartItem;
import com.example.library.model.Customer;
import com.example.library.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CartItemReposirory extends JpaRepository<CartItem,Long> {
 @Query(value ="update cart set shopping_cart_id=null where shopping_cart_id=?1" , nativeQuery = true)
    void deleteCartItemById(Long cartId);

    @Query("SELECT c FROM CartItem c WHERE c.product.id = :productId")
    CartItem findByProductId(@Param("productId") Long productId);

    List<CartItem> findByCustomer(Customer customer);

}
