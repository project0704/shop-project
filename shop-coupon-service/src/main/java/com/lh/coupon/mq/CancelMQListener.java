package com.lh.coupon.mq;

import com.alibaba.fastjson.JSON;
import com.lh.constant.ShopCode;
import com.lh.coupon.mapper.TradeCouponMapper;
import com.lh.entity.MQEntity;
import com.lh.shop.pojo.TradeCoupon;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.annotation.MessageModel;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
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
    private TradeCouponMapper couponMapper;

    @Override
    public void onMessage(MessageExt msg) {
        try {
            //1. 解析消息内容
            log.info("接收到消息");
            String body = new String(msg.getBody(), CharsetUtil.UTF_8);
            MQEntity mqEntity = JSON.parseObject(body, MQEntity.class);
            Long couponId = mqEntity.getCouponId();
            if(couponId !=null){
                //2. 查询优惠券信息
                TradeCoupon coupon = couponMapper.selectByPrimaryKey(couponId);
                //3.更改优惠券状态
                coupon.setIsUsed(ShopCode.SHOP_COUPON_UNUSED.getCode());
                coupon.setUsedTime(null);
                coupon.setOrderId(null);
                couponMapper.updateByPrimaryKey(coupon);
            }
            log.info("回退优惠券成功");
        } catch (Exception e) {
            log.error("回退优惠券失败");
        }
    }
}
