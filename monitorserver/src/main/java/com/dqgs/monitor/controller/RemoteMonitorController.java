package com.dqgs.monitor.controller;

import com.dqgs.monitor.config.PathConfig;
import com.dqgs.monitor.entity.enums.ScreenRecordType;
import com.dqgs.monitor.service.WsServerEndpoint;
import com.dqgs.monitor.utils.Calculator;
import com.dqgs.monitor.utils.CommandOperation;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author by Passer
 * @version 1.0
 * @date 2020/10/28 17:52
 */

@Api(tags = "远程监控")
@Slf4j
@RestController
@RequestMapping("/remotemonitor")
public class RemoteMonitorController {
    /**
     * 获取截图程序路径
     */
    @Autowired
    private PathConfig pathConfig;
    /**
     * websocket对象
     */
    @Autowired
    private WsServerEndpoint wsServerEndpoint;

    /**
     * 开启远程控制
     * 开启实时传输屏幕信息程序
     * 开启处理客户端键鼠操作
     *
     * @return void
     * @author Passer
     * @date 2020/11/6 14:35
     */
    @ApiOperation(value = "开启远程监控")
    @GetMapping("/start")
    public void getMonitoringScreen() throws InterruptedException {
        //先执行关闭操作保证程序处于关闭状态
        getMonitoringScreenStop(0);
        //休眠保证，以上操作完成，避免误杀相关进程
        Thread.sleep(500);

        //运行程序
        //此处不能以线程形式运行该程序，如何会造成运行一段时间后，该exe不工作，所以采用以桌面打开方式运行
        try {
            Desktop.getDesktop().open(new File(pathConfig.getExePath()));
        } catch (IOException e) {
            log.error("开启实时传输屏幕信息程序出错：{}", e.toString());

            //发送开启远程监控失败消息
            try {
                wsServerEndpoint.sendInfo(Calculator.getDefinitionMessage(false, ScreenRecordType.valueOf("REMOTE_MONITOR_START")));
            } catch (IOException ex) {
                log.error("websocket发送(开启远程监控错误消息)错误：{}", ex.toString());
            }
            //失败不进行下面操作直接返回
            return;
        }
        //开启处理客户机键鼠事件
        CommandOperation.setRemoteMonitorType(new AtomicBoolean(true));

        //发送开启远程监控成功消息
        try {
            wsServerEndpoint.sendInfo(Calculator.getDefinitionMessage(true, ScreenRecordType.valueOf("REMOTE_MONITOR_START")));
        } catch (IOException ex) {
            log.error("websocket发送(开启远程监控成功消息)错误：{}", ex.toString());
        }
    }

    /**
     * 关闭键鼠操作
     * 关闭实时传输图片信息程序
     *
     * @param status 0代表内部调用，不发送成功与否消息，1外部调用需要发送,可以不传输该字段，为空默认外部调用
     * @author Passer
     * @date 2020/10/30 18:42
     */
    @ApiOperation(value = "关闭远程监控")
    @GetMapping("/stop")
    public void getMonitoringScreenStop(@RequestParam(required = false) int status) {
        //关闭程序
        try {
            Runtime.getRuntime().exec("cmd /c " + "taskkill /F /IM " + pathConfig.getExeName());
            //关闭键鼠事件处理
            CommandOperation.setRemoteMonitorType(new AtomicBoolean(false));
            //发送关闭远程监控成功消息
            if (status != 0) {
                try {
                    wsServerEndpoint.sendInfo(Calculator.getDefinitionMessage(true, ScreenRecordType.valueOf("REMOTE_MONITOR_STOP")));
                } catch (IOException ex) {
                    log.error("websocket发送(关闭远程监控成功消息)错误：{}", ex.toString());
                }
            }
        } catch (Exception e) {
            log.error("关闭实时传输屏幕信息程序失败:{}", e.toString());
            //发送关闭远程监控失败消息
            if (status != 0) {
                try {
                    wsServerEndpoint.sendInfo(Calculator.getDefinitionMessage(false, ScreenRecordType.valueOf("REMOTE_MONITOR_STOP")));
                } catch (IOException ex) {
                    log.error("websocket发送(关闭远程监控成功消息)错误：{}", ex.toString());
                }
            }
        }
    }

}
