| Markdown版本笔记 | 我的GitHub首页 | 我的博客 | 我的微信 | 我的邮箱 |  
| :------------: | :------------: | :------------: | :------------: | :------------: |  
| [MyAndroidBlogs][Markdown] | [baiqiantao][GitHub] | [baiqiantao][博客] | bqt20094 | baiqiantao@sina.com |  
  
[Markdown]:https://github.com/baiqiantao/MyAndroidBlogs  
[GitHub]:https://github.com/baiqiantao  
[博客]:http://www.cnblogs.com/baiqiantao/  
  
  
***  
目录  
===  

- [小米推送介绍](#小米推送介绍)
	- [FAQ](#FAQ)
	- [简洁版自定义消息推送Demo](#简洁版自定义消息推送Demo)
		- [在适当的时候初始化](#在适当的时候初始化)
		- [Activity](#Activity)
		- [Receiver](#Receiver)
		- [消息处理类](#消息处理类)
		- [配置文件](#配置文件)
- [可以防止一个误报的 warning 导致无法成功编译，如果编译使用的 Android 版本是 23。](#可以防止一个误报的-warning-导致无法成功编译，如果编译使用的-Android-版本是-23。)
  
# 小米推送介绍  
[Demo地址](https://github.com/baiqiantao/PushTest.git)  
[推送文档](https://dev.mi.com/console/doc/detail?pId=230)  
[控制台](http://admin.xmpush.xiaomi.com/v2/app/nav)  
[SDK下载](https://dev.mi.com/mipush/downpage/)  
  
【17329910039】【bqt中信账号】  
  
小米相比极光等推送SDK的优势：  
- MIUI上系统级通道：在MIUI上系统级长连接，最大程度提高消息送达率  
- iOS/Android全平台支持：支持两大系统的推送服务，iOS开发者还可以将存量用户无缝迁移到小米推送中  
- 稳定 安全 高效：每秒百万级推送速度，亿级同时在线，99.8%的消息300毫秒内到达  
- 使用简单灵活：客户端0.5天集成，也可通过服务端API与业务逻辑相结合  
- 细致全面的统计工具：提供细致全面的统计工具，帮助开发者精准把握推送的使用情况  
- 自助调查工具：开发者可以自助查询设备与消息的送达/在线情况  
  
## FAQ  
[FAQ1](https://dev.mi.com/console/doc/detail?pId=283)  
[FAQ2](https://dev.mi.com/console/doc/detail?pId=66)  
  
**小米推送服务有哪些限制？**  
目前针对首批合作开发者，小米推送服务没有设置任何推送频率的使用限制，之后出于防止恶意应用攻击等考虑，可能会增加对推送消息的频率、对单一用户可以接收的数量等做一些限制，但不会影响开发者的正常使用。而且所提供的推送服务完全免费。 对于单条消息,可携带的数据量最大不能超过4KB。    
  
**Android版推送中，多个app都使用推送时，他们会共享连接吗？**  
在最新MIUI上，会直接使用系统长连接通道，所有app会和系统共享一个长连接；在其他rom上，目前没有共享连接。多通道的设计从通道安全性和流量计算上会更加合理，并且小米推送的多通道实现可以保证多条长连接对系统电量的影响和一条长连接基本相同。  
  
**什么是透传？**  
透传类推送是指开发者可选择不通过任何预定义的方式展现，由应用直接接收推送消息。利用透传消息，开发者可自定义更多使用推送的方式和展现形式，从而能更为灵活地使用消息推送通道。 在一些拥有应用启动管理功能的Android系统上（如MIUI），透传的实现需要应用在后台处于启动状态。    
  
**透传和通知栏，在送达率上有什么分别？**  
- 首先解释一下透传和通知栏方式的原理，透传是指当小米推送服务客户端SDK接收到消息之后，直接把消息通过回调方法发送给应用，不做任何处理；而通知栏方式，则在设备接收到消息之后，首先由小米推送服务SDK弹出标准安卓通知栏通知，在用户点击通知栏之后，激活应用。   
- 在非MIUI系统中，由于维护小米推送服务长连接的service是寄生在App的运行空间当中的，因此透传和通知栏方式在送达率上并没有任何区别，都需要应用驻留在后台。即，如果一台设备通知栏消息能够接收到并弹出，那么其透传消息也同样能接收到。   
- 在MIUI系统中，由于长连接是由MIUI系统服务建立并维护的，因此在接收消息的时候并不需要应用驻留后台。如果采用通知栏方式接收消息，由于通知栏也是MIUI系统服务弹出的，就可以做到不需要用户后台驻留或者可以自启动消息就能送达。而如果采用透传消息，由于需要直接执行应用的代码，因此即使消息已经到了系统服务，如果应用没有驻留后台或者能自启动，消息依然不能送达，需等下次用户手动点击激活应用后，才能接收到消息。   
- 综上，在MIUI系统中，通知栏消息的送达率会远高于透传方式；在非MIUI系统中，通知栏和透传方式的送达率是一样的。        
  
**使用推送服务demo没有成功是什么原因？**  
一般注册推送服务没成功，常见的原因如下：   
- 1.没有开启推送服务。使用推送服务前需要登录开发者账号、创建应用、开通推送服务3个步骤，缺一不可。   
- 2.没有正确配置AndroidManifest.xml文件。需要特别注意包名、权限部分，包名需要跟开发者站点上开通推送服务的一致，权限的前缀需要改成包名。   
- 3.系统时间错误。由于小米推送服务需要使用https请求向服务器注册一个匿名账号，在次过程中如果系统时间错误，会引起https过期，导致注册不成功。   
- 4.联网被阻止。小米推送服务客户端需要使用5222和443两个端口，如果在公司内网，需要联系IT部门把这两个端口开放。同时需要检查应用的联网是否会被一些手机安全助手阻止。需要特别注意的是，在MIUI系统上，长连接是由“小米服务框架”这个系统应用维护的，因此需要确保这个应用的联网并没有被阻止。      
  
**如果我使用通知栏类型消息，能否在通知栏消息到达之前，先执行一段app的代码？或者在通知栏到达时，通知app？**  
在MIUI系统上，通知栏类型的消息，是不需要应用启动就能弹出的（这一特性决定了通知栏消息的弹出可以不受应用自启动管理的影响），因此在整个弹出通知栏消息的过程中，app是完全不可感知的，当用户点击通知栏消息之后，才会执行到app的代码。  
  
**为什么onNotificationMessageArrived方法没被调用到？**  
- 首先，确定你的接入是否正确，这个方法需要在manifest中添加 `<action android:name=”com.xiaomi.mipush.MESSAGE_ARRIVED” /`> 这个action。 在接入正确的前提下，这个方法也不是保证一定能被调用的。  
- 在MIUI系统上，这个方法的调用需要同时满足如下两个条件：  
    - 1.新版的MIUI。这个特性是在2015年才加进小米推送服务的，因此需要MIUI升级到较新的版本才能调用这个方法。  
    - 2.需要应用驻留后台。小米推送服务的通知栏消息，是可以在应用不启动的前提下，就弹出通知栏消息的，在这种情况下， 由于MIUI的自启动管理，限制了应用不能在被杀后被后台唤醒，所以推送消息不能直接唤醒应用执行这个方法。  
  
**为什么我在onNotificationMessageClicked方法中的startActivity不能调起目标界面？**  
由于onNotificationMessageClicked中传入的context是application context，本身没有activity栈，因此需要在创建activity时候加入NEW_TASK的flag：   
```java  
Intent i = new Intent(context, MyActivity.class);   
i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);   
context.startActivity(i);  
```  
  
**当我的应用被杀掉之后，还能否接收到小米推送服务的消息？**  
有如下几种情况：如果是在MIUI系统中，使用通知栏类型的消息，是不需要应用出于启动状态就能接收并弹出通知栏的。使用透传消息，则需要应用驻留后台才能接收，由于MIUI的自启动管理限制，所以如果应用被杀，是收不到透传消息的。而如果是在非MIUI系统中，是需要应用驻留后台才能接收消息的，因此如果应用被杀死并且不能后台自启动的话，是没有办法接收消息的。为了让app尽可能的驻留后台，小米推送服务SDK监听了网络变化等系统事件，并且有应用之间的互相唤醒，但这些措施并不能保证应用可以一直在后台驻留。  
  
## 简洁版自定义消息推送Demo  
### 在适当的时候初始化  
```java  
private void initMiPush() {  
    String APP_ID = "1000270";  
    String APP_KEY = "670100056270";  
      
    // 注册push服务，注册成功后会向Receiver发送广播，可以从onCommandResult方法中的参数中获取注册信息  
    if (PushUtil.shouldInitMiPush(this)) {  
        MiPushClient.registerPush(this, APP_ID, APP_KEY);  
    }  
}  
```  
  
```java  
public static boolean shouldInitMiPush(Context context) {  
    ActivityManager am = ((ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE));  
    if (am != null) {  
        List<ActivityManager.RunningAppProcessInfo> processInfos = am.getRunningAppProcesses();  
        String mainProcessName = context.getPackageName();  
        int myPid = Process.myPid();  
        for (ActivityManager.RunningAppProcessInfo info : processInfos) {  
            if (info.pid == myPid && mainProcessName.equals(info.processName)) {  
                return true;  
            }  
        }  
    }  
    return false;  
}  
```  
  
### Activity  
```java  
/**  
 * 1、设置 topic 和 alias。 服务器端使用 appsecret 即可以向demo发送广播和单点的消息。<br/>  
 * 2、为了修改本 demo 为使用你自己的 appid，你需要修改几个地方：Application中的 APP_ID 和 APP_KEY  
 *      AndroidManifest.xml 中的 packagename，和权限 permission.MIPUSH_RECEIVE 的前缀为你的 packagename。  
 */  
public class MiPushTestActivity extends ListActivity {  
      
    public static boolean isForeground = false;  
      
    protected void onCreate(Bundle savedInstanceState) {  
        super.onCreate(savedInstanceState);  
        String[] array = {"设置别名",  
                "撤销别名",  
                "设置帐号",  
                "撤销帐号",  
                "设置标签",  
                  
                "撤销标签",  
                "设置接收消息时间",  
                "暂停推送",  
                "重新开始推送",};  
        setListAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, new ArrayList<>(Arrays.asList(array))));  
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
    protected void onListItemClick(ListView l, View v, int position, long id) {  
        switch (position) {  
            case 0:  
                set_alias();  
                break;  
            case 1:  
                unset_alias();  
                break;  
            case 2:  
                set_account();  
                break;  
            case 3:  
                unset_account();  
                break;  
            case 4:  
                subscribe_topic();  
                break;  
            case 5:  
                unsubscribe_topic();  
                break;  
            case 6:  
                int startHour = 10;  
                int startMin = 0;  
                int endHour = 23;  
                int endMin = 0;  
                MiPushClient.setAcceptTime(this, startHour, startMin, endHour, endMin, null);  
                break;  
            case 7:  
                MiPushClient.pausePush(this, null);  
                break;  
            case 8:  
                MiPushClient.resumePush(this, null);  
                break;  
        }  
    }  
      
    public void set_alias() {  
        final EditText editText = new EditText(this);  
        new AlertDialog.Builder(this)  
                .setTitle("设置别名")  
                .setView(editText)  
                .setPositiveButton("确认", (dialog, which) -> {  
                    String alias = editText.getText().toString();  
                    MiPushClient.setAlias(this, alias, null);  
                })  
                .setNegativeButton("取消", null)  
                .show();  
    }  
      
    public void unset_alias() {  
        final EditText editText = new EditText(this);  
        new AlertDialog.Builder(this)  
                .setTitle("撤销别名")  
                .setView(editText)  
                .setPositiveButton("确认", (dialog, which) -> {  
                    String alias = editText.getText().toString();  
                    MiPushClient.unsetAlias(this, alias, null);  
                })  
                .setNegativeButton("取消", null)  
                .show();  
          
    }  
      
    public void set_account() {  
        final EditText editText = new EditText(this);  
        new AlertDialog.Builder(this)  
                .setTitle("设置帐号")  
                .setView(editText)  
                .setPositiveButton("确认", (dialog, which) -> {  
                    String account = editText.getText().toString();  
                    MiPushClient.setUserAccount(this, account, null);  
                })  
                .setNegativeButton("取消", null)  
                .show();  
          
    }  
      
    public void unset_account() {  
        final EditText editText = new EditText(this);  
        new AlertDialog.Builder(this)  
                .setTitle("撤销帐号")  
                .setView(editText)  
                .setPositiveButton("确认", (dialog, which) -> {  
                    String account = editText.getText().toString();  
                    MiPushClient.unsetUserAccount(this, account, null);  
                })  
                .setNegativeButton("取消", null)  
                .show();  
    }  
      
    public void subscribe_topic() {  
        final EditText editText = new EditText(this);  
        new AlertDialog.Builder(this)  
                .setTitle("设置标签")  
                .setView(editText)  
                .setPositiveButton("确认", (dialog, which) -> {  
                    String topic = editText.getText().toString();  
                    MiPushClient.subscribe(this, topic, null);  
                })  
                .setNegativeButton("取消", null)  
                .show();  
    }  
      
    public void unsubscribe_topic() {  
        final EditText editText = new EditText(this);  
        new AlertDialog.Builder(this)  
                .setTitle("撤销标签")  
                .setView(editText)  
                .setPositiveButton("确认", (dialog, which) -> {  
                    String topic = editText.getText().toString();  
                    MiPushClient.unsubscribe(this, topic, null);  
                })  
                .setNegativeButton("取消", null)  
                .show();  
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
/**  
 * 注意，重写的这些方法都运行在非 UI 线程中  
 */  
public class MiPushReceiver extends PushMessageReceiver {  
      
    @Override  
    /*用来接收服务器向客户端发送的透传消息*/  
    public void onReceivePassThroughMessage(Context context, MiPushMessage message) {  
        if (message == null) return;  
        Log.i("bqt", "【onReceivePassThroughMessage】" + message.toString());  
        printMsg(message);  
        new Handler(Looper.getMainLooper()).post(() -> PushMsgReceiverHelper.getInstance().onMiPushMsgReceiver(message));  
    }  
      
    @Override  
    /*用来接收服务器向客户端发送的通知消息，这个回调方法会在用户手动点击通知后触发*/  
    public void onNotificationMessageClicked(Context context, MiPushMessage message) {  
        Log.i("bqt", "【onNotificationMessageClicked】" + message.toString());  
        printMsg(message);  
    }  
      
    @Override  
    /*用来接收服务器向客户端发送的通知消息，这个回调方法是在通知消息到达客户端时触发。  
    另外应用在前台时不弹出通知的通知消息到达客户端也会触发这个回调函数。*/  
    public void onNotificationMessageArrived(Context context, MiPushMessage message) {  
        Log.i("bqt", "【onNotificationMessageArrived】" + message.toString());  
        printMsg(message);  
    }  
      
    @Override  
    /*用来接收客户端向服务器发送命令后的响应结果*/  
    public void onCommandResult(Context context, MiPushCommandMessage message) {  
        Log.i("bqt", "【onCommandResult】" + message.toString());  
        printCmdMsg(message);  
    }  
      
    @Override  
    /*用来接收客户端向服务器发送注册命令后的响应结果*/  
    public void onReceiveRegisterResult(Context context, MiPushCommandMessage message) {  
        Log.i("bqt", "【onReceiveRegisterResult】" + message.toString());  
        String command = message.getCommand();  
        List<String> arguments = message.getCommandArguments();  
        String cmdArg1 = ((arguments != null && arguments.size() > 0) ? arguments.get(0) : null);  
        Log.i("bqt", "command=" + command + "  " + cmdArg1);  
    }  
      
    private void printMsg(MiPushMessage message) {  
        String topic = message.getTopic();  
        String alias = message.getAlias();  
        String content = message.getContent();  
        Log.i("bqt", "topic=" + topic + "  alias=" + alias + "  content=" + content);  
    }  
      
    private void printCmdMsg(MiPushCommandMessage message) {  
        String command = message.getCommand();  
        List<String> arguments = message.getCommandArguments();  
        String cmdArg1 = ((arguments != null && arguments.size() > 0) ? arguments.get(0) : null);  
        String cmdArg2 = ((arguments != null && arguments.size() > 1) ? arguments.get(1) : null);  
        Log.i("bqt", "command=" + command + "  " + cmdArg1 + "  " + cmdArg2);  
          
        switch (command) {  
            case MiPushClient.COMMAND_REGISTER://注册  
                if (message.getResultCode() == ErrorCode.SUCCESS) {  
                    Log.i("bqt", "注册成功 mRegId =" + cmdArg1);  
                }  
                break;  
            case MiPushClient.COMMAND_SET_ALIAS://设置别名  
                if (message.getResultCode() == ErrorCode.SUCCESS) {  
                    Log.i("bqt", "设置别名成功 mAlias =" + cmdArg1);  
                }  
                break;  
            case MiPushClient.COMMAND_UNSET_ALIAS://取消设置别名  
                if (message.getResultCode() == ErrorCode.SUCCESS) {  
                    Log.i("bqt", "取消设置别名成功 mAlias =" + cmdArg1);  
                }  
                break;  
            case MiPushClient.COMMAND_SET_ACCOUNT://设置账户  
                if (message.getResultCode() == ErrorCode.SUCCESS) {  
                    Log.i("bqt", "设置账户成功 mAccount =" + cmdArg1);  
                }  
                break;  
            case MiPushClient.COMMAND_UNSET_ACCOUNT://撤销账户  
                if (message.getResultCode() == ErrorCode.SUCCESS) {  
                    Log.i("bqt", "撤销账户成功 mAccount =" + cmdArg1);  
                }  
                break;  
            case MiPushClient.COMMAND_SUBSCRIBE_TOPIC://订阅标签  
                if (message.getResultCode() == ErrorCode.SUCCESS) {  
                    Log.i("bqt", "订阅标签成功 mTopic =" + cmdArg1);  
                }  
                break;  
            case MiPushClient.COMMAND_UNSUBSCRIBE_TOPIC://撤销标签  
                if (message.getResultCode() == ErrorCode.SUCCESS) {  
                    Log.i("bqt", "撤销标签成功 mTopic =" + cmdArg1);  
                }  
                break;  
            case MiPushClient.COMMAND_SET_ACCEPT_TIME://设置接收推送时间  
                if (message.getResultCode() == ErrorCode.SUCCESS) {  
                    Log.i("bqt", "设置接收推送时间成功 mStartTime =" + cmdArg1 + "  mEndTime=" + cmdArg2);  
                }  
                break;  
            default:  
                Log.i("bqt", "未知命令，Reason=" + message.getReason());  
                break;  
        }  
    }  
}  
```  
  
### 消息处理类  
```java  
/**  
 * 处理推送SDK推过来的自定义消息（又叫应用内消息，或者透传消息）  
 */  
public class PushMsgReceiverHelper {  
    private static PushMsgReceiverHelper instance = new PushMsgReceiverHelper();  
      
    private PushMsgReceiverHelper() {  
    }  
      
    public static PushMsgReceiverHelper getInstance() {  
        return instance;  
    }  
      
    /**  
     * 处理极光推送推过来的自定义消息  
     */  
    public void onJPushMsgReceiver(Bundle bundle) {  
    }  
      
    /**  
     * 处理小米推送推过来的自定义消息  
     */  
    public void onMiPushMsgReceiver(MiPushMessage message) {  
        Log.i("bqt", "【小米推送】" + message);  
        if (MiPushTestActivity.isForeground) {  
            EventBus.getDefault().post(new BasePushBean(message.getContent(), BasePushBean.TYPE_STRING));  
        }  
    }  
      
    /**  
     * 处理华为光推送推过来的自定义消息  
     */  
    public void onHuaweiPushMsgReceiver(String message) {  
    }  
      
    /**  
     * 处理魅族推送推过来的自定义消息  
     */  
    public void onMeiZhuPushMsgReceiver(String message) {  
    }  
      
}  
```  
  
### 配置文件  
**build.gradle**  
```java  
implementation files('libs/MiPush_SDK_Client_3_6_2.jar')  
```  
  
**混淆文件：proguard-android.txt**  
client sdk已经混淆过了，不需要再混淆。请使用keep命令保留client sdk的内容：  
```java  
-keep class com.bqt.push.receiver.MiPushReceiver {*;}  
#可以防止一个误报的 warning 导致无法成功编译，如果编译使用的 Android 版本是 23。  
-dontwarn com.xiaomi.push.**  
```  
  
**AndroidManifest.xml**  
```xml  
<!--====================== 推送SDK需要定义的权限 =====================-->  
<!--极光-->  
<!--小米-->  
<permission  
    android:name="${applicationId}.permission.MIPUSH_RECEIVE"  
    android:protectionLevel="signature"/>  
<uses-permission android:name="${applicationId}.permission.MIPUSH_RECEIVE"/>  
<!--华为-->  
<!--魅族-->  
  
```  
```xml  
<!--========================= 小米推送需要注册的组件 start =========================-->  
<receiver  
    android:name=".receiver.MiPushReceiver"  
    android:exported="true"  
    tools:ignore="ExportedReceiver">  
    <intent-filter>  
        <action android:name="com.xiaomi.mipush.RECEIVE_MESSAGE"/>  
    </intent-filter>  
    <intent-filter>  
        <action android:name="com.xiaomi.mipush.MESSAGE_ARRIVED"/>  
    </intent-filter>  
    <intent-filter>  
        <action android:name="com.xiaomi.mipush.ERROR"/>  
    </intent-filter>  
</receiver>  
  
<receiver  
    android:name="com.xiaomi.push.service.receivers.NetworkStatusReceiver"  
    android:exported="true">  
    <intent-filter>  
        <action  
            android:name="android.net.conn.CONNECTIVITY_CHANGE"  
            tools:ignore="BatteryLife"/>  
        <category android:name="android.intent.category.DEFAULT"/>  
    </intent-filter>  
</receiver>  
  
<receiver  
    android:name="com.xiaomi.push.service.receivers.PingReceiver"  
    android:exported="false"  
    android:process=":pushservice">  
    <intent-filter>  
        <action android:name="com.xiaomi.push.PING_TIMER"/>  
    </intent-filter>  
</receiver>  
  
<service  
    android:name="com.xiaomi.push.service.XMJobService"  
    android:enabled="true"  
    android:exported="false"  
    android:permission="android.permission.BIND_JOB_SERVICE"  
    android:process=":pushservice"/>  
<service  
    android:name="com.xiaomi.push.service.XMPushService"  
    android:enabled="true"  
    android:process=":pushservice"/>  
<service  
    android:name="com.xiaomi.mipush.sdk.PushMessageHandler"  
    android:enabled="true"  
    android:exported="true"  
    tools:ignore="ExportedService"/>  
<service  
    android:name="com.xiaomi.mipush.sdk.MessageHandleService"  
    android:enabled="true"/>  
<!--========================= 小米推送需要注册的组件 end =========================-->  
```  
  
2018-4-18  
