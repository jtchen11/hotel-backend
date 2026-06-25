package com.hotel.common;

import lombok.Data;

@Data
public class Result<T> {
    private Integer code;
    private String msg;
    private T data;

    private Result(Integer code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public static <T> Result<T> success(T data) {
        return new Result<>(StatusCode.SUCCESS, "success", data);
    }

    public static <T> Result<T> error(Integer code, String msg) {
        return new Result<>(code, msg, null);
    }

    public static <T> Result<T> error(String msg) {
        return new Result<>(StatusCode.SERVER_ERROR, msg, null);
    }

    public static Result<Void> success() {
        return new Result<>(StatusCode.SUCCESS, "success", null);
    }
}