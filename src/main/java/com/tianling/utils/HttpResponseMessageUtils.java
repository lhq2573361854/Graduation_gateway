package com.tianling.utils;

import com.tianling.common.AuthenticationMessage;
import com.tianling.common.ExceptionMessage;
import com.tianling.entities.ResponseInfo;
import org.springframework.http.HttpStatus;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @author Tianling
 * @email 859073143@qq.com
 * @since 2021/1/17 20:04
 */
public class HttpResponseMessageUtils<T> {
    private static final String SPLIT = ",";

    public  static  <T> Mono<ResponseInfo<T>>  querySuccessResponse(){
        return ResponseInfo.ok(Mono.empty(), HttpStatus.OK.value(), ExceptionMessage.QUERYSUCCESS);
    }

    public  static  <T> Mono<ResponseInfo<T>>  querySuccessResponse(Flux data){
        return data.collectList().flatMap(HttpResponseMessageUtils::querySuccessResponse);
    }

    public  static  <T> Mono<ResponseInfo<T>>  queryFailedResponse(){
        return ResponseInfo.failed(Mono.empty(), HttpStatus.BAD_REQUEST.value(), ExceptionMessage.QUERYFAILED);
    }

    public  static  <T> ResponseInfo<T>  queryFailedCommonResponse(){
        return new ResponseInfo<T>(ExceptionMessage.QUERYFAILED,HttpStatus.BAD_REQUEST.value(),null);
    }



    public  static  <T> Mono<ResponseInfo<T>>  updateSuccessResponse(){
        return ResponseInfo.ok(Mono.empty(), HttpStatus.OK.value(), ExceptionMessage.UPDATEFAILED);
    }

    public  static  <T> Mono<ResponseInfo<T>>  updateFailedResponse(){
        return ResponseInfo.failed(Mono.empty(), HttpStatus.BAD_REQUEST.value(), ExceptionMessage.UPDATEFAILED);
    }

    public  static  <T> ResponseInfo<T>  updateFailedCommonResponse(){
        return new ResponseInfo<T>(ExceptionMessage.UPDATEFAILED,HttpStatus.BAD_REQUEST.value(),null);
    }


    public  static  <T> Mono<ResponseInfo<T>>  insertSuccessResponse(){
        return ResponseInfo.ok(Mono.empty(), HttpStatus.OK.value(), ExceptionMessage.INSERTSUCCESS);
    }

    public  static  <T> Mono<ResponseInfo<T>>  insertFailedResponse(){
        return ResponseInfo.failed(Mono.empty(), HttpStatus.BAD_REQUEST.value(), ExceptionMessage.INSERTFAILED);
    }

    public  static  <T> ResponseInfo<T>  insertFailedCommonResponse(){
         return new ResponseInfo<T>(ExceptionMessage.INSERTFAILED,HttpStatus.BAD_REQUEST.value(),null);
    }

    public  static  <T> Mono<ResponseInfo<T>>  deleteSuccessResponse(){
        return ResponseInfo.ok(Mono.empty(), HttpStatus.OK.value(), ExceptionMessage.DELETESUCCESS);
    }
    public  static  <T> Mono<ResponseInfo<T>>  deleteFailedResponse(){
        return ResponseInfo.failed(Mono.empty(), HttpStatus.BAD_REQUEST.value(), ExceptionMessage.DELETEFAILED);
    }

    public  static  <T> ResponseInfo<T>  deleteFailedCommonResponse(){
        return new ResponseInfo<T>(ExceptionMessage.DELETEFAILED,HttpStatus.BAD_REQUEST.value(),null);
    }


    public  static  <T> Mono<ResponseInfo<T>>  querySuccessResponse(String msg){
        return ResponseInfo.ok(Mono.empty(), HttpStatus.OK.value(), ExceptionMessage.QUERYSUCCESS+SPLIT+msg);
    }

    public  static  <T> Mono<ResponseInfo<T>>  queryFailedResponse(String msg){
        return ResponseInfo.failed(Mono.empty(), HttpStatus.BAD_REQUEST.value(), ExceptionMessage.QUERYFAILED+SPLIT+msg);
    }

    public  static  <T> ResponseInfo<T>  queryFailedCommonResponse(String msg){
        return new ResponseInfo<T>(ExceptionMessage.INSERTFAILED,HttpStatus.BAD_REQUEST.value(),msg);
    }


