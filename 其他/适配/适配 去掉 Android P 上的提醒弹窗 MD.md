| Markdown版本笔记 | 我的GitHub首页 | 我的博客 | 我的微信 | 我的邮箱 |  
| :------------: | :------------: | :------------: | :------------: | :------------: |  
| [MyAndroidBlogs][Markdown] | [baiqiantao][GitHub] | [baiqiantao][博客] | bqt20094 | baiqiantao@sina.com |  
  
[Markdown]:https://github.com/baiqiantao/MyAndroidBlogs  
[GitHub]:https://github.com/baiqiantao  
[博客]:http://www.cnblogs.com/baiqiantao/  
  
去掉 Android P 上的提醒弹窗 MD  
***  
目录  
===  

- [去掉 Android P 上的提醒弹窗](#去掉-Android-P-上的提醒弹窗)
	- [背景](#背景)
	- [分析](#分析)
	- [解决方案](#解决方案)
  
# 去掉 Android P 上的提醒弹窗  
[减少使用非 SDK 接口，提升系统稳定性](https://mp.weixin.qq.com/s/Wej2CYBQHNXDaf46DseavA)  
  
## 背景  
在MIUI 10升级到 Android P 后，对于`debuggable=true`的app来说，Activity 启动的时候，会在界面上弹出一个`Dialog`进行警告，警告的内容为：  
  
> Detected problems with API compatibility(visit g.co/dev/appcompat for more info  
  
而对于release的app来说，Dialog对应换成Toast进行警告，警告内容一样。  
  
调研了一下，是 `Android P` 后谷歌限制了开发者调用`非官方公开API方法或接口`，也就是说，你用`反射`直接调用源码就会有这样的提示弹窗出现。  
  
非 SDK 接口指的是 Android 系统内部使用、并未提供在 SDK 中的接口，开发者可能通过 Java 反射、JNI 等技术来调用这些接口。但是，这么做是很危险的：非 SDK 接口没有任何公开文档，必须查看源代码才能理解其行为逻辑。  
  
## 分析  
经过实验，发现除了控制台会打印出`警告日志`外，对于`dark greylist`调用，`UI`上还会显示出对应的警告，而对于`light greylist`，则只有控制台有日志进行警告，UI上并不会有警告。  
  
项目中的控制台的日志如下：  
```c  
//这三个是我刚引入的  
Accessing hidden method Landroid/content/pm/PackageParser$Package;-><init>(Ljava/lang/String;)V (light greylist, reflection)  
Accessing hidden method Landroid/app/ActivityThread;->currentActivityThread()Landroid/app/ActivityThread; (light greylist, reflection)  
Accessing hidden field Landroid/app/ActivityThread;->mHiddenApiWarningShown:Z (dark greylist, reflection)  
  
//下面这几个是原先就存在的，但是源码中都没有找到  
//深灰  
Accessing hidden method Landroid/util/MathUtils;->dist(FFFF)F (dark greylist, linking)  
//浅灰  
Accessing hidden method Landroid/app/AppGlobals;->getInitialApplication()Landroid/app/Application; (light greylist, linking)  
Accessing hidden method Landroid/view/View;->computeFitSystemWindows(Landroid/graphics/Rect;Landroid/graphics/Rect;)Z (light greylist, reflection)  
Accessing hidden method Landroid/view/ViewGroup;->makeOptionalFitsSystemWindows()V (light greylist, reflection)  
Accessing hidden method Landroid/graphics/Canvas;->save(I)I (light greylist, linking)  
Accessing hidden method Landroid/os/SystemProperties;-><init>()V (light greylist, reflection)  
//...  
```  
  
通过查看`AOSP Developer Preview 1`的代码 [Activity.java#7073](https://www.colabug.com/goto/aHR0cHM6Ly9hbmRyb2lkLmdvb2dsZXNvdXJjZS5jb20vcGxhdGZvcm0vZnJhbWV3b3Jrcy9iYXNlLysvYW5kcm9pZC1wLXByZXZpZXctMS9jb3JlL2phdmEvYW5kcm9pZC9hcHAvQWN0aXZpdHkuamF2YSM3MDcz) 发现，在`Activity`中`performStart`方法中进行的这个警告，对应的相关代码如下：  
```java  
boolean isAppDebuggable = (mApplication.getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;  
// This property is set for all non-user builds except final release  
boolean isApiWarningEnabled = SystemProperties.getInt("ro.art.hiddenapi.warning", 0) == 1;  
if (isAppDebuggable || isApiWarningEnabled) {  
    if (!mMainThread.mHiddenApiWarningShown && VMRuntime.getRuntime().hasUsedHiddenApi()) {  
        // Only show the warning once per process.  
        mMainThread.mHiddenApiWarningShown = true;  
        String appName = getApplicationInfo().loadLabel(getPackageManager()).toString();  
        String warning = "Detected problems with API compatibilityn" + "(visit g.co/dev/appcompat for more info)";  
        if (isAppDebuggable) {  
            new AlertDialog.Builder(this)  
                .setTitle(appName)  
                .setMessage(warning)  
                .setPositiveButton(android.R.string.ok, null)  
                .setCancelable(false)  
                .show();  
        } else {  
            Toast.makeText(this, appName + "n" + warning, Toast.LENGTH_LONG).show();  
        }  
    }  
}  
```  
  
那么有没有办法将这个讨人厌的警告干掉呢，经过简单的分析，发现并不是完全不可能，只是方法仅对Developer Preview 1有效，不保证后续的预览版一定有效或者正式Release版一定有效。  
  
我们来看一下这个方法。  
  
- `isAppDebuggable`来自构建时设置的`debuggable`是否为true，改变这个值，只能改变警告的方式，debug用Dialog警告，release用Toast警告，因此这个变量不是关键。  
- `isApiWarningEnabled`来自`SystemProperties`中的配置项`ro.art.hiddenapi.warning`，对于SystemProperties的配置项，如果app的uid不在system group中，则调用set方法会出现错误，因此这个值不能进行改变。  
- `VMRuntime.getRuntime().hasUsedHiddenApi()`是一个`native`方法，干预的难度比较大。  
- 所以只有干预`mMainThread.mHiddenApiWarningShown`这个值，只要在调用`performStart`前，`ActivityThread`的`mHiddenApiWarningShown`变量的值为true，产生这个警告的条件就无法满足。  
  
因此问题就变得很简单，只要每次反射调用私有API后让ActivityThread的mHiddenApiWarningShown变为true，这个警告就有可能消失。  
  
## 解决方案  
将下面这个方法在app初始化时候调用一次，这个弹窗就不会出现了。  
  
```java  
private void closeAndroidPDialog() {  
    if (Build.VERSION.SDK_INT >= 28) {  
        try {  
            Class aClass = Class.forName("android.content.pm.PackageParser$Package");  
            Constructor declaredConstructor = aClass.getDeclaredConstructor(String.class);  
            declaredConstructor.setAccessible(true);  
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
        try {  
            Class cls = Class.forName("android.app.ActivityThread");  
            Method declaredMethod = cls.getDeclaredMethod("currentActivityThread");  
            declaredMethod.setAccessible(true);  
            Object activityThread = declaredMethod.invoke(null);  
            Field mHiddenApiWarningShown = cls.getDeclaredField("mHiddenApiWarningShown");  
            mHiddenApiWarningShown.setAccessible(true);  
            mHiddenApiWarningShown.setBoolean(activityThread, true);  
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
    }  
}  
```  
  
2019-1-3  
