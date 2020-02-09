package com.example.lenovo.recognition;

import android.content.DialogInterface;
import android.graphics.Paint;
import android.os.*;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import jp.wasabeef.glide.transformations.BlurTransformation;
import jp.wasabeef.glide.transformations.CropCircleTransformation;
import okhttp3.*;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class MyFragment extends Fragment {
    ImageView blurImageView, avatarImageView;
    TextView guanyu, lishi, guanli, user_name;
//    private String userinfo = null;
    EditText edit_pwd, edit_pwd_again;
    private String username;
    private boolean isWork = true;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mine, container, false);
        blurImageView = view.findViewById(R.id.h_back);
        avatarImageView = view.findViewById(R.id.h_head);
        user_name = view.findViewById(R.id.user_name);
        guanyu = view.findViewById(R.id.guanyu);
        lishi = view.findViewById(R.id.lishi);
        lishi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager()
                        .beginTransaction()
                        .addToBackStack(null) //将当前fragment加入到返回栈中
                        .replace(R.id.content_frame, new HistoryFragment()).commit();
            }
        });
        guanli = view.findViewById(R.id.guanli);
        guanli.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder alertDialog7 = new AlertDialog.Builder(getContext());
                View view1 = View.inflate(getContext(), R.layout.activity_setting, null);
                edit_pwd = view1.findViewById(R.id.edit_pwd);
                edit_pwd_again = view1.findViewById(R.id.edit_pwd_again);
                Button bu = view1.findViewById(R.id.setting_pwd);
                alertDialog7
                        .setTitle("修改密码")
                        .setIcon(R.mipmap.ic_launcher)
                        .setView(view1)
                        .create();
                final AlertDialog show = alertDialog7.show();
                bu.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (edit_pwd.getText().toString().equals("") || edit_pwd_again.getText().toString().equals("")) {
                            Toast.makeText(getActivity(), "输入的密码不能为空", Toast.LENGTH_SHORT).show();
                        } else if (edit_pwd.getText().toString().equals(edit_pwd_again.getText().toString())) {
                            try {
                                JSONObject jsonObject = new JSONObject(MainActivity.user_info);
                                final OkHttpClient client = new OkHttpClient();
                                RequestBody body = new FormBody.Builder()
                                        .add("userId", jsonObject.getString("userId"))
                                        .add("userName", jsonObject.getString("userName"))
                                        .add("password", edit_pwd.getText().toString())
                                        .add("userRank", jsonObject.getString("userRank"))
                                        .build();
                                final Request request = new Request.Builder()
                                        .url(Constants.URL + "/users/" + MainActivity.read_userName)
                                        .put(body)
                                        .addHeader("Content-Type", "application/json")
                                        .addHeader("Connection", "keep-alive")
                                        .build();
                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            try {
                                                client.newCall(request).execute();
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }).start();

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            Toast.makeText(getActivity(), "修改成功", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getActivity(), "两次密码不同，请检查", Toast.LENGTH_SHORT).show();
                        }
                        show.dismiss();
                    }
                });
            }
        });
        guanyu.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
        guanyu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog alertDialog2 = new AlertDialog.Builder(getContext())
                        .setTitle("ABOUT US")//标题
                        .setMessage("这是由顾任远、马麟、邓子辉共同完成的车牌识别APP")//内容
                        .setIcon(R.mipmap.ic_launcher)//图标
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {//添加"Yes"按钮
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                            }
                        })
                        .create();
                alertDialog2.show();
            }
        });
        lishi.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
        Glide.with(this).load(R.drawable.user_head)
                .bitmapTransform(new BlurTransformation(getActivity(), 25), new CenterCrop(getActivity()))
                .into(blurImageView);

        Glide.with(this).load(R.drawable.user_head)
                .bitmapTransform(new CropCircleTransformation(getActivity()))
                .into(avatarImageView);

        if(MainActivity.user_info == null)
            new Thread(new Runnable() {
                @Override
                public void run() {
                    MyFragment.GetUserInfo(new HistoryFragment.VolleyCallback() {
                        @Override
                        public void onSuccess(String result1) {
                            MainActivity.user_info = result1;
                            Log.e("result1", result1);
                        }
                    });

                }
            }).start();
        while (MainActivity.user_info == null) ;
        Log.e("MainActivity.user_info", MainActivity.user_info);
        try {
            JSONObject jsonObject = new JSONObject(MainActivity.user_info);
            username = jsonObject.getString("userName");
            Message message = new Message();
            message.what = 1;
            handler.sendMessage(message);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return view;
    }

    private static void GetUserInfo(final HistoryFragment.VolleyCallback callback) {
        OkHttpClient okHttpClient = new OkHttpClient();
        final Request request = new Request.Builder()
                .url(Constants.URL + "/users/" + MainActivity.read_userName)
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

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                user_name.setText("用户名：" + username);
            }
        }
    };
}
