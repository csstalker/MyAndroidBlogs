| Markdown版本笔记 | 我的GitHub首页 | 我的博客 | 我的微信 | 我的邮箱 |  
| :------------: | :------------: | :------------: | :------------: | :------------: |  
| [MyAndroidBlogs][Markdown] | [baiqiantao][GitHub] | [baiqiantao][博客] | bqt20094 | baiqiantao@sina.com |  
  
[Markdown]:https://github.com/baiqiantao/MyAndroidBlogs  
[GitHub]:https://github.com/baiqiantao  
[博客]:http://www.cnblogs.com/baiqiantao/  
  
七牛云存储 qiniu 域名 回收 文件上传 备份 下载    
***  
目录  
===  

- [背景](#背景)
- [解决方法](#解决方法)
	- [下载 qshell](#下载-qshell)
	- [命令行操作过程](#命令行操作过程)
	- [下载文件时的详细配置](#下载文件时的详细配置)
- [在 Android 中上传文件到七牛](#在-Android-中上传文件到七牛)
	- [配置](#配置)
	- [工具类](#工具类)
	- [使用案例](#使用案例)
  
# 背景  
[七牛云添加自定义域名和域名解析教程](http://boke112.com/3870.html)  
[MPic](http://mpic.lzhaofu.cn/)：一款支持多种上传方式且自动生成MarkDown链接的图床工具  
  
七牛存储空间配置的测试域名30天后就被回收了，导致我之前上传的资源无法访问，并且去控制台发现也完全无法下载、无法备份，因为七牛我主要是用来作为图床的，这么一来，我之前GitHub、博客、markdown笔记里面引用的图片都全部显示不了了，这真是个巨坑呀！  
  
不过好在还有补救措施，我们可以先想办法把文件恢复，然后转移到其他云服务器中。下面就是恢复文件的教程。  
  
导致这种情况的官方说明详见：[测试域名使用规范](https://developer.qiniu.com/fusion/kb/1319/test-domain-access-restriction-rules)  
其中最狠的是这句话：  
> 每个测试域名生命周期为30个自然日，超过30日系统将自动回收，回收即为域名删除。  
  
# 解决方法  
## 下载 qshell  
去[GitHub](https://github.com/qiniu/qshell)下载 [qshell命令行工具](http://devtools.qiniu.com/qshell-v2.2.0.zip)，根据操作系统选择对应的版本。  
  
注意：本工具是一个命令行工具，在Windows下面请先打开命令行终端(CMD)，然后输入工具名称执行，不要双击打开，否则会出现闪退现象。  
  
| 文件名 | 描述 |  
| --- | --- |  
| qshell-windows-x86.exe | Windows 32位系统 |  
| qshell-windows-x64.exe | Windows 64位系统 |  
  
建议将文件名修改为 qshell，以简化后续命令行时的输入。  
  
## 命令行操作过程  
**登录**  
[account 命令简介](https://github.com/qiniu/qshell/blob/master/docs/account.md)  
例如：`qshell account m1** ZM**`  
  
**列举七牛空间里面的所有文件**  
[listbucket 命令简介](https://github.com/qiniu/qshell/blob/master/docs/listbucket.md)  
例如：`qshell listbucket blog-2018 list.txt`  
> 生成的文件格式如下：  
`blog/180927/1G6FDLDF75.png    19963    FlOF-rOkQrjmTOY9KVDF-uUwZ9mZ    15380564227730302    image/png    0    0    `  
  
**获取list.txt结果的第一列**  
在linux环境中可以使用cat命令：`cat list.txt | awk '{print $1}' >list_final.txt`  
也可以通过任意方法处理(Excel、正则表达式、手动写代码...)，处理后的文件格式例如：`blog/180927/1G6FDLDF75.png`  
  
**批量复制七牛空间中的文件到另一个空间**  
[batchcopy 命令简介](https://github.com/qiniu/qshell/blob/master/docs/batchcopy.md)  
例如：`qshell  batchcopy blog-2018 blog-2018-copy list_final.txt`  
  
**从七牛空间下载数据到本地**  
[qdownload 命令简介](https://github.com/qiniu/qshell/blob/master/docs/batchcopy.md)  
例如：`qshell qdownload qshell.conf`  
  
配置文件 qshell.conf 内容如下：  
```json  
{  
    "dest_dir" : "D:\\七牛文件",  
    "bucket" : "blog-2018-copy",  
    "cdn_domain" : "phz2kt37i.bkt.clouddn.com"  
}  
```  
  
至此，全部文件都下载下来了。  
  
## 下载文件时的详细配置  
```json  
{  
    "dest_dir" : "D:\\七牛文件",  
    "bucket" : "blog-2018-copy",  
    "prefix" : "",  
    "suffixes" : "",  
    "cdn_domain" : "phz2kt37i.bkt.clouddn.com",  
    "referer" : "",  
    "log_file" : "download.log",  
    "log_level" : "info",  
    "log_rotate" : 1,  
    "log_stdout" : false  
}  
```  
  
|参数名|描述|可选参数|  
|--------------|---------------|----------------|  
|**dest_dir**|本地数据备份路径，为全路径|N|  
|**bucket**|空间名称|N|  
|prefix|只同步指定前缀的文件，默认为空|Y|  
|suffixes|只同步指定后缀的文件，默认为空|Y|  
|**cdn_domain**|设置下载的CDN域名，默认为空表示从存储源站下载，【该功能默认需要计费】|N|  
|**referer**|如果CDN域名配置了域名白名单防盗链，需要指定一个允许访问的referer地址|N|  
|log_level|下载日志输出级别，可选值为`debug`,`info`,`warn`,`error`,默认`info`|Y|  
|log_file|下载日志的输出文件，如果不指定会输出到qshell工作目录下默认的文件中|Y|  
|log_rotate|下载日志文件的切换周期，单位为天，默认为1天即切换到新的下载日志文件|Y|  
|log_stdout|下载日志是否同时输出一份到标准终端，默认为false|Y|  
  
# 在 Android 中上传文件到七牛  
## 配置  
依赖：  
```groovy  
implementation 'com.squareup.okhttp3:okhttp:3.8.0'  
implementation 'com.squareup.okio:okio:1.13.0'  
implementation 'com.qiniu:qiniu-android-sdk:7.3.12'  
```  
  
权限：  
```xml  
<uses-permission android:name="android.permission.INTERNET"/>  
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"/>  
```  
  
## 工具类  
```java  
public class QiNiuUtils {  
    private static final String ACCESSKEY = "m1**";  
    private static final String SECRETKEY = "ZM**";  
    private static final String BUCKET_NAME = "temp";//存储空间的名字  
    private static final String MAC_NAME = "HmacSHA1";  
    private static final String ENCODING = "UTF-8";  
  
    public static void upLoad(File file, String key, Configuration config, UpCompletionHandler handler) {  
        String token = QiNiuUtils.getToken();  
        new UploadManager(config).put(file, key, token, handler, null);  
    }  
  
    //获取Token  
    private static String getToken() {  
        JSONObject json = new JSONObject();  
        long deadline = System.currentTimeMillis() / 1000 + 3600 * 12;  
        try {  
            json.put("deadline", deadline);  
            json.put("scope", BUCKET_NAME);  
        } catch (JSONException e) {  
            e.printStackTrace();  
        }  
  
        String encodedPutPolicy = UrlSafeBase64.encodeToString(json.toString());  
  
        byte[] sign = null;  
        try {  
            sign = hmacsha1encrypt(encodedPutPolicy, SECRETKEY);  
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
  
        String encodedSign = UrlSafeBase64.encodeToString(sign);  
        return ACCESSKEY + ':' + encodedSign + ':' + encodedPutPolicy;  
    }  
  
    private static byte[] hmacsha1encrypt(String encryptText, String encryptKey) throws Exception {  
        byte[] data = encryptKey.getBytes(ENCODING);  
        SecretKeySpec secretKey = new SecretKeySpec(data, MAC_NAME);  
        Mac mac = Mac.getInstance(MAC_NAME);  
        mac.init(secretKey);  
        byte[] text = encryptText.getBytes(ENCODING);  
        return mac.doFinal(text);  
    }  
}  
```  
  
## 使用案例  
```java  
Configuration config = new Configuration.Builder().build();  
String fileName = file.getName();  
QiNiuUtils.upLoad(file, fileName, config, (key, info, response) -> {  
   //用户自定义的内容上传完成后处理动作必须实现的方法，建议用户自己处理异常。若未处理，抛出的异常被直接丢弃。  
   Log.i("bqt", "文件上传保存名称：" + key); //sms_2018.09.08-16_02_09.txt  
   Log.i("bqt", "上传完成返回日志信息：\n" + new Gson().toJson(info));  
   Log.i("bqt", "上传完成的响应内容：\n" + response);  
});  
```  
  
上传完成返回日志信息：  
```json  
{  
    "duration": 923,  
    "host": "upload.qiniup.com",  
    "id": "1536393734861932",  
    "ip": "111.177.9.136",  
    "path": "/",  
    "port": 80,  
    "reqId": "BwMAAOKFEJb2XVIV",  
    "response": {  
        "nameValuePairs": {  
            "hash": "FhX-pWNc1ThcCCVIryzKZQyRQhsq",  
            "key": "sms_2018.09.08-16_02_09.txt"  
        }  
    },  
    "sent": 422819,  
    "statusCode": 200,  
    "timeStamp": 1536393736,  
    "totalSize": 422098,  
    "upToken": {  
        "accessKey": "m1-qhAIzfk6_rNdWfvf5ngaybQva5CmzHl9-pccg",  
        "returnUrl": "",  
        "token": "..."  
    },  
    "xlog": "...",  
    "xvia": "vdn-hbxy-tel-1-1"  
}  
```  
  
上传完成的响应内容  
```json  
{  
    "hash": "FhX-pWNc1ThcCCVIryzKZQyRQhsq",  
    "key": "sms_2018.09.08-16_02_09.txt"  
}  
```  
  
2018-11-10  
