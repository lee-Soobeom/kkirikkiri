package com.lsb.kkirikkiri.enums;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum BoardId {
    SHARE("share", "공구게시판", "article/share/write", "article/share/article"),
    PROMOTE("promote", "홍보게시판", "article/promote/write", "article/promote/article"),
    NOTICE("notice", "공지사항", "article/notice/write", "article/notice/article");

    public final String code;
    public final String displayText;
    public final String writeView;
    public final String articleView;

    public static BoardId from(String code) {
        for (BoardId board : values()) {
            if (board.code.equals(code)) {
                return board;
            }
        }
        throw new IllegalArgumentException("Unknown boardType: " + code);
    }
}
