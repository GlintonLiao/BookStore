package book.service.impl;

import book.dao.UserDAO;
import book.pojo.User;
import book.service.UserService;

public class UserServiceImpl implements UserService {

    private UserDAO userDAO;

    @Override
    public User login(String uname, String pwd) {
        return userDAO.getUser(uname,pwd);
    }
}
