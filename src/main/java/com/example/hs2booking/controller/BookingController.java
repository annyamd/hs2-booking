package com.example.hs2booking.controller;

import com.example.hs2booking.model.dto.BookingDTO;
import com.example.hs2booking.service.BookingService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

import static com.example.hs2booking.util.ValidationMessages.*;


@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {

    private final BookingService bookingService;

    @GetMapping(value = "/")
    public Mono<ResponseEntity<?>> getAllRecords(
            @RequestParam(value = "page", defaultValue = "0") @Min(value = 0, message = MSG_PAGE_NEGATIVE) int page,
            @RequestParam(value = "size", defaultValue = "5") @Min(value = 0, message = MSG_SIZE_NEGATIVE) @Max(value = 50, message = MSG_SIZE_TOO_BIG) int size
    ) {
        return bookingService.findAll(page, size)
                .collectList()
                .map(ResponseEntity::ok);
    }

    @GetMapping(value = "/{bookingId}")
    public Mono<ResponseEntity<?>> getRecordById(
            @PathVariable @Min(value = 0, message = MSG_ID_NEGATIVE) long bookingId
    ) {
        return bookingService.findById(bookingId)
                .map(ResponseEntity::ok);
    }

    @PostMapping(value = "/")
    public Mono<ResponseEntity<?>> createBookingRecord(@Valid @RequestBody BookingDTO newRecord) {
        return bookingService.create(newRecord)
                .map(created -> ResponseEntity.status(HttpStatus.CREATED).body(created));
    }

    @DeleteMapping(value = "/{recordId}")
    public Mono<ResponseEntity<?>> deleteBookingRecord(
            @PathVariable @Min(value = 0, message = MSG_ID_NEGATIVE) long recordId
    ) {
        return bookingService.delete(recordId)
                .then(Mono.just(new ResponseEntity<>(HttpStatus.OK)));
    }

}
