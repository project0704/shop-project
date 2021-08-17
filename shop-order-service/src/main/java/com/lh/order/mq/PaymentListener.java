package com.lh.order.mq;

import com.lh.api.IOrderService;
import com.lh.constant.ShopCode;
import com.lh.order.mapper.TradeOrderMapper;
import com.lh.shop.pojo.TradeOrder;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.annotation.MessageModel;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @program: shop-project
 * @description:
 * @author: lh
 * @date: 2021-08-16 23:13
 **/
@Slf4j
@Component
@RocketMQMessageListener(topic = "${mq.pay.topic}",consumerGroup = "${mq.pay.consumer.group.name}"
        ,messageModel = MessageModel.BROADCASTING)
public class PaymentListener extends BaseConsumer implements RocketMQListener<MessageExt> {
    @Autowired
    private IOrderService orderService;
    @Override
    public void onMessage(MessageExt messageExt) {
        try {
            log.info("接受到消息！");
            TradeOrder order = handlerMessage(orderService, messageExt, ShopCode.SHOP_ORDER_PAY_STATUS_IS_PAY.getCode());
            log.info("订单支付成功="+order.getOrderId());
        } catch (Exception e) {
            log.error("订单支付失败！");
        }
    }
}
