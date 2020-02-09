package com.example.lenovo.recognition;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.*;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import me.nereo.multi_image_selector.MultiImageSelector;
import me.nereo.multi_image_selector.MultiImageSelectorActivity;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.example.lenovo.recognition.MainActivity.uploadFile;

public class HomeFragment extends Fragment {

    private boolean isFirstLoading = true;
    private int requestCode = 1;
    List<String> paths;
    private String mFilePath;
    private static final String[] authBaseArr = {
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    ImageView imageView;
    Bitmap bitmap;
    Button sure_button;
    AlertDialog alertDialog1;

//    @Override
//    public void onAttach(Context context) {
//        super.onAttach(context);
//        bitmap = ((MainActivity) getActivity()).getMybitmap();
//        mFilePath = ((MainActivity)getActivity()).getmFilePath();
//    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        imageView = view.findViewById(R.id.image);
        Button upload_button = view.findViewById(R.id.upload_button);
        sure_button = view.findViewById(R.id.sure_button);
        sure_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ProgressDialog pd1 = new ProgressDialog(getContext());
                pd1.setTitle("图片已上传");
                pd1.setMessage("图片正在识别中,请稍后...");
                pd1.setCancelable(false);
                //这里是设置进度条的风格,HORIZONTAL是水平进度条,SPINNER是圆形进度条
                pd1.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                pd1.setIndeterminate(true);
                pd1.show();

                alertDialog1 = new AlertDialog.Builder(getContext())
                        .setTitle("识别结果")//标题
                        .setMessage("")//内容
                        .setIcon(R.mipmap.ic_launcher)//图标
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {//添加"Yes"按钮
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        })
                        .create();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        boolean success = false;
                        try {
                            String result = uploadFile(new File(MainActivity.myfilepath), Constants.IDENTIFY_URL);
                            JSONObject jsonObject = new JSONObject(result);
                            if (jsonObject.getBoolean("success") == true) {
                                MainActivity.result = jsonObject.getString("result");
                                MainActivity.identityTime = jsonObject.getString("identityTime");
                                success = jsonObject.getBoolean("success");
                            } else {
                                MainActivity.identityTime = "0";
//                                Looper.prepare();
//                                Toast.makeText(getActivity(),"识别异常",Toast.LENGTH_SHORT).show();
//                                Looper.loop();
                            }
                            Log.e("TAG", "结果为" + jsonObject);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        pd1.dismiss();
                        String jieguo = "识别失败";
                        if (success == true) {
                            jieguo = "识别成功";
                        } else {
                            MainActivity.result = null;
                        }
                        alertDialog1.setMessage(jieguo);
                        Message message = new Message();
                        myHandler.sendMessage(message);
                    }
                }).start();

            }
        });
        upload_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MultiImageSelector.create(getActivity()).start(getActivity(), requestCode);
            }
        });
        return view;
    }


    @Override
    public void onResume() {
        super.onResume();

        if (!isFirstLoading) {
            //如果不是第一次加载，刷新数据
            imageView.setImageBitmap(MainActivity.mybitmap);
        }

        isFirstLoading = false;
    }

    private Handler myHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            alertDialog1.show();
        }
    };
}
