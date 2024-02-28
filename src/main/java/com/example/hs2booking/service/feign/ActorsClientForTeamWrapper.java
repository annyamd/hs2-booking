package com.example.hs2booking.service.feign;

import com.example.hs2booking.controller.exceptions.fallback.ServiceUnavailableException;
import com.example.hs2booking.model.dto.TeamDTO;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class ActorsClientForTeamWrapper {

    private final ActorsClient actorsClient;

    @CircuitBreaker(name = "teamCircuitBreaker", fallbackMethod = "getFallbackTeamDTO")
    public TeamDTO getTeamDTO(long teamId) {

        System.out.println("Normal [actorsClient].[findTeamById] call ..."); // TODO убрать вывод

        return actorsClient.findTeamById(teamId);
    }

    public TeamDTO getFallbackTeamDTO(Throwable exception) throws ServiceUnavailableException {

        System.out.println("Fallback [actorsClient].[findTeamById] call ..."); // TODO убрать вывод

        /*return new TeamDTO(0L, "NO_TEAM_NAME", 0L, 0L, true, new HashSet<>());*/

        throw new ServiceUnavailableException("Actors service is temporarily unavailable");
    }
}
