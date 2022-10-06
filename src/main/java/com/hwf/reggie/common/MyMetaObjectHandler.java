package com.hwf.reggie.common;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.hwf.reggie.util.BaseContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * 自定义元对象数据处理器,用于处理重复字段的自动填充问题，继承由Mp提供的类，在往数据库插入或更新数据时会自动触发这个类中相应的代码，其中的创建人id借助线程类的共享空间来实现，线程访问链条为filter到controller到metaObjectHandler，这个链条共享一个线程id，可以借此存放该链条上需要访问的数据从而实现在该工具类上访问httpSession中的内容
 */
@Slf4j
@Component
public class MyMetaObjectHandler implements MetaObjectHandler {
    @Override
    public void insertFill(MetaObject metaObject) {
        metaObject.setValue("createTime", LocalDateTime.now());
        metaObject.setValue("updateTime",LocalDateTime.now());
        metaObject.setValue("createUser", BaseContext.getCurrentId());
        metaObject.setValue("updateUser",BaseContext.getCurrentId());

    }

    @Override
    public void updateFill(MetaObject metaObject) {
        metaObject.setValue("updateTime",LocalDateTime.now());
        metaObject.setValue("updateUser",BaseContext.getCurrentId());
    }
}
