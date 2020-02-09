package com.example.lenovo.recognition;
import android.content.Context;
import android.content.SharedPreferences;

public class Userdata {
    public static SharedPreferences share(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences("userdata",Context.MODE_PRIVATE);
        return sharedPreferences;
    }

    public static String getPwd(Context context){
        return share(context).getString("pwd",null);
    }
    public static void setPwd(String pwd,Context context){
        SharedPreferences.Editor e = share(context).edit();
        e.putString("pwd",pwd);
        e.apply();
    }

    public static String getUser(Context context){
        return share(context).getString("user",null);
    }
    public  static void setUser(String user,Context context){
        SharedPreferences.Editor e = share(context).edit();
        e.putString("user",user);
        e.apply();
    }

    public static String getState(Context context){
        return share(context).getString("state",null);
    }
    public static void setState(String state,Context context){
        SharedPreferences.Editor e = share(context).edit();
        e.putString("state",state);
        e.apply();
    }

    public static void remove(Context context){
        SharedPreferences.Editor e = share(context).edit();
        e.remove("pwd");
        e.remove("user");
        e.remove("state");
        e.apply();
    }
}
