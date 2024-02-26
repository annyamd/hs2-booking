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

import java.util.List;

import static com.example.hs2booking.util.ValidationMessages.*;


@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {

    private final BookingService bookingService;

    @GetMapping(value = "/")
    public ResponseEntity<?> getAllRecords(
            @RequestParam(value = "page", defaultValue = "0") @Min(value = 0, message = MSG_PAGE_NEGATIVE) int page,
            @RequestParam(value = "size", defaultValue = "5") @Min(value = 0, message = MSG_SIZE_NEGATIVE) @Max(value = 50, message = MSG_SIZE_TOO_BIG) int size
    ) {
        List<BookingDTO> records = bookingService.findAll(page, size);
        return ResponseEntity.ok(records);
    }

    @GetMapping(value = "/{bookingId}")
    public ResponseEntity<?> getRecordById(
            @PathVariable @Min(value = 0, message = MSG_ID_NEGATIVE) long bookingId
    ) {
        BookingDTO booking = bookingService.findById(bookingId);
        return ResponseEntity.ok(booking);
    }

    @PostMapping(value = "/")
    public ResponseEntity<?> createBookingRecord(@Valid @RequestBody BookingDTO newRecord) {
        BookingDTO created = bookingService.create(newRecord);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @DeleteMapping(value = "/{recordId}")
    public ResponseEntity<?> deleteBookingRecord(
            @PathVariable @Min(value = 0, message = MSG_ID_NEGATIVE) long recordId
    ) {
        bookingService.delete(recordId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
