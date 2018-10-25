| Markdown版本笔记 | 我的GitHub首页 | 我的博客 | 我的微信 | 我的邮箱 |  
| :------------: | :------------: | :------------: | :------------: | :------------: |  
| [MyAndroidBlogs][Markdown] | [baiqiantao][GitHub] | [baiqiantao][博客] | bqt20094 | baiqiantao@sina.com |  
  
[Markdown]:https://github.com/baiqiantao/MyAndroidBlogs  
[GitHub]:https://github.com/baiqiantao  
[博客]:http://www.cnblogs.com/baiqiantao/  
  
WebView JSBridge web JS桥 演示 原理 测试 MD  
***  
目录  
===  

- [简介](# 简介)
	- [gradle配置](# gradle配置)
	- [Java端：注册提供给JS端调用的接口](# java端：注册提供给js端调用的接口)
	- [JS端：注册提供给Java端调用的接口](# js端：注册提供给java端调用的接口)
	- [JS 端注意事项](# js-端注意事项)
- [源码解析](# 源码解析)
	- [BridgeWebView](# bridgewebview)
	- [java 调用 js 中注册的方法](# java-调用-js-中注册的方法)
		- [调用过程](# 调用过程)
		- [回调过程](# 回调过程)
		- [一个疑惑](# 一个疑惑)
	- [js 调用 java 中注册的方法](# js-调用-java-中注册的方法)
- [一个完整的Demo](# 一个完整的demo)
	- [MainActivity](# mainactivity)
	- [注册的BridgeHandler](# 注册的bridgehandler)
	- [HTML+JS源码](# htmljs源码)
  
# 简介  
Demo位置：https://github.com/baiqiantao/JsBridge.git   
[GitHub](https://github.com/lzyzsd/JsBridge)  
  
- 从微信jsBridge文件获取了灵感并修改了一些错误修复及功能增强。  
- 该项目在Java和JavaScript之间架起了一座桥梁。  
- 它提供了从js调用Java代码并从java调用js代码的安全方便的方式。  
  
![mark](http://pfpk8ixun.bkt.clouddn.com/blog/181021/ig0H31Bajf.png?imageslim)  
  
## gradle配置  
在project的build.gradle中引入`jitpack.io`：  
```groovy  
allprojects {  
    repositories {  
        google()  
        jcenter()  
        maven { url "https://jitpack.io" }  
    }  
}  
```  
在module中添加依赖：  
```groovy  
implementation 'com.github.lzyzsd:jsbridge:1.0.4'  
```  
  
## Java端：注册提供给JS端调用的接口  
Register a `Java handler` function so that js can call：  
```java  
webView.registerHandler("submitFromWeb", new BridgeHandler() {  
    @Override  
    public void handler(String data, CallBackFunction function) {  
        Log.i(TAG, "handler = submitFromWeb, data from web = " + data);  
        //do something  
        function.onCallBack("submitFromWeb exe, response data from Java"); //回调  
    }  
});  
```  
  
js can call this `Java handler` method "submitFromWeb" through:  
```javascript  
WebViewJavascriptBridge.callHandler(  
    'submitFromWeb'  //方法名  
    , {'param': str1}  //参数  
    , function(responseData) { //回调  
        document.getElementById("show").innerHTML = "send get responseData from java, data = " + responseData  
    }  
);  
```  
  
You can set a `default handler` in Java, so that js can send message to Java `without assigned handlerName`:  
```java  
webView.setDefaultHandler(new DefaultHandler());  
```  
```javascript  
window.WebViewJavascriptBridge.send(  //没有方法名  
    data  
    , function(responseData) {  
        document.getElementById("show").innerHTML = "repsonseData from java, data = " + responseData  
    }  
);  
```  
  
## JS端：注册提供给Java端调用的接口  
Register a `JavaScript handler` function so that Java can call:  
```javascript  
WebViewJavascriptBridge.registerHandler("functionInJs", function(data, responseCallback) {  
    document.getElementById("show").innerHTML = ("data from Java: = " + data);  
    var responseData = "Javascript Says Right back aka!";  
    responseCallback(responseData);  
});  
```  
  
Java can call this `js handler` function "functionInJs" through:  
```java  
webView.callHandler("functionInJs", new Gson().toJson(user), new CallBackFunction() {  
    @Override  
    public void onCallBack(String data) {  
    }  
});  
```  
  
You can also define a `default handler` use init method, so that Java can send message to js `without assigned handlerName`:  
for example:  
```javascript  
bridge.init(function(message, responseCallback) {  
    console.log('JS got a message', message);  
    var data = {  
        'Javascript Responds': 'Wee!'  
    };  
    console.log('JS responding with', data);  
    responseCallback(data);  
});  
```  
  
when Java call:  
```java  
webView.send("hello");  
```  
will print 'JS got a message hello' and 'JS responding with' in webview console.  
  
## JS 端注意事项  
This lib will inject注入 a `WebViewJavascriptBridge` Object to `window object`. So in your js, before use WebViewJavascriptBridge, you must detect检测 if WebViewJavascriptBridge exist. If WebViewJavascriptBridge does not exit, you can listen to `WebViewJavascriptBridgeReady` event, as the blow code shows:  
```javascript  
if (window.WebViewJavascriptBridge) {  
    //do your work here  
} else {  
    document.addEventListener(  
        'WebViewJavascriptBridgeReady'  
        , function() {  
            //do your work here  
        },  
        false  
    );  
}  
```  
  
# 源码解析  
## BridgeWebView  
首先自定义了一个 WebView：  
```java  
public class BridgeWebView extends WebView implements WebViewJavascriptBridge  
```  
  
继承的是系统自带的 WebView，鉴于很多人会使用腾讯的 X5 WebView，如果想保持使用 X5 的话，我们其实重新定义一个 WebView 并将其继承的 WebView 改成 X5 的就行了。  
  
里面定义了两个集合，一个用于在调用 `registerHandler` 时保存Java中注册的 方法名 和 BridgeHandler 的映射，一个用于在调用 `send` 时保存 Java 向 JS 发送的请求和 CallBackFunction 的映射：  
```java  
Map<String, BridgeHandler> messageHandlers = new HashMap<String, BridgeHandler>();//供js调用  
Map<String, CallBackFunction> responseCallbacks = new HashMap<String, CallBackFunction>();//调用js  
```  
  
然后是一些初始化操作：  
```java  
getSettings().setJavaScriptEnabled(true);  
if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {  
   WebView.setWebContentsDebuggingEnabled(true);  
}  
setWebViewClient(new BridgeWebViewClient(this));  
```  
  
这里定义了一个 BridgeWebViewClient，里面并没有什么复杂的逻辑，仅仅是重写了 WebViewClient 的 `shouldOverrideUrlLoading` 方法和 `onPageFinished` 方法。我们先看一下 onPageFinished 方法，shouldOverrideUrlLoading 后面我们用到时再分析。  
  
onPageFinished 主要用于在页面加载后加载 js 文件，以及调用一些需要在页面加载后才能调用的 js 逻辑：  
```java  
@Override  
public void onPageFinished(WebView view, String url) {  
    super.onPageFinished(view, url);  
    if (BridgeWebView.toLoadJs != null) {  
        BridgeUtil.webViewLoadLocalJs(view, BridgeWebView.toLoadJs); //加载 JS 代码  
    }  
    if (webView.getStartupMessage() != null) {  
        for (Message m : webView.getStartupMessage()) {  
            webView.dispatchMessage(m);//需要在页面加载完执行的逻辑  
        }  
        webView.setStartupMessage(null);//清空这些任务  
    }  
}  
```  
```java  
public static final String toLoadJs = "WebViewJavascriptBridge.js";  
public static void webViewLoadLocalJs(WebView view, String path){  
    String jsContent = assetFile2Str(view.getContext(), path);  
    view.loadUrl("javascript:" + jsContent);  
}  
```  
  
## java 调用 js 中注册的方法  
### 调用过程  
接口 `WebViewJavascriptBridge` 其实就是让我们调用 js 中注册的方法的：  
```java  
public interface WebViewJavascriptBridge {  
    void send(String data);  
    void send(String data, CallBackFunction responseCallback);  
}  
```  
  
鉴于 js 和 java 交互无非这两种方式：`webview.addJavascriptInterface` 或 `WebViewClient.shouldOverrideUrlLoading`，所以其实不用看源码我们也知道，其最终肯定是通过两者中的一种来实现的。  
  
我们大致瞅一下其调用过程：  
```java  
send(String data) -> send(data, null);  
send(String data, CallBackFunction responseCallback) -> doSend(null, data, responseCallback);  
```  
  
两者都会调用 `doSend` 方法，这个方法其实主要就是将消息相关信息封装到 `Message` 中：  
```java  
private void doSend(String handlerName, String data, CallBackFunction responseCallback) {  
      Message m = new Message();  
      if (!TextUtils.isEmpty(handlerName)) m.setHandlerName(handlerName);  
      if (!TextUtils.isEmpty(data)) m.setData(data);  
      if (responseCallback != null) {  
            //为此次调用定义一个唯一的回调id，例如【JAVA_CB_1_541】【JAVA_CB_2_455】  
            String callbackStr = String.format(BridgeUtil.CALLBACK_ID_FORMAT, ++uniqueId + (BridgeUtil.UNDERLINE_STR + SystemClock.currentThreadTimeMillis()));  
            responseCallbacks.put(callbackStr, responseCallback);//将回调id和对应的回调方法保存起来  
            m.setCallbackId(callbackStr);//将回调id赋给此消息，此id是实现交互的最关键的信息  
            //之后，当具有同一id的消息通过 shouldOverrideUrlLoading 回调给我们时，我们就可以找到并执行这里定义的回调方法  
      }  
      queueMessage(m);  
}  
```  
  
这里用到的 Message 是库中定义的一个非常简单的数据结构：  
```java  
public class Message{  
    private String callbackId; //callbackId  
    private String responseId; //responseId  
    private String responseData; //responseData  
    private String data; //data of message  
    private String handlerName; //name of handler  
  
    private final static String CALLBACK_ID_STR = "callbackId";  
    private final static String RESPONSE_ID_STR = "responseId";  
    private final static String RESPONSE_DATA_STR = "responseData";  
    private final static String DATA_STR = "data";  
    private final static String HANDLER_NAME_STR = "handlerName";  
}  
```  
  
接下来会做一个简单的判断，以决定是立即调用还是在页面加载完成后调用：  
```java  
private void queueMessage(Message m) {  
      if (startupMessage != null) startupMessage.add(m); //在 onPageFinished 后调用(案例中没用到)  
      else dispatchMessage(m); //立即调用(默认操作)  
}  
```  
  
下面是最核心的部分之一了，简单来说就是，经过一系列操作后，最终是通过 loadUrl 来实现原生调用 js 的方法的：  
```java  
void dispatchMessage(Message m) {  
      String messageJson = m.toJson(); //以Json格式通讯  
      messageJson = messageJson.replaceAll("(\\\\)([^utrn])", "\\\\\\\\$1$2");//转义特殊字符  
      messageJson = messageJson.replaceAll("(?<=[^\\\\])(\")", "\\\\\"");//转义特殊字符  
      String javascriptCommand = String.format(BridgeUtil.JS_HANDLE_MESSAGE_FROM_JAVA, messageJson);//调用的js命令  
      if (Thread.currentThread() == Looper.getMainLooper().getThread()) { //必须在主线程中调用  
            this.loadUrl(javascriptCommand); //最核心的部分，原理就是通过 loadUrl 调用 js 方法  
      }  
}  
```  
  
js命令的全部内容例如下，其中包含的内容有：**唯一的callbackId**、js中定义的方法名`handlerName`、传递过去的数据`data`。  
> javascript:WebViewJavascriptBridge._handleMessageFromNative('{\"callbackId\":\"JAVA_CB_1_541\",\"data\":\"【包青天20094】\",\"handlerName\":\"showInHtml\"}');  
  
至此，整个调用过程结束了。  
  
### 回调过程  
经过上面的操作，我们已经调用了 js 端的代码了，下面我们探究在调用时我们注册的回调方法是怎么被调用的。  
  
我们不需要关心 js 那端的逻辑是怎么样的，因为这完全是前端同学需要关注的事情，我们只关注 js 调用之后是怎么和我们原生交互的。  
  
前面我们已经说过了，js和java交互只有两种方式：`webview.addJavascriptInterface` 或 `WebViewClient.shouldOverrideUrlLoading`，这个库采用的就是重写 `shouldOverrideUrlLoading` 这种方式。  
  
我们首先看一下 `shouldOverrideUrlLoading` 这个方法，里面主要是根据回调 url 的不同做不同的处理：  
```java  
@Override  
public boolean shouldOverrideUrlLoading(WebView view, String url) {  
    if (url.startsWith(BridgeUtil.YY_RETURN_DATA)) {//格式为【yy://return/{function}/returncontent】  
        webView.handlerReturnData(url);  
        return true;  
    } else if (url.startsWith(BridgeUtil.YY_OVERRIDE_SCHEMA)) { //格式为【yy://】  
        webView.flushMessageQueue();  
        return true;  
    } else return super.shouldOverrideUrlLoading(view, url); //不做任何处理  
}  
```  
  
上面的案例中，我们回调的 url 为`【yy://__QUEUE_MESSAGE__/】`，所以会调用`flushMessageQueue`方法，从方法名来看，其作用应该是"刷新消息队列"的，我们先看看其基本结构：  
```java  
void flushMessageQueue() {  
   if (Thread.currentThread() == Looper.getMainLooper().getThread()) { //主线程的调用才处理  
       //JS代码为【"javascript:WebViewJavascriptBridge._fetchQueue();"】  
      loadUrl(BridgeUtil.JS_FETCH_QUEUE_FROM_JAVA, new CallBackFunction() {  
         @Override  
         public void onCallBack(String data) { //处理调用后的回调  
          //...  
      });  
   }  
}  
```  
```java  
public void loadUrl(String jsUrl, CallBackFunction returnCallback) {  
   this.loadUrl(jsUrl);//调用 js 代码  
   responseCallbacks.put(BridgeUtil.parseFunctionName(jsUrl), returnCallback); //处理后的 id 为【_fetchQueue】  
}  
```  
  
调用完 loadUrl 之后集合 `responseCallbacks` 中就有两个元素了。既然又调用了一次 loadUrl，那我们就继续追踪`shouldOverrideUrlLoading`。这一次回调的 url 为：  
> yy://return/_fetchQueue/[{"responseId":"JAVA_CB_1_541","responseData":"【你好，我是白乾涛，这是JS名为showInHtml的Handler返回的数据】"}]  
  
所以就会调用`handlerReturnData`这个方法，这个方法是用于将返回的数据回调给 `CallBackFunction` 的，逻辑非常简单：  
```java  
void handlerReturnData(String url) {  
   String functionName = BridgeUtil.getFunctionFromReturnUrl(url); //固定为【_fetchQueue】  
   CallBackFunction f = responseCallbacks.get(functionName); //找到对应的 CallBackFunction  
   String data = BridgeUtil.getDataFromReturnUrl(url); //拿到 js 返回的数据  
   if (f != null) {  
      f.onCallBack(data); //将返回的数据回调给 CallBackFunction  
      responseCallbacks.remove(functionName);//移除此 CallBackFunction  
      return;  
   }  
}  
```  
  
解析后，这里的：  
- functionName 为 `_fetchQueue`  
- CallBackFunction 为上面我们在调用 `flushMessageQueue` 时定义的回调方法  
- 返回的 data 为 `[{"responseId":"JAVA_CB_1_541","responseData":"【你好，我是白乾涛，这是JS名为showInHtml的Handler返回的数据】"}]`，可以看到这个 data 其实是一个 `JSONArray`  
  
所以接下来就进入其 `onCallBack` 中了，这个方法里面的代码稍多一点点，大致逻辑为：  
```java  
List<Message> list = Message.toArrayList(data); //解析返回的数据  
for (int i = 0; i < list.size(); i++) { //遍历这个集合，本例中只有id为 JAVA_CB_1_541 的元素  
    if (!TextUtils.isEmpty(responseId) { //如果消息的responseId不为空  
        function.onCallBack(responseData); //【核心】，调用相应的回调方法  
        responseCallbacks.remove(responseId); //执行完就移除掉  
    }else{ //这里面的逻辑是在js主动调用原生方法时才会执行的，我们后面再分析  
    }  
}  
```  
  
完成以后，所有的回调都执行了，集合 responseCallbacks 中的所有元素也都移除了。至此整个回调过程也结束了。  
  
### 一个疑惑  
通过上面的流程分析，我们知道，在我们首次 loadUrl 后的 shouldOverrideUrlLoading 回调中，我们首先是进入 flushMessageQueue 方法里面又 load 了一段固定的 js 代码，然后再在 shouldOverrideUrlLoading 回调中获取 js 返回的数据以及执行回调，为什么要这么搞呢，明明一次交互就可以了的？  
  
## js 调用 java 中注册的方法  
这里的大多数流程和上面的基本是一致的，下面我们之简单的过一下：  
  
- 首先肯定还是回调 `shouldOverrideUrlLoading` ，此时回调的url为固定的【yy://__QUEUE_MESSAGE__/】  
- 接着调用 `flushMessageQueue`，然后又调用一次 loadUrl，消息 id 同样为【_fetchQueue】  
- 然后会再次回调 `shouldOverrideUrlLoading` ，此时的url为：  
> yy://return/_fetchQueue/[{"handlerName":"showTips","data":{"type":"dialog","title":"我是标题","msg":""},"callbackId":"cb_3_1540110644942"}]  
  
- 所以会进入 `handlerReturnData`，接着会回调`flushMessageQueue`中的 `onCallBack`方法，接着就是遍历集合  
- 目前集合中只有一个元素，由于 `responseId` 为空，所以会走和上面分析的 java 调 js 不同的分支  
![mark](http://pfpk8ixun.bkt.clouddn.com/blog/181021/5B74fCJe6e.png?imageslim)  
  
我们着重分析此时要走的逻辑：  
```java  
CallBackFunction responseFunction = null; //临时定义一个回调  
final String callbackId = m.getCallbackId();  
if (!TextUtils.isEmpty(callbackId)) { //有 callbackId 时的回调是继续load一个js代码  
   responseFunction = new CallBackFunction() {  
      @Override  
      public void onCallBack(String data) {  
         Message responseMsg = new Message();  
         responseMsg.setResponseId(callbackId); //将 callbackId作为 responseId  
         responseMsg.setResponseData(data);  
         queueMessage(responseMsg);  
      }  
   };  
} else { //没有 callbackId 时的回调是一个空实现(正常是不会走到这里的)  
   responseFunction = data -> { };  
}  
BridgeHandler handler = TextUtils.isEmpty(m.getHandlerName() ? defaultHandler : messageHandlers.get(m.getHandlerName());  
if (handler != null) handler.handler(m.getData(), responseFunction); //调用我们定义的 handler  
```  
  
里面首先是定义了一个 `CallBackFunction` 回调，然后会调用我们定义的 `BridgeHandler` 的 `handler` 方法，例如下面是本案例中我写的部分逻辑：  
```java  
@Override  
public void handler(String data, CallBackFunction function) {  
    //...显示Toast或弹Dialog或打印Log  
    function.onCallBack(...); //返回数据  
}  
```  
  
接下来就会走我们上面定义的 responseFunction 的 onCallBack 方法，接着会走 queueMessage 方法，和上面的流程一样，我们接着会走 dispatchMessage，然后调用  loadUrl 调用 js 的方法的，此时的 js 命令为：  
> javascript:WebViewJavascriptBridge._handleMessageFromNative('{\"responseData\":\"{\\\"status\\\":\\\"success\\\",\\\"time\\\":0}\",\"responseId\":\"cb_2_1540112210451\"}');  
  
至此，便完整实现了 js 调用原生方法，并在原生响应 js 的命令后回调数据给 js。  
  
# 一个完整的Demo  
## MainActivity  
```java  
public class MainActivity extends ListActivity {  
    private BridgeWebView mBridgeWebView;  
      
    protected void onCreate(Bundle savedInstanceState) {  
        super.onCreate(savedInstanceState);  
        mBridgeWebView = new BridgeWebView(this);  
        registerHandler();  
        mBridgeWebView.loadUrl("file:///android_asset/jsbridge.html");  
        getListView().addFooterView(mBridgeWebView);  
          
        String[] array = {"调用JS中的名为showInHtml的Handler",  
                "调用JS中的默认Handler，没有回调",  
                "调用JS中的默认Handler，有回调",};  
        setListAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, new ArrayList<>(Arrays.asList(array))));  
    }  
      
    private void registerHandler() {  
        mBridgeWebView.setDefaultHandler(new DefaultBridgeHandler());//默认的Handle  
        mBridgeWebView.registerHandler(JBNativeIDs.BRIDGE_ID_DEVICEINFO, new DeviceInfoBridgeHandler());//获取设备信息  
        mBridgeWebView.registerHandler(JBNativeIDs.BRIDGE_ID_BACKNATIVE, new BackNativeBridgeHandler(this));//结束当前Activity  
        mBridgeWebView.registerHandler(JBNativeIDs.BRIDGE_ID_SHOWTIPS, new ShowTipsBridgeHandler(this));//显示原生toast或弹窗  
    }  
      
    @Override  
    protected void onListItemClick(ListView l, View v, int position, long id) {  
        switch (position) {  
            case 0:  
                mBridgeWebView.callHandler(JBJsIDs.BRIDGE_ID_SHOWINHTML, "【包青天20094】", new SimpleCallBackFunction());  
                break;  
            case 1:  
                mBridgeWebView.send("【包青天20095】");  
                break;  
            case 2:  
                mBridgeWebView.send("【包青天20096】", new SimpleCallBackFunction());  
                break;  
        }  
    }  
      
    class SimpleCallBackFunction implements CallBackFunction {  
        @Override  
        public void onCallBack(String data) {  
            Log.i("bqt", "【JS回调】，是否在主线程：" + (Looper.myLooper() == Looper.getMainLooper()) + "\n" + data);  
            //也可以用Thread.currentThread() == Looper.getMainLooper().getThread()  
        }  
    }  
}  
```  
  
## 注册的BridgeHandler  
```java  
public class DeviceInfoBridgeHandler implements BridgeHandler {  
    /**  
     * @param data js调用原生所传递的参数  
     * @param function js调用原生所传递的回调接口  
     */  
    @Override  
    public void handler(String data, final CallBackFunction function) {  
        Log.i("bqt", "【获取设备信息，参数】" + data);  
          
        new Thread(() -> {  
            SystemClock.sleep(2000);//模拟耗时操作  
            DeviceInfo deviceInfo = new DeviceInfo("小米6", "8.0");  
            JBRespBean bridgeRespBean = JBRespBean.newBuilder()  
                    .status(JBRespBean.STATUS_SUCCESS)  
                    .time(System.currentTimeMillis())  
                    .data(deviceInfo)  
                    .build();  
              
            final String response = new Gson().toJson(bridgeRespBean);  
            Log.i("bqt", "【给JS的响应】" + response);  
              
            new Handler(Looper.getMainLooper()).post(() -> {  
                function.onCallBack(response);//必须在主线程中回调给JS，因为JS中加载网页也是UI操作，也必须在主线程中  
            });  
        }).start();  
    }  
      
    static class DeviceInfo {  
        String phoneName;  
        String sysVersion;  
          
        DeviceInfo(String phoneName, String sysVersion) {  
            this.phoneName = phoneName;  
            this.sysVersion = sysVersion;  
        }  
    }  
}  
```  
  
## HTML+JS源码  
```html  
<!DOCTYPE html>  
<html>  
<head>  
    <meta charset="UTF-8">  
    <title>JSBridge测试页面</title>  
</head>  
<body>  
<p>  
    <!--xmp标签可以在页面上输出html代码-->  
    <xmp id="show"></xmp>  
</p>  
<p><input type="text" id="inputMsg" value=""/></p>  
<p><input type="button" id="button1" value="获取设备信息" onclick="deviceInfo();"/></p>  
<p><input type="button" id="button2" value="显示原生dialog" onclick="showTips();"/></p>  
<p><input type="button" id="button3" value="结束当前Activity" onclick="backNative();"/></p>  
<p><input type="button" id="button4" value="使用默认的Handle" onclick="defaultHandler();"/></p>  
<p><input type="button" id="button5" value="显示html源码" onclick="showHtml();"/></p>  
<p>  
    <xmp id="htmlContent"></xmp>  
</p>  
</body>  
<script>  
        //******************************************************************************************  
        // 点击事件  
        //******************************************************************************************  
        function deviceInfo() {  
            var inputMsg = document.getElementById("inputMsg").value;  
            var data = {  
                content: inputMsg  
            };  
            window.WebViewJavascriptBridge.callHandler("deviceInfo",  
                data,  
                function(responseData) {  
                    document.getElementById("show").innerHTML = "【返回数据】" + responseData  
                }  
            );  
        }  
        function showTips() {  
            var inputMsg = document.getElementById("inputMsg").value;  
            var data = {  
                type: "dialog",  
                title: "我是标题",  
                msg: inputMsg  
            };  
            window.WebViewJavascriptBridge.callHandler('showTips',  
                data,  
                function(responseData) {  
                    document.getElementById("show").innerHTML = "【返回数据】 " + responseData  
                }  
            );  
        }  
        function backNative() {  
            window.WebViewJavascriptBridge.callHandler('backNative',  
                null,  
                function(responseData) {  
                    console.log("【此时Activity关闭了，不要再操作UI了】" + responseData);  
                }  
            );  
        }  
        function defaultHandler() {  
            var data = {  
                type: "toast",  
                msg: "使用默认的Handle处理"  
            };  
            window.WebViewJavascriptBridge.send(data,  
                function(responseData) {  
                    window.WebViewJavascriptBridge.callHandler('showTips', data, null); //显示原生toast  
                }  
            );  
        }  
        function showHtml() {  
            document.getElementById("htmlContent").innerHTML = document.getElementsByTagName("html")[0].innerHTML;  
        }  
        //******************************************************************************************  
        // JS端注册提供给Java端调用的接口  
        //******************************************************************************************  
        function bridgeLog(logContent) {  
            document.getElementById("show").innerHTML = logContent;  
        }  
        // 调用注册方法  
        function connectWebViewJavascriptBridge(callback) {  
            if (window.WebViewJavascriptBridge) {  
                callback(WebViewJavascriptBridge)  
            } else {  
                document.addEventListener(  
                    'WebViewJavascriptBridgeReady',  
                    function() {  
                        callback(WebViewJavascriptBridge)  
                    },  
                    false  
                );  
            }  
        }  
        connectWebViewJavascriptBridge(function(bridge) {  
            //注册默认的Handler  
            bridge.init(function(data, responseCallback) {  
                document.getElementById("show").innerHTML = ("【原生传过来的数据】" + data);  
                if (responseCallback) { //这行代码意思是：如果有responseCallback这个方法，就执行以下逻辑  
                    responseCallback("【你好，我是白乾涛，这是JS默认的Handler返回的数据】");  
                }  
            });  
            //注册供原生调用的Handler  
            bridge.registerHandler("showInHtml", function(data, responseCallback) {  
                document.getElementById("show").innerHTML = ("【原生传过来的数据】" + data);  
                if (responseCallback) {  
                    responseCallback("【你好，我是白乾涛，这是JS名为showInHtml的Handler返回的数据】");  
                }  
            });  
        })  
</script>  
</html>  
```  
  
2018-5-8  
