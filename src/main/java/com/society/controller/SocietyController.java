package com.society.controller;

import com.society.entity.Society;
import com.society.repository.SocietyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/societies")
public class SocietyController {

    @Autowired
    private SocietyRepository societyRepository;

    // Search societies by name
    @GetMapping("/search")
    public ResponseEntity<List<Society>> searchSocieties(@RequestParam String query) {
        List<Society> societies = societyRepository.findByNameContainingIgnoreCase(query);
        return ResponseEntity.ok(societies);
    }

    // Get society by code
    @GetMapping("/code/{code}")
    public ResponseEntity<?> getSocietyByCode(@PathVariable String code) {
        return societyRepository.findBySocietyCode(code)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Get society by ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getSocietyById(@PathVariable Long id) {
        return societyRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
