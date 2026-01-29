package com.lsb.kkirikkiri.entities;

import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(of = "id")
public class ServiceEntity {
    private int id;
    private String question;
    private String answer;
    private String filter;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
