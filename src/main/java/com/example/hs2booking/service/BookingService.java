package com.example.hs2booking.service;

import com.example.hs2booking.controller.exceptions.invalid.InvalidBookingTimeException;
import com.example.hs2booking.controller.exceptions.invalid.NoBookingTargetException;
import com.example.hs2booking.controller.exceptions.not_found.*;
import com.example.hs2booking.controller.exceptions.unavailable_action.PlaygroundNotAvailableException;
import com.example.hs2booking.model.dto.BookingDTO;
import com.example.hs2booking.model.dto.PlayerDTO;
import com.example.hs2booking.model.dto.PlaygroundDTO;
import com.example.hs2booking.model.dto.TeamDTO;
import com.example.hs2booking.model.entity.Booking;
import com.example.hs2booking.repository.BookingRepository;
import com.example.hs2booking.service.feign.ActorsClient;
import com.example.hs2booking.service.feign.PlaygroundClient;
import com.example.hs2booking.util.GeneralService;
import com.example.hs2booking.util.Mapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingService extends GeneralService<Booking, BookingDTO> {
    private final static int BK_DURATION_MAX = 100;
    private final static int BK_DURATION_MIN = 15;

    private final Mapper<Booking, BookingDTO> mapper = new BookingMapper();
    private final BookingRepository bookingRepository;
    private final PlaygroundClient playgroundClient;
    private final ActorsClient actorsClient;


    @Transactional
    @Override
    public BookingDTO create(BookingDTO dto) {
        PlaygroundDTO playground = playgroundClient.findById(dto.getPlaygroundId());
        if (!playground.getPlaygroundAvailability().getIsAvailable()) {
            throw new PlaygroundNotAvailableException(playground.getPlaygroundId());
        }

        int size = 1;
        TeamDTO team = null;
        PlayerDTO player = null;
        boolean isPlayerChosen = false;
        if (dto.getPlayerId() != null) {
            isPlayerChosen = true;
            player = actorsClient.findPlayerById(dto.getPlayerId());
        } else if (dto.getTeamId() != null) {
            team = actorsClient.findTeamById(dto.getTeamId());
            size = team.getTeamSize().intValue();
        } else {
            throw new NoBookingTargetException();
        }

        checkTimeForBooking(
                playground,
                dto.getDate(),
                playground.getPlaygroundAvailability().getAvailableFrom(),
                playground.getPlaygroundAvailability().getAvailableTo(),
                dto.getStartTime(),
                dto.getEndTime(),
                size
        );

        return super.create(dto);
    }

    public List<BookingDTO> getBookingsByPlayer(long playerId) {
        return bookingRepository.findByPlayerId(playerId)
                .stream()
                .map(mapper::entityToDto)
                .toList();
    }

    public List<BookingDTO> getBookingsByTeam(long teamId) {
        return bookingRepository.findByTeamId(teamId)
                .stream()
                .map(mapper::entityToDto)
                .toList();
    }

    public List<BookingDTO> getBookingsByPlayground(long pgId) {
        return bookingRepository.findByPlaygroundId(pgId)
                .stream()
                .map(mapper::entityToDto)
                .toList();
    }

    private void checkTimeForBooking(PlaygroundDTO playground, LocalDate date, LocalTime pgStartTime, LocalTime pgEndTime,
                                     LocalTime bookStartTime, LocalTime bookEndTime, int size) {
        if (bookStartTime.isAfter(bookEndTime)) {
            throw new InvalidBookingTimeException("start time must be earlier than end time");
        }
        if (bookStartTime.isBefore(pgStartTime)) {
            throw new InvalidBookingTimeException("pg closed at this time");
        }
        if (bookEndTime.isAfter(pgEndTime)) {
            throw new InvalidBookingTimeException("pg closed at this time");
        }

        int duration = getDurationMinutes(bookStartTime, bookEndTime);

        if (duration < BK_DURATION_MIN) {
            throw new InvalidBookingTimeException("too small period of time");
        }
        if (duration > BK_DURATION_MAX) {
            throw new InvalidBookingTimeException("too big period of time");
        }

        List<Booking> bookings = bookingRepository.findByPlaygroundId(playground.getPlaygroundId());

        int capacity = playground.getPlaygroundAvailability().getCapacity();
        final int[] curCount = new int[1];
        curCount[0] = size;

        bookings
                .forEach(it -> {
                    if (date.isEqual(it.getDate())) {
                        LocalTime start = it.getStartTime();
                        LocalTime end = it.getEndTime();
                        if (start.isAfter(bookStartTime) && start.isBefore(bookEndTime)
                                || end.isBefore(bookEndTime) && end.isAfter(start)) {
                            if (it.getPlayerId() != null) {
                                curCount[0] += 1;
                            } else if (it.getTeamId() != null) {
                                TeamDTO team = actorsClient.findTeamById(it.getTeamId());
                                curCount[0] += team.getTeamSize();
                            }
                            if (curCount[0] > capacity) {
                                throw new InvalidBookingTimeException("no space in chosen playground");
                            }
                        }
                    }
                });
    }

    private int getDurationMinutes(LocalTime bookStartTime, LocalTime bookEndTime) {
        return 60 * (bookEndTime.getHour() - bookStartTime.getHour())
                + (bookEndTime.getMinute() - bookStartTime.getMinute());
    }

    @Override
    protected NotFoundException getNotFoundIdException(long id) {
        return new BookingNotFoundException("id = " + id);
    }

    @Override
    protected Mapper<Booking, BookingDTO> getMapper() {
        return mapper;
    }

    @Override
    protected JpaRepository<Booking, Long> getRepository() {
        return bookingRepository;
    }

    static class BookingMapper implements Mapper<Booking, BookingDTO> {

        @Override
        public BookingDTO entityToDto(Booking entity) {
            Long teamId = null;
            Long playerId = null;
            if (entity.getTeamId() != null && entity.getTeamId() != 0) {
                teamId = entity.getTeamId();
            } else {
                playerId = entity.getPlayerId();
            }
            return new BookingDTO(
                    entity.getBookingId(),
                    entity.getPlaygroundId(),
                    playerId,
                    teamId,
                    entity.getDate(),
                    entity.getStartTime(),
                    entity.getEndTime()
            );
        }

        @Override
        public Booking dtoToEntity(BookingDTO dto) {
            Long playerId = dto.getPlayerId();
            Long teamId = dto.getTeamId();
            if (dto.getPlayerId() != null) {
                teamId = null;
            }
            return new Booking(
                    null,
                    dto.getDate(),
                    dto.getStartTime(),
                    dto.getEndTime(),
                    dto.getPlaygroundId(),
                    playerId,
                    teamId
            );
        }
    }

}
