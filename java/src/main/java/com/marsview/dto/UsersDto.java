package com.marsview.dto;

import com.marsview.domain.Users;
import lombok.Data;

/**
 * <p>说明</p>
 *
 * @author yangshare simayifeng@gmail.com
 * createTime: 2024/9/29 07:48
 */
@Data
public class UsersDto extends Users {

    /**
     * 用户名
     */
    private String userName;

    /**
     * 密码
     */
    private String userPwd;

    /**
     * 邮箱地址
     */
    private String email;

    /**
     * 验证码
     */
    private String code;

}
