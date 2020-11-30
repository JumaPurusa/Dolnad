package com.dopage.dolnad.Models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class GenericResponse {

    @SerializedName("success")
    private boolean success;

    @SerializedName("status")
    private int status;

    @SerializedName("result")
    private Result result;

    @SerializedName("message")
    private String message;

    public boolean isSuccess() {
        return success;
    }

    public int getStatus() {
        return status;
    }

    public Result getResult() {
        return result;
    }

    public String getMessage() {
        return message;
    }
}
