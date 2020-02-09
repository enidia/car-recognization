package com.example.lenovo.recognition;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;

public class HistoryFragment extends Fragment {

    private List<Picture> pictureList = new ArrayList<>();
    RecyclerView recyclerView;
    PictureAdapter pictureAdapter;
    private JSONObject history_json = null;
    List<Map<String, Object>> mList;
    public static String result = null;
    JSONObject jsonObject = null;

    Response response1;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history, container, false);
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        //防止访问网络在主线程运行
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        HistoryFragment.GetFun(new VolleyCallback() {
            @Override
            public void onSuccess(String result1) {
                result = result1;
            }
        });

        while(result == null);
        Log.e("zhi",result);


        try {
            JSONArray jsonArray = new JSONArray(result);
            for (int i=0;i<jsonArray.length();i++){
                Picture picture = new Picture();
                picture.setTitle(jsonArray.getJSONObject(i).getString("result"));
                picture.setTime(jsonArray.getJSONObject(i).getString("identifyDate"));
                picture.setBitmap(DownloadImg(Constants.DOWNLOAD_URL+"/"+jsonArray.getJSONObject(i).getString("imageUrl")));
                pictureList.add(picture);

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        pictureAdapter = new PictureAdapter(pictureList);
        recyclerView.setAdapter(pictureAdapter);

        return view;
    }

    public static Bitmap DownloadImg(String imgPath) {
        Bitmap bmp = null;
        try {
            URL imgUrl = new URL(imgPath);
            //打开连接
            URLConnection con = imgUrl.openConnection();
            con.setConnectTimeout(5000);
            InputStream in = con.getInputStream();
            bmp = BitmapFactory.decodeStream(in);

        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bmp;
    }

    public static void GetFun(final VolleyCallback callback){
        OkHttpClient okHttpClient = new OkHttpClient();
        final Request request = new Request.Builder()
                .url(Constants.URL + "/history/" + MainActivity.read_userName)
                //.url("https://tpc.googlesyndication.com/simgad/2267810362956640009?sqp=4sqPyQQ7QjkqNxABHQAAtEIgASgBMAk4A0DwkwlYAWBfcAKAAQGIAQGdAQAAgD-oAQGwAYCt4gS4AV_FAS2ynT4&rs=AOga4qn7mAcd2fT5GMJ4CtJAM2cMRU4bpg")
                .addHeader("cookie", MainActivity.cookie.toString())
                .build();
        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            public void onFailure(Call call, IOException e) {
                Log.e("TAG", "出错");
            }

            public void onResponse(Call call, Response response) throws IOException {
                callback.onSuccess(response.body().string());
            }
        });
    }

    interface VolleyCallback {
        void onSuccess(String result);
    }
}
