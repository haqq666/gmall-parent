package com.atguigu.gmall.user.service.impl;

import com.atguigu.gmall.common.constant.SysRedisConstant;
import com.atguigu.gmall.common.util.Jsons;
import com.atguigu.gmall.common.util.MD5;
import com.atguigu.gmall.model.user.UserInfo;
import com.atguigu.gmall.model.vo.user.LoginSuccessVo;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.gmall.user.service.UserInfoService;
import com.atguigu.gmall.user.mapper.UserInfoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import springfox.documentation.spring.web.json.Json;

import javax.annotation.Resource;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
* @author 乔豆麻担
* @description 针对表【user_info(用户表)】的数据库操作Service实现
* @createDate 2022-09-07 00:32:39
*/
@Service
public class UserInfoServiceImpl extends ServiceImpl<UserInfoMapper, UserInfo>
    implements UserInfoService{

    @Resource
    UserInfoMapper userInfoMapper;

    @Autowired
    StringRedisTemplate redisTemplate;

    @Override
    public LoginSuccessVo login(UserInfo userInfo) {

        LoginSuccessVo vo = new LoginSuccessVo();

        String encrypt = MD5.encrypt(userInfo.getPasswd());

        LambdaUpdateWrapper<UserInfo> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(UserInfo::getLoginName,userInfo.getLoginName())
                .eq(UserInfo::getPasswd,encrypt);
        UserInfo info = userInfoMapper.selectOne(wrapper);


        if (info != null){

            String token = UUID.randomUUID().toString().replace("-", "");
            //账号密码正确
            redisTemplate.opsForValue().set(SysRedisConstant.LOGIN_USER_TOKEN + token,
                    Jsons.toStr(info),7, TimeUnit.DAYS);

            vo.setNickName(info.getNickName());
            vo.setToken(token);

            return vo;
        }
        return null;
    }

    @Override
    public void logout(String token) {
        redisTemplate.delete(token);
    }
}




