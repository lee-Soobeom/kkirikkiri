package com.lsb.kkirikkiri.enums;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum Menu {
    ALL("all", "전체"),
    CHICKEN("chicken", "치킨"),
    PIZZA("pizza", "피자"),
    HAMBURGER("hamburger", "햄버거"),
    STEW("stew", "찜/탕"),
    JAPANESE_FOOD("japanese-food", "회/일식"),
    CHINESE_FOOD("chinese-food", "중식"),
    WESTERN_FOOD("western-food", "양식"),
    DESSERT("dessert", "디저트"),
    ETC("etc", "그 외");

    public final String code;
    public final String displayText;
}
