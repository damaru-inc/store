package com.damaru.store.controller;

import com.damaru.store.entity.Category;
import com.damaru.store.entity.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class WebController {

    @Autowired
    CategoryRepository categoryRepository;

    @GetMapping("categories")
    public List<Category> getCategories() {
        List<Category> ret = (List<Category>) categoryRepository.findAll();
        return ret;
    }

    @PostMapping("category")
    public Category createCategory(@RequestBody Category category) {
        final Category newCategory = categoryRepository.save(category);
        return newCategory;
    }

    // TODO: 1. Create the Item class. Looks like the Category class, but it has one more field: Category category.
    // 2. Create the ItemRepsitory interface, just like CategoryInterface but references Item, not Category.
    // 3. Uncomment the following and test it.

//    @GetMapping("items")
//    public List<Item> getItems() {
//        List<Item> ret = (List<Item>) itemRepository.findAll();
//        return ret;
//    }


}
