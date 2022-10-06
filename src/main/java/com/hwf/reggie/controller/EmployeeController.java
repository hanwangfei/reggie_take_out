package com.hwf.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hwf.reggie.common.R;
import com.hwf.reggie.entity.Employee;
import com.hwf.reggie.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.nio.charset.StandardCharsets;

@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {
    @Autowired
    private EmployeeService employeeService;

    /**
     * 员工登录
     *
     * @param request
     * @param employee
     * @return
     */
    @PostMapping("/login")
    public R<Employee> login(HttpServletRequest request, @RequestBody Employee employee) {

        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes(StandardCharsets.UTF_8));

        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Employee::getUsername, employee.getUsername());
        Employee emp = employeeService.getOne(queryWrapper);

        if (emp == null || !emp.getPassword().equals(password))
            return R.error("登录失败");
        if (emp.getStatus() == 0)
            return R.error("账号已禁用");
        request.getSession().setAttribute("employee", emp.getId());
        return R.success(emp);
    }

    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest request) {
        HttpSession session = request.getSession();
        session.removeAttribute("employee");
        return R.success("退出成功");

    }

    /**
     * 新增员工
     *
     * @param employee
     * @return
     */
    @PostMapping
    public R save(@RequestBody Employee employee, HttpServletRequest request) {
        log.info("员工信息：", employee.toString());
        //统一设置初始密码123456，进行md5加密
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes(StandardCharsets.UTF_8)));
        employeeService.save(employee);
        return R.success("员工新增成功");

    }


    /**
     * 分页查询功能
     */
    @GetMapping("/page")
    public R page(int pageSize, int page, String name) {
        log.info("page={},pageSize={},name={}", page, pageSize, name);  //注意在日志打印中要用这种占位方式来拼接字符串，而不能直接使用+号来拼接
        Page pageInfo = new Page(page, pageSize);
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.like(StringUtils.isNotEmpty(name), Employee::getName, name);
        queryWrapper.orderByDesc(Employee::getUpdateTime);

        employeeService.page(pageInfo, queryWrapper);  //这里不用返回值，会将查询结果封装回pageInfo对象中

        return R.success(pageInfo);
    }

    /**
     * 根据id来修改员工信息
     *
     * @param employee
     * @return
     */
    @PutMapping
    public R<String> update(@RequestBody Employee employee, HttpServletRequest request) throws InterruptedException {
        log.info(employee.toString());
        employeeService.updateById(employee);
        return R.success("员工信息修改成功");
    }


    /**
     * 根据id来查询员工信息
     *
     * @param id
     * @return
     */
    @GetMapping("/{id}")  //这里是restful风格，要使用路径变量来接收
    public R<Employee> getById(@PathVariable long id) { //这里对应的注解就是pathvariable,表示提取了路径变量
        Employee employee = employeeService.getById(id);
        return R.success(employee);
    }

}
