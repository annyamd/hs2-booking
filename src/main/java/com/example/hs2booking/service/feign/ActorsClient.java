package com.example.hs2booking.service.feign;

import com.example.hs2booking.model.dto.PlayerDTO;
import com.example.hs2booking.model.dto.TeamDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@FeignClient(name = "actors-service")
public interface ActorsClient {

    @GetMapping("/feign/players/{id}")
    PlayerDTO findPlayerById(@PathVariable long id);

    @GetMapping("/feign/teams/{id}")
    TeamDTO findTeamById(@PathVariable long id);

}
