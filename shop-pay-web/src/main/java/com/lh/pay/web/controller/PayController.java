package com.lh.pay.web.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.lh.api.IPayService;
import com.lh.entity.Result;
import com.lh.shop.pojo.TradePay;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @program: shop-project
 * @description:
 * @author: lh
 * @date: 2021-08-17 21:46
 **/
@RequestMapping("/pay")
@RestController
public class PayController {
    @Reference
    private IPayService payService;
    @RequestMapping(value = "/createPayment",method = RequestMethod.POST)
    public Result createPayment(@RequestBody TradePay pay){
        return payService.createPayment(pay);
    }
    @RequestMapping(value = "/callbackPayment",method = RequestMethod.POST)
    public Result callbackPayment(@RequestBody TradePay pay){
       return payService.callbackPayment(pay);
    }
}
