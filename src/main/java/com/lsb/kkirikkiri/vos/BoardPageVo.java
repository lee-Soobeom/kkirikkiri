package com.lsb.kkirikkiri.vos;

import lombok.Getter;

@Getter
public class BoardPageVo {
    private final int rowCount = 5;
    private final int anchorCount = 5;
    private final int minPage = 1;

    private final int maxPage;
    private final int startPage;
    private final int endPage;
    private final int totalCount;
    private final int requestPage;
    private final int dbOffset;

    private final String sort;


    public BoardPageVo(int requestPage, int totalCount, String sort) {
        this.requestPage = Math.max(requestPage, this.minPage);
        this.totalCount = totalCount;
        this.sort = (sort == null || sort.isBlank()) ? "latest" : sort;
        this.maxPage = totalCount / this.rowCount + (totalCount % this.rowCount == 0 ? 0 : 1);
        this.startPage = (requestPage / this.anchorCount) * this.anchorCount + 1;
        this.endPage = Math.min(this.maxPage, this.startPage + (this.anchorCount - 1));
        this.dbOffset = (this.requestPage - 1) * this.rowCount;
    }
}
