package book.service;

import book.pojo.User;

public interface UserService {
    User login(String uname, String pwd);
}
