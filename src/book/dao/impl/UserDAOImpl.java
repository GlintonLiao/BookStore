package book.dao.impl;

import book.dao.UserDAO;
import book.pojo.User;
import myssm.basedao.BaseDAO;

public class UserDAOImpl extends BaseDAO<User> implements UserDAO {
    @Override
    public User getUser(String uname, String pwd) {
        return load("select * from t_user where uname like ? and pwd like ? ", uname, pwd);
    }
}
