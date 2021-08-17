package com.lh.order.web.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.lh.api.IOrderService;
import com.lh.entity.Result;
import com.lh.shop.pojo.TradeOrder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @program: shop-project
 * @description:
 * @author: lh
 * @date: 2021-08-17 21:45
 **/
@Slf4j
@RestController
@RequestMapping("/order")
public class OrderController {
    @Reference
    private IOrderService orderService;

    @RequestMapping(value = "/confirmOrder",method = RequestMethod.POST)
    public Result confirmOrder(@RequestBody TradeOrder order){
        log.info("开始确认订单～～");
        return orderService.confirmOrder(order);
    }

}
