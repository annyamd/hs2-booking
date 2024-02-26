package com.example.hs2booking.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

import static com.example.hs2booking.util.ValidationMessages.MSG_ID_NEGATIVE;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookingDTO {

    @Min(value = 0, message = MSG_ID_NEGATIVE)
    private Long id;

    @Min(value = 0, message = MSG_ID_NEGATIVE)
    @NotNull(message = "playgroundId field can't be null")
    private Long playgroundId;

    @Min(value = 0, message = MSG_ID_NEGATIVE)
    private Long playerId;

    @Min(value = 0, message = MSG_ID_NEGATIVE)
    private Long teamId;

    @Future(message = "date must be at least tomorrow")
    @JsonFormat(pattern = "dd.MM.yyyy")
    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonDeserialize(using = LocalDateDeserializer.class)
    @NotNull(message = "date field can't be null")
    private LocalDate date;

    @JsonFormat(pattern = "HH:mm")
    @NotNull(message = "startTime field can't be null")
    private LocalTime startTime;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
    @NotNull(message = "endTime field can't be null")
    private LocalTime endTime;
}
