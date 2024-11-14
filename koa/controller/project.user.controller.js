const userService = require('../service/project.user.service');
const util = require('../utils/util');
module.exports = {
  // 用户列表
  async list(ctx) {
    const { projectId, userName = null, pageNum, pageSize } = ctx.request.query;
    if (!projectId || projectId == 0) {
      return ctx.throw(400, '项目ID不能为空');
    }
    const list = await userService.list(projectId, userName, pageNum, pageSize);
    const { total } = await userService.listCount(projectId, userName);

    util.success(ctx, {
      list,
      total,
      pageSize: +pageSize,
      pageNum: +pageNum,
    });
  },
  // 用户创建
  async create(ctx) {
    const params = ctx.request.body;
    if (!params.projectId) {
      return ctx.throw(400, '项目ID不能为空');
    }

    if (!params.userId || !params.userName) {
      return ctx.throw(400, '用户ID和用户名不能为空');
    }

    // 1：管理员， 2：普通用户，如果是普通用户，必须传入角色id
    if (!params.systemRole || params.systemRole > 9) {
      return ctx.throw(400, '系统角色不能为空');
    }

    // 如果是普通用户，必须传入角色id
    if (params.systemRole == 2 && !params.roleId) {
      return ctx.throw(400, '用户角色不能为空');
    }

    if (params.systemRole == 1) {
      params.roleId = 0;
    }

    const user = await userService.getUserRole(params.userId, params.projectId);
    if (user) {
      return ctx.throw(400, '该用户已存在');
    }
    await userService.createUser(params);
    util.success(ctx);
  },

  // 用户删除
  async delete(ctx) {
    const { id } = ctx.request.body;
    if (!util.isNumber(id)) {
      return ctx.throw(400, '用户ID不能为空');
    }
    await userService.deleteUser(id);
    util.success(ctx);
  },

  // 用户更新
  async update(ctx) {
    const params = ctx.request.body;
    if (!util.isNumber(params.id)) {
      return ctx.throw(400, '用户ID不能为空');
    }

    if (!params.systemRole) {
      return ctx.throw(400, '系统角色不能为空');
    }

    // 1：管理员， 2：普通用户，如果是普通用户，必须传入角色id
    if (params.systemRole == 2 && !params.roleId) {
      return ctx.throw(400, '用户角色不能为空');
    }
    if (params.systemRole == 1) {
      params.roleId = 0;
    }
    await userService.updateUser(params);
    util.success(ctx);
  },

  // 用户详情
  async detail(ctx) {
    const { id } = ctx.request.query;
    if (!util.isNumber(id)) {
      return ctx.throw(400, '用户ID不能为空');
    }
    const userInfo = await userService.detail(+id);
    util.success(ctx, userInfo);
  },
};
