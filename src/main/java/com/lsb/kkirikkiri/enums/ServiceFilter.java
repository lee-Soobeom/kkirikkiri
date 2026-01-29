package com.lsb.kkirikkiri.enums;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum ServiceFilter {
    EVENT("event", "이벤트/혜택"),
    PAY("pay", "결제/환불"),
    RESTRICT("restrict", "이용제한"),
    USER("user", "사용자 계정/프로필");

    public final String code;
    public final String displayText;
}
