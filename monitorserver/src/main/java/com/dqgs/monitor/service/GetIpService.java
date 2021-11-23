package com.dqgs.monitor.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @author by Passer
 * @version 1.0
 * @date 2020/11/6 11:31
 */
@Service
@Slf4j
public class GetIpService {

    /**
     * 获取当前设备ip，并插入数据，判断其是否有效
     *
     * @author Passer
     * @date 2020/11/6 11:38
     * @param status 在线，或者离线
     * @return void
     */
    public void getIptoDatabase(int status) {
        //获取的是本地ip地址//PC-20140317PXKX/192.168.0.121
        InetAddress address = null;
        try {
            address = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            log.error("无法获取当前设备ip地址，错误：{}", e.toString());
        }
        //判断是否获取到ip对象
        if (address == null) {
            return;
        }
        //获取ip地址
        String hostAddress = address.getHostAddress();
        if (status == 0) {
            log.info("修改客户端在线状态为不在线");
        } else if (status == 1) {
            log.info("修改客户端在线状态为在线");
        }

    }
}
