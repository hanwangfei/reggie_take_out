package com.hwf.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hwf.reggie.common.R;
import com.hwf.reggie.entity.User;

public interface UserService extends IService<User> {
    R<String> sendMsg(String email, String code);
}
