| Markdown版本笔记 | 我的GitHub首页 | 我的博客 | 我的微信 | 我的邮箱 |  
| :------------: | :------------: | :------------: | :------------: | :------------: |  
| [MyAndroidBlogs][Markdown] | [baiqiantao][GitHub] | [baiqiantao][博客] | bqt20094 | baiqiantao@sina.com |  
  
[Markdown]:https://github.com/baiqiantao/MyAndroidBlogs  
[GitHub]:https://github.com/baiqiantao  
[博客]:http://www.cnblogs.com/baiqiantao/  
  
  
***  
目录  
===  

- [魅族推送简介](#魅族推送简介)
	- [FAQ](#FAQ)
	- [简洁版自定义消息推送Demo](#简洁版自定义消息推送Demo)
		- [Receiver](#Receiver)
		- [配置文件](#配置文件)
		- [AndroidManifest.xml](#AndroidManifestxml)
  
# 魅族推送简介  
[Demo地址](https://github.com/baiqiantao/PushTest.git)   
  
[魅族官方GitHub](https://github.com/MEIZUPUSH)  
[魅族推送文档](https://github.com/MEIZUPUSH/PushDemo#pushsdk_internal)  
[集成推送平台PushSDK设计文档](https://github.com/MEIZUPUSH/UpsDemo)  
  
**应用背景**  
app为了及时获取到服务器端的消息更新，一般会采用轮寻或者推送的方式来获取消息更新，轮寻导致终端设备流量、电量、等系统资源严重浪费，所以目前采用的比较广泛的是推送的方式，目前 Meizu 的 Push SDK 不能脱离 Flyme OS 存在，当该 SDK 脱离 Flyme OS 之后由于没有长链接导致不能正常收到推送消息。本 SDK 首先要解决的是长链接由 SDK 自己维护，同时还要解决的就是多个 app 引用同一个 SDK 时长链接的复用问题。  
  
**设计思想**  
该 SDK 以 Android Service 方式运行，独占一个进程，该 Service 自己维护与推送服务器的长链接。如果一款手机安装了多个集成了 SDK 的手机应用，则只有一个 service 实例运行，不会每个应用都会开启一个后台 service，而是采用多个应用共享一个Push通道的方式，这就解决了长链接复用的问题，节省了对流量、电量的浪费。使用该 SDK 只需要关心 PushManager 提供的API，与 MzPushMessageReceiver 提供的回调接口以及相应的配置即可。  
  
## FAQ  
魅族推送服务是否支持所有Android平台？   
> 魅族推送服务适用所有安卓系统，但仅在Flyme系统上有系统级的推送服务，包括基于YunOS的Flyme系统，及安装了Flyme系统的非魅族手机。  
  
魅族推送SDK与其他推送SDK同时集成时是否会有冲突？  
> 在与其他推送SDK同时集成时不会有冲突。  
  
魅族推送平台的服务是收费服务吗？  
基础推送功能是免费的，定制功能会考虑收费。  
  
魅族推送平台是否可以统计到达数、点击数等数据？   
> 通知栏消息可以统计到推送数、接收数、展示数、点击数等数据，透传消息可以统计到推送数、接收数，展示数，点击数需客户端埋点上报。  
  
魅族推送消息提示类型是否支持自定义？  
> 魅族推送消息暂时不支持自定义通知提示，但是在高级设置中的设置通知提醒类型（震动、闪灯、声音）。用户收到消息时优先由系统设置项控制是否按照指定类型提醒。  
  
通过服务器传的参数，客户端获取一定要用getString？服务器接了API也是string类型的？   
> 全部都会转为string  
  
PushId及用户订阅关系在用户重装后是否会发生变化？  
> 用户重装后不会有变化。  
  
## 简洁版自定义消息推送Demo  
PushSDK3.0以后的版本使用了最新的魅族插件发布aar包，因此大家可以直接引用aar包；对于一些通用的权限配置，工程混淆，应用可以不再配置了，现有你只需要在你的应用中配置相应的消息接收的receiver  
  
### Receiver  
```java  
public class MZPushReceiver extends MzPushMessageReceiver {  
      
    @Override  
    //接收服务器推送的透传消息  
    public void onMessage(Context context, String s) {  
        Log.i("bqt", "魅族【onMessage】" + s);  
    }  
      
    @Override  
    //注册。调用PushManager.register(context方法后，会在此回调注册状态应用在接受返回的pushid  
    public void onRegister(Context context, String pushid) {  
        Log.i("bqt", "魅族【onRegister】" + pushid);  
    }  
      
    @Override  
    //取消注册。调用PushManager.unRegister(context）方法后，会在此回调反注册状态  
    public void onUnRegister(Context context, boolean b) {  
        Log.i("bqt", "魅族【onUnRegister】" + b);  
    }  
      
    @Override  
    //设置通知栏小图标。重要！详情参考应用小图标自定设置  
    public void onUpdateNotificationBuilder(PushNotificationBuilder builder) {  
        Log.i("bqt", "魅族【onUpdateNotificationBuilder】" + builder.getmNotificationsound() + "  "  
                + builder.getmLargIcon() + "  " + builder.getmNotificationDefaults() + "  " + builder.getmNotificationFlags() + "  "  
                + builder.getmStatusbarIcon() + "  " + Arrays.toString(builder.getmVibratePattern()));  
        builder.setmStatusbarIcon(R.drawable.ic_launcher);  
    }  
      
    @Override  
    //检查通知栏和透传消息开关状态回调  
    public void onPushStatus(Context context, PushSwitchStatus pushSwitchStatus) {  
        Log.i("bqt", "魅族【onPushStatus】" + pushSwitchStatus.toString());  
    }  
      
    @Override  
    //调用新版订阅PushManager.register(context,appId,appKey)回调  
    public void onRegisterStatus(Context context, RegisterStatus registerStatus) {  
        Log.i("bqt", "魅族【onRegisterStatus】" + registerStatus.toString());  
    }  
      
    @Override  
    //新版反订阅回调  
    public void onUnRegisterStatus(Context context, UnRegisterStatus unRegisterStatus) {  
        Log.i("bqt", "魅族【onUnRegisterStatus】" + unRegisterStatus.toString());  
    }  
      
    @Override  
    //标签回调  
    public void onSubTagsStatus(Context context, SubTagsStatus subTagsStatus) {  
        Log.i("bqt", "魅族【onSubTagsStatus】" + subTagsStatus.toString());  
    }  
      
    @Override  
    //别名回调  
    public void onSubAliasStatus(Context context, SubAliasStatus subAliasStatus) {  
        Log.i("bqt", "魅族【onSubAliasStatus】" + subAliasStatus.toString());  
    }  
      
    @Override  
    //通知栏消息到达回调，flyme6基于android6.0以上不再回调  
    public void onNotificationArrived(Context context, MzPushMessage mzPushMessage) {  
        Log.i("bqt", "魅族【onNotificationArrived】" + mzPushMessage.toString());  
    }  
      
    @Override  
    //通知栏消息点击回调  
    public void onNotificationClicked(Context context, MzPushMessage mzPushMessage) {  
        Log.i("bqt", "魅族【onNotificationClicked】" + mzPushMessage.toString());  
    }  
      
    @Override  
    //通知栏消息删除回调；flyme6基于android6.0以上不再回调  
    public void onNotificationDeleted(Context context, MzPushMessage mzPushMessage) {  
        Log.i("bqt", "魅族【onNotificationDeleted】" + mzPushMessage.toString());  
    }  
}  
```  
  
### 配置文件  
**project的build.gradle**  
```java  
allprojects {  
    repositories {  
        google()  
        jcenter()  
        maven {url 'https://oss.jfrog.org/artifactory/oss-snapshot-local'}//魅族maven仓路径  
    }  
}  
```  
  
**module的build.gradle**  
```java  
implementation 'com.meizu.flyme.internet:push-internal:3.5.0@aar'//魅族推送  
```  
  
### AndroidManifest.xml  
**添加权限：**  
<permission  
```java  
android:name="com.bqt.push.push.permission.MESSAGE"  
android:protectionLevel="signature"/>  
<permission  
android:name="com.bqt.push.permission.C2D_MESSAGE"  
android:protectionLevel="signature"/>  
  
<!-- 兼容flyme5.0以下版本-->  
<uses-permission android:name="com.meizu.flyme.push.permission.RECEIVE"/>  
<uses-permission android:name="com.bqt.push.push.permission.MESSAGE"/>  
<!--  兼容flyme3.0配置权限-->  
<uses-permission android:name="com.meizu.c2dm.permission.RECEIVE"/>  
<uses-permission android:name="com.bqt.push.permission.C2D_MESSAGE"/>  
```  
  
**注册四大组件**  
只需注册自定义的继承自 MzPushMessageReceiver 的接收消息的Receiver即可  
```java  
<receiver  
    android:name="com.bqt.push.MZPushReceiver"  
    tools:ignore="ExportedReceiver">  
    <intent-filter>  
    <!-- 接收push消息 -->  
    <action android:name="com.meizu.flyme.push.intent.MESSAGE"/>  
    <!-- 接收register消息 -->  
    <action android:name="com.meizu.flyme.push.intent.REGISTER.FEEDBACK"/>  
    <!-- 接收unregister消息-->  
    <action android:name="com.meizu.flyme.push.intent.UNREGISTER.FEEDBACK"/>  
    <!-- 兼容低版本Flyme3推送服务配置 -->  
    <action android:name="com.meizu.c2dm.intent.REGISTRATION"/>  
    <action android:name="com.meizu.c2dm.intent.RECEIVE"/>  
  
    <category android:name="com.bqt.push"/>  
    </intent-filter>  
</receiver>  
```  
  
2018-4-20  
