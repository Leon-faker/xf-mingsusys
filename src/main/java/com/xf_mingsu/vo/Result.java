package com.xf_mingsu.vo;

public class Result<T> {
    private int code;
    private String msg;
    private T data;

    public static <T> Result<T> success() {
        return (new Result()).setCode(ResultCodeEnum.SUCCESS.getCode()).setMsg(ResultCodeEnum.SUCCESS.getMessage());
    }

    public static <T> Result<T> success(T data) {
        return (new Result()).setCode(ResultCodeEnum.SUCCESS.getCode()).setMsg(ResultCodeEnum.SUCCESS.getMessage()).setData(data);
    }

    public static Result success(String msg) {
        return (new Result()).setCode(ResultCodeEnum.SUCCESS.getCode()).setMsg(msg);
    }

    public static <T> Result<T> success(T data, String msg) {
        return (new Result()).setCode(ResultCodeEnum.SUCCESS.getCode()).setData(data).setMsg(msg);
    }

    public static <T> Result<T> fail() {
        return (new Result()).setCode(ResultCodeEnum.FAIL.getCode()).setMsg(ResultCodeEnum.FAIL.getMessage());
    }

    public static <T> Result<T> fail(ResultCodeEnum code, T data, String message) {
        return (new Result()).setCode(code.getCode()).setMsg(message).setData(data);
    }

    public static <T> Result<T> fail(T data) {
        return (new Result()).setCode(ResultCodeEnum.FAIL.getCode()).setMsg(ResultCodeEnum.FAIL.getMessage()).setData(data);
    }

    public static <T> Result<T> fail(T data, String msg) {
        return (new Result()).setCode(ResultCodeEnum.FAIL.getCode()).setData(data).setMsg(msg);
    }

    public static Result fail(String msg) {
        return (new Result()).setCode(ResultCodeEnum.FAIL.getCode()).setMsg(msg);
    }

    public Result() {
    }

    public int getCode() {
        return this.code;
    }

    public String getMsg() {
        return this.msg;
    }

    public T getData() {
        return this.data;
    }

    public Result<T> setCode(int code) {
        this.code = code;
        return this;
    }

    public Result<T> setMsg(String msg) {
        this.msg = msg;
        return this;
    }

    public Result<T> setData(T data) {
        this.data = data;
        return this;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof Result)) {
            return false;
        } else {
            Result<?> other = (Result)o;
            if (!other.canEqual(this)) {
                return false;
            } else if (this.getCode() != other.getCode()) {
                return false;
            } else {
                Object this$msg = this.getMsg();
                Object other$msg = other.getMsg();
                if (this$msg == null) {
                    if (other$msg != null) {
                        return false;
                    }
                } else if (!this$msg.equals(other$msg)) {
                    return false;
                }

                Object this$data = this.getData();
                Object other$data = other.getData();
                if (this$data == null) {
                    if (other$data != null) {
                        return false;
                    }
                } else if (!this$data.equals(other$data)) {
                    return false;
                }

                return true;
            }
        }
    }

    protected boolean canEqual(Object other) {
        return other instanceof Result;
    }

    public int hashCode() {
        int result = 1;
        int result1 = result * 59 + this.getCode();
        Object $msg = this.getMsg();
        result1 = result * 59 + ($msg == null ? 43 : $msg.hashCode());
        Object $data = this.getData();
        result1 = result * 59 + ($data == null ? 43 : $data.hashCode());
        return result1;
    }

    public String toString() {
        return "Result(code=" + this.getCode() + ", msg=" + this.getMsg() + ", data=" + this.getData() + ")";
    }
}
