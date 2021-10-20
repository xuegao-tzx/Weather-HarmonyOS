package com.example.weather.net;

import org.devio.hi.json.HiJson;

import java.util.Map;

import java.util.Map;

public interface IHiNet {
    void get(String url, Map<String, String> params, NetListener listener);

    interface NetListener{
        void onSuccess(HiJson res);
        void onFail(String message);
    }
}