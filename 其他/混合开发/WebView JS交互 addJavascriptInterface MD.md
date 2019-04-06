| Markdown版本笔记 | 我的GitHub首页 | 我的博客 | 我的微信 | 我的邮箱 |  
| :------------: | :------------: | :------------: | :------------: | :------------: |  
| [MyAndroidBlogs][Markdown] | [baiqiantao][GitHub] | [baiqiantao][博客] | bqt20094 | baiqiantao@sina.com |  
  
[Markdown]:https://github.com/baiqiantao/MyAndroidBlogs  
[GitHub]:https://github.com/baiqiantao  
[博客]:http://www.cnblogs.com/baiqiantao/  
  
WebView JS交互 addJavascriptInterface MD  
***  
目录  
===  

- [原生和 JS 交互](#原生和-JS-交互)
	- [addJavascriptInterface](#addJavascriptInterface)
	- [removeJavascriptInterface](#removeJavascriptInterface)
	- [evaluateJavascript](#evaluateJavascript)
	- [案例](#案例)
		- [原生代码](#原生代码)
		- [前端代码](#前端代码)
  
# 原生和 JS 交互  
[Demo](https://github.com/baiqiantao/WebViewTest.git)  
  
注意，如果在 java 与 Javascript 交互的时候出现如下错误：  
```java  
Uncaught ReferenceError: <pre name="code" class="html">getDeviceID   
```  
很可能是因为在 html 页面还没有加载完，就加载里面的 JS 方法，这样是找不到这个方法的。  
  
解决方法为：**把调用放到 onPageFinished() 里面**。  
  
## addJavascriptInterface  
[addJavascriptInterface](https://developer.android.google.cn/reference/android/webkit/WebView.html)  
  
```java  
void addJavascriptInterface(Object object, String name)  
```  
  
参数：  
- Object object : the Java object to inject注入 into this WebView's JavaScript context. Null values are ignored.  
- String name : the name used to expose暴露、公开 the object in JavaScript  
  
方法说明：  
- Injects the supplied Java object into this WebView.  
- The object is injected into the JavaScript context of the main frame, using the supplied name.   
- This allows the Java object's methods to be accessed from JavaScript.   
- For applications targeted to API level **JELLY_BEAN_MR1** and above, only **public** methods that are annotated with `JavascriptInterface` can be accessed from JavaScript.   
- For applications targeted to API level **JELLY_BEAN** or below, all **public** methods (including the inherited继承的 ones) can be accessed, see the `important security note`安全注意事项 below for implications意义、含义.  
- Note that injected objects will not appear in JavaScript until the page is next (re)loaded. JavaScript should be enabled before injecting the object.  
  
使用案例：  
```java  
class JsObject {  
    @JavascriptInterface  
    public String toString() { return "injectedObject"; }  
}  
  
webview.getSettings().setJavaScriptEnabled(true);  
webView.addJavascriptInterface(new JsObject(), "injectedObject");  
webView.loadData("", "text/html", null);  
webView.loadUrl("javascript:alert(injectedObject.toString())");  
```  
  
***IMPORTANT***  
- This method can be used to allow JavaScript to control the host application.   
- This is a powerful feature, but also presents a security risk for apps targeting **JELLY_BEAN** or earlier.   
- Apps that target a version later than **JELLY_BEAN** are still vulnerable很容易受到攻击 if the app runs on a device running Android earlier than **4.2**.   
- The most secure way to use this method is to target **JELLY_BEAN_MR1** and to ensure the method is called only when running on Android **4.2** or later.   
- With these older versions, JavaScript could use `reflection` to access an injected object's `public fields`.   
- Use of this method in a WebView containing untrusted content could allow an attacker攻击者 to manipulate操纵 the host application in unintended非预期 ways, executing执行 Java code with the permissions of the host application.   
- Use extreme care格外小心 when using this method in a WebView which could contain untrusted content.  
  
说明：  
- JavaScript interacts with交互 Java object on a `private, background thread` of this WebView. Care is therefore required to maintain保持 thread safety.  
- The Java object's fields are not accessible.  
- For applications targeted to API level **LOLLIPOP** and above, methods of injected Java objects are enumerable枚举 from JavaScript.  
  
## removeJavascriptInterface  
```java  
void removeJavascriptInterface(String name)  
```  
String name : the name used to expose暴露 the object in JavaScript. This value must never be null.  
  
Removes a previously injected Java object from this WebView.   
Note that the removal will not be reflected in JavaScript until the page is next (re)loaded.   
  
## evaluateJavascript  
```java  
void evaluateJavascript (String script, ValueCallback<String> resultCallback)  
```  
参数：  
String script : the JavaScript to execute.  
ValueCallback resultCallback :  
- A callback to be invoked when the script execution completes with the result of the execution (if any).   
    当脚本执行完成时的回调，如果有执行结果的话会携带执行结果。  
- May be null if no notification of the result is required.  
    如果不需要结果通知，可以为null。  
  
说明：  
- Asynchronously evaluates JavaScript in the context of the currently displayed page.   
    在当前显示页面的上下文中异步执行JavaScript。  
- If non-null, resultCallback will be invoked with any result returned from that execution.  
    如果非空，resultCallback会被回调并携带调用后返回的结果。   
- **This method must be called on the UI thread and the callback will be made on the UI thread**.  
    此方法必须在UI线程上调用，并且回调也是运行在UI线程上的。  
  
**Compatibility note 兼容性说明**   
- Applications targeting **N** or later, JavaScript state from an empty WebView is no longer persisted across navigations like `loadUrl(String)`.  
    目标版本为N或更高的应用程序，空的WebView中的JavaScript状态不再继续像loadUrl那样在navigations中保留。  
- For example, global variables and functions defined before calling `loadUrl(String)` will not exist in the loaded page.   
    例如，在调用`loadUrl`之前定义的全局变量和函数将不会存在于加载的页面中。  
- Applications should use addJavascriptInterface(Object, String) instead to persist JavaScript objects across navigations.  
    应用程序应该使用 addJavascriptInterface 来跨navigations维护(持久化)JavaScript对象。  
  
## 案例  
### 原生代码  
```java  
webview.addJavascriptInterface(new JSInterface(this, webview), JSInterface.JS_INTERFACE_NAME);  
```  
  
```java  
public class JSInterface {  
    public static final String JS_INTERFACE_NAME = "JSInterface";//JS调用类名  
    private Context mContext;  
    private WebView webView;  
      
    public JSInterface(Context context, WebView webView) {  
        this.mContext = context;  
        this.webView = webView;  
    }  
      
    @JavascriptInterface  
    public void hello(String content) {  
        Log.i("bqt", "JS 调用原生时是否发生在主线程：" + (Looper.myLooper() == Looper.getMainLooper()));//false  
        new Handler(Looper.getMainLooper()).post(() -> //WebView等UI操作必须在主线程中进行  
                Toast.makeText(mContext, "原生的hello方法被调用了：" + content, Toast.LENGTH_SHORT).show());  
          
        SystemClock.sleep(3000);//模拟耗时操作  
          
        String call = "javascript:javacalljs(" + System.currentTimeMillis() + ")";//格式很重要，大部分错误都是由于格式问题导致的  
        new Handler(Looper.getMainLooper()).post(() -> webView.loadUrl(call));//WebView等UI操作必须在主线程中进行  
    }  
      
    @JavascriptInterface  
    public void hello2(String content) {  
        new Handler(Looper.getMainLooper()).post(() -> Toast.makeText(mContext, content, Toast.LENGTH_SHORT).show());  
          
        SystemClock.sleep(3000);//模拟耗时操作  
          
        String call = "javascript:javacalljs2(" + System.currentTimeMillis() + ")";//JS此方法的返回值会通过onReceiveValue回调到原生  
        new Handler(Looper.getMainLooper()).post(() -> webView.evaluateJavascript(call, value -> {  
            Log.i("bqt", "ValueCallback 是否发生在主线程：" + (Looper.myLooper() == Looper.getMainLooper()));//true  
            Toast.makeText(mContext, "【onReceiveValue】" + value, Toast.LENGTH_SHORT).show();  
        }));  
    }  
}  
```  
  
### 前端代码  
```html  
<!DOCTYPE html>  
<html lang="en">  
<head>  
    <meta charset="UTF-8">  
    <title>Title</title>  
</head>  
<body>  
  
<button onclick='jscalljava("包青天")'>点击后调用 JS 的 jscalljava 方法，在此方法中 JS 调用原生 JSInterface 类中的 hello 方法，原生 hello 方法执行完操作后回调 JS 中的 javacalljs 方法</button>  
<button onclick='jscalljava2()'>演示原生调用 JS 方法后，JS 将执行完return的结果通过原生提供的 ValueCallback 对象的 onReceiveValue 方法回调给原生</button>  
  
</body>  
  
<script type="text/javascript">  
function jscalljava(content){  
    console.log("【jscalljava】");  
    JSInterface.hello(content);  
}  
  
function jscalljava2(){  
    console.log("【jscalljava2】");  
    var content="[\"包青天\",\"白乾涛\",\"bqt\"]";  
    JSInterface.hello2(content);  
}  
  
function javacalljs(content){  
    console.log("【js中的javacalljs方法被调用了】content="+content);  
}  
  
function javacalljs2(content){  
    console.log("【js中的javacalljs2方法被调用了】content="+content);  
    return 20094;  
}  
  
</script>  
</html>  
```  
  
2017-8-25  
