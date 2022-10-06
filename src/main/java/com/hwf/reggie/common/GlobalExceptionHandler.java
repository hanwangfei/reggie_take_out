package com.hwf.reggie.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLIntegrityConstraintViolationException;

/**
 * 全局异常处理器，底层基于aop代理
 */
@ControllerAdvice(annotations = {RestController.class, Controller.class})  //指定拦截的controller类型
@ResponseBody
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 异常处理方法
     * @return
     */
    @ExceptionHandler(SQLIntegrityConstraintViolationException.class) //指定要处理的异常类
    public R exceptionHandler(SQLIntegrityConstraintViolationException ex){
        log.error(ex.getMessage());
        if(ex.getMessage().contains("Duplicate entry")){
            String[] split = ex.getMessage().split(" ");
            String msg = split[2]+"已存在，不能重复";
            return R.error(msg);
        }
        return R.error("未知错误");

    }


    /**
     * 处理自定义异常
     * @return
     */
    @ExceptionHandler(CustomException.class) //指定要处理的异常类
    public R exceptionHandler(CustomException ex){
        return R.error(ex.getMessage());
    }
}
