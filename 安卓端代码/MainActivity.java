package com.example.lenovo.recognition;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.audiofx.LoudnessEnhancer;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import com.luseen.luseenbottomnavigation.BottomNavigation.BottomNavigationItem;
import com.luseen.luseenbottomnavigation.BottomNavigation.OnBottomNavigationItemClickListener;
import me.nereo.multi_image_selector.MultiImageSelector;
import me.nereo.multi_image_selector.MultiImageSelectorActivity;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    private String mFilePath;
    private List<Picture> pictureList = new ArrayList<>();
    private int requestCode = 1;
    List<String> paths;
    private static final String[] authBaseArr = {
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    FragmentManager fragmentManager;
    FrameLayout frameLayout;
//    ImageView imageView;
    BottomNavigationView bottomNavigationView;
//    public Bitmap mybitmap;
    public static String result = null;
    public static String identityTime = "0";
    public static String filename = null;
    public static Bitmap mybitmap = null;
    public static String myfilepath = null;
    public static StringBuilder cookie = null;
    public static String identify_time = null;
    public static String read_userName = null;
    public static String user_info = null;
    private long exitTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bottomNavigationView = findViewById(R.id.bnv_menu);
        bottomNavigationView.getMenu().getItem(0).setChecked(true);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                setIndexSelected(menuItem);
                return true;
            }
        });
        fragmentManager = getSupportFragmentManager();
        changeFragment(new HomeFragment());
        frameLayout = (FrameLayout) findViewById(R.id.content_frame);
        initNavi();
    }

    //通过index判断当前加载哪个界面
    public void setIndexSelected (MenuItem menuItem)
    {
        switch (menuItem.getItemId()) {
            case R.id.item_bottom_1:
                changeFragment(new HomeFragment());
                break;
            case R.id.item_bottom_2:
                changeFragment(new ResultFragment());
                break;
            case R.id.item_bottom_3:
                changeFragment(new MyFragment());
                break;
            default:
                changeFragment(new HomeFragment());
                break;
        }
    }

    private void changeFragment(Fragment fragment)
    {
        FragmentManager fragmentManager = getSupportFragmentManager();//开启事务
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.content_frame , fragment);
        transaction.commit();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                paths = data.getStringArrayListExtra(MultiImageSelectorActivity.EXTRA_RESULT);
                Bitmap bitmap = BitmapFactory.decodeFile(paths.get(0));
//                mybitmap = bitmap;
                mFilePath = paths.get(0);
                MainActivity.mybitmap = bitmap;
                MainActivity.myfilepath = mFilePath;
                MainActivity.filename = mFilePath.substring(mFilePath.lastIndexOf("/")+1,mFilePath.length());
                Log.e("文件",mFilePath);
//                imageView.setImageBitmap(bitmap);
            }
        }
    }

    private void initNavi() {

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        builder.detectFileUriExposure();
        if (android.os.Build.VERSION.SDK_INT >= 23) {
            if (!hasBasePhoneAuth()) {
                this.requestPermissions(authBaseArr, requestCode);
                return;
            }
        }
    }

    private boolean hasBasePhoneAuth() {
        PackageManager pm = getPackageManager();
        for (String auth : authBaseArr) {
            if (pm.checkPermission(auth, getPackageName()) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }


    public static String uploadFile(File file, String RequestURL) {
        String result = null;
        String BOUNDARY = UUID.randomUUID().toString();  //边界标识   随机生成
        String PREFIX = "--", LINE_END = "\r\n";
        String CONTENT_TYPE = "multipart/form-data";   //内容类型

        try {
            URL url = new URL(RequestURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            if (cookie != null) {
//                //发送cookie信息上去，以表明自己的身份，否则会被认为没有权限
//                println("set cookieVal = [" + cookie + "]")
                conn.setRequestProperty("Cookie", cookie.toString());
            }
            conn.setReadTimeout(Constants.TIME_OUT);
            conn.setConnectTimeout(Constants.TIME_OUT);
            conn.setDoInput(true);  //允许输入流
            conn.setDoOutput(true); //允许输出流
            conn.setUseCaches(false);  //不允许使用缓存
            conn.setRequestMethod("POST");  //请求方式
            conn.setRequestProperty("Charset", Constants.CHARSET);  //设置编码
            conn.setRequestProperty("connection", "keep-alive");
            conn.setRequestProperty("Content-Type", CONTENT_TYPE + ";boundary=" + BOUNDARY);
            conn.connect();

            if (file != null) {
                /**
                 * 当文件不为空，把文件包装并且上传
                 */
                DataOutputStream dos = new DataOutputStream(conn.getOutputStream());
                StringBuffer sb = new StringBuffer();
                sb.append(PREFIX);
                sb.append(BOUNDARY);
                sb.append(LINE_END);
                /**
                 * 这里重点注意：
                 * name里面的值为服务器端需要key   只有这个key 才可以得到对应的文件
                 * filename是文件的名字，包含后缀名的   比如:abc.png
                 */

                sb.append("Content-Disposition: form-data; name=\"file\"; filename=\"" + file.getName() + "\"" + LINE_END);
                sb.append("Content-Type: application/octet-stream; charset=" + Constants.CHARSET + LINE_END);
                sb.append(LINE_END);
                dos.write(sb.toString().getBytes());
                InputStream is = new FileInputStream(file);
                byte[] bytes = new byte[1024];
                int len = 0;
                while ((len = is.read(bytes)) != -1) {
                    dos.write(bytes, 0, len);
                }
                is.close();
                dos.write(LINE_END.getBytes());
                byte[] end_data = (PREFIX + BOUNDARY + PREFIX + LINE_END).getBytes();
                dos.write(end_data);
                dos.flush();
                /**
                 * 获取响应码  200=成功
                 * 当响应成功，获取响应的流
                 */
                int res = conn.getResponseCode();
                if (res == 200) {
                    InputStream input = conn.getInputStream();
                    StringBuilder builder = new StringBuilder();
                    byte[] buffer = new byte[1024];
                    int lenghs;
                    while ((lenghs = input.read(buffer)) != -1){
                        builder.append(new String(buffer, 0, lenghs, "UTF-8"));
                    }
//                    StringBuffer sb1 = new StringBuffer();
//                    int ss;
//                    while ((ss = input.read()) != -1) {
//                        sb1.append((char) ss);
//                    }
                    result = builder.toString();
//                    result = sb1.toString();
                    System.out.println(result);
                }
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static String getUserName(String stringname){
        read_userName = stringname;
        return read_userName;
    }

}
