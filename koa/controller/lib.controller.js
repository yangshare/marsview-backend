const { v4 } = require('uuid');
const libService = require('../service/lib.service');
const util = require('../utils/util');
const { md5Encry } = require('../utils/sign');
const config = require('../config');

module.exports = {
  async list(ctx) {
    const { userId } = util.decodeToken(ctx);
    const { pageNum = 1, pageSize = 10, keyword = '', type = 1 } = ctx.request.query;
    const { total } = await libService.listCount(keyword, type, userId);
    if (total == 0) {
      return util.success(ctx, {
        list: [],
        total: 0,
        pageSize: +pageSize || 10,
        pageNum: +pageNum || 1,
      });
    }
    const list = await libService.list(pageNum, pageSize, keyword, type, userId);
    util.success(ctx, {
      list,
      total,
      pageSize: +pageSize,
      pageNum: +pageNum,
    });
  },

  async installList(ctx) {
    const { userId } = util.decodeToken(ctx);
    const list = await libService.installList(userId);
    util.success(ctx, list);
  },

  async detail(ctx) {
    const { id } = ctx.request.params;
    if (!util.isNotEmpty(id)) {
      return ctx.throw(400, '组件id不能为空');
    }
    const [result = {}] = await libService.getDetailById(+id);
    util.success(ctx, result);
  },

  async create(ctx) {
    const { tag, name, description = '' } = ctx.request.body;
    const { userId, userName } = util.decodeToken(ctx);
    if (!userId || !userName) {
      return ctx.throw(400, '账号信息异常，请重新登录');
    }
    const { total } = await libService.listCount('', 1, userId);
    const message = total > 2 && userId != 50 ? '您当前最多可以创建2个自定义组件' : '';
    if (message) {
      return util.fail(ctx, message);
    }
    if (!tag) {
      return ctx.throw(400, '组件标识不能为空');
    }
    if (/^[a-zA-Z]+$/g.test(tag) === false) {
      return ctx.throw(400, '组件标识只支持英文');
    }
    if (!name) {
      return ctx.throw(400, '组件名称不能为空');
    }

    await libService.createLib('MC' + tag, name, description, userId, userName);
    util.success(ctx);
  },

  async delete(ctx) {
    const { id } = ctx.request.params;
    if (!util.isNumber(id)) {
      return ctx.throw(400, '组件id不正确');
    }
    await libService.deleteLibById(id);
    util.success(ctx);
  },

  async update(ctx) {
    const { id, reactCode, lessCode, configCode, mdCode, hash } = ctx.request.body;
    if (!util.isNumber(id)) {
      return ctx.throw(400, '组件id不正确');
    }

    if (!reactCode) {
      return ctx.throw(400, '源码不能为空');
    }

    if (!configCode) {
      return ctx.throw(400, '组件配置不能为空');
    }
    await libService.updateLib({
      reactCode,
      lessCode,
      configCode,
      mdCode,
      hash,
      id,
    });
    util.success(ctx);
  },

  async publish(ctx) {
    const { libId, reactCompile, configCode, cssCompile, releaseHash } = ctx.request.body;
    if (!util.isNumber(libId)) {
      return ctx.throw(400, '组件id不正确');
    }

    if (!reactCompile) {
      return ctx.throw(400, 'react代码不能为空');
    }

    if (!configCode) {
      return ctx.throw(400, '组件配置不能为空');
    }

    if (!releaseHash) {
      return ctx.throw(400, '缺少hash参数');
    }

    const { userId, userName } = util.decodeToken(ctx);

    const detail = await libService.getPublishByLibId(libId);
    if (detail) {
      if (detail && detail.releaseHash === releaseHash) {
        return ctx.throw(400, '当前已经是最新版本');
      }
      const id = v4();
      const jsName = md5Encry(userId + reactCompile + Date.now()) + '.js';
      const cssName = md5Encry(userId + cssCompile + Date.now()) + '.css';
      const configName = md5Encry(userId + configCode + Date.now()) + '.js';
      await util.uploadString(jsName, reactCompile);
      await util.uploadString(cssName, cssCompile);
      await util.uploadString(configName, configCode);
      await libService.updateLibPublish({
        libId,
        reactUrl: `${config.OSS_CDNDOMAIN1}/libs/${jsName}`,
        cssUrl: `${config.OSS_CDNDOMAIN1}/libs/${cssName}`,
        configUrl: `${config.OSS_CDNDOMAIN1}/libs/${configName}`,
        releaseHash,
      });
    } else {
      const id = v4();
      const jsName = md5Encry(userId + reactCompile + Date.now()) + '.js';
      const cssName = md5Encry(userId + cssCompile + Date.now()) + '.css';
      const configName = md5Encry(userId + configCode + Date.now()) + '.js';
      await util.uploadString(jsName, reactCompile);
      await util.uploadString(cssName, cssCompile);
      await util.uploadString(configName, configCode);
      await libService.publish({
        libId,
        releaseId: id,
        reactUrl: `${config.OSS_CDNDOMAIN1}/libs/${jsName}`,
        cssUrl: `${config.OSS_CDNDOMAIN1}/libs/${cssName}`,
        configUrl: `${config.OSS_CDNDOMAIN1}/libs/${configName}`,
        releaseHash,
        userId,
        userName,
      });
    }

    util.success(ctx);
  },
};
