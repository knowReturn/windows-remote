package com.dqgs.monitor.config;

import com.dqgs.monitor.service.GetIpService;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ExitCodeGenerator;
import org.springframework.stereotype.Component;

/**
 * 通过实现DisposableBean，在容器退出时执行相关操作
 * 也可以通过在方法上打注解@PreDestroy
 *
 * @author by Passer
 * @version 1.0
 * @date 2020/11/6 11:54
 */
@Component
public class DestroyDisposableBean implements DisposableBean, ExitCodeGenerator{
    /**
     * 在容器退出时，将该客户端修改为不在线状态
     */
    @Autowired
    private GetIpService getIpService;

    @Override
    public void destroy() throws Exception {
        //当容器退出运行时修改客户端为不在线状态
        getIpService.getIptoDatabase(0);
    }

    @Override
    public int getExitCode() {
        return 5;
    }
}
