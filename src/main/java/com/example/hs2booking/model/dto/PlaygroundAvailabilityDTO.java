package com.example.hs2booking.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

import static com.example.hs2booking.util.ValidationMessages.MSG_ID_NEGATIVE;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlaygroundAvailabilityDTO {

    @Min(value = 0, message = MSG_ID_NEGATIVE)
    private Long id;

    @NotNull(message = "is_available field can't be null")
    private Boolean isAvailable;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
    private LocalTime availableFrom;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
    private LocalTime availableTo;

    @Positive(message = "capacity field must be positive value")
    @NotNull(message = "capacity field can't be null")
    private Integer capacity;
}
