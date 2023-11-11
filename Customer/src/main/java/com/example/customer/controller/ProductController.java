package com.example.customer.controller;

import com.example.library.dto.BannerDto;
import com.example.library.dto.ProductDto;
import com.example.library.model.Category;
import com.example.library.model.Customer;
import com.example.library.service.BannerService;
import com.example.library.service.CategoryService;
import com.example.library.service.CustomerService;
import com.example.library.service.ProductService;
import jakarta.servlet.http.HttpSession;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.List;

@Controller
public class ProductController {
    private CategoryService categoryService;

    private ProductService productService;

    private CustomerService customerService;

    private BannerService bannerService;

    public ProductController(CategoryService categoryService, ProductService productService,
                             CustomerService customerService,BannerService bannerService) {
        this.bannerService=bannerService;
        this.customerService=customerService;
        this.categoryService = categoryService;
        this.productService = productService;
    }

    @GetMapping("/index")
    public String getHomePage(Model model, Principal principal, HttpSession session){
        if (principal != null) {
            Customer customer = customerService.findByUsername(principal.getName());
            session.setAttribute("userLoggedIn",true);
            session.setAttribute("username", customer.getFirstName() + " " + customer.getLastName());
        }
        List<BannerDto> bannerDtoList=bannerService.getAllBanners();
        List<Category> categories = categoryService.findAllByActivatedTrue();
        List<ProductDto> products=productService.allProduct();
        model.addAttribute("categories",categories);
        model.addAttribute("banners",bannerDtoList);
        model.addAttribute("products",products);


        return "home";
    }
            @GetMapping("/shoplist")
           public String productList(Model model){
               model.addAttribute("page","Products");
               model.addAttribute("title","Menu");
                List<ProductDto> products=productService.products();
                model.addAttribute("products",products);
                return "shoplist";

            }
            @GetMapping("/product-detail/{id}")
            public String productDetail(@PathVariable("id")Long id,Model model){
             ProductDto product=productService.getById(id);
             model.addAttribute("product",product);
             model.addAttribute("title","Product Detail");
             model.addAttribute("page","Product Detail");
             model.addAttribute("productDetail",product);
             return "product-detail";
            }
          @GetMapping("/menu")
          public String Menu(Model model) {
          model.addAttribute("page", "Products");
          model.addAttribute("title", "Menu");
          List<ProductDto> products = productService.products();
          model.addAttribute("products", products);
          return "home";

    }
    @GetMapping("/products-list")
    public String getShopPage(@RequestParam(name = "id",required = false,defaultValue = "0") long id, Model model,
                              @RequestParam(name = "pageNo",required = false,defaultValue = "0")int pageNo,
                              @RequestParam(name = "sort",required = false,defaultValue = "")String sort){
        List<Category> categories = categoryService.findAllByActivatedTrue();

        Page<ProductDto> products;
        if(id==0) {
            products = productService.findAllByActivated(pageNo,sort);
            model.addAttribute("sort",sort);
        }else{
            products = productService.findAllByActivated(id,pageNo);
        }
        long totalProducts = products.getTotalElements();
        model.addAttribute("totalProducts", totalProducts);
        model.addAttribute("products",products);
        model.addAttribute("currentPage", pageNo);
        model.addAttribute("totalPages", products.getTotalPages());
        model.addAttribute("categories",categories);


        return "shop";
    }
}
