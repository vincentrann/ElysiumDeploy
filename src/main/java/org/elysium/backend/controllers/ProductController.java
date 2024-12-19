package org.elysium.backend.controllers;

import org.elysium.backend.models.Category;
import org.elysium.backend.models.Product;
import org.elysium.backend.repos.ProductRepository;
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

    @Autowired
    private ProductRepository productRepository;

    // Endpoint to get a product by ID
    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable String id) {
        Optional<Product> product = productService.getProductById(id);
        return product.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/title/{title}")
    public ResponseEntity<Product> getProductByTitle(@PathVariable String title) {
        // Find the product by title in the database
        Product product = productRepository.findByName(title)
                .orElseThrow(() -> new RuntimeException("Product not found with title: " + title));
        return ResponseEntity.ok(product);
    }

    @GetMapping("/filter/sort/price/asc")
    public List<Product> getProductsSortedByPriceAsc() {
        return productService.getProductsSortedByPriceAsc();
    }

    @GetMapping("/filter/sort/price/desc")
    public List<Product> getProductsSortedByPriceDesc() {
        return productService.getProductsSortedByPriceDesc();
    }

    @GetMapping("/filter/sort/name/asc")
    public List<Product> getProductsSortedByNameAsc() {
        return productService.getProductsSortedByNameAsc();
    }

    // Get products sorted by name (descending)
    @GetMapping("/filter/sort/name/desc")
    public List<Product> getProductsSortedByNameDesc() {
        return productService.getProductsSortedByNameDesc();
    }

    @GetMapping("/filter/category")
    public ResponseEntity<List<Product>> getProductsByCategory(@RequestParam String categoryName) {
        List<Product> products = productService.getProductsByCategoryName(categoryName);
        return ResponseEntity.ok(products);
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
