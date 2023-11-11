package com.example.customer.controller;

import com.example.library.dto.CustomerDto;
import com.example.library.model.Customer;
import com.example.library.service.CustomerService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
public class LoginController {
    private final CustomerService customerService;
    private final BCryptPasswordEncoder passwordEncoder;
    public LoginController(CustomerService customerService, BCryptPasswordEncoder passwordEncoder){
        this.customerService=customerService;
        this.passwordEncoder=passwordEncoder;
    }
    @GetMapping ( "/login")
       public String longin(Model model){
        model.addAttribute("title","Login Page");
        model.addAttribute("page","Home");
        return "login";

    }
    @GetMapping("/register")
    public String register(Model model){
        model.addAttribute("title","Register");
        model.addAttribute("page","Register");
        model.addAttribute("customerDto",new CustomerDto());
        return "register";
    }
    @PostMapping("/do-register")
    public String registerCustomer(@Valid @ModelAttribute("customerDto")CustomerDto customerDto,
                                   BindingResult result,
                                   Model model){
        try{
            if(result.hasErrors()){
                model.addAttribute("customerDto",customerDto);
                return "register";
            }
            String username=customerDto.getUsername();
            Customer customer=customerService.findByUsername(username);
            if(customer!=null){
                model.addAttribute("customerDto",customerDto);
                model.addAttribute("error","Email has been register !");
                return "register";
            }
            if(customerDto.getPassword().equals(customerDto.getConfirmPassword())){
                customerDto.setPassword(passwordEncoder.encode(customerDto.getPassword()));
                customerService.save(customerDto);
                model.addAttribute("success","Register successfully !");
            }else{
                model.addAttribute("error","pasword is incorrect");
                model.addAttribute("customerDto",customerDto);
                return  "register";
            }
        }catch (Exception e){
            e.printStackTrace();
            model.addAttribute("error","Server is error,try again later !");
        }
        return "register";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.removeAttribute("address");
        session.invalidate();
        return "redirect:/login";
    }



}
