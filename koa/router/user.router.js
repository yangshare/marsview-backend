const Router = require('@koa/router');
const router = new Router({ prefix: '/api/user' });
const util = require('../utils/util');
const userService = require('../service/user.service');
const nodemailer = require('nodemailer');
const config = require('../config');
const { Keyv } = require('keyv');
const keyv = new Keyv();
/**
 * 用户登录
 */
router.post('/login', async (ctx) => {
  const { userName, userPwd } = ctx.request.body;
  if (!userName || !userPwd) {
    util.fail(ctx, '用户名或密码不能为空');
    return;
  }
  const res = await userService.findUser(userName, userPwd);
  if (!res) {
    util.fail(ctx, '用户名或密码错误');
    return;
  }
  const nickName = userName.split('@')[0];
  const token = util.createToken({ userName, userId: res.id });
  userService.updateLoginTime(res.id, nickName);
  util.success(ctx, {
    userId: res.id,
    userName,
    token,
  });
});

/**
 * 获取用户信息
 */
router.get('/info', async (ctx) => {
  const { userId, userName } = util.decodeToken(ctx);
  util.success(ctx, {
    userId,
    userName,
  });
});

/**
 * 用户搜索
 */
router.post('/search', async (ctx) => {
  const { keyword } = ctx.request.body;
  if (!keyword) {
    util.fail(ctx, '关键字不能为空');
    return;
  }
  const res = await userService.search(keyword);
  if (!res) {
    util.fail(ctx, '当前用户名不存在');
    return;
  }
  util.success(ctx, res);
});

/**
 * 用户注册
 */
router.post('/sendEmail', async (ctx) => {
  try {
    const { email } = ctx.request.body;
    if (!email || !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email)) {
      util.fail(ctx, '邮箱不能为空或格式错误');
      return;
    }
    const val = await keyv.get(email);
    if (val) {
      util.fail(ctx, '验证码已发送，请查收');
      return;
    }
    let transporter = nodemailer.createTransport({
      host: config.EMAIL_HOST,
      port: config.EMAIL_PORT,
      auth: {
        user: config.EMAIL_USER, // 你的Gmail地址
        pass: config.EMAIL_PASSWORD, // 你的Gmail密码或应用专用密码
      },
    });

    const random = Math.random().toString().slice(2, 7);

    let mailOptions = {
      from: `"Marsview" <${config.EMAIL_USER}>`, // 发送者地址
      to: email, // 接收者列表
      subject: 'Marsview账号注册', // 主题行
      text: '验证码发送', // 纯文字正文
      html: `当前验证码为：<b>${random}</b>，3分钟内有效。<br/><br/>我是 Marsview 开源作者，感谢您注册成为 Marsview 用户，您在搭建过程中遇到任何问题均可邮件、issue或者微信联系我。<br/><br/>邮  箱：sunnyboysoft@163.com<br/>issue：https://github.com/JackySoft/marsview/issues<br/>微  信：MarsOne666`, // HTML正文
    };

    await transporter.sendMail(mailOptions);
    await keyv.set(email, random, 3 * 60 * 1000);
    util.success(ctx, '发送成功');
  } catch (error) {
    util.fail(ctx, error.message);
  }
});

/**
 * 用户注册
 */
router.post('/regist', async (ctx) => {
  const { userName, code, userPwd } = ctx.request.body;
  if (!userName || !userPwd) {
    util.fail(ctx, '用户名或密码不能为空');
    return;
  }
  if (!code) {
    util.fail(ctx, '邮箱验证码不能为空');
    return;
  }
  const val = await keyv.get(userName);
  if (!val) {
    util.fail(ctx, '验证码已过期');
    return;
  }
  if (val != code) {
    util.fail(ctx, '验证码错误');
    return;
  }
  const user = await userService.search(userName);
  if (user) {
    util.fail(ctx, '当前用户已存在');
    return;
  }

  const nickName = userName.split('@')[0];
  const res = await userService.create(nickName, userName, userPwd);
  if (res.affectedRows == 1) {
    const token = util.createToken({ userName, userId: res.insertId });

    util.success(ctx, {
      userId: res.id,
      userName,
      token,
    });
  } else {
    util.fail(ctx, '注册失败,请重试');
  }
});

module.exports = router;
