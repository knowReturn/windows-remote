package com.dqgs.monitor.entity.enums;

import com.alibaba.fastjson.JSONObject;

/**
 * 定义录屏，远程控制
 * 开启结束信息
 *
 * @author by Passer
 * @version 1.0
 * @date 2020/11/6 15:37
 */
public enum ScreenRecordType {

    /**
     * 定义录屏开始信息
     * 成功or失败
     */
    SCREEN_RECORD_START {
        /**
         *
         * @param bo
         */
        @Override
        public String info(boolean bo) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("type", "SCREEN_RECORD_START");
            jsonObject.put("value", bo);
            return jsonObject.toString();
        }
    },

    /**
     * 定义录屏停止消息
     * 成功or失败
     */
    SCREEN_RECORD_STOP {
        @Override
        public String info(boolean bo) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("type", "SCREEN_RECORD_START");
            jsonObject.put("value", bo);
            return jsonObject.toString();
        }
    },

    /**
     * 定义远程监控开启消息
     * 成功or失败
     */
    REMOTE_MONITOR_START {
        @Override
        public String info(boolean bo) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("type", "REMOTE_MONITOR_START");
            jsonObject.put("value", bo);
            return jsonObject.toString();
        }
    },

    /**
     * 定义远程监控关闭消息
     * 成功or失败
     */
    REMOTE_MONITOR_STOP {
        @Override
        public String info(boolean bo) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("type", "REMOTE_MONITOR_STOP");
            jsonObject.put("value", bo);
            return jsonObject.toString();
        }
    };

    public abstract String info(boolean bo);
}
