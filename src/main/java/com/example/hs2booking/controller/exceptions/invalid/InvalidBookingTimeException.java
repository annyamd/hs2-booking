package com.example.hs2booking.controller.exceptions.invalid;

public class InvalidBookingTimeException extends InvalidRequestDataException {
    public InvalidBookingTimeException(String message) {
        super("Booking can't be added: " + message);
    }
}
