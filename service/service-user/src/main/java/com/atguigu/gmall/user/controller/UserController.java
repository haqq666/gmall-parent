package com.atguigu.gmall.user.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.common.result.ResultCodeEnum;
import com.atguigu.gmall.model.user.UserInfo;
import com.atguigu.gmall.model.vo.user.LoginSuccessVo;
import com.atguigu.gmall.user.service.UserInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author HaQQ
 * @version 1.0
 * @date 2022/9/7 0:36
 */
@RequestMapping("/api/user")
@RestController
public class UserController {

    @Autowired
    UserInfoService userInfoService;

    @PostMapping("/passport/login")
    public Result login(@RequestBody UserInfo userInfo){
       LoginSuccessVo vo = userInfoService.login(userInfo);
       if (vo != null){
           return Result.ok(vo);
       }
       return Result.build("", ResultCodeEnum.LOGIN_ERROR);
    }

    @GetMapping("/passport/logout")
    public Result logout(@RequestHeader("token")String token){
        userInfoService.logout(token);
        return Result.ok();
    }
}
