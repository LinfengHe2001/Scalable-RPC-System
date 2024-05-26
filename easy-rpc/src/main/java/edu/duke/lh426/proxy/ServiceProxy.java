package edu.duke.lh426.proxy;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import edu.duke.lh426.model.RpcRequest;
import edu.duke.lh426.model.RpcResponse;
import edu.duke.lh426.serializer.JdkSerializer;
import edu.duke.lh426.serializer.Serializer;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class ServiceProxy implements InvocationHandler {

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Serializer serializer = new JdkSerializer();

        // 构造请求
        RpcRequest rpcRequest = RpcRequest.builder()
                .serviceName(method.getDeclaringClass().getName())
                .methodName(method.getName())
                .parameterTypes(method.getParameterTypes())
                .args(args)
                .build();
        try {
            // 序列化
            byte[] bodyBytes = serializer.serialize(rpcRequest);
            try (HttpResponse httpResponse = HttpRequest.post("http://localhost:8080")
                    .body(bodyBytes)
                    .execute()) {
                byte[] result = httpResponse.bodyBytes();
                // 反序列化
                RpcResponse rpcResponse = serializer.deserialize(result, RpcResponse.class);
                return rpcResponse.getData();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
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