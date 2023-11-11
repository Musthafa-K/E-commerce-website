package com.example.customer.controller;

import com.example.library.model.Customer;
import com.example.library.service.CustomerService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.security.Principal;

@Controller
public class HomeController {

    private final CustomerService customerService;

    public HomeController(CustomerService customerService){
        this.customerService=customerService;
    }
//    @RequestMapping(value = {"/","/index"},method = RequestMethod.GET)
//    public String home(Model model, Principal principal, HttpSession session){
//        model.addAttribute("title","Home");
//        model.addAttribute("page","Home");
//           if(principal !=null){
//               Customer customer=customerService.findByUsername(principal.getName());
//               session.setAttribute("username",customer.getFirstName()+""+customer.getLastName());
//               //Shopping
//           }
//           return "home";
//    }
}
