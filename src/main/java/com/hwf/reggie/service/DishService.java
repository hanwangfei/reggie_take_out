package com.hwf.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hwf.reggie.dto.DishDto;
import com.hwf.reggie.entity.Dish;

public interface DishService extends IService<Dish> {
    void savaWithFlavor(DishDto dishDto);

    DishDto getByIdWithFlavor(Long id);

    void updateWithFlavor(DishDto dishDto);
}
