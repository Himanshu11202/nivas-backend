package com.society.service;

import com.society.entity.Event;
import com.society.entity.EventResponse;
import com.society.entity.Notification;
import com.society.entity.User;
import com.society.repository.EventRepository;
import com.society.repository.EventResponseRepository;
import com.society.repository.NotificationRepository;
import com.society.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class EventService {

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private EventResponseRepository eventResponseRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    // Create event
    @Transactional
    public Event createEvent(Event event, Long createdBy) {
        event.setCreatedBy(createdBy);
        Event savedEvent = eventRepository.save(event);

        // Send notification to all residents
        sendEventNotification(savedEvent);

        return savedEvent;
    }

    // Send notification to all residents
    private void sendEventNotification(Event event) {
        List<User> residents = userRepository.findByRoleAndStatus(User.Role.RESIDENT, User.UserStatus.ACTIVE);

        for (User resident : residents) {
            Notification notification = new Notification();
            notification.setUserId(resident.getId());
            notification.setMessage("New Event Created: " + event.getTitle() + " on " + event.getDate() + " at " + event.getLocation());
            notification.setType(Notification.NotificationType.GENERAL);
            notification.setIsRead(false);
            notification.setCreatedAt(LocalDateTime.now());
            notificationRepository.save(notification);
        }
    }

    // Get all events
    public List<Event> getAllEvents() {
        return eventRepository.findAllByOrderByDateDesc();
    }

    // Get upcoming events
    public List<Event> getUpcomingEvents() {
        return eventRepository.findUpcomingEvents(LocalDate.now());
    }

    // Get past events
    public List<Event> getPastEvents() {
        return eventRepository.findPastEvents(LocalDate.now());
    }

    // Get event by ID
    public Event getEventById(Long id) {
        return eventRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Event not found"));
    }

    // Delete event
    @Transactional
    public void deleteEvent(Long id) {
        // Delete all responses first
        List<EventResponse> responses = eventResponseRepository.findByEventId(id);
        eventResponseRepository.deleteAll(responses);
        // Delete event
        eventRepository.deleteById(id);
    }

    // RSVP to event
    @Transactional
    public EventResponse rsvpToEvent(Long eventId, Long userId, EventResponse.ResponseStatus status) {
        // Check if user already responded
        Optional<EventResponse> existingResponse = eventResponseRepository.findByEventIdAndUserId(eventId, userId);

        EventResponse response;
        if (existingResponse.isPresent()) {
            // Update existing response
            response = existingResponse.get();
            response.setStatus(status);
        } else {
            // Create new response
            response = new EventResponse();
            response.setEventId(eventId);
            response.setUserId(userId);
            response.setStatus(status);
        }

        return eventResponseRepository.save(response);
    }

    // Get user's response for an event
    public Optional<EventResponse> getUserResponse(Long eventId, Long userId) {
        return eventResponseRepository.findByEventIdAndUserId(eventId, userId);
    }

    // Get event responses with counts
    public Map<String, Object> getEventResponses(Long eventId) {
        Map<String, Object> result = new HashMap<>();

        Long goingCount = eventResponseRepository.countByEventIdAndStatus(eventId, EventResponse.ResponseStatus.GOING);
        Long notGoingCount = eventResponseRepository.countByEventIdAndStatus(eventId, EventResponse.ResponseStatus.NOT_GOING);
        List<EventResponse> allResponses = eventResponseRepository.findByEventId(eventId);

        result.put("going", goingCount);
        result.put("notGoing", notGoingCount);
        result.put("total", goingCount + notGoingCount);
        result.put("responses", allResponses);

        return result;
    }

    // Get event statistics for admin
    public Map<String, Object> getEventStats(Long eventId) {
        Map<String, Object> stats = new HashMap<>();

        Event event = getEventById(eventId);
        Map<String, Object> responses = getEventResponses(eventId);

        stats.put("event", event);
        stats.put("responses", responses);

        return stats;
    }
}
