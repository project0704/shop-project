package com.lh.api;

import com.lh.entity.Result;
import com.lh.shop.pojo.TradeGoods;
import com.lh.shop.pojo.TradeGoodsNumberLog;

public interface IGoodsService {
    TradeGoods  findOne(Long goodsId);

    Result reduceGoodsNum(TradeGoodsNumberLog goodsNumberLog);
}
