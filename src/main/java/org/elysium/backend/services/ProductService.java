package org.elysium.backend.services;

import org.elysium.backend.models.Category;
import org.elysium.backend.models.Product;
import org.elysium.backend.repos.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    public Product insertProduct(Product product) {
        // Check if a product with the given ID already exists
        Optional<Product> existingProduct = productRepository.findById(product.getId());
        if (existingProduct.isPresent()) {
            throw new RuntimeException("Product with this ID already exists");
        }

        // Generate a random ID if it's not provided
        if (product.getId() == null || product.getId().isEmpty()) {
            product = new Product.ProductBuilder(
                    Product.generateRandomId(),
                    product.getName(),
                    product.getPrice()
            )
                    .setDescription(product.getDescription())
                    .setStockQuantity(product.getStockQuantity())
                    .setCategory(product.getCategory())
                    .setImageUrl(product.getImageUrl())
                    .setDiscountPrice(product.getDiscountPrice())
                    .setWeight(product.getWeight())
                    .setDimensions(product.getDimensions())
                    .setBrand(product.getBrand())
                    .setRating(product.getRating())
                    .setNumberOfReviews(product.getNumberOfReviews())
                    .build();
        }

        // Save the new product to the database
        return productRepository.save(product);
    }

    public List<Product> searchItemByName(String name)
    {
        List<Product> products =productRepository.findByNameIgnoreCase(name);
        if(products.isEmpty())
        {
            throw new RuntimeException("Product with this name does not exist" + name);
        }
        return products;
    }

    public List<Product> searchItemByBrand(String brand)
    {
        List<Product> products =productRepository.findByBrand(brand);
        if(products.isEmpty())
        {
            throw new RuntimeException("Product with this name does not exist" + brand);
        }
        return products;
    }

    public List<Product> searchCategory(Category category)
    {
        List<Product> products =productRepository.findByCategoryName(category.getName());
        if(products.isEmpty())
        {
            throw new RuntimeException("Product with this name does not exist" + category.getName());
        }
        return products;
    }

    public List<Product> getProductsSortedByPriceAsc() {
        return productRepository.findAllByOrderByPriceAsc();
    }

    public List<Product> getProductsSortedByPriceDesc() {
        return productRepository.findAllByOrderByPriceDesc();
    }



    public Optional<Product> getProductById(String id) {
        return productRepository.findById(id);
    }

    // Retrieve all products
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

}