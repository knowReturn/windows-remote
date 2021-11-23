package com.dqgs.monitor;

import com.alibaba.fastjson.JSONObject;
import org.bytedeco.ffmpeg.global.avcodec;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.FFmpegFrameRecorder;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.FrameGrabber;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.awt.*;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@SpringBootTest
class MonitorApplicationTests {

    private int i = 0;

    @Test
    void contextLoads() throws InterruptedException {
        ScheduledThreadPoolExecutor scheduledThreadPoolExecutor = new ScheduledThreadPoolExecutor(10);
        long da = System.currentTimeMillis();
        scheduledThreadPoolExecutor.scheduleAtFixedRate(() -> {
            i++;
            System.out.println("执行:" + i + ":" + System.currentTimeMillis());
            try {
                Thread.sleep(20);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }, 0, (20), TimeUnit.MILLISECONDS);
        Thread.sleep(1000);
    }

    @Test
    void fun1() throws AWTException {
        GraphicsEnvironment graphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice[] graphicsDevices = graphicsEnvironment.getScreenDevices();
        for (GraphicsDevice graphicsDevice : graphicsDevices) {
            System.out.println(graphicsDevice.getIDstring());
        }
    }

    @Test
    void fun2() throws IOException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("command", 1);
        jsonObject.put("values", "83,24");
        System.out.println(jsonObject.toJSONString());
    }

    @Test
    void fun3() throws AWTException {
        /**
         * 滚轴负值向上，正值向下
         */
        //System.setProperty("java.awt.headless", "false");
        Point p = MouseInfo.getPointerInfo().getLocation();
        int width = (int) (p.getX() + 50);
        int heigh = (int) (p.getY() + 50);

        Robot robot;
        robot = new Robot();
        try {
            robot = new Robot();
            robot.mouseWheel(100);
//            robot.keyPress(173);
//            robot.keyPress(KeyEvent.VK_SHIFT);
//            robot.keyPress(KeyEvent.VK_ESCAPE);
//            robot.keyRelease(KeyEvent.VK_1);
//            robot.keyRelease(KeyEvent.VK_SHIFT);
//            robot.keyRelease(173);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Test
    void fun4() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("type", "mouse");
        JSONObject jsonObject1 = new JSONObject();
        jsonObject1.put("mouse", "1,2,3,4,5,6");
        jsonObject1.put("key", "3");
        jsonObject.put("value", jsonObject1);
        System.out.println(jsonObject);
    }

    @Test
    void fun5() throws IOException {
        int value = 7;
        int[] ints = {1, 2, 4};
        getNum(ints, value);
    }

    void getNum(int[] keys, int kill) {
        int n = keys.length;
        int nit = 1 << n;
        double in;
        List<Integer> list = new ArrayList<>();
        for (int i = 0; i < nit; i++) {
            in = 0;
            list.clear();
            for (int j = 0; j < n; j++) {
                int tmp = 1 << j; // 由0到n右移位
                if ((tmp & i) != 0) { // 与运算，同为1时才会是1
                    in += keys[j];
                    list.add(keys[j]);
                }
            }
            if (in == kill)
                System.out.println(Arrays.toString(list.toArray()));
        }
        //System.out.println(Arrays.toString(list.toArray()));
    }

    @Test
    void fun6() throws Exception {
        //运行，设置视频源和推流地址
    }

    @Test
    void fun7() throws Exception {
        System.out.println("开始录屏");
        FFmpegFrameRecorder recorder = null;
        FFmpegFrameGrabber grabber = new FFmpegFrameGrabber("desktop");
        grabber.setFormat("gdigrab");
        grabber.setFrameRate(30);
        grabber.start();
        //流媒体输出地址，分辨率(长，高），是否录制音频(0：/不录制，1录制)
        recorder = setRecorder("D:\\Desktop\\image\\1.flv", grabber.getImageWidth(), grabber.getImageHeight());
        recorder.start();
        long startTime = 0, videoTS = 0;
        Frame frame = null;
        while ((frame = grabber.grab()) != null) {
            if (startTime == 0) {
                startTime = System.currentTimeMillis();
            }
            videoTS = 1000 * (System.currentTimeMillis() - startTime);
            recorder.setTimestamp(videoTS);
            recorder.record(frame);
        }
        recorder.stop();
        grabber.stop();
        grabber.release();
        System.out.println("流媒体输出结束");
//        while (true) {
//            long start = System.currentTimeMillis();
//            try {
//                grabber.grabPacket();
//                long end = System.currentTimeMillis() - start;
//                System.out.println(1 + ":" + end);
//            } catch (FrameGrabber.Exception e) {
//                e.printStackTrace();
//            }
//        }

    }

    private FFmpegFrameRecorder setRecorder(String rtmpUrl, int imageWidth, int height) {
        // 流媒体输出地址，分辨率（长，高），是否录制音频（0:不录制/1:录制）
        FFmpegFrameRecorder recorder = new FFmpegFrameRecorder(rtmpUrl, imageWidth, height, 0);
        recorder.setInterleaved(true);
        // 该参数用于降低延迟
        // recorder.setVideoOption("tune", "zerolatency");
        // ultrafast(终极快)提供最少的压缩（低编码器CPU）和最大的视频流大小；
        // 参考以下命令: ffmpeg -i '' -crf 30 -preset ultrafast
        recorder.setVideoOption("preset", "ultrafast");
        recorder.setVideoOption("crf", "30");
        // 视频编码器输出的比特率2000kbps/s
        recorder.setVideoBitrate(2000000);
        // H.264编码格式
        recorder.setVideoCodec(avcodec.AV_CODEC_ID_H264);
        // 提供输出流封装格式(rtmp协议只支持flv封装格式)
        recorder.setFormat("flv");
        // 视频帧率
        recorder.setFrameRate(30);
        // 关键帧间隔，一般与帧率相同或者是视频帧率的两倍
        recorder.setGopSize(60);
        // 不可变(固定)音频比特率
        recorder.setAudioOption("crf", "0");
        // Highest quality
        recorder.setAudioQuality(0);
        // 音频比特率 192 Kbps
        recorder.setAudioBitrate(192000);
        // 频采样率
        recorder.setSampleRate(44100);
        // 双通道(立体声)
        recorder.setAudioChannels(2);
        // 音频编/解码器
        recorder.setAudioCodec(avcodec.AV_CODEC_ID_AAC);
        return recorder;
    }
//
//
//    @Test
//    void fun11() throws Exception {
//        recordCamera("rtmp://127.0.0.1/live/record1",25);
//    }
//    public void recordCamera(String outputFile, double frameRate) throws Exception {
//        Loader.load(opencv_objdetect.class);
//        FFmpegFrameGrabber grabber = new FFmpegFrameGrabber("desktop");//本机摄像头默认0，这里使用javacv的抓取器，至于使用的是ffmpeg还是opencv，请自行查看源码
//        grabber.setFormat("gdigrab");
//        grabber.setFrameRate(30);
//        grabber.start();//开启抓取器
//
//        OpenCVFrameConverter.ToIplImage converter = new OpenCVFrameConverter.ToIplImage();//转换器
//        IplImage grabbedImage = converter.convert(grabber.grab());//抓取一帧视频并将其转换为图像，至于用这个图像用来做什么？加水印，人脸识别等等自行添加
//        int width = grabbedImage.width();
//        int height = grabbedImage.height();
//
//        FrameRecorder recorder = FrameRecorder.createDefault(outputFile, width, height);
//        recorder.setVideoCodec(avcodec.AV_CODEC_ID_H264); // avcodec.AV_CODEC_ID_H264，编码
//        recorder.setFormat("flv");//封装格式，如果是推送到rtmp就必须是flv封装格式
//        recorder.setFrameRate(frameRate);
//
//        recorder.start();//开启录制器
//        long startTime = 0;
//        long videoTS = 0;
//        CanvasFrame frame = new CanvasFrame("camera", CanvasFrame.getDefaultGamma() / grabber.getGamma());
//        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        frame.setAlwaysOnTop(true);
//        Frame rotatedFrame = converter.convert(grabbedImage);//不知道为什么这里不做转换就不能推到rtmp
//        while (frame.isVisible() && (grabbedImage = converter.convert(grabber.grab())) != null) {
//            rotatedFrame = converter.convert(grabbedImage);
//            frame.showImage(rotatedFrame);
//            if (startTime == 0) {
//                startTime = System.currentTimeMillis();
//            }
//            videoTS = 1000 * (System.currentTimeMillis() - startTime);
//            recorder.setTimestamp(videoTS);
//            recorder.record(rotatedFrame);
//        }
//        frame.dispose();
//        recorder.stop();
//        recorder.release();
//        grabber.stop();
//
//    }

    @Test
    void fun10() throws FileNotFoundException {
        int[] keys = {1, 2, 3, 4, 8, 9, 12, 13, 16, 17, 18, 19, 20, 27, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 83, 84, 85, 86, 87, 88, 89, 90, 91, 92, 93, 96, 97, 98, 99, 100, 101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 111, 112, 113, 114, 115, 116, 117, 118, 119, 120, 121, 122, 123, 144, 145, 186, 187, 188, 189, 190, 191, 192, 219, 220, 222};
        System.out.println(keys.length);
    }

    @Test
    void fun11() throws UnknownHostException {
        String out = "D:\\Desktop\\image\\1.flv";
        out = out.substring(out.lastIndexOf(".") + 1);
        System.out.println(out);
    }
}
