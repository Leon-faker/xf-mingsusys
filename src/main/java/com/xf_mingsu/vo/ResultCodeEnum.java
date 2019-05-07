package com.xf_mingsu.vo;

public enum ResultCodeEnum {
    SUCCESS(200, "操作成功!"),
    FAIL(-100, "操作失败!");

    private int code;
    private String message;

    private ResultCodeEnum(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return this.code;
    }

    public String getMessage() {
        return this.message;
    }
}
