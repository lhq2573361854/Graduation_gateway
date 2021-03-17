package com.tianling.handler.phone.message;

/**
 * @author Tianling
 * @email 859073143@qq.com
 * @since 2021/3/11 9:41
 */
public interface SendMessage {
    /**
     * 发送验证码
     * @param phone
     * @param code
     * @return
     */
    String sendMessage(String phone,String code);
}
