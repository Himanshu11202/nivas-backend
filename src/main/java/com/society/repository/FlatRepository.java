package com.society.repository;

import com.society.entity.Flat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FlatRepository extends JpaRepository<Flat, Long> {
    Optional<Flat> findByFlatNumber(String flatNumber);
    boolean existsByFlatNumber(String flatNumber);
    List<Flat> findByWing(String wing);
    List<Flat> findByFloor(String floor);
    
    @Query("SELECT COUNT(f) FROM Flat f")
    long countTotalFlats();
    
    @Query("SELECT COUNT(f) FROM Flat f WHERE f.wing = :wing")
    long countByWing(String wing);
}
