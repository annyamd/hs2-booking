package com.example.hs2booking.controller.exceptions.invalid;

import com.example.hs2booking.controller.exceptions.ControllerException;

public class InvalidRequestDataException extends ControllerException {
    public InvalidRequestDataException(String message) {
        super("Invalid request", message);
    }
}