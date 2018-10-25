| Markdown版本笔记 | 我的GitHub首页 | 我的博客 | 我的微信 | 我的邮箱 |  
| :------------: | :------------: | :------------: | :------------: | :------------: |  
| [MyAndroidBlogs][Markdown] | [baiqiantao][GitHub] | [baiqiantao][博客] | bqt20094 | baiqiantao@sina.com |  
  
[Markdown]:https://github.com/baiqiantao/MyAndroidBlogs  
[GitHub]:https://github.com/baiqiantao  
[博客]:http://www.cnblogs.com/baiqiantao/  
  
FileProvider N 7.0 升级 安装APK 选择文件 拍照 临时权限 MD    
***  
目录  
===  

- [问题](#问题)
- [官方文档的相关描述](#官方文档的相关描述)
- [配置](#配置)
	- [声明 FileProvider](#声明 fileprovider)
	- [配置 resource](#配置 resource)
- [系统提供的各种文件路径](#系统提供的各种文件路径)
- [使用案例](#使用案例)
	- [安装指定路径的apk](#安装指定路径的apk)
	- [拍照并指定保存位置](#拍照并指定保存位置)
  
# 问题  
我们在开发 app 时避免不了需要添加应用内升级功能。当 app 启动时，如果检测到最新版本，将 apk 安装包从服务器下载下来，执行安装。    
安装apk的代码一般写法如下，网上随处可以搜到  
```java  
public static void installApk(Context context, File file) {  
    Intent intent = new Intent(Intent.ACTION_VIEW);  
    Uri data = Uri.fromFile(file); //核心代码  
    intent.setDataAndType(data, "application/vnd.android.package-archive");  
    context.startActivity(intent);  
}  
```  
  
然而，当我们在`Android7.0`手机中执行时，会发现会报如下错误日志：  
```java  
Caused by: android.os.FileUriExposedException: file:///storage/emulated/0/Android/data/net.csdn.blog.ruancoder/cache/test.apk exposed beyond app through Intent.getData()  
  at android.os.StrictMode.onFileUriExposed(StrictMode.java:1799)  
  at android.net.Uri.checkFileUriExposed(Uri.java:2346)  
  at android.content.Intent.prepareToLeaveProcess(Intent.java:8933)  
  at android.content.Intent.prepareToLeaveProcess(Intent.java:8894)  
  at android.app.Instrumentation.execStartActivity(Instrumentation.java:1517)  
  at android.app.Activity.startActivityForResult(Activity.java:4224)  
  at android.support.v4.app.BaseFragmentActivityJB.startActivityForResult(BaseFragmentActivityJB.java:50)  
  at android.support.v4.app.FragmentActivity.startActivityForResult(FragmentActivity.java:79)  
  at android.app.Activity.startActivityForResult(Activity.java:4183)  
  at android.support.v4.app.FragmentActivity.startActivityForResult(FragmentActivity.java:859)  
  at android.app.Activity.startActivity(Activity.java:4507)  
  at android.app.Activity.startActivity(Activity.java:4475)  
```  
  
# 官方文档的相关描述  
[FileUriExposedException官方文档](https://developer.android.google.cn/reference/android/os/FileUriExposedException.html) 的一些描述：     
- 当应用程序将文件以【file://】形式的Uri公开到另一个应用程序时抛出的异常    
- 不鼓励这种曝光方式，因为接收的app可能无法访问你所共享的路径。例如，接收app可能未请求运行时权限`Manifest.permission.READ_EXTERNAL_STORAGE` ，或者平台可能跨用户配置文件边界`[user profile boundaries]`共享Uri。  
- 相反，应用程序应使用【content://】形式的Uris，以便平台可以扩展接收应用程序的临时权限以访问资源。  
- 仅针对 `Build.VERSION_CODES.N` 或更高版本的应用程序抛出此操作。 早期SDK版本的app可以以【file://】形式的Uri共享文件，但强烈建议不要这样做。  
  
[FileProvider 官方文档](https://developer.android.google.cn/reference/android/support/v4/content/FileProvider.html) 的一些描述：     
- FileProvider是`ContentProvider`的一个特殊子类，它通过创建 `content://` Uri 而不是 `file:///` Uri 来促进与应用程序关联的文件的安全共享。  
- content URI 允许您使用临时访问权限来获取读写访问权限。当您创建包含 `content URI` 的Intent时，为了将 `content URI` 发送到客户端app，您还可以调用 `Intent.setFlags()` 来添加权限。只要接收 Activity 的堆栈处于活动状态，客户端应用程序就可以使用这些权限。对于转到Service的Intent，只要Service正在运行，权限就可用。  
- 相比之下，要控制对 `file:/// Uri` 的访问，您必须修改基础文件的文件系统权限。您提供的权限可供任何app使用，并在您更改之前保持有效。这种访问level从根本上说是不安全的。  
- 通过 content URI 提高文件访问安全性使FileProvider成为Android安全基础架构的关键部分。  
  
# 配置  
> 不要再去看垃圾官方文档了，也不要去看网上各种垃圾文章了，也不要通过看源码什么的去研究怎么个性化设置了，MLGB，我在几个月的时间内几次尝试理清怎么配置，结果还是发现了各种各样的bug，真的不要再折腾了，这些个性化的东西不重要，只要按照我下面的配置就行了，保证是最简单稳定的。  
  
## 声明 FileProvider  
在清单文件中声明FileProvider：  
```xml  
<manifest>  
    <application>  
        <provider  
            android:name="android.support.v4.content.FileProvider"  
            android:authorities="${applicationId}.fileprovider"  
            android:exported="false"  
            android:grantUriPermissions="true">  
            <meta-data  
                android:name="android.support.FILE_PROVIDER_PATHS"  
                android:resource="@xml/file_paths" />  
        </provider>  
    </application>  
</manifest>  
```  
  
其中:  
- `android:name` 固定写法  
    > 如果要覆盖FileProvider方法的任何默认行为，可扩展FileProvider类并在这里使用完全限定的类名。  
- `android:authorities` 需自定义，是用来标识该 provider 的唯一标识，建议结合包名来保证 authority 的唯一性  
    > Set the android:authorities attribute to a URI authority based on a domain you control; for example, if you control the domain `mydomain.com` you should use the authority `com.mydomain.fileprovider`  
- `android:exported` 必须设置成 false，否则运行时会报错 `java.lang.SecurityException: Provider must not be exported`  
    > the FileProvider does not need to be public  
- `android:grantUriPermissions` 用来控制是否允许临时授予文件的访问权限，必须设置成 true  
- meta-data 节点  
    - `android:name` 固定写法。  
    - `android:resource` 指定共享文件的路径，此文件放在`res/xml/`下  
  
## 配置 resource  
> 文件内容完全照抄就行了，鳖折腾。     
  
在`res/xml/`下添加`file_paths.xml`配置文件     
```xml  
<?xml version="1.0" encoding="utf-8"?>  
<paths>  
    <files-path  
        name="files"  
        path="/"/>  
    <cache-path  
        name="cache"  
        path="/"/>  
    <external-path  
        name="external"  
        path="/"/>  
    <external-files-path  
        name="external_file_path"  
        path="/"/>  
    <external-cache-path  
        name="external_cache_path"  
        path="/"/>  
    <!--<external-media-path  
        name="external-media-path"  
        path=""/>-->  
</paths>  
```  
  
可配置的元素：    
- `files-path` 对应内部存储目录 `Context.getFilesDir()`  
- `cache-path` 对应内部存储目录 `Context.getCacheDir()`  
- `external-path` 对应 `Environment.getExternalStorageDirectory()`  
- `external-files-path` 对应 `Context.getExternalFilesDir()`  
- `external-cache-path` 对应 `Context.getExternalCacheDir()`  
- `external-media-path` 对应 `Context.getExternalMediaDirs()`  
  
# 系统提供的各种文件路径  
很少会用到的路径：  
```java  
String downloadCache = Environment.getDownloadCacheDirectory().getAbsolutePath(); //【/cache】  
String data = Environment.getDataDirectory().getAbsolutePath(); //【/data】  
String root = Environment.getRootDirectory().getAbsolutePath(); //【/system】  
```  
  
SD卡上的路径  
```java  
String ext = Environment.getExternalStorageDirectory().getAbsolutePath(); //【/storage/emulated/0】  
String extDowmload = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath(); //【/storage/emulated/0/Download】  
String extDcim = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath(); //【/storage/emulated/0/DCIM】  
```  
![](http://pfpk8ixun.bkt.clouddn.com/markdown-img-paste-20181006173003251.png)    
  
`data/包名`下的路径：  
```java  
String filesPath = getFilesDir().getAbsolutePath(); //【/data/user/0/包名/files】  
String cachePath = getCacheDir().getAbsolutePath(); //【/data/user/0/包名/cache】  
String extCachePath = getExternalCacheDir().getAbsolutePath(); //【/storage/emulated/0/Android/data/包名/cache】  
  
String extFileDowmloadPath = getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();//【/storage/emulated/0/Android/data/包名/files/Download】  
String extFilesDCIMPath = getExternalFilesDir(Environment.DIRECTORY_DCIM).getAbsolutePath();//【/storage/emulated/0/Android/data/包名/files/DCIM】  
```  
  
# 使用案例  
## 安装指定路径的apk  
记得要申请权限：  
```xml  
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"/>  
<uses-permission android:name="android.permission.REQUEST_INSTALL_PACKAGES"/>  
```  
  
示例代码：  
```java  
public class MainActivity extends AppCompatActivity {  
    private File apkFile;  
    public static final String FROM_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/temp.apk";  
      
    protected void onCreate(Bundle savedInstanceState) {  
        super.onCreate(savedInstanceState);  
        setContentView(R.layout.activity_main);  
          
        apkFile = new File(FROM_PATH);  
        findViewById(R.id.tv).setOnClickListener(v -> {  
            //在6.0的华为手机上不能安装【getFilesDir()】目录下的安装包，但可以安装【getExternalStorageDirectory()】下的安装包  
            //而在8.0的小米手机上既可以安装【getFilesDir()】目录下的安装包，也可以安装【getExternalStorageDirectory()】下的安装包  
            File fileDir = new File(getFilesDir(), "bqt");  
            if (!fileDir.exists()) fileDir.mkdirs();  
            apkFile = new File(fileDir, "temp2.apk");  
            copyFile(new File(FROM_PATH), apkFile);  
        });  
        findViewById(R.id.tv2).setOnClickListener(v -> installApk(this, apkFile));  
    }  
      
    public static void copyFile(File from, File to) {  
        try {  
            FileInputStream fis = new FileInputStream(from);  
            FileOutputStream fos = new FileOutputStream(to);  
            byte[] buf = new byte[1024];  
            int len;  
            while ((len = fis.read(buf)) != -1) {  
                fos.write(buf, 0, len);  
            }  
            fis.close();  
            fos.close();  
        } catch (IOException e) {  
            e.printStackTrace();  
        }  
    }  
      
    private static void installApk(Context context, File file) {  
        Intent intent = new Intent(Intent.ACTION_VIEW);  
        Uri uri;  
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {  
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);  
            uri = FileProvider.getUriForFile(context, context.getPackageName() + ".fileprovider", file);  
            //【content://{$authority}/external/temp.apk】或【content://{$authority}/files/bqt/temp2.apk】  
        } else {  
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);//【file:///storage/emulated/0/temp.apk】  
            uri = Uri.fromFile(file);  
        }  
        Log.i("bqt", "【Uri】" + uri);  
        intent.setDataAndType(uri, "application/vnd.android.package-archive");  
        context.startActivity(intent);  
    }  
}  
```  
  
## 拍照并指定保存位置  
记得要申请权限：  
```xml  
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"/>  
<uses-permission android:name="android.permission.CAMERA"/>  
```  
  
示例代码：  
```java  
public class MainActivity extends Activity {  
      
    private int REQUEST_CODE_CAMERA = 10086;  
    private File tempFile;  
      
    @Override  
    protected void onCreate(Bundle savedInstanceState) {  
        super.onCreate(savedInstanceState);  
        setContentView(R.layout.activity_main);  
        findViewById(R.id.tv).setOnClickListener(v -> {  
            //TMLGB，在6.0的华为手机上，使用 getFilesDir() 铁定失败  
            //在8.0的小米6上，使用Environment.getExternalStorageDirectory()也同样失败  
            File fileDir = Build.VERSION.SDK_INT >= Build.VERSION_CODES.N ? getFilesDir() : Environment.getExternalStorageDirectory();  
            fileDir = new File(fileDir, "bqt");  
            if (!fileDir.exists()) fileDir.mkdirs();  
            tempFile = new File(fileDir, "temp");  
            showCamera(this, tempFile, REQUEST_CODE_CAMERA);  
        });  
    }  
      
    @Override  
    public void onActivityResult(int requestCode, int resultCode, Intent data) {  
        super.onActivityResult(requestCode, resultCode, data);  
        if (requestCode == REQUEST_CODE_CAMERA && resultCode == Activity.RESULT_OK) {  
            Log.i("【bqt", tempFile.getAbsolutePath());//【data/user/0/包名/files/bqt/temp】或【/storage/emulated/0/bqt/temp】  
        }  
    }  
      
    public static void showCamera(Activity activity, File file, int requestCode) {  
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);  
        Uri uri;  
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {  
            String authority = activity.getPackageName() + ".fileprovider"; //【清单文件中provider的authorities属性的值】  
            uri = FileProvider.getUriForFile(activity, authority, file);  
        } else {  
            uri = Uri.fromFile(file);  
        }  
        Log.i("bqt", "【uri】" + uri);//【content://{$authority}/files/bqt/temp】或【file:///storage/emulated/0/bqt/temp】  
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);  
        activity.startActivityForResult(intent, requestCode);  
    }  
}  
```  
  
2018-8-28  
