package com.example.customer.controller;

import com.example.library.dto.AddressDto;
import com.example.library.dto.CouponDto;
import com.example.library.dto.CustomerDto;
import com.example.library.dto.ProductDto;
import com.example.library.exception.OutOfStockException;
import com.example.library.model.*;
import com.example.library.service.*;
import com.fasterxml.jackson.databind.util.JSONPObject;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.razorpay.Utils;
import jakarta.servlet.http.HttpSession;
import org.json.JSONObject;
import org.slf4j.LoggerFactory;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.logging.Logger;
import java.security.Principal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Controller

public class OrderController {

    private  ProductService productService;

    private CustomerService customerService;
    private OrderService orderService;
    private ShoppingCartService shoppingCartService;
    private AddressService addressService;
    private WalletService walletService;

    private CouponService couponService;

    public OrderController (CustomerService customerService,OrderService orderService,ShoppingCartService shoppingCartService,
                            AddressService addressService,WalletService walletService, CouponService couponService,ProductService productService){
        this.addressService=addressService;
        this.customerService=customerService;
        this.walletService=walletService;
        this.shoppingCartService=shoppingCartService;
        this.orderService=orderService;
        this.couponService=couponService;
        this.productService=productService;
    }

    @GetMapping("/check-out")
    public String checkOut(Principal principal, Model model) {
        if (principal == null) {
            return "redirect:/login";
        } else {
            Customer customer = customerService.findByUsername(principal.getName());

            ShoppingCart cart = customerService.findByUsername(principal.getName()).getCart();
            Set<CartItem> cartItems=cart.getCartItems();
            List<Address> addressList = customer.getAddresses();
            Wallet wallet=walletService.findByCustomer(customer);
            List<CouponDto> couponDto=couponService.getAllCoupons();
            model.addAttribute("wallet",wallet);
            model.addAttribute("coupons",couponDto);
            model.addAttribute("addressDto",new AddressDto());
            model.addAttribute("customer", customer);
            model.addAttribute("addressList", addressList);
            model.addAttribute("size",addressList.size());
            model.addAttribute("cartItems", cartItems);
            model.addAttribute("page", "Check-Out");
            model.addAttribute("shoppingCart", cart);
            model.addAttribute("grandTotal", cart.getTotalItems());
            return "checkout";
        }
    }
//       ORIGINAL ONE
    @RequestMapping(value = "/add-order", method = RequestMethod.POST)
    @ResponseBody
    public String createOrder(@RequestBody Map<String, Object> data, Principal principal, HttpSession session, Model model) throws Exception {
        System.out.println("Order created");
        String paymentMethod = data.get("payment_Method").toString();
        Long address_id = Long.parseLong(data.get("addressId").toString());
        Double oldTotalPrice = (Double) session.getAttribute("totalPrice");
        Customer customer = customerService.findByUsername(principal.getName());
        ShoppingCart cart = customer.getCart();
        if (paymentMethod.equals("COD")) {
            if (address_id==null){
                throw new Exception("The address_id value does not exist in the address table.");
            }
            System.out.println("Cash on delivery");
            Order order = orderService.save(cart, address_id, paymentMethod, oldTotalPrice);
            System.out.println("checked orderService");
            session.removeAttribute("totalItems");
            session.removeAttribute("totalPrice");
            session.setAttribute("orderId", order.getId());
            model.addAttribute("order", order);
            model.addAttribute("page", "Order Detail");
            model.addAttribute("success", "Order Added Successfully");
            JSONObject options = new JSONObject();
            options.put("status", "Cash");
            return options.toString();
        } else if (paymentMethod.equals("Wallet")) {
            System.out.println("Wallet");
            walletService.debit(customer.getWallet(), cart.getTotalPrice());
            Order order = orderService.save(cart, address_id, paymentMethod, oldTotalPrice);
            session.removeAttribute("totalItems");
            session.removeAttribute("totalPrice");
            session.setAttribute("orderId", order.getId());
            model.addAttribute("order", order);
            model.addAttribute("page", "Order Detail");
            model.addAttribute("success", "Order Added Successfully");
            JSONObject options = new JSONObject();
            options.put("status", "Wallet");
            return options.toString();
        } else {
            System.out.println("Delivery on else case");
            Order order = orderService.save(cart, address_id, paymentMethod, oldTotalPrice);
            System.out.println("checked order service");
            String orderId = order.getId().toString();
            System.out.println("checked orderId");
            session.removeAttribute("totalItems");
            session.removeAttribute("totalPrice");
            session.setAttribute("orderId", order.getId());
            model.addAttribute("order", order);
            model.addAttribute("page", "Order Detail");
            model.addAttribute("success", "Order Added Successfully");
            RazorpayClient razorpayClient = new RazorpayClient("rzp_test_wrxGjL0caXafmI","0BopJJwocFHawFFxrRXAYkMJ");
            JSONObject options = new JSONObject();
            options.put("amount", order.getTotalPrice() * 100);
            options.put("currency", "INR");
            options.put("receipt", orderId);
            com.razorpay.Order orderRazorPay = razorpayClient.orders.create(options);
            return orderRazorPay.toString();
        }

    }



