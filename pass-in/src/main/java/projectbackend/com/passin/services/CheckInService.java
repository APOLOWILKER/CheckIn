package projectbackend.com.passin.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import projectbackend.com.passin.domain.attendee.Attendee;
import projectbackend.com.passin.domain.checkin.CheckIn;
import projectbackend.com.passin.domain.checkin.exceptions.CheckInAlreadyExistsException;
import projectbackend.com.passin.repositories.CheckinRepository;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CheckInService {
    private final CheckinRepository checkinRepository;

    public void registerCheckIn(Attendee attendee){
        this.verifyCheckInExists(attendee.getId());

        CheckIn newCheckIn = new CheckIn();
        newCheckIn.setAttendee(attendee);
        newCheckIn.setCreatedAt(LocalDateTime.now());

        this.checkinRepository.save(newCheckIn);
    }

    private void verifyCheckInExists(String attendeeId){
        Optional<CheckIn> isCheckedIn = this.checkinRepository.findByAttendeeId(attendeeId);
        if (isCheckedIn.isPresent()) throw new CheckInAlreadyExistsException("Attendee already checked in");
    }
}
