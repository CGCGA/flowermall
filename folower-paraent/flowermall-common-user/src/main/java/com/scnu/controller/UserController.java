package com.scnu.controller;

import com.scnu.pojo.User;
import com.scnu.service.UserService;
import com.scnu.utils.CookieUtils;
import com.scnu.vo.SysResult;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @RequestMapping("user/manage/checkUserName")
    public SysResult checkUserName(String userName) {
        Integer exist = userService.checkUserName(userName);

        if(exist == 0) {
            return SysResult.ok();
        } else {
            return SysResult.build(201, "已存在", null);
        }
    }

    @RequestMapping("user/manage/save")
    public SysResult userSave(User user) {
        Integer a = userService.checkUserName(user.getUserName());
        if(a>0) {
            return SysResult.build(201, "用户名已存在", null);
        }
        try {
            userService.userSave(user);
            return SysResult.ok();
        } catch (Exception e) {
            e.printStackTrace();
            return SysResult.build(201, e.getMessage(), null);
        }

    }

    @RequestMapping("/user/manage/login")
    public SysResult doLogin(User user, HttpServletRequest request,
                             HttpServletResponse response) {
        //System.out.println("cg");
        String ticket = userService.doLogin(user);

        if(StringUtils.isNotEmpty(ticket)) {
            CookieUtils.setCookie(request, response,"EM_TICKET", ticket);
            return SysResult.ok();
        } else {
            return SysResult.build(201, "登录失败", null);
        }
    }


    @RequestMapping("/user/manage/query/{ticket}")
    public SysResult checkLoginUser(@PathVariable String ticket) {
        String userJson = userService.queryUserJson(ticket);
        //System.out.println("cgtest:"+userJson);

        if(StringUtils.isNotEmpty(userJson)) {
            return SysResult.build(200, "ok", userJson);
        } else {
            return SysResult.build(201, "", null);
        }

    }

}