    public  static  <T> Mono<ResponseInfo<T>>  updateSuccessResponse(String msg){
        return ResponseInfo.ok(Mono.empty(), HttpStatus.OK.value(), ExceptionMessage.UPDATESUCCESS+SPLIT+msg);
    }

    public  static  <T> Mono<ResponseInfo<T>>  updateFailedResponse(String msg){
        return ResponseInfo.failed(Mono.empty(), HttpStatus.BAD_REQUEST.value(), ExceptionMessage.UPDATEFAILED+SPLIT+msg);
    }

    public  static  <T> ResponseInfo<T>  updateFailedCommonResponse(String msg){
        return new ResponseInfo<T>(ExceptionMessage.UPDATEFAILED,HttpStatus.BAD_REQUEST.value(),msg);
    }


    public  static  <T> Mono<ResponseInfo<T>>  insertSuccessResponse(String msg){
        return ResponseInfo.ok(Mono.empty(), HttpStatus.OK.value(), ExceptionMessage.INSERTSUCCESS+SPLIT+msg);
    }

    public  static  <T> Mono<ResponseInfo<T>>  insertFailedResponse(String msg){
        return ResponseInfo.failed(Mono.empty(), HttpStatus.BAD_REQUEST.value(), ExceptionMessage.INSERTFAILED+SPLIT+msg);
    }

    public  static  <T> ResponseInfo<T>  insertFailedCommonResponse(String msg){
        return new ResponseInfo<T>(ExceptionMessage.INSERTFAILED,HttpStatus.BAD_REQUEST.value(),msg);
    }

    public  static  <T> Mono<ResponseInfo<T>>  deleteSuccessResponse(String msg){
        return ResponseInfo.ok(Mono.empty(), HttpStatus.OK.value(), ExceptionMessage.DELETESUCCESS+SPLIT+msg);
    }

    public  static  <T> Mono<ResponseInfo<T>>  deleteFailedResponse(String msg){
        return ResponseInfo.failed(Mono.empty(), HttpStatus.BAD_REQUEST.value(), ExceptionMessage.DELETEFAILED+SPLIT+msg);
    }

    public  static  <T> ResponseInfo<T>  deleteFailedCommonResponse(String msg){
        return new ResponseInfo<T>(ExceptionMessage.DELETEFAILED,HttpStatus.OK.value(),msg);
    }


    public  static  <T> Mono<ResponseInfo<T>>  querySuccessResponse(T t){
        return ResponseInfo.ok(Mono.just(t), HttpStatus.OK.value(), ExceptionMessage.QUERYSUCCESS);
    }

    public  static  <T> Mono<ResponseInfo<T>>  queryFailedResponse(T t){
        return ResponseInfo.failed(Mono.just(t), HttpStatus.BAD_REQUEST.value(), ExceptionMessage.QUERYFAILED);
    }

    public  static  <T> ResponseInfo<T>  queryFailedCommonResponse(T t){
        return new ResponseInfo<T>(ExceptionMessage.DELETEFAILED,HttpStatus.BAD_REQUEST.value(),t);
    }

    public  static  <T> Mono<ResponseInfo<T>>  updateSuccessResponse(T t){
        return ResponseInfo.ok(Mono.just(t), HttpStatus.OK.value(), ExceptionMessage.UPDATESUCCESS);
    }

    public  static  <T> Mono<ResponseInfo<T>>  updateFailedResponse(T t){
        return ResponseInfo.failed(Mono.just(t), HttpStatus.BAD_REQUEST.value(), ExceptionMessage.UPDATEFAILED);
    }

    public  static  <T> ResponseInfo<T>  updateFailedCommonResponse(T t){
        return new ResponseInfo<T>(ExceptionMessage.UPDATEFAILED,HttpStatus.BAD_REQUEST.value(),t);
    }

    public  static  <T> Mono<ResponseInfo<T>>  insertSuccessResponse(T t){
        return ResponseInfo.ok(Mono.just(t), HttpStatus.OK.value(), ExceptionMessage.INSERTSUCCESS);
    }

