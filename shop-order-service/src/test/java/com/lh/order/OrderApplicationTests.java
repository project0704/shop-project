package com.lh.order;

import com.lh.api.IOrderService;
import com.lh.shop.pojo.TradeOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.math.BigDecimal;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = OrderApplication.class)
public class OrderApplicationTests {
    @Autowired
    private IOrderService orderService;
    @Test
    public void confirmOrder() throws IOException {
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
        orderService.confirmOrder(order);

        System.in.read();
    }
}
