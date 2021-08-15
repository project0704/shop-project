package com.lh.api;

import com.lh.entity.Result;
import com.lh.shop.pojo.TradeCoupon;

public interface ICouponService {
    TradeCoupon findOne(Long couponId);

    Result updateCouponStatus(TradeCoupon coupon);
}
