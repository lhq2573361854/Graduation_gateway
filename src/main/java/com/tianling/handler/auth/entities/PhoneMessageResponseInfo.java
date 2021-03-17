package com.tianling.handler.auth.entities;

import lombok.Data;

import java.io.Serializable;

/**
 * @author Tianling
 * @email 859073143@qq.com
 * @since 2021/3/11 22:15
 */
@Data
public class PhoneMessageResponseInfo implements Serializable {
    private Integer code;
    private String message;
    private DataMessage data;

    @Data
    public static class DataMessage{
       private Integer code;
       private String message;
       private Result result;
    }
    @Data
    public static class Result{
        private String message_id;
        private Integer total;
    }
}
