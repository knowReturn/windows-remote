package com.dqgs.monitor.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * @author by Passer
 * @version 1.0
 * @date 2020/10/28 14:36
 */
@Slf4j
@Component
public class ScreenshotService {

    /**
     * 指定捕获屏幕区域，这里使用全屏捕获
     */
    private static Rectangle rectangle;
    /**
     * 本地环境
     */
    private static GraphicsEnvironment graphicsEnvironment;
    /**
     * Java硬件设备操作类
     */
    private static Robot robot;
    /**
     * 是否开始发送截图
     */
    private boolean flag;

    /**
     * 帧率
     */
    private int rate = 60;

    public static void init() {
        //获取当前屏幕大小
        Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
        //指定捕获屏幕区域，这里全屏
        rectangle = new Rectangle(dimension);
        //本地环境
        graphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment();
    }

    /**
     * 设置截屏屏幕，即帧率；
     *
     * @param rate 帧率
     * @author Passer
     * @date 2020/10/28 15:33
     */
    public void setRate(int rate) {
        this.rate = rate;
    }

    /**
     * 获取本地环境有几个屏幕
     *
     * @return java.awt.GraphicsDevice[]
     * @author Passer
     * @date 2020/10/28 14:45
     */
    public GraphicsDevice[] getGraphicsDevices() {
        return graphicsEnvironment.getScreenDevices();
    }


    /**
     * 判断当前屏幕是否打开
     *
     * @param graphicsDevice 图形设备对象
     * @return boolean
     * @author Passer
     * @date 2020/10/28 14:56
     */
    public boolean judgeScreenOn(GraphicsDevice graphicsDevice) {
        try {
            robot = new Robot(graphicsDevice);
            //创建整屏截图
            BufferedImage image = robot.createScreenCapture(rectangle);
            //当截图不为null并且长宽存在时代表屏幕开启
            if (image != null && image.getWidth() > 1) {
                return true;
            }
        } catch (AWTException e) {
            log.error(e.toString());
        }

        return false;
    }

    /**
     * 此处截图并通过websocket发送至前端
     *
     * @param initialDelay 初始延迟
     * @param period       周期，每隔多少毫秒执行
     * @author Passer
     * @date 2020/10/28 15:07
     */
    public void screenFrequency(long initialDelay, long period) {
        //判断屏幕是否处于开启状态
        if (!judgeScreenOn(getGraphicsDevices()[0])) {
            return;
        }
//        Java2DFrameConverter java2DFrameConverter = new Java2DFrameConverter();
//        FFmpegFrameGrabber grabber = new FFmpegFrameGrabber("desktop");
//        grabber.setFormat("gdigrab");
//        grabber.setFrameRate(30);
//        try {
//
//            grabber.start();
//            //流媒体输出地址，分辨率(长，高），是否录制音频(0：/不录制，1录制)
//            Frame frame = null;
//            while ((frame = grabber.grab()) != null) {
//                System.out.println("测试");
//                BufferedImage bufferedImage = java2DFrameConverter.getBufferedImage(frame);
//                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//                ImageIO.write(bufferedImage, "JPEG", byteArrayOutputStream);
//                wsServerEndpoint.sendInfo(Base64.getEncoder().encodeToString(byteArrayOutputStream.toByteArray()));
//            }
//            //grabber.stop();
//            //grabber.release();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        ScheduledThreadPoolExecutor scheduledThreadPoolExecutor = new ScheduledThreadPoolExecutor(2, new ThreadFactory() {
//            private final AtomicInteger atomicInteger = new AtomicInteger(0);
//
//            @Override
//            public Thread newThread(Runnable r) {
//                int c = atomicInteger.incrementAndGet();
//                log.info("screen-Threads-" + c);
//                //通过定时器可以更好管理线程
//                return new Thread(r);
//            }
//        });
//        //定义周期定时任务，可以以指定频率周期执行
//        scheduledThreadPoolExecutor.scheduleAtFixedRate(() -> {
//            String result = null;
//            long s = System.currentTimeMillis();
//            try {
//                wsServerEndpoint.sendInfo(result);
//                System.out.println(System.currentTimeMillis() - s);
//            } catch (IOException e) {
//                log.error("发送消息内容出错{}", e.toString());
//            }
//            System.out.println(result);
//            result = "";
//        }, initialDelay, period, TimeUnit.MILLISECONDS);
    }

}
