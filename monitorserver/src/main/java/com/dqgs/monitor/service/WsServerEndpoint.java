package com.dqgs.monitor.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dqgs.monitor.utils.Calculator;
import com.dqgs.monitor.entity.enums.Operator;
import com.dqgs.monitor.utils.CommandOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.awt.*;
import java.io.IOException;
import java.io.Serializable;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author by Passer
 * @version 1.0
 * @date 2020/10/28 16:38
 */

@ServerEndpoint("/myWs/screen")
@Service
@Slf4j
public class WsServerEndpoint implements Serializable {
    /**
     * concurrent包的线程安全set，用来存放每个客户端对应的MyWebSocket对象
     */
    private static final CopyOnWriteArraySet<WsServerEndpoint> WRITE_ARRAY_SET = new CopyOnWriteArraySet<>();
    /**
     * 静态变量，用来记录当前在线连接数，该值具有原子性
     */
    private static final AtomicInteger ONLINE_COUNT = new AtomicInteger(0);
    /**
     * 与某个客户端的连接会话，需要通过它来给客户端发送数据
     */
    private Session session;
    /**
     * 初始化机器人类对象
     */
    private final Robot robot = CommandOperation.initRobot();

    /**
     * 鼠标键盘事件处理对象
     */
    private static Calculator calculator = new Calculator();

    /**
     * 连接成功，
     * 建立成功调用方法
     *
     * @param session 会话对象
     * @author Passer
     * @date 2020/10/28 16:41
     */
    @OnOpen
    public void onOpen(Session session) {
        this.session = session;
        //存储当前会话
        WRITE_ARRAY_SET.add(this);
        //在线数自增，加1
        ONLINE_COUNT.getAndIncrement();
        log.info("新连接加入：{}", ONLINE_COUNT);
    }

    /**
     * 连接关闭调用方法
     *
     * @param session 会话对象
     * @author Passer
     * @date 2020/10/28 17:10
     */
    @OnClose
    public void onClose(Session session) {
        //当连接关闭时，删除当前会话记录
        WRITE_ARRAY_SET.remove(this);
        //在线人数自减
        ONLINE_COUNT.getAndDecrement();
        log.info("websocket连接关闭，当前连接数：{}", ONLINE_COUNT);
    }

    /**
     * 接收到的消息
     * 对消息进行处理
     *
     * @param message 消息内容
     * @author Passer
     * @date 2020/10/28 16:49
     */
    @OnMessage
    public void onMessage(String message) {
        JSONObject jsonObject = JSON.parseObject(message);
        log.info("客户端发送消息：{}", message);
        if (CommandOperation.remoteMonitorType != null && CommandOperation.remoteMonitorType.get() && jsonObject.containsKey("type")) {
            calculator.calculator(robot, jsonObject, Operator.valueOf(jsonObject.getString("type")));
        }
    }

    /**
     * 连接发生错误时处理
     *
     * @param error 错误对象
     * @author Passer
     * @date 2020/10/28 17:20
     */
    @OnError
    public void onError(Throwable error) {
        log.error("websocket连接发生错误！");
        error.printStackTrace();
    }

    /**
     * 发送消息到服务器
     *
     * @param o 需要发送字符串
     * @return void
     * @author Passer
     * @date 2020/10/28 17:23
     */
    public void sendInfo(String o) throws IOException {
        for (WsServerEndpoint wsServerEndpoint : WRITE_ARRAY_SET) {
            wsServerEndpoint.sendMessage(o);
        }
    }

    public void sendMessage(String o) throws IOException {
        this.session.getBasicRemote().sendText(o);
    }
}
