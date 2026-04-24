package com.society.controller;

import com.society.entity.Product;
import com.society.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/marketplace")
@CrossOrigin(origins = "http://localhost:3000")
public class ProductController {

    @Autowired
    private ProductService productService;

    // ============ RESIDENT APIs ============

    // Get all active products with seller info
    @GetMapping("/products")
    @PreAuthorize("hasRole('RESIDENT') or hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<List<Map<String, Object>>> getAllProducts() {
        List<Map<String, Object>> products = productService.getAllProductsWithSellerInfo();
        return ResponseEntity.ok(products);
    }

    // Get product by ID with full details including seller contact
    @GetMapping("/products/{id}")
    @PreAuthorize("hasRole('RESIDENT') or hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<Map<String, Object>> getProductById(@PathVariable Long id) {
        try {
            Map<String, Object> product = productService.getProductWithSellerDetails(id);
            return ResponseEntity.ok(product);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Create new product
    @PostMapping("/products")
    @PreAuthorize("hasRole('RESIDENT') or hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<?> createProduct(@RequestBody Product product, @RequestParam Long userId) {
        try {
            Product savedProduct = productService.createProduct(product, userId);
            return ResponseEntity.ok(savedProduct);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // Get user's own products
    @GetMapping("/my-products")
    @PreAuthorize("hasRole('RESIDENT') or hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<List<Product>> getMyProducts(@RequestParam Long userId) {
        List<Product> products = productService.getUserProducts(userId);
        return ResponseEntity.ok(products);
    }

    // Update user's product
    @PutMapping("/products/{id}")
    @PreAuthorize("hasRole('RESIDENT') or hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<?> updateProduct(
            @PathVariable Long id,
            @RequestBody Product product,
            @RequestParam Long userId) {
        try {
            Product updated = productService.updateProduct(id, product, userId);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // Delete (deactivate) product
    @DeleteMapping("/products/{id}")
    @PreAuthorize("hasRole('RESIDENT') or hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<?> deleteProduct(@PathVariable Long id, @RequestParam Long userId) {
        try {
            productService.deleteProduct(id, userId);
            return ResponseEntity.ok(Map.of("message", "Product deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // Search products
    @GetMapping("/products/search")
    @PreAuthorize("hasRole('RESIDENT') or hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<List<Product>> searchProducts(@RequestParam String query) {
        List<Product> products = productService.searchProducts(query);
        return ResponseEntity.ok(products);
    }

    // Filter by category
    @GetMapping("/products/category/{category}")
    @PreAuthorize("hasRole('RESIDENT') or hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<List<Product>> getProductsByCategory(@PathVariable String category) {
        List<Product> products = productService.getProductsByCategory(category);
        return ResponseEntity.ok(products);
    }

    // Filter by price range
    @GetMapping("/products/price-range")
    @PreAuthorize("hasRole('RESIDENT') or hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<List<Product>> getProductsByPriceRange(
            @RequestParam BigDecimal min,
            @RequestParam BigDecimal max) {
        List<Product> products = productService.getProductsByPriceRange(min, max);
        return ResponseEntity.ok(products);
    }

    // Get categories list
    @GetMapping("/categories")
    @PreAuthorize("hasRole('RESIDENT') or hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<List<String>> getCategories() {
        return ResponseEntity.ok(productService.getCategories());
    }

    // ============ ADMIN APIs ============

    // Get all products (admin only)
    @GetMapping("/admin/products")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<List<Product>> getAllProductsForAdmin() {
        List<Product> products = productService.getAllProductsForAdmin();
        return ResponseEntity.ok(products);
    }

    // Admin delete product
    @DeleteMapping("/admin/products/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<?> adminDeleteProduct(@PathVariable Long id, @RequestParam Long adminId) {
        try {
            productService.deleteProduct(id, adminId);
            return ResponseEntity.ok(Map.of("message", "Product deleted by admin"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
