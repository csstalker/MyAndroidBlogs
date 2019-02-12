| Markdown版本笔记 | 我的GitHub首页 | 我的博客 | 我的微信 | 我的邮箱 |  
| :------------: | :------------: | :------------: | :------------: | :------------: |  
| [MyAndroidBlogs][Markdown] | [baiqiantao][GitHub] | [baiqiantao][博客] | bqt20094 | baiqiantao@sina.com |  
  
[Markdown]:https://github.com/baiqiantao/MyAndroidBlogs  
[GitHub]:https://github.com/baiqiantao  
[博客]:http://www.cnblogs.com/baiqiantao/  
  
WebView 加载网页 加载资源 总结 MD  
***  
目录  
===  

- [WebView 加载数据的几种方法](#webview-加载数据的几种方法)
	- [loadData](#loaddata)
		- [使用说明](#使用说明)
		- [加载中文注意事项](#加载中文注意事项)
	- [loadDataWithBaseURL](#loaddatawithbaseurl)
		- [使用说明](#使用说明)
		- [baseUrl的用法解析](#baseurl的用法解析)
	- [loadUrl](#loadurl)
	- [postUrl](#posturl)
	- [evaluateJavascript](#evaluatejavascript)
- [加载各种类型的资源方法总结](#加载各种类型的资源方法总结)
- [一个完整的案例](#一个完整的案例)
	- [Java 代码](#java-代码)
	- [参考HTML代码](#参考html代码)
  
# WebView 加载数据的几种方法  
[demo](https://github.com/baiqiantao/WebViewTest.git)  
  
## loadData  
使用 loadUrl 不但浪费流量，而且加载起来也慢，所以可以将页面提前写好放到项目中，这样就可以用 loadData 更快的加载页面，用户体验会好些。  
  
```java  
void loadData(String data, String mimeType, String encoding)  
```  
- String data : a String of data in the given encoding  
- String mimeType : the MIME type of the data, e.g. 'text/html'  
- String encoding : the encoding of the data  
  
### 使用说明  
Loads the given data into this WebView using a 'data' scheme URL.  
使用 'data' 方案 URL 将给定的数据加载到此WebView中。  
  
Note that JavaScript's same origin policy means that script running in a page loaded using this method will be unable to access content loaded using any scheme other than 'data', including 'http(s)'.   
请注意，JavaScript的"同源策略"意味着，在使用此方法加载的页面中运行的脚本，将无法访问使用除了使用 data方案 之外的任何内容，包括http。  
  
---  
  
To avoid this restriction, use loadDataWithBaseURL() with an appropriate base URL.  
为避免此限制，请使用带有适当的base URL的loadDataWithBaseURL()方法。  
  
The encoding parameter specifies whether the data is base64 or URL encoded. If the data is base64 encoded, the value of the encoding parameter must be 'base64'.   
encoding参数用于指定数据是否是采用base64编码或URL编码。如果数据为base64编码，则编码参数的值必须为'base64'。  
  
For all other values of the parameter, including null, it is assumed that the data uses ASCII encoding for octets inside the range of safe URL characters and use the standard %xx hex encoding of URLs for octets outside that range.   
对于此参数的其他所有可能值，包括null，假定数据在安全URL字符范围内使用八位字节的ASCII编码，并且，在此范围以外的字节使用标准的 %xx 十六进制编码的八位字节的URL。  
  
For example, '#', '%', '\', '?' should be replaced by %23, %25, %27, %3f respectively.  
例如， '＃'， '％'， '\'， '？'应分别由％23，％25，％27，％3f代替。  
  
---  
  
The 'data' scheme URL formed by this method uses the default US-ASCII charset.   
由此方法形成的'data' 方案 URL使用默认的US-ASCII字符集。  
  
If you need to set a different charset, you should form a 'data' scheme URL which explicitly specifies a charset parameter in the mediatype portion of the URL and call loadUrl(String) instead.   
如果需要设置不同的字符集，则应该形成一个'data' 方案 URL，它 显式 地在URL的 mediatype 部分 指定 一个charset 参数，或者也可以通过调用loadUrl代替。  
  
Note that the charset obtained from the mediatype portion of a data URL always overrides that specified in the HTML or XML document itself.  
请注意，从数据URL的 mediatype部分 获取的字符集，总是覆盖HTML或XML文档本身指定的字符集。  
  
### 加载中文注意事项  
在使用 loadData 方法时，如果按照官网文档样式去写，当出现中文时中文部分会出现乱码，即使指定“utf-8”、“gbk”、“gb2312”也都一样。  
```java  
String time = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss SSS", Locale.getDefault()).format(new Date());  
String data = "<html><body>你好：<b>包青天</b> <p/>请登录</body></html>" + "<p/>" + time;  
```  
  
错误方式一：  
```java  
webview.loadData(data, "text/html", "UTF8");  
```  
![](index_files/d6a28b46-437a-48bc-ab33-8dddc4d0b938.png)  
  
错误方式二：  
```java  
webview.loadData(data, "text/html", "base64");  
```  
![](index_files/277fed31-cb9e-4860-b667-2ed88c0f25fa.png)  
  
正确方式一：用loadData去加载  
```java  
webview.loadData(data, "text/html; charset=UTF-8", "encoding可以是base64之外的任何内容");  
```  
![](index_files/8f3c4eef-2646-4300-be2f-88f70fae2f50.png)  
  
正确方式二：用loadDataWithBaseURL去加载  
```java  
webview.loadDataWithBaseURL(null, data, "text/html", "UTF-8", null);  
```  
  
**结论**  
对于loadData中的encoding参数的值：  
- If the data is base64 encoded, the value of the encoding parameter must be 'base64'，如果数据为base64编码，则编码参数的值必须为'base64'  
- 如果数据不是base64编码，设置为null或其他任何值(除了"base64"之外)都是没有影响的  
- 如果数据不是base64编码，那么绝对不可以设置为【base64】，否则会解析异常  
  
## loadDataWithBaseURL  
此方法实际上比loadData用途广泛的多，也是官方推荐使用的方法。  
  
```java  
void loadDataWithBaseURL(String baseUrl, String data, String mimeType, String encoding, String historyUrl)  
```  
- baseUrl : the URL to use as the page's base URL. If null defaults to 'about:blank'.  
- data : a String of data in the given encoding  
- mimeType : the MIMEType of the data, e.g. 'text/html'. If null, defaults to 'text/html'.  
- encoding : the encoding of the data  
- historyUrl : the URL to use as the history entry. If null defaults to 'about:blank'. If non-null, this must be a valid URL.  
  
### 使用说明  
Loads the given data into this WebView, using baseUrl as the base URL for the content.   
将给定的数据加载到此WebView中，使用baseUrl作为内容的基本URL。  
  
The base URL is used both to resolve relative URLs and when applying JavaScript's same origin policy.   
基本URL既用于解析相对URL，又用于应用JavaScript的同源策略。  
  
---  
  
The historyUrl is used for the history entry.  
 historyUrl用于历史记录条目。  
  
---  
  
Note that content specified in this way can access local device files (via 'file' scheme URLs) only if baseUrl specifies a scheme other than 'http', 'https', 'ftp', 'ftps', 'about' or 'javascript'.  
请注意，只有在baseUrl指定了除“http”，“https”，“ftp”，“ftps”，“about”或“javascript”之外的scheme时，以此方式指定的内容才能访问本地设备文件(通过 'file' scheme URL)。  
  
If the base URL uses the data scheme, this method is equivalent to calling loadData() and the historyUrl is ignored, and the data will be treated as part of a data: URL.   
如果baseUrl使用data scheme，则此方法相当于调用loadData方法，并且historyUrl会被忽略，并且data将被视为 data:URL 的一部分。  
  
If the base URL uses any other scheme, then the data will be loaded into the WebView as a plain string (i.e. not part of a data URL) and any URL-encoded entities in the string will not be decoded.  
如果baseUrl使用任何其他scheme，则data将作为纯字符串(即不是 data URL 的一部分)加载到WebView中，并且字符串中的任何URL编码实体将不被解码。  
  
Note that the baseUrl is sent in the 'Referer' HTTP header when requesting subresources (images, etc.) of the page loaded using this method.  
请注意，当请求使用此方法，加载页面的子资源(图像等)时，baseUrl会在 'Referer' HTTP头中发送。  
  
### baseUrl的用法解析  
此方法中的第一个参数baseUrl的值的作用是：指定你第二个参数data中数据是以什么地址为基准的。  
这个参数是非常有用、非常重要的，因为data中的数据可能会有超链接或者是image元素，而很多网站中使用的地址都是相对路径，如果没有指定baseUrl，webview将访问不到这些资源。估计这就是文档中说的：JS的"同源策略"。  
  
> JavaScript's Same Origin Policy：  
JS的同源策略：它认为自任何站点装载的信赖内容是不安全的，当被浏览器半信半疑的脚本运行在沙箱时，它们应该只被允许访问来自同一站点的资源，而不是那些来自其它站点可能怀有恶意的资源。  
  
例如  
```java  
String data = "这里两个图片的地址是相对路径<img src='/2015/74/33.jpg' /><p/><img src='/2015/74/35.jpg' />";  
String baseUrl = "http://img.mmjpg.com";  
webview.loadDataWithBaseURL(baseUrl, data, "text/html", "utf-8", null);  
```  
如果baseUrl没有指定，那么这两张图片将显示不出来，因为这里两个图片的地址是相对路径，不指定baseUrl时根本找不到这两张图片。  
  
## loadUrl  
```java  
void loadUrl(String url) Loads the given URL.  
void loadUrl(String url, Map<String, String> additionalHttpHeaders) Loads the given URL with the specified additional HTTP headers.  
```  
  
Map: the additional headers to be used in the HTTP request for this URL, specified as a map from name to value.  
要在此URL的HTTP请求中使用的附加的headers，指定为从name到value的映射。  
  
Note that if this map contains any of the headers that are set by default by this WebView, such as those controlling caching, accept types or the User-Agent, their values may be overridden by this WebView's defaults.    
请注意，如果此映射包含此WebView默认设置的任何headers，例如控制缓存，可接受的类型，或User-Agent，那么这些设定的值可能会被该WebView的默认值所覆盖。  
  
## postUrl  
```java  
void postUrl (String url, byte[] postData)  
```  
- String url : the URL of the resource to load  
- byte[] postData : the data will be passed to "POST" request, which must be be "application/x-www-form-urlencoded" encoded.  
  
Loads the URL with postData using "POST" method into this WebView.   
If url is not a network URL, it will be loaded with loadUrl(String) instead, ignoring the postData param.  
```java  
String url = "http://tapi.95xiu.com/app/pay/weixinmiao/user_pay.php" ;  
String postData= "uid=" + uid + "&session_id=" + session_id + "&total_fee="+total_fee;  
webview.postUrl(url , EncodingUtils.getBytes(postData, "base64"));  
```  
  
## evaluateJavascript  
```java  
void evaluateJavascript (String script, ValueCallback<String> resultCallback)  
```  
Asynchronously evaluates JavaScript in the context of the currently displayed page. If non-null, |resultCallback| will be invoked with any result returned from that execution. This method must be called on the UI thread and the callback will be made on the UI thread.  
在当前显示页面的上下文中异步执行JavaScript。 如果非空，| resultCallback | 将使用从该执行返回的任何结果来调用它。 必须在UI线程上调用此方法，并在UI线程上进行回调。  
  
  
# 加载各种类型的资源方法总结  
**加载assets下的资源**  
```java  
webView.loadUrl("file:///android_asset/" + "h5/test.html");  
```  
或  
```java  
String data = "<html><body>包青天<p/><img src='icon.jpg' /></body></html>" + "<p/>" + time;  
webView.loadDataWithBaseURL("file:///android_asset/", data, "text/html", "utf-8", null);  
```  
  
**加载 res/drawable 或 res/mipmap  或 res/raw 下的资源**  
```java  
webView.loadUrl("file:///android_res/" + "drawable/ic_launcher");  
webView.loadUrl("file:///android_res/" + "mipmap/ic_launcher");  
webView.loadUrl("file:///android_res/" + "raw/test");  
```  
或  
```java  
data = "<html><body>包青天<p/><img src='drawable/icon' /></body></html>" + "<p/>" + time;  
webView.loadDataWithBaseURL("file:///android_res/", data, "text/html", "utf-8", null);  
```  
  
**加载SD卡中的资源**  
```java  
String FILE_BASE_SIMPLE = "file://" + Environment.getExternalStorageDirectory().getAbsolutePath()+ "/";//【file:///sdcard/】  
webView.loadUrl(FILE_BASE_SIMPLE + "test.html");//【file:///storage/emulated/0/test.html】或【file:///sdcard/test.html】  
```  
或  
```java  
data = "<html><body>包青天<p/><img src='icon.png' /></body></html>" + "<p/>" + time;  
webView.loadDataWithBaseURL(FILE_BASE_SIMPLE, data, "text/html", "utf-8", null);  
```  
  
# 一个完整的案例  
演示load不同资源的方法  
  
## Java 代码  
```java  
public class LoadUrl_DataActivity extends ListActivity {  
    private WebView webView;  
    private boolean b;  
      
    protected void onCreate(Bundle savedInstanceState) {  
        super.onCreate(savedInstanceState);  
        String[] array = {"0、loadData时中文乱码问题，错误解决方案",  
                "1、loadData时中文乱码问题，正确解决方案",  
                "2、演示loadDataWithBaseURL方法中，参数baseUrl的作用",  
                "3、加载assets下的资源",  
                "4、加载res/drawable下的资源",  
                "5、加载res/raw下的资源",  
                "6、加载SD卡中的资源",  
                "7、加载网页http资源",};  
        setListAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, new ArrayList<>(Arrays.asList(array))));  
        webView = new WebView(this);  
        getListView().addFooterView(webView);  
    }  
      
    @Override  
    protected void onListItemClick(ListView l, View v, int position, long id) {  
        String time = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss SSS", Locale.getDefault()).format(new Date());  
        String data = "<html><body>包青天<p/><a href=http://www.baidu.com>百度</a></body></html>" + "<p/>" + time;  
          
        b = !b;  
        Toast.makeText(this, "" + b, Toast.LENGTH_SHORT).show();  
        switch (position) {  
            case 0://loadData时中文乱码问题，错误解决方案  
                String encoding = b ? "UTF8" : "base64";//encoding参数的值，设置为null或其他任何值(除了"base64")都是没有影响的  
                webView.loadData(data, "text/html", encoding);//如果不是采用的base64编码，那么绝对不可以设置为base64  
                break;  
            case 1://loadData时中文乱码问题，正确解决方案  
                if (b) webView.loadData(data, "text/html; charset=UTF-8", "encoding可以是base64之外的任何内容");  
                else webView.loadDataWithBaseURL(null, data, "text/html", "utf-8", null);  
                break;  
            case 2://演示loadDataWithBaseURL方法中，参数baseUrl的作用  
                String baseUrl = b ? null : "http://img.mmjpg.com";//不设置baseUrl时，data2里面的两张图片将显示不出来  
                String data2 = "这里两个图片的地址是相对路径<img src='/2017/936/5.jpg' /><p/><img src='/2015/74/35.jpg' />";  
                webView.loadDataWithBaseURL(baseUrl, data2, "text/html", "utf-8", null);  
                break;  
            case 3://加载assets下的资源  
                data = "<html><body>包青天<p/><img src='icon.jpg' /></body></html>" + "<p/>" + time;  
                if (b) webView.loadUrl(URLUtils.ASSET_BASE + "h5/test.html");  
                else webView.loadDataWithBaseURL(URLUtils.ASSET_BASE, data, "text/html", "utf-8", null);  
                break;  
            case 4://加载res/drawable 或 res/mipmap下的资源。不会区分drawable与drawable-***，但会区分drawable和mipmap  
                data = "<html><body>包青天<p/><img src='drawable/icon.不会校验文件后缀名' /></body></html>" + "<p/>" + time;  
                if (b) webView.loadUrl(URLUtils.RESOURCE_BASE + "mipmap/ic_launcher");//建议直接省略文件后缀名  
                else webView.loadDataWithBaseURL(URLUtils.RESOURCE_BASE, data, "text/html", "utf-8", null);  
                break;  
            case 5://加载res/raw下的资源，和res/drawable一样，都属于Resources资源文件  
                data = "<html><body>包青天<p/><img src='raw/icon.jpg' /></body></html>" + "<p/>" + time;  
                if (b) webView.loadUrl(URLUtils.RESOURCE_BASE + "raw/test");  
                else webView.loadDataWithBaseURL(URLUtils.RESOURCE_BASE, data, "text/html", "utf-8", null);  
                break;  
            case 6://加载SD卡中的资源。注意，如果提示【net::ERR_ACCESS_DENIED】，是因为没有申请权限  
                data = "<html><body>包青天<p/><img src='icon.png' /></body></html>" + "<p/>" + time;  
                if (b) webView.loadUrl(URLUtils.FILE_BASE_SIMPLE + "test.html");  
                else webView.loadDataWithBaseURL(URLUtils.FILE_BASE_SIMPLE, data, "text/html", "utf-8", null);  
                break;  
            case 7://加载网页http资源  
                webView.loadUrl(b ? "http://www.meituba.com/" : "http://img.mmjpg.com/2017/936/5.jpg");  
                break;  
        }  
    }  
}  
```  
  
## 参考HTML代码  
```html  
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">  
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en">  
<head>  
    <meta http-equiv="Content-Type" content="text/html;charset=UTF-8">  
    <meta name="Description" content="Description"/>  
    <meta name="Keywords" content="Keywords"/>  
    <title>title</title>  
</head>  
<body>  
<h1>加载资源</h1>  
​  
<p>res下的图片 src='file:///android_res/drawable/ic_launcher'</p>  
<img src='file:///android_res/drawable/ic_launcher' alt="" width="100%p"/>  
​  
<p>assets下的图片 src='file:///android_asset/icon.jpg'</p>  
<img src='file:///android_asset/icon.jpg' alt="" width="100%p"/>  
​  
<p>SD卡下的图片 src='file:///sdcard/icon.png'</p>  
<img src='file:///sdcard/icon.png' alt="" width="100%p"/>  
​  
<p>  
    <a href=" tel:15031257363">电话</a>&nbsp;  
    <a href="sms:15031257363">短信</a>&nbsp;  
    <a href="mailto:1242599243@qq.com">邮件</a>  
</p>  
</body>  
</html>  
```  
  
2017-7-27  
