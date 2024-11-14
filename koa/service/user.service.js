const connection = require('../sql');

class UserService {
  // 查询总条数
  async findUser(userName, userPwd) {
    const statement = 'SELECT id, user_name as userName FROM users WHERE user_name = ? and user_pwd = ? ;';
    const [result] = await connection.execute(statement, [userName, userPwd]);
    return result[0];
  }

  // 用户注册
  async create(userName, userPwd) {
    const statement = 'INSERT INTO users (user_name, user_pwd) VALUES (?, ?);';
    const [result] = await connection.execute(statement, [userName, userPwd]);
    return result;
  }
  // 用户查找
  async search(userName) {
    const statement = 'select id, user_name as userName from users where user_name = ?;';
    const [result] = await connection.execute(statement, [userName]);
    return result[0];
  }
}
module.exports = new UserService();
