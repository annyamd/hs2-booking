package com.example.hs2booking.model.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

import static com.example.hs2booking.util.ValidationMessages.MSG_ID_NEGATIVE;

@Data
@AllArgsConstructor
public class PlayerDTO {

    @Min(value = 0, message = MSG_ID_NEGATIVE)
    private Long id;

    @Min(value = 0, message = MSG_ID_NEGATIVE)
    @NotNull(message = "userId field can't be null")
    private Long userId;

    @NotNull(message = "firstName field can't be null")
    @NotBlank(message = "firstName field can't be blank")
    private String firstName;

    @NotNull(message = "lastName field can't be null")
    @NotBlank(message = "lastName field can't be blank")
    private String lastName;

    @Min(value = 0, message = "age field must have a positive value")
    @Max(value = 100, message = "age field is lower or equals to 100")
    private Integer age;

    @Min(value = 0, message = "heightCm field must have a positive value")
    @Max(value = 300, message = "heightCm field is lower or equals to 300")
    private Float heightCm;

    @Min(value = 0, message = "weightKg field must have a positive value")
    @Max(value = 500, message = "weightKg field is lower or equals to 500")
    private Float weightKg;

    private Gender gender;
}
