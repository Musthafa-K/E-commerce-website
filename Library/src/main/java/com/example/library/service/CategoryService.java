package com.example.library.service;

import com.example.library.dto.CategoryDto;
import com.example.library.model.Category;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
@Service
public interface CategoryService {
    Category save(Category category);

    Category update(Category category);
    List<Category>findAllByActivatedTrue();
    List<Category>findAll();
    Optional<Category>findById(Long id);

    void deleteById(Long id);
    void enableById(Long id);
    Long countAllCategories();

    List<CategoryDto>getCategoriesAndSize();
}
