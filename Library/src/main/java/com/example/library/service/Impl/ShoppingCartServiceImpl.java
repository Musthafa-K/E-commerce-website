package com.example.library.service.Impl;

import com.example.library.dto.CartItemDto;
import com.example.library.dto.ProductDto;
import com.example.library.dto.ShoppingCartDto;
import com.example.library.exception.InsufficientQuantityException;
import com.example.library.model.CartItem;
import com.example.library.model.Customer;
import com.example.library.model.Product;
import com.example.library.model.ShoppingCart;
import com.example.library.repository.CartItemReposirory;
import com.example.library.repository.ShoppingCartRepository;
import com.example.library.service.CustomerService;
import com.example.library.service.ShoppingCartService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service

public class ShoppingCartServiceImpl implements ShoppingCartService {

    private final ShoppingCartRepository cartRepository;
    private final CartItemReposirory itemReposirory;
    private final CustomerService customerService;

    public ShoppingCartServiceImpl(ShoppingCartRepository cartRepository, CartItemReposirory itemReposirory, CustomerService customerService){
        this.cartRepository =cartRepository;
        this.itemReposirory=itemReposirory;
        this.customerService=customerService;

    }

    //ORIGINAL ONE
    @Override
    public ShoppingCart addItemToCart(ProductDto productDto,int
                                      quantity,String username) {
        Customer customer = customerService.findByUsername(username);
        ShoppingCart shoppingCart = customer.getCart();
        CartItem existingCartItem=itemReposirory.findByProductId(productDto.getId());
        System.out.println(shoppingCart+"addItem To cart Cart Service");

        if(existingCartItem !=null){
            existingCartItem.setQuantity(existingCartItem.getQuantity()+1);
            itemReposirory.save(existingCartItem);
        }
        if (shoppingCart == null) {
            shoppingCart = new ShoppingCart();
            System.out.println(getCart(username));
        }
        Set<CartItem> cartItemList = shoppingCart.getCartItems();
        CartItem cart = find(cartItemList, productDto.getId());
        Product product = transfer(productDto);
        System.out.println(product.getCategory());

        double unitPrice = productDto.getCostPrice();

        int itemQuantity = 0;
        if (cartItemList == null) {
            cartItemList = new HashSet<>();
            if (cart == null) {
                cart = new CartItem();
                cart.setProduct(product);
                cart.setCart(shoppingCart);
                cart.setQuantity(quantity);
                cart.setUnitPrice(unitPrice);
                cart.setCart(shoppingCart);
                cartItemList.add(cart);
                itemReposirory.save(cart);

            } else {
                itemQuantity = cart.getQuantity() + quantity;
                cart.setQuantity(itemQuantity);
                itemReposirory.save(cart);
            }
        } else {
            if (cart == null) {
                cart = new CartItem();
                cart.setProduct(product);
                cart.setCart(shoppingCart);
                cart.setQuantity(quantity);
                cart.setUnitPrice(unitPrice);
                cart.setCart(shoppingCart);
                cartItemList.add(cart);
                itemReposirory.save(cart);

            } else {
                itemQuantity = cart.getQuantity() + quantity;
                cart.setQuantity(itemQuantity);
                itemReposirory.save(cart);

            }
        }
        shoppingCart.setCartItems(cartItemList);
        double totalPrice = totalPrice(shoppingCart.getCartItems());
        int totalItem = totalItem(shoppingCart.getCartItems());

        shoppingCart.setTotalItems(totalItem);
        shoppingCart.setTotalPrice(totalPrice);
        shoppingCart.setCustomer(customer);
        return cartRepository.save(shoppingCart);
    }

//    @Override
//    public ShoppingCart addItemToCart(ProductDto productDto, int quantity, String username) {
//        Customer customer = customerService.findByUsername(username);
//        ShoppingCart shoppingCart = customer.getCart();
//        CartItem existingCartItem = itemReposirory.findByProductId(productDto.getId());
//
//        if (shoppingCart == null) {
//            shoppingCart = new ShoppingCart();
//        }
//
//        Set<CartItem> cartItemList = shoppingCart.getCartItems();
//
//        if (existingCartItem != null) {
//            existingCartItem.setQuantity(existingCartItem.getQuantity() + quantity);
//            itemReposirory.save(existingCartItem);
//        } else {
//
//            CartItem cart = new CartItem();
//            Product product = transfer(productDto);
//            double unitPrice = productDto.getCostPrice();
//
//            cart.setProduct(product);
//            cart.setCart(shoppingCart);
//            cart.setQuantity(quantity);
//            cart.setUnitPrice(unitPrice);
//            cartItemList.add(cart);
//            itemReposirory.save(cart);
//        }
//
//        shoppingCart.setCartItems(cartItemList);
//
//        double totalPrice = totalPrice(shoppingCart.getCartItems());
//        int totalItem = totalItem(shoppingCart.getCartItems());
//
//        shoppingCart.setTotalItems(totalItem);
//        shoppingCart.setTotalPrice(totalPrice);
//        shoppingCart.setCustomer(customer);
//
//        return cartRepository.save(shoppingCart);
//    }






