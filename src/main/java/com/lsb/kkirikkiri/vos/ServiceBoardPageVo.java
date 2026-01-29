package com.lsb.kkirikkiri.vos;

import lombok.Getter;

@Getter
public class ServiceBoardPageVo {
    private final int rowCount = 6;
    private final int anchorCount = 5;
    private final int minPage = 1;
    private final int maxPage;
    private final int startPage;
    private final int endPage;
    private final int totalCount;
    private final int requestPage;
    private final int dbOffset;

    public ServiceBoardPageVo(int totalCount, int requestPage) {
        this.totalCount = totalCount;
        this.maxPage = totalCount / this.rowCount + (totalCount % this.rowCount == 0 ? 0 : 1);
        this.requestPage = requestPage;
        this.startPage = (requestPage / this.anchorCount) * this.anchorCount + 1;
        this.endPage = Math.min(this.maxPage, this.startPage + (this.anchorCount - 1));
        this.dbOffset = (requestPage - 1) * this.rowCount;
    }
}
