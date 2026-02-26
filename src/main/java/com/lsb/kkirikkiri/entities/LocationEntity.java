package com.lsb.kkirikkiri.entities;

import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class LocationEntity {
    private Double lat;
    private Double lng;
}
