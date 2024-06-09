package com.hackathon.melodymap.utils;

public interface ResponseCallback {
    void onResponse(String response);
    void onError(Throwable throwable);
}