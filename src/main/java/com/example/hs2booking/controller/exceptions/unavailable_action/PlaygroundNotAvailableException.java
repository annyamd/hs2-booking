package com.example.hs2booking.controller.exceptions.unavailable_action;


public class PlaygroundNotAvailableException extends UnavailableActionException {
    public PlaygroundNotAvailableException(long playgroundId) {
        super("Playground (id = " + playgroundId + ") is not available (closed)");
    }
}
