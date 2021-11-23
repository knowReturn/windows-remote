package com.dqgs.monitor.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

/**
 * websocket配置类
 *
 * @author by Passer
 * @version 1.0
 * @date 2020/10/28 16:25
 */
@Configuration
@EnableWebSocket
public class WebSocketConfig {

    /**
     * 注入ServerEndpointExporter
     *
     * @author Passer
     * @date 2020/10/28 16:37
     * @return org.springframework.web.socket.server.standard.ServerEndpointExporter
     */
    @Bean
    public ServerEndpointExporter serverEndpoint() {
        return new ServerEndpointExporter();
    }
}
