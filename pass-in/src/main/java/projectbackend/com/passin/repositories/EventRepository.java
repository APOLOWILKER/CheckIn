package projectbackend.com.passin.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import projectbackend.com.passin.domain.event.Event;

public interface EventRepository extends JpaRepository<Event, String> {
}
