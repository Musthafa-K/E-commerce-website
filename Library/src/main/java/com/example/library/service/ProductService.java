package com.example.library.service;

import com.example.library.dto.ProductDto;
import com.example.library.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.List;

public interface ProductService {

    List<Product>findAll();

    List<ProductDto> products();
    List<ProductDto>allProduct();
    Product save(List<MultipartFile>imageProduct, ProductDto product);

    Product update(List<MultipartFile>imageProduct,ProductDto productDto);

    void enableById(Long id);
    void deleteById(Long id);

    ProductDto getById(Long id);

    Product findById(Long id);

    Long countAllProducts();

    List<ProductDto>randomProduct();

    List<Object[]> getProductStatus();

    Page<ProductDto>searchProducts(int pageNo,String keyword);
    Page<ProductDto>getAllProducts(int pageNo);
    List<Object[]> getProductsStatsBetweenDates(Date startDate, Date endDate);


    Page<ProductDto>getAllProductsForCustomer(int pageNo);
    List<ProductDto> findAllByCategory(String category);

    List<ProductDto> filterHighProducts();

    List<ProductDto>filterLowerProducts();
    List<ProductDto>listViewProducts();

    List<ProductDto>findByCategoryId(Long  id);
    List<ProductDto>searchProducts(String keyword);

    Page<ProductDto> findAllByActivated(long id, int pageNo);

    Page<ProductDto> findAllByActivated(int pageNo, String sort);
}