    @Override
    public ShoppingCart updateCart(ProductDto productDto, int quantity, String username) {
        System.out.println("Shopping cart updated function");
        Customer customer = customerService.findByUsername(username);
        ShoppingCart shoppingCart = customer.getCart();
        Set<CartItem> cartItemList = shoppingCart.getCartItems();
        CartItem item = find(cartItemList, productDto.getId());
        int itemQuantity = quantity;

        if (productDto.getCurrentQuantity() < quantity) {
            throw new InsufficientQuantityException("Insufficient quantity available for product: " + productDto.getName());
        }

        item.setQuantity(itemQuantity);
        itemReposirory.save(item);
        shoppingCart.setCartItems(cartItemList);
        int totalItem = totalItem(cartItemList);
        double totalPrice = totalPrice(cartItemList);
        shoppingCart.setTotalPrice(totalPrice);
        shoppingCart.setTotalItems(totalItem);
        return cartRepository.save(shoppingCart);
    }


       //ORIGINAL ONE
    @Override
    public ShoppingCart removeItemFromCart(ProductDto productDto,String username){
        Customer customer=customerService.findByUsername(username);
        ShoppingCart shoppingCart=customer.getCart();
        Set<CartItem>cartItemList=shoppingCart.getCartItems();
        CartItem item=find(cartItemList,productDto.getId());
        cartItemList.remove(item);
        itemReposirory.delete(item);

        double totalPrice = totalPrice(cartItemList);
        int totalItem = totalItem(cartItemList);
        shoppingCart.setCartItems(cartItemList);
        shoppingCart.setTotalPrice(totalPrice);
        shoppingCart.setTotalItems(totalItem);
        return cartRepository.save(shoppingCart);


    }











    @Override
    public ShoppingCart updateTotalPrice(Double newTotalPrice,String username) {
        Customer customer = customerService.findByUsername(username);
        ShoppingCart shoppingCart = customer.getCart();
        shoppingCart.setTotalPrice(newTotalPrice);

        cartRepository.save(shoppingCart);


        return shoppingCart;
    }



    @Override
    public ShoppingCart combineCart(ShoppingCartDto cartDto,ShoppingCart cart){
        if (cart==null){
            cart=new ShoppingCart();
        }
        Set<CartItem>cartItems=cart.getCartItems();
        if (cartItems==null){
            cartItems= new HashSet<>();

        }
        Set<CartItem> cartItemDto=convertCartItem(cartDto.getCartItems(),cart);
         for (CartItem cartItem:cartItemDto){
             cartItems.add(cartItem);
         }
         double totalPrice=totalPrice(cartItems);
         int totalItems=totalItem(cartItems);
         cart.setTotalItems(totalItems);
         cart.setCartItems(cartItems);
         cart.setTotalPrice(totalPrice);
         return cart;
    }

    @Transactional
    @Override
    public void deleteCartById(long id) {
        ShoppingCart shoppingCart = cartRepository.getById(id);
        for (CartItem cartItem : shoppingCart.getCartItems()) {
            cartItem.setCart(null);
            itemReposirory.deleteById(cartItem.getId());
        }
        shoppingCart.setCustomer(null);
        shoppingCart.getCartItems().clear();
        shoppingCart.setTotalPrice(0);
        shoppingCart.setTotalItems(0);
        cartRepository.save(shoppingCart);
    }



          @Override
          public ShoppingCart getCart(String username){
            Customer customer=customerService.findByUsername(username);
            ShoppingCart cart=customer.getCart();
            return cart;
          }



    private CartItem find(Set< CartItem >cartItems,long productId){
        if(cartItems==null){
            return  null;
        }
        CartItem cartItem=null;
        for(CartItem item:cartItems){
            if(item.getProduct().getId()==productId){
                cartItem=item;
            }

        }
        return cartItem;

    }
    private CartItemDto findInDTO(ShoppingCartDto shoppingCart,long productId){
        if(shoppingCart==null){
            return null;
        }
        CartItemDto cartItem=null;
        for (CartItemDto item:shoppingCart.getCartItems()){
            if(item.getProduct().getId()==productId){
                cartItem=item;
            }
        }
        return cartItem;
    }
    private Product transfer(ProductDto productDto){
        Product product=new Product();
        product.setId(productDto.getId());
        product.setName(productDto.getName());
        product.setCurrentQuantity(productDto.getCurrentQuantity());
        product.setCategory(productDto.getCategory());
        product.setDescription(productDto.getDescription());
        product.setImages(productDto.getImages());
        product.setCostPrice(productDto.getCostPrice());
        product.setSalePrice(productDto.getSalePrice());
        product.set_activated(productDto.isActivated());
        product.set_deleted(productDto.isDeleted());
        return product;
    }

        private int totalItem(Set< CartItem > cartItemList){
        int totalItem=0;
        for (CartItem item: cartItemList){
            totalItem+=item.getQuantity();
        }
        return totalItem;
        }

        private double totalPrice(Set< CartItem > cartItemList){
         double totalPrice=0.0;
         for (CartItem item: cartItemList){
             totalPrice+=item.getUnitPrice()*item.getQuantity();
         }
         return totalPrice;
        }

        private int totalItemDto(Set< CartItemDto > cartItemList){
           int totalItem=0;
           for (CartItemDto item: cartItemList){
               totalItem += item.getQuantity();
           }
           return totalItem;
        }
        private String totalPriceDto(Set< CartItemDto > cartItemList){
          String totalPrice= String.valueOf(0.0);
        for(CartItemDto item: cartItemList){
            totalPrice += item.getUnitPrice()*item.getQuantity();
        }
        return  totalPrice;
        }
        private Set<CartItem> convertCartItem(Set<CartItemDto>cartItemDtos,ShoppingCart cart){
              Set<CartItem>cartItems=new HashSet<>();
              for (CartItemDto cartItemDto:cartItemDtos){
                  CartItem cartItem=new CartItem();
                  cartItem.setQuantity(cartItemDto.getQuantity());
                  cartItem.setProduct(transfer(cartItemDto.getProduct()));
                  cartItem.setUnitPrice(cartItem.getUnitPrice());
                  cartItem.setId(cartItemDto.getId());
                  cartItem.setCart(cart);
                  cartItems.add(cartItem);
              }
              return cartItems;
        }
        @Override
    public double getShoppingCartTotal(List<CartItem> cartItems) {
        double total = 0.0;

        for (CartItem cartItem : cartItems) {
            total += cartItem.getUnitPrice() * cartItem.getQuantity();
        }

        return total;
    }


}
