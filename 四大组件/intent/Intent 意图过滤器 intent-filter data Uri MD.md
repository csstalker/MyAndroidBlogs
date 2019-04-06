| Markdown版本笔记 | 我的GitHub首页 | 我的博客 | 我的微信 | 我的邮箱 |  
| :------------: | :------------: | :------------: | :------------: | :------------: |  
| [MyAndroidBlogs][Markdown] | [baiqiantao][GitHub] | [baiqiantao][博客] | bqt20094 | baiqiantao@sina.com |  
  
[Markdown]:https://github.com/baiqiantao/MyAndroidBlogs  
[GitHub]:https://github.com/baiqiantao  
[博客]:http://www.cnblogs.com/baiqiantao/  
  
Intent 意图过滤器 intent-filter data Uri  
***  
目录  
===  

- [intent-filter 简介](#intent-filter-简介)
	- [使用细节](#使用细节)
	- [intent-filter 中的 data](#intent-filter-中的-data)
		- [data 中的 mimeType](#data-中的-mimeType)
		- [data 中的 Uri](#data-中的-Uri)
	- [intent-filter 总结](#intent-filter-总结)
	- [匹配示例](#匹配示例)
		- [案例1](#案例1)
		- [案例2](#案例2)
		- [案例3](#案例3)
	- [自定义 action 及 data 案例](#自定义-action-及-data-案例)
  
# intent-filter 简介  
如果一个 Intent 请求在一片数据(Uri)上执行一个动作(Action)， Android 如何知道`哪个应用程序的哪个组件`能用来响应这个请求呢？  
Intent Filter 就是用来注册 Activity 、Service 和 Broadcast Receiver 具有能在某种数据上执行某种动作的能力。  
  
使用 intent-filter 指定action、data等属性后，应用程序组件就会告诉 Android ，它们能为请求执行哪些动作、传递的是那种类型的数据的组件提供服务，这些组件包括同一个程序的组件、本地的或第三方的应用程序。  
也就是说，只有某一 Intent 中的 Action 及 Data 和我们在清单文件中为 Activity、Service 或 Broadcast Receiver 指定的 intent-filter 相匹配，我们这些组件才响应此 Intent 。  
  
![mark](http://phz2kt37i.bkt.clouddn.com/blog/181115/54dd5Jc038.png?imageslim)  
intent-filter 标签下仅有三个属性：data 、action 、category ，和 Intent 中的属性相对应，属性的值也和 Intent 中定义的对应常量的值相同，如：  
```java  
public static final String ACTION_VIEW = "android.intent.action.VIEW";    
public static final String ACTION_MAIN = "android.intent.action.MAIN";   
public static final String CATEGORY_DEFAULT = "android.intent.category.DEFAULT";  
```  
  
## 使用细节  
1、一个 Intent 只能指定一个action，但是一个 Activity、Service 或 Broadcast Receiver 可以监听多个 action(即intent-filter中可以设置多个)，这是两个不同的概念！  
  
2、Intent 的 Category 默认值为 CATEGORY_DEFAULT，为防止出bug，建议 intent-filter 中都至少添加一个 category 属性，比如默认创建的 MainActivity 的配置如下：  
```xml  
<intent-filter>  
    <action android:name="android.intent.action.MAIN" />  
    <category android:name="android.intent.category.LAUNCHER" />  
</intent-filter>  
```  
3、可以添加任意个 data、category 标签，由于匹配时是 or 的关系 ，因此，只要其中任意一个与 Intent 匹配，系统就会激活你的 Activity。  
如下为系统浏览器的 intent-filter 配置。  
```xml  
<intent-filter>  
    <action android:name="android.intent.action.VIEW" />  
    <category android:name="android.intent.category.DEFAULT" />  
    <category android:name="android.intent.category.BROWSABLE" />  
    <data android:scheme="http" />  
    <data android:scheme="https" />  
    <data android:scheme="about" />  
    <data android:scheme="javascript" />  
</intent-filter>  
```  
  
## intent-filter 中的 data  
data 属性包括两方面：Uri 和 mimeType ，类似windows下的文件路径和后缀名(PS：我们可以这么不严谨的认为，Windows下文件的后缀名对文件没有实际影响，仅仅是标识文件的类型)。  
![mark](http://phz2kt37i.bkt.clouddn.com/blog/181115/54dd5Jc038.png?imageslim)  
  
和Intent中API的对应关系：  
```java  
Intent(String action, Uri data)//Uri即为通过setData设置的数据  
Intent setData(Uri data)  
Intent setType(String type)  
Intent setDataAndType(Uri data, String type)//Set an explicit MIME data type.  
```  
  
注意：  
- 调用 setData 后会将 mimeType 置为 null， 调用 setType 后会将 data 置为 null，所以若想同时设置 data 与 mimeType ，必须使用 setDataAndType 方法 。  
- 往往出现的空指针异常，可能就是因为上面的原因故而找不到匹配的 Activity 所导致的。  
  
### data 中的 mimeType  
MIME：全称`Multipurpose Internet Mail Extensions`(多功能 Internet 邮件扩充服务)，它是一种`多用途网际邮件扩充协议`，在1992年最早应用于电子邮件系统，但后来也应用到浏览器。MIME类型就是设定具有某种`扩展名`的文件限定使用某种`应用程序`来打开的方式类型，当某种扩展名文件被访问的时候，浏览器会自动使用指定的应用程序来打开。多用于指定一些客户端自定义的扩展名，以及一些媒体类型文件的打开方式。  
  
在Android中通过文件的 MIME 类型来判断有哪些应用程序可以处理这些文件，并使用其中的某一个应用程序处理之（如果有多个可选的应用程序，并且在没有设定哪个是默认的应用程序之前，会让用户指定选择使用哪一个）。  
  
### data 中的 Uri  
Uri由四部分属性组成：`scheme`、`host`、`port`、`path`，其数据模型为：  
![mark](http://phz2kt37i.bkt.clouddn.com/blog/181115/ka4fj17LEI.png?imageslim)  
  
> 注：path 属性可以使用 data 属性中的 android:path、android:pathPrefix 或 pathPattern，  
  
如：对于某个URI：`file://com.android.jony.test:520/mnt/sdcard`  
- scheme --> file  
- host --> com.android.jony.test  
- port --> 520，一般都不写端口号，因为都有默认的端口号  
- path --> mnt/sdcard  
  
其中 host 和 port 为URI的 `authority`，如果没有指定 host，port 将被忽略。  
  
**path、pathPrefix、pathPattern**  
- path 用来匹配【完整】的路径，如：`http://example.com/blog/abc.html`，这里将 path 设置为 `/blog/abc.html` 才能够进行匹配；  
- pathPrefix 用来匹配路径的【开头】部分，拿上面的 Uri 来说，这里将 pathPrefix 设置为 `/blog` 就能进行匹配了；  
- pathPattern 用表达式来匹配整个路径，这里需要说下匹配符号与转义。  
  
**匹配符号**  
- `*`用来匹配 0 次或更多  
- `.`用来匹配任意字符  
  
因此 `.*` 就是用来匹配任意字符0次或更多，如：`.*html` 可以匹配 “abchtml”、“chtml”，“html”，“sdf.html”...  
转义：因为当读取 Xml 的时候，【/】是被当作转义字符的（当它被用作 pathPattern 转义之前），因此这里需要两次转义，读取 Xml 是一次，在 pathPattern 中使用又是一次。  
如：`*` 这个字符就应该写成 `//*`，而 `/` 这个字符就应该写成 `////`  
  
**intent-filter和Intent中Uri进行匹配时的规则**  
- 若intent-filter中只设置了scheme，只会比较Uri的scheme部分  
- 若intent-filter中设置了scheme和authority（host和port），那么只会匹配Uri中的scheme和authority  
- 若intent-filter中设置了scheme、authority和path，那么只会匹配Uri中的scheme、authority、path（path可以使用通配符进行匹配）  
- 若intent-filter中设置了mimeType，则一定会进行数据类型的匹配，即便 mimeType 设置为 "*/*" 也是如此  
  
**Uri 中常见的 scheme 属性**  
注意：data 中属性的是 scheme 不是 schema。也许你记得 `xmlns:Android="http://schemas.android.com/apk/res/android"` 这段声明，你就会想起其中的 schema ，但这里的是 scheme 不是 schema。  
  
> scheme：计划; 体系; 阴谋  
schema：概要; 计划; 图表;  
  
- 【`tel://`】：号码数据格式，后跟电话号码  
- 【`mailto://`】：邮件数据格式，后跟邮件收件人地址  
- 【`smsto://`】：短息数据格式，后跟短信接收号码  
- 【`content://`】：内容数据格式，后跟需要读取的内容  
- 【`file://`】：文件数据格式，后跟文件路径  
- 【`market://search?q=pname:包名`】：市场数据格式，在Google Market里搜索指定包名的应用  
- 【`market://details?id=包名`】：市场数据格式，在Google Market里跳转到指定包名应用的详情页  
- 【`geo://latitude,longitude`】:经纬数据格式，在地图上显示经纬度指定的位置  
- 【`mqqwpa://im/chat?chat_type=wpa&uin=909120849&version=1"`】：和指定QQ聊天  
- 【`http://`】：HTML链接  
  
## intent-filter 总结  
- intent-filter 声明了需要接收怎样的 Intent，当发送的 Intent 和 intent-filter 中定义的相符合，就会启动相应的Activity  
- 当有多个Activity符合发送的Intent时，Android系统会列出所有满足Intent的Activity，用户可以通过选择进行相关的操作  
- 在一个Activity的intent-filter中可以有多个action、多个category、多个data，这样可以有多种组合与Intent进行匹配  
- 注意：如果在一个Activity中有多个Intent进行匹配的时候，建议使用多个intent-filter与Intent进行匹配  
- data属性，这是一个进行反向限制Intent的操作，要求Intent的data必须是intent-filter中声明的数据之一  
  
## 匹配示例  
### 案例1  
目的：匹配某个【站点】的以【.pdf】结尾的 【http】 路径，使得别的程序想要打开网络中某个站点的 pdf 文件时，用户能够选择我们的程序进行查看。  
  
> 注意：如果在 data 标签里增加 `android:host="yoursite.com"`，只会匹配 `http://yoursite.com/xxx/xxx.pdf`，但不会匹配 `http://www.yoursite.com`。如果你也想匹配这个路径的话，你就需要再添加一个 data 标签，除了 `android:host` 改为 `www.yoursite.com` 其他都一样。  
  
整个 intent-filter 设置为：  
```xml  
<intent-filter>  
    <action android:name="android.intent.action.VIEW" />  
    <category android:name="android.intent.category.DEFAULT" />  
    <data  
        android:host="yoursite.com"  
        android:pathPattern=".*//.pdf"  
        android:scheme="http" />  
    <data  
        android:host="www.yoursite.com"  
        android:pathPattern=".*//.pdf"  
        android:scheme="http" />  
</intent-filter>  
```  
这里设置 category 的原因是，创建的 Intent 的实例默认 category 就包含了 `Intent.CATEGORY_DEFAULT` ，google 这样做的原因是为了让这个 Intent 始终有一个 category。  
  
### 案例2  
目的：如果我们做的是一个 IM 应用，或是其他类似于微博之类的应用，如何让别人通过 Intent 进行调用出现在选择框里呢？  
  
我们只需注册 `android.intent.action.SEND` 与 `mimeType` 为 `text/plain` 或 `*/*` 就可以了，整个 intent-filter 设置为：  
```xml  
<intent-filter>    
    <action android:name="android.intent.action.SEND" />  
    <category android:name="android.intent.category.DEFAULT" />  
    <data mimeType="*/*" />  
</intent-filter>    
```  
  
### 案例3  
目的：如果我们做的是一个音乐播放软件，当在文件浏览器中打开某音乐文件的时候，如何使我们的应用能够出现在选择框里？  
  
这类似于文件关联了，其实做起来跟上面一样，也很简单，我们只需注册 `android.intent.action.VIEW` 与 `mimeType` 为 `audio/*` 就可以了，整个 intent-filter 设置为：  
```xml  
<intent-filter>    
     <action android:name="android.intent.action.VIEW" />  
     <category android:name="android.intent.category.DEFAULT" />  
     <data android:mimeType="audio/*" />  
</intent-filter>    
```  
  
## 自定义 action 及 data 案例  
清单文件中配置：  
```xml  
<activity  
    android:name=".MyActionActivity"  
    android:label="@string/title_activity_hide" >  
    <intent-filter>  
        <action android:name="com.bqt.mAction" />  
        <category android:name="android.intent.category.DEFAULT" />  
  
        <data android:mimeType="application/mMimeType" /><!--注意：mimeType 格式不能乱写，否则无法匹配-->  
        <data android:scheme="mScheme" />  
        <data android:host="com.bqt.mHostName" />  
        <data android:port="20094" />  
        <data android:pathPattern=".*//.bqt" />  
        <data android:pathPrefix="/mPathPrefix" /><!--注意：路径应该为【/mPathPrefix】而非【mPathPrefix】，否则匹配失败-->  
    </intent-filter>  
</activity>  
```  
  
激活此Activity的Intent：  
```java  
Intent intent = new Intent("com.bqt.mAction");//指定action，须和清单文件中定义的值一致才可激活该Intent  
intent.addCategory(Intent.CATEGORY_DEFAULT);//若清单文件中定义的值为默认值，这里可不设置  
Uri uri = Uri.parse("mScheme://com.bqt.mHostName:20094/mPathPrefix/anythingBehindIsOk");//拼接完整的data  
intent.setDataAndType(uri, "application/mMimeType");  
intent.putExtra("bqt", getPackageName());  
startActivity(intent);  
```  
  
2018-11-14  
