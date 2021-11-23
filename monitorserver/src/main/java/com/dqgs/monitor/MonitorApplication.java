package com.dqgs.monitor;

import com.dqgs.monitor.utils.CommandOperation;
import com.dqgs.monitor.service.GetIpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication
public class MonitorApplication {

    /**
     * 获取ip并修改客户端在线状态对象
     */
    @Autowired
    private GetIpService getIpService;

    public static void main(String[] args) {
        //SpringApplication.run(MonitorApplication.class, args);
        SpringApplicationBuilder builder = new SpringApplicationBuilder(MonitorApplication.class);
        builder.headless(false).run(args);
    }

    @Bean
    public void init() {
        CommandOperation.initKeys();
        //修改在线状态为在线
        getIpService.getIptoDatabase(1);
    }

}
