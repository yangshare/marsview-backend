const connection = require('../sql');
class ProjectsService {
  async listCount(keyword, type, userId) {
    const statement =
      `
      SELECT   
        count(p.id) as total
      FROM 
        projects p  
      LEFT JOIN   
        (select * from pages_role WHERE user_id= ?) pr ON p.id = pr.page_id AND pr.type = 1 
      WHERE 
        ((name like COALESCE(CONCAT('%',?,'%'), name) OR ? IS NULL) AND ` +
      (type == 1 ? 'p.user_id = ?' : 'p.user_id != ?') +
      `
      ) OR pr.page_id IS NOT NULL
      `;
    const [result] = await connection.execute(statement, [userId, keyword || null, keyword || null, userId]);
    return result[0];
  }
  async list(pageNum, pageSize, keyword, type, userId) {
    const offset = (+pageNum - 1) * pageSize + '';
    const limit = pageSize;
    const statement =
      `
      SELECT   
        p.id,
        p.name,
        p.updated_at as updatedAt,
        p.remark,
        p.logo,
        p.user_name as userName,
        p.user_id as userId,
        p.is_public as isPublic,
        SUBSTRING_INDEX(p.user_name, '@', 1) as userName
      FROM 
        projects p  
      LEFT JOIN   
        (select * from pages_role WHERE user_id= ?) pr ON p.id = pr.page_id AND pr.type = 1 
      WHERE 
        ((name like COALESCE(CONCAT('%',?,'%'), name) OR ? IS NULL) AND ` +
      (type == 1 ? 'p.user_id = ?' : 'p.user_id != ?') +
      `
      ) OR pr.page_id IS NOT NULL
      ORDER BY p.updated_at DESC LIMIT ${offset},${limit};`;
    const [result] = await connection.execute(statement, [userId, keyword || null, keyword || null, userId]);
    return result;
  }

  // 自己拥有的项目列表
  async ownList(pageNum, pageSize, userId) {
    const offset = (+pageNum - 1) * pageSize + '';
    const limit = pageSize;
    const statement = `SELECT id, name, remark, logo, user_name as userName, updated_at as updatedAt FROM projects WHERE user_id = ${userId} LIMIT ${offset},${limit};`;
    const [result] = await connection.execute(statement);
    return result;
  }

  // 自己名下项目总数
  async ownListCount(userId) {
    const statement = 'SELECT COUNT(`id`) total FROM projects where user_id = ?;';
    const [result] = await connection.execute(statement, [userId]);
    return result[0];
  }

  async getProjectByName(name) {
    const statement = `SELECT * FROM projects WHERE name = ?;`;
    const [result] = await connection.execute(statement, [name]);
    return result;
  }

  async createProject(params) {
    const statement = 'INSERT INTO projects (name, remark, logo, user_name, user_id,  is_public) VALUES (?, ?, ?, ?, ?, ?);';
    const [result] = await connection.execute(statement, [
      params.name,
      params.remark,
      params.logo,
      params.userName,
      params.userId,
      params.isPublic || 1,
    ]);

    return result;
  }

  async getProjectInfoById(id) {
    const statement =
      "SELECT id,name,remark,logo,user_id as userId,layout,menu_mode as menuMode,menu_theme_color as menuThemeColor,breadcrumb,tag,footer,is_public as isPublic,SUBSTRING_INDEX(user_name, '@', 1) as userName FROM projects WHERE id = ?;";
    const [result] = await connection.execute(statement, [id]);
    return result;
  }

  async deleteProject(id, userId) {
    const statement = 'DELETE FROM projects WHERE id = ? and user_id = ?;';
    const [result] = await connection.execute(statement, [id, userId]);
    return result;
  }

  async updateProjectInfo(params) {
    const statement =
      'UPDATE projects SET name = ?, remark = ?, logo = ?, layout = ?, menu_mode = ?, menu_theme_color = ?, system_theme_color = ?, breadcrumb = ?, tag = ?, footer = ?, is_public = ? WHERE id = ?;';
    const [result] = await connection.execute(statement, [
      params.name,
      params.remark,
      params.logo,
      params.layout,
      params.menuMode,
      params.menuThemeColor,
      params.systemThemeColor,
      params.breadcrumb,
      params.tag,
      params.footer,
      params.isPublic,
      params.id,
    ]);
    return result;
  }
}

module.exports = new ProjectsService();
