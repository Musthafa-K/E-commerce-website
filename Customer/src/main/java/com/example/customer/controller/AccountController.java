package com.example.customer.controller;

import com.example.library.dto.AddressDto;
import com.example.library.dto.CustomerDto;
import com.example.library.model.Address;
import com.example.library.model.Category;
import com.example.library.model.Customer;
import com.example.library.model.Order;
import com.example.library.repository.CustomerRepository;
import com.example.library.repository.OrderRepository;
import com.example.library.service.AddressService;
import com.example.library.service.CategoryService;
import com.example.library.service.CustomerService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Controller
public class AccountController {

    private final CustomerService customerService;

    private final CustomerRepository customerRepository;

    private final AddressService addressService;

    private final OrderRepository orderRepository;

    private final CategoryService categoryService;
    private BCryptPasswordEncoder passwordEncoder;

    public AccountController(BCryptPasswordEncoder passwordEncoder,CustomerService customerService, CustomerRepository customerRepository, AddressService addressService, OrderRepository orderRepository, CategoryService categoryService) {
        this.customerService = customerService;
        this.customerRepository = customerRepository;
        this.addressService = addressService;
        this.orderRepository = orderRepository;
        this.categoryService = categoryService;
        this.passwordEncoder=passwordEncoder;
    }



    @GetMapping("/account")
    public String accountHome(Model model, Principal principal, HttpServletRequest httpServletRequest) {
        if (principal == null) {
            return "redirect:/login";
        }
        String username = principal.getName();
        Customer customer = customerService.findByUsername(username);

        List<Order> orders = orderRepository.findByCustomer(customer);
        Collections.sort(orders, Collections.reverseOrder(Comparator.comparing(Order::getId)));
        model.addAttribute("orders", orders);

        model.addAttribute("customer", customer);
        model.addAttribute("addresses", customer.getAddresses());

        /*For the name*/
        HttpSession httpSession1 = httpServletRequest.getSession();
        String name = null;
        if (httpSession1 != null) {
            name = httpServletRequest.getRemoteUser();
        }
        model.addAttribute("name", name);

        return "page-account";
    }

    @GetMapping("/add-address")
    public String addAddress(Model model, Principal principal) {
        if (principal == null) {
            return "redirect:/login";
        }
        Customer customer = customerService.findByUsername(principal.getName());
        AddressDto addressDto = new AddressDto();
        List<Address> address = customer.getAddresses();
        model.addAttribute("addressList",address);
        model.addAttribute("addressDto", addressDto);
        model.addAttribute("size",address.size());
        return "address-content";
    }

    @PostMapping("/add-address")
    public String saveAddress(Model model, Principal principal, @ModelAttribute("addressDto") AddressDto addressDto, RedirectAttributes redirectAttributes) {
        if (principal == null) {
            return "redirect:/login";
        }
        String username = principal.getName();
        Address newAddress = new Address();
        newAddress = addressService.save(addressDto, username);
        model.addAttribute("address", newAddress);
        redirectAttributes.addFlashAttribute("message", "Address added");
        return "redirect:/account";
    }

    @GetMapping("/update-address/{id}")
    public String editAddress(@PathVariable("id") Long id, Model model, Principal principal, HttpServletRequest request) {
        if (principal == null) {
            return "redirect:/login";
        }
        System.out.println("update address get mapping");
        AddressDto addressDto = addressService.findById(id);
        model.addAttribute("addressDto", addressDto);
        return "update-address";
    }

    @PostMapping("/update-address/{id}")
    public String updateAddress(@PathVariable("id") Long id, HttpServletRequest request, Principal principal, @ModelAttribute("addressDto") AddressDto addressDto, RedirectAttributes redirectAttributes) {
        if (principal == null) {
            return "redirect:/login";
        }
        System.out.println("update address post mapping");
        Address newAddress = addressService.update(addressDto);
        redirectAttributes.addFlashAttribute("message", "Address updated");

            return "page-account";
        }
    

    @PostMapping("/add-address-checkout")
    public String AddAddress(@ModelAttribute("addressDto") AddressDto addressDto,
                             Model model, Principal principal, HttpServletRequest request) {

        addressService.save(addressDto, principal.getName());
        model.addAttribute("success", "Address Added");

        return "redirect:" + request.getHeader("Referer");
    }




    @RequestMapping(value = "/update-info", method = {RequestMethod.GET, RequestMethod.PUT})
    public String updateCustomer(
            @ModelAttribute("customer") Customer customer,
            Model model,
            RedirectAttributes redirectAttributes,
            Principal principal) {

        if (principal == null) {
            return "redirect:/login";
        }
        CustomerDto customerSaved = customerService.save(new CustomerDto());

        redirectAttributes.addFlashAttribute("customer", customerSaved);

        return "redirect:/account";
    }

    @GetMapping("/about")
    public String getAboutUs(Model model) {
        List<Category> categories = categoryService.findAllByActivatedTrue();
        model.addAttribute("categories", categories);
        return "about";
    }

    @GetMapping("/contact")
    public String getContactUs(Model model) {
        List<Category> categories = categoryService.findAllByActivatedTrue();
        model.addAttribute("categories", categories);
        return "contact";
    }

    @GetMapping("/check-account-details")
    public  String getUpdateAccount(Principal principal,Model model){
        if(principal==null){
            return "redirect:/login";
        }else{
            Customer customer=customerService.findByUsername(principal.getName());
            model.addAttribute("customer",customer);
            return "account-details";
        }
    }

    @PostMapping("/update-account")
    public String UpdateAccount(@ModelAttribute("customer")CustomerDto customerDto,
                                RedirectAttributes redirectAttributes,
                                Principal principal){

        if(principal==null){
            return "redirect:/login";
        }else{

            CustomerDto customerUpdated = customerService.updateAccount(customerDto,principal.getName());
            redirectAttributes.addFlashAttribute("customer",customerUpdated);
            redirectAttributes.addFlashAttribute("success","Updated Successfully");
            return "redirect:/account";

        }
    }
    @GetMapping("/enable-address/{id}")
    public String enableAddress(@PathVariable("id")long address_id,
                                RedirectAttributes redirectAttributes){

        addressService.enable(address_id);
        redirectAttributes.addFlashAttribute("success","Address enabled");

        return "redirect:/account";
    }

    @GetMapping("/disable-address/{id}")
    public String disableAddress(@PathVariable("id")long address_id,
                                 RedirectAttributes redirectAttributes){

        addressService.disable(address_id);
        redirectAttributes.addFlashAttribute("success","Address disabled");

        return "redirect:/account";
    }

        @PostMapping("/change-password")
    public String changePass(@RequestParam("oldPassword") String oldPassword,
                             @RequestParam("newPassword") String newPassword,
                             @RequestParam("repeatNewPassword") String repeatPassword,
                             RedirectAttributes attributes,
                             Principal principal) {
        if (principal == null) {
            return "redirect:/login";
        } else {

            Customer customer = customerService.findByUsername(principal.getName());
            if (passwordEncoder.matches(oldPassword, customer.getPassword())
                    && !passwordEncoder.matches(newPassword, oldPassword)
                    && !passwordEncoder.matches(newPassword, customer.getPassword())
                    && repeatPassword.equals(newPassword) && newPassword.length() >= 3) {

                customer.setPassword(passwordEncoder.encode(newPassword));
                customerService.changePass(customer);
                attributes.addFlashAttribute("success", "Your password has been changed successfully!");
                return "redirect:/account";
            } else {

                attributes.addFlashAttribute("message", "Entered Password Does Not Match");
                return "redirect:/account";
            }
        }
    }





}
