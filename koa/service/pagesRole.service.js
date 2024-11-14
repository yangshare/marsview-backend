const connection = require('../sql');

class PagesRoleService {
  async getPagesRoleList(pageId) {
    const statement = 'SELECT id, page_id as pageId, role, user_id as userId, user_name as userName FROM pages_role WHERE page_id = ?;';
    const [result] = await connection.execute(statement, [pageId]);
    return result;
  }

  async create(type, pageId, role, userId, userName) {
    const statement = 'INSERT INTO pages_role (type, page_id, role, user_id, user_name) VALUES (?, ?, ?, ?, ?);';
    const [result] = await connection.execute(statement, [type, pageId, role, userId, userName]);
    return result;
  }

  // 根据ID删除
  async delete(id) {
    const statement = 'DELETE FROM pages_role WHERE id = ?;';
    const [result] = await connection.execute(statement, [id]);
    return result;
  }

  // 根据页面ID删除
  async deleteByPageId(pageId) {
    const statement = 'DELETE FROM pages_role WHERE page_id = ?;';
    const [result] = await connection.execute(statement, [pageId]);
    return result;
  }
}

module.exports = new PagesRoleService();
