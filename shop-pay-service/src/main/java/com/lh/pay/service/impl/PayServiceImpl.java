package com.lh.pay.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.lh.api.IPayService;
import com.lh.constant.ShopCode;
import com.lh.entity.Result;
import com.lh.exception.CastException;
import com.lh.pay.mapper.TradeMqProducerTempMapper;
import com.lh.pay.mapper.TradePayMapper;
import com.lh.shop.pojo.TradeMqProducerTemp;
import com.lh.shop.pojo.TradePay;
import com.lh.shop.pojo.TradePayExample;
import com.lh.utils.IDWorker;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.SendStatus;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.Date;

/**
 * @program: shop-project
 * @description:
 * @author: lh
 * @date: 2021-08-16 22:07
 **/
@Slf4j
@Component
@Service(interfaceClass = IPayService.class)
public class PayServiceImpl implements IPayService{
    @Resource
    private TradePayMapper tradePayMapper;
    @Resource
    private TradeMqProducerTempMapper mqProducerTempMapper;
    @Autowired
    private IDWorker idWorker;
    @Value("${mq.pay.consumer.group.name}")
    private String groupName;
    @Value("${mq.topic}")
    private String topic;
    @Value("${mq.pay.tag}")
    private String tag;
    @Autowired
    private ThreadPoolTaskExecutor poolTaskExecutor;
    @Autowired
    private RocketMQTemplate rocketMQTemplate;

    @Override
    public Result createPayment(TradePay pay) {
        //查询订单支付状态
        try {
            TradePayExample payExample = new TradePayExample();
            TradePayExample.Criteria criteria = payExample.createCriteria();
            criteria.andOrderIdEqualTo(pay.getOrderId());
            criteria.andIsPaidEqualTo(ShopCode.SHOP_ORDER_PAY_STATUS_IS_PAY.getCode());
            int r = tradePayMapper.countByExample(payExample);
            if(r>0){
                CastException.cast(ShopCode.SHOP_ORDER_PAY_STATUS_IS_PAY);
            }
            Long payId = idWorker.nextId();
            pay.setPayId(payId);
            pay.setIsPaid(ShopCode.SHOP_ORDER_PAY_STATUS_NO_PAY.getCode());
            tradePayMapper.insert(pay);
            log.info("创建支付订单成功！");
        } catch (Exception e) {
            log.info("创建支付订单异常="+e);
            return new Result(ShopCode.SHOP_FAIL.getSuccess(),ShopCode.SHOP_FAIL.getMessage());
        }
        return new Result(ShopCode.SHOP_SUCCESS.getSuccess(),ShopCode.SHOP_SUCCESS.getMessage());
    }

    @Override
    public Result callbackPayment(TradePay pay) {
        if(pay==null||!ShopCode.SHOP_ORDER_PAY_STATUS_IS_PAY.getCode().equals(pay.getIsPaid())){
            CastException.cast(ShopCode.SHOP_PAYMENT_PAY_ERROR);
        }
        pay = tradePayMapper.selectByPrimaryKey(pay.getPayId());
        if(pay==null){
            CastException.cast(ShopCode.SHOP_PAYMENT_NOT_FOUND);
        }
        pay.setIsPaid(ShopCode.SHOP_ORDER_PAY_STATUS_IS_PAY.getCode());
        int r = tradePayMapper.updateByPrimaryKeySelective(pay);
        if(r>0){
            TradeMqProducerTemp mqProducerTemp = new TradeMqProducerTemp();
            mqProducerTemp.setGroupName(groupName);
            mqProducerTemp.setMsgTopic(topic);
            mqProducerTemp.setMsgTag(tag);
            mqProducerTemp.setMsgBody(JSON.toJSONString(pay));
            mqProducerTemp.setId(String.valueOf(idWorker.nextId()));
            mqProducerTemp.setMsgKey(String.valueOf(pay.getPayId()));
            mqProducerTemp.setCreateTime(new Date());
            mqProducerTempMapper.insert(mqProducerTemp);
            TradePay finalPay = pay;
            poolTaskExecutor.submit(new Runnable() {
                @Override
                public void run() {
                    try {
                        SendResult sendResult = sendMessage(topic,tag, String.valueOf(finalPay.getPayId()),JSON.toJSONString(finalPay));
                        log.info("发送消息返回="+sendResult);
                        if(SendStatus.SEND_OK.equals(sendResult.getSendStatus())){
                            mqProducerTempMapper.deleteByPrimaryKey(mqProducerTemp.getId());
                            log.info("删除消息表成功！");
                        }
                    } catch (Exception e) {
                        log.info("发送消息异常！");
                    }
                }
            });

        }
        return new Result(ShopCode.SHOP_SUCCESS.getSuccess(),ShopCode.SHOP_SUCCESS.getMessage());
    }

    private SendResult sendMessage(String topic,String tag,String key,String body) throws Exception {
        if(StringUtils.isEmpty(topic)){
            CastException.cast(ShopCode.SHOP_MQ_TOPIC_IS_EMPTY);
        }
        if(StringUtils.isEmpty(body)){
            CastException.cast(ShopCode.SHOP_MQ_MESSAGE_BODY_IS_EMPTY);
        }
        Message message = new Message(topic, tag, key, body.getBytes());
        SendResult send = rocketMQTemplate.getProducer().send(message);
        return send;
    }
}
