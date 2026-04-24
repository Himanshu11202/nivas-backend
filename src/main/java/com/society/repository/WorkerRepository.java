package com.society.repository;

import com.society.entity.Worker;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WorkerRepository extends JpaRepository<Worker, Long> {
    
    List<Worker> findByStatus(Worker.WorkerStatus status);
    
    List<Worker> findByJobRole(String jobRole);
    
    List<Worker> findByStatusAndJobRole(Worker.WorkerStatus status, String jobRole);
    
    long countByStatus(Worker.WorkerStatus status);
    
    @Query("SELECT COUNT(w) FROM Worker w WHERE w.status = 'ACTIVE'")
    long countActiveWorkers();
}