    @RequestMapping(value = "/check-out/apply-coupon", method = RequestMethod.POST, params = "action=apply")
    public String applyCoupon(@RequestParam("couponCode")String couponCode,Principal principal,
                              RedirectAttributes attributes,HttpSession session){


        if(couponService.findByCouponCode(couponCode.toUpperCase())) {

            Coupon coupon = couponService.findByCode(couponCode.toUpperCase());
            ShoppingCart cart = customerService.findByUsername(principal.getName()).getCart();
            Double totalPrice = cart.getTotalPrice();
            session.setAttribute("totalPrice",totalPrice);
            Double newTotalPrice = couponService.applyCoupon(couponCode.toUpperCase(), totalPrice);

            shoppingCartService.updateTotalPrice(newTotalPrice, principal.getName());

            attributes.addFlashAttribute("success", "Coupon applied Successfully");
            attributes.addAttribute("couponCode", couponCode);
            attributes.addAttribute("couponOff", coupon.getOffPercentage());
            return "redirect:/check-out";
        }else{
            attributes.addFlashAttribute("error", "Coupon Code invalid");
            return "redirect:/check-out";
        }

    }

    @RequestMapping(value = "/check-out/apply-coupon", method = RequestMethod.POST, params = "action=remove")
    public String removeCoupon(Principal principal,RedirectAttributes attributes,
                               HttpSession session){

        Double totalPrice = (Double) session.getAttribute("totalPrice");
        shoppingCartService.updateTotalPrice(totalPrice, principal.getName());
        attributes.addFlashAttribute("success", "Coupon removed Successfully");
//        session.removeAttribute("totalPrice");

        return "redirect:/check-out";
    }

    private static final Logger logger=Logger.getLogger(OrderController.class.getName());

    @ExceptionHandler(OutOfStockException.class)
    public String handleOutOfStockException(OutOfStockException ex,RedirectAttributes redirectAttributes){
        redirectAttributes.addFlashAttribute("errorMessage",ex.getMessage());
        logger.info("Error message"+ ex.getMessage());
        return "redirect:/checkout";
    }



    @RequestMapping(value = "/verify-payment",method = RequestMethod.POST)
    @ResponseBody
    public String verifyPayment(@RequestBody Map<String,Object> data,HttpSession session,Principal principal) throws RazorpayException {

        String secret= "0BopJJwocFHawFFxrRXAYkMJ";
        String order_id= data.get("razorpay_order_id").toString();
        String payment_id=data.get("razorpay_payment_id").toString();
        String signature=data.get("razorpay_signature").toString();
        JSONObject options = new JSONObject();
        options.put("razorpay_order_id", order_id);
        options.put("razorpay_payment_id", payment_id);
        options.put("razorpay_signature", signature);

        boolean status =  Utils.verifyPaymentSignature(options, secret);
        Order order=orderService.findOrderById((Long)session.getAttribute("orderId"));
        if(status){
            orderService.updatePayment(order,status);
            Customer customer=customerService.findByUsername(principal.getName());
            ShoppingCart cart = customer.getCart();
            shoppingCartService.deleteCartById(cart.getId());
        }else {
            orderService.updatePayment(order, status);
        }
        JSONObject response = new JSONObject();
        response.put("status",status);

        return response.toString();
    }
@GetMapping("/order-confirmation")
public String getOrderConfirmation(Model model,HttpSession session){
    System.out.println("Order confirmation is checked");

    Long order_id=(Long)session.getAttribute("orderId");
    Order order=orderService.findOrderById(order_id);
    String paymentMethod = order.getPaymentMethod();
    if (paymentMethod.equals("COD")){
        model.addAttribute("payment","Pending");
    }else{
        model.addAttribute("payment","Successful");
    }
    model.addAttribute("order", order);
    model.addAttribute("success", "Order Added Successfully");
    session.removeAttribute("orderId");

    return "order-detail";
}


