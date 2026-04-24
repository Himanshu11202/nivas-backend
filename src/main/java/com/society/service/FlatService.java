package com.society.service;

import com.society.entity.Flat;
import com.society.repository.FlatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class FlatService {

    @Autowired
    private FlatRepository flatRepository;

    // Flat Management
    public Flat createFlat(Flat flat) {
        // Check if flat number already exists
        if (flatRepository.existsByFlatNumber(flat.getFlatNumber())) {
            throw new RuntimeException("Flat number already exists: " + flat.getFlatNumber());
        }
        
        return flatRepository.save(flat);
    }

    public Flat updateFlat(Long id, Flat flatDetails) {
        Flat flat = flatRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Flat not found"));
        
        // Check if flat number is being changed and if it conflicts with existing flat
        if (!flat.getFlatNumber().equals(flatDetails.getFlatNumber()) 
                && flatRepository.existsByFlatNumber(flatDetails.getFlatNumber())) {
            throw new RuntimeException("Flat number already exists: " + flatDetails.getFlatNumber());
        }
        
        flat.setFlatNumber(flatDetails.getFlatNumber());
        flat.setWing(flatDetails.getWing());
        flat.setFloor(flatDetails.getFloor());
        
        return flatRepository.save(flat);
    }

    public void deleteFlat(Long id) {
        if (!flatRepository.existsById(id)) {
            throw new RuntimeException("Flat not found");
        }
        flatRepository.deleteById(id);
    }

    public Flat getFlatById(Long id) {
        return flatRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Flat not found"));
    }

    public List<Flat> getAllFlats() {
        return flatRepository.findAll();
    }

    public List<Flat> getFlatsByWing(String wing) {
        return flatRepository.findByWing(wing);
    }

    public List<Flat> getFlatsByFloor(String floor) {
        return flatRepository.findByFloor(floor);
    }

    // Dashboard Statistics
    public Map<String, Object> getFlatStats() {
        long totalFlats = flatRepository.countTotalFlats();
        
        // Count flats by wing
        Map<String, Long> wingCounts = Map.of(
                "Wing A", flatRepository.countByWing("A"),
                "Wing B", flatRepository.countByWing("B"),
                "Wing C", flatRepository.countByWing("C"),
                "Wing D", flatRepository.countByWing("D")
        );
        
        return Map.of(
                "totalFlats", totalFlats,
                "wingCounts", wingCounts
        );
    }

    // Additional helper methods
    public Flat getFlatByNumber(String flatNumber) {
        return flatRepository.findByFlatNumber(flatNumber)
                .orElseThrow(() -> new RuntimeException("Flat not found"));
    }

    public boolean flatExists(String flatNumber) {
        return flatRepository.existsByFlatNumber(flatNumber);
    }
}
