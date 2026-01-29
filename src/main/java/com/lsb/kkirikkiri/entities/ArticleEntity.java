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
public class ArticleEntity {
    private int id;
    private String boardId;
    private String title;
    private String menu;
    private String menuName;
    private Integer minOrderPrice;
    private Integer orderPrice;
    private Integer deliveryPrice;
    private LocalDateTime orderTime;
    private String restaurant;
    private LocalDateTime pickupTime;
    private Integer addressPostal;
    private String addressPrimary;
    private String addressSecondary;
    private String content;
    private String nickname;
    private int view;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
