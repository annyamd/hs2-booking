package com.example.hs2booking.service.feign;

import com.example.hs2booking.model.dto.PlaygroundDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;


@FeignClient(name = "playgrounds-service")
public interface PlaygroundClient {

    @GetMapping("/feign/playgrounds/{id}")
    PlaygroundDTO findById(@PathVariable long id);

}
