const connection = require('../sql');

class RoleService {
  // 查询总条数
  async listCount(projectId, name) {
    const statement = "SELECT COUNT(`id`) total FROM roles WHERE project_id = ? and (name LIKE COALESCE(CONCAT('%',?,'%'), name) OR ? IS NULL) ;";
    const [result] = await connection.execute(statement, [projectId, name || null, name || null]);
    return result[0];
  }

  async list(projectId, name, pageNum = 1, pageSize = 10) {
    const offset = (+pageNum - 1) * pageSize + '';
    const limit = pageSize;
    const statement = `SELECT id,project_id projectId,name,half_checked halfChecked,checked,remark,updated_at updatedAt,created_at createdAt,user_id userId,user_name userName FROM roles WHERE project_id = ? and  (name LIKE COALESCE(CONCAT('%',?,'%'), name) OR ? IS NULL) ORDER BY updated_at DESC LIMIT ${offset},${limit};`;
    const [result] = await connection.execute(statement, [projectId, name || null, name || null]);
    return result;
  }

  async listAll(projectId) {
    const statement =
      'SELECT id,project_id projectId,name,half_checked halfChecked,checked,remark,updated_at updatedAt,created_at createdAt,user_id userId,user_name userName FROM roles WHERE project_id = ?;';
    const [result] = await connection.execute(statement, [projectId]);
    return result;
  }

  async create(projectId, name, remark, userId, userName) {
    const statement = 'INSERT INTO roles (project_id, name, remark, user_id, user_name) VALUES (?, ?, ?, ?, ?);';
    const [result] = await connection.execute(statement, [projectId, name, remark, userId, userName]);
    return result;
  }

  async delete(id, projectId) {
    let result = [];
    const statement = 'DELETE FROM roles WHERE id = ? && project_id = ?';
    [result] = await connection.execute(statement, [id, projectId]);

    return result;
  }

  async update(id, projectId, name = '', remark = '') {
    let result = [];
    const statement = 'UPDATE roles SET name = ?, remark = ? WHERE id = ? && project_id = ?';
    [result] = await connection.execute(statement, [name, remark, id, projectId]);
    return result;
  }

  async updateLimits(id, projectId, checked = '', halfChecked = '') {
    let result = [];
    const statement = 'UPDATE roles SET checked = ?, half_checked = ? WHERE id = ? && project_id = ?';
    [result] = await connection.execute(statement, [checked, halfChecked, id, projectId]);
    return result;
  }

  // 根据角色ID查询权限ID
  async getRoleInfo(id) {
    const statement =
      'SELECT id,project_id projectId,name,half_checked halfChecked,checked,remark,updated_at updatedAt,created_at createdAt,user_id userId,user_name userName FROM roles WHERE id = ?';
    const [result] = await connection.execute(statement, [id]);
    return result[0];
  }
}

module.exports = new RoleService();
