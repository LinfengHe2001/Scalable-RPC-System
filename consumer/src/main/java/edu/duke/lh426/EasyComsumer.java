package edu.duke.lh426;

import edu.duke.lh426.common.User;
import edu.duke.lh426.common.UserService;
import edu.duke.lh426.proxy.ServiceProxyFactory;


public class EasyComsumer {
    public static void main(String[] args) {
        // 动态代理
        UserService userService = ServiceProxyFactory.getProxy(UserService.class);
        User user = new User();
        user.setName("helinfeng");
        // 调用
        User newUser = userService.getUser(user);
        if (newUser != null) {
            System.out.println(newUser.getName());
        } else {
            System.out.println("user == null");
        }
    }
}
