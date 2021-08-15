package com.lh.coupon.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.lh.api.ICouponService;
import com.lh.constant.ShopCode;
import com.lh.coupon.mapper.TradeCouponMapper;
import com.lh.entity.Result;
import com.lh.exception.CastException;
import com.lh.shop.pojo.TradeCoupon;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @program: shop-project
 * @description:
 * @author: lh
 * @date: 2021-08-15 02:33
 **/
@Service(interfaceClass = ICouponService.class)
@Component
public class CouponServiceImpl implements ICouponService{
    @Autowired
    private TradeCouponMapper couponMapper;
    @Override
    public TradeCoupon findOne(Long couponId) {
        if(couponId==null){
            CastException.cast(ShopCode.SHOP_COUPON_NO_EXIST);
        }
        return couponMapper.selectByPrimaryKey(couponId);
    }

    @Override
    public Result updateCouponStatus(TradeCoupon coupon) {
        if (coupon==null||coupon.getCouponId()==null) {
            CastException.cast(ShopCode.SHOP_COUPON_NO_EXIST);
        }
        couponMapper.updateByPrimaryKey(coupon);
        return new Result(ShopCode.SHOP_SUCCESS.getSuccess(),ShopCode.SHOP_SUCCESS.getMessage());
    }
}
