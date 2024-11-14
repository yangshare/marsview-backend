const connection = require('../sql');
class PagesService {
  async listCount(keyword, type, userId) {
    const statement =
      "SELECT COUNT(`id`) total FROM lib WHERE (name like COALESCE(CONCAT('%',?,'%'), name) OR ? IS NULL) AND " +
      (type == 1 ? 'user_id = ?' : 'user_id != ?');
    const [result] = await connection.execute(statement, [keyword || null, keyword || null, userId]);
    return result[0];
  }
  async list(pageNum, pageSize, keyword, type, userId) {
    const offset = (+pageNum - 1) * pageSize + '';
    const limit = pageSize;
    const statement =
      `
      SELECT 
        id,
        tag,
        name,
        description,
        user_id as userId,
        SUBSTRING_INDEX(user_name, '@', 1) as userName,
        updated_at as updatedAt,
        created_at as createdAt
      FROM 
        lib 
      WHERE
        (name LIKE COALESCE(CONCAT('%',?,'%'), name) OR ? IS NULL) AND ` +
      (type == 1 ? 'user_id = ? ' : 'user_id != ? ') +
      `ORDER BY updated_at DESC LIMIT ${offset},${limit};`;
    const [result] = await connection.execute(statement, [keyword || null, keyword || null, userId]);
    return result;
  }

  async installList(userId) {
    const statement = `
    SELECT 
      a.id, 
      a.tag, 
      a.name, 
      a.user_id as userId, 
      SUBSTRING_INDEX(a.user_name, '@', 1) as userName,
      b.release_id as releaseId, 
      b.react_url as reactUrl, 
      b.css_url as cssUrl, 
      b.config_url as configUrl, 
      b.release_hash as releaseHash
    FROM 
      lib as a 
    RIGHT JOIN 
      lib_publish as b 
    ON 
      a.id = b.lib_id 
    WHERE b.user_id = ?
    ORDER BY a.updated_at DESC
    `;
    const [result] = await connection.execute(statement, [userId]);
    return result;
  }

  async createLib(tag, name, description = '', userId, userName) {
    const statement = 'INSERT INTO lib (tag, name, description,user_id,user_name) VALUES (?, ?, ?, ?, ?);';
    const [result] = await connection.execute(statement, [tag, name, description, userId, userName]);
    return result;
  }

  async getDetailById(id) {
    const statement = `
    select 
      a.id,
      a.tag,
      a.name,
      a.description,
      a.react_code as reactCode,
      a.less_code as lessCode,
      a.config_code as configCode,
      a.md_code as mdCode,
      a.hash,
      a.user_id as userId,
      SUBSTRING_INDEX(a.user_name, '@', 1) as userName,
      a.updated_at as updatedAt,
      a.created_at as createdAt,
      b.release_id as releaseId, 
      b.react_url as reactUrl,
      b.css_url as cssUrl,
      b.config_url as configUrl,
      b.release_hash as releaseHash 
    from 
      lib as a 
    left join 
      lib_publish as b 
    ON 
      a.id = b.lib_id 
    where a.id = ?`;
    const [result] = await connection.execute(statement, [id]);
    return result;
  }

  async deleteLibById(id) {
    const statement = 'DELETE FROM lib WHERE id = ?;';
    const [result] = await connection.execute(statement, [id]);
    return result;
  }

  async updateLib(params) {
    let statement = `UPDATE lib SET updated_at = ?`;
    let sql_params = [new Date()];

    if (params.reactCode) {
      statement += `, react_code = ?`;
      sql_params.push(params.reactCode);
    }
    if (params.lessCode) {
      statement += `, less_code = ?`;
      sql_params.push(params.lessCode);
    }
    if (params.configCode) {
      statement += `, config_code = ?`;
      sql_params.push(params.configCode);
    }
    if (params.mdCode) {
      statement += `, md_code = ?`;
      sql_params.push(params.mdCode);
    }

    if (params.hash) {
      statement += `, hash = ?`;
      sql_params.push(params.hash);
    }

    statement += ` WHERE id = ?;`;
    sql_params.push(params.id);
    const [result] = await connection.execute(statement, sql_params);
    return result;
  }

  async publish(params) {
    const statement =
      'INSERT INTO lib_publish (release_id, lib_id, react_url, css_url, config_url, release_hash, user_id, user_name) VALUES (?, ?, ?, ?, ?, ?, ?, ?);';
    const [result] = await connection.execute(statement, [
      params.releaseId,
      params.libId,
      params.reactUrl,
      params.cssUrl,
      params.configUrl,
      params.releaseHash,
      params.userId,
      params.userName,
    ]);
    return result;
  }

  async getPublishByLibId(lib_id) {
    const statement =
      'SELECT id, release_id as releaseId, lib_id as libId, react_url as reactUrl, css_url as cssUrl, config_url as configUrl, release_hash as releaseHash, user_id as userId, user_name as userName, count, updated_at as updatedAt FROM lib_publish WHERE lib_id = ?;';
    const [result] = await connection.execute(statement, [lib_id]);
    return result[0];
  }

  async updateLibPublish(params) {
    let statement = `UPDATE lib_publish SET react_url = ?, config_url = ?, release_hash = ?, count = count + 1`;
    let sql_params = [params.reactUrl, params.configUrl, params.releaseHash];
    if (params.cssUrl) {
      statement += `, css_url = ?`;
      sql_params.push(params.cssUrl);
    }
    statement += ` WHERE lib_id = ?`;
    sql_params.push(params.libId);
    const [result] = await connection.execute(statement, sql_params);
    return result;
  }
}

module.exports = new PagesService();
