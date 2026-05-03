package com.society.config;

import com.society.entity.Product;
import com.society.entity.User;
import com.society.repository.ProductRepository;
import com.society.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

@Component
public class ProductDataSeeder implements CommandLineRunner {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public void run(String... args) throws Exception {
        // Find a resident user to assign products to
        List<User> residents = userRepository.findByRole(User.Role.RESIDENT);
        if (residents.isEmpty()) {
            System.out.println("No residents found, cannot seed products...");
            return;
        }

        User seller = residents.get(0);
        System.out.println("Seeding dummy products for user: " + seller.getName());

        // Only seed products if none exist to avoid foreign key constraint issues
        long existingCount = productRepository.count();
        if (existingCount > 0) {
            System.out.println("Products already exist (" + existingCount + "), skipping seeding...");
            return;
        }

        // Create dummy products with Unsplash image URLs
        List<Product> dummyProducts = Arrays.asList(
            createProduct(
                "Study Table with Drawer",
                "Wooden study table in excellent condition. Has 2 drawers for storage. Perfect for students or work from home.",
                new BigDecimal("2500"),
                "Furniture",
                "USED",
                Arrays.asList(
                    "https://images.unsplash.com/photo-1518455027359-f3f8164ba6bd?w=800",
                    "https://images.unsplash.com/photo-1493934558415-9d19f0b2b4d2?w=800"
                ),
                seller
            ),
            createProduct(
                "Sony Bravia 32-inch LED TV",
                "Sony LED TV in perfect working condition. 2 years old. Remote included. Full HD display.",
                new BigDecimal("12000"),
                "Electronics",
                "USED",
                Arrays.asList(
                    "https://images.unsplash.com/photo-1593359677879-a4bb92f829d1?w=800",
                    "https://images.unsplash.com/photo-1509281373149-e957c6296406?w=800"
                ),
                seller
            ),
            createProduct(
                "Samsung Refrigerator 250L",
                "Double door refrigerator. 3 years old but well maintained. Frost free. Energy efficient.",
                new BigDecimal("18000"),
                "Home Appliances",
                "USED",
                Arrays.asList(
                    "https://images.unsplash.com/photo-1571175443880-49e1d58b794a?w=800",
                    "https://images.unsplash.com/photo-1584568694244-14fbdf83bd30?w=800"
                ),
                seller
            ),
            createProduct(
                "Dining Table Set - 4 Chairs",
                "4-seater dining table with chairs. Teak wood finish. Minor scratches but sturdy.",
                new BigDecimal("8000"),
                "Furniture",
                "USED",
                Arrays.asList(
                    "https://images.unsplash.com/photo-1577140917170-285929fb55b7?w=800",
                    "https://images.unsplash.com/photo-1506439773649-6e0eb8cfb237?w=800"
                ),
                seller
            ),
            createProduct(
                "Honda Activa Scooter",
                "2018 model, well maintained. New battery installed last month. All papers clear.",
                new BigDecimal("35000"),
                "Vehicles",
                "USED",
                Arrays.asList(
                    "https://images.unsplash.com/photo-1558618666-fcd25c85cd64?w=800",
                    "https://images.unsplash.com/photo-1449426468159-d96dbf08f19f?w=800"
                ),
                seller
            ),
            createProduct(
                "Engineering Books - Complete Set",
                "First year engineering books. All in good condition. Includes Maths, Physics, Chemistry, Programming.",
                new BigDecimal("1500"),
                "Books",
                "USED",
                Arrays.asList(
                    "https://images.unsplash.com/photo-1495446815901-a7297e633e8d?w=800",
                    "https://images.unsplash.com/photo-1512820790803-83ca734da794?w=800"
                ),
                seller
            ),
            createProduct(
                "Whirlpool Washing Machine",
                "Fully automatic washing machine. 6.5 kg capacity. 2 years old. Working perfectly.",
                new BigDecimal("10000"),
                "Home Appliances",
                "USED",
                Arrays.asList(
                    "https://images.unsplash.com/photo-1626806787461-102c1bfaaea1?w=800",
                    "https://images.unsplash.com/photo-1584622650111-993a426fbf0a?w=800"
                ),
                seller
            ),
            createProduct(
                "Office Chair - Ergonomic",
                "Ergonomic office chair with lumbar support. Adjustable height. Mesh back.",
                new BigDecimal("4000"),
                "Furniture",
                "LIKE_NEW",
                Arrays.asList(
                    "https://images.unsplash.com/photo-1505843490538-5133c6c7d0e1?w=800",
                    "https://images.unsplash.com/photo-1580480055273-228ff5388ef8?w=800"
                ),
                seller
            ),
            createProduct(
                "Cricket Kit - Full Set",
                "Full cricket kit including bat, pads, gloves, helmet, and kit bag. Used for 1 season only.",
                new BigDecimal("3500"),
                "Sports",
                "LIKE_NEW",
                Arrays.asList(
                    "https://images.unsplash.com/photo-1531415074968-036ba1b575da?w=800",
                    "https://images.unsplash.com/photo-1593766821405-f5fe92537622?w=800"
                ),
                seller
            ),
            createProduct(
                "Men's Formal Shirts - Pack of 5",
                "5 formal shirts from Peter England and Van Heusen. Size 40. All in good condition.",
                new BigDecimal("2000"),
                "Clothing",
                "USED",
                Arrays.asList(
                    "https://images.unsplash.com/photo-1596755094514-f87e34085b2c?w=800",
                    "https://images.unsplash.com/photo-1602810318383-e386cc2a3ccf?w=800"
                ),
                seller
            ),
            createProduct(
                "iPhone 12 - 128GB",
                "iPhone 12 in excellent condition. No scratches. Battery health 85%. Box and charger included.",
                new BigDecimal("35000"),
                "Electronics",
                "LIKE_NEW",
                Arrays.asList(
                    "https://images.unsplash.com/photo-1605236453806-6ff36851218e?w=800",
                    "https://images.unsplash.com/photo-1510557880182-3d4d3cba35a5?w=800"
                ),
                seller
            ),
            createProduct(
                "Microwave Oven - LG",
                "20L microwave oven. Grill function available. 1.5 years old. Perfect for small families.",
                new BigDecimal("4500"),
                "Home Appliances",
                "USED",
                Arrays.asList(
                    "https://images.unsplash.com/photo-1585659722983-3a675dabf23d?w=800",
                    "https://images.unsplash.com/photo-1574269909862-7e1d70bb8078?w=800"
                ),
                seller
            )
        );

        // Save all products
        productRepository.saveAll(dummyProducts);
        System.out.println("Successfully seeded " + dummyProducts.size() + " dummy products!");
    }

    private Product createProduct(String title, String description, BigDecimal price,
                                   String category, String condition, List<String> images, User user) {
        Product product = new Product();
        product.setTitle(title);
        product.setDescription(description);
        product.setPrice(price);
        product.setCategory(category);
        product.setCondition(condition);
        product.setImages(images);
        product.setUser(user);
        product.setIsActive(true);
        return product;
    }
}
