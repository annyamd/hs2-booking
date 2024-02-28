package com.example.hs2booking.controller.exceptions.fallback;

import com.example.hs2booking.controller.exceptions.ControllerException;

public class ServiceUnavailableException extends ControllerException {

    public ServiceUnavailableException(String message) {
        super("Service unavailable", message);
    }
}
