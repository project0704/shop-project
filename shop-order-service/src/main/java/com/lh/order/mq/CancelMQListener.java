package com.lh.order.mq;

import com.alibaba.fastjson.JSON;
import com.lh.constant.ShopCode;
import com.lh.entity.MQEntity;
import com.lh.order.mapper.TradeOrderMapper;
import com.lh.shop.pojo.TradeOrder;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.annotation.MessageModel;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @program: shop-project
 * @description: 订单取消监听类
 * @author: lh
 * @date: 2021-08-15 15:19
 **/
@Slf4j
@Component
@RocketMQMessageListener(topic = "${mq.order.topic}",
        consumerGroup = "${mq.order.consumer.group.name}"
        ,messageModel = MessageModel.BROADCASTING)
public class CancelMQListener implements RocketMQListener<MessageExt> {
    @Autowired
    private TradeOrderMapper orderMapper;
    @Override
    public void onMessage(MessageExt msg) {
        log.info("CancelOrderProcessor receive message:"+msg);
        String body = new String(msg.getBody(), CharsetUtil.UTF_8);
        MQEntity mqEntity = JSON.parseObject(body, MQEntity.class);
        Long orderId = mqEntity.getOrderId();
        TradeOrder order = new TradeOrder();
        order.setOrderId(orderId);
        order.setOrderStatus(ShopCode.SHOP_ORDER_CANCEL.getCode());
        orderMapper.updateByPrimaryKeySelective(order);
        log.info("订单:["+order.getOrderId()+"]状态设置为取消");
    }
}
