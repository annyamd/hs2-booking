package com.example.hs2booking.controller.exceptions.not_found;

public class BookingNotFoundException extends NotFoundException {
    public BookingNotFoundException(String filtersString) {
        super("booking", filtersString);
    }
}
