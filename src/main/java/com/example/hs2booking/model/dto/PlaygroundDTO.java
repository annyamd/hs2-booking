package com.example.hs2booking.model.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

import static com.example.hs2booking.util.ValidationMessages.MSG_ID_NEGATIVE;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class PlaygroundDTO {

    @Min(value = 0, message = MSG_ID_NEGATIVE)
    private Long playgroundId;

    @NotNull(message = "location field can't be null")
    @NotBlank(message = "location field can't be blank")
    private String location;

    @NotNull(message = "playground_name field can't be null")
    @NotBlank(message = "playground_name field can't be blank")
    private String playgroundName;

    @NotNull(message = "latitude field can't be null")
    @Min(value=-90, message = "latitude is higher or equals to -90")
    @Max(value=90, message = "latitude is lower or equals to 90")
    private Float latitude;

    @NotNull(message = "longitude field can't be null")
    @Min(value=-180, message = "longitude is higher or equals to -180")
    @Max(value=180, message = "longitude is lower or equals to 180")
    private Float longitude;

    @NotNull(message = "sports_id field can't be null")
    private List<Long> sportsId;

    @NotNull(message = "playground_availability field can't be null")
    @Valid
    private PlaygroundAvailabilityDTO playgroundAvailability;
}
