package com.zsg.jx.lightcontrol.ui;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.zsg.jx.lightcontrol.R;
import com.zsg.jx.lightcontrol.util.BaseTools;
import com.zsg.jx.lightcontrol.util.Config;
import com.zsg.jx.lightcontrol.util.JsonUtil;
import com.zsg.jx.lightcontrol.util.L;
import com.zsg.jx.lightcontrol.volley.RequestListener;
import com.zsg.jx.lightcontrol.volley.RequestManager;
import com.zsg.jx.lightcontrol.volley.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

public class FindPasswordActivity extends BaseActivity {
    private static final String TAG = "testFindPasswordActivity";
    private EditText et_phone;
    private EditText et_verCode;
    private EditText et_pwd;
    private EditText et_pwd2;
    private Button btn_sure;
    private Button btn_getCode;

    private String phone;
    private String code = "";
    private String passwordstr = "";

    private Timer mTimer;   //定时器 用来显示验证码下次发送等待时间

    private Handler timerHandler = new Handler() {
        public void handleMessage(Message msg) {
            int num = msg.what;
            btn_getCode.setText(num
                    + getString(R.string.second));
            if (num == -1) {
                mTimer.cancel();
                btn_getCode.setEnabled(true);
                btn_getCode.setText(R.string.register_button_code);
            }
        }

        ;
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_password);

        initView();
    }

    private void initView() {
        et_phone = (EditText) findViewById(R.id.et_phone);
        et_verCode = (EditText) findViewById(R.id.et_verCode);
        btn_sure = (Button) findViewById(R.id.btn_next);
        btn_getCode = (Button) findViewById(R.id.btn_getCode);

        et_pwd = (EditText) findViewById(R.id.et_pwd);
        et_pwd2 = (EditText) findViewById(R.id.et_pwd2);

        btn_sure.setEnabled(false);
        btn_sure.setOnClickListener(this);
        btn_getCode.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_btn_left:
                //结束当前活动,返回到登录界面
                //finish();
                onBackPressed();
                break;
            case R.id.btn_next:
                phone = et_phone.getText().toString().trim();
                if (!BaseTools.isPhoneNumber(phone)) {
                    showShortToast(getString(R.string.phone_format_not_match));
                    return;
                }

                String inputcode = et_verCode.getText().toString().trim();
                if (inputcode.equals("")) {
                    showShortToast(getString(R.string.code_not_empty));
                    return;
                }

                if (!inputcode.equals(code)) {
                    showShortToast(getString(R.string.code_not_right));
                    return;
                }

                passwordstr = et_pwd.getText().toString().trim();
                String repasswordstr = et_pwd2.getText().toString().trim();
                if (passwordstr.equals("")) {
                    showShortToast(getString(R.string.pwd_not_empty));
                    return;
                } else if (repasswordstr.equals("")) {
                    showShortToast(getString(R.string.ensure_pwd_not_empty));
                    return;
                } else if (!passwordstr.equals(repasswordstr)) {
                    showShortToast(getString(R.string.pwd_ensurepwd_not_match));
                    return;
                } else if (passwordstr.length() < 6 || passwordstr.length() > 16) {
                    showShortToast(getString(R.string.pwd_length_not_right));
                    return;
                }

                findPassword();

                break;
            case R.id.btn_getCode:
                //发送验证码
                phone = et_phone.getText().toString().trim();
                if (!BaseTools.isPhoneNumber(phone)) {
                    showShortToast(getString(R.string.phone_format_not_match));
                    return;
                }
                btn_getCode.setText(getString(R.string.is_sending));
                btn_getCode.setEnabled(false);
                requestLogin();
                break;
        }
    }

    private void findPassword() {
        RequestParams params = new RequestParams();
        RequestManager.get(Config.URL_CHANGPASSWORD + "?mobile=" + phone + "&checknum=" + code + "&password=" + passwordstr, this, params, new RequestListener() {
            @Override
            public void requestSuccess(String result) {
                L.i(TAG, "json：" + result);
                try {
                    JSONObject json = new JSONObject(result);
                    closeLoadingDialog();
                    if (!"ok".equals(JsonUtil.getStr(json, Config.STATUS))) {
                        BaseTools.showToastByLanguage(FindPasswordActivity.this, json);
                    } else {
                        BaseTools.showToastByLanguage(FindPasswordActivity.this, json);
                        finish();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void requestError(VolleyError e) {
                if (e.networkResponse == null) {
                    Toast.makeText(FindPasswordActivity.this, getString(R.string.net_fail), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void requestLogin() {
        RequestParams params = new RequestParams();
        RequestManager.get(Config.URL_CHECKMOBILE + "?mobile=" + phone + "&type=2", this, params, requestListener());
    }

    private RequestListener requestListener() {
        return new RequestListener() {
            @Override
            public void requestSuccess(String result) {
                L.i(TAG, "json：" + result);
                try {
                    JSONObject json = new JSONObject(result);
                    if (!JsonUtil.getStr(json, Config.STATUS).equals("ok")) {
                        btn_sure.setEnabled(false);
                        BaseTools.showToastByLanguage(FindPasswordActivity.this, json);
                        btn_getCode.setEnabled(true);
                        btn_getCode.setText(R.string.register_button_code);
                    } else {
                        code = String.valueOf(JsonUtil.getInt(json, Config.CODE));
                        btn_sure.setEnabled(true);
                        showShortToast(getString(R.string.code_has_send));
                        mTimer = new Timer();
                        mTimer.schedule(new TimerTask() {
                            int num = 90;

                            @Override
                            public void run() {
                                Message msg = new Message();
                                msg.what = num;
                                timerHandler.sendMessage(msg);
                                num--;
                            }
                        }, 0, 1000);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void requestError(VolleyError e) {
                e.printStackTrace();
                // HttpStatus.SC_UNAUTHORIZED=401  未授权 登录失败
                if (e.networkResponse == null) {
                    Toast.makeText(FindPasswordActivity.this, getString(R.string.net_fail), Toast.LENGTH_LONG).show();
                    btn_sure.setEnabled(false);
                    btn_getCode.setEnabled(true);
                    btn_getCode.setText(R.string.register_button_code);
                    return;
                }

            }
        };
    }

    public void doBack(View v) {
        finish();
    }

}
