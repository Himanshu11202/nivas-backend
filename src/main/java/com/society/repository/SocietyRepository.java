package com.society.repository;

import com.society.entity.Society;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SocietyRepository extends JpaRepository<Society, Long> {

    Optional<Society> findBySocietyCode(String societyCode);

    List<Society> findByNameContainingIgnoreCase(String name);

    boolean existsBySocietyCode(String societyCode);
}
