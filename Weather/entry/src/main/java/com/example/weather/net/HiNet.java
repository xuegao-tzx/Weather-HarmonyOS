package com.example.weather.net;

import com.example.weather.util.HiExecutor;
import ohos.hiviewdfx.HiLog;
import ohos.hiviewdfx.HiLogLabel;
import ohos.net.NetHandle;
import ohos.net.NetManager;
import org.devio.hi.json.HiJson;
import org.devio.hi.json.JSONException;
import org.devio.hi.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;

/**
 * The type Hi net.
 */
public class HiNet implements IHiNet {
    private NetManager netManager;
    private HiLogLabel logLabel = new HiLogLabel(0, 0, HiNet.class.getSimpleName());

    /**
     * Instantiates a new Hi net.
     */
    public HiNet() {
        netManager = NetManager.getInstance(null);
    }

    @Override
    public void get(String url, Map<String, String> params, NetListener listener) {
        String finalUrl = HiNetUtil.buildParams(url, params);
        HiLog.debug(logLabel, "finalUrl:" + finalUrl);
        HiExecutor.runBG(new Runnable() {
            @Override
            public void run() {
                doGet(finalUrl, listener);
            }
        });
    }

    private void doGet(String finalUrl, NetListener listener) {
        NetHandle netHandle = netManager.getDefaultNet();
        HttpURLConnection connection = null;
        InputStream inputStream = null;
        ByteArrayOutputStream baos = null;
        try {
            URL url = new URL(finalUrl);
            URLConnection urlConnection = netHandle.openConnection(url, Proxy.NO_PROXY);
            if (urlConnection instanceof HttpURLConnection) {
                connection = (HttpURLConnection) urlConnection;
            }
            connection.setRequestMethod("GET");
            connection.connect();
            HiLog.debug(logLabel, "connect...");
            if (connection.getResponseCode() == 200) {
                inputStream = connection.getInputStream();
                baos = new ByteArrayOutputStream();
                int readLen;
                byte[] bytes = new byte[1024];
                while ((readLen = inputStream.read(bytes)) != -1) {
                    baos.write(bytes, 0, readLen);
                }
                String result = baos.toString();
                HiExecutor.runUI(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            HiJson res = new HiJson(new JSONObject(result));
                            listener.onSuccess(res);
                            HiLog.debug(logLabel, "success.");
                        } catch (JSONException e) {
                            e.printStackTrace();
                            listener.onFail("data parse error, msg" + e.toString());
                        }
                    }
                });
            } else {
                HiLog.debug(logLabel, "Request fail, code:" + connection.getResponseCode());
                listener.onFail("Request fail, code:" + connection.getResponseCode());
            }
        } catch (Exception e) {
            HiLog.debug(logLabel, "Request fail, msg:" + e.toString());
            listener.onFail("Request fail, msg:" + e.toString());
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
            HiNetUtil.close(inputStream);
            HiNetUtil.close(baos);
        }
    }

}
