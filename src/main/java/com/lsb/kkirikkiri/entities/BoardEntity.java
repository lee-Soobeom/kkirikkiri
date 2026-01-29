package com.lsb.kkirikkiri.entities;

import lombok.*;

import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(of = "id")
public class BoardEntity {
    private String id;
    private String displayText;
    private int sortOrder;
    private LocalDateTime createdAt;
}
