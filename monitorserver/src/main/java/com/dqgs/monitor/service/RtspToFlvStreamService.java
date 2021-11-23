package com.dqgs.monitor.service;

import lombok.extern.slf4j.Slf4j;
import org.bytedeco.ffmpeg.avcodec.AVPacket;
import org.bytedeco.ffmpeg.global.avutil;
import org.bytedeco.javacv.*;

import javax.servlet.AsyncContext;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import static org.bytedeco.ffmpeg.global.avcodec.*;

/**
 * 将rtsp转flv视频流
 *
 * @author by Passer
 * @version 1.0
 * @date 2020/12/4 15:21
 */
@Slf4j
public class RtspToFlvStreamService {
    /**
     * 需要转码得屏幕
     */
    private final String url;
    /**
     * 流输出，异步内容
     */
    private final List<AsyncContext> outEntitys;
    /**
     * 抓帧器
     */
    public FFmpegFrameGrabber grabber = null;
    /**
     * 音频编码器
     */
    protected int audioCodec;
    /**
     * 视频编码解码器
     */
    protected int videoCodec;
    /**
     * 帧率
     */
    protected double framerate;
    /**
     * 比特率
     */
    protected int bitrate;
    /**
     * 帧记录器
     */
    FFmpegFrameRecorder record = null;
    /**
     * 视频分辨率
     */
    int width = -1, height = -1;
    /**
     * 保存转换好的输出流
     */
    ByteArrayOutputStream stream;
    /**
     * 音频参数
     * 想要录制音频，这三个参数必须有：audioChannels > 0 && audioBitrate > 0 && sampleRate > 0
     * audioChannels （0:不录制/1:录制）
     */
    private int audioChannels;
    /**
     * 音频比特率
     */
    private int audioBitrate;
    /**
     * 采样率
     */
    private int sampleRate;
    /**
     * 设置是否关闭视频输出
     */
    private boolean flag = false;
    /**
     * 由于部分摄像头视频流是H264(数据包)或者H265(帧)
     * 判断抓帧还是抓取数据包
     */
    private boolean jud;

    /**
     * 构造器，设置rtsp地址，
     * 和客户端异步输出内容对象
     *
     * @param url        rtsp地址
     * @param outEntitys 异步内容对象集合
     * @author Passer
     * @date 2020/12/4 16:21
     */
    public RtspToFlvStreamService(String url, List<AsyncContext> outEntitys) {
        this.url = url;
        this.outEntitys = outEntitys;
    }

    /**
     * 创建抓帧器
     *
     * @return com.epnce.sve.service.RtspService
     * @author Passer
     * @date 2020/12/4 15:24
     */
    public RtspToFlvStreamService createGrabber() throws FrameGrabber.Exception {
        // 采集/抓取器
        grabber = new FFmpegFrameGrabber(url);
        //设置rtsp视频流连接协议
        if (url.contains("rtsp")) {
            grabber.setOption("rtsp_transport", "tcp");
            //设置超时时间
            grabber.setOption("stimeout", "1000000");
            //设置无缓冲，低延时
            grabber.setOption("fflags", "nobuffer");
            //设置最大延迟
            grabber.setOption("max_delay", "0");
            //开启自动加速
            grabber.setOption("hwaccel", "auto");
//            -vsync auto是默认的视频同步模式。根据muxer的处理能力选择0(passthrough)或1(cfr)
            grabber.setOption("vsync", "auto");
        }
        // 开始之后ffmpeg会采集视频信息，之后就可以获取音视频信息
        try {
            grabber.start(true);
        } catch (FrameGrabber.Exception e) {
            //当启动抓帧器出现错误,尝试再次启动
            grabber.restart();
        }
        //设置分辨率
        if (width < 0 || height < 0) {
            width = grabber.getImageWidth();
            height = grabber.getImageHeight();
        }
        // 视频参数
        audioCodec = grabber.getAudioCodec();
        videoCodec = grabber.getVideoCodec();
        // 帧率
        framerate = grabber.getVideoFrameRate();
        // 比特率
        bitrate = grabber.getVideoBitrate();
        // 音频参数
        // 想要录制音频，这三个参数必须有：audioChannels > 0 && audioBitrate > 0 && sampleRate > 0
        audioChannels = grabber.getAudioChannels();
        audioBitrate = grabber.getAudioBitrate();
        sampleRate = grabber.getSampleRate();
        if (audioBitrate < 1) {
            // 默认音频比特率
            audioBitrate = 128 * 1000;
        }
        return this;
    }

