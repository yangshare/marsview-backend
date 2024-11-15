const connection = require('../sql');
class PublishService {
  async listCount(env, userName, createdAt, updatedAt, pageId) {
    const statement = `
    SELECT 
      count(id)
    FROM 
      pages_publish
    WHERE
        (env = COALESCE(?, env) OR ? IS NULL)
        AND (user_name = COALESCE(?, user_name) OR ? IS NULL)
        AND (created_at >= ? OR ? IS NULL)
        AND (updated_at <= ? OR ? IS NULL)
        AND (page_id = COALESCE(?, page_id) OR ? IS NULL)
    `;
    const [result] = await connection.execute(statement, [
      env || null,
      env || null,
      userName || null,
      userName || null,
      createdAt || null,
      createdAt || null,
      updatedAt || null,
      updatedAt || null,
      pageId || null,
      pageId || null,
    ]);
    return result.length;
  }

  async list(params) {
    const offset = (+params.pageNum - 1) * params.pageSize + '';
    const limit = params.pageSize;
    const statement = `
    SELECT
      id,
      page_id as pageId,
      page_name as pageName,
      page_data as pageData,
      user_id as userId,
      user_name as userName,
      env,
      created_at as createdAt,
      updated_at as updatedAt
    FROM 
      pages_publish
    WHERE
        (env = COALESCE(?, env) OR ? IS NULL)
        AND (user_name = COALESCE(?, user_name) OR ? IS NULL)
        AND (created_at >= ? OR ? IS NULL)
        AND (updated_at <= ? OR ? IS NULL)
        AND (page_id = COALESCE(?, page_id) OR ? IS NULL)
    ORDER BY id DESC
    LIMIT ${offset},${limit};
    `;
    const [result] = await connection.execute(statement, [
      params.env || null,
      params.env || null,
      params.userName || null,
      params.userName || null,
      params.start || null,
      params.start || null,
      params.end || null,
      params.end || null,
      params.pageId || null,
      params.pageId || null,
    ]);
    return result;
  }

  async createPublish(page_id, page_name, page_data, user_name, user_id, env) {
    const statement = 'INSERT INTO pages_publish (page_id, page_name, page_data, user_name, user_id, env) VALUES (?, ?, ?, ?, ?, ?);';
    const [result] = await connection.execute(statement, [page_id, page_name, page_data, user_name, user_id, env]);
    return result;
  }
}

module.exports = new PublishService();
