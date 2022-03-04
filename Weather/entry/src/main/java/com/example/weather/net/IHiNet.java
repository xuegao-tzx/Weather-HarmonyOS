package com.example.weather.net;

import org.devio.hi.json.HiJson;

import java.util.Map;

/**
 * The interface Hi net.
 */
public interface IHiNet {
    /**
     * Get.
     *
     * @param url      the url
     * @param params   the params
     * @param listener the listener
     */
    void get(String url, Map<String, String> params, NetListener listener);

    /**
     * The interface Net listener.
     */
    interface NetListener {
        /**
         * On success.
         *
         * @param res the res
         */
        void onSuccess(HiJson res);

        /**
         * On fail.
         *
         * @param message the message
         */
        void onFail(String message);
    }
}