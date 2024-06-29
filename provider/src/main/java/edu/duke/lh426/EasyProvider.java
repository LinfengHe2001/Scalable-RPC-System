package edu.duke.lh426;

import edu.duke.lh426.common.UserService;
import edu.duke.lh426.registry.LocalRegistry;
import edu.duke.lh426.service.HttpServer;
import edu.duke.lh426.service.VertxHttpServer;

public class EasyProvider {
    public static void main(String[] args) {
        RpcApplication.init();
        // 注册服务
        LocalRegistry.register(UserService.class.getName(), UserServiceImpl.class);

        // 启动 web 服务
        HttpServer httpServer = new VertxHttpServer();
        httpServer.doStart(RpcApplication.getRpcConfig().getServerPort());
    }
}
