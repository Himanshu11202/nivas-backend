package com.society.service;

import com.society.entity.Product;
import com.society.entity.User;
import com.society.repository.ProductRepository;
import com.society.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    // Create new product
    @Transactional
    public Product createProduct(Product product, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        product.setUser(user);
        product.setIsActive(true);
        return productRepository.save(product);
    }

    // Get all active products
    public List<Product> getAllActiveProducts() {
        return productRepository.findByIsActiveTrueOrderByCreatedAtDesc();
    }

    // Get product by ID
    public Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
    }

    // Get user's products
    public List<Product> getUserProducts(Long userId) {
        return productRepository.findByUserIdAndIsActiveTrueOrderByCreatedAtDesc(userId);
    }

    // Update product
    @Transactional
    public Product updateProduct(Long id, Product updatedProduct, Long userId) {
        Product product = getProductById(id);
        
        // Check if user owns this product
        if (!product.getUser().getId().equals(userId)) {
            throw new RuntimeException("You can only update your own products");
        }
        
        product.setTitle(updatedProduct.getTitle());
        product.setDescription(updatedProduct.getDescription());
        product.setPrice(updatedProduct.getPrice());
        product.setCategory(updatedProduct.getCategory());
        product.setCondition(updatedProduct.getCondition());
        
        // Update images if provided
        if (updatedProduct.getImages() != null && !updatedProduct.getImages().isEmpty()) {
            product.setImages(updatedProduct.getImages());
        }
        
        return productRepository.save(product);
    }

    // Soft delete product (deactivate)
    @Transactional
    public void deleteProduct(Long id, Long userId) {
        Product product = getProductById(id);
        
        // Check if user owns this product or is admin
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        boolean isAdmin = user.getRole() == User.Role.ADMIN || user.getRole() == User.Role.SUPER_ADMIN;
        
        if (!product.getUser().getId().equals(userId) && !isAdmin) {
            throw new RuntimeException("You can only delete your own products");
        }
        
        product.setIsActive(false);
        productRepository.save(product);
    }

    // Search products
    public List<Product> searchProducts(String query) {
        return productRepository.searchByTitle(query);
    }

    // Filter by category
    public List<Product> getProductsByCategory(String category) {
        return productRepository.findByCategoryAndIsActiveTrueOrderByCreatedAtDesc(category);
    }

    // Filter by price range
    public List<Product> getProductsByPriceRange(BigDecimal minPrice, BigDecimal maxPrice) {
        return productRepository.findByPriceRange(minPrice, maxPrice);
    }

    // Admin: Get all products including inactive
    public List<Product> getAllProductsForAdmin() {
        return productRepository.findAllOrderByCreatedAtDesc();
    }

    // Get product with seller details
    public Map<String, Object> getProductWithSellerDetails(Long productId) {
        Product product = getProductById(productId);
        User seller = product.getUser();
        
        Map<String, Object> result = new HashMap<>();
        result.put("id", product.getId());
        result.put("title", product.getTitle());
        result.put("description", product.getDescription());
        result.put("price", product.getPrice());
        result.put("category", product.getCategory());
        result.put("images", product.getImages());
        result.put("condition", product.getCondition());
        result.put("createdAt", product.getCreatedAt());
        
        // Seller details (with contact)
        Map<String, Object> sellerInfo = new HashMap<>();
        sellerInfo.put("id", seller.getId());
        sellerInfo.put("name", seller.getName());
        sellerInfo.put("phoneNumber", seller.getPhoneNumber());
        sellerInfo.put("flatNumber", seller.getFlatNumber());
        
        result.put("seller", sellerInfo);
        
        return result;
    }

    // Get products with seller info (for listing)
    public List<Map<String, Object>> getAllProductsWithSellerInfo() {
        List<Product> products = getAllActiveProducts();
        
        return products.stream().map(p -> {
            Map<String, Object> product = new HashMap<>();
            product.put("id", p.getId());
            product.put("title", p.getTitle());
            product.put("description", p.getDescription());
            product.put("price", p.getPrice());
            product.put("category", p.getCategory());
            product.put("images", p.getImages());
            product.put("condition", p.getCondition());
            product.put("createdAt", p.getCreatedAt());
            product.put("sellerName", p.getUser().getName());
            product.put("sellerFlat", p.getUser().getFlatNumber());
            return product;
        }).collect(Collectors.toList());
    }

    // Get product categories
    public List<String> getCategories() {
        return List.of("Furniture", "Electronics", "Vehicles", "Home Appliances", 
                      "Books", "Clothing", "Sports", "Others");
    }
}
