package com.lh.order.web;


import com.alibaba.dubbo.config.annotation.Reference;
import com.lh.api.IOrderService;
import com.lh.entity.Result;
import com.lh.shop.pojo.TradeOrder;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;

@RunWith(SpringRunner.class)
@Slf4j
@SpringBootTest(classes = ShopOrderWebApplication.class)
public class ShopOrderWebApplicationTests {
    @Autowired
    private RestTemplate restTemplate;
    @Reference
    private IOrderService orderService;
    @Value("${shop.order.baseURI}")
    private String baseURI;
    @Value("${shop.order.confirm}")
    private String confirmOrderPath;


    @Test
    public void confirmOrder() {
        Long couponId = 345988230098857984L;
        Long userId = 345963634385633280L;
        Long goodsId = 345959443973935104L;
        TradeOrder order = new TradeOrder();
        order.setCouponId(couponId);
        order.setUserId(userId);
        order.setGoodsId(goodsId);
        order.setAddress("北京");
        order.setGoodsNumber(1);
        order.setGoodsPrice(new BigDecimal(5000));
        order.setShippingFee(BigDecimal.ZERO);
        order.setOrderAmount(new BigDecimal(5000));
        order.setMoneyPaid(new BigDecimal(100));
        log.info("开始请求！！"+baseURI + confirmOrderPath);
        Result result = restTemplate.postForEntity(baseURI + confirmOrderPath, order, Result.class).getBody();
        log.info("请求结束～～"+result);
    }

}