    public  static  <T> Mono<ResponseInfo<T>>  insertFailedResponse(T t){
        return ResponseInfo.failed(Mono.just(t), HttpStatus.BAD_REQUEST.value(), ExceptionMessage.INSERTFAILED);
    }

    public  static  <T> ResponseInfo<T>  insertFailedCommonResponse(T t){
        return new ResponseInfo<T>(ExceptionMessage.INSERTFAILED,HttpStatus.BAD_REQUEST.value(),t);
    }

    public  static  <T> Mono<ResponseInfo<T>>  deleteSuccessResponse(T t){
        return ResponseInfo.ok(Mono.just(t), HttpStatus.OK.value(), ExceptionMessage.DELETESUCCESS);
    }
    public  static  <T> Mono<ResponseInfo<T>>  deleteFailedResponse(T t){
        return ResponseInfo.failed(Mono.just(t), HttpStatus.BAD_REQUEST.value(), ExceptionMessage.DELETEFAILED);
    }

    public  static  <T> ResponseInfo<T>  deleteFailedCommonResponse(T t){
        return new ResponseInfo<T>(ExceptionMessage.DELETEFAILED,HttpStatus.BAD_REQUEST.value(),t);
    }


    public static <T> Mono<ResponseInfo<T>> exceptionHandler(T data){
        return ResponseInfo.ok(Mono.just(data),HttpStatus.BAD_REQUEST.value(),"");
    }


    public static <T> Mono<ResponseInfo<T>> exceptionHandler(String data){
        return ResponseInfo.ok(Mono.empty(),HttpStatus.BAD_REQUEST.value(),data);
    }

    public static <T> Mono<ResponseInfo<T>> exceptionHandler(T data,String msg){
        return ResponseInfo.ok(Mono.just(data),HttpStatus.BAD_REQUEST.value(),msg);
    }

    public  static  <T> ResponseInfo<T>  authenticationSuccess(T t){
        return new ResponseInfo<T>(AuthenticationMessage.AUTHENTICATIONSUCCESS.getMessage(), HttpStatus.OK.value(),t);
    }

    public  static  <T> ResponseInfo<T>  authenticationFailed(T t){
        return new ResponseInfo<T>(AuthenticationMessage.AUTHENTICATIONFAILED.getMessage(), HttpStatus.UNAUTHORIZED.value(),t);
    }
    public  static   ResponseInfo  authenticationSuccess(){
        return new ResponseInfo(AuthenticationMessage.AUTHENTICATIONSUCCESS.getMessage(), HttpStatus.OK.value(),null);
    }

    public  static  <T> ResponseInfo<T>  authenticationFailed(){
        return new ResponseInfo<T>(AuthenticationMessage.AUTHENTICATIONFAILED.getMessage(), HttpStatus.UNAUTHORIZED.value(),null);
    }

    public  static  <T> ResponseInfo<T>  authorizationFailed(T t){
        return new ResponseInfo<T>(AuthenticationMessage.AUTHRIZATIONFAILED.getMessage(), HttpStatus.UNAUTHORIZED.value(),t);
    }

    public  static  <T> ResponseInfo<T>  authorizationSuccess(T t){
        return new ResponseInfo<T>(AuthenticationMessage.AUTHRIZATIONSUCCESS.getMessage(), HttpStatus.OK.value(),t);
    }

    public  static  <T> ResponseInfo<T>  authorizationFailed(){
        return new ResponseInfo<T>(AuthenticationMessage.AUTHRIZATIONFAILED.getMessage(), HttpStatus.FORBIDDEN.value(),null);
    }
    public  static  <T> ResponseInfo<T>  authorizationSuccess(){
        return new ResponseInfo<T>(AuthenticationMessage.AUTHRIZATIONSUCCESS.getMessage(), HttpStatus.OK.value(),null);
    }

    public  static  <T> ResponseInfo<T>  logout(){
        return new ResponseInfo<T>("登出成功", HttpStatus.OK.value(),null);
    }


    public static  <T> Mono<ResponseInfo<T>> sendMessageSuccess(){
        return ResponseInfo.ok(Mono.empty(),HttpStatus.OK.value(), "发送成功");
    }

    public static <T> Mono<ResponseInfo<T>> sendMessageFailed() {
        return ResponseInfo.ok(Mono.empty(),HttpStatus.BAD_REQUEST.value(), "发送失败");

    }
}
