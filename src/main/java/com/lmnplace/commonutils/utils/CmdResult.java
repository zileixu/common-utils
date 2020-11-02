package com.lmnplace.commonutils.utils;

public class CmdResult {
    private boolean isSuccess;
    private String msg;

    public CmdResult(boolean isSuccess, String msg) {
        this.isSuccess = isSuccess;
        this.msg = msg;
    }

    public boolean isSuccess() {
        return isSuccess;
    }

    public void setSuccess(boolean success) {
        isSuccess = success;
    }

    public String getMsg() {
        return msg;
    }
}