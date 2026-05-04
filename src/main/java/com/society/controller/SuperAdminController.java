package com.society.controller;

import com.society.entity.Society;
import com.society.entity.User;
import com.society.repository.SocietyRepository;
import com.society.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

@RestController
@RequestMapping("/api/super-admin")
public class SuperAdminController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SocietyRepository societyRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Temporary API to create Super Admin
     * Can only be used ONCE - disabled after first Super Admin is created
     */
    @PostMapping("/create")
    public ResponseEntity<?> createSuperAdmin(@RequestBody Map<String, String> request) {
        try {
            // Check if SUPER_ADMIN already exists
            List<User> existingSuperAdmins = userRepository.findByRole(User.Role.SUPER_ADMIN);
            
            if (!existingSuperAdmins.isEmpty()) {
                return ResponseEntity.badRequest().body(
                    Map.of("error", "Super Admin already created", 
                           "message", "Only one Super Admin is allowed. This API is now disabled.")
                );
            }

            // Extract fields from request
            String name = request.get("name");
            String email = request.get("email");
            String password = request.get("password");

            // Validate required fields
            if (name == null || name.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Name is required"));
            }
            if (email == null || email.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Email is required"));
            }
            if (password == null || password.length() < 6) {
                return ResponseEntity.badRequest().body(Map.of("error", "Password must be at least 6 characters"));
            }

            // Check if email already exists
            Optional<User> existingUser = userRepository.findByEmail(email);
            if (existingUser.isPresent()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Email already exists"));
            }

            // Hash password using BCrypt
            String hashedPassword = passwordEncoder.encode(password);

            // Create Super Admin user
            User superAdmin = new User();
            superAdmin.setName(name);
            superAdmin.setEmail(email);
            superAdmin.setPassword(hashedPassword);
            superAdmin.setRole(User.Role.SUPER_ADMIN);
            superAdmin.setStatus(User.UserStatus.ACTIVE);

            // Save to database
            userRepository.save(superAdmin);

            return ResponseEntity.ok(Map.of(
                "message", "Super Admin created successfully",
                "email", email,
                "role", "SUPER_ADMIN",
                "note", "This API is now disabled. Please use /api/super-admin/login to authenticate."
            ));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of(
                "error", "Failed to create Super Admin",
                "message", e.getMessage(),
                "exception", e.getClass().getName()
            ));
        }
    }

    @PostMapping("/create-resident")
    public ResponseEntity<?> createResident(@RequestBody Map<String, String> request) {
        try {
            String name = request.get("name");
            String email = request.get("email");
            String password = request.get("password");
            String flatNumber = request.get("flatNumber");

            if (name == null || email == null || password == null || flatNumber == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "All fields required"));
            }

            // Check if email already exists
            Optional<User> existingUser = userRepository.findByEmail(email);
            if (existingUser.isPresent()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Email already exists"));
            }

            // Create Resident user
            User resident = new User();
            resident.setName(name);
            resident.setEmail(email);
            resident.setPassword(passwordEncoder.encode(password));
            resident.setRole(User.Role.RESIDENT);
            resident.setFlatNumber(flatNumber);
            resident.setStatus(User.UserStatus.ACTIVE);

            userRepository.save(resident);

            return ResponseEntity.ok(Map.of(
                "message", "Resident created successfully",
                "email", email,
                "role", "RESIDENT"
            ));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/marketplace-test")
    public ResponseEntity<?> testMarketplace() {
        try {
            long count = userRepository.count();
            List<User> residents = userRepository.findByRole(User.Role.RESIDENT);
            return ResponseEntity.ok(Map.of(
                "total_users", count,
                "residents_count", residents.size(),
                "message", "Marketplace data check"
            ));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginSuperAdmin(@RequestBody Map<String, String> request) {
        try {
            System.out.println("Login API called");
            String email = request.get("email");
            String password = request.get("password");
            System.out.println("Email: " + email);

            if (email == null || email.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Email is required"));
            }
            if (password == null || password.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Password is required"));
            }

            Optional<User> userOpt = userRepository.findByEmail(email);
            System.out.println("User found: " + userOpt.isPresent());

            if (userOpt.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Invalid email or password"));
            }

            User user = userOpt.get();
            System.out.println("User role: " + user.getRole());

            if (user.getRole() != User.Role.SUPER_ADMIN) {
                return ResponseEntity.badRequest().body(Map.of("error", "Not a Super Admin account"));
            }

            boolean matches = passwordEncoder.matches(password, user.getPassword());
            System.out.println("Password matches: " + matches);

            if (!matches) {
                return ResponseEntity.badRequest().body(Map.of("error", "Invalid email or password"));
            }

            if (user.getStatus() != User.UserStatus.ACTIVE) {
                return ResponseEntity.badRequest().body(Map.of("error", "Account is not active"));
            }

            return ResponseEntity.ok(Map.of(
                "message", "Login successful",
                "user", Map.of(
                    "id", user.getId(),
                    "name", user.getName(),
                    "email", user.getEmail(),
                    "role", user.getRole().name(),
                    "status", user.getStatus().name()
                )
            ));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of(
                "error", "Login failed",
                "message", e.getMessage(),
                "exception", e.getClass().getName()
            ));
        }
    }

    // ============ SOCIETY MANAGEMENT ENDPOINTS ============

    // Create Society
    @PostMapping("/societies")
    public ResponseEntity<?> createSociety(@RequestBody Map<String, String> request) {
        try {
            String name = request.get("name");
            String location = request.get("location");

            // Admin details
            String adminName = request.get("adminName");
            String adminEmail = request.get("adminEmail");
            String adminPassword = request.get("adminPassword");
            String adminPhone = request.get("adminPhone");

            if (name == null || name.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Society name is required"));
            }

            // Generate unique society code
            String societyCode;
            do {
                societyCode = "SOC" + (10000 + new Random().nextInt(90000));
            } while (societyRepository.existsBySocietyCode(societyCode));

            Society society = new Society(societyCode, name, location);
            Society savedSociety = societyRepository.save(society);

            // Create Society Admin if admin details provided
            User admin = null;
            if (adminName != null && adminEmail != null && adminPassword != null) {
                // Check if email already exists
                if (userRepository.existsByEmail(adminEmail)) {
                    return ResponseEntity.badRequest().body(Map.of("error", "Admin email already exists"));
                }

                admin = new User();
                admin.setName(adminName);
                admin.setEmail(adminEmail);
                admin.setPassword(passwordEncoder.encode(adminPassword));
                admin.setRole(User.Role.SOCIETY_ADMIN);
                admin.setSocietyId(savedSociety.getId());
                admin.setPhoneNumber(adminPhone);
                admin.setStatus(User.UserStatus.ACTIVE);
                userRepository.save(admin);
            }

            return ResponseEntity.ok(Map.of(
                "message", "Society created successfully",
                "societyCode", savedSociety.getSocietyCode(),
                "society", savedSociety,
                "admin", admin
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Error creating society: " + e.getMessage()));
        }
    }

    // Get All Societies
    @GetMapping("/societies")
    public ResponseEntity<List<Society>> getAllSocieties() {
        List<Society> societies = societyRepository.findAll();
        return ResponseEntity.ok(societies);
    }

    // Search Societies by Name
    @GetMapping("/societies/search")
    public ResponseEntity<?> searchSocieties(@RequestParam String name) {
        try {
            List<Society> societies = societyRepository.findByNameContainingIgnoreCase(name);
            return ResponseEntity.ok(societies);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // Get Society by ID
    @GetMapping("/societies/id/{id}")
    public ResponseEntity<?> getSocietyById(@PathVariable Long id) {
        return societyRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Get Society by Code
    @GetMapping("/societies/{code}")
    public ResponseEntity<?> getSocietyByCode(@PathVariable String code) {
        return societyRepository.findBySocietyCode(code)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Delete Society by ID
    @DeleteMapping("/societies/{id}")
    public ResponseEntity<?> deleteSociety(@PathVariable Long id) {
        try {
            if (!societyRepository.existsById(id)) {
                return ResponseEntity.notFound().build();
            }

            // First, delete all users associated with this society
            List<User> societyUsers = userRepository.findBySocietyId(id);
            userRepository.deleteAll(societyUsers);

            // Then delete the society
            societyRepository.deleteById(id);

            return ResponseEntity.ok(Map.of(
                "message", "Society deleted successfully"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // Get Society Members by Society ID
    @GetMapping("/societies/{id}/members")
    public ResponseEntity<?> getSocietyMembers(@PathVariable Long id) {
        try {
            List<User> members = userRepository.findBySocietyId(id);
            return ResponseEntity.ok(members);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // Get All Society Admins
    @GetMapping("/admins")
    public ResponseEntity<List<User>> getAllSocietyAdmins() {
        List<User> admins = userRepository.findByRole(User.Role.SOCIETY_ADMIN);
        return ResponseEntity.ok(admins);
    }

    // ============ MAINTENANCE COLLECTION ENDPOINTS ============

    // Record maintenance payment from society
    @PostMapping("/societies/{id}/payment")
    public ResponseEntity<?> recordMaintenancePayment(@PathVariable Long id, @RequestBody Map<String, Object> request) {
        try {
            Optional<Society> societyOpt = societyRepository.findById(id);
            if (societyOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            Society society = societyOpt.get();
            Double amount = ((Number) request.get("amount")).doubleValue();

            // Update revenue and pending payments with null checks
            Double currentRevenue = society.getTotalRevenue() != null ? society.getTotalRevenue() : 0.0;
            Double currentPending = society.getPendingPayments() != null ? society.getPendingPayments() : 0.0;
            
            society.setTotalRevenue(currentRevenue + amount);
            society.setPendingPayments(Math.max(0, currentPending - amount));
            society.setLastPaymentDate(java.time.LocalDateTime.now());

            // Extend subscription by 30 days
            society.setSubscriptionExpiryDate(java.time.LocalDateTime.now().plusDays(30));
            society.setSubscriptionStatus(Society.SubscriptionStatus.ACTIVE);

            societyRepository.save(society);

            return ResponseEntity.ok(Map.of(
                "message", "Payment recorded successfully",
                "society", society
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // Set maintenance amount for a society
    @PutMapping("/societies/{id}/maintenance-amount")
    public ResponseEntity<?> setMaintenanceAmount(@PathVariable Long id, @RequestBody Map<String, Object> request) {
        try {
            Optional<Society> societyOpt = societyRepository.findById(id);
            if (societyOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            Society society = societyOpt.get();
            Double amount = ((Number) request.get("amount")).doubleValue();
            society.setMaintenanceAmount(amount);

            societyRepository.save(society);

            return ResponseEntity.ok(Map.of(
                "message", "Maintenance amount updated successfully",
                "society", society
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // Get maintenance collection stats
    @GetMapping("/maintenance-stats")
    public ResponseEntity<?> getMaintenanceStats() {
        try {
            List<Society> societies = societyRepository.findAll();
            Double totalRevenue = societies.stream().mapToDouble(Society::getTotalRevenue).sum();
            Double totalPending = societies.stream().mapToDouble(Society::getPendingPayments).sum();
            int activeSocieties = (int) societies.stream().filter(s -> s.getSubscriptionStatus() == Society.SubscriptionStatus.ACTIVE).count();
            int blockedSocieties = (int) societies.stream().filter(s -> s.getSubscriptionStatus() == Society.SubscriptionStatus.BLOCKED).count();

            return ResponseEntity.ok(Map.of(
                "totalRevenue", totalRevenue,
                "totalPendingPayments", totalPending,
                "activeSocieties", activeSocieties,
                "blockedSocieties", blockedSocieties,
                "totalSocieties", societies.size()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // Block/Unblock society
    @PutMapping("/societies/{id}/status")
    public ResponseEntity<?> updateSocietyStatus(@PathVariable Long id, @RequestBody Map<String, String> request) {
        try {
            Optional<Society> societyOpt = societyRepository.findById(id);
            if (societyOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            Society society = societyOpt.get();
            String status = request.get("status");

            if ("ACTIVE".equals(status)) {
                society.setSubscriptionStatus(Society.SubscriptionStatus.ACTIVE);
            } else if ("BLOCKED".equals(status)) {
                society.setSubscriptionStatus(Society.SubscriptionStatus.BLOCKED);
            } else if ("EXPIRED".equals(status)) {
                society.setSubscriptionStatus(Society.SubscriptionStatus.EXPIRED);
            }

            societyRepository.save(society);

            return ResponseEntity.ok(Map.of(
                "message", "Society status updated successfully",
                "society", society
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
