package com.hwf.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.hwf.reggie.common.R;
import com.hwf.reggie.entity.User;
import com.hwf.reggie.service.UserService;
import com.hwf.reggie.service.impl.UserServiceImpl;
import com.hwf.reggie.util.SMSUtils;
import com.hwf.reggie.util.ValidateCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.Map;

@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {
    @Autowired
    private UserService userService;

    /**
     * 发送手机短信验证码
     * @param user
     * @return
     */
    @PostMapping("sendMsg")
    public R<String> sendMsg(@RequestBody User user, HttpSession httpSession){
        //获取手机号
        String phone = user.getPhone();
        if(StringUtils.isNotEmpty(phone)){
            //生成随机的四位验证码
            String code = ValidateCodeUtils.generateValidateCode(4).toString();
            log.info("code = {}",code);
            //调用api发送短信
            //SMSUtils.sendMessage("");
            //for (int i = 0; i < 10; i++) {
            userService.sendMsg(phone,code);

            //}

            //将验证码保存到session中用于后面的比对
            httpSession.setAttribute("phone",code);
            return R.success("手机验证码短信发送成功");
        }
        return R.error("短信验证码发送失败");


    }


    @PostMapping("/login")
    public R<User> login(@RequestBody Map map, HttpSession session){
        log.info(map.toString());

        //获取手机号或邮箱
        String  phone = map.get("phone").toString();

        //获取验证码
        String code  = map.get("code").toString();

        //从session中获取保存的验证码
        Object codeSession = session.getAttribute("phone");

        //进行验证码比对（页面提交的和session中保存的）

        if(codeSession !=null && codeSession.equals(code)){
            //如果比对成功，则登录成功
            //判断当前邮箱是否为新用户，如果是，则需要自动完成注册
            LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(User::getPhone,phone);
            User user = userService.getOne(queryWrapper);
            if(user==null){
                //新用户
                user = new User();
                user.setPhone(phone);
                user.setStatus(1);
                userService.save(user);
            }
            //登录成功，需要将用户id放入session中
            session.setAttribute("user",user.getId());
            return R.success(user);

        }
        return R.error("登录失败");
    }



}
