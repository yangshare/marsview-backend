const menuService = require('../service/menu.service');
const pageService = require('../service/pages.service');
const util = require('../utils/util');
module.exports = {
  async list(ctx) {
    const { name, status, projectId } = ctx.request.body;
    if (!util.isNumber(projectId)) {
      return ctx.throw(400, 'projectId不合法');
    }
    const list = await menuService.list(name, status, projectId);
    util.success(ctx, { list });
  },
  async create(ctx) {
    const { userId, userName } = util.decodeToken(ctx);
    const { projectId, name, type, isCreate } = ctx.request.body;

    if (projectId === 0) {
      return ctx.throw(400, '请先创建项目');
    }
    if (!util.isNumber(projectId)) {
      return ctx.throw(400, 'projectId不合法');
    }

    if (!name) {
      return ctx.throw(400, '菜单名称不能为空');
    }

    try {
      let pageId = 0;
      // 只有菜单和页面类型支持自动创建页面
      if (type !== 2 && isCreate === 1) {
        const res = await pageService.createPage(name, userId, userName, '', '', 1, 2, projectId);
        pageId = res.insertId || 0;
      }

      await menuService.create({ ...ctx.request.body, pageId, userId, userName });
      util.success(ctx);
    } catch (error) {
      util.fail(ctx, error.message);
    }
  },

  async delete(ctx) {
    const { id } = ctx.request.body;
    if (!util.isNotEmpty(id)) {
      return ctx.throw(400, '菜单ID不能为空');
    }
    await menuService.recursionDeleteMenuById(id);
    util.success(ctx);
  },

  async update(ctx) {
    const { id, name } = ctx.request.body;

    if (!util.isNotEmpty(id)) {
      return ctx.throw(400, '菜单ID不能为空');
    }

    if (!name) {
      return ctx.throw(400, '菜单名称不能为空');
    }

    await menuService.update(ctx.request.body);

    util.success(ctx);
  },

  async copy(ctx) {
    const { userId, userName } = util.decodeToken(ctx);
    const { id } = ctx.request.body;

    if (!util.isNotEmpty(id)) {
      return ctx.throw(400, '菜单ID不能为空');
    }

    const [menuInfo] = await menuService.getMenuInfoById(id);

    if (!menuInfo) {
      return ctx.throw(400, '菜单ID不存在');
    }

    await menuService.create({
      ...menuInfo,
      name: `${menuInfo.name}-副本`,
      userId,
      userName,
    });
    util.success(ctx);
  },
};
