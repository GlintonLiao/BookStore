package book.dao;

import book.pojo.User;

public interface UserDAO {
    User getUser(String uname , String pwd );
}
