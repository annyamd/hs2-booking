package com.example.hs2booking.service.feign;

import com.example.hs2booking.controller.exceptions.fallback.ServiceUnavailableException;
import com.example.hs2booking.model.dto.PlaygroundDTO;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PlaygroundClientWrapper {

    private final PlaygroundClient playgroundClient;

    @CircuitBreaker(name = "playgroundCircuitBreaker", fallbackMethod = "getFallbackPlaygroundDTO")
    public PlaygroundDTO getPlaygroundDTO(long playgroundID) {

        System.out.println("Normal [playgroundClient].[findById] call ..."); // TODO убрать вывод

        return playgroundClient.findById(playgroundID);
    }

    public PlaygroundDTO getFallbackPlaygroundDTO(Throwable exception) throws ServiceUnavailableException {

        System.out.println("Fallback [playgroundClient].[findById] call ..."); // TODO убрать вывод

        /*return new PlaygroundDTO(0L, "NO_LOCATION", "NO_PLAYGROUND_NAME", 0F, 0F, new LinkedList<>(),
                new PlaygroundAvailabilityDTO(0L, true, LocalTime.MIN, LocalTime.MAX, 0));*/

        throw new ServiceUnavailableException("Playground service is temporarily unavailable");
    }
}
