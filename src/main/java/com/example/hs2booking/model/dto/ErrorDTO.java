package com.example.hs2booking.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
public class ErrorDTO {
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss")
    private final LocalDateTime timestamp;
    private final String message;
    private final String error;
    private List<ViolationDTO> violations;

    public ErrorDTO(LocalDateTime timestamp, String message, String error) {
        this.timestamp = timestamp;
        this.message = message;
        this.error = error;
    }
}
