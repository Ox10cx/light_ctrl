package com.zsg.jx.lightcontrol.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.zsg.jx.lightcontrol.R;
import com.zsg.jx.lightcontrol.model.DeviceArea;
import com.zsg.jx.lightcontrol.model.DeviceGroup;

import java.util.ArrayList;

/**
 * Created by zsg on 2016/8/15.
 */
public class AreaDeviceAdapter extends BaseAdapter {
    private ArrayList<DeviceArea> datas;
    private Context context;
    private LayoutInflater inflater;

    public AreaDeviceAdapter(Context context) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        datas = new ArrayList<>();
    }


    public void updateData(ArrayList<DeviceArea> newdata) {
        datas.clear();
        datas.addAll(newdata);
        //添加自定义视图项  名字为空
        DeviceArea deviceArea=new DeviceArea("");
        datas.add(deviceArea);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return datas.size();
    }

    @Override
    public Object getItem(int position) {
        return datas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holderView;

        if (convertView == null) {
            holderView = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(
                    R.layout.devicearea_item, null);
            holderView.name1 = (TextView) convertView.findViewById(R.id.tv_areaname1);
            holderView.name2 = (TextView) convertView.findViewById(R.id.tv_areaname2);
            convertView.setTag(holderView);
        } else {
            holderView = (ViewHolder) convertView.getTag();
        }

        //判断是否是最后一个
        if(position==datas.size()-1){
            holderView.name1.setText("");
            holderView.name1.setBackground(context.getResources().getDrawable(R.drawable.defines_icon2));
            holderView.name2.setText("自定义");
            return convertView;
        }

        DeviceArea group = datas.get(position);

        if (group.getName().length() > 2)
            holderView.name1.setText(group.getName().substring(0, 2));
        else
            holderView.name1.setText(group.getName());
        holderView.name2.setText(group.getName());

        return convertView;
    }

    private final static class ViewHolder {
        public TextView name1;
        public TextView name2;
    }
}

