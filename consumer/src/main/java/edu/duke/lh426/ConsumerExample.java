package edu.duke.lh426;


import edu.duke.lh426.common.User;
import edu.duke.lh426.common.UserService;
import edu.duke.lh426.proxy.ServiceProxyFactory;


/**
 * 服务消费者示例
 */
public class ConsumerExample {

    public static void main(String[] args) {
        // 获取代理
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
        short number = userService.getNumber();
        System.out.println(number);
    }
}
