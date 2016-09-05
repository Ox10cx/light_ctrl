package com.zsg.jx.lightcontrol.ui;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.Context;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListView;

import com.zsg.jx.lightcontrol.R;
import com.zsg.jx.lightcontrol.adapter.AreaDeviceAdapter;
import com.zsg.jx.lightcontrol.model.DeviceArea;

import java.util.ArrayList;

/**
 * 区域碎片
 * Created by zsg on 2016/8/15.
 */
@SuppressLint("ValidFragment")
public class AreaDeviceFragment extends Fragment {
    private Context context;
    private ListView mlistView;
    private ArrayList<DeviceArea> datas;
    private GridView mgridView;
    private AreaDeviceAdapter adapter;

    public AreaDeviceFragment(Context context) {
        this.context = context;
    }

    public AreaDeviceFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_area_device, container, false);
        initView(v);
        return v;
    }

    private void initView(View v) {
        mgridView = (GridView) v.findViewById(R.id.area_list);
        adapter = new AreaDeviceAdapter(context);
        initData();
        adapter.updateData(datas);
        mgridView.setAdapter(adapter);

        mgridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });

        mgridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                //添加区域

                return false;
            }
        });
    }

    private void initData() {
        datas = new ArrayList<>();
        DeviceArea deviceArea = new DeviceArea("我的房间");
        DeviceArea deviceArea2 = new DeviceArea("宝贝的房间");
        DeviceArea deviceArea3 = new DeviceArea("客厅");
        datas.add(deviceArea);
        datas.add(deviceArea2);
        datas.add(deviceArea3);

    }

}
