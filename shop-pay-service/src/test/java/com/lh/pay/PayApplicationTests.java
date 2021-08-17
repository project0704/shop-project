package com.lh.pay;

import com.lh.api.IPayService;
import com.lh.constant.ShopCode;
import com.lh.entity.Result;
import com.lh.shop.pojo.TradePay;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.math.BigDecimal;

@SpringBootTest(classes = PayApplication.class)
@RunWith(SpringRunner.class)
public class PayApplicationTests {

    @Autowired
    private IPayService payService;

    @Test
    public void createPayment(){
        Long orderId = 624805921696124928L;
        TradePay tradePay = new TradePay();
        tradePay.setOrderId(orderId);
        //5000-100-20
        tradePay.setPayAmount(new BigDecimal(4880));
        payService.createPayment(tradePay);
    }

    @Test
    public void callbackPayment() throws IOException {
        TradePay pay = new TradePay();
        pay.setPayId(624806157017554944L);
        pay.setOrderId(624805921696124928L);
        pay.setIsPaid(ShopCode.SHOP_ORDER_PAY_STATUS_IS_PAY.getCode());
        Result result = payService.callbackPayment(pay);

        System.in.read();
    }
}
