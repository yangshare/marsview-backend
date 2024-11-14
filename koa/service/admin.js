const connection = require('../sql');

class AdminService {
  async getProjectConfig(projectId) {
    const statement = `SELECT id,name,remark,logo,user_id as userId,layout,menu_mode as menuMode,menu_theme_color as menuThemeColor,breadcrumb,tag,footer,is_public as isPublic,SUBSTRING_INDEX(user_name, '@', 1) as userName FROM projects WHERE id = ?;`;
    const [result] = await connection.execute(statement, [projectId]);
    return result;
  }
  async getAllMenuList(projectId) {
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
    WHERE project_id = ?;`;
    const [result] = await connection.execute(statement, [projectId]);
    return result;
  }
  async getMenuList(menuIds, roleId) {
    const statement = `
    SELECT 
      m.id,
      m.project_id as projectId,
      m.name,
      m.parent_id as parentId,
      m.type,
      m.icon,
      m.path,
      m.page_id as pageId,
      m.sort_num as sortNum,
      m.status,
      m.code,
      m.user_id as userId,
      m.user_name as userName,
      m.updated_at as updatedAt,
      m.created_at as createdAt  
    FROM 
      menu m  
    JOIN 
      roles r 
    ON m.id in(${menuIds}) > 0 WHERE r.id = ?;`;
    const [result] = await connection.execute(statement, [roleId]);
    return result;
  }
  async getPageDetailById(id) {
    const statement = `
    SELECT
      id,
      name,
      user_id as userId,
      user_name as userName,
      remark,
      is_public as isPublic,
      is_edit as isEdit,
      preview_img as previewImg,
      stg_publish_id as stgPublishId,
      pre_publish_id as prePublishId,
      prd_publish_id as prdPublishId,
      stg_state as stgState,
      pre_state as preState,
      prd_state as prdState,
      project_id as projectId,
      updated_at as updatedAt,
      SUBSTRING_INDEX(user_name, '@', 1) as userName
    FROM 
      pages 
    WHERE id = ?;`;
    const [result] = await connection.execute(statement, [id]);
    return result;
  }
  async getLastPublishInfo(pageId, publishId) {
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
    WHERE page_id = ? && id = ?;`;
    const [result] = await connection.execute(statement, [pageId, publishId]);
    return result;
  }
}

module.exports = new AdminService();
