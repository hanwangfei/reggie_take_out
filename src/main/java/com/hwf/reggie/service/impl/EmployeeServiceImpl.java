package com.hwf.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hwf.reggie.entity.Employee;
import com.hwf.reggie.mapper.EmployeeMapper;
import com.hwf.reggie.service.EmployeeService;
import org.springframework.stereotype.Service;

@Service
public class EmployeeServiceImpl extends ServiceImpl<EmployeeMapper,Employee> implements EmployeeService{


}
