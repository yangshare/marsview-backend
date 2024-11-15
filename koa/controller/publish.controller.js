const pagesService = require('../service/pages.service');
const publishService = require('../service/publish.service');
const util = require('../utils/util');
module.exports = {
  async list(ctx) {
    const { env, userName, start, end, pageNum, pageSize, pageId } = ctx.request.body;
    const list = await publishService.list(ctx.request.body);
    const total = await publishService.listCount(env, userName, start, end, pageId);
    util.success(ctx, {
      list,
      total,
      pageSize: +pageSize,
      pageNum: +pageNum,
    });
  },

  async create(ctx) {
    const { pageId, env, previewImg } = ctx.request.body;

    if (!util.isNotEmpty(pageId)) {
      return ctx.throw(400, '页面ID不能为空');
    }

    if (!util.isNumber(pageId)) {
      return ctx.throw(400, '页面ID参数错误');
    }

    if (!util.checkEnv(env)) {
      return ctx.throw(400, '发布环境参数错误');
    }

    const { userId, userName } = util.decodeToken(ctx);

    const [pageInfo] = await pagesService.getPageInfoById(+pageId);
    if (!pageInfo || !pageInfo.pageData) {
      return ctx.throw(400, '页面不存在或页面数据为空');
    }
    const result = await publishService.createPublish(pageId, pageInfo.name, pageInfo.pageData, userName, userId, env);

    await pagesService.updatePageState(result.insertId, pageId, env, previewImg);

    util.success(ctx);
  },
};
