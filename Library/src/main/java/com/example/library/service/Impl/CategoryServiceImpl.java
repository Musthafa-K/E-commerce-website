package com.example.library.service.Impl;

import com.example.library.dto.CategoryDto;
import com.example.library.model.Category;
import com.example.library.repository.CategoryRepository;
import com.example.library.service.CategoryService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service

public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;

    public CategoryServiceImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public Category save(Category category) {
        Category categorySave = new Category(category.getName());
        return categoryRepository.save(categorySave);
    }

    @Override
    public Category update(Category category) {
        Category categoryUpdate = categoryRepository.getById(category.getId());
        categoryUpdate.setName(category.getName());
        return categoryRepository.save(categoryUpdate);
    }

    @Override
    public List<Category> findAllByActivatedTrue() {
        return categoryRepository.findAllByActivatedTrue();
    }

    @Override
    public List<Category> findAll() {
        return categoryRepository.findAll();
    }

    @Override
    public Optional<Category> findById(Long id) {
        return categoryRepository.findById(id);
    }

    @Override
    public void deleteById(Long id) {
        Category category = categoryRepository.getById(id);
        category.setActivated(false);
        category.setDeleted(true);
        categoryRepository.save(category);
    }
@Override
    public void enableById(Long id){
        Category category=categoryRepository.getById(id);
        category.setActivated(true);
        category.setDeleted(false);
        categoryRepository.save(category);
}
@Override
    public List<CategoryDto> getCategoriesAndSize(){
        List<CategoryDto> categories=categoryRepository.getCategoriesAndSize();
        return categories;
}

    @Override
    public Long countAllCategories() {
        return categoryRepository.countAllCategories();
    }


}
