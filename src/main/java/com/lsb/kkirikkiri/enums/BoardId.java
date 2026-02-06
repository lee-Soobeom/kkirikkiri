package com.lsb.kkirikkiri.enums;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum BoardId {
    SHARE("share", "공구게시판"),
    PROMOTE("promote", "홍보게시판"),
    NOTICE("notice", "공지사항");

    public final String code;
    public final String displayText;

    }
