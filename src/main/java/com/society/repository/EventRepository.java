package com.society.repository;

import com.society.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

    // Find all events ordered by date desc
    List<Event> findAllByOrderByDateDesc();

    // Find upcoming events
    @Query("SELECT e FROM Event e WHERE e.date >= :currentDate ORDER BY e.date ASC")
    List<Event> findUpcomingEvents(@Param("currentDate") LocalDate currentDate);

    // Find past events
    @Query("SELECT e FROM Event e WHERE e.date < :currentDate ORDER BY e.date DESC")
    List<Event> findPastEvents(@Param("currentDate") LocalDate currentDate);

    // Find events by type
    List<Event> findByTypeOrderByDateDesc(Event.EventType type);
}
