package com.hwf.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hwf.reggie.entity.Orders;

public interface OrderService extends IService<Orders> {

    public void submit(Orders orders);
}
