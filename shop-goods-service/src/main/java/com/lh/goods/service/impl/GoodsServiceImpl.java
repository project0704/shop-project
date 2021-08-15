package com.lh.goods.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.lh.api.IGoodsService;
import com.lh.constant.ShopCode;
import com.lh.entity.Result;
import com.lh.exception.CastException;
import com.lh.goods.mapper.TradeGoodsMapper;
import com.lh.goods.mapper.TradeGoodsNumberLogMapper;
import com.lh.shop.pojo.TradeGoods;
import com.lh.shop.pojo.TradeGoodsNumberLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * @program: shop-project
 * @description:
 * @author: lh
 * @date: 2021-08-15 01:53
 **/
@Component
@Service(interfaceClass = IGoodsService.class)
public class GoodsServiceImpl implements IGoodsService {

    @Autowired
    private TradeGoodsMapper tradeGoodsMapper;
    @Autowired
    private TradeGoodsNumberLogMapper goodsNumberLogMapper;

    @Override
    public TradeGoods findOne(Long goodsId) {
        if (goodsId == null) {
            CastException.cast(ShopCode.SHOP_REQUEST_PARAMETER_VALID);
        }
        return tradeGoodsMapper.selectByPrimaryKey(goodsId);
    }

    @Override
    public Result reduceGoodsNum(TradeGoodsNumberLog goodsNumberLog) {
        if(goodsNumberLog==null||goodsNumberLog.getOrderId()==null
            ||goodsNumberLog.getGoodsId()==null
            ||goodsNumberLog.getGoodsNumber()==null
            || goodsNumberLog.getGoodsNumber() <1){
            CastException.cast(ShopCode.SHOP_REQUEST_PARAMETER_VALID);
        }
        TradeGoods goods = tradeGoodsMapper.selectByPrimaryKey(goodsNumberLog.getGoodsId());
        if (goods.getGoodsNumber()<goodsNumberLog.getGoodsNumber()) {
            CastException.cast(ShopCode.SHOP_GOODS_NUM_NOT_ENOUGH);
        }
        //扣减库存
        goods.setGoodsNumber(goods.getGoodsNumber()-goodsNumberLog.getGoodsNumber());
        tradeGoodsMapper.updateByPrimaryKey(goods);
        //记录库存保存日志
        goodsNumberLog.setGoodsNumber(-(goodsNumberLog.getGoodsNumber()));
        goodsNumberLog.setLogTime(new Date());
        goodsNumberLogMapper.insert(goodsNumberLog);
        return new Result(ShopCode.SHOP_SUCCESS.getSuccess(),ShopCode.SHOP_SUCCESS.getMessage());
    }
}
