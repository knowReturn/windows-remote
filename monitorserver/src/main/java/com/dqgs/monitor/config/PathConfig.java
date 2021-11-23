package com.dqgs.monitor.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.system.ApplicationHome;
import org.springframework.context.annotation.Configuration;

import java.io.File;

/**
 * 获取和生成截图程序路径
 *
 * @author by Passer
 * @version 1.0
 * @date 2020/11/2 17:38
 */
@Configuration
public class PathConfig {

    /**
     * 截屏程序路径
     */
    @Value("${screen.exe.name}")
    private String exeName;

    /**
     * 记录录屏默认路径，可选
     */
    @Value("${screen.record.viedo}")
    private String recordVideo;

    /**
     * 获取当前项目路径
     *
     * @return java.lang.String
     * @author Passer
     * @date 2020/11/2 17:42
     */
    public String getExePath() {
        StringBuilder stringBuilder = new StringBuilder();
        //获取jar包路径
        ApplicationHome h = new ApplicationHome(getClass());
        File jarF = h.getSource();
        stringBuilder.append(jarF.getParentFile().toString());
        stringBuilder.append(exeName);
        return stringBuilder.toString();
    }

    /**
     * 记录录屏路径
     *
     * @return java.lang.String
     * @author Passer
     * @date 2020/11/6 14:08
     */
    public StringBuilder getRecordVideo() {
        StringBuilder stringBuilder = new StringBuilder();
        //获取jar包所在路径
        ApplicationHome h = new ApplicationHome(getClass());
        File jarF = h.getSource();
        stringBuilder.append(jarF.getParentFile().toString());
        stringBuilder.append(recordVideo);
        return stringBuilder;
    }

    /**
     * 获取截图软件名称
     * 用于关闭该软件进程
     *
     * @return java.lang.String
     * @author Passer
     * @date 2020/11/6 14:10
     */
    public String getExeName() {
        return exeName.substring(exeName.lastIndexOf("\\") + 1);
    }
}
