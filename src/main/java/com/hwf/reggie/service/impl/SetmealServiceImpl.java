package com.hwf.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hwf.reggie.common.CustomException;
import com.hwf.reggie.dto.SetmealDto;
import com.hwf.reggie.entity.Setmeal;
import com.hwf.reggie.entity.SetmealDish;
import com.hwf.reggie.mapper.SetmealMapper;
import com.hwf.reggie.service.SetmealDishService;
import com.hwf.reggie.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {


    @Autowired
    private SetmealDishService setmealDishService;


    /**
     * 新增套餐，同时保存套餐和菜品的关联关系
     * @param setmealDto
     */
    @Override
    @Transactional
    public void savaWithDish(SetmealDto setmealDto){

        //1.保存套餐的基本信息，操作setmeal，执行insert
        this.save(setmealDto);

        //2.保存套餐和菜品的关联关系，操作setmeal_dish,执行insert
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        setmealDishes.stream().map((item)->{
            item.setSetmealId(setmealDto.getId());
            return item;
        }).collect(Collectors.toList());

        setmealDishService.saveBatch(setmealDishes);
    }

    /**
     * 删除套餐，同时删除套餐与菜品的关联数据
     * @param ids
     */
    @Override
    @Transactional
    public void removeWithDish(List<Long> ids){
        //1。查询套餐状态，只有停售的套餐才可以删除
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.in(Setmeal::getId,ids).eq(Setmeal::getStatus,1);
        int count = this.count(queryWrapper);
        if(count>0){
            throw new CustomException("存在在售套餐，不能删除");
        }

        //2.删除套餐表中的数据
        this.removeByIds(ids);
        //3.删除关系表中的数据
        LambdaQueryWrapper<SetmealDish> queryWrapper1 = new LambdaQueryWrapper();
        queryWrapper1.in(SetmealDish::getSetmealId,ids);
        setmealDishService.remove(queryWrapper1);

    }

    /**
     *修改套餐
     */
    @Override
    @Transactional
    public void updateSetmealWithDish(SetmealDto setmealDto) {

        //对套餐表直接修改
        this.updateById(setmealDto);

        //对包含的菜品表先清除在添加
        Long setmealId = setmealDto.getId();

        //构建条件构造器
        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SetmealDish::getSetmealId, setmealId);
        setmealDishService.remove(queryWrapper);

        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        setmealDishes = setmealDishes.stream().map((item) -> {
            item.setSetmealId(setmealId);
            return item;
        }).collect(Collectors.toList());

        setmealDishService.saveBatch(setmealDishes);
    }



}
