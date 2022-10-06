package com.hwf.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hwf.reggie.dto.DishDto;
import com.hwf.reggie.entity.Dish;
import com.hwf.reggie.entity.DishFlavor;
import com.hwf.reggie.mapper.DishMapper;
import com.hwf.reggie.service.DishFlavorService;
import com.hwf.reggie.service.DishService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service

public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {
    @Autowired
    private DishFlavorService dishFlavorService;


    @Transactional
    //新增菜品，同时插入菜品对应的口味数据，操作dish,dish_flavor两张表
    @Override
    public void savaWithFlavor(DishDto dishDto){
        this.save(dishDto); //保存到菜品表

        Long dishId = dishDto.getId();
        //保存数据到菜品口味表,批量保存
        List<DishFlavor> flavors = dishDto.getFlavors();
        flavors.stream().map((item)->{
            item.setDishId(dishId);
            return item;
        }).collect(Collectors.toList());

        dishFlavorService.saveBatch(flavors);

    }

    /**
     * 根据id来查询菜品信息和对应的口味信息
     * @return
     */
    @Override
    public DishDto getByIdWithFlavor(Long id){
        //先查询菜品的基本信息
        Dish dish = this.getById(id);
        DishDto dishDto = new DishDto();
        BeanUtils.copyProperties(dish,dishDto);
        //查询菜品对应的口味信息
        LambdaQueryWrapper<DishFlavor> lambdaQueryWrapper = new LambdaQueryWrapper();
        lambdaQueryWrapper.eq(DishFlavor::getDishId,dish.getId());
        List<DishFlavor> flavors = dishFlavorService.list(lambdaQueryWrapper);

        dishDto.setFlavors(flavors);

        return dishDto;


    }

    /**
     * 更新菜品信息，同时更新口味表
     * @param dishDto
     */
    @Override
    @Transactional
    public void updateWithFlavor(DishDto dishDto){
        //更新dish表基本信息
        this.updateById(dishDto);
        //清理菜品对应口味数据  delete操作
        LambdaQueryWrapper<DishFlavor> lambdaQueryWrapper = new LambdaQueryWrapper();
        lambdaQueryWrapper.eq(DishFlavor::getDishId,dishDto.getId());
        dishFlavorService.remove(lambdaQueryWrapper);

        //添加当前提交过来的口味数据  insert操作
        List<DishFlavor> flavors = dishDto.getFlavors();
        flavors = dishDto.getFlavors();
        flavors.stream().map((item)->{
            item.setDishId(dishDto.getId());
            return item;
        }).collect(Collectors.toList());
        dishFlavorService.saveBatch(flavors);
    }

}
