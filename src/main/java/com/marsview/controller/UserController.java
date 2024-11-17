package com.marsview.controller;

import ch.qos.logback.core.testUtil.RandomUtil;
import com.alibaba.druid.util.StringUtils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.marsview.controller.basic.BasicController;
import com.marsview.controller.basic.ResultResponse;
import com.marsview.domain.Users;
import com.marsview.dto.UsersDto;
import com.marsview.service.UsersService;
import com.marsview.util.Md5Utils;
import com.marsview.util.SessionUtils;
import com.marsview.vo.CommSearchVo;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * <p>类说明</p>
 *
 * @author yangshare simayifeng@gmail.com
 * @createTime: 2024/9/27 10:42
 */
@Tag(name = "用户管理")
@RestController
@RequestMapping("api/user")
public class UserController extends BasicController {

  @Resource
  private JavaMailSender mailSender;

  @Resource
  private RedisTemplate redisTemplate;

  @Autowired
  private UsersService userService;

  @Operation(summary = "用户注册")
  @PostMapping("regist")
  public ResultResponse regist(HttpServletRequest request, HttpServletResponse response, @Parameter(description = "用户信息") @RequestBody UsersDto dto) {
    //从redis获取验证码
    String code = redisTemplate.opsForValue().get("lowcode.register.code" + dto.getUserName()) + "";
    //判断验证码是否正确
    if (StringUtils.equals(dto.getCode(), code)) {
      Users users = new Users();
      users.setUserPwd(dto.getUserPwd());
      users.setUserName(dto.getUserName());
      users.setCreatedAt(new Date());

      boolean result = userService.save(users);
      if (result) {
        SessionUtils.setUser(request, users);
        return getResponse(
          Map.of("userId", users.getId(),
            "userName", users.getUserName(),
            "token", Md5Utils.getMd5(users.getId() + users.getUserName() + users.getUserPwd())
          ));
      } else {
        return getErrorResponse("注册失败");
      }
    } else {
      redisTemplate.delete("lowcode.register.code" + dto.getEmail());
      return getErrorResponse("验证码错误");
    }
  }

  @Operation(summary = "发送邮件")
  @PostMapping("sendEmail")
  public ResultResponse sendEmail(HttpServletResponse response, @Parameter(description = "用户信息") @RequestBody UsersDto dto) {
    SimpleMailMessage message = new SimpleMailMessage();
    int code = RandomUtil.getRandomServerPort();
    //放入缓存，保存3分钟
    redisTemplate.opsForValue().set("lowcode.register.code" + dto.getEmail(), code, 60 * 3, TimeUnit.SECONDS);
    message.setFrom("");
    message.setTo(dto.getEmail());
    message.setSubject("lowcode账号注册");
    String codeStr = "您当前的验证码为：" + code + "，3分钟内有效。感谢您成为lowcode一员。";
    System.out.println(codeStr);
    message.setText(codeStr);
    try {
      mailSender.send(message);
      return getResponse();
    } catch (MailException e) {
      e.printStackTrace();
      return getErrorResponse("系统未配置邮箱发送服务,请联系管理员！");
    }
  }

  @Operation(summary = "登录")
  @PostMapping("login")
  public ResultResponse login(HttpServletRequest request, HttpServletResponse response, @Parameter(description = "用户信息") @RequestBody UsersDto dto) {
    QueryWrapper<Users> wrapper = new QueryWrapper<>();
    wrapper.eq("user_name", dto.getUserName());
    wrapper.eq("user_pwd", dto.getUserPwd());
    Users users = userService.getOne(wrapper);
    if (users != null) {
      SessionUtils.setUser(request, users);
      SessionUtils.setUser(request, users);
      return getResponse(
        Map.of("userId", users.getId(),
          "userName", users.getUserName(),
          "token", Md5Utils.getMd5(users.getId() + users.getUserName() + users.getUserPwd())
        ));
    } else {
      return getErrorResponse("用户名或密码错误");
    }
  }

  @Operation(summary = "获取用户信息")
  @GetMapping("info")
  public ResultResponse info(HttpServletRequest request, HttpServletResponse response) {
    Users users = SessionUtils.getUser(request);
    return getResponse(
      Map.of("userId", users.getId(),
        "userName", users.getUserName(),
        "token", Md5Utils.getMd5(users.getId() + users.getUserName() + users.getUserPwd())
      ));
  }

  @Operation(summary = "搜索用户")
  @PostMapping("search")
  public ResultResponse search(@RequestBody CommSearchVo searchVo) {
    if (org.apache.commons.lang3.StringUtils.isBlank(searchVo.getKeyword())) {
      return getErrorResponse("请输入搜索关键字");
    }
    QueryWrapper<Users> wrapper = new QueryWrapper<>();
    wrapper.eq("user_name", searchVo.getKeyword());
    wrapper.last(" limit 1 ");
    return getResponse(userService.getOne(wrapper));
  }

  public static void main(String[] args) {
    System.out.println(RandomUtil.getRandomServerPort());
    System.out.println(RandomUtil.getRandomServerPort());
    System.out.println(RandomUtil.getRandomServerPort());
  }
}
