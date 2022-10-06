package com.hwf.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hwf.reggie.common.R;
import com.hwf.reggie.dto.DishDto;
import com.hwf.reggie.entity.Category;
import com.hwf.reggie.entity.Dish;
import com.hwf.reggie.entity.DishFlavor;
import com.hwf.reggie.service.CategoryService;
import com.hwf.reggie.service.DishFlavorService;
import com.hwf.reggie.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.PerformanceSensitive;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.yaml.snakeyaml.events.Event;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 菜品管理
 */
@RestController
@RequestMapping("/dish")
@Slf4j
public class DishController {
    @Autowired
    private DishService dishService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private DishFlavorService dishFlavorService;


    /**
     * 新增菜品
     * @param dishDto
     * @return
     */
    @PostMapping
    public R save(@RequestBody DishDto dishDto){
        log.info(dishDto.toString());
        dishService.savaWithFlavor(dishDto);
        return R.success("新增菜品成功");
    }

    /**
     * 菜品信息分页查询
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page,int pageSize,String name){
        //构造分页构造器
        Page<Dish> dishPage = new Page<>(page,pageSize);
        Page<DishDto> dishDtoPage = new Page<>(page,pageSize);
        //条件构造器
        LambdaQueryWrapper<Dish> lambdaQueryWrapper = new LambdaQueryWrapper();
        lambdaQueryWrapper.like(name !=null,Dish::getName,name);
        lambdaQueryWrapper.orderByDesc(Dish::getUpdateTime);

        dishService.page(dishPage,lambdaQueryWrapper);

        //对象拷贝,忽略掉records属性
        BeanUtils.copyProperties(dishPage,dishDtoPage,"records");
        List<Dish> records = dishPage.getRecords();
        List<DishDto> list = records.stream().map((item)->{
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(item,dishDto);
            Long categoryId = item.getCategoryId();  //分类Id
            Category category = categoryService.getById(categoryId);  //拿到分类对象
            dishDto.setCategoryName(category.getName());  //为dto赋值
            return dishDto;

        }).collect(Collectors.toList());

        dishDtoPage.setRecords(list);
        return R.success(dishDtoPage);
    }


    /**
     * 根据id查询菜品信息和对应的口味信息
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<DishDto> getDish(@PathVariable Long id){
        return R.success(dishService.getByIdWithFlavor(id));
    }


    /**
     * 修改菜品
     * @param dishDto
     * @return
     */
    @PutMapping
    public R putDish(@RequestBody DishDto dishDto){
        dishService.updateWithFlavor(dishDto);
        return R.success("修改菜品成功");
    }


    /**
     * 停售或起售菜品(单个和批量共用该接口)
     * @return
     */
    @PostMapping("/status/{status}")
    public R endSale(@PathVariable Integer status,@RequestParam("ids") List<Long> ids){
        List<Dish> dishList = new ArrayList<>();
        dishList = dishService.listByIds(ids);
        dishList.stream().forEach((item)->{
            item.setStatus(status);
        });
        dishService.updateBatchById(dishList);
        return R.success("");
    }


    /**
     * 删除菜品同时要删除该菜品对应的口味
     *
     */
    @DeleteMapping
    public R deleteDish(@RequestParam("ids") List<Long> ids){
        dishService.removeByIds(ids);
        LambdaQueryWrapper<DishFlavor> lambdaQueryWrapper = new LambdaQueryWrapper();
        lambdaQueryWrapper.in(DishFlavor::getDishId,ids);
        dishFlavorService.remove(lambdaQueryWrapper);
        return R.success("");
    }

    /**
     * 根据条件查询相应的菜品
     * @param dish
     * @return
     */
//    @GetMapping("/list")
//    public R list(Dish dish){
//        LambdaQueryWrapper<Dish> lambdaQueryWrapper =  new LambdaQueryWrapper();
//        lambdaQueryWrapper.eq(dish.getCategoryId() !=null,Dish::getCategoryId,dish.getCategoryId());
//        //如果停售则忽略
//        lambdaQueryWrapper.eq(Dish::getStatus,1);
//        lambdaQueryWrapper.like(dish.getName()!=null,Dish::getName,dish.getName());
//        lambdaQueryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);
//        List<Dish> list = dishService.list(lambdaQueryWrapper);
//        return R.success(list);
//    }
    @GetMapping("/list")
    public R list(Dish dish){
        LambdaQueryWrapper<Dish> lambdaQueryWrapper =  new LambdaQueryWrapper();
        lambdaQueryWrapper.eq(dish.getCategoryId() !=null,Dish::getCategoryId,dish.getCategoryId());
        //如果停售则忽略
        lambdaQueryWrapper.eq(Dish::getStatus,1);
        lambdaQueryWrapper.like(dish.getName()!=null,Dish::getName,dish.getName());
        lambdaQueryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);
        List<Dish> list = dishService.list(lambdaQueryWrapper);


        List<DishDto> dishDtoList = list.stream().map((item)->{
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(item,dishDto);
            Long categoryId = item.getCategoryId();  //分类Id
            Category category = categoryService.getById(categoryId);  //拿到分类对象
            dishDto.setCategoryName(category.getName());  //为dto赋值

            Long dishId = item.getId();  //当前菜品id
            LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(DishFlavor::getDishId,dishId);
            List<DishFlavor> dishFlavorList = dishFlavorService.list(queryWrapper);
            dishDto.setFlavors(dishFlavorList);
            return dishDto;

        }).collect(Collectors.toList());


        return R.success(dishDtoList);
    }

}
