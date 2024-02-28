package com.example.hs2booking.model.entity;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name="booking")
public class Booking {

    @Id
    @Column("booking_id")
    private Long bookingId;

    @Column("date")
    private LocalDate date;

    @Column("start_time")
    private LocalTime startTime;

    @Column("end_time")
    private LocalTime endTime;

    @Column("playground_id")
    private Long playgroundId;

    @Column("player_id")
    private Long playerId;

    @Column("team_id")
    private Long teamId;

}
