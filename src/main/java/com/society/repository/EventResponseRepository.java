package com.society.repository;

import com.society.entity.EventResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EventResponseRepository extends JpaRepository<EventResponse, Long> {

    // Find response by event and user
    Optional<EventResponse> findByEventIdAndUserId(Long eventId, Long userId);

    // Find all responses for an event
    List<EventResponse> findByEventId(Long eventId);

    // Count responses by status
    @Query("SELECT COUNT(er) FROM EventResponse er WHERE er.eventId = :eventId AND er.status = :status")
    Long countByEventIdAndStatus(@Param("eventId") Long eventId, @Param("status") EventResponse.ResponseStatus status);

    // Get all responses with status
    @Query("SELECT er.status, COUNT(er) FROM EventResponse er WHERE er.eventId = :eventId GROUP BY er.status")
    List<Object[]> getResponseCountsByEventId(@Param("eventId") Long eventId);

    // Delete response by event and user
    void deleteByEventIdAndUserId(Long eventId, Long userId);
}
