package com.example.hs2booking.service.feign;

import com.example.hs2booking.controller.exceptions.fallback.ServiceUnavailableException;
import com.example.hs2booking.model.dto.PlayerDTO;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ActorsClientForPlayerWrapper {

    private final ActorsClient actorsClient;

    @CircuitBreaker(name = "playersCircuitBreaker", fallbackMethod = "getFallbackPlayerDTO")
    public PlayerDTO getPlayerDTO(long playerId) {

        System.out.println("Normal [actorsClient].[findPlayerById] call ..."); // TODO убрать вывод

        return actorsClient.findPlayerById(playerId);
    }

    public PlayerDTO getFallbackPlayerDTO(Throwable exception) throws ServiceUnavailableException {
        System.out.println("Fallback [actorsClient].[findPlayerById] call ..."); // TODO убрать вывод

        /*return new PlayerDTO(0L, 0L, "NO_FIRST_NAME",
                "NO_LAST_NAME", 0, 0f, 0f, Gender.M);*/
        throw new ServiceUnavailableException("Actors service is temporarily unavailable");
    }
}
