package com.lh.api;

import com.lh.entity.Result;
import com.lh.shop.pojo.TradePay;

public interface IPayService {
    /**
     * 创建支付订单
     * @param pay
     * @return
     */
    Result createPayment(TradePay pay);

    /**
     * 支付回调
     * @param pay
     * @return
     */
    Result callbackPayment(TradePay pay);
}
