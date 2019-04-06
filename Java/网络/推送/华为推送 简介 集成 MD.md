| Markdown版本笔记 | 我的GitHub首页 | 我的博客 | 我的微信 | 我的邮箱 |  
| :------------: | :------------: | :------------: | :------------: | :------------: |  
| [MyAndroidBlogs][Markdown] | [baiqiantao][GitHub] | [baiqiantao][博客] | bqt20094 | baiqiantao@sina.com |  
  
[Markdown]:https://github.com/baiqiantao/MyAndroidBlogs  
[GitHub]:https://github.com/baiqiantao  
[博客]:http://www.cnblogs.com/baiqiantao/  
  
  
***  
目录  
===  

- [华为推送简介](#华为推送简介)
	- [拉起应用注意事项](#拉起应用注意事项)
	- [集成步骤](#集成步骤)
	- [开发前自检](#开发前自检)
	- [简洁版自定义消息推送Demo](#简洁版自定义消息推送Demo)
		- [获取需要的SDK](#获取需要的SDK)
		- [初始化](#初始化)
		- [Activity](#Activity)
		- [Receiver](#Receiver)
		- [消息处理类](#消息处理类)
		- [配置文件](#配置文件)
		- [AndroidManifest.xml](#AndroidManifestxml)
  
# 华为推送简介  
[Demo地址](https://github.com/baiqiantao/PushTest.git)   
账号【谢总@asp.citic.com】【icitic52818888】  
应用包名：com.bqt.push  
APP ID：100257971  
[文档](http://developer.huawei.com/consumer/cn/service/hms/catalog/huaweipush_agent.html?page=hmssdk_huaweipush_devprepare_agent)  
  
## 拉起应用注意事项  
[原文说明](https://developer.huawei.com/consumer/cn/service/hms/catalog/huaweipush_agent.html?page=hmssdk_huaweipush_devguide_client_agent#7 是否允许接收push透传消息)  
  
应用需要创建一个子类继承com.huawei.hms.support.api.push.PushReceiver，实现onToken，onPushState ，onPushMsg，onEvent这几个抽象方法，用于接收token返回，push连接状态，透传消息和通知栏点击事件处理。  
由于是通过广播触发，所以当应用的进程不存在时可能由于系统原因无法通过广播方式拉起应用处理通知栏点击事件等。需要在手机上面为应用设置“允许自启动”才能在进程不存在时正常处理PushReceiver的广播。  
  
## 集成步骤  
1、注册成为开发者  
  
2、[登录](http://developer.huawei.com/consumer/cn)，创建应用  
点击“会员中心”，“创建产品”，在“创建产品”页面选择“移动应用”，创建新的应用。  
填写移动应用的基本信息：应用名称、应用类型和默认语言，点击“上传APK”或者“产品详情”，完成产品创建。  
  
3、生成证书指纹  
打开命令行工具，执行cd命令进入keytool.exe所在的目录：  
```java  
cd C:\Program Files\Java\jdk1.8.0_92\bin  
```  
  
获取对应的SHA256指纹  
```java  
keytool -list -v -keystore C:\_zxwk\zxwlke.keystore  
```  
  
获取的信息  
```java  
密钥库类型: JKS  
密钥库提供方: SUN  
  
您的密钥库包含 1 个条目  
  
别名: zxwk  
创建日期: 2014-11-21  
条目类型: PrivateKeyEntry  
证书链长度: 1  
证书[1]:  
所有者: CN=zxwk, OU=zxwk, O=zxwk, L=wuhan, C=CN  
发布者: CN=zxwk, OU=zxwk, O=zxwk, L=wuhan, C=CN  
序列号: 546ede5e  
有效期开始日期: Fri Nov 21 14:40:30 CST 2014, 截止日期: Sun Oct 28 14:40:30 CST 2114  
证书指纹:  
         MD5: DD:C9:E3:2A:77:D3:21:05:A4:C6:20:2F:EB:75:65:5C  
         SHA1: DA:FF:CF:09:E9:59:32:39:E7:0D:22:C5:3A:1C:CD:C8:9B:19:FC:7A  
         SHA256: 6D:3C:C4:74:46:E4:A4:27:4D:5F:97:53:E6:8D:EE:41:7F:FE:88:92:80:DE:02:F9:2B:76:76:92:E2:80:34:F8  
         签名算法名称: SHA1withRSA  
         版本: 3  
```  
  
4、申请PUSH服务  
登录“华为开发者联盟”，点击“会员中心”，在“我的产品”页面，选择创建的应用，进入产品详情页面，点击产品服务列表的“+”号添加PUSH服务。  
在配置信息界面填写应用包名、回调地址、SHA256指纹，应用包名必须和APK包名保持一致，且不可更改。回调地址配置后可修改。  
  
5、获取PUSH服务参数  
开发者为APP开通PUSH服务后，华为方会分配服务参数，用于开发者集成HMS SDK时使用。获取服务参数操作如下。  
登录“华为开发者联盟”，选择“会员中心 > 我的产品”，点击产品名称，进入指定的产品页面。  
点击“移动应用详情”链接，在弹出的“移动服务详情”对话框获取服务参数。  
```java  
产品名称：测试  
应用包名：com.bqt.push  
APP ID：100257971        applicationID，华为开发者联盟为APP分配的唯一标识  
APP SECRET：785bf3e10d53b0420b79a6c2ef97981a     应用秘钥，在开发PUSH 服务端时需要用到此参数  
```  
  
## 开发前自检  
  
| 参数 | 类型 | 来源 | 意义 |  
| ------------ | ------------ | ------------ |  
| Keystore-file | 文件 | 自行创建 | Android应用签名文件，和华为开发者联盟申请应用服务时填写的签名文件指纹匹配 |  
| Appid | 字符串 | 开发者联盟网站应用服务详情 | 开发者联盟给应用分配的唯一标识 |  
| 应用包名 | 字符串 | 开发者联盟网站创建应用时填写的包名 | Android应用的包名要与这个包名一致 |  
| APP SECRET | 字符串 | 开发者联盟，“移动应用详情”界面 | PUSH 服务端开发时发送消息需要 |  
  
注意：请务必确保开发环境和开发者联盟上使用的签名文件一致，否则连接华为移动服务将失败，错误码为 6003 或 907135702。  
  
## 简洁版自定义消息推送Demo  
### 获取需要的SDK  
1、去 [官网](http://developer.huawei.com/consumer/cn/service/hms/catalog/huaweipush_agent.html?page=hmssdk_huaweipush_sdkdownload_agent) 下载 HMS Agent套件(必选)  
  
![](index_files/215ddf08-77d6-4700-baa6-b91635412e14.png)  
   
这货是非常非常非常坑爹的，我日你个巴拉！下载之后会发现里面杂七杂八什么都系都有，有华为支付、华为账号、华为...等所有华为开放的服务，而且还是一个module！麻辣隔壁的，我就用一个推送(其他推送SDK只需写一个广播接受者就可以了)，你这货让我导入这么多东西怎么能忍！更坑爹的是，里面common包下有十几个文件，这是所有服务通用的包，就算把其他服务去掉，只有这是几个类这也是不能忍的！  
  
2、运行Agent下的【GetHMSAgent.bat】文件  
草你麻痹的，我移了半天，越搞错误越多，完全不能忍！这时就仔细看了下解压包根目录那一坨坨都是些什么垃圾东西，结果发现了新大陆！  
点击运行GetHMSAgent.bat文件可以从全量的 HMSAgent 代码，根据您的选择删除不需要的代码！  
![](index_files/946cf08a-84d6-4ed1-8a9c-21d9dea91882.png)  
  
这些爽翻了！  
不过用的时候要注意，运行过程会让你输入cpid，我们没有这玩意，随意输几个就可以了。  
  
3、运行后我们只需把 hms 包移到我们的项目中就可以了。  
![](index_files/9eb8c30e-0f46-491c-b62b-8df962c7f4e2.png)  
  
### 初始化  
```java  
public class App extends Application {  
    @Override  
    public void onCreate() {  
        super.onCreate();  
        boolean success = HMSAgent.init(this);  
        Log.i("bqt", ""+success);  
          
    }  
      
    @Override  
    public void onTerminate() {  
        super.onTerminate();  
        HMSAgent.destroy();  
    }  
}  
```  
  
### Activity  
```java  
public class HuaweiPushActivity extends ListActivity {  
      
    public static boolean isForeground = false;  
      
    private TextView tv;  
    private String[] array;  
      
    protected void onCreate(Bundle savedInstanceState) {  
        super.onCreate(savedInstanceState);  
        tv = new TextView(this);  
        tv.setTextColor(Color.BLUE);  
        getListView().addFooterView(tv);  
        array = new String[]{"获取token",  
                "删除token",  
                " 获取push状态",  
                "设置是否接收普通透传消息",  
                "设置接收通知消息",  
                "显示push协议",};  
        setListAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, new ArrayList<>(Arrays.asList(array))));  
        EventBus.getDefault().register(this);  
    }  
      
    private boolean b;  
      
    @Override  
    protected void onListItemClick(ListView l, View v, int position, long id) {  
        tv.append("\n" + array[position] + "  ");  
        b = !b;  
        switch (position) {  
            case 0:  
                HMSAgent.Push.getToken((rst) -> tv.append("+rst"));  
                break;  
            case 1://token=0990005841682350300001697200CN01  belongId=1  
                String token = getSharedPreferences("huawei_token", Context.MODE_PRIVATE).getString("token", null);  
                HMSAgent.Push.deleteToken(token, rst -> tv.append("" + rst));  
                break;  
            case 2:  
                HMSAgent.Push.getPushState(rst -> tv.append("" + rst));  
                break;  
            case 3:  
                HMSAgent.Push.enableReceiveNormalMsg(b, rst -> tv.append(b + "   " + rst));  
                break;  
            case 4:  
                HMSAgent.Push.enableReceiveNotifyMsg(b, rst -> tv.append(b + "   " + rst));  
                break;  
            case 5:  
                HMSAgent.Push.queryAgreement(rst -> tv.append("" + rst));  
                break;  
        }  
    }  
      
    @Override  
    protected void onResume() {  
        isForeground = true;  
        super.onResume();  
    }  
      
    @Override  
    protected void onPause() {  
        isForeground = false;  
        super.onPause();  
    }  
      
    @Override  
    protected void onDestroy() {  
        super.onDestroy();  
        EventBus.getDefault().unregister(this);  
    }  
      
    @Subscribe(threadMode = ThreadMode.MAIN)  
    public void onPushEvent(BasePushBean bean) {  
        TextView tv = new TextView(this);  
        tv.setTextColor(Color.BLUE);  
        tv.setText(bean.msg);  
        getListView().addFooterView(tv);  
    }  
}  
```  
  
### Receiver  
```java  
public class HuaweiPushRevicer extends PushReceiver {  
      
    /**  
     * 这里接收到穿透消息，自定义通知栏，和操作  
     *  
     * @param context 上下文  
     * @param msg     消息  
     * @param bundle  bundle  
     * @return 不清楚返回真假的作用  
     */  
    @Override  
    public boolean onPushMsg(Context context, byte[] msg, Bundle bundle) {  
        try {  
            String content = new String(msg, "UTF-8");  
            Log.i("bqt", "华为【onPushMsg】" + content);  
            PushMsgReceiverHelper.getInstance().onHuaweiPushMsgReceiver(content);  
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
        return false;  
    }  
      
    @Override  
    public void onToken(Context context, String token, Bundle extras) {  
        Log.i("bqt", "华为【onToken】token=" + token + "  belongId=" + extras.getString("belongId"));  
        //保存在本地备用  
        context.getSharedPreferences("huawei_token", Context.MODE_PRIVATE).edit().putString("token", token).apply();  
        Toast.makeText(context, token, Toast.LENGTH_SHORT).show();  
    }  
      
    /**  
     * 这里接收到的是通知栏消息，无法自定义操作，默认打开应用  
     *  
     * @param context 上下文  
     * @param event   事件  
     * @param extras  额外  
     */  
    public void onEvent(Context context, Event event, Bundle extras) {  
        int notifyId = extras.getInt(BOUND_KEY.pushNotifyId, 0);  
        String message = extras.getString(BOUND_KEY.pushMsgKey);  
        Log.i("bqt", "华为【onEvent】event=" + event + "  notifyId=" + notifyId + "  message=" + message);  
          
        if (Event.NOTIFICATION_OPENED.equals(event) || Event.NOTIFICATION_CLICK_BTN.equals(event)) {  
            NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);  
            if (0 != notifyId && manager != null) {  
                manager.cancel(notifyId);  
            }  
        }  
        super.onEvent(context, event, extras);  
    }  
      
    /**  
     * 得到华为推送的连接状态  
     *  
     * @param context   上下文  
     * @param pushState 连接状态  
     */  
    @Override  
    public void onPushState(Context context, boolean pushState) {  
        Log.i("bqt", "华为【onPushState】连接状态：" + pushState);  
    }  
}  
```  
  
### 消息处理类  
```java  
/**  
 * 处理华为光推送推过来的自定义消息  
 */  
public void onHuaweiPushMsgReceiver(String message) {  
    Log.i("bqt", "【华为推送】" + message);  
    if (HuaweiPushActivity.isForeground) {  
        EventBus.getDefault().post(new BasePushBean(message, BasePushBean.TYPE_STRING));  
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
        maven {url 'http://developer.huawei.com/repo'}//华为maven仓路径  
    }  
}  
```  
  
**module的build.gradle**  
```java  
implementation 'com.huawei.android.hms:push:2.6.0.301'  
```  
  
```java  
// 签名配置  
signingConfigs {  
    release {  
        storeFile file("./push.keystore")  
        storePassword "***"  
        keyAlias "zxwk"  
        keyPassword "***"  
    }  
}  
```  
  
**混淆文件：proguard-android.txt**  
```java  
-ignorewarning  
-keepattributes *Annotation*  
-keepattributes Exceptions  
-keepattributes InnerClasses  
-keepattributes Signature  
-keepattributes SourceFile,LineNumberTable  
-keep class com.hianalytics.android.**{*;}  
-keep class com.huawei.updatesdk.**{*;}  
-keep class com.huawei.hms.**{*;}  
  
-keep class com.huawei.gamebox.plugin.gameservice.**{*;}  
  
-keep public class com.huawei.android.hms.agent.** extends android.app.Activity { public *; protected *; }  
-keep interface com.huawei.android.hms.agent.common.INoProguard {*;}  
-keep class * extends com.huawei.android.hms.agent.common.INoProguard {*;}       
```  
  
### AndroidManifest.xml  
**添加权限：**  
```java  
<uses-permission android:name="android.permission.INTERNET"/>  
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"/>  
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"/>  
<uses-permission android:name="android.permission.ACCESS_WIFI_STATE"/>  
<uses-permission android:name="android.permission.READ_PHONE_STATE"/>  
```  
  
**在application节点下增加APPID：**  
```java  
<meta-data  
    android:name="com.huawei.hms.client.appid"  
    android:value="appid=100257971"/>  
```  
  
**注册四大组件：**  
```java  
<!-- =============================== 注册Activity =============================== -->  
<!-- 使用 HMSAgent 代码接入HMSSDK 需要注册的activity-->  
<activity  
    android:name=".hms.agent.common.HMSAgentActivity"  
    android:configChanges="orientation|locale|screenSize|layoutDirection|fontScale"  
    android:excludeFromRecents="true"  
    android:exported="false"  
    android:hardwareAccelerated="true"  
    android:theme="@android:style/Theme.Translucent" >  
    <meta-data  
        android:name="hwc-theme"  
        android:value="androidhwext:style/Theme.Emui.Translucent" />  
</activity>  
<!-- 接入HMSSDK 需要注册的activity，定义了HMS-SDK中一些跳转所需要的透明页面-->  
<activity  
    android:name="com.huawei.hms.activity.BridgeActivity"  
    android:configChanges="orientation|locale|screenSize|layoutDirection|fontScale"  
    android:excludeFromRecents="true"  
    android:exported="false"  
    android:hardwareAccelerated="true"  
    android:theme="@android:style/Theme.Translucent">  
    <meta-data  
        android:name="hwc-theme"  
        android:value="androidhwext:style/Theme.Emui.Translucent"/>  
</activity>  
  
<!-- 应用自升级接口所需要使用的页面-->  
<activity  
    android:name="com.huawei.updatesdk.service.otaupdate.AppUpdateActivity"  
    android:configChanges="orientation|screenSize"  
    android:exported="false"  
    android:theme="@style/upsdkDlDialog">  
    <meta-data  
        android:name="hwc-theme"  
        android:value="androidhwext:style/Theme.Emui.Translucent.NoTitleBar"/>  
</activity>  
  
<!-- 应用自升级接口所需要使用的页面-->  
<activity  
    android:name="com.huawei.updatesdk.support.pm.PackageInstallerActivity"  
    android:configChanges="orientation|keyboardHidden|screenSize"  
    android:exported="false"  
    android:theme="@style/upsdkDlDialog">  
    <meta-data  
        android:name="hwc-theme"  
        android:value="androidhwext:style/Theme.Emui.Translucent"/>  
</activity>  
  
<!-- =============================== 注册Revicer =============================== -->  
<!-- 接收Push消息广播，此receiver需要开发者自己创建并继承PushReceiver类-->  
<receiver android:name=".HuaweiPushRevicer">  
    <intent-filter>  
        <!-- 必须,用于接收token-->  
        <action android:name="com.huawei.android.push.intent.REGISTRATION"/>  
        <!-- 必须，用于接收消息-->  
        <action android:name="com.huawei.android.push.intent.RECEIVE"/>  
        <!-- 可选，用于点击通知栏或通知栏上的按钮后触发onEvent回调-->  
        <action android:name="com.huawei.android.push.intent.CLICK"/>  
        <!-- 可选，查看push通道是否连接，不查看则不需要 -->  
        <action android:name="com.huawei.intent.action.PUSH_STATE"/>  
    </intent-filter>  
</receiver>  
  
<!-- 接收通道发来的通知栏消息 -->  
<receiver android:name="com.huawei.hms.support.api.push.PushEventReceiver">  
    <intent-filter>  
        <action android:name="com.huawei.intent.action.PUSH"/>  
    </intent-filter>  
</receiver>  
  
<!-- =============================== 注册Provider =============================== -->  
<!-- 用于HMS-SDK引导升级HMS，提供给系统安装器读取升级文件，这边 要替换上您应用的包名-->  
<provider  
    android:name="com.huawei.hms.update.provider.UpdateProvider"  
    android:authorities="com.bqt.push.hms.update.provider"  
    android:exported="false"  
    android:grantUriPermissions="true"/>  
  
<!-- 用于应用自升级，这边  要替换上您应用的包名-->  
<provider  
    android:name="com.huawei.updatesdk.fileprovider.UpdateSdkFileProvider"  
    android:authorities="com.bqt.push.updateSdk.fileProvider"  
    android:exported="false"  
    android:grantUriPermissions="true">  
</provider>  
  
<!-- =============================== 注册Service =============================== -->  
<!-- 应用下载服务，用于应用自升级-->  
<service  
    android:name="com.huawei.updatesdk.service.deamon.download.DownloadService"  
    android:exported="false"/>  
```  
  
2018-4-19  
