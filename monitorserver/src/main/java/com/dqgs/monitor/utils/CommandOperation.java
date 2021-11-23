package com.dqgs.monitor.utils;

import lombok.extern.slf4j.Slf4j;

import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 全系统变量
 *
 * @author by Passer
 * @version 1.0
 * @date 2020/10/30 13:43
 */
@Slf4j
public final class CommandOperation {
    /**
     * 定义键位，1鼠标左键，2鼠标右键，4鼠标滑轮
     * vue属性，组合键，按下时代表键位
     */
    private static final int[] BUTTON_KEY = {1, 2, 4};
    /**
     * 通过此变量全局判断是否执行键鼠操作
     */
    public static AtomicBoolean remoteMonitorType;
    /**
     * 通过此变量全局判断是否执行录屏
     */
    public static AtomicBoolean screenRecordingType;
    /**
     * 机器人类对象，用于操控电脑硬件设备
     */
    public static Robot robot;

    /**
     * 只记录常规按键，功能键不执行
     * 键盘键位对应所有键值数组
     * 用于判断所执行键值是否是键
     */
    public static int[] keys = {1, 2, 3, 4, 8, 9, 12, 13, 16, 17, 18, 19, 20, 27, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 83, 84, 85, 86, 87, 88, 89, 90, 91, 92, 93, 96, 97, 98, 99, 100, 101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 111, 112, 113, 114, 115, 116, 117, 118, 119, 120, 121, 122, 123, 144, 145, 186, 187, 188, 189, 190, 191, 192, 219, 220, 222};

    /**
     * 与Java按键事件绑定
     */
    public static int[] keysEvent = new int[256];

    public static void initKeys() {
        keysEvent[13] = KeyEvent.VK_ENTER;
        keysEvent[8] = KeyEvent.VK_BACK_SPACE;
        keysEvent[9] = KeyEvent.VK_TAB;
        keysEvent[16] = KeyEvent.VK_SHIFT;
        keysEvent[17] = KeyEvent.VK_CONTROL;
        keysEvent[18] = KeyEvent.VK_ALT;
        keysEvent[20] = KeyEvent.VK_CAPS_LOCK;
        keysEvent[27] = KeyEvent.VK_ESCAPE;
        keysEvent[32] = KeyEvent.VK_SPACE;
        keysEvent[33] = KeyEvent.VK_PAGE_UP;
        keysEvent[34] = KeyEvent.VK_PAGE_DOWN;
        keysEvent[35] = KeyEvent.VK_END;
        keysEvent[36] = KeyEvent.VK_HOME;
        keysEvent[37] = KeyEvent.VK_LEFT;
        keysEvent[38] = KeyEvent.VK_UP;
        keysEvent[39] = KeyEvent.VK_RIGHT;
        keysEvent[40] = KeyEvent.VK_DOWN;
        keysEvent[188] = KeyEvent.VK_COMMA;
        keysEvent[189] = KeyEvent.VK_MINUS;
        keysEvent[190] = KeyEvent.VK_PERIOD;
        keysEvent[191] = KeyEvent.VK_SLASH;
        keysEvent[186] = KeyEvent.VK_SEMICOLON;
        keysEvent[187] = KeyEvent.VK_EQUALS;
        keysEvent[219] = KeyEvent.VK_OPEN_BRACKET;
        keysEvent[220] = KeyEvent.VK_BACK_SLASH;
        keysEvent[221] = KeyEvent.VK_CLOSE_BRACKET;
        keysEvent[96] = KeyEvent.VK_NUMPAD0;
        keysEvent[97] = KeyEvent.VK_NUMPAD1;
        keysEvent[98] = KeyEvent.VK_NUMPAD2;
        keysEvent[99] = KeyEvent.VK_NUMPAD3;
        keysEvent[100] = KeyEvent.VK_NUMPAD4;
        keysEvent[101] = KeyEvent.VK_NUMPAD5;
        keysEvent[102] = KeyEvent.VK_NUMPAD6;
        keysEvent[103] = KeyEvent.VK_NUMPAD7;
        keysEvent[104] = KeyEvent.VK_NUMPAD8;
        keysEvent[105] = KeyEvent.VK_NUMPAD9;
        keysEvent[106] = KeyEvent.VK_MULTIPLY;
        keysEvent[107] = KeyEvent.VK_ADD;
        keysEvent[46] = KeyEvent.VK_DELETE;
        keysEvent[144] = KeyEvent.VK_NUM_LOCK;
        keysEvent[112] = KeyEvent.VK_F1;
        keysEvent[113] = KeyEvent.VK_F2;
        keysEvent[114] = KeyEvent.VK_F3;
        keysEvent[115] = KeyEvent.VK_F4;
        keysEvent[116] = KeyEvent.VK_F5;
        keysEvent[117] = KeyEvent.VK_F6;
        keysEvent[118] = KeyEvent.VK_F7;
        keysEvent[119] = KeyEvent.VK_F8;
        keysEvent[120] = KeyEvent.VK_F9;
        keysEvent[121] = KeyEvent.VK_F10;
        keysEvent[122] = KeyEvent.VK_F11;
        keysEvent[123] = KeyEvent.VK_F12;
    }

