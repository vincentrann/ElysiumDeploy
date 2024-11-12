package org.elysium.backend.controllers;

import org.elysium.backend.models.Category;
import org.elysium.backend.models.Product;
import org.elysium.backend.services.CategoryService;
import org.elysium.backend.services.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    // Endpoint to get a product by ID
    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable String id) {
        Optional<Product> product = productService.getProductById(id);
        return product.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/products/sort/price/asc")
    public List<Product> getProductsSortedByPriceAsc() {
        return productService.getProductsSortedByPriceAsc();
    }

    @GetMapping("/products/sort/price/desc")
    public List<Product> getProductsSortedByPriceDesc() {
        return productService.getProductsSortedByPriceDesc();
    }

    @GetMapping("/products/sort/name/asc")
    public List<Product> getProductsSortedByNameAsc() {
        return productService.getProductsSortedByNameAsc();
    }

    // Get products sorted by name (descending)
    @GetMapping("/products/sort/name/desc")
    public List<Product> getProductsSortedByNameDesc() {
        return productService.getProductsSortedByNameDesc();
    }

    @GetMapping("/search/name")
    public List<Product> searchItemByName(@RequestParam String name) {
        return productService.searchItemByName(name);
    }

    @GetMapping("/search/brand")
    public List<Product> searchItemByBrand(@RequestParam String brand) {
        return productService.searchItemByBrand(brand);
    }

    @GetMapping("/search/category")
    public List<Product> searchCategoryByName(@RequestParam String categoryName) {
        // Fetch the category from the database by name
        Category category = CategoryService.getCategoryByName(categoryName)
                .orElseThrow(() -> new RuntimeException("Category with name " + categoryName + " not found"));

        // Pass the retrieved category to the service method
        return productService.searchCategory(category);
    }

    // Endpoint to get all products
    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts() {
        List<Product> products = productService.getAllProducts();
        return ResponseEntity.ok(products);
    }
}
