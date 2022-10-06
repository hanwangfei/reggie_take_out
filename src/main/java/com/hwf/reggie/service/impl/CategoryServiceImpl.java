package com.hwf.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hwf.reggie.common.CustomException;
import com.hwf.reggie.entity.Category;
import com.hwf.reggie.entity.Dish;
import com.hwf.reggie.entity.Setmeal;
import com.hwf.reggie.mapper.CategoryMapper;
import com.hwf.reggie.service.CategoryService;
import com.hwf.reggie.service.DishService;
import com.hwf.reggie.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper,Category> implements CategoryService {

    @Autowired
    private DishService dishService;

    @Autowired
    private SetmealService setmealService;
    /**
     *更具id删除分类，删除之前需要判断
     * @param id
     */
    @Override
    public void remove(Long id) {
        //查询该分类是否关联了菜品或套餐
        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        dishLambdaQueryWrapper.eq(Dish::getCategoryId,id);
        int count1 = dishService.count(dishLambdaQueryWrapper);
        if(count1>0){
            //已经关联了菜品，不能删除
            throw new CustomException("当前分类关联了菜品，不能删除");

        }
        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setmealLambdaQueryWrapper.eq(Setmeal::getCategoryId,id);
        int count2= setmealService.count(setmealLambdaQueryWrapper);
        if(count2>0){
            //已经关联了套餐，不能删除
            throw new CustomException("当前分类关联了套餐，不能删除");
        }

        super.removeById(id);

    }
}
