const pageService = require('../service/pages.service');
const pagesRoleService = require('../service/pagesRole.service');
const userService = require('../service/user.service');
const util = require('../utils/util');

module.exports = {
  async list(ctx) {
    const { userId } = util.decodeToken(ctx);
    const { pageNum, pageSize, keyword, type = 1 } = ctx.request.query;
    const { total } = await pageService.listCount(keyword, type, userId);
    if (total == 0) {
      return util.success(ctx, {
        list: [],
        total: 0,
        pageSize: +pageSize || 12,
        pageNum: +pageNum || 1,
      });
    }
    const list = await pageService.list(pageNum || 1, pageSize || 12, keyword, type, userId);

    util.success(ctx, {
      list,
      total,
      pageSize: +pageSize,
      pageNum: +pageNum,
    });
  },

  async listPageTemplate(ctx) {
    const { pageNum, pageSize, keyword } = ctx.request.query;
    const { total } = await pageService.listPageTemplateCount(keyword);
    if (total == 0) {
      return util.success(ctx, {
        list: [],
        total: 0,
        pageSize: +pageSize || 12,
        pageNum: +pageNum || 1,
      });
    }
    const list = await pageService.listPageTemplate(pageNum || 1, pageSize || 12, keyword);
    util.success(ctx, {
      list,
      total,
      pageSize: +pageSize,
      pageNum: +pageNum,
    });
  },

  async detail(ctx) {
    const { id } = ctx.request.params;
    if (!util.isNotEmpty(id)) {
      return ctx.throw(400, '页面ID不能为空');
    }
    const [pageInfo] = await pageService.getPageInfoById(+id);
    util.success(ctx, pageInfo || {});
  },

  async copy(ctx) {
    const { id } = ctx.request.body;
    if (!util.isNotEmpty(id)) {
      return ctx.throw(400, '页面ID不能为空');
    }
    const [pageInfo] = await pageService.getPageInfoById(+id);
    if (!pageInfo) {
      return util.fail(ctx, '页面不存在');
    }
    const { userId, userName } = util.decodeToken(ctx);
    const { name, remark, pageData, isPublic = 1, isEdit = 1 } = pageInfo;
    await pageService.createPage(name + '-副本', userId, userName, remark, pageData, isPublic == 3 ? 1 : isPublic, isEdit);
    util.success(ctx);
  },

  async delete(ctx) {
    const { id } = ctx.request.body;
    if (!util.isNotEmpty(id)) {
      return ctx.throw(400, '页面ID不能为空');
    }
    const { userId } = util.decodeToken(ctx);
    const [pageInfo] = await pageService.getPageSimpleById(+id);
    if (!pageInfo || pageInfo.userId !== userId) {
      return util.fail(ctx, '您暂无权限删除该页面');
    }
    const res = await pageService.deletePage(id, userId);
    await pagesRoleService.deleteByPageId(id);
    if (res.affectedRows > 0) {
      util.success(ctx);
    } else {
      return ctx.throw(400, '当前暂无权限');
    }
  },

  async create(ctx) {
    const { userId, userName } = util.decodeToken(ctx);
    const { name, remark, isPublic = 1, isEdit = 1 } = ctx.request.body;
    if (!name) {
      return ctx.throw(400, '页面名称不能为空');
    }

    await pageService.createPage(name, userId, userName, remark, '', isPublic, isEdit);
    util.success(ctx);
  },

  async update(ctx) {
    const { id, name, remark = '', pageData, isPublic = 1, isEdit = 1, previewImg = '' } = ctx.request.body;
    if (!util.isNotEmpty(id)) {
      return ctx.throw(400, '页面ID不能为空');
    }

    if (!name) {
      return ctx.throw(400, '页面名称不能为空');
    }
    const { userId } = util.decodeToken(ctx);
    const [pageInfo] = await pageService.getPageSimpleById(+id);
    if (!pageInfo) {
      return util.fail(ctx, '当前页面不存在');
    }
    // 只读权限的页面只有创建者才能编辑
    if (pageInfo.isEdit === 2 && pageInfo.userId !== userId) {
      return util.fail(ctx, '您当前暂无编辑权限');
    }
    // 模板页面只有管理员才能编辑
    if (pageInfo.isPublic === 3 && pageInfo.userId !== userId) {
      return util.fail(ctx, '您当前暂无编辑权限');
    }
    await pageService.updatePageInfo(name, remark, pageData, isPublic, isEdit, id, previewImg);
    util.success(ctx);
  },

  // 页面角色 - 成员列表
  async roleList(ctx) {
    const { pageId } = ctx.request.body;
    if (!pageId) {
      return ctx.throw(400, '页面ID不能为空');
    }
    const list = await pagesRoleService.getPagesRoleList(pageId);
    util.success(ctx, { list });
  },

  /**
   * 页面或者项目 - 添加成员
   * page_id: 页面ID或者项目ID，共用同一张表
   */
  async roleAdd(ctx) {
    const { type, pageId, role, userName } = ctx.request.body;
    if (!type) {
      return ctx.throw(400, '成员类型不能为空');
    }

    if (!pageId || isNaN(+pageId)) {
      return ctx.throw(400, '页面ID或项目ID不能为空');
    }

    if (!role) {
      return ctx.throw(400, '角色不能为空');
    }

    if (!userName) {
      return ctx.throw(400, '开发者ID或名称不能为空');
    }
    const res = await userService.search(userName);
    if (!res) {
      return ctx.throw(400, '当前用户不存在');
    }
    await pagesRoleService.create(type, pageId, role, res.id, userName);
    util.success(ctx);
  },

  // 删除页面成员
  async roleDelete(ctx) {
    const { id } = ctx.request.body;
    if (!util.isNumber(id)) {
      return ctx.throw(400, 'ID不能为空');
    }
    await pagesRoleService.delete(id);
    util.success(ctx);
  },

  // 页面回滚
  async rollback(ctx) {
    const { pageId, lastPublishId, env } = ctx.request.body;
    if (!util.isNotEmpty(pageId)) {
      return ctx.throw(400, '页面ID不能为空');
    }

    if (!util.isNotEmpty(lastPublishId)) {
      return ctx.throw(400, '回滚ID不能为空');
    }

    if (!util.checkEnv(env)) {
      return ctx.throw(400, '环境不能为空');
    }
    await pageService.updateLastPublishId(pageId, lastPublishId, env);
    util.success(ctx);
  },
};
