package com.society.controller;

import com.society.entity.Flat;
import com.society.service.FlatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/flats")
public class FlatController {

    @Autowired
    private FlatService flatService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SOCIETY_ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<Flat> createFlat(@RequestBody Flat flat) {
        Flat createdFlat = flatService.createFlat(flat);
        return ResponseEntity.ok(createdFlat);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SOCIETY_ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<Flat> updateFlat(@PathVariable Long id, @RequestBody Flat flat) {
        Flat updatedFlat = flatService.updateFlat(id, flat);
        return ResponseEntity.ok(updatedFlat);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SOCIETY_ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<Void> deleteFlat(@PathVariable Long id) {
        flatService.deleteFlat(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SOCIETY_ADMIN', 'SUPER_ADMIN') or hasRole('RESIDENT')")
    public ResponseEntity<Flat> getFlatById(@PathVariable Long id) {
        Flat flat = flatService.getFlatById(id);
        return ResponseEntity.ok(flat);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SOCIETY_ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<List<Flat>> getAllFlats() {
        List<Flat> flats = flatService.getAllFlats();
        return ResponseEntity.ok(flats);
    }

    @GetMapping("/wing/{wing}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SOCIETY_ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<List<Flat>> getFlatsByWing(@PathVariable String wing) {
        List<Flat> flats = flatService.getFlatsByWing(wing);
        return ResponseEntity.ok(flats);
    }

    @GetMapping("/floor/{floor}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SOCIETY_ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<List<Flat>> getFlatsByFloor(@PathVariable String floor) {
        List<Flat> flats = flatService.getFlatsByFloor(floor);
        return ResponseEntity.ok(flats);
    }

    @GetMapping("/number/{flatNumber}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SOCIETY_ADMIN', 'SUPER_ADMIN') or hasRole('RESIDENT')")
    public ResponseEntity<Flat> getFlatByNumber(@PathVariable String flatNumber) {
        Flat flat = flatService.getFlatByNumber(flatNumber);
        return ResponseEntity.ok(flat);
    }

    @GetMapping("/stats")
    @PreAuthorize("hasAnyRole('ADMIN', 'SOCIETY_ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<Map<String, Object>> getFlatStats() {
        Map<String, Object> stats = flatService.getFlatStats();
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/exists/{flatNumber}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SOCIETY_ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<Boolean> checkFlatExists(@PathVariable String flatNumber) {
        boolean exists = flatService.flatExists(flatNumber);
        return ResponseEntity.ok(exists);
    }
}
