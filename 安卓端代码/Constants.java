package com.example.lenovo.recognition;

public class Constants {
    public static String URL = "http://192.168.43.200:8080";
    public static String REGISTER_URL = URL+"/register";
    public static String LOGIN_URL = URL+"/login";
    public static String IDENTIFY_URL = URL + "/identify";
    public static String DOWNLOAD_URL = URL + "/download";
//    public static String JDBC_URL = "jdbc:mysql://192.168.43.200:3306/android";
    public static final String TAG = "uploadFile";
    public static final int TIME_OUT = 500*1000;   //上传文件超时时间
    public static final String CHARSET = "utf-8"; //设置编码
    public static final long EXIT_TIME = 2000;//返回退出两秒
}
