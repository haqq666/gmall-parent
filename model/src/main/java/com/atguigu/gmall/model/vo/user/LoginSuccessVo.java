package com.atguigu.gmall.model.vo.user;

import lombok.Data;

/**
 * @author HaQQ
 * @version 1.0
 * @date 2022/9/7 1:03
 */
@Data
public class LoginSuccessVo {
    private String token;
    private String nickName;
}
