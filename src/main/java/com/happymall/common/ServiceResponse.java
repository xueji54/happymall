package com.happymall.common;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.io.Serializable;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class ServiceResponse<T> implements Serializable {
        private int status;
        private String msg;
        private T date;

        private ServiceResponse(int status){
            this.status = status;
        }

        private ServiceResponse(int status,T date){
            this.status = status;
            this.date = date;
        }

        private ServiceResponse(int status,String msg,T date){
            this.status = status;
            this.msg = msg;
            this.date = date;
        }

        private ServiceResponse(int status,String msg){
            this.status = status;
            this.msg = msg;
        }

        @JsonIgnore
        public boolean isSuccess(){
            return this.status == ResponseCode.SUCCESS.getCode();
        }

        public int getStatus(){
            return status;
        }

        public T getDate(){
            return date;
        }

    public String getMsg() {
        return msg;
    }

    public static <T> ServiceResponse<T> creatBySuccess(){
            return new ServiceResponse<T>(ResponseCode.SUCCESS.getCode());
    }

    public static <T>ServiceResponse<T> creatBysuccessMessage(String msg){
        return new ServiceResponse<T>(ResponseCode.SUCCESS.getCode(),msg);
    }

    public static <T>ServiceResponse<T> creatBysuccessMessage(T date){
        return new ServiceResponse<T>(ResponseCode.SUCCESS.getCode(),date);
    }

    public static <T>ServiceResponse<T> creatBysuccessMessage(String msg,T date){
        return new ServiceResponse<T>(ResponseCode.SUCCESS.getCode(),msg,date);
    }

    public static <T>ServiceResponse<T> creatByErrorMessage(){
        return new ServiceResponse<T>(ResponseCode.ERROR.getCode(),ResponseCode.ERROR.getDesc());
    }

    public static <T>ServiceResponse<T> creatByErrorMessage(String msg){
        return new ServiceResponse<T>(ResponseCode.ERROR.getCode(),msg);
    }



}
