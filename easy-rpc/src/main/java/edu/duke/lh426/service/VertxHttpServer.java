package edu.duke.lh426.service;

import io.vertx.core.Vertx;

/**
 * Vertx HTTP 服务器
 */
public class VertxHttpServer implements HttpServer {

    /**
     * 启动服务器
     *
     * @param port
     */
    public void doStart(int port) {
        // 创建 Vert.x 实例
        Vertx vertx = Vertx.vertx();

        // 创建 HTTP 服务器
        io.vertx.core.http.HttpServer server = vertx.createHttpServer();

        // 监听端口并处理请求
        server.requestHandler(new HttpServerHandler());

        // 启动 HTTP 服务器并监听指定端口
        server.listen(port, result -> {
            if (result.succeeded()) {
                System.out.println("Server is now listening on port " + port);
            } else {
                System.err.println("Failed to start server: " + result.cause());
            }
        });
    }
}


//创建一个基于Vert.x框架的HTTP服务器，并启动监听指定端口。
//doStart方法用于启动服务器，接收一个整数参数port，表示要监听的端口号。
//在doStart方法中，首先创建了一个Vert.x实例，用于管理事件循环和执行异步操作。
//然后使用vertx.createHttpServer()方法创建了一个HTTP服务器对象。
//接下来，通过调用server.requestHandler(new HttpServerHandler())方法为服务器设置请求处理器
// 这里使用了自定义的HttpServerHandler类来处理请求。
//最后，通过调用server.listen(port, result -> {...})方法启动HTTP服务器并监听指定端口。
// 如果启动成功，会打印一条消息提示服务器正在监听指定端口；如果启动失败，则会打印错误信息。