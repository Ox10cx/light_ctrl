package com.zsg.jx.lightcontrol.model;

/**
 * 设备区域划分
 * Created by zsg on 2016/8/15.
 */
public class DeviceArea {
    private String name;    //区域名称

    public DeviceArea(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
