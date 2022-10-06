package com.hwf.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hwf.reggie.common.R;
import com.hwf.reggie.entity.User;
import com.hwf.reggie.mapper.UserMapper;
import com.hwf.reggie.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Service;


@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Autowired
    private JavaMailSenderImpl mailSender;  //报红但可以用

    @Value("${spring.mail.username}")
    private String username;   // 邮件发送人

    @Override
    public R<String> sendMsg(String email, String code) {

        //发送到邮箱
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setSubject("尊敬的用户您好：");  //头文件
        mailMessage.setText("你的验证码为："+code+"；该验证码用于验证登录，请勿泄露");  //内容
        mailMessage.setTo(email);   //接收者
        mailMessage.setFrom(username);  //发送者
        mailSender.send(mailMessage);

        return R.error("验证码发送失败，请稍后重试");
    }
}