    @GetMapping("/orders")
    public String getOrder(Principal principal,Model model){
        if (principal == null) {
            return "redirect:/login";
        } else {
            Customer customer = customerService.findByUsername(principal.getName());
            List<Order> orderList = orderService.findAllOrdersByCustomer(customer.getId());
            model.addAttribute("orders", orderList);
            model.addAttribute("title", "Order");
            model.addAttribute("page", "Order");
            return "orders";
        }
    }

    @GetMapping("/cancel-order/{id}")
    public String cancelOrder(@PathVariable("id")long order_id, RedirectAttributes attributes){
        orderService.cancelOrder(order_id);
        attributes.addFlashAttribute("success", "Cancel order successfully!");
        return "redirect:/account";
    }

    @GetMapping("/return-order/{id}")
    public String returnOrder(@PathVariable("id")long order_id, RedirectAttributes attributes,
                              Principal principal){
        Customer customer=customerService.findByUsername(principal.getName());
        orderService.returnOrder(order_id,customer);
        attributes.addFlashAttribute("success", "Order Returned successfully!");
        return "redirect:/account";
    }



    @GetMapping("/order-view/{id}")
    public String orderView(@PathVariable("id")long order_id,Model model,HttpSession session){
    Order order=orderService.findOrderById(order_id);
        System.out.println(order);
    String paymentMethod = order.getPaymentMethod();
    if (paymentMethod.equals("COD")){
        model.addAttribute("payment","Pending");
    }else {
        model.addAttribute("payment", "Paid");
    }
    Customer customer=customerService.findById(order.getCustomer().getId());
        System.out.println(customer);
    Address address = addressService.findDefaultAddress(customer.getId());
        System.out.println(address);
    model.addAttribute("order",order);
    model.addAttribute("address",address);

    return "order-view";
}


    @GetMapping("/order-tracking/{id}")
    public String orderTrack(@PathVariable("id")long order_id,Model model,HttpSession session,Principal principal){

        Order order=orderService.findOrderById(order_id);
        String paymentMethod = order.getPaymentMethod();
        System.out.println("Order Tracking");
        if (paymentMethod.equals("COD")){
            model.addAttribute("payment","Pending");
        }else {
            model.addAttribute("payment", "Paid");
        }
        Date currentTime = new Date();
        Customer customer=customerService.findById(order.getCustomer().getId());
        Address address = addressService.findDefaultAddress(customer.getId());
        if(order.getOrderStatus().equals("Pending")){
            int pending=1;
            model.addAttribute("pending",pending);
        }else if(order.getOrderStatus().equals("Confirmed")){
            int pending=1;
            int confirmed=2;
            model.addAttribute("pending",pending);
            model.addAttribute("confirmed",confirmed);
        }else if(order.getOrderStatus().equals("Shipped")){
            int pending=1;
            int confirmed=2;
            int shipped=3;
            model.addAttribute("pending",pending);
            model.addAttribute("confirmed",confirmed);
            model.addAttribute("shipped",shipped);
        }else if(order.getOrderStatus().equals("Delivered")){
            int pending=1;
            int confirmed=2;
            int shipped=3;
            int delivered=4;
            model.addAttribute("pending",pending);
            model.addAttribute("confirmed",confirmed);
            model.addAttribute("shipped",shipped);
            model.addAttribute("delivered",delivered);
        }
        model.addAttribute("order",order);
        model.addAttribute("time",currentTime);
        model.addAttribute("address",address);

        return "order-track";
    }


}
