package com.lh.pay.web;

import com.alibaba.dubbo.config.annotation.Reference;
import com.lh.api.IPayService;
import com.lh.constant.ShopCode;
import com.lh.entity.Result;
import com.lh.shop.pojo.TradePay;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
@Slf4j
@SpringBootTest(classes = ShopPayWebApplication.class)
@RunWith(SpringRunner.class)
public class ShopPayWebApplicationTests {
    @Autowired
    private RestTemplate restTemplate;
    @Reference
    private IPayService payService;
    @Value("${shop.pay.baseURI}")
    private String baseURI;
    @Value("${shop.pay.createPayment}")
    private String createPayment;
    @Value("${shop.pay.callbackPayment}")
    private String callbackPayment;
    @Test
    public void createPayment() {
        Long orderId = 625135300502491136L;
        TradePay tradePay = new TradePay();
        tradePay.setOrderId(orderId);
        //5000-100-20
        tradePay.setPayAmount(new BigDecimal(4880));
        log.info("开始创建支付订单～～"+baseURI+createPayment);
        Result result = restTemplate.postForEntity(baseURI + createPayment, tradePay, Result.class).getBody();
        log.info("创建支付订单完毕～～"+result);

    }
    @Test
    public void callbackPayment() {
        TradePay pay = new TradePay();
        pay.setPayId(625139895865843712L);
        pay.setOrderId(625135300502491136L);
        pay.setIsPaid(ShopCode.SHOP_ORDER_PAY_STATUS_IS_PAY.getCode());
        log.info("开始支付回调～～"+baseURI+callbackPayment);
        Result result = restTemplate.postForEntity(baseURI+callbackPayment,pay,Result.class).getBody();
        log.info("支付回调结束！！"+result);
    }

}