    /**
     * 设置是否执行操控对象的键鼠操作
     *
     * @param atomicBoolean 判断是否执行键鼠
     * @author Passer
     * @date 2020/10/31 16:17
     */
    public static void setRemoteMonitorType(AtomicBoolean atomicBoolean) {
        CommandOperation.remoteMonitorType = atomicBoolean;
    }

    /**
     * 设置是否正在执行录屏
     *
     * @param atomicBoolean 判断是否在录屏
     * @author Passer
     * @date 2020/11/9 11:38
     */
    public static void setScreenRecordingType(AtomicBoolean atomicBoolean) {
        CommandOperation.screenRecordingType = atomicBoolean;
    }

    /**
     * 创建或者初始化机器人操作类
     *
     * @return java.awt.Robot
     * @author Passer
     * @date 2020/10/31 16:26
     */
    public static Robot initRobot() {
        try {
            robot = new Robot();
            log.info("创建robot成功");
        } catch (AWTException e) {
            log.error("创建robot类对象失败：{}", e.toString());
        }
        return robot;
    }

    /**
     * 根据给定值
     * 匹配对应前端鼠标按键值
     * 将对应鼠标按键值匹配Java对应按键值
     *
     * @param kill 给定值
     * @return int[] 鼠标键位值集合
     * @author Passer
     * @date 2020/10/31 16:43
     */
    public static int matchButton(int kill) {
        //获取给定值匹配html鼠标值
        //根据对应值匹配对应鼠标键位
        switch (kill) {
            case 1:
                //鼠标左键
                return InputEvent.BUTTON1_DOWN_MASK;
            case 2:
                //鼠标中键
                return InputEvent.BUTTON2_DOWN_MASK;
            case 3:
                //鼠标右键
                return InputEvent.BUTTON3_DOWN_MASK;
            default:
                return 0;
        }
    }

    /**
     * 判断按键值是否处于键盘正常按键值范围之内
     * 返回处于正常值范围内的按键值
     *
     * @param line 按键值数组
     * @return int
     * @author Passer
     * @date 2020/11/5 17:41
     */
    public static int judgmentKey(String line) {
        //判断按键值是否是常规键
        int key = Integer.parseInt(line);
        //采用二分法查找是否存在该值
        if (Arrays.binarySearch(keys, key) != -1) {
            //查看该值是否已Java按键事件绑定，未绑定直接返回该值
            if (keysEvent[key] > 0) {
                return keysEvent[key];
            } else {
                return key;
            }
        }

        return -1;
    }

    /**
     * html网页鼠标点击事件，
     * 多个鼠标点击时，其值是各个鼠标值相加之和
     * 该方法，找到html对应鼠标值
     *
     * @param kill 给定值
     * @return java.util.List<java.lang.Integer>
     * @author Passer
     * @date 2020/10/31 16:36
     */
    public static List<Integer> getNum(int kill) {
        int n = BUTTON_KEY.length;
        int nit = 1 << n;
        double in;
        List<Integer> list = new ArrayList<>();
        for (int i = 0; i < nit; i++) {
            in = 0;
            list.clear();
            for (int j = 0; j < n; j++) {
                // 由0到n右移位
                int tmp = 1 << j;
                // 与运算，同为1时才会是1
                if ((tmp & i) != 0) {
                    in += BUTTON_KEY[j];
                    list.add(BUTTON_KEY[j]);
                }
            }
            if (in == kill) {
                return list;
            }
        }
        return null;
    }
}
