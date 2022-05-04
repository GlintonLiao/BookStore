package book.controller;

import book.pojo.User;
import book.service.UserService;

public class UserController {

    private UserService userService;

    public String login(String uname, String pwd ){

        User user = userService.login(uname, pwd);

        System.out.println("user = " + user);
        return "index";
    }
}
