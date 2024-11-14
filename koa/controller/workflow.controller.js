const workflow = require('../service/workflow.service');
const util = require('../utils/util');

module.exports = {
  async list(ctx) {
    const { userId } = util.decodeToken(ctx);
    const { pageNum = 1, pageSize = 10, keyword = '' } = ctx.request.query;
    const { total } = await workflow.listCount(keyword, userId);
    if (total == 0) {
      return util.success(ctx, {
        list: [],
        total: 0,
        pageSize: +pageSize || 10,
        pageNum: +pageNum || 1,
      });
    }
    const list = await workflow.list(pageNum, pageSize, keyword, userId);
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
      return ctx.throw(400, '模板id不能为空');
    }
    const { userId } = util.decodeToken(ctx);
    const [result = {}] = await workflow.getDetailById(+id, userId);
    util.success(ctx, result);
  },

  async create(ctx) {
    const { formName, formDesc } = ctx.request.body;
    const { userId, userName } = util.decodeToken(ctx);
    if (!userId || !userName) {
      return ctx.throw(400, '账号信息异常，请重新登录');
    }
    if (!formName) {
      return ctx.throw(400, '模板名称不能为空');
    }

    await workflow.createTemplate(formName, formDesc, userId, userName);
    util.success(ctx);
  },

  async delete(ctx) {
    const { id } = ctx.request.params;
    if (!util.isNumber(id)) {
      return ctx.throw(400, '组件id不正确');
    }
    const { userId } = util.decodeToken(ctx);
    await workflow.deleteTemplateById(id, userId);
    util.success(ctx);
  },

  async update(ctx) {
    const { id, formName, formDesc, pageId, templateData } = ctx.request.body;
    if (!util.isNumber(id)) {
      return ctx.throw(400, '组件id不正确');
    }

    const { userId } = util.decodeToken(ctx);

    await workflow.updateTemplate(id, formName, formDesc, pageId, templateData, userId);
    util.success(ctx);
  },
};
