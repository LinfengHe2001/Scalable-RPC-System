package edu.duke.lh426;

import edu.duke.lh426.common.UserService;
import edu.duke.lh426.config.RegistryConfig;
import edu.duke.lh426.config.RpcConfig;
import edu.duke.lh426.model.ServiceMetaInfo;
import edu.duke.lh426.registry.LocalRegistry;
import edu.duke.lh426.registry.Registry;
import edu.duke.lh426.registry.RegistryFactory;
import edu.duke.lh426.service.tcp.VertxTcpServer;

/**
 * 服务提供者示例
 */
public class ProviderExample {

    public static void main(String[] args) {
        // 初始化RPC框架
        RpcApplication.init();

        // 注册服务，将UserService类名和服务实现类UserServiceImpl关联起来
        String serviceName = UserService.class.getName();
        LocalRegistry.register(serviceName, UserServiceImpl.class);

        // 获取RPC配置信息
        RpcConfig rpcConfig = RpcApplication.getRpcConfig();
        // 获取注册中心配置信息
        RegistryConfig registryConfig = rpcConfig.getRegistryConfig();
        // 根据注册中心配置创建注册中心实例
        Registry registry = RegistryFactory.getInstance(registryConfig.getRegistry());
        // 创建服务元信息对象，并设置服务名称、主机和端口
        ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
        serviceMetaInfo.setServiceName(serviceName);
        serviceMetaInfo.setServiceHost(rpcConfig.getServerHost());
        serviceMetaInfo.setServicePort(rpcConfig.getServerPort());

        // 尝试将服务注册到注册中心
        try {
            registry.register(serviceMetaInfo);
        } catch (Exception e) {
            // 如果注册失败，抛出运行时异常
            throw new RuntimeException(e);
        }

        // 启动TCP服务，监听8080端口
        VertxTcpServer vertxTcpServer = new VertxTcpServer();
        vertxTcpServer.doStart(8080);
    }
}
