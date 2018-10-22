[Markdown版本](https://github.com/baiqiantao/MyAndroidBlogs)
[我的GitHub](https://github.com/baiqiantao)


---------------


广播接收者 BroadcastReceiver 常用方式
***
目录
===
[TOC]

## 自定义广播案例，本地广播
自定义广播
> LocalBroadcastManager 发出的广播只会在APP内部传播，且广播接收者也只能收到本APP发出的广播！本地广播无法通过静态方式注册，相比起系统全局广播更加安全和高效。

```java
public static final String ACTION_NOTIFY_H5_LOGIN = "broadcast.action_notify_h5_login";
```
```java
//一般定义为内部类
class MyReceiver extends BroadcastReceiver {
   
   @Override
   public void onReceive(Context context, Intent intent) {
      if (intent != null && intent.getAction() != null) {
         switch (intent.getAction()) {
            case ACTION_NOTIFY_H5_LOGIN:
               int status = intent.getIntExtra("status", 0);
               //收到广播后的逻辑
               break;
            default:
               break;
         }
      }
   }
}
```

动态注册、取消注册广播（不可以在清单文件中静态注册）
```java
private void registerH5Receiver() {
   myReceiver = new MyReceiver();
   IntentFilter intentFilter = new IntentFilter();
   intentFilter.addAction(ACTION_NOTIFY_H5_LOGIN);//可以添加多个Action
   //registerReceiver(myReceiver, intentFilter);
   LocalBroadcastManager.getInstance(mContext).registerReceiver(myReceiver, intentFilter);
}
```
```java
@Override
protected void onDestroy() {
   super.onDestroy();
   if (myReceiver != null) {
      //unregisterReceiver(myReceiver);
      LocalBroadcastManager.getInstance(mContext).unregisterReceiver(myReceiver);
   }
}
```

发送广播
```java
private void sendLoginBroadcast(int status) {
   Intent intent = new Intent(ACTION_NOTIFY_H5_LOGIN);
   intent.putExtra("status", status);
   //设置此Flag后，即使APP已退出，能匹配此Action的广播接收者依然可以接收到此广播
   //intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
   //mContext.sendBroadcast(intent);
   LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
}
```

## 获取及监听有线耳机、蓝牙耳机的连接状态
```java
public class MainActivity extends Activity {
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        registerHeadsetPlugReceiver();
        AudioManager manager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        Log.i("【bqt】", "有线耳机是否连接：" + manager.isWiredHeadsetOn());
        getBluetoothState();
    }
    
    private void registerHeadsetPlugReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_HEADSET_PLUG); //监听有线耳机系统广播
        intentFilter.addAction(BluetoothHeadset.ACTION_CONNECTION_STATE_CHANGED);//监听蓝牙耳机系统广播
        intentFilter.addAction(AudioManager.ACTION_AUDIO_BECOMING_NOISY);//监听耳机的断开连接的事件
        registerReceiver(headsetPlugReceiver, intentFilter);
    }
    
    private BroadcastReceiver headsetPlugReceiver = new BroadcastReceiver() {
        
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothHeadset.ACTION_CONNECTION_STATE_CHANGED.equals(action)) {
                getBluetoothState();
            } else if (Intent.ACTION_HEADSET_PLUG.equals(action)) {
                int state = intent.hasExtra("state") ? intent.getIntExtra("state", -1) : -1;//0代表拔出，1代表插入
                //当拔出有线耳机时，要等上一秒钟，才会收到Android的系统广播，所以如果对时间要求比较高，可以使用下面的广播
                Log.i("【bqt】", "有线耳机状态=" + state);
            } else if (AudioManager.ACTION_AUDIO_BECOMING_NOISY.equals(action)) {
                Log.i("【bqt】", "耳机断开了");
            }
        }
    };
    
    private int getBluetoothState() {
        //权限 <uses-permission android:name="android.permission.BLUETOOTH"/>
        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        int state = adapter.getProfileConnectionState(BluetoothProfile.HEADSET);
        //BluetoothProfile.STATE_DISCONNECTED  = 0;  _CONNECTING= 1;  _CONNECTED= 2;  _DISCONNECTING = 3;
        Log.i("【bqt】", "蓝牙耳机状态=" + state);
        return state;
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(headsetPlugReceiver);
    }
}
```
