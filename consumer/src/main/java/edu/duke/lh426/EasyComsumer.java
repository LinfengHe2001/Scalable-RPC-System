package edu.duke.lh426;

import edu.duke.lh426.model.User;
import edu.duke.lh426.proxy.ServiceProxyFactory;
import edu.duke.lh426.service.UserService;

public class EasyComsumer {
    public static void main(String[] args) {
        // 动态代理
        UserService userService = ServiceProxyFactory.getProxy(UserService.class);
        User user = new User();
        user.setName("easy-RPC");
        // 调用
        User newUser = userService.getUser(user);
        if (newUser != null) {
            System.out.println(newUser.getName());
        } else {
            System.out.println("user == null");
        }
    }
}
