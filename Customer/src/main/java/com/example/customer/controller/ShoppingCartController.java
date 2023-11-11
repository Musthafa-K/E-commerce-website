package com.example.customer.controller;

import com.example.library.dto.AddressDto;
import com.example.library.dto.ProductDto;
import com.example.library.exception.InsufficientQuantityException;
import com.example.library.model.Address;
import com.example.library.model.CartItem;
import com.example.library.model.Customer;
import com.example.library.model.ShoppingCart;
import com.example.library.repository.CartItemReposirory;
import com.example.library.service.AddressService;
import com.example.library.service.CustomerService;
import com.example.library.service.Impl.ShoppingCartServiceImpl;
import com.example.library.service.ProductService;
import com.example.library.service.ShoppingCartService;
import com.razorpay.RazorpayException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.List;
import java.util.Set;

@Controller
public class ShoppingCartController {
    private final ShoppingCartService cartService;
    private final ProductService productService;
    private final CustomerService customerService;
    private final AddressService addressService;
    private final CartItemReposirory itemReposirory;
    private final ShoppingCartServiceImpl shoppingCartService;
    public ShoppingCartController(ShoppingCartService cartService, ProductService productService,
                                  CustomerService customerService, AddressService addressService, CartItemReposirory itemReposirory,ShoppingCartServiceImpl shoppingCartService) {
        this.cartService = cartService;
        this.productService = productService;
        this.customerService = customerService;
        this.addressService=addressService;
        this.itemReposirory=itemReposirory;
        this.shoppingCartService=shoppingCartService;

    }

    @GetMapping("/cart")
    public String Cart(Model model,HttpServletRequest httpServletRequest, Principal principal, RedirectAttributes redirectAttributes) {
        if (principal == null) {
            return "redirect:/login";
        } else {
            Customer customer = customerService.findByUsername(principal.getName());
            ShoppingCart cart = customer.getCart();
            Set<CartItem> item = cart.getCartItems();
            if (cart == null||cart.getCartItems().isEmpty()) {
                System.out.println("cart null");
                redirectAttributes.addFlashAttribute("text", "Cart is empty");
                String referer=httpServletRequest.getHeader("referer");
                if (referer=="http://localhost:8020/user/cart"){
                    return "redirect:/shoplist";
                }
                //return "cart";
            }
            if (cart != null) {
                model.addAttribute("grantTotal", cart.getTotalPrice());
            }
            model.addAttribute("shoppingCart", cart);
            model.addAttribute("cartItem",item);
            model.addAttribute("title", "Cart");
            return "cart";
        }
    }
    @RequestMapping(value = "/add-to-cart/{id}", method = RequestMethod.GET)
    public String addItemToCart(@PathVariable("id") Long id,
                                @RequestParam(value = "quantity", required = false, defaultValue = "1") int quantity,
                                HttpServletRequest request, Model model,
                                Principal principal,
                                HttpSession session,
                                RedirectAttributes redirectAttributes){
        ProductDto productDto=productService.getById(id);
        if(principal==null){
            return"redirect:/login";
        }else {
            String username= principal.getName();
            System.out.print(id);
            try {
                ShoppingCart shoppingCart=cartService.addItemToCart(productDto,quantity,username);
                System.out.println(shoppingCart);
                session.setAttribute("totalItem",shoppingCart.getTotalItems());
                model.addAttribute("shoppingCart",shoppingCart);
                Set<CartItem> cartItems = shoppingCart.getCartItems();
                model.addAttribute("cartItem",cartItems);

            }catch (InsufficientQuantityException ex){
                String errorMesssage= ex.getMessage();
                redirectAttributes.addFlashAttribute("errorMessage",errorMesssage);
            }

        }
        return "cart";
    }




    @RequestMapping(value = "/update-cart/{id}", method = RequestMethod.POST, params = "action-update")
    public String updateCart(@RequestParam(value = "id",required = false) Long id,
                             @RequestParam(value = "quantity",required = false) int quantity,

                             Model model,
                             Principal principal,RedirectAttributes redirectAttributes,HttpServletRequest httpServletRequest) throws Exception {
        if (principal == null) {
            return "redirect:/login";
        } else {

            ProductDto productDto = productService.getById(id);
            String username = principal.getName();
            try{
                ShoppingCart shoppingCart = cartService.updateCart(productDto, quantity, username);
                model.addAttribute("shoppingCart",shoppingCart);

            }catch (InsufficientQuantityException ex){
                String errorMessge=ex.getMessage();
                redirectAttributes.addFlashAttribute("errorMessage",errorMessge);
            }



        }
        return "redirect:/cart";
    }

//    @RequestMapping(value = "/update-cart/{id}",method = RequestMethod.POST,params = "action-update")
//    public String updateCart(@RequestParam("id")Long id,
//                             @RequestParam("quantity")int quantity,
//
//                             Model model,
//                             Principal principal)  {
//        if (principal==null){
//            return "redirect:/login";
//        }else {
//
//            ProductDto productDto=productService.getById(id);
//            String username= principal.getName();
//            ShoppingCart shoppingCart=cartService.updateCart(productDto,quantity,username);
//            model.addAttribute("shoppingCart",shoppingCart);
//            return "redirect:/cart";
//        }
//    }
    @RequestMapping(value = "/delete-cart/{id}",method=RequestMethod.POST,params = "action-delete")
    public String deleteItem(@RequestParam("id")Long id,
                             Model model,
                             Principal principal){
        if(principal==null){
            return "redirect:/login";
        }else{
            ProductDto productDto=productService.getById(id);
            String username=principal.getName();
            ShoppingCart shoppingCart=cartService.removeItemFromCart(productDto,username);
            model.addAttribute("shopppingCart",shoppingCart);
            return "redirect:/cart";
        }
    }






}

