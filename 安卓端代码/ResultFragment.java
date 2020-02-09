package com.example.lenovo.recognition;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.gson.annotations.Since;
import okhttp3.*;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ResultFragment extends Fragment {
    ImageView result_image;
    TextView result_text, result_time;
    String result;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_result, container, false);
        result_image = view.findViewById(R.id.result_image);
        result_text = view.findViewById(R.id.result_text);
        result_time = view.findViewById(R.id.result_time);

        if (MainActivity.identityTime.equals("0")) result = "";
        else {
            Date date = new Date(((long) Integer.valueOf(MainActivity.identityTime)) * 1000);
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            result = formatter.format(date);
        }

        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder()
                .url(Constants.URL + "/download/" + MainActivity.filename)
                //.url("https://tpc.googlesyndication.com/simgad/2267810362956640009?sqp=4sqPyQQ7QjkqNxABHQAAtEIgASgBMAk4A0DwkwlYAWBfcAKAAQGIAQGdAQAAgD-oAQGwAYCt4gS4AV_FAS2ynT4&rs=AOga4qn7mAcd2fT5GMJ4CtJAM2cMRU4bpg")
                .addHeader("cookie", MainActivity.cookie.toString())
                .build();
        Log.e("TAG", MainActivity.cookie.toString());
        okHttpClient.newCall(request).enqueue(new Callback() {
            public void onFailure(Call call, IOException e) {
                Log.e("TAG", "出错");
            }

            public void onResponse(Call call, Response response) throws IOException {
                InputStream inputStream = response.body().byteStream();//得到图片的流
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                Message msg = new Message();
                msg.obj = bitmap;
                handler.sendMessage(msg);
                Log.e("TAG", response.body().string());
            }
        });
        return view;
    }

    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            Bitmap bitmap = (Bitmap) msg.obj;
            result_image.setImageBitmap(bitmap);//将图片的流转换成图片
            result_text.setText(MainActivity.result);
            result_time.setText(result);
        }
    };

}
