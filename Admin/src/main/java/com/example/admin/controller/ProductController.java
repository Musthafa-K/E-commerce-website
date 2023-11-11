package com.example.admin.controller;

import com.example.library.dto.ProductDto;
import com.example.library.model.Category;
import com.example.library.service.CategoryService;
import com.example.library.service.ProductService;
import org.springframework.data.domain.Page;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.List;

@Controller
public class ProductController {
    private final ProductService productService;
    private final CategoryService categoryService;

   public ProductController(ProductService productService,CategoryService categoryService){
       this.productService=productService;
       this.categoryService=categoryService;
   }
   @GetMapping("/products")
    public String products(Model model){
       List<ProductDto>products=productService.allProduct();
       model.addAttribute("products",products);
       model.addAttribute("size",products.size());
       Authentication authentication= SecurityContextHolder.getContext().getAuthentication();
       if(authentication==null|| authentication instanceof AnonymousAuthenticationToken){
           return "redirect:/login";
       }
       return "products";
   }
   @GetMapping("/products/{pageNo}")
    public String allProducts(@PathVariable("pageNo") int pageNo, Model model, Principal principal){
       if(principal==null){
           return "redirect:/login";
       }
       Page<ProductDto> products=productService.getAllProducts(pageNo);
       model.addAttribute("title","Manage Products");
       model.addAttribute("size",products.getSize());
       model.addAttribute("products",products);
       model.addAttribute("currentPage",pageNo);
       model.addAttribute("totalPages",products.getTotalPages());
       return "products";
   }
   @GetMapping("/search-productcs/{pageNo}")
    public String searchProduct(@PathVariable("pageNo") int pageNo,
                                @RequestParam(value = "keyword")String keyword,
                                Model model){
       Page<ProductDto> products=productService.searchProducts(pageNo, keyword);
        model.addAttribute("title","Result Search Products");
        model.addAttribute("size",products.getSize());
        model.addAttribute("products",products);
        model.addAttribute("currentPage",pageNo);
        model.addAttribute("totalPages",products.getTotalPages());
        return "product-result";
   }
   @GetMapping("/add-product")
    public String addProductPage(Model model){
       model.addAttribute("title","Add Product");
       List<Category>categories=categoryService.findAllByActivatedTrue();
       model.addAttribute("categories",categories);
       model.addAttribute("productDto",new ProductDto());
       Authentication authentication=SecurityContextHolder.getContext().getAuthentication();
       if(authentication == null || authentication instanceof AnonymousAuthenticationToken){
           return "redirect:/login";
       }
       return "add-product";
   }
   @PostMapping("/save-product")
    public String saveProduct(@ModelAttribute("productDto") ProductDto product,
                              @RequestParam("imageProduct")List<MultipartFile> imageProduct,
                              RedirectAttributes redirectAttributes){
       try {
           productService.save(imageProduct,product);
           redirectAttributes.addFlashAttribute("success","Add new product successfuly!");
       }catch (Exception e){
           e.printStackTrace();
           redirectAttributes.addFlashAttribute("error","Failed to add new product!");
       }
       return "redirect:/products/0";
   }
    @GetMapping ("/update-product/{id}")
    public String updateProductForm(@PathVariable("id")Long id,Model model){
       Authentication authentication=SecurityContextHolder.getContext().getAuthentication();
       if(authentication==null||authentication instanceof AnonymousAuthenticationToken){
           return  "redirect:/login";
       }
       List<Category> categories=categoryService.findAllByActivatedTrue();
       ProductDto productDto=productService.getById(id);
       model.addAttribute("title","Add Product");
       model.addAttribute("categories",categories);
       model.addAttribute("productDto",productDto);
       return "update-product";

   }
   @PostMapping("/update-product/{id}")
    public String updateProduct(@ModelAttribute("productDto")ProductDto productDto,
                                @RequestParam("imageProduct") List<MultipartFile> imageProduct,
                                RedirectAttributes redirectAttributes){
       try{
           productService.update(imageProduct,productDto);
           redirectAttributes.addFlashAttribute("success","Update successfully!");
       }catch(Exception e){
           e.printStackTrace();
           redirectAttributes.addFlashAttribute("error","Error server, Please try again !");

       }
       return "redirect:/products";
   }
    @RequestMapping(value = "/enable-product/{id}", method = {RequestMethod.GET})
    public String enabledProduct(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            productService.enableById(id);
            redirectAttributes.addFlashAttribute("success", "Enabled successfully!");
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Enabled failed!");
        }
        return "redirect:/products/0";
    }

    @RequestMapping(value = "/delete-product/{id}", method = {RequestMethod.GET})
    public String deletedProduct(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            productService.deleteById(id);
            redirectAttributes.addFlashAttribute("success", "Deleted successfully!");
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Deleted failed!");
        }
        return "redirect:/products/0";
    }


}
