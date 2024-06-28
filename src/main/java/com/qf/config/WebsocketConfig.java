package com.qf.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;


@Configuration
@EnableWebSocket
public class WebsocketConfig {
    //这段代码是一个 Spring Boot 配置类中的方法，
    // 用于配置一个 WebSocket 服务器的 Endpoint 导出器。
    // 在 Spring Boot 中，使用 WebSocket 需要创建一个类似于 RESTful API 的端点，
    // 但是不同于 RESTful API，WebSocket 的端点需要通过导出器来注册并暴露给客户端。
    // 使用 ServerEndpointExporter 类可以自动将带有 @ServerEndpoint 注解的类注册为 WebSocket 端点，
    // 并且自动启用 WebSocket 功能。这样客户端就可以通过相应的 URL 连接到该 WebSocket 服务器的端点。
    @Bean
    public ServerEndpointExporter serverEndpointExporter() {
        return new ServerEndpointExporter();
    }
}
