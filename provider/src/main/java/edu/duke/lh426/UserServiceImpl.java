package edu.duke.lh426;


import edu.duke.lh426.common.User;
import edu.duke.lh426.common.UserService;

public class UserServiceImpl implements UserService {

    public User getUser(User user) {
        System.out.println("成功连接RPC");
        System.out.println("用户名：" + user.getName());
        return user;
    }
}