    /**
     * 创建抓帧器需要考虑兼容性
     * 需要判断视频编码和音频编码
     * 满足视频编码为H264和音频编码为AAC时
     *
     * @throws FrameRecorder.Exception
     * @author Passer
     * @date 2020/12/4 15:26
     */
    public void createRecorder() throws FrameRecorder.Exception {
        //初始化输出流
        stream = new ByteArrayOutputStream();
        // 录制/推流器
        record = new FFmpegFrameRecorder(stream, width, height);
        record.setInterleaved(true);
        //crf画面质量：在优先保证画面质量(也不太在乎转码时间的情况下)，使用crf控制转码比较合适，该参数取值范围为0~51，其中0为无损模式，画质越差，生成的文件缺越小。
        //18~28是一个合理的范围。18被认为是视觉无损的。它的输出视频质量和输入视频相当
        record.setOption("crf", "30");
        //ultrafast(终极快)提供最少的压缩（低编码器CPU）和最大的视频流大小
        record.setOption("preset", "ultrafast");
        //降低延迟
        record.setOption("tune", "zerolatency");
        //设置关键帧数量
        record.setGopSize((int) (framerate * 2));
        //设置视频比特率
        record.setVideoBitrate(bitrate);
        if (audioChannels > 0) {
            //设置音频编码器
            record.setAudioCodec(audioCodec);
            //设置音频通道
            record.setAudioChannels(audioChannels);
            //设置音频比特率
            record.setAudioBitrate(audioBitrate);
            //设置音频编码名称
            record.setAudioCodecName("aac");
        }
        //设置采样率
        record.setSampleRate(sampleRate);
        //当推流或者视频保存格式为flv
        // 封装格式flv
        record.setFormat("flv");
        //判断是否包含音频,视频格式是否是HD264
        jud = (audioCodec == AV_CODEC_ID_AAC || audioChannels == 0) && videoCodec == AV_CODEC_ID_H264;
        //运行帧记录器
        if (jud) {
            //判断视频编码器是否为HD264，如果不是转化
            //设置帧率
            record.setFrameRate(framerate);
            record.setVideoCodec(videoCodec);
            record.start(grabber.getFormatContext());
        } else {
            //判断视频编码器是否为HD264，如果不是转化
            //设置帧率为25
            record.setFrameRate(25);
            record.setVideoCodec(AV_CODEC_ID_H264);
            record.start();
        }
    }

    /**
     * 向指定客户端输出字节
     *
     * @param bytes 需要输出的字节数组
     * @author Passer
     * @date 2020/12/4 15:56
     */
    public void outBytes(byte[] bytes) {
        //遍历输出数据到指定客户端,当输出对象为null,关闭连接
        Iterator<AsyncContext> iterator = outEntitys.iterator();
        while (iterator.hasNext()) {
            try {
                AsyncContext asyncContext = iterator.next();
                asyncContext.getResponse().getOutputStream().write(bytes);
            } catch (Exception e) {
                log.info("移除输出");
                iterator.remove();
            }
        }
        //重新加载输出流
        stream.reset();
    }

    /**
     * 继承线程类主方法
     * 实现主要业务逻辑
     *
     * @author Passer
     * @date 2020/12/11 10:42
     */
    public void start() {
        //抓取数据错误次数
        int error = 0;
        try {
            //过滤javacvn内部告警信息
            avutil.av_log_set_level(avutil.AV_LOG_ERROR);
            FFmpegLogCallback.set();
            //创建抓帧器，帧记录器
            createGrabber().createRecorder();
            //向客户端输出flv流头信息
            //转流，头信息
            byte[] header = stream.toByteArray();
            outBytes(header);
            //设置抓取视频
            flag = true;
            //记录最后一次错误时间
            long lastTime = 0;

            //清除缓存
            grabber.flush();
            //转码视频数据
            if (jud) {
                //数据结构包
                AVPacket avPacket;
                while (flag && error < framerate && !outEntitys.isEmpty()) {
                    //抓取数据
                    //判断是否抓取到数据
                    avPacket = grabber.grabPacket();
                    if (avPacket == null) {
                        error++;
                        log.info("抓取数据包null, 次数：{}", error);
                        //此处记载错误次数，重置方法，当报null，两次间隔操过一秒重置
                        if (lastTime != 0 && System.currentTimeMillis() - lastTime > 1500) {
                            error = 0;
                        }
                        //发送测试字节查询输出是否存在
                        outBytes(new byte[]{0});
                        //记录报错时间
                        lastTime = System.currentTimeMillis();
                        continue;
                    }
                    //读取数据到输出流中
                    record.recordPacket(avPacket);
                    //判断是否有数据，有数据将数据输出
                    if (stream.size() > 0) {
                        outBytes(stream.toByteArray());
                    }
                    //释放缓存
                    av_packet_unref(avPacket);
                }
            } else {
                //帧
                Frame frame;
                while (flag && error < framerate && !outEntitys.isEmpty()) {
                    //抓取数据
                    //判断是否抓取到数据
                    frame = grabber.grab();
                    if (frame == null) {
                        error++;
                        log.info("抓取数据包null");
                        //此处记载错误次数，重置方法，当报null，两次间隔操过一秒重置
                        if (lastTime != 0 && System.currentTimeMillis() - lastTime > 1500) {
                            error = 0;
                        }
                        //发送测试字节查询输出是否存在
                        outBytes(new byte[]{0});
                        //记录报错时间
                        lastTime = System.currentTimeMillis();
                        continue;
                    }
                    //读取数据到输出流中
                    record.record(frame);
                    //判断是否有数据，有数据将数据输出
                    if (stream.size() > 0) {
                        outBytes(stream.toByteArray());
                    }
                }
            }
        } catch (IOException e) {
            log.error("转流失败，退出转换，错误信息：{}，错误信息：{}", Thread.currentThread().getStackTrace()[1], e.toString());
        } finally {
            flag = false;
            close();
        }
    }

    /**
     * 关闭抓帧器，帧记录器
     * 以及其他相关资源并释放
     *
     * @author Passer
     * @date 2020/12/4 15:30
     */
    public void close() {
        try {
            flag = false;
            //关闭抓帧器，帧记录器
            grabber.stop();
            record.stop();
            grabber.release();
            record.release();
            grabber.close();
            record.close();
            //关闭输出流
            stream.close();
            //完成异步输出
            outEntitys.forEach(asyncContext -> {
                try {
                    asyncContext.complete();
                } catch (Exception e) {
                    log.error("完成异步输出，错误行数：{}，错误信息：{}", Thread.currentThread().getStackTrace()[1], e.getMessage());
                }
            });
            log.info("完成");
        } catch (Exception e) {
            log.error("关闭资源失败，错误行数：{}，错误信息：{}", Thread.currentThread().getStackTrace()[1], e.toString());
        }
    }
}
