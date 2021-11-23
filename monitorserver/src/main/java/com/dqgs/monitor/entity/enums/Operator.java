package com.dqgs.monitor.entity.enums;

import com.alibaba.fastjson.JSONObject;
import com.dqgs.monitor.utils.CommandOperation;
import lombok.extern.slf4j.Slf4j;

import java.awt.*;

/**
 * 通过枚举执行不同的键鼠操作
 *
 * @author by Passer
 * @version 1.0
 * @date 2020/10/30 16:45
 */
@Slf4j
public enum Operator {

    /**
     * 移动鼠标
     */
    MOUSE_MOVE {
        @Override
        public void execute(Robot robot, JSONObject jsonObject) {
            try {
                String[] line = jsonObject.getString("values").split(",");
                robot.mouseMove(Integer.parseInt(line[0]), Integer.parseInt(line[1]));
                robot.delay(5);
            } catch (Exception e) {
                log.error("执行鼠标移动出错:{}", e.toString());
            }
        }
    },

    /**
     * 按下鼠标一个或多个键
     */
    MOUSE_PRESS {
        @Override
        public void execute(Robot robot, JSONObject jsonObject) {
            //获取鼠标键位对应值
            int line = CommandOperation.matchButton(jsonObject.getIntValue("values"));
            //执行鼠标按下操作
            if (line != 0) {
                try {
                    robot.mousePress(line);
                    robot.delay(5);
                } catch (Exception e) {
                    log.error("执行鼠标按下：{}", line + "错误:" + e.toString());
                }
            }
        }
    },

    /**
     * 释放鼠标一个或多个按键
     */
    MOUSE_RELEASE {
        @Override
        public void execute(Robot robot, JSONObject jsonObject) {
            //获取鼠标键位对应值
            int line = CommandOperation.matchButton(jsonObject.getIntValue("values"));
            //执行鼠标释放操作
            if (line != 0) {
                try {
                    robot.mouseRelease(line);
                    robot.delay(5);
                } catch (Exception e) {
                    log.error("执行鼠标释放：{}", line + "错误:" + e.toString());
                }
            }
        }
    },

    /**
     * 滚动鼠标滑轮，正值向下，负值向上
     */
    MOUSE_WHEEL {
        @Override
        public void execute(Robot robot, JSONObject jsonObject) {
            String[] line = jsonObject.getString("values").split(",");
            try {
                //执行滑轮操作时，需先定位鼠标位置在执行滑动
                robot.mouseMove(Integer.parseInt(line[0]), Integer.parseInt(line[1]));
                robot.mouseWheel(Integer.parseInt(line[2]));
                robot.delay(5);
            } catch (Exception e) {
                log.error("执行鼠标滑轮错误：{}", e.toString());
            }
        }
    },

    /**
     * 按下单个按键事件
     */
    KEY_PRESS {
        @Override
        public void execute(Robot robot, JSONObject jsonObject) {
            //此处考虑组合键所以必须，先执行按下操作在执行释放操作
            //该方法判断键值是否处于范围内，返回范围内键值
            int line = CommandOperation.judgmentKey(jsonObject.getString("values"));
            //执行按键按下操作
            if (line != -1) {
                try {
                    robot.keyPress(line);
                    //休眠5ms
                    robot.delay(5);
                } catch (Exception e) {
                    log.error("执行按键按下错误：{}", line + "错误" + e.toString());
                }
            }
        }
    },

    /**
     * 释放单个按键
     */
    KEY_RELEASE {
        @Override
        public void execute(Robot robot, JSONObject jsonObject) {
            //该方法判断键值是否处于范围内，返回范围内键值
            int line = CommandOperation.judgmentKey(jsonObject.getString("values"));
            //执行按键释放操作
            //判断按键值是否有效
            if (line != -1) {
                try {
                    robot.keyRelease(line);
                    //休眠5ms
                    robot.delay(5);
                } catch (Exception e) {
                    log.error("执行释放按键错误：{}", line + "错误" + e.toString());
                }
            }
        }
    };

    public abstract void execute(Robot robot, JSONObject jsonObject);
}
