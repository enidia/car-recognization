package com.example.lenovo.recognition;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.*;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import okhttp3.*;
import okio.BufferedSink;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.json.JSONException;
import org.json.JSONObject;


import java.io.*;
import java.net.CookieStore;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class LoginActivity extends AppCompatActivity {

    private boolean bPwdSwitch = false;
    private EditText etPassWord;
    private CheckBox cbRememberPwd;
    private EditText etAccount;
    private TextView sign_up;

    private static final String USER_VALUE = "user_value";
    private static final String USER_PWD = "user_password";
    private String uservalue = null;
    private String userpassword = null;
    private JSONObject register_json = null;
    private JSONObject login_json = null;

    protected void onSaveInstanceState(Bundle outState) {
        outState.putString(USER_VALUE, uservalue);
        outState.putString(USER_PWD, userpassword);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle saveInsatnceState) {
        super.onRestoreInstanceState(saveInsatnceState);
        uservalue = saveInsatnceState.getString(USER_VALUE);
        if (etAccount != null) {
            etAccount.setText(uservalue);
        }
        userpassword = saveInsatnceState.getString(USER_PWD);
        if (etPassWord != null) {
            etPassWord.setText(userpassword);
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        final ImageView ivPwdSwitch = findViewById(R.id.iv_pwd_switch);
        etPassWord = findViewById(R.id.password);
        etAccount = findViewById(R.id.username);
        cbRememberPwd = findViewById(R.id.cb_remember_pwd);
        sign_up = findViewById(R.id.sign_up);

        uservalue = etAccount.getText().toString();
        userpassword = etPassWord.getText().toString();

        String state = (String) Userdata.getState(LoginActivity.this);
        if (state != null) {
            cbRememberPwd.setChecked(true);
        } else {
            cbRememberPwd.setChecked(false);
        }
        etPassWord.setText((String) Userdata.getPwd(LoginActivity.this));
        etAccount.setText((String) Userdata.getUser(LoginActivity.this));

        etAccount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                uservalue = etAccount.getText().toString();
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        etPassWord.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                userpassword = etPassWord.getText().toString();
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        //隐藏密码

        ivPwdSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bPwdSwitch = !bPwdSwitch;
                if (bPwdSwitch) {
                    ivPwdSwitch.setImageResource(
                            R.drawable.ic_launcher_invisground);
                    etPassWord.setInputType(
                            InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                } else {
                    ivPwdSwitch.setImageResource(
                            R.drawable.ic_launcher_visground);
                    etPassWord.setInputType(
                            InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                }
            }
        });

        //注册
        sign_up.setOnClickListener(new View.OnClickListener() {
            final String path = Constants.REGISTER_URL;

            @Override
            public void onClick(View v) {
                RequestBody formBody = new FormBody.Builder()
                        .add("userName", etAccount.getText().toString())
                        .add("password", etPassWord.getText().toString())
                        .build();
                final Request request = new Request.Builder()
                        .url(path)
                        .post(formBody)
                        .build();
                OkHttpClient okHttpClient = new OkHttpClient();
                okHttpClient.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.e("TAG", "onFailure: " + e.getMessage());
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        Headers headers = response.headers();

                        if (cbRememberPwd.isChecked()) {
                            Userdata.setUser(etAccount.getText().toString(), LoginActivity.this);
                            Userdata.setPwd(etPassWord.getText().toString(), LoginActivity.this);
                            Userdata.setState("true", LoginActivity.this);
                        } else {
                            Userdata.remove(LoginActivity.this);
                        }

                        String result_json = response.body().string();
                        try {
                            register_json = new JSONObject(result_json);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        try {
                            if (register_json.getBoolean("success") == true) {
                                Looper.prepare();
                                Toast.makeText(LoginActivity.this, "注册成功", Toast.LENGTH_SHORT).show();
                                Looper.loop();
                            } else {
                                Looper.prepare();
                                Toast.makeText(LoginActivity.this, "用户名已存在", Toast.LENGTH_SHORT).show();
                                Looper.loop();
                            }
                        } catch (Exception e) {
                            Looper.prepare();
                            Toast.makeText(LoginActivity.this, "未知异常", Toast.LENGTH_SHORT).show();
                            Looper.loop();
                            e.printStackTrace();
                        }
                    }
                });
//                RequestParams params = new RequestParams();
//                params.put("userName", etAccount.getText().toString());
//                params.put("password", etPassWord.getText().toString());
//                final String path = Constants.REGISTER_URL; // Post
//                AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
//                asyncHttpClient.setConnectTimeout(3000);
//                asyncHttpClient.post(path, params, new AsyncHttpResponseHandler() {
//
//                    @Override
//                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
//                        if (statusCode == 200) {
//                            String result = null;
//                            JSONObject jsonObj = null;
//                            try {
//                                result = new String(responseBody, "utf-8");
//                                jsonObj = new JSONObject(result);
//                                Log.e("TAG", jsonObj.toString());
//                            } catch (UnsupportedEncodingException e) {
//                                e.printStackTrace();
//                            } catch (JSONException e) {
//                                e.printStackTrace();
//                            }
//                            try {
//                                if (jsonObj.getBoolean("success") == true) {
//                                    Toast.makeText(LoginActivity.this, "注册成功", Toast.LENGTH_SHORT).show();
//                                } else {
//                                    Toast.makeText(LoginActivity.this, "注册失败，已存在的用户", Toast.LENGTH_SHORT).show();
//                                }
//                            } catch (JSONException e) {
//                                e.printStackTrace();
//                            }
//                        }
//                    }
//
//                    @Override
//                    public void onFailure(int statusCode, Header[] headers,
//                                          byte[] responseBody, Throwable error) {
//                        Toast.makeText(LoginActivity.this, "网络异常", Toast.LENGTH_SHORT).show();
//                    }
//                });
            }
        });


        //登录
        Button login_button = findViewById(R.id.Login);
        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String path = Constants.LOGIN_URL;
                RequestBody formBody = new FormBody.Builder()
                        .add("userName", etAccount.getText().toString())
                        .add("password", etPassWord.getText().toString())
                        .build();
                final Request request = new Request.Builder()
                        .url(path)
                        .post(formBody)
                        .build();
                OkHttpClient okHttpClient = new OkHttpClient();
                okHttpClient.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.e("TAG", "onFailure: " + e.getMessage());
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        Headers headers = response.headers();

                        for (int i = 0; i < headers.size(); i++) {
                        }
                        if (cbRememberPwd.isChecked()) {
                            Userdata.setUser(etAccount.getText().toString(), LoginActivity.this);
                            Userdata.setPwd(etPassWord.getText().toString(), LoginActivity.this);
                            Userdata.setState("true", LoginActivity.this);
                        } else {
                            Userdata.remove(LoginActivity.this);
                        }

                        HttpUrl loginUrl = request.url();
                        List<Cookie> cookies = Cookie.parseAll(loginUrl, headers);
                        StringBuilder cookieStr = new StringBuilder();
                        for (Cookie cookie : cookies) {
                            cookieStr.append(cookie.name()).append("=").append(cookie.value() + ";");
                        }
                        MainActivity.cookie = cookieStr;
                        String result_json = response.body().string();
                        Log.e("TAG", cookieStr.toString());
                        Log.e("TAG", cookies.toString());
                        Log.e("TAG", result_json);
                        try {
                            login_json = new JSONObject(result_json);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        try {
                            if (login_json.getBoolean("success") == true) {
                                Looper.prepare();
                                Toast.makeText(LoginActivity.this, "登录成功", Toast.LENGTH_SHORT).show();

                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                startActivity(intent);

                                MainActivity.read_userName = etAccount.getText().toString();
                                finish();
                                Looper.loop();
                            } else {
                                Looper.prepare();
                                Toast.makeText(LoginActivity.this, "用户名或密码错误", Toast.LENGTH_SHORT).show();
                                Looper.loop();
                            }
                        } catch (Exception e) {
                            Looper.prepare();
                            Toast.makeText(LoginActivity.this, "未知异常", Toast.LENGTH_SHORT).show();
                            Looper.loop();
                            e.printStackTrace();
                        }
                    }
                });
