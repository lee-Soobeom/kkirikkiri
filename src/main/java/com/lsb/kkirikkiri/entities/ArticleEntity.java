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
    private Integer id;
    private String boardId;
    private String userId;
    private int participantsId;
    private Integer walletId;
    private String title;
    private String menu;
    private String menuName;
    private Integer minOrderPrice;
    private Integer orderPrice;
    private Integer deliveryPrice;
    private LocalDateTime orderTime;
    private String restaurant;
    private String restaurantLat;
    private String restaurantLng;
    private LocalDateTime pickupTime;
    private String addressPrimary;
    private String addressSecondary;
    private String content;
    private Boolean shareChecked;
    private Boolean entryChecked;
    private int view;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;


}
