package com.society.controller;

import com.society.entity.Event;
import com.society.entity.EventResponse;
import com.society.entity.User;
import com.society.service.EventService;
import com.society.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/events")
public class EventController {

    @Autowired
    private EventService eventService;

    @Autowired
    private UserRepository userRepository;

    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String email = userDetails.getUsername();
            User user = userRepository.findByEmail(email).orElse(null);
            return user != null ? user.getId() : null;
        }
        return null;
    }

    // Create event (Admin only)
    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<?> createEvent(@RequestBody Map<String, Object> request) {
        try {
            Long userId = getCurrentUserId();
            if (userId == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "User not authenticated"));
            }
            
            Event event = new Event();
            event.setTitle((String) request.get("title"));
            event.setDescription((String) request.get("description"));
            event.setDate(LocalDate.parse((String) request.get("date")));
            event.setTime((String) request.get("time"));
            event.setLocation((String) request.get("location"));
            event.setType(Event.EventType.valueOf((String) request.get("type")));
            
            Object imageObj = request.get("image");
            event.setImage(imageObj != null ? (String) imageObj : null);

            Event saved = eventService.createEvent(event, userId);
            return ResponseEntity.ok(saved);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // Get all events
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'RESIDENT')")
    public ResponseEntity<List<Event>> getAllEvents() {
        return ResponseEntity.ok(eventService.getAllEvents());
    }

    // Get upcoming events
    @GetMapping("/upcoming")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'RESIDENT')")
    public ResponseEntity<List<Event>> getUpcomingEvents() {
        return ResponseEntity.ok(eventService.getUpcomingEvents());
    }

    // Get past events
    @GetMapping("/past")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'RESIDENT')")
    public ResponseEntity<List<Event>> getPastEvents() {
        return ResponseEntity.ok(eventService.getPastEvents());
    }

    // Get event by ID
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'RESIDENT')")
    public ResponseEntity<?> getEventById(@PathVariable Long id) {
        try {
            Event event = eventService.getEventById(id);
            return ResponseEntity.ok(event);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // Delete event (Admin only)
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<?> deleteEvent(@PathVariable Long id) {
        try {
            eventService.deleteEvent(id);
            return ResponseEntity.ok(Map.of("message", "Event deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // RSVP to event
    @PostMapping("/{id}/rsvp")
    @PreAuthorize("hasRole('RESIDENT') or hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<?> rsvpToEvent(@PathVariable Long id, @RequestBody Map<String, String> request) {
        try {
            Long userId = getCurrentUserId();
            if (userId == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "User not authenticated"));
            }
            EventResponse.ResponseStatus status = EventResponse.ResponseStatus.valueOf(request.get("status"));
            EventResponse response = eventService.rsvpToEvent(id, userId, status);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // Get user's response for an event
    @GetMapping("/{id}/response")
    @PreAuthorize("hasRole('RESIDENT') or hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<?> getUserResponse(@PathVariable Long id) {
        Long userId = getCurrentUserId();
        if (userId == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "User not authenticated"));
        }
        return ResponseEntity.ok(eventService.getUserResponse(id, userId));
    }

    // Get event responses (Admin only)
    @GetMapping("/{id}/responses")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<?> getEventResponses(@PathVariable Long id) {
        return ResponseEntity.ok(eventService.getEventResponses(id));
    }

    // Get event stats (Admin only)
    @GetMapping("/{id}/stats")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<?> getEventStats(@PathVariable Long id) {
        return ResponseEntity.ok(eventService.getEventStats(id));
    }
}
