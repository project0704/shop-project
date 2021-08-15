package com.lh.shop.pojo;

import java.util.Date;

public class TradeGoodsNumberLog extends com.lh.shop.pojo.TradeGoodsNumberLogKey {
    private Integer goodsNumber;

    private Date logTime;

    public Integer getGoodsNumber() {
        return goodsNumber;
    }

    public void setGoodsNumber(Integer goodsNumber) {
        this.goodsNumber = goodsNumber;
    }

    public Date getLogTime() {
        return logTime;
    }

    public void setLogTime(Date logTime) {
        this.logTime = logTime;
    }
}