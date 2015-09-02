
package com.spt.carengine.voice.view;

/**
 * @desc:发送的聊天数据消息
 * @author: pangzf
 * @blog:http://blog.csdn.net/pangzaifei/article/details/43023625
 * @github:https://github.com/pangzaifei/zfIMDemo
 * @qq:1660380990
 * @email:pzfpang451@163.com
 */
public class VoiceMessageItem {
    private String message;// 消息内容

    public enum MsgSource {
        Rebot, Mine;
    };

    private MsgSource messageFromWhere; // 消息来自

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public MsgSource getMessageFromWhere() {
        return messageFromWhere;
    }

    public void setMessageFromWhere(MsgSource messageFromWhere) {
        this.messageFromWhere = messageFromWhere;
    }

}
