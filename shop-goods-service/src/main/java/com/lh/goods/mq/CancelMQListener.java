package com.lh.goods.mq;

import com.alibaba.fastjson.JSON;
import com.lh.constant.ShopCode;
import com.lh.entity.MQEntity;
import com.lh.goods.mapper.TradeGoodsMapper;
import com.lh.goods.mapper.TradeGoodsNumberLogMapper;
import com.lh.goods.mapper.TradeMqConsumerLogMapper;
import com.lh.shop.pojo.*;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.annotation.MessageModel;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

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
    @Value("${mq.order.consumer.group.name}")
    private String groupName;

    @Autowired
    private TradeGoodsMapper goodsMapper;

    @Autowired
    private TradeMqConsumerLogMapper mqConsumerLogMapper;

    @Autowired
    private TradeGoodsNumberLogMapper goodsNumberLogMapper;

    @Override
    public void onMessage(MessageExt mesage) {
        String msgId=null;
        String tags=null;
        String keys=null;
        String body=null;
        try {
            log.info("接受到消息");
            //1. 解析消息内容
            msgId = mesage.getMsgId();
            tags = mesage.getTags();
            keys = mesage.getKeys();
            body = new String(mesage.getBody(), CharsetUtil.UTF_8);
            //2. 查询消息消费记录
            TradeMqConsumerLogKey mqConsumerLogKey = new TradeMqConsumerLogKey();
            mqConsumerLogKey.setGroupName(groupName);
            mqConsumerLogKey.setMsgKey(keys);
            mqConsumerLogKey.setMsgTag(tags);
            TradeMqConsumerLog mqConsumerLog = mqConsumerLogMapper.selectByPrimaryKey(mqConsumerLogKey);
            if(mqConsumerLog!=null){
                log.info("消息已消费");
                //3. 判断如果消费过...
                //3.1 获得消息处理状态
                Integer status = mqConsumerLog.getConsumerStatus();
                if(ShopCode.SHOP_MQ_MESSAGE_STATUS_SUCCESS.getCode().intValue()==status.intValue()){
                    log.info("消息:"+msgId+",已经处理过");
                    return;
                }
                if(ShopCode.SHOP_MQ_MESSAGE_STATUS_PROCESSING.getCode().intValue()==status.intValue()){
                    log.info("消息:"+msgId+",正在处理");
                    return;
                }
                if (ShopCode.SHOP_MQ_MESSAGE_STATUS_FAIL.getCode().intValue()==status.intValue()){
                    //处理失败
                    //获得消息处理次数
                    Integer times = mqConsumerLog.getConsumerTimes();
                    if(times>3){
                        log.info("消息:"+msgId+",消息处理超过3次,不能再进行处理了");
                        return;
                    }
                    mqConsumerLog.setConsumerStatus(ShopCode.SHOP_MQ_MESSAGE_STATUS_PROCESSING.getCode());
                    //使用数据库乐观锁进行更新
                    TradeMqConsumerLogExample mqConsumerLogExample = new TradeMqConsumerLogExample();
                    TradeMqConsumerLogExample.Criteria criteria = mqConsumerLogExample.createCriteria();
                    criteria.andMsgTagEqualTo(tags);
                    criteria.andMsgKeyEqualTo(keys);
                    criteria.andGroupNameEqualTo(groupName);
                    criteria.andConsumerTimesEqualTo(times);
                    int r = mqConsumerLogMapper.updateByExampleSelective(mqConsumerLog,mqConsumerLogExample);
                    if(r<=0){
                        //未修改成功,其他线程并发修改
                        log.info("并发修改,稍后处理");
                    }
                }
            }else {
                log.info("消息未消费");
                //4. 判断如果没有消费过...
                mqConsumerLog = new TradeMqConsumerLog();
                mqConsumerLog.setConsumerStatus(ShopCode.SHOP_MQ_MESSAGE_STATUS_PROCESSING.getCode());
                mqConsumerLog.setMsgKey(keys);
                mqConsumerLog.setGroupName(groupName);
                mqConsumerLog.setMsgBody(body);
                mqConsumerLog.setMsgId(msgId);
                mqConsumerLog.setMsgTag(tags);
                mqConsumerLog.setConsumerTimes(0);
                mqConsumerLogMapper.insert(mqConsumerLog);
            }
            //5. 回退库存
            MQEntity mqEntity = JSON.parseObject(body, MQEntity.class);
            Long goodsId = mqEntity.getGoodsId();
            TradeGoods goods = goodsMapper.selectByPrimaryKey(goodsId);
            goods.setGoodsNumber(goods.getGoodsNumber()+mqEntity.getGoodsNum());
            goodsMapper.updateByPrimaryKey(goods);
            //记录库存操作日志
            TradeGoodsNumberLog goodsNumberLog = new TradeGoodsNumberLog();
            goodsNumberLog.setGoodsNumber(mqEntity.getGoodsNum());
            goodsNumberLog.setGoodsId(goodsId);
            goodsNumberLog.setOrderId(mqEntity.getOrderId());
            goodsNumberLog.setLogTime(new Date());
            goodsNumberLogMapper.insert(goodsNumberLog);
            //6. 将消息的处理状态改为成功
            mqConsumerLog.setConsumerStatus(ShopCode.SHOP_MQ_MESSAGE_STATUS_SUCCESS.getCode());
            mqConsumerLog.setConsumerTimestamp(new Date());
            mqConsumerLogMapper.updateByPrimaryKey(mqConsumerLog);
            log.info("回退库存成功");
        }catch (Exception e){
            log.info("库存回退异常！"+e);
            TradeMqConsumerLogKey mqConsumerLogKey = new TradeMqConsumerLogKey();
            mqConsumerLogKey.setMsgTag(tags);
            mqConsumerLogKey.setGroupName(groupName);
            mqConsumerLogKey.setMsgKey(keys);
            TradeMqConsumerLog mqConsumerLog = mqConsumerLogMapper.selectByPrimaryKey(mqConsumerLogKey);
            if (mqConsumerLog==null) {
                //数据库没有记录
                mqConsumerLog = new TradeMqConsumerLog();
                mqConsumerLog.setMsgTag(tags);
                mqConsumerLog.setMsgId(msgId);
                mqConsumerLog.setMsgBody(body);
                mqConsumerLog.setMsgKey(keys);
                mqConsumerLog.setGroupName(groupName);
                mqConsumerLog.setConsumerStatus(ShopCode.SHOP_MQ_MESSAGE_STATUS_FAIL.getCode());
                mqConsumerLog.setConsumerTimestamp(new Date());
                mqConsumerLog.setConsumerTimes(0);
                mqConsumerLogMapper.insert(mqConsumerLog);
            }else {
                mqConsumerLog.setConsumerTimes(mqConsumerLog.getConsumerTimes()+1);
                mqConsumerLog.setConsumerTimestamp(new Date());
                mqConsumerLogMapper.updateByPrimaryKeySelective(mqConsumerLog);
            }
        }
    }
}
