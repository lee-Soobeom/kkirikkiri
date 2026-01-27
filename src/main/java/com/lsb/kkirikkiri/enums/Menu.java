package com.lsb.kkirikkiri.enums;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum Menu {
    CHICKEN("chicken", "치킨"),
    PIZZA("pizza", "피자"),
    HAMBURGER("hamburger", "햄버거"),
    STEW("stew", "찜/탕"),
    RAW("raw", "회"),
    JAPANESE_FOOD("japanese-food", "일식"),
    CHINESE_FOOD("chinese-food", "중식"),
    WESTERN_FOOD("western-food", "양식");

    public final String code;
    public final String displayText;
}
