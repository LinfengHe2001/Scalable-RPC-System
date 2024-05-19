package edu.duke.lh426;

import edu.duke.lh426.model.User;
import edu.duke.lh426.service.UserService;

public class UserServiceImpl implements UserService {

    public User getUser(User user) {
        System.out.println("用户名：" + user.getName());
        return user;
    }
}