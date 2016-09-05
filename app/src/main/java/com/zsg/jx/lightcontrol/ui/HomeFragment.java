package com.zsg.jx.lightcontrol.ui;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.zsg.jx.lightcontrol.R;
import com.zsg.jx.lightcontrol.app.MyApplication;
import com.zsg.jx.lightcontrol.model.Light;
import com.zsg.jx.lightcontrol.model.WifiDevice;
import com.zsg.jx.lightcontrol.util.BaseTools;
import com.zsg.jx.lightcontrol.util.Config;
import com.zsg.jx.lightcontrol.util.DialogUtil;
import com.zsg.jx.lightcontrol.util.JsonUtil;
import com.zsg.jx.lightcontrol.util.Lg;
import com.zsg.jx.lightcontrol.view.NestRadioGroup;
import com.zsg.jx.lightcontrol.volley.RequestListener;
import com.zsg.jx.lightcontrol.volley.RequestManager;
import com.zsg.jx.lightcontrol.volley.RequestParams;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;


/**
 * 主页碎片
 * Created by zsg on 2016/8/12.
 */
@SuppressLint("ValidFragment")
public class HomeFragment extends Fragment implements View.OnClickListener {
    public final static String TAG = "testHomeFragment";
    private static final int LINK_DEVICE = 1;
    private static final int ADD_LIGHT = 2;


    private HomeActivity context;
    private Banner mbanner;      //图片轮播
    private ArrayList<Integer> mimageList;        //轮播的图片
    private NestRadioGroup mselectGroup = null;       //单选分组
    private RadioButton mAllRadio;
    private RadioButton mAreaRadio;
    private TextView mtvAllRadio;
    private TextView mtvAreaRadio;
    private ImageButton btnAddWifi;
    private TextView btnWgMsg;

    private AllDeviceFragment allDeviceFragment;
    private AreaDeviceFragment areaDeviceFragment;
    private FragmentManager fm;
    private FragmentTransaction transaction;

    private TextView tvDeviceName;
    private ArrayList<WifiDevice> mListDevice;
    public WifiDevice currentDevice;       //当前显示的wifi设备
    //只有都得到了 状态和列表 才显示
    private boolean isGetLightStatus = false;       //是否得到了各个灯的状态   （长连接得到）
    private boolean isGetLightList = false;         //是否得到了各个灯的列表    （web得到）
    private byte[] lightStatuList;      //各个灯的状态
    private HashMap<Integer, Integer> lightIndex = new HashMap<>();     //储存所有灯的列表
    private String result = "";

    private ImageView refresh_view;


    public HomeFragment(HomeActivity context) {
        this.context = context;
        mListDevice = new ArrayList<>();
    }


