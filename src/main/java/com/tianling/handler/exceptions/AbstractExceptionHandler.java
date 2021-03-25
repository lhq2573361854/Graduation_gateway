package com.tianling.handler.exceptions;

import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.tianling.common.ExceptionMessage;
import com.tianling.common.PathVariables;
import com.tianling.exception.ValidateCodeException;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.cloud.gateway.support.NotFoundException;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerErrorException;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Tianling
 * @email 859073143@qq.com
 * @since 2021/2/15 22:48
 */
public  abstract class AbstractExceptionHandler {
    private static  Integer DEFAULT_ERROR_CODE = 400;

    protected String formatMessage(Throwable ex) {
        String errorMessage = null;
        if (ex instanceof NotFoundException) {
            errorMessage = ExceptionMessage.NOTFOUNDEXCEPTION;
        } else if (ex instanceof ResponseStatusException) {
            ResponseStatusException responseStatusException = (ResponseStatusException) ex;
            AbstractExceptionHandler.DEFAULT_ERROR_CODE =  ((ResponseStatusException)ex).getStatus().value();
            errorMessage = responseStatusException.getMessage();
        } else if(ex instanceof NullPointerException) {
            errorMessage = ExceptionMessage.NULLPOINTEREXCEPTION;
        }else if(ex instanceof ServerErrorException || ex instanceof SignatureException){
            errorMessage = ExceptionMessage.SERVERERROREXCEPTION;
        } else if(ex instanceof ValidateCodeException){
            errorMessage = ExceptionMessage.VALIDATE_CODE_EXCEPTION_MESSAGE;
            AbstractExceptionHandler.DEFAULT_ERROR_CODE =  410;
        }else if(ex instanceof JsonProcessingException){
            errorMessage = ExceptionMessage.JSON_FORMAT_EXCEPTION;
        }else if(ex instanceof RuntimeException){
            if(StrUtil.containsIgnoreCase(ex.getMessage(), PathVariables.UNIQUE)){
                errorMessage =ExceptionMessage.UNIQUE_EXCEPTION;
            }else{
                errorMessage = ex.getMessage();
            }

        }else {
            errorMessage = ex.getMessage();
        }



        return errorMessage;
    }
    protected Map<String, Object> buildErrorMap(String errorMessage) {
        Map<String, Object> resMap = new HashMap<>();
        resMap.put("msg", errorMessage);
        resMap.put("code", DEFAULT_ERROR_CODE);
        resMap.put("data", null);
        return resMap;
    }
}
