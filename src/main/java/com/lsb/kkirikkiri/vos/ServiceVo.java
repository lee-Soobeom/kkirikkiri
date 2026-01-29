package com.lsb.kkirikkiri.vos;


import com.lsb.kkirikkiri.entities.ServiceEntity;
import lombok.*;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class ServiceVo extends ServiceEntity {
    private String filterDisplayText;
}
