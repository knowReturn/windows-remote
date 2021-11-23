package com.dqgs.monitor.utils;

import com.alibaba.fastjson.JSONObject;
import com.dqgs.monitor.entity.enums.Operator;
import com.dqgs.monitor.entity.enums.ScreenRecordType;
import org.springframework.scheduling.annotation.Async;

import java.awt.*;

/**
 * 封装枚举方法
 * 方便调用
 *
 * @author by Passer
 * @version 1.0
 * @date 2020/10/30 17:52
 */
public class Calculator {
    /**
     * 处理监控键鼠事件
     *
     * @param robot      机器人类
     * @param jsonObject 键鼠操作命令
     * @param operator 需要调用的枚举类方法对象
     */
    @Async
    public void calculator(Robot robot, JSONObject jsonObject, Operator operator) {
        operator.execute(robot, jsonObject);
    }

    /**
     * 获取操作成功与否消息体
     *
     * @author Passer
     * @date 2020/11/6 15:54
     * @param bo 操作成功或者失败
     * @param screenRecordType 需要调用的枚举类方法对象
     * @return java.lang.String
     */
    public static String getDefinitionMessage(boolean bo, ScreenRecordType screenRecordType) {
        return screenRecordType.info(bo);
    }
}
