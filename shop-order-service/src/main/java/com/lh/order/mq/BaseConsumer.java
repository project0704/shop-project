package com.lh.order.mq;

import com.alibaba.fastjson.JSON;
import com.lh.api.IOrderService;
import com.lh.constant.ShopCode;
import com.lh.shop.pojo.TradeOrder;
import com.lh.shop.pojo.TradePay;
import io.netty.util.CharsetUtil;
import org.apache.rocketmq.common.message.MessageExt;

/**
 * @program: shop-project
 * @description:
 * @author: lh
 * @date: 2021-08-16 23:00
 **/
public class BaseConsumer {
    public TradeOrder handlerMessage(IOrderService orderService, MessageExt message,Integer code){
        //解析消息内容
        String body = new String(message.getBody(), CharsetUtil.UTF_8);
        TradePay tradePay = JSON.parseObject(body, TradePay.class);
        TradeOrder order = orderService.findOne(tradePay.getOrderId());
        if(ShopCode.SHOP_ORDER_PAY_STATUS_NO_PAY.getCode().equals(code)){
            order.setOrderStatus(ShopCode.SHOP_ORDER_PAY_STATUS_NO_PAY.getCode());
        }
        if(ShopCode.SHOP_ORDER_PAY_STATUS_IS_PAY.getCode().equals(code)){
            order.setOrderStatus(ShopCode.SHOP_ORDER_PAY_STATUS_IS_PAY.getCode());
        }
        orderService.changeOrderStatus(order);
        return order;
    };


}
