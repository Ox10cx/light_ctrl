// ICallBack.aidl
package com.zsg.jx.lightcontrol;

// Declare any non-default types here with import statements
interface ICallback {
       void onConnect(String address);
       void onDisconnect(String address);
       boolean onRead(String address, in byte[] val);
       boolean onWrite(String address, out byte[] val);
       void onNotify(String imei, int type);
       void onSwitchRsp(String imei, boolean ret,int cmdnum);
       //得到灯泡开启关闭状态回调
       void onGetStatusRsp(String imei, int ret,int cmdnum);
       void onCmdTimeout(String cmd, String imei,int cmdnum);
       void onPingRsp(String imei, int ret);
        //得到灯泡是否在线离线状态回调
       void onGetLightList(String imei, out byte[] list,int cmdnum);
       void onSetBrightChromeRsp(String imei, int ret,int cmdnum);
       void onGetBrightChromeRsp(String imei, int index, int bright, int chrome,int cmdnum);

       void onPairLightRsp(String imei, int ret,int cmdnum);

       void onRemoveLightRsp(String imei,int ret,int cmdnum);

       //得到设备ip回调
       void onGetDeviceIpRsp(String imei,String ip,int port,int cmdnum);

       void onConnectP2P(String imei);

       void onDisconnectP2P(String address);
}
