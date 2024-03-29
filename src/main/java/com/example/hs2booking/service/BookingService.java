package com.example.hs2booking.service;

import com.example.hs2booking.controller.exceptions.invalid.InvalidBookingTimeException;
import com.example.hs2booking.controller.exceptions.invalid.NoBookingTargetException;
import com.example.hs2booking.controller.exceptions.not_found.*;
import com.example.hs2booking.controller.exceptions.unavailable_action.PlaygroundNotAvailableException;
import com.example.hs2booking.model.dto.BookingDTO;
import com.example.hs2booking.model.dto.PlaygroundDTO;
import com.example.hs2booking.model.dto.TeamDTO;
import com.example.hs2booking.model.entity.Booking;
import com.example.hs2booking.repository.BookingRepository;
import com.example.hs2booking.service.feign.ActorsClientForPlayerWrapper;
import com.example.hs2booking.service.feign.ActorsClientForTeamWrapper;
import com.example.hs2booking.service.feign.PlaygroundClientWrapper;
import com.example.hs2booking.util.Mapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.LocalDate;
import java.time.LocalTime;

@Service
@RequiredArgsConstructor
public class BookingService {
    private final static int BK_DURATION_MAX = 100;
    private final static int BK_DURATION_MIN = 15;

    private final Mapper<Booking, BookingDTO> mapper = new BookingMapper();
    private final BookingRepository bookingRepository;
    private final PlaygroundClientWrapper playgroundClientWrapper;
    private final ActorsClientForTeamWrapper actorsClientForTeamWrapper;
    private final ActorsClientForPlayerWrapper actorsClientForPlayerWrapper;

    public Flux<BookingDTO> findAll(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return bookingRepository.findAllBy(pageable)
                .map(mapper::entityToDto);
    }

    public Mono<BookingDTO> findById(long id) {
        return bookingRepository.findById(id)
                .switchIfEmpty(Mono.error(getNotFoundIdException(id)))
                .map(mapper::entityToDto);
    }

    public Mono<Void> delete(long id) {
        return bookingRepository.deleteById(id);
    }

    public Mono<BookingDTO> create(BookingDTO dto) {
        LocalDate date = dto.getDate();
        LocalTime bookStartTime = dto.getStartTime();
        LocalTime bookEndTime = dto.getEndTime();

        if (bookStartTime.isAfter(bookEndTime)) {
            throw new InvalidBookingTimeException("start time must be earlier than end time");
        }

        Mono<PlaygroundDTO> pgMono = Mono.just(dto.getPlaygroundId())
                .flatMap(this::getPlaygroundUnblocking)
                .flatMap(playground -> checkBookingTime(
                        playground,
                        dto.getDate(),
                        playground.getPlaygroundAvailability().getAvailableFrom(),
                        playground.getPlaygroundAvailability().getAvailableTo(),
                        dto.getStartTime(),
                        dto.getEndTime()
                ));

        Mono<Integer> sizeMono;

        if (dto.getPlayerId() == null && dto.getTeamId() == null) {
            throw new NoBookingTargetException();
        } else if (dto.getPlayerId() == null) {
            sizeMono = Mono.just(dto.getTeamId())
                    .flatMap(this::getTeamUnblocking)
                    .map(team -> team.getTeamSize().intValue());
        } else {
            sizeMono = Mono.just(1);
        }

        return Mono.zip(pgMono, sizeMono)
                .flatMap((tuple) -> {
                    int size = tuple.getT2();
                    int capacity = tuple.getT1().getPlaygroundAvailability().getCapacity();
                    final int[] curCount = new int[1];
                    curCount[0] = size;

                    return bookingRepository.findByPlaygroundId(dto.getPlaygroundId())
                            .filter(it -> date.isEqual(it.getDate()))
                            .filter(it -> it.getStartTime().isAfter(bookStartTime) && it.getStartTime().isBefore(bookEndTime)
                                    || it.getEndTime().isBefore(bookEndTime) && it.getEndTime().isAfter(bookStartTime))
                            .flatMap(it -> {
                                if (it.getPlayerId() != null) {
                                    return Mono.just(1);
                                } else if (it.getTeamId() != null) {
                                    return getTeamUnblocking(it.getTeamId())
                                            .map(team -> team.getTeamSize().intValue());
                                }
                                return Mono.error(new InvalidBookingTimeException("no space in chosen playground"));
                            })
                            .flatMap(it -> {
                                curCount[0] += it;
                                if (curCount[0] > capacity) {
                                    return Mono.error(new InvalidBookingTimeException("no space in chosen playground"));
                                }
                                return Mono.just(it);
                            })
                            .then();
                })
                .then(Mono.just(mapper.dtoToEntity(dto)))
                .flatMap(bookingRepository::save)
                .map(mapper::entityToDto);
    }

    private Mono<PlaygroundDTO> getPlaygroundUnblocking(long id) {
        return Mono
                .fromCallable(() -> playgroundClientWrapper.getPlaygroundDTO(id))
                .subscribeOn(Schedulers.boundedElastic());
    }

    private Mono<TeamDTO> getTeamUnblocking(long id) {
        return Mono
                .fromCallable(() -> actorsClientForTeamWrapper.getTeamDTO(id))
                .subscribeOn(Schedulers.boundedElastic());
    }

    private Mono<PlaygroundDTO> checkBookingTime(PlaygroundDTO pg, LocalDate date, LocalTime pgStartTime, LocalTime pgEndTime,
                                                 LocalTime bookStartTime, LocalTime bookEndTime) {
        return Mono.just(pg)
                .handle((playground, sink) -> {
                    if (!playground.getPlaygroundAvailability().getIsAvailable()) {
                        sink.error(new PlaygroundNotAvailableException(playground.getPlaygroundId()));
                    } else if (bookEndTime.isAfter(pgEndTime)) {
                        sink.error(new InvalidBookingTimeException("pg closed at this time"));
                    } else if (bookStartTime.isBefore(pgStartTime)) {
                        sink.error(new InvalidBookingTimeException("pg closed at this time"));
                    } else {
                        int duration = getDurationMinutes(bookStartTime, bookEndTime);

                        if (duration < BK_DURATION_MIN) {
                            sink.error(new InvalidBookingTimeException("too small period of time"));
                        } else if (duration > BK_DURATION_MAX) {
                            sink.error(new InvalidBookingTimeException("too big period of time"));
                        } else {
                            sink.next(playground);
                        }
                    }

                });
    }


    public Flux<BookingDTO> getBookingsByPlayer(long playerId) {
        return bookingRepository.findByPlayerId(playerId)
                .map(mapper::entityToDto);
    }

    public Flux<BookingDTO> getBookingsByTeam(long teamId) {
        return bookingRepository.findByTeamId(teamId)
                .map(mapper::entityToDto);
    }

    public Flux<BookingDTO> getBookingsByPlayground(long pgId) {
        return bookingRepository.findByPlaygroundId(pgId)
                .map(mapper::entityToDto);
    }


    private int getDurationMinutes(LocalTime bookStartTime, LocalTime bookEndTime) {
        return 60 * (bookEndTime.getHour() - bookStartTime.getHour())
                + (bookEndTime.getMinute() - bookStartTime.getMinute());
    }

    protected NotFoundException getNotFoundIdException(long id) {
        return new BookingNotFoundException("id = " + id);
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
