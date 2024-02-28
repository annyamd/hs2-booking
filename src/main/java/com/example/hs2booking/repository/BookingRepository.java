package com.example.hs2booking.repository;

import com.example.hs2booking.model.entity.Booking;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;


@Repository
public interface BookingRepository extends ReactiveCrudRepository<Booking, Long> {

    Flux<Booking> findByPlayerId(Long playerId);

    Flux<Booking> findByTeamId(Long teamId);

    Flux<Booking> findByPlaygroundId(Long playgroundId);

    Flux<Booking> findAllBy(Pageable pageable);
}