//                RequestParams params = new RequestParams();
//                params.put("userName", etAccount.getText().toString());
//                params.put("password", etPassWord.getText().toString());
//
//
//
//                AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
//                asyncHttpClient.setConnectTimeout(3000);
//                asyncHttpClient.post(path, params, new AsyncHttpResponseHandler() {
//
//                    @Override
//                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
//                        if (statusCode == 200) {
//                            String result = null;
//                            JSONObject jsonObj = null;
//                            try {
//                                result = new String(responseBody, "utf-8");
//                                jsonObj = new JSONObject(result);
////                                CookieStore cookieStore = getHttpClient()
////                                jsonObj.getString("success");
//                                Log.e("TAG",jsonObj.toString());
//                                //接下来对result进行解析，判断账号和密码
//                            } catch (Exception e) {
//                                e.printStackTrace();
//                            }
//
//                            if (cbRememberPwd.isChecked()) {
//                                Userdata.setUser(etAccount.getText().toString(), LoginActivity.this);
//                                Userdata.setPwd(etPassWord.getText().toString(), LoginActivity.this);
//                                Userdata.setState("true", LoginActivity.this);
//                            } else {
//                                Userdata.remove(LoginActivity.this);
//                            }
//
//                            try {
//                                if (jsonObj.getBoolean("success") == true) {
//                                    Toast.makeText(LoginActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
//                                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
//                                    startActivity(intent);
//                                    finish();
//                                } else {
//                                    Toast.makeText(LoginActivity.this, "用户名或密码错误", Toast.LENGTH_SHORT).show();
//                                }
//                            } catch (JSONException e) {
//                                e.printStackTrace();
//                            }
//                        }
//
//                    }
//
//                    @Override
//                    public void onFailure(int statusCode, Header[] headers,
//                                          byte[] responseBody, Throwable error) {
//                        Toast.makeText(LoginActivity.this, "网络异常", Toast.LENGTH_SHORT).show();
//                    }
//                });

            }
        });


        /*String spFileName = getResources().getString(R.string.shared_preferences_file_name);
        String accountKey = getResources().getString(R.string.login_account_name);
        String passwordKey = getResources().getString(R.string.login_password);

        String rememberPasswordKey = getResources().getString(R.string.login_remember_password);
        SharedPreferences spFile = getSharedPreferences(spFileName, Context.MODE_PRIVATE);

        String account = spFile.getString(accountKey, null);
        String password = spFile.getString(passwordKey, null);
        Boolean rememberPassword = spFile.getBoolean(
                rememberPasswordKey, false);
        if (account != null && !TextUtils.isEmpty(account)) {
            etAccount.setText(account);
        }
        if (password != null && !TextUtils.isEmpty(password)) {
            etPassWord.setText(password);
        }
        cbRememberPwd.setChecked(rememberPassword);
    }*/

    }
}
