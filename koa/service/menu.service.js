const connection = require('../sql');

class MenuService {
  async list(name, status, project_id) {
    const statement = `
      SELECT
        id,
        project_id as projectId,
        name,
        parent_id as parentId,
        type,
        icon,
        path,
        page_id as pageId,
        sort_num as sortNum,
        status,
        code,
        user_id as userId,
        user_name as userName,
        updated_at as updatedAt,
        created_at as createdAt
      FROM 
        menu
      WHERE
        (name LIKE COALESCE(CONCAT('%',?,'%'), name) OR ? IS NULL)
      AND 
        (status = COALESCE(?, status) OR ? IS NULL)
      AND project_id = ?;
    `;
    const [result] = await connection.execute(statement, [
      name || null,
      name || null,
      status >= 0 ? status : null,
      status >= 0 ? status : null,
      project_id,
    ]);
    return result;
  }

  async create(params) {
    const statement =
      'INSERT INTO menu (project_id, name, parent_id, type, code, icon, path, page_id, sort_num, status, user_name, user_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);';
    const [result] = await connection.execute(statement, [
      params.projectId,
      params.name,
      params.parentId || null,
      params.type || 1,
      params.code || '',
      params.icon || '',
      params.path || '',
      params.pageId || 0,
      params.sortNum || 1,
      params.status || 1,
      params.userName,
      params.userId,
    ]);
    return result;
  }

  async getMenuInfoById(id) {
    const statement = `
    SELECT 
      id,
      project_id as projectId,
      name,
      parent_id as parentId,
      type,
      icon,
      path,
      page_id as pageId,
      sort_num as sortNum,
      status,
      code,
      user_id as userId,
      user_name as userName,
      updated_at as updatedAt,
      created_at as createdAt
    FROM 
      menu 
    WHERE id = ?;`;
    const [result] = await connection.execute(statement, [id]);
    return result;
  }

  async recursionDeleteMenuById(id) {
    const statement = `DELETE FROM menu WHERE id = ? || parent_id = ?;`;
    const [result] = await connection.execute(statement, [id, id]);
    return result;
  }

  // 根据页面ID删除
  async deleteMenuByProjectId(projectId) {
    const statement = 'DELETE FROM menu WHERE project_id = ?;';
    const [result] = await connection.execute(statement, [projectId]);
    return result;
  }

  async update(params) {
    const statement =
      'UPDATE menu SET name = ?, parent_id = ?, type = ?, code = ?, icon = ?, path = ?, page_id = ?, sort_num = ?, status = ? WHERE id = ?;';
    const [result] = await connection.execute(statement, [
      params.name,
      params.parentId || null,
      params.type || 1,
      params.code || '',
      params.icon || '',
      params.path || '',
      params.pageId || 0,
      params.sortNum || 1,
      params.status || 1,
      params.id,
    ]);
    return result;
  }
}

module.exports = new MenuService();
