const rolesService = require('../service/roles.service');
const util = require('../utils/util');
module.exports = {
  async list(ctx) {
    const { projectId, name = null, pageNum = 1, pageSize = 10 } = ctx.request.query;

    if (!projectId) {
      return ctx.throw(400, '项目ID不能为空');
    }
    const list = await rolesService.list(projectId, name, pageNum, pageSize);
    const { total } = await rolesService.listCount(projectId, name);

    util.success(ctx, {
      list,
      total,
      pageSize: +pageSize,
      pageNum: +pageNum,
    });
  },

  async listAll(ctx) {
    const { projectId } = ctx.request.query;
    if (!projectId || projectId == '0') {
      return ctx.throw(400, '项目ID不能为空');
    }
    const list = await rolesService.listAll(projectId);
    util.success(ctx, list);
  },

  async create(ctx) {
    const { projectId, name, remark } = ctx.request.body;
    if (!name) {
      return ctx.throw(400, '角色名称不能为空');
    }

    if (projectId == '0' || !projectId) {
      return ctx.throw(400, '请先创建项目');
    }

    if (!util.isNumber(projectId)) {
      return ctx.throw(400, 'projectId不合法');
    }

    const { userId, userName } = util.decodeToken(ctx);

    await rolesService.create(projectId, name, remark, userId, userName);
    util.success(ctx);
  },

  async delete(ctx) {
    const { id, projectId } = ctx.request.body;
    if (!id) {
      return ctx.throw(400, '角色ID不能为空');
    }

    if (!projectId) {
      return ctx.throw(400, '项目ID不能为空');
    }
    await rolesService.delete(id, projectId);
    util.success(ctx);
  },

  async update(ctx) {
    const { id, projectId, name, remark } = ctx.request.body;
    if (!id) {
      return ctx.throw(400, '角色ID不能为空');
    }

    if (!projectId) {
      return ctx.throw(400, '项目ID不能为空');
    }
    await rolesService.update(id, projectId, name, remark);
    util.success(ctx);
  },

  async updateLimits(ctx) {
    const { id, projectId, checked = '', halfChecked = '' } = ctx.request.body;
    if (!id) {
      return ctx.throw(400, '角色ID不能为空');
    }

    if (!projectId) {
      return ctx.throw(400, '项目ID不能为空');
    }
    await rolesService.updateLimits(id, projectId, checked, halfChecked);
    util.success(ctx);
  },
};
