package com.example.hs2booking.controller.exceptions.not_found;


import com.example.hs2booking.controller.exceptions.ControllerException;

public class NotFoundException extends ControllerException {
    private final static String errorName = "Element Not Found";

    public NotFoundException(String objectType, String filtersString) {
        super(errorName, "no elements found (type: " + objectType + ", filters: " + filtersString + ")");
    }
}
