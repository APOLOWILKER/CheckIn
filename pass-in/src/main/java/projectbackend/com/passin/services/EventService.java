package projectbackend.com.passin.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import projectbackend.com.passin.domain.attendee.Attendee;
import projectbackend.com.passin.domain.event.Event;
import projectbackend.com.passin.domain.event.exceptions.EventFullException;
import projectbackend.com.passin.domain.event.exceptions.EventNotFoundException;
import projectbackend.com.passin.dto.attendee.AttendeeIdDTO;
import projectbackend.com.passin.dto.attendee.AttendeeRequestDTO;
import projectbackend.com.passin.dto.event.EventIdDTO;
import projectbackend.com.passin.dto.event.EventRequestDTO;
import projectbackend.com.passin.dto.event.EventResponseDTO;
import projectbackend.com.passin.repositories.EventRepository;

import java.text.Normalizer;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EventService {
    private final EventRepository eventRepository;
    private final AttendeeService attendeeService;

//    public EventService(EventRepository repository){
//        this.eventRepository = repository;
//    }
    // construtor para inicializar o service, mas tem o requiredArgs que
    // já gera isso em tempo de execução e injeta as dependencias.

    public EventResponseDTO getEventDetail(String eventId){
        Event event = this.getEventByid(eventId);
        List<Attendee> attendeeList = this.attendeeService.getAllAttendeesFromEvent(eventId);

        return new EventResponseDTO(event, attendeeList.size());
    }

    public EventIdDTO createEvent(EventRequestDTO eventDTO){
        Event newEvent = new Event();
        newEvent.setTitle(eventDTO.title());
        newEvent.setDetails(eventDTO.details());
        newEvent.setMaximumAttendees(eventDTO.maximumAttendees());
        newEvent.setSlug(this.createSlug(eventDTO.title()));

        this.eventRepository.save(newEvent);

        return new EventIdDTO(newEvent.getId());
    }

    public AttendeeIdDTO registerAttendeeOnEvent(String eventId, AttendeeRequestDTO  attendeeRequestDTO){
        this.attendeeService.verifyAttendeeSubscription(attendeeRequestDTO.email(), eventId);

        Event event = this.getEventByid(eventId);
        List<Attendee> attendeeList = this.attendeeService.getAllAttendeesFromEvent(eventId);

        if (event.getMaximumAttendees() <= attendeeList.size()) throw new EventFullException("Event is full");

        Attendee newAttendee = new Attendee();
        newAttendee.setName(attendeeRequestDTO.name());
        newAttendee.setEmail(attendeeRequestDTO.email());
        newAttendee.setEvent(event);
        newAttendee.setCreatedAt(LocalDateTime.now());
        this.attendeeService.registerAttendee(newAttendee);

        return new AttendeeIdDTO(newAttendee.getId());
    }

    private Event getEventByid(String eventId){
       return this.eventRepository.findById(eventId).orElseThrow(() -> new EventNotFoundException("Event not found with ID:" + eventId));
    }

    private String createSlug(String text){
        String normalized = Normalizer.normalize(text, Normalizer.Form.NFD);
        return normalized.replaceAll("[\\p{InCOMBINING_DIACRITICAL_MARKS}]", "")
                .replaceAll("[^\\w\\s]", "")
                .replaceAll("\\s+", "-")
                .toLowerCase();
    }
}
