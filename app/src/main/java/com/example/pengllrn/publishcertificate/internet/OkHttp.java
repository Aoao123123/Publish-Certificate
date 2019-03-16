package com.example.pengllrn.publishcertificate.internet;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by pengllrn on 2019/1/4.
 */

public class OkHttp {
    public final int POSTOK = 0x2017;
    public final int GETOK = 0x2020;
    public final int GETIMGOK = 0x2030;
    public final int WRANG = 0x22;
    public final int EXCEPTION = 0x30;
    private final int SUC = 0x40;
    private Handler handler;
    private Context mContext;

    public OkHttp(Context context, Handler handler) {
        this.mContext = context;
        this.handler = handler;
    }


    public void postDataWithCookie(final String path, final RequestBody requestBody) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    OkHttpClient client = new OkHttpClient();
                    client = new OkHttpClient.Builder().cookieJar(new CookieJar() {
                        private final HashMap<String, List<Cookie>> cookieStore = new HashMap<String, List<Cookie>>();

                        @Override
                        public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
                            cookieStore.put(url.host(), cookies);
                            SharedPreferences.Editor editor = mContext.getSharedPreferences("mycookie", Context.MODE_PRIVATE).edit();
                            editor.putString("sessionid", String.valueOf(cookieStore.get(url.host()).get(0)));
                            editor.apply();
                        }

                        @Override
                        public List<Cookie> loadForRequest(HttpUrl url) {
                            List<Cookie> cookies = cookieStore.get(url.host());
                            return cookies != null ? cookies : new ArrayList<Cookie>();
                        }
                    }).build();
                    Request request = new Request.Builder()
                            .url(path)
                            .post(requestBody)
                            .build();
                    Response response = client.newCall(request).execute();
                    if (response.isSuccessful()) {
                        String responseData = response.body().string();
                        Message msg = new Message();
                        msg.what = POSTOK;
                        msg.obj = responseData;
                        handler.sendMessage(msg);
                        System.out.println("Connected");
                    } else {
                        Message msg = new Message();
                        msg.what = WRANG;
                        handler.sendMessage(msg);
                        System.out.println("Not response");
                    }
                } catch (IOException e) {
                    Message msg = new Message();
                    msg.what = EXCEPTION;
                    handler.sendMessage(msg);
                    e.printStackTrace();
                    System.out.println("Error");
                }
            }
        }).start();
    }

    public void postDataFromInternet(final String path, final RequestBody requestBody) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    OkHttpClient client = new OkHttpClient();
                    //用post提交键值对格式的数据
                    SharedPreferences pref = mContext.getSharedPreferences("mycookie", Context.MODE_PRIVATE);
                    String sessionid = pref.getString("sessionid", "");
                    Request request = new Request.Builder()
                            .addHeader("cookie", sessionid)
                            .url(path)
                            .post(requestBody)
                            .build();
                    Response response = client.newCall(request).execute();
                    if (response.isSuccessful()) {
                        String responseData = response.body().string();
                        Message msg = new Message();
                        msg.what = POSTOK;
                        msg.obj = responseData;
                        handler.sendMessage(msg);
                        System.out.println("Connected");
                    } else {
                        //TODO 错误报告
                        Message msg = new Message();
                        msg.what = WRANG;
                        handler.sendMessage(msg);
                        System.out.println("Not response");
                    }
                } catch (IOException e) {
                    Message msg = new Message();
                    msg.what = EXCEPTION;
                    handler.sendMessage(msg);
                    e.printStackTrace();
                    System.out.println("Error");
                }
            }
        }).start();
    }

    public void postData2Internet(final String path, final RequestBody requestBody) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    OkHttpClient client = new OkHttpClient();
                    //用post提交键值对格式的数据
                    SharedPreferences pref = mContext.getSharedPreferences("mycookie", Context.MODE_PRIVATE);
                    String sessionid = pref.getString("sessionid", "");
                    Request request = new Request.Builder()
                            .addHeader("cookie", sessionid)
                            .url(path)
                            .post(requestBody)
                            .build();
                    Response response = client.newCall(request).execute();
                    if (response.isSuccessful()) {
                        Message msg = new Message();
                        msg.obj = response.body().string();
                        msg.what = SUC;
                        handler.sendMessage(msg);
                    } else {
                        //TODO 错误报告
                        Message msg = new Message();
                        msg.what = WRANG;
                        handler.sendMessage(msg);
                    }
                } catch (IOException e) {
                    Message msg = new Message();
                    msg.what = EXCEPTION;
                    handler.sendMessage(msg);
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void getDataFromInternet(final String path) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    OkHttpClient client = new OkHttpClient();
                    SharedPreferences pref = mContext.getSharedPreferences("mycookie", Context.MODE_PRIVATE);
                    String sessionid = pref.getString("sessionid", "");
                    //用post提交键值对格式的数据
                    Request request = new Request.Builder()
                            .addHeader("cookie", sessionid)
                            .url(path)
                            .build();
                    Response response = client.newCall(request).execute();
                    if (response.isSuccessful()) {
                        String responseData = response.body().string();
                        Message msg = new Message();
                        msg.what = GETOK;
                        msg.obj = responseData;
                        handler.sendMessage(msg);
                        System.out.println("Connected");
                    } else {
                        //TODO 错误报告
                        Message msg = new Message();
                        msg.what = WRANG;
                        handler.sendMessage(msg);
                        System.out.println("Not response");
                    }
                } catch (IOException e) {
                    Message msg = new Message();
                    msg.what = EXCEPTION;
                    handler.sendMessage(msg);
                    e.printStackTrace();
                    System.out.println("Error");
                }
            }
        }).start();
    }

    public void postFromInternet(final String path, final RequestBody requestBody) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    OkHttpClient client = new OkHttpClient();
                    //用post提交键值对格式的数据
                    Request request = new Request.Builder()
                            .url(path)
                            .post(requestBody)
                            .build();
                    Response response = client.newCall(request).execute();
                    if (response.isSuccessful()) {
                        String responseData = response.body().string();
                        Message msg = new Message();
                        msg.what = POSTOK;
                        msg.obj = responseData;
                        handler.sendMessage(msg);
                        System.out.println("Connected");
                    } else {
                        //TODO 错误报告
                        Message msg = new Message();
                        msg.what = WRANG;
                        handler.sendMessage(msg);
                        System.out.println("Not response");
                    }
                } catch (IOException e) {
                    Message msg = new Message();
                    msg.what = EXCEPTION;
                    handler.sendMessage(msg);
                    e.printStackTrace();
                    System.out.println("Error");
                }
            }
        }).start();
    }

    public void getFromInternet(final String path) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    OkHttpClient client = new OkHttpClient();
                    //用post提交键值对格式的数据
                    Request request = new Request.Builder()
                            .url(path)
                            .build();
                    Response response = client.newCall(request).execute();
                    if (response.isSuccessful()) {
                        String responseData = response.body().string();
                        Message msg = new Message();
                        msg.what = GETOK;
                        msg.obj = responseData;
                        handler.sendMessage(msg);
                        System.out.println("Connected");
                    } else {
                        //TODO 错误报告
                        Message msg = new Message();
                        msg.what = WRANG;
                        handler.sendMessage(msg);
                        System.out.println("Not response");
                    }
                } catch (IOException e) {
                    Message msg = new Message();
                    msg.what = EXCEPTION;
                    handler.sendMessage(msg);
                    e.printStackTrace();
                    System.out.println("Error");
                }
            }
        }).start();
    }
}
