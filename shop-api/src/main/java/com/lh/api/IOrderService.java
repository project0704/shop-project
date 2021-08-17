package com.lh.api;

import com.lh.entity.Result;
import com.lh.shop.pojo.TradeOrder;

/**
 * @program: shop-project
 * @description: 下单接口
 * @author: lh
 * @date: 2021-08-15 01:38
 **/
public interface IOrderService {
    /**
     * 确认订单
     * @param order
     * @return Result
     */
    Result confirmOrder(TradeOrder order);

    TradeOrder findOne(Long orderId);

    void changeOrderStatus(TradeOrder order);
}
