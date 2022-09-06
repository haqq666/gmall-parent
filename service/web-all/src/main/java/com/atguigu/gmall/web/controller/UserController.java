package com.atguigu.gmall.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author HaQQ
 * @version 1.0
 * @date 2022/9/7 0:48
 */
@Controller
public class UserController {

    @GetMapping("/login.html")
    public String login(@RequestParam("originUrl")String originUrl, Model model){
        model.addAttribute("originUrl",originUrl);
        return "login";
    }

}
