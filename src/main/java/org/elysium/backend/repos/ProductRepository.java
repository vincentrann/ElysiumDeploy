package org.elysium.backend.repos;

import org.elysium.backend.models.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, String> {
 // Custom query methods

 // Find products by category (corrected to use Integer for categoryId)

 @Query("SELECT p FROM Product p WHERE p.category.name = :categoryName")
 List<Product> findByCategoryName(@Param("categoryName") String categoryName);


 // Find products by brand
 List<Product> findByBrand(String brand);

 // Find products by name (case-insensitive)
 @Query("SELECT p FROM Product p WHERE p.name like %:name%")
 List<Product> findByNameIgnoreCase(String name);
 @Query("SELECT p FROM Product p WHERE LOWER(p.category.name) = LOWER(:categoryName)")
 List<Product> findByCategoryNameIgnoreCase(@Param("categoryName") String categoryName);

 // Find products with a price less than a certain value
 List<Product> findByPriceLessThan(double price);

 List<Product> findAllByOrderByNameAsc();
 @Query("SELECT p FROM Product p ORDER BY p.name DESC")
 List<Product> findAllByOrderByNameDesc();

 // Find products with a price between a range
 List<Product> findByPriceBetween(double minPrice, double maxPrice);

 // Find products with a discount
 List<Product> findByDiscountPriceGreaterThan(double discountPrice);

 // Find products by rating greater than or equal to a certain value
 List<Product> findByRatingGreaterThanEqual(double rating);

 Optional<Product> findById(String id);

 List<Product> findAllByOrderByPriceAsc(); // Ascending order
 List<Product> findAllByOrderByPriceDesc(); // Descending order



 // used for admins, directly finds the name of product
 Optional<Product> findByName(String name); // This will find the product by name


}
