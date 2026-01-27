package com.lsb.kkirikkiri.entities;
import lombok.*;

import java.time.LocalDateTime;

@Builder
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
    private String minOrderPrice;
    private String orderPrice;
    private String deliveryPrice;
    private LocalDateTime orderTime;
    private String restaurant;
    private LocalDateTime pickupTime;
    private Integer addressPostal;
    private String addressPrimary;
    private String addressSecondary;
    private String content;
    private int view;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
