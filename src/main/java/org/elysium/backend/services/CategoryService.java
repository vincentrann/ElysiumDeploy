package org.elysium.backend.services;

import org.elysium.backend.models.Category;
import org.elysium.backend.repos.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CategoryService {

    @Autowired
    private static CategoryRepository categoryRepository;

    public Optional<Category> getCategoryById(String id) {
        return categoryRepository.findById(id);
    }

    public static Optional<Category> getCategoryByName(String name) {
        return categoryRepository.findByNameIgnoreCase(name);
    }
}
