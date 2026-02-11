package com.lsb.kkirikkiri.enums;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum MessageType {
    ACCEPT("accept", "수락"),
    CONFIRM("confirm", "요청"),
    DENY("deny", "거절"),
    TALK("talk", "대화");

    public final String code;
    public final String displayText;
}
