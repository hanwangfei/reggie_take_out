package com.hwf.reggie.dto;


import com.hwf.reggie.entity.Setmeal;
import com.hwf.reggie.entity.SetmealDish;
import lombok.Data;
import java.util.List;

@Data
public class SetmealDto extends Setmeal {

    private List<SetmealDish> setmealDishes;

    private String categoryName;
}
