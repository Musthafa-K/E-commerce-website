package com.example.admin.controller;

import com.example.library.dto.CustomerDto;
import com.example.library.service.CustomerService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@Controller
public class CustomerController {
    private CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping("/users")
    public String listUser(Model model, Principal principal) {
        if (principal == null) {
            return "login";
        }
        List<CustomerDto> customers = customerService.findAll();
        model.addAttribute("title", "users");
        model.addAttribute("users", customers);
        model.addAttribute("size", customers.size());
        return "users";
    }

//    public String blockUser(@PathVariable("id")Long id){
//        try{
//            customerService.blockById(id);
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//        return "redirect:/users";
//    }
    @GetMapping(value="/unblock-users/{id}")
    public String unblockUser(@PathVariable("id")Long id, RedirectAttributes attributes){
        try{
            customerService.unblockById(id);
            attributes.addFlashAttribute("success","Unblocked successfully");
        }catch (Exception e){
            e.printStackTrace();
            attributes.addFlashAttribute("error","Failed to Unblock");
        }
        return "redirect:/users";
    }
    @GetMapping(value = "/block-users/{id}")
    public String blockUser(@PathVariable("id")Long id,RedirectAttributes attributes){
        try{
            customerService.blockById(id);
            attributes.addFlashAttribute("success","Blocked successfully");
        }catch (Exception e){
            e.printStackTrace();
            attributes.addFlashAttribute("error","Failed to delete");
        }
        return "redirect:/users";
    }
}
