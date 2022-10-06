package com.hwf.reggie.filter;

import com.alibaba.fastjson.JSON;
import com.hwf.reggie.common.R;
import com.hwf.reggie.util.BaseContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 检查用户是否已经登录的过滤器
 */
@Slf4j
@WebFilter(urlPatterns = "/*")
public class LoginCheckFilter implements Filter {
    //路径匹配器，支持通配符
    public static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        log.info("拦截到请求：{}", request.getRequestURI());
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        //获取请求uri
        String requestURI = request.getRequestURI();
        //判断本次请求是否需要处理
        String[] urls = new String[]{
                "/employee/login",
                "/employee/logout",
                "/backend/**",
                "/front/**",
                "/common/**",
                "/user/sendMsg",   //移动端发送短信
                "/user/login"  //移动端登录
        };
        boolean check = check(urls, requestURI);
        //请求不需要处理，直接放行
        if (check) {
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }
        //判断pc用户是否登录
        if (request.getSession().getAttribute("employee") != null) {
            Long empId = (Long) request.getSession().getAttribute("employee");
            BaseContext.setCurrentId(empId);
            //log.info("已设置当前用户id:{}", empId);
            //long id = Thread.currentThread().getId();
            //log.info("filter中线程的id为{}", id);
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }
        //判断移动用户是否登录
        if (request.getSession().getAttribute("user") != null) {
            Long userId = (Long) request.getSession().getAttribute("user");
            BaseContext.setCurrentId(userId);
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }

        //如果未登录，通过输出流对象向客户端页面响应数据
        log.info("用户未登录，不能直接访问页面");
        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));

    }

    /**
     * 路径匹配，检查当前请求是否需要放行
     */
    public boolean check(String[] urls, String requestURI) {
        for (String url : urls) {
            boolean match = PATH_MATCHER.match(url, requestURI);
            if (match)
                return true;
        }
        return false;
    }
}
