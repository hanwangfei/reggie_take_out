package com.hwf.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hwf.reggie.dto.SetmealDto;
import com.hwf.reggie.entity.Employee;
import com.hwf.reggie.entity.Setmeal;

import java.util.List;

public interface SetmealService extends IService<Setmeal> {

    void savaWithDish(SetmealDto setmealDto);

    void removeWithDish(List<Long> ids);

    void updateSetmealWithDish(SetmealDto setmealDto);
}
