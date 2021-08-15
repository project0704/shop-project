package com.lh.user.mq;

import com.alibaba.fastjson.JSON;
import com.lh.api.IUserService;
import com.lh.constant.ShopCode;
import com.lh.entity.MQEntity;
import com.lh.shop.pojo.TradeUserMoneyLog;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.annotation.MessageModel;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Date;

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
    private IUserService userService;

    @Override
    public void onMessage(MessageExt msg) {
        try {
            log.info("接收到消息");
            //1.解析消息
            String body = new String(msg.getBody(), CharsetUtil.UTF_8);
            MQEntity mqEntity = JSON.parseObject(body, MQEntity.class);
            if (mqEntity.getUserMoney() != null && mqEntity.getUserMoney().compareTo(BigDecimal.ZERO) > 0) {
                //2.调用业务层,进行余额修改
                TradeUserMoneyLog userMoneyLog = new TradeUserMoneyLog();
                userMoneyLog.setUseMoney(mqEntity.getUserMoney());
                userMoneyLog.setMoneyLogType(ShopCode.SHOP_USER_MONEY_REFUND.getCode());
                userMoneyLog.setOrderId(mqEntity.getOrderId());
                userMoneyLog.setUserId(mqEntity.getUserId());
                userService.updateMoneyPaid(userMoneyLog);
                log.info("余额回退成功");
            }
        }catch (Exception e){
            log.error("余额回退失败");
        }
    }
}
