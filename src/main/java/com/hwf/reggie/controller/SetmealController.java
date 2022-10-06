package com.hwf.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hwf.reggie.common.R;
import com.hwf.reggie.dto.SetmealDto;
import com.hwf.reggie.entity.Category;
import com.hwf.reggie.entity.Setmeal;
import com.hwf.reggie.entity.SetmealDish;
import com.hwf.reggie.service.CategoryService;
import com.hwf.reggie.service.SetmealDishService;
import com.hwf.reggie.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 套餐管理
 */
@RestController
@RequestMapping("/setmeal")
@Slf4j
public class SetmealController {
    @Autowired
    private SetmealService setmealService;

    @Autowired
    private SetmealDishService setmealDishService;

    @Autowired
    private CategoryService categoryService;


    /**
     * 新增套餐
     *
     * @param setmealDto
     * @return
     */
    @PostMapping
    public R save(@RequestBody SetmealDto setmealDto) {
        setmealService.savaWithDish(setmealDto);
        return R.success("新增套餐成功");
    }


    /**
     * 套餐分页查询
     *
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name) {
        Page<Setmeal> pageInfo = new Page<>(page, pageSize);
        Page<SetmealDto> dtoPage = new Page<>(page, pageSize);

        LambdaQueryWrapper<Setmeal> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.like(name != null, Setmeal::getName, name).orderByDesc(Setmeal::getUpdateTime);
        setmealService.page(pageInfo, lambdaQueryWrapper);
        //对象拷贝
        BeanUtils.copyProperties(pageInfo, dtoPage, "records");
        List<Setmeal> records = pageInfo.getRecords();

        List<SetmealDto> list = records.stream().map((item) -> {

            SetmealDto setmealDto = new SetmealDto();
            BeanUtils.copyProperties(item, setmealDto);
            Long categoryId = item.getCategoryId();
            //根据分类Id查询分类对象
            Category category = categoryService.getById(categoryId);
            if (category != null) {
                String categoryName = category.getName();
                setmealDto.setCategoryName(categoryName);
            }
            return setmealDto;

        }).collect(Collectors.toList());
        dtoPage.setRecords(list);
        return R.success(dtoPage);
    }


    /**
     * 删除套餐
     * @param ids
     * @return
     */
    @DeleteMapping
    public R delete(@RequestParam("ids") List<Long> ids) {

        setmealService.removeWithDish(ids);
        return R.success("删除成功");
    }


    /**
     * 停售或起售
     *
     */
    @PostMapping("/status/{status}")
    public R endSave(@PathVariable int status,@RequestParam List<Long> ids){
        List<Setmeal> list = setmealService.listByIds(ids);
        list.stream().forEach((item)->{
            item.setStatus(status);
        });
        setmealService.updateBatchById(list);
        return R.success("修改成功");
    }


    /**
     * 根据id响应回前端一个setMealDto对象
     * @param id
     * @return
     */

    @GetMapping("{id}")
    public R getById(@PathVariable Long id){
        //根据id查询套餐表
        Setmeal setmeal = setmealService.getById(id);

        //根据套餐id查询所对应的菜品
        //构造条件封装
        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SetmealDish::getSetmealId,id);
        queryWrapper.eq(SetmealDish::getIsDeleted,0);
        List<SetmealDish> setmealDishes = setmealDishService.list(queryWrapper);

        //获取分类id，根据分类id查询分类名称
        Long categoryId = setmeal.getCategoryId();
        Category category = categoryService.getById(categoryId);
        String categoryName = category.getName();

        SetmealDto setmealDto = new SetmealDto();
        //对象拷贝
        BeanUtils.copyProperties(setmeal,setmealDto);
        setmealDto.setSetmealDishes(setmealDishes);
        setmealDto.setCategoryName(categoryName);


        return R.success(setmealDto);
    }


    /**
     * 修改套餐
     */
    @PutMapping
    public R uupdate(@RequestBody SetmealDto setmealDto){
        setmealService.updateSetmealWithDish(setmealDto);
        return R.success("套餐修改成功");
    }


    @GetMapping("/list")
    public R list(Setmeal setmeal){
        LambdaQueryWrapper<Setmeal> lambdaQueryWrapper = new LambdaQueryWrapper();
        lambdaQueryWrapper.eq(setmeal.getCategoryId() != null,Setmeal::getCategoryId,setmeal.getCategoryId());

        lambdaQueryWrapper.eq(Setmeal::getStatus,1);
        lambdaQueryWrapper.like(setmeal.getName()!=null,Setmeal::getName,setmeal.getName());
        List<Setmeal> list = setmealService.list(lambdaQueryWrapper);
        return R.success(list);
    }


}
