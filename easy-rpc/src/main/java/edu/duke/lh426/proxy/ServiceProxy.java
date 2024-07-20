package edu.duke.lh426.proxy;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import edu.duke.lh426.RpcApplication;
import edu.duke.lh426.config.RpcConfig;
import edu.duke.lh426.constant.RpcConstant;
import edu.duke.lh426.fault.retry.RetryStrategy;
import edu.duke.lh426.fault.retry.RetryStrategyFactory;
import edu.duke.lh426.fault.tolerant.TolerantStrategy;
import edu.duke.lh426.fault.tolerant.TolerantStrategyFactory;
import edu.duke.lh426.loadbalancer.LoadBalancer;
import edu.duke.lh426.loadbalancer.LoadBalancerFactory;
import edu.duke.lh426.model.RpcRequest;
import edu.duke.lh426.model.RpcResponse;
import edu.duke.lh426.model.ServiceMetaInfo;
import edu.duke.lh426.registry.Registry;
import edu.duke.lh426.registry.RegistryFactory;
import edu.duke.lh426.serializer.Serializer;
import edu.duke.lh426.serializer.SerializerFactory;
import edu.duke.lh426.service.tcp.VertxTcpClient;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ServiceProxy implements InvocationHandler {

    /**
     * 调用代理
     *
     * @return
     * @throws Throwable
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // 构造请求
        String serviceName = method.getDeclaringClass().getName();
        RpcRequest rpcRequest = RpcRequest.builder()
                .serviceName(serviceName)
                .methodName(method.getName())
                .parameterTypes(method.getParameterTypes())
                .args(args)
                .build();

        // 从注册中心获取服务提供者请求地址
        RpcConfig rpcConfig = RpcApplication.getRpcConfig();
        Registry registry = RegistryFactory.getInstance(rpcConfig.getRegistryConfig().getRegistry());
        ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
        serviceMetaInfo.setServiceName(serviceName);
        serviceMetaInfo.setServiceVersion(RpcConstant.DEFAULT_SERVICE_VERSION);
        List<ServiceMetaInfo> serviceMetaInfoList = registry.serviceDiscovery(serviceMetaInfo.getServiceKey());
        if (CollUtil.isEmpty(serviceMetaInfoList)) {
            throw new RuntimeException("暂无服务地址");
        }

        // 负载均衡
        LoadBalancer loadBalancer = LoadBalancerFactory.getInstance(rpcConfig.getLoadBalancer());
        // 将调用方法名（请求路径）作为负载均衡参数
        Map<String, Object> requestParams = new HashMap<>();
        requestParams.put("methodName", rpcRequest.getMethodName());
        ServiceMetaInfo selectedServiceMetaInfo = loadBalancer.select(requestParams, serviceMetaInfoList);
//            // http 请求
//            // 指定序列化器
//            Serializer serializer = SerializerFactory.getInstance(RpcApplication.getRpcConfig().getSerializer());
//            byte[] bodyBytes = serializer.serialize(rpcRequest);
//            RpcResponse rpcResponse = doHttpRequest(selectedServiceMetaInfo, bodyBytes, serializer);
        // rpc 请求
        // 使用重试机制
        RpcResponse rpcResponse;
        try {
            RetryStrategy retryStrategy = RetryStrategyFactory.getInstance(rpcConfig.getRetryStrategy());
            rpcResponse = retryStrategy.doRetry(() ->
                    VertxTcpClient.doRequest(rpcRequest, selectedServiceMetaInfo)
            );
        } catch (Exception e) {
            // 容错机制
            TolerantStrategy tolerantStrategy = TolerantStrategyFactory.getInstance(rpcConfig.getTolerantStrategy());
            Map<String, Object> context = new HashMap<>();
            context.put(selectedServiceMetaInfo.getServiceName(), rpcRequest.getServiceName());
            rpcResponse = tolerantStrategy.doTolerant(context, e);
        }
        return rpcResponse.getData();
    }

    /**
     * 发送 HTTP 请求
     *
     * @param selectedServiceMetaInfo
     * @param bodyBytes
     * @return
     * @throws IOException
     */
    private static RpcResponse doHttpRequest(ServiceMetaInfo selectedServiceMetaInfo, byte[] bodyBytes) throws IOException {
        final Serializer serializer = SerializerFactory.getInstance(RpcApplication.getRpcConfig().getSerializer());
        // 发送 HTTP 请求
        try (HttpResponse httpResponse = HttpRequest.post(selectedServiceMetaInfo.getServiceAddress())
                .body(bodyBytes)
                .execute()) {
            byte[] result = httpResponse.bodyBytes();
            // 反序列化
            RpcResponse rpcResponse = serializer.deserialize(result, RpcResponse.class);
            return rpcResponse;
        }
    }
}
//version1
//ServiceProxy类的主要作用是作为远程过程调用（RPC）的代理，将方法调用转换为HTTP请求并发送给远程服务端，然后将响应结果反序列化并返回给调用者。
//invoke方法是InvocationHandler接口的核心方法，当代理对象的方法被调用时，会执行这个方法。
//它接收三个参数：proxy表示代理对象本身，method表示被调用的方法，args表示方法的参数列表。
//在invoke方法中，首先创建了一个JdkSerializer对象，用于序列化和反序列化数据。

//接下来，构造一个RpcRequest对象，包含了要调用的远程服务的名称、方法名、参数类型和参数值等信息。
//使用serializer对象将RpcRequest对象序列化为字节数组。
//使用HttpRequest对象发送POST请求到远程服务端，并将序列化后的请求体作为请求内容。
//获取远程服务的响应，并将其反序列化为RpcResponse对象。
//最后，从RpcResponse对象中获取返回值，并将其返回给调用者。
//如果在发送请求或处理响应过程中发生异常，将异常信息打印到控制台，并返回null。