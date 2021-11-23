package com.dqgs.monitorserver;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dqgs.monitorserver.utils.properties.LoadProperties;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest()
class MonitorserverApplicationTests {

    @Test
    void fun2() {
        String jsong = "{\"type\":\"command\",\"command\":\"request-control\",\"sessionId\":\"1\"}";
        JSONObject jsonObject = JSON.parseObject(jsong);
        System.out.println(jsonObject.containsKey("path"));
    }

    @Test
    void fun3() {
        System.out.println(LoadProperties.load("monitoring.rds.port"));
    }


}
