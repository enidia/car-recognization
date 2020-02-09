package com.example.lenovo.recognition;

import android.content.Context;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class HttpUtil {
    //先定义一个String类型来接收接口相同的部分
    private static final String BASE_URL = "http://192.168.1.101:8890/type/jason/action/";
    //建立静态的AsyncHttpClient
    private static AsyncHttpClient client = new AsyncHttpClient();
    //AsyncHttpClient中有get和post方法，需要用到public方法来修饰，以便调用
    public  static void get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler){
        client.get(getAbsoluteUrl(url), params, responseHandler);
    }
    //post方法中HttpEntity参数是后面发送JSON格式所用到的一个方法
//    public static void post(Context context, String url, HttpEntity entity, String contentType, AsyncHttpResponseHandler responseHandler) {
//        client.post(context,getAbsoluteUrl(url),entity, contentType, responseHandler);
//    }
    //单独写一个方法添加URL
    private static String getAbsoluteUrl(String url) {
        return BASE_URL + url;
    }

}