    public HomeFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_home, container, false);
        initView(v);
        return v;
    }

    private void initView(View v) {
        //初始化轮播图片
        mimageList = new ArrayList<>();
        // mimageList.add(R.drawable.main_news);
        mimageList.add(R.drawable.main_news1);
        mimageList.add(R.drawable.main_news2);
        mimageList.add(R.drawable.main_news3);
        mimageList.add(R.drawable.main_news4);


        //初始化图片轮播
        mbanner = (Banner) v.findViewById(R.id.banner);
        //设置指示器样式
        mbanner.setBannerStyle(BannerConfig.CIRCLE_INDICATOR);
        //设置指示器位置
        mbanner.setIndicatorGravity(BannerConfig.CENTER);
        mbanner.isAutoPlay(true);
        mbanner.setDelayTime(3000);
        //设置轮播图片(所有设置参数方法都放在此方法之前执行)
        mbanner.setImages(mimageList);

        //初始化单选
        mselectGroup = (NestRadioGroup) v.findViewById(R.id.radiogroup);
        mAllRadio = (RadioButton) v.findViewById(R.id.all_radio);
        mAreaRadio = (RadioButton) v.findViewById(R.id.area_radio);

        mtvAreaRadio = (TextView) v.findViewById(R.id.tv_arearadio);
        mtvAllRadio = (TextView) v.findViewById(R.id.tv_allradio);
        mselectGroup.setOnCheckedChangeListener(new NestRadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(NestRadioGroup group, int checkedId) {

                if (checkedId == R.id.all_radio) {
                    changeFragment(0);
                } else {
                    changeFragment(1);
                }
            }
        });

        mAllRadio.setChecked(true);

        //刷新的按钮
        refresh_view = (ImageView) v.findViewById(R.id.refresh_btn);
        refresh_view.setOnClickListener(this);

        initFragment();

        btnAddWifi = (ImageButton) v.findViewById(R.id.btn_addwifi);
        btnAddWifi.setOnClickListener(this);

        btnWgMsg = (TextView) v.findViewById(R.id.btn_wgmsg);

        tvDeviceName = (TextView) v.findViewById(R.id.tv_wgname);
        updateDevice();
    }

    private void initFragment() {
        allDeviceFragment = new AllDeviceFragment(context, this);
        areaDeviceFragment = new AreaDeviceFragment(context);

        fm = getFragmentManager();
        transaction = fm.beginTransaction();

        transaction.add(R.id.device_content, allDeviceFragment);
        transaction.add(R.id.device_content, areaDeviceFragment);
        transaction.commit();

        changeFragment(0);

    }

    private void changeFragment(int position) {
        transaction = fm.beginTransaction();
        transaction.hide(allDeviceFragment);
        transaction.hide(areaDeviceFragment);
        switch (position) {
            case 0:
                mtvAllRadio.setTextColor(getResources().getColor(R.color.theme));
                mtvAreaRadio.setTextColor(Color.GRAY);
                transaction.show(allDeviceFragment);
                transaction.commit();
                break;
            case 1:
                mtvAreaRadio.setTextColor(getResources().getColor(R.color.theme));
                mtvAllRadio.setTextColor(Color.GRAY);
                transaction.show(areaDeviceFragment);
                transaction.commit();
                break;
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_addwifi) {
            //添加wifi设备
            initAddDialog(getActivity());
        } else if (v.getId() == R.id.refresh_btn) {//刷新的按钮
            showRefreshAnimation();
        }

    }


    public void updateDevice() {
        if (tvDeviceName == null || mListDevice == null)
            return;
        btnWgMsg.setText("网关 x " + mListDevice.size());

        //当前有可用网关时
        if (currentDevice != null) {
            //先判断该网关是否在线
            for (WifiDevice device : mListDevice) {
                if (device.getAddress().equals(currentDevice.getAddress())) {
                    if (device.getStatus() == WifiDevice.LOGIN_STATUS) {
                        //当前设备还处于在线状态 则不做处理
                        return;
                    }
                    break;
                }
            }
        }
        //当前无可用网关时
        currentDevice = null;
        for (int i = 0; i < mListDevice.size(); i++) {
            WifiDevice device = mListDevice.get(i);
            if (device.getStatus() == WifiDevice.LOGIN_STATUS) {
                currentDevice = device;
                break;
            }
        }
        if (currentDevice == null)
            tvDeviceName.setText(getString(R.string.nogetwg));
        else if (currentDevice.getName().isEmpty()) {
            tvDeviceName.setText(getString(R.string.newwg1));
        } else
            tvDeviceName.setText(currentDevice.getName());

        lightIndex.clear();
        isGetLightStatus = false;       //是否得到了各个灯的状态   （长连接得到）
        isGetLightList = false;

        context.currentDevice = currentDevice;

    }


    public void update(ArrayList<WifiDevice> devices) {
        mListDevice.clear();
        mListDevice.addAll(devices);
        updateDevice();
    }

    /**
     * 更新wifi设备的状态
     *
     * @param imei
     * @param status
     */
    public void setDeviceStatus(String imei, int status) {
        for (int i = 0; i < mListDevice.size(); i++) {
            WifiDevice d = mListDevice.get(i);
            if (d.getAddress().equals(imei)) {
                d.setStatus(status);
                return;
            }
        }
    }

    private void initAddDialog(final Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.addnew_dialog, null);
        final Dialog dlg = new Dialog(context, R.style.common_dialog);
        dlg.setContentView(view);
        dlg.show();
        RelativeLayout rl_addgateway = (RelativeLayout) view.findViewById(R.id.rl_addgatway);
        RelativeLayout rl_addlights = (RelativeLayout) view.findViewById(R.id.rl_addlamps);
        RelativeLayout rl_addpermisson = (RelativeLayout) view.findViewById(R.id.rl_permisson);
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.rl_addgatway:
                        //添加网关
                        Intent intent = new Intent(context, WifiConnectionActivity.class);
                        startActivityForResult(intent, LINK_DEVICE);
                        break;
                    case R.id.rl_addlamps:
                        //添加灯具
                        Intent intent1 = new Intent(context, AddLightActivity.class);
                        intent1.putExtra("group_index", 0);
                        startActivityForResult(intent1, ADD_LIGHT);
                        break;
                    case R.id.rl_permisson:
                        //添加权限
                        break;
                }
                dlg.cancel();
            }
        };
        rl_addgateway.setOnClickListener(listener);
        rl_addlights.setOnClickListener(listener);
        rl_addpermisson.setOnClickListener(listener);
        // 设置相关位置，一定要在 show()之后
        //Window window2 = dlg.getWindow();
        //WindowManager.LayoutParams params = window2.getAttributes();
        //window.setAttributes(params);
    }

    /**
     * 获取灯泡列表
     */
    public void requestLightList() {
        lightIndex.clear();
        isGetLightStatus = false;       //是否得到了各个灯的状态   （长连接得到）
        isGetLightList = false;

        if (currentDevice == null) {
            hideRefreshAnimation();
            return;
        }

        try {

            if (MyApplication.getInstance().mService != null) {
                context.showLoadingDialog("获取灯泡列表...", false);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        context.closeLoadingDialog();
                    }
                },12*1000);
                //获取wifi下的灯泡连接状态
                MyApplication.getInstance().mService.getLightList(currentDevice.getAddress());
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }


        //得到该设备在web服务器上的的灯泡列表
        RequestParams params = new RequestParams();
        params.put("imei", currentDevice.getAddress());
        RequestManager.post(Config.URL_GETLIGHTLIST, context, params, new RequestListener() {
            @Override
            public void requestSuccess(String json) {
                Log.d(TAG, json);
                isGetLightStatus = true;
                result = json;
                if (isGetLightList && isGetLightStatus) {
                    setListData(result);
                }
            }

            @Override
            public void requestError(VolleyError e) {
                if (e.networkResponse == null) {
                    Toast.makeText(context, getString(R.string.getlightlist_fail), Toast.LENGTH_SHORT).show();
                    context.showComReminderDialog();
                }
            }
        });


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult>>> requestCode:" + requestCode);
        if (data != null) {
            if (resultCode == Activity.RESULT_OK) {
                if (requestCode == LINK_DEVICE) {
                    Bundle b = data.getExtras();
                    int changed = b.getInt("ret", 0);
                    if (changed == 1) {
                        //通知主活动向web服务器请求更新设备列表
                        context.requestWifiList();
                    }
                } else if (requestCode == ADD_LIGHT) {
                    String light_name = data.getStringExtra("light_name");
                    int addGroupIndex = data.getIntExtra("group_index", 0);
                    String light_no = data.getStringExtra("light_no");
                    Log.d(TAG, "ADD_LIGHT>>>" + light_name + ">>" + addGroupIndex + "<<" + light_no);
                    //通知主活动更新设备列表
                    context.getLightInfo(light_name, addGroupIndex, light_no);
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);

    }


    public void updateLightStatus(byte[] list) {
        isGetLightList = true;
        lightStatuList = list;
        if (isGetLightList && isGetLightStatus) {
            setListData(result);
        }
    }


    /**
     * 根据返回来的所有灯泡列表  和所有灯泡状态  来设置 listView的数据
     *
     * @param result
     */
    public void setListData(String result) {
        context.closeLoadingDialog();
        hideRefreshAnimation();
        //isGetLightStatus = false;
        //isGetLightList = false;
        Log.d(TAG, "setListData，更新数据");
        if (result != null && (result.trim()).length() != 0) {
            try {
                JSONObject json = new JSONObject(result);
                if (JsonUtil.getInt(json, Config.CODE) != 1) {
                    context.showLongToast(JsonUtil.getStr(json, Config.ERRORCN));
                } else {
                    JSONArray lightsList = json.getJSONArray("lights");
                    LinkedList<Light> temlist = new LinkedList<>();
                    int index = 1;
                    String name = "";
                    Lg.i(TAG, "lightStatuList.length-->>" + lightStatuList.length);
                    for (int i = 0; i < lightsList.length(); i++) {
                        JSONObject ob = lightsList.getJSONObject(i);
                        if (ob.has("index")) {
                            index = ob.getInt("index");
                        }
                        if (ob.has("name")) {
                            name = ob.getString("name");
                        } else {
                            name = "unkown";
                        }
                        Light light = new Light();
                        light.setId("" + index);
                        Lg.i(TAG, "light_index-->>" + index + "   lightStatuList.length:" + lightStatuList.length);
                        if (index <= lightStatuList.length) {
                            light.setLightStatu(lightStatuList[index - 1]);
                            light.setName(name);
                        }
                        //在线或者离线状态
                        if (light.getLightStatu() != 0) {
                            lightIndex.put(index, temlist.size());
                            Lg.i(TAG, "lightIndex.put(index, temlist.size()->>" + index + "  " + temlist.size());
                            temlist.add(light);
                        }
                    }
                    //childList.add(temlist);
                    //更新视图项
                    allDeviceFragment.updateDeviceList(temlist);
                    context.lightList.clear();
                    context.lightList.addAll(temlist);
                    context.lightIndex.clear();
                    context.lightIndex.putAll(lightIndex);
                    Log.e(TAG, "xxxxxxxxxxxxxxxxxxxxtemlist:" + temlist.toString());

                }


                //adapter.notifyDataSetChanged();
            } catch (JSONException e) {
                Lg.i(TAG, "" + e.toString());
                e.printStackTrace();
            }
        }
    }


    //开启动画效果
    private void showRefreshAnimation() {
        // hideRefreshAnimation();
        refresh_view.setClickable(false);
        refresh_view.setImageResource(R.drawable.refresh_after);
        //显示刷新动画
        Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.refresh);
        refresh_view.startAnimation(animation);
        context.requestWifiList();


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                hideRefreshAnimation();
            }

        }, 10000);
    }

    //关闭动画效果
    private void hideRefreshAnimation() {
        if (refresh_view != null) {
            refresh_view.setClickable(true);
            refresh_view.setImageResource(R.drawable.refresh_before);
            refresh_view.clearAnimation();
        }
    }

}