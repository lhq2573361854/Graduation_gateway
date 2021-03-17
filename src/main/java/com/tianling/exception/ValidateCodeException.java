package com.tianling.exception;

/**
 * @author Tianling
 * @email 859073143@qq.com
 * @since 2021/3/10 20:01
 */
public class ValidateCodeException extends RuntimeException{
    public ValidateCodeException(String message) {
        super(message);
    }

    public ValidateCodeException() {

    }
}
