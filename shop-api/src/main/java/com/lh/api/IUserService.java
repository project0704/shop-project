package com.lh.api;

import com.lh.entity.Result;
import com.lh.shop.pojo.TradeGoods;
import com.lh.shop.pojo.TradeUser;
import com.lh.shop.pojo.TradeUserMoneyLog;

public interface IUserService {
    TradeUser findOne(Long userId);

    Result updateMoneyPaid(TradeUserMoneyLog userMoneyLog);
}
