package com.damaru.store.controller;

import com.damaru.store.entity.Category;
import com.damaru.store.entity.CategoryRepository;
import com.damaru.store.entity.Item;
import com.damaru.store.entity.ItemRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class WebController {
	
	public static final Logger log = LoggerFactory.getLogger(WebController.class);

    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    ItemRepository itemRepository;

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

    @GetMapping("items")
    public List<Item> getItems() {
    	List<Item> ret = null;
    	log.info("Getting items....");
    
    	try {
    		ret = fetchThem();
    	} catch (Exception e) {
    		log.error("Oops! got an exception!", e);
    	}
    	
       return ret;
    }
    
    private List<Item> fetchThem() throws Exception {
    	List<Item> ret = null;
    	ret = (List<Item>) itemRepository.findAll();
    	
    	if (ret.size() == 2) {
    		//throw new Exception("This is bad, there were 2 items!");
    	}
    	return ret;
    }


}
