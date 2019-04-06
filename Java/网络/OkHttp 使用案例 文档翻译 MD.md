| Markdown版本笔记 | 我的GitHub首页 | 我的博客 | 我的微信 | 我的邮箱 |  
| :------------: | :------------: | :------------: | :------------: | :------------: |  
| [MyAndroidBlogs][Markdown] | [baiqiantao][GitHub] | [baiqiantao][博客] | bqt20094 | baiqiantao@sina.com |  
  
[Markdown]:https://github.com/baiqiantao/MyAndroidBlogs  
[GitHub]:https://github.com/baiqiantao  
[博客]:http://www.cnblogs.com/baiqiantao/  
  
***  
目录  
===  

- [Recipes 食谱/知识点清单](#Recipes-食谱知识点清单)
	- [Synchronous Get 同步Get](#Synchronous-Get-同步Get)
	- [Asynchronous Get 异步Get](#Asynchronous-Get-异步Get)
	- [Accessing Headers 访问头信息](#Accessing-Headers-访问头信息)
	- [Posting a String 以Post发送字符串](#Posting-a-String-以Post发送字符串)
	- [Post Streaming 以Post发送流](#Post-Streaming-以Post发送流)
	- [Posting a File 以Post发送文件](#Posting-a-File-以Post发送文件)
	- [Posting form parameters 以Post发送表单参数](#Posting-form-parameters-以Post发送表单参数)
	- [Posting a multipart request 以Post发送多请求体的请求](#Posting-a-multipart-request-以Post发送多请求体的请求)
	- [Parse a JSON Response With Gson/Moshi 使用Gson来解析响应](#Parse-a-JSON-Response-With-GsonMoshi-使用Gson来解析响应)
	- [Response Caching 缓存响应](#Response-Caching-缓存响应)
	- [Canceling a Call 取消一个请求](#Canceling-a-Call-取消一个请求)
	- [Timeouts 超时](#Timeouts-超时)
	- [Per-call Configuration 预配置](#Per-call-Configuration-预配置)
	- [Handling authentication 处理身份验证](#Handling-authentication-处理身份验证)
  
# Recipes 食谱/知识点清单  
[博客地址](https://www.cnblogs.com/baiqiantao/p/7049811.html)  
[原文位置](https://github.com/square/okhttp/wiki/Recipes)  
  
We've written some recipes that demonstrate how to solve common problems with OkHttp. Read through them to learn about how everything works together. Cut-and-paste these examples freely; that's what they're for.  
> 我们编写了一些食谱，演示如何解决使用OkHttp时常见的问题。 阅读他们，了解一切是如何一起工作。 自由剪切并粘贴这些例子，这就是他们的目的。  
  
## Synchronous Get 同步Get  
Download a file, print its headers, and print its response body as a string.  
> 下载一个文件，打印它的响应结果的响应头，并以字符串形式将它的响应体打印出来。  
  
The string() method on response body is convenient and efficient for small documents. But if the response body is large (greater than 1 MiB), avoid string() because it will load the entire document into memory. In that case, prefer to process the body as a stream.  
> 响应体中的string()方法对于小文档来时是方便和高效的。但是如果响应体很大（大于1Mib），则应避免使用string()方法，因为它将把整个文档加载到内存中。在这种情况下，可以将响应体作为流来处理。  
  
```java  
public void synchronousGet() throws Exception {  
    Request request = new Request.Builder()//Get请求  
            .url("http://publicobject.com/helloworld.txt")  
            .build();  
      
    Response response = client.newCall(request).execute();//同步请求  
    if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);//响应失败  
      
    Headers responseHeaders = response.headers();//响应头  
    for (int i = 0; i < responseHeaders.size(); i++) {  
        System.out.println("【" + responseHeaders.name(i) + "】" + responseHeaders.value(i));  
    }  
      
    System.out.println("【响应结果】" + response.body().string());//响应体  
}  
```  
  
## Asynchronous Get 异步Get  
Download a file on a worker thread, and get called back when the response is readable. The callback is made after the response headers are ready. Reading the response body may still block. OkHttp doesn't currently offer asynchronous APIs to receive a response body in parts.  
> 在工作线程上下载一个文件，并在响应可读时调用。回调是在响应头准备好之后进行的。读取响应体可能仍然会阻塞。OkHttp当前不提供异步API以部分接收响应主体。  
  
```java  
public void asynchronousGet() throws Exception {  
    Request request = new Request.Builder()  
            .url("http://publicobject.com/helloworld.txt")  
            .build();  
      
    client.newCall(request).enqueue(new Callback() {//异步请求  
        @Override  
        public void onFailure(Call call, IOException e) {  
            e.printStackTrace();  
        }  
          
        @Override  
        public void onResponse(Call call, Response response) throws IOException {  
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);  
              
            Headers responseHeaders = response.headers();  
            for (int i = 0, size = responseHeaders.size(); i < size; i++) {  
                System.out.println("【" + responseHeaders.name(i) + "】" + responseHeaders.value(i));  
            }  
              
            System.out.println("【响应结果】" + response.body().string());  
        }  
    });  
}  
```  
  
## Accessing Headers 访问头信息  
Typically HTTP headers work like a Map<String, String>: each field has one value or none. But some headers permit multiple values, like Guava's Multimap. For example, it's legal and common for an HTTP response to supply multiple Vary headers. OkHttp's APIs attempt to make both cases comfortable.  
> 通常，HTTP头的工作方式类似于Map <String，String>：每个字段都有一个值或没有值。 但是一些头允许多个值，如Guava的Multimap。 例如，HTTP响应提供多个Vary(变化、不等)标头是合法和通用的。 OkHttp的API尝试使这两种情况都舒适。  
  
When writing request headers, use header(name, value) to set the only occurrence of name to value. If there are existing values, they will be removed before the new value is added. Use addHeader(name, value) to add a header without removing the headers already present.  
> 当写请求头时，使用 header(name, value) 将设置唯一发生的(出现的)名称的值。如果存在现有值，则在添加新值之前将删除它们。 使用 addHeader(name, value) 来添加标题时，将不会移除已经存在的头。  
  
When reading response a header, use header(name) to return the last occurrence of the named value. Usually this is also the only occurrence! If no value is present, header(name) will return null. To read all of a field's values as a list, use headers(name).  
> 当读取响应一个头时，使用 header(name) 返回最后一次出现的名称的值。 通常这也是唯一发生的事情！ 如果没有值，header(name) 将返回null。 要读取所有字段的值作为列表，请使用 headers(name) 。  
  
To visit all headers, use the Headers class which supports access by index.  
> 要访问所有标题，请使用支持通过索引访问的Headers类。  
  
```java  
public void accessingHeaders() throws Exception {  
    Request request = new Request.Builder()  
            .url("https://api.github.com/repos/square/okhttp/issues")  
            .header("User-Agent", "OkHttp Headers.java")//使用 header 会移除已经存在的头  
            .addHeader("Accept", "application/json; q=0.5")  
            .addHeader("Accept", "application/vnd.github.v3+json")//使用 addHeader 不会移除已经存在的头  
            .build();  
      
    Response response = client.newCall(request).execute();  
    if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);  
      
    //访问Header  
    System.out.println("【Server】" + response.header("Server"));  
    System.out.println("【Date】" + response.header("Date"));  
    System.out.println("【Vary】" + response.headers("Vary"));  
}  
```  
  
## Posting a String 以Post发送字符串  
Use an HTTP POST to send a request body to a service. This example posts a markdown document to a web service that renders markdown as HTML. Because the entire request body is in memory simultaneously, avoid posting large (greater than 1 MiB) documents using this API.  
> 使用HTTP POST将请求体发送到服务器。此示例将一个markdown文档发送到一个将markdown作为HTML来显示(呈现)的web服务器上。 因为整个请求体同时在内存中，所以请避免使用此API发送大型（大于1个MiB）文档。  
  
```java  
public static final MediaType MEDIA_TYPE_MARKDOWN = MediaType.parse("text/x-markdown; charset=utf-8");  
  
public void postString() throws Exception {  
    String postBody = ""  
            + "Releases\n"  
            + "--------\n"  
            + "\n"  
            + " * _1.0_ May 6, 2013\n"  
            + " * _1.1_ June 15, 2013\n"  
            + " * _1.2_ August 11, 2013\n";  
      
    Request request = new Request.Builder()  
            .url("https://api.github.com/markdown/raw")  
            .post(RequestBody.create(MEDIA_TYPE_MARKDOWN, postBody))//以Post发送字符串，字符串是MARKDOWN文件中的内容  
            .build();  
}  
```  
  
## Post Streaming 以Post发送流  
Here we POST a request body as a stream. The content of this request body is being generated as it's being written. This example streams directly into the Okio buffered sink. Your programs may prefer an OutputStream, which you can get from BufferedSink.outputStream().  
> 这里我们将一个请求体作为流来发送。这个请求体的内容在被写入时生成。此示例直接流入Okio缓冲池(sink：水槽、水池)。您编程时可能更喜欢使用OutputStream，您可以从BufferedSink.outputStream()中获取。  
  
```java  
public static final MediaType MEDIA_TYPE_MARKDOWN = MediaType.parse("text/x-markdown; charset=utf-8");  
  
public void postStreaming() throws Exception {  
    RequestBody requestBody = new RequestBody() {  
        @Override  
        public MediaType contentType() {  
            return MEDIA_TYPE_MARKDOWN;//内容类型为：MARKDOWN媒体文件  
        }  
          
        @Override  
        public void writeTo(BufferedSink sink) throws IOException {  
            sink.writeUtf8("Numbers\n");  
            sink.writeUtf8("-------\n");  
            for (int i = 2; i <= 997; i++) {  
                sink.writeUtf8(String.format(" * %s = %s\n", i, factor(i)));  
            }  
        }  
          
        private String factor(int n) {  
            for (int i = 2; i < n; i++) {  
                int x = n / i;  
                if (x * i == n) return factor(x) + " × " + i;  
            }  
            return Integer.toString(n);  
        }  
    };  
      
    Request request = new Request.Builder()  
            .url("https://api.github.com/markdown/raw")  
            .post(requestBody)//以Post发送流  
            .build();  
}  
```  
  
## Posting a File 以Post发送文件  
It's easy to use a file as a request body.  
> 使用OkHttp可以很容易将文件作为请求体来进行发送。  
  
```java  
public static final MediaType MEDIA_TYPE_MARKDOWN = MediaType.parse("text/x-markdown; charset=utf-8");  
  
public void postFile() throws Exception {  
    File file = new File(Environment.getExternalStorageDirectory(), "README.md");  
      
    Request request = new Request.Builder()  
            .url("https://api.github.com/markdown/raw")  
            .post(RequestBody.create(MEDIA_TYPE_MARKDOWN, file))//以Post发送文件  
            .build();  
}  
```  
  
## Posting form parameters 以Post发送表单参数  
Use FormBody.Builder to build a request body that works like an HTML `<form>` tag. Names and values will be encoded using an HTML-compatible form URL encoding.  
> 使用FormBody.Builder构建一个像HTML `<form>`标签一样工作的请求体。 将使用HTML兼容的表单URL编码对名称和值进行编码。  
  
```java  
public void postFormParameters() throws Exception {  
    RequestBody formBody = new FormBody.Builder()  
            .add("search", "Jurassic Park")  
            .build();  
  
    Request request = new Request.Builder()  
            .url("https://en.wikipedia.org/w/index.php")  
            .post(formBody)//以Post发送表单参数  
            .build();  
}  
```  
  
## Posting a multipart request 以Post发送多请求体的请求  
MultipartBody.Builder can build sophisticated request bodies compatible with HTML file upload forms. Each part of a multipart request body is itself a request body, and can define its own headers. If present, these headers should describe the part body, such as its Content-Disposition. The Content-Length and Content-Type headers are added automatically if they're available.  
> MultipartBody.Builder可以构建与HTML文件上传表单兼容的复杂请求体。多部分请求体的每个部分本身就是一个请求体，并且可以定义自己的头。如果存在这样的请求体部件，这些头应该描述请求体部件主体，比如它的Content-Disposition(内容配置)。 如果这些请求体可用，那么Content-Length(内容长度)和Content-Type(内容类型)将自动添加到头部字段。  
  
```java  
private static final MediaType MEDIA_TYPE_PNG = MediaType.parse("image/png");  
  
public void postMultipartRequest() throws Exception {  
    RequestBody partRequestBody = RequestBody.create(MEDIA_TYPE_PNG, new File(Environment.getExternalStorageDirectory(), "logo.png"));  
    RequestBody requestBody = new MultipartBody.Builder()  
            .setType(MultipartBody.FORM)  
            .addFormDataPart("title", "Square Logo")  
            .addFormDataPart("image", "logo-square.png", partRequestBody)//多部分请求体的每个部分本身就是一个请求体  
            .build();  
      
    Request request = new Request.Builder()  
            .header("Authorization", "Client-ID " + "...")  
            .url("https://api.imgur.com/3/image")  
            .post(requestBody)//以Post发送多请求体的请求  
            .build();  
}  
```  
  
## Parse a JSON Response With Gson/Moshi 使用Gson来解析响应  
Moshi is a handy API for converting between JSON and Java objects. Here we're using it to decode a JSON response from a GitHub API.  
> Moshi(以前推荐的是Gson，不知道什么时候改成了Moshi)是用于在JSON和Java对象之间进行转换的一个方便的API。这里我们使用它来解码来自GitHub API的JSON响应。  
  
Note that ResponseBody.charStream() uses the Content-Type response header to select which charset to use when decoding the response body. It defaults to UTF-8 if no charset is specified.  
> 请注意，ResponseBody.charStream()使用Content-Type响应头来选择，在解码响应体时，应使用哪个字符集。如果未指定字符集，则默认为UTF-8。  
  
```java  
private final Moshi moshi = new Moshi.Builder().build();  
  
public void parseJSONResponseWithGson() throws Exception {  
    Request request = new Request.Builder()  
            .url("https://api.github.com/gists/c2a7c39532239ff261be")  
            .build();  
  
    Response response = client.newCall(request).execute();  
    if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);  
      
    Gist gist = new Gson().fromJson(response.body().charStream(), Gist.class);//使用Gson来解析JSON响应  
    Gist gist2 = moshi.adapter(Gist.class).fromJson(response.body().source());//使用Moshi来解析JSON响应  
      
    for (Map.Entry<String, Gist.GistFile> entry : gist.files.entrySet()) {  
        System.out.println("【Key】" + entry.getKey());  
        System.out.println("【Value】" + entry.getValue().content);  
    }  
}  
  
static class Gist {  
    Map<String, GistFile> files;  
}  
  
static class GistFile {  
    String content;  
}  
```  
  
## Response Caching 缓存响应  
To cache responses, you'll need a cache directory that you can read and write to, and a limit on the cache's size. The cache directory should be private, and untrusted applications should not be able to read its contents!  
> 要缓存响应，您将需要一个可以读取和写入的缓存目录，并对缓存大小进行限制。 缓存目录应该是私有的，不受信任的应用程序不能读取其内容！  
  
It is an error to have multiple caches accessing the same cache directory simultaneously. Most applications should call new OkHttpClient() exactly once, configure it with their cache, and use that same instance everywhere. Otherwise the two cache instances will stomp on each other, corrupt the response cache, and possibly crash your program.  
> 让多个缓存同时访问同一缓存目录是一个错误。 大多数应用程序都应该只调用一次 new OkHttpClient()，配置它们的缓存，并在任何地方使用相同的实例。 否则两个缓存实例将彼此踩踏(相互影响)，损坏响应缓存，并可能会导致程序崩溃。  
  
Response caching uses HTTP headers for all configuration. You can add request headers like Cache-Control: max-stale=3600 and OkHttp's cache will honor them. Your webserver configures how long responses are cached with its own response headers, like Cache-Control: max-age=9600. There are cache headers to force a cached response, force a network response, or force the network response to be validated with a conditional GET.  
> 响应缓存使用HTTP头进行所有配置。 您可以在请求头部添加 Cache-Control:max-stale=3600，OkHttp的缓存将使其符合要求(存就会执行)。您的web服务器通过它自己的响应头，来配置响应缓存的时间长短，如Cache-Control:max-age = 9600。有一些缓存头强制缓存响应，强制网络响应，或强制使用有条件的GET对网络响应进行验证。  
  
To prevent a response from using the cache, use CacheControl.FORCE_NETWORK. To prevent it from using the network, use CacheControl.FORCE_CACHE. Be warned: if you use FORCE_CACHE and the response requires the network, OkHttp will return a 504 Unsatisfiable Request response.  
> 当不使用缓存时，可以使用 CacheControl.FORCE_NETWORK 来防止使用缓存的响应 。当只使用缓存而不使用网络获取数据时，可以使用 CacheControl.FORCE_CACHE 。警告：如果您使用 FORCE_CACHE 并且响应结果需要从网络获取时，OkHttp将返回504不满足的请求响应。  
  
```java  
public void responseCaching() throws Exception {  
    File cacheFile = new File(Environment.getExternalStorageDirectory().getAbsoluteFile(), "cache");//缓存路径  
    Cache cache = new Cache(cacheFile, 10 * 1024 * 1024);//缓存大小10 MiB  
  
    client = new OkHttpClient.Builder()  
            .cache(cache)//缓存响应  
            .build();  
  
    Request request = new Request.Builder()  
            .url("http://publicobject.com/helloworld.txt")  
            .build();  
      
    Response response1 = client.newCall(request).execute();  
    if (!response1.isSuccessful()) throw new IOException("Unexpected code " + response1);  
      
    String response1Body = response1.body().string();  
    System.out.println("【Response 1 response】" + response1);  
    System.out.println("【Response 1 cache response】" + response1.cacheResponse());  
    System.out.println("【Response 1 network response】" + response1.networkResponse());  
      
    Response response2 = client.newCall(request).execute();  
    if (!response2.isSuccessful()) throw new IOException("Unexpected code " + response2);  
      
    String response2Body = response2.body().string();  
    System.out.println("【Response 2 response】" + response2);  
    System.out.println("【Response 2 cache response】" + response2.cacheResponse());  
    System.out.println("【Response 2 network response】" + response2.networkResponse());  
      
    System.out.println("【两次响应结果是否相同】" + response1Body.equals(response2Body));  
}  
```  
  
## Canceling a Call 取消一个请求  
Use Call.cancel() to stop an ongoing call immediately. If a thread is currently writing a request or reading a response, it will receive an IOException. Use this to conserve the network when a call is no longer necessary; for example when your user navigates away from an application. Both synchronous and asynchronous calls can be canceled.  
> 可以使用Call.cancel()立即停止正在进行的通话。 如果线程当前正在写请求或读取响应，它将收到一个IOException。 当一个Call不再需要时，可以使用它来节省网络(流量)；例如，当您的用户通过导航推出程序时。同步和异步呼叫都可以被取消。  
  
```java  
public void cancelingCall() throws Exception {  
    Request request = new Request.Builder()  
            .url("http://httpbin.org/delay/2") // This URL is served with a 2 second delay. 此网址延迟2秒后才处理请求。  
            .build();  
      
    final long startNanos = System.nanoTime();  
    final Call call = client.newCall(request);  
      
    // Schedule a job to cancel the call in 1 second.  在1秒内安排一个作业取消请求。  
    Executors.newScheduledThreadPool(1).schedule(() -> {  
        System.out.printf("【取消前】%.2f Canceling call.%n", (System.nanoTime() - startNanos) / 1e9f);  
        call.cancel();//取消一个请求  
        System.out.printf("【取消后】%.2f Canceled call.%n", (System.nanoTime() - startNanos) / 1e9f);  
    }, 1, TimeUnit.SECONDS);  
      
    try {  
        System.out.printf("【读取响应前】%.2f Executing call.%n", (System.nanoTime() - startNanos) / 1e9f);  
        Response response = call.execute();  
        System.out.printf("【读取响应后】%.2f Call was expected to fail, but completed: %s%n", (System.nanoTime() - startNanos) / 1e9f, response);  
    } catch (IOException e) {  
        System.out.printf("【正在读取响应时取消请求会收到一个异常】%.2f Call failed as expected: %s%n", (System.nanoTime() - startNanos) / 1e9f, e);  
    }  
}  
```  
  
## Timeouts 超时  
Use timeouts to fail a call when its peer is unreachable. Network partitions can be due to client connectivity problems, server availability problems, or anything between. OkHttp supports connect, read, and write timeouts.  
> 当网络请求不可到达时，Call请求会由于超时而导致失败。网络不可到达的原因可能是由于客户端连接问题，服务器可用性问题，或客户端和服务器之间的任何问题。 OkHttp支持连接超时、读取超时以及写入超时。  
  
```java  
public void settTmeouts() throws Exception {  
    client = new OkHttpClient.Builder()  
            .connectTimeout(10, TimeUnit.SECONDS)//连接超时时间  
            .writeTimeout(10, TimeUnit.SECONDS)//写入超时时间  
            .readTimeout(500, TimeUnit.MILLISECONDS)//读取超时时间  
            .build();  
  
    Request request = new Request.Builder()  
            .url("http://httpbin.org/delay/2") // This URL is served with a 2 second delay.  
            .build();  
      
    Response response = client.newCall(request).execute();  
    System.out.println("【响应结果】" + response.body().string());  
}  
```  
  
## Per-call Configuration 预配置  
All the HTTP client configuration lives in OkHttpClient including proxy settings, timeouts, and caches. When you need to change the configuration of a single call, call OkHttpClient.newBuilder(). This returns a builder that shares the same connection pool, dispatcher, and configuration with the original client. In the example below, we make one request with a 500 ms timeout and another with a 3000 ms timeout.  
> 所有HTTP客户端配置都存在于OkHttpClient中，包括代理设置，超时和高速缓存。当您需要更改单个Call的配置时，请调用OkHttpClient.newBuilder()。这个方法返回一个Builder，该Builder与初始客户端共享相同的连接池、分发器和配置。 在下面的示例中，我们将使用一个500毫秒的超时的请求，和另一个3000毫秒的超时的请求。  
  
```java  
public void perCallConfiguration() throws Exception {  
    OkHttpClient copyClient = client.newBuilder()// Copy to customize OkHttp for this request.  
            .readTimeout(500, TimeUnit.MILLISECONDS)  
            .build();  
  
    Request request = new Request.Builder()  
            .url("http://httpbin.org/delay/1") // This URL is served with a 1 second delay.  
            .build();  
  
    Response response = copyClient.newCall(request).execute();  
    if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);  
  
    System.out.println("【响应结果】" + response.body().string());  
}  
```  
  
## Handling authentication 处理身份验证  
OkHttp can automatically retry unauthenticated requests. When a response is 401 Not Authorized, an Authenticator is asked to supply credentials. Implementations should build a new request that includes the missing credentials. If no credentials are available, return null to skip the retry.  
> OkHttp可以自动重试未经身份验证的请求。当响应为 401未授权 时，将要求身份验证者提供凭证(证书)。 实现/用户 应该构建一个新的请求，其中包含缺少的凭证(证书)。 如果没有可用的凭据，返回null以跳过重试。  
  
Use Response.challenges() to get the schemes and realms of any authentication challenges. When fulfilling a Basic challenge, use Credentials.basic(username, password) to encode the request header.  
> 使用 Response.challenges() 来获取任何认证质疑的方案和领域(范围、王国)。在验证一个基本的质疑时，请使用 Credentials.basic(username, password) 对请求头进行编码。  
  
```java  
//处理身份验证  
public void handlingAuthentication() throws Exception {  
    client = new OkHttpClient.Builder()  
            .authenticator((route, response) -> {//身份验证  
                System.out.println("【Authenticating for response】" + response);  
                System.out.println("【Challenges】" + response.challenges());//获取认证质疑的方案和领域  
                return response.request()  
                        .newBuilder()  
                        .header("Authorization", Credentials.basic("jesse", "password1"))//对请求头进行编码  
                        .build();  
            })  
            .build();  
  
    Request request = new Request.Builder()  
            .url("http://publicobject.com/secrets/hellosecret.txt")  
            .build();  
}  
```  
  
To avoid making many retries when authentication isn't working, you can return null to give up. For example, you may want to skip the retry when these exact credentials have already been attempted:  
> 为了避免在身份验证不工作时(无效时)进行多次重试，可以返回null来放弃(该请求)。例如，当这些确切的用户凭证已经被尝试时，用户可能希望跳过重试：  
  
```java  
if (credential.equals(response.request().header("Authorization"))) return null; // If we already failed with these credentials, don't retry.  
```  
  
You may also skip the retry when you’ve hit an application-defined attempt limit:  
> 当你超过应用程序定义的尝试连接限制(次数)时，你也可能希望跳过重试：  
  
```java  
if (responseCount(response) >= 3) return null; // If we've failed 3 times, give up.  
```  
  
This above code relies on this responseCount() method:  
> 上面的代码依赖于这个responseCount()方法：  
  
```java  
private int responseCount(Response response) {  
    int result = 1;  
    while ((response = response.priorResponse()) != null) {  
        result++;  
    }  
    return result;  
}  
```  
  
2017-9-7  
