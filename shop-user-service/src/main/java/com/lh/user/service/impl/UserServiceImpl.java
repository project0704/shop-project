package com.lh.user.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.lh.api.IUserService;
import com.lh.constant.ShopCode;
import com.lh.entity.Result;
import com.lh.exception.CastException;
import com.lh.shop.pojo.TradeUser;
import com.lh.shop.pojo.TradeUserMoneyLog;
import com.lh.shop.pojo.TradeUserMoneyLogExample;
import com.lh.user.mapper.TradeUserMapper;
import com.lh.user.mapper.TradeUserMoneyLogMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @program: shop-project
 * @description:
 * @author: lh
 * @date: 2021-08-15 01:59
 **/
@Service
@Component
public class UserServiceImpl implements IUserService {
    @Autowired
    private TradeUserMapper tradeUserMapper;
    @Autowired
    private TradeUserMoneyLogMapper userMoneyLogMapper;
    @Override
    public TradeUser findOne(Long userId) {
        if (userId == null) {
            CastException.cast(ShopCode.SHOP_REQUEST_PARAMETER_VALID);
        }
        return tradeUserMapper.selectByPrimaryKey(userId);
    }

    @Override
    public Result updateMoneyPaid(TradeUserMoneyLog userMoneyLog) {
        if (userMoneyLog==null
                ||userMoneyLog.getOrderId()==null
                ||userMoneyLog.getUserId()==null
                ||userMoneyLog.getUseMoney().compareTo(BigDecimal.ZERO)<=0) {
            CastException.cast(ShopCode.SHOP_REQUEST_PARAMETER_VALID);
        }
        TradeUserMoneyLogExample moneyLogExample = new TradeUserMoneyLogExample();
        TradeUserMoneyLogExample.Criteria criteria = moneyLogExample.createCriteria();
        criteria.andOrderIdEqualTo(userMoneyLog.getOrderId());
        criteria.andUserIdEqualTo(userMoneyLog.getUserId());
        int r = userMoneyLogMapper.countByExample(moneyLogExample);
        Integer moneyLogType = userMoneyLog.getMoneyLogType();
        TradeUser user = tradeUserMapper.selectByPrimaryKey(userMoneyLog.getUserId());
        if(ShopCode.SHOP_USER_MONEY_PAID.getCode()==moneyLogType){
            if (r>0) {
                //已经付款
                CastException.cast(ShopCode.SHOP_ORDER_PAY_STATUS_IS_PAY);
            }
            user.setUserMoney(new BigDecimal(user.getUserMoney()).subtract(userMoneyLog.getUseMoney()).longValue());
            tradeUserMapper.updateByPrimaryKey(user);
        }
        if(ShopCode.SHOP_USER_MONEY_REFUND.getCode()==moneyLogType){
            //退款
            if(r<0){
                CastException.cast(ShopCode.SHOP_ORDER_PAY_STATUS_NO_PAY);
            }
            TradeUserMoneyLogExample userMoneyLogExample = new TradeUserMoneyLogExample();
            TradeUserMoneyLogExample.Criteria criteria1 = userMoneyLogExample.createCriteria();
            criteria.andOrderIdEqualTo(userMoneyLog.getOrderId());
            criteria.andUserIdEqualTo(userMoneyLog.getUserId());
            criteria.andMoneyLogTypeEqualTo(ShopCode.SHOP_USER_MONEY_REFUND.getCode());
            r = userMoneyLogMapper.countByExample(userMoneyLogExample);
            if(r>0){
                CastException.cast(ShopCode.SHOP_USER_MONEY_REFUND_ALREADY);
            }
            user.setUserMoney(new BigDecimal(user.getUserMoney()).subtract(userMoneyLog.getUseMoney()).longValue());
            tradeUserMapper.updateByPrimaryKey(user);
        }
        userMoneyLog.setCreateTime(new Date());
        userMoneyLogMapper.insert(userMoneyLog);
        return new Result(ShopCode.SHOP_SUCCESS.getSuccess(),ShopCode.SHOP_SUCCESS.getMessage());
    }
}
