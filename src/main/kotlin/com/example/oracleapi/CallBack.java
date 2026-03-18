package com.example.oracleapi;

public class CallBack {
    public interface Finish_ {
        void onSuccess();
        void onError(Exception e);
    }
}
