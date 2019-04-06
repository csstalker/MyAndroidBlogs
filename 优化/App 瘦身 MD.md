| Markdown版本笔记 | 我的GitHub首页 | 我的博客 | 我的微信 | 我的邮箱 |  
| :------------: | :------------: | :------------: | :------------: | :------------: |  
| [MyAndroidBlogs][Markdown] | [baiqiantao][GitHub] | [baiqiantao][博客] | bqt20094 | baiqiantao@sina.com |  
  
[Markdown]:https://github.com/baiqiantao/MyAndroidBlogs  
[GitHub]:https://github.com/baiqiantao  
[博客]:http://www.cnblogs.com/baiqiantao/  
  
史上最全App瘦身实践  
***  
目录  
===  

- [史上最全App瘦身实践](#史上最全App瘦身实践)
- [分析app组成结构](#分析app组成结构)
	- [assets](#assets)
	- [lib](#lib)
	- [resources.arsc](#resourcesarsc)
	- [META-INF](#META-INF)
	- [res](#res)
	- [dex](#dex)
- [优化assets](#优化assets)
	- [删除无用字体](#删除无用字体)
	- [减少icon-font的使用](#减少icon-font的使用)
	- [动态下载资源](#动态下载资源)
	- [压缩资源文件](#压缩资源文件)
- [优化lib](#优化lib)
	- [配置abiFilters](#配置abiFilters)
	- [分析用户手机的cpu](#分析用户手机的cpu)
	- [避免复制so](#避免复制so)
- [优化resources.arsc](#优化resourcesarsc)
	- [删除无用的资源映射](#删除无用的资源映射)
	- [进行资源名称混淆](#进行资源名称混淆)
- [优化META-INF](#优化META-INF)
	- [MANIFEST.MF](#MANIFESTMF)
	- [CERT.SF](#CERTSF)
	- [CERT.RSA](#CERTRSA)
	- [优化建议](#优化建议)
- [优化res](#优化res)
	- [通过as删除无用资源](#通过as删除无用资源)
	- [打包时剔除无用资源](#打包时剔除无用资源)
	- [删除无用的语言](#删除无用的语言)
	- [控制raw中资源的大小](#控制raw中资源的大小)
	- [统一应用风格，减少shape文件](#统一应用风格，减少shape文件)
	- [使用toolbar，减少menu文件](#使用toolbar，减少menu文件)
	- [限制灵活性，减少layout文件](#限制灵活性，减少layout文件)
		- [复用layout文件](#复用layout文件)
		- [融合layout代码](#融合layout代码)
	- [动态下载图片](#动态下载图片)
	- [分门别类放置不同分辨率的图片](#分门别类放置不同分辨率的图片)
		- [1\. 淘宝](#1\-淘宝)
		- [2\. 微博](#2\-微博)
		- [3\. 微信](#3\-微信)
	- [优化思路](#优化思路)
- [优化图片](#优化图片)
	- [使用VectorDrawable](#使用VectorDrawable)
		- [svg转VectorDrawable](#svg转VectorDrawable)
		- [svg的兼容性](#svg的兼容性)
		- [svg的使用技巧](#svg的使用技巧)
			- [设置恰当的宽高](#设置恰当的宽高)
			- [利用padding和scaleType属性](#利用padding和scaleType属性)
		- [svg的问题和解决方案](#svg的问题和解决方案)
			- [容易写错属性](#容易写错属性)
			- [不兼容selector](#不兼容selector)
			- [不支持自定义控件](#不支持自定义控件)
			- [不兼容第三方库](#不兼容第三方库)
			- [性能问题](#性能问题)
			- [不便于管理](#不便于管理)
			- [不便于预览](#不便于预览)
	- [使用WebP](#使用WebP)
		- [png转webp](#png转webp)
		- [webp的问题](#webp的问题)
			- [兼容性不好](#兼容性不好)
			- [不便于预览](#不便于预览)
	- [复用图片](#复用图片)
		- [复用相同的icon](#复用相同的icon)
		- [使用Tint](#使用Tint)
		- [复用按压效果](#复用按压效果)
		- [通过旋转来复用](#通过旋转来复用)
	- [压缩图片](#压缩图片)
		- [ImageOptim](#ImageOptim)
		- [pngquant](#pngquant)
		- [tinypng](#tinypng)
		- [注意事项](#注意事项)
- [优化dex](#优化dex)
	- [记录方法数和代码行数](#记录方法数和代码行数)
		- [dexcout](#dexcout)
		- [statistic](#statistic)
		- [apk method](#apk-method)
	- [利用Lint分析无用代码](#利用Lint分析无用代码)
	- [通过proguard来删除无用代码](#通过proguard来删除无用代码)
	- [剔除测试代码](#剔除测试代码)
	- [区分debug/rtm/release模式](#区分debugrtmrelease模式)
	- [使用拆分后的support库](#使用拆分后的support库)
	- [减少方法数，不用mulitdex](#减少方法数，不用mulitdex)
	- [使用更小库或合并现有库](#使用更小库或合并现有库)
- [总结](#总结)
  
# 史上最全App瘦身实践  
[原文](https://www.jianshu.com/p/8f14679809b3)  
2016.08.26  
  
业务方和开发都希望app尽量的小，本文会给出多个实用性的技巧来帮助开发者进行app的瘦身工作。瘦身和减负虽好，但需要注意瘦身对于项目可维护性的影响，建议根据自身的项目进行技巧的选取。  
  
# 分析app组成结构  
  
做瘦身之前一定要了解自己app的组成结构，然后要有针对性的进行优化，并且要逐步记录比对，这样才能更好的完成此项工作。目前as的2.2预览版中已经有了apk分析器，功能相当强大，此外你还可以利用[nimbledroid](https://link.jianshu.com?t=https://nimbledroid.com/)来分析apk。nimbledroid是一个强大的工具，推荐一试。  
  
![image_1ar7h642m1v52g0p1r5c4fo1idc4e.png-79.1kB](http://static.zybuluo.com/shark0017/3w5tak2q3cpsp6zs1d9pmpg6/image_1ar7h642m1v52g0p1r5c4fo1idc4e.png)  
  
我们都知道apk是由：  
  
*   asserts  
*   lib  
*   res  
*   dex  
*   META-INF  
*   androidManifest  
  
这几个部分构成的。下面我会利用as的分析工具，以微信、微博、淘宝为例进行讲述。  
  
![image_1ar5097i31eqm12kp1ndlbilos59.png-36.3kB](http://static.zybuluo.com/shark0017/qqd74rg681ti6jjxfj4uza4d/image_1ar5097i31eqm12kp1ndlbilos59.png)  
开始分析后几秒钟就能输出结果，你还可以看到具体类目占的百分比，清晰明了。旁边的“对比”按钮提供了diff的功能，让你可以方便的进行apk优化前后的对比，简直利器。  
  
![image_1ar50k0hl4ek1b8k8fd14bjg9d13.png-63.8kB](http://static.zybuluo.com/shark0017/04z74cumd1mm6al77hwkhfhh/image_1ar50k0hl4ek1b8k8fd14bjg9d13.png)  
  
![image_1ar50n9r11tha15la1gpi1uqs19d81t.png-90.8kB](http://static.zybuluo.com/shark0017/gezrr42hkzqr4o7juf3llmo6/image_1ar50n9r11tha15la1gpi1uqs19d81t.png)  
  
## assets  
  
assets目录可以存放一些配置文件或资源文件，比如webview的本地html，react native的jsbundle等，微信的整个assets占用了13.4M。如果你的应用对本地资源要求很少的话，这个文件应该不会太大。  
  
![image_1ar50vjva119s1abt9it64n1pbfm.png-120.2kB](http://static.zybuluo.com/shark0017/kwg85z1zqd2lud17pep5s2jo/image_1ar50vjva119s1abt9it64n1pbfm.png)  
  
## lib  
  
lib目录下会有各种so文件，分析器会检查出项目自己的so和各种库的so。微博和微信一样只支持了arm一个平台，淘宝支持了arm和x86两个平台。  
  
![image_1ar8g77ucuj7ffo14hs15a1lknm.png-36.4kB](http://static.zybuluo.com/shark0017/fht242wdij34i55h5chavd1q/image_1ar8g77ucuj7ffo14hs15a1lknm.png)  
  
淘宝：  
  
![image_1ar8gsi62hbg15711m1l1l1816oo2a.png-23.6kB](http://static.zybuluo.com/shark0017/fibr7zwwl5ebdgiztm9niw6o/image_1ar8gsi62hbg15711m1l1l1816oo2a.png)  
  
## resources.arsc  
  
这个文件是编译后的二进制资源文件，里面是id-name-value的一个map。因为微信做了资源的混淆，所以这里可以看到资源名称都是不可读的。  
  
![image_1ar50ru80tkj165u1v5g90j1tte9.png-133.3kB](http://static.zybuluo.com/shark0017/vt9sv5ndjcibfnuf8iam73yx/image_1ar50ru80tkj165u1v5g90j1tte9.png)  
  
索性放个微博的图，易于大家理解：  
  
![image_1ar8ggv1q4ac6m100pd4r7uo1g.png-80.1kB](http://static.zybuluo.com/shark0017/zke72sbw0hdn94bicsajjm1g/image_1ar8ggv1q4ac6m100pd4r7uo1g.png)  
  
## META-INF  
  
META-INF目录下存放的是签名信息，用来保证apk包的完整性和系统的安全性，帮助用户避免安装来历不明的盗版apk。  
  
![image_1ar8gijsiudo1h99lc89leiic1t.png-28kB](http://static.zybuluo.com/shark0017/jcfg3jhhb6j7rq3brycguwpr/image_1ar8gijsiudo1h99lc89leiic1t.png)  
  
## res  
  
res目录存放的是资源文件。包括图片、字符串。raw文件夹下面是音频文件，各种xml文件等等。因为微信做了资源混淆，图片名字都不可读了。  
  
![image_1ar5143g519urnco1tbt2f3158k1j.png-35.6kB](http://static.zybuluo.com/shark0017/v5lixno8a4gmpwwja2jxw8e3/image_1ar5143g519urnco1tbt2f3158k1j.png)  
  
微博就没有做资源混淆，所以可读性较好：  
  
![image_1ar8gefrd1anu1q5b5h170d1rie13.png-57.3kB](http://static.zybuluo.com/shark0017/poty8j5x0znycf2nm827wb5g/image_1ar8gefrd1anu1q5b5h170d1rie13.png)  
  
## dex  
  
dex文件是java代码打包后的字节码，一个dex文件最多只支持65536个方法，这也是为什么微信有了三个dex文件的原因。  
  
![image_1ar8g2r9keg21dn1s8grcur7v9.png-5.1kB](http://static.zybuluo.com/shark0017/30aab21h1yljk0vaa6cgrcyh/image_1ar8g2r9keg21dn1s8grcur7v9.png)  
  
因为dex分包是不均匀的，你可以理解为装箱，一个箱子的大小是固定的，但你代码的量是不确定的，微信把前两个箱子装满了，最后还剩了2m多的代码，这些代码也占用了一个箱子，最终产生了上图不均匀的结果。  
  
现在，我们已经知道了apk中各个文件的大小和它们占的比例，下面就可以开始针对性的进行优化了。  
  
# 优化assets  
  
assets中会存放资源文件，这个目录中各个app存放的内容都有所不同，所以优化也比较难。自从引入RN以来，这个目录下还会有jsbundle的信息。如果你有地址选择的功能，这里还会存放地址的映射文件（可参考全名k歌）。对于这块的资源，as是不会进行主动的删减的，所以一切都是需要靠开发者进行手动管理的。  
  
全名k歌中的bundle文件：  
  
![image_1arn9rc731t40m3kgsa1md11ohg3u.png-126.1kB](http://static.zybuluo.com/shark0017/hfo5f89h6te3x8p0hs7tx61x/image_1arn9rc731t40m3kgsa1md11ohg3u.png)  
  
## 删除无用字体  
  
中文字体是相当大的，我一直不建议将字体文件随意丢弃到assets中。有时候一个小功能急着上，开发者为了追求速度，可以先放在这里图省事。但一定要知道这个隐患，并且一定要多和产品核对功能的必要性。此外，对于有些只会用在logo中的字体，我推荐将字体文件进行删减处理。  
 [FontZip](https://link.jianshu.com?t=https://github.com/forJrking/FontZip)是一个字体提取工具，readme中写到：  
  
> 经过测试，已经把项目5MB的艺术字体，按需求提取后，占用只有20KB，并且可正常使用。  
  
## 减少icon-font的使用  
  
icon-font和svg都能完成一些icon的展示，但因为icon-font在assets中难以管理，并且功能和svg有所重叠，所以我建议减少icon-font的使用，利用svg进行代替，毕竟一个很小的icon-font也比svg大呢。我给出一个提供各种格式icon的网站，方便大家进行测试：[https://icomoon.io/app/](https://link.jianshu.com?t=https://icomoon.io/app/)  
  
![image_1ara1tf2ucmeetq14v510a21fob1d.png-16.1kB](http://static.zybuluo.com/shark0017/e3l80m2tcz5pqd3sekysg40z/image_1ara1tf2ucmeetq14v510a21fob1d.png)  
  
![image_1ara1mtufm781bbs1khag1k21ag.png-22.2kB](http://static.zybuluo.com/shark0017/8mf0vvr68lci6l5zheoa3egz/image_1ara1mtufm781bbs1khag1k21ag.png)  
  
*   svg：549字节  
*   png：375字节（单一分辨率）  
*   ion-font：1.1kb  
  
## 动态下载资源  
  
字体、js代码这样的资源能动态下载的就做动态下载，虽然这样会有出错的可能性，复杂度也会提升，但这个对于app的瘦身和用户来说是有长远的好处的。如果你用了RN，你就可以在app运行时动态去拉取最新的代码，将图片和js代码一并下载后解压使用。  
  
## 压缩资源文件  
  
有些资源文件是必须要随着app一并发布的，对于这样的文件，可以采用压缩存储的方式，在需要资源的时候将其解压使用，下面就是解压zip文件的代码：  
  
```  
public static void unzipFile(File zipFile, String destination) throws IOException {  
    FileInputStream fileStream = null;  
    BufferedInputStream bufferedStream = null;  
    ZipInputStream zipStream = null;  
    try {  
        fileStream = new FileInputStream(zipFile);  
        bufferedStream = new BufferedInputStream(fileStream);  
        zipStream = new ZipInputStream(bufferedStream);  
        ZipEntry entry;  
          
        File destinationFolder = new File(destination);  
        if (destinationFolder.exists()) {  
            deleteDirectory(destinationFolder);  
        }  
          
        destinationFolder.mkdirs();  
          
        byte[] buffer = new byte[WRITE_BUFFER_SIZE];  
        while ((entry = zipStream.getNextEntry()) != null) {  
            String fileName = entry.getName();  
            File file = new File(destinationFolder, fileName);  
            if (entry.isDirectory()) {  
                file.mkdirs();  
            } else {  
                File parent = file.getParentFile();  
                if (!parent.exists()) {  
                    parent.mkdirs();  
                }  
                  
                FileOutputStream fout = new FileOutputStream(file);  
                try {  
                    int numBytesRead;  
                    while ((numBytesRead = zipStream.read(buffer)) != -1) {  
                        fout.write(buffer, 0, numBytesRead);  
                    }  
                } finally {  
                    fout.close();  
                }  
            }  
            long time = entry.getTime();  
            if (time > 0) {  
                file.setLastModified(time);  
            }  
        }  
    } finally {  
        try {  
            if (zipStream != null) {  
                zipStream.close();  
            }  
            if (bufferedStream != null) {  
                bufferedStream.close();  
            }  
            if (fileStream != null) {  
                fileStream.close();  
            }  
        } catch (IOException e) {  
            e.printStackTrace();  
        }  
    }  
}  
```  
  
全名k歌中的assets目录下我就发现了大量的zip文件：  
  
![image_1arn9mkkmm061j631b3f1ag7a3i3h.png-26.5kB](http://static.zybuluo.com/shark0017/ri4tnmztsu1djhoi7gurezk2/image_1arn9mkkmm061j631b3f1ag7a3i3h.png)  
  
android上也有一个[7z库](https://link.jianshu.com?t=https://github.com/hzy3774/AndroidUn7zip)帮助我们方便的使用7z。这个库我目前没用到，有需求的同学可以尝试一下。  
  
# 优化lib  
  
## 配置abiFilters  
  
一个硬件设备对应一个架构（mips、arm或者x86），只保留与设备架构相关的库文件夹（主流的架构都是arm的，mips属于小众，默认也是支持arm的so的，但x86的不支持）可以大大降低lib文件夹的大小。配置方式也十分简单，直接[配置abiFilters](https://link.jianshu.com?t=http://stackoverflow.com/questions/30794584/exclude-jnilibs-folder-from-production-apk)即可：  
  
```  
defaultConfig {  
    versionCode 1  
    versionName '1.0.0'  
  
    renderscriptTargetApi 23  
    renderscriptSupportModeEnabled true  
  
    // http://stackoverflow.com/questions/30794584/exclude-jnilibs-folder-from-production-apk  
    ndk {  
        abiFilters "armeabi", "armeabi-v7a" ,"x86"  
    }  
}  
  
```  
  
之后生成的apk中就会排出多余的平台文件了。armeabi就不用说了，这个是必须包含的，v7是一个图形加强版本，x86是英特尔平台的支持库。  
  
[官方例子](https://link.jianshu.com?t=http://tools.android.com/tech-docs/new-build-system/user-guide/apk-splits)：  
  
![image_1arsrvqedh49dju1s7ulkf13du13.png-61.3kB](http://static.zybuluo.com/shark0017/gts6xsmv9zgm2y5cneklyhck/image_1arsrvqedh49dju1s7ulkf13du13.png)  
  
## 分析用户手机的cpu  
  
我们在舍弃so之前需要进行用户cpu型号的统计，这样你才能放心大胆的进行操作。我先是花了几个版本的时间统计了用户的cpu型号，然后排除了没有或少量用户才会用到的so，以达到瘦身的目的。  
  
```  
@NonNull  
public static String getCpuName() {  
    String name = getCpuName1();  
    if (TextUtils.isEmpty(name)) {  
        name = getCpuName2();  
        if (TextUtils.isEmpty(name)) {  
            name = "unknown";  
        }  
    }  
    return name;  
}  
  
private static String getCpuName1() {  
    String[] abiArr;  
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {  
        abiArr = Build.SUPPORTED_ABIS;  
    } else {  
        abiArr = new String[]{Build.CPU_ABI, Build.CPU_ABI2};  
    }  
  
    StringBuilder abiStr = new StringBuilder();  
    for (String abi : abiArr) {  
        abiStr.append(abi);  
        abiStr.append(',');  
    }  
    return abiStr.toString();  
}  
  
private static String getCpuName2() {  
    try {  
        FileReader e = new FileReader("/proc/cpuinfo");  
        BufferedReader br = new BufferedReader(e);  
        String text = br.readLine();  
        String[] array = text.split(":\\s+", 2);  
        e.close();  
        br.close();  
        return array[1];  
    } catch (IOException var4) {  
        var4.printStackTrace();  
        return null;  
    }  
}  
  
```  
  
**注意：**  
  
1.  如果你和我一样用到了`renderscript`那么你必须包含v7，否则会出现模糊异常的问题。  
2.  如果你用了RN，那么对于x86需要谨慎的保留，否则可能会出现用户找不到so而崩溃的情况。毕竟rn是一个全局的东西，稍有不慎就可能会出现开机崩的情况。  
3.  so这个东西还是比较危险的，我们虽然可以通过统计cpu型号来降低风险，但我还是推荐发布app前走一遍大量机型的云测，通过云测平台把风险进一步降低。  
4.  小厂的项目可能会舍弃一些so，但随着公司规模的增大，你未来仍旧要重复考虑这个问题。所以我推荐在崩溃系统中上传用户cpu型号的信息，这样我们就可以在第一时间知道因找不到so引起的崩溃量，至于是否需要增加so就看问题的严重程度了。  
  
## 避免复制so  
  
so有个[常年大坑](https://link.jianshu.com?t=https://zhuanlan.zhihu.com/p/21359984)。在Android 6.0之前，so文件会压缩到apk中。系统在安装应用的时候，会把so文件解压到data分区，这样同一个so文件会有两份存在，一个在apk里，一个在data中。这也导致多占用了一倍的空间，而且会出现各种诡异的错误。这个策略虽然和apk的瘦身无关，但它和app安装在用户手机中的大小有关，因此我们也是需要多多留意的。  
  
> Starting from Android Studio 2.2 Preview 2 and newest build tools, the build process will automatically store native libraries uncompressed and page aligned in the APK.  
  
**在6.0+中**，可以通过如下的方式进行申明：  
  
```  
<application  
   android:extractNativeLibs=”false”  
   ...  
>  
  
```  
  
# 优化resources.arsc  
  
resources.arsc中存放了一个对应关系：  
  
| id | name | default | v11 |  
| --- | --- | --- | --- |  
| 0x7f090002 | PopupAnimation | @ref/0x7f040042, @ref/0x7f040041 | … |  
  
我们在程序运行的时候肯定要经常用到id，因此它在安装之后仍需要被频繁的读取。如果将这个文件进行压缩，在每次读取前系统都必须进行解压的操作，这就会有一些性能和内存的开销，综合考虑下这是得不偿失的。  
  
## 删除无用的资源映射  
  
resources.arsc的正确瘦身方式是删除不必要的`string entry`，你可以借助 [android-arscblamer](https://link.jianshu.com?t=https://github.com/google/android-arscblamer) 来检查出可以优化的部分，比如一些空的引用。  
  
## 进行资源名称混淆  
  
微信团队开源了一个资源混淆工具，[AndResGuard](https://link.jianshu.com?t=https://github.com/shwenzhang/AndResGuard)。它将资源的名称进行了混淆，所以可以用它对resources.arsc进行优化，只是具体优化效果与编码方式、id数量、平均减少命名长度有关。  
  
**表1：**  
  
| id | name | default | v11 |  
| --- | --- | --- | --- |  
| 0x7f090001 | Android | @ref/0x7f040042, @ref/0x7f040041 | … |  
| 0x7f090002 | ios | @ref/0x7f040042, @ref/0x7f040041 | … |  
| 0x7f090003 | Windows Phone | @ref/0x7f040042, @ref/0x7f040041 | … |  
  
**表2：**  
  
| id | name | default | v11 |  
| --- | --- | --- | --- |  
| 0x7f090001 | a | @ref/0x7f040042, @ref/0x7f040041 | … |  
| 0x7f090002 | b | @ref/0x7f040042, @ref/0x7f040041 | … |  
| 0x7f090003 | c | @ref/0x7f040042, @ref/0x7f040041 | … |  
  
我们一眼就可以知道表2肯定比表1存储的字符要小，所以整个文件的大小肯定也要小一些。  
  
**关于AndResGuard**  
  
这个压缩工具其实就是一个task，使用也十分简单，具体的用法请参考[中文文档](https://link.jianshu.com?t=https://github.com/shwenzhang/AndResGuard/blob/master/README.zh-cn.md)。  
  
原理介绍：[安装包立减1M--微信Android资源混淆打包工具](https://link.jianshu.com?t=http://mp.weixin.qq.com/s?__biz=MzAwNDY1ODY2OQ==&mid=208135658&idx=1&sn=ac9bd6b4927e9e82f9fa14e396183a8f#rd)  
  
```  
andResGuard {  
    mappingFile = null  
    use7zip = true  
    useSign = true  
    keepRoot = false  
    whiteList = [  
        //for your icon  
        "R.drawable.icon",  
        //for fabric  
        "R.string.com.crashlytics.*",  
        //for umeng update  
        "R.string.umeng*",  
        "R.string.UM*",  
        "R.layout.umeng*",  
        "R.drawable.umeng*",  
        //umeng share for sina  
        "R.drawable.sina*"  
    ]  
    compressFilePattern = [  
        "*.png",  
        "*.jpg",  
        "*.jpeg",  
        "*.gif",  
        "resources.arsc"  
    ]  
     sevenzip {  
         artifact = 'com.tencent.mm:SevenZip:1.1.9'  
         //path = "/usr/local/bin/7za"  
    }  
}  
  
```  
  
使用这个工具的时候需要注意一些坑，像友盟这种喜欢用反射获取资源的SDK就是一个坑（友盟的SDK就是坑王）！对于app启动图标这样的icon可以不做混淆，推荐将其放入白名单里。  
  
# 优化META-INF  
  
META-INF文件夹中有三个文件，分别是MANIFEST.MF、CERT.SF、CERT.RSA。下面我将会列出简要的分析，如果你希望更详尽的了解原理，可以查看[《Android APK 签名文件MANIFEST.MF、CERT.SF、CERT.RSA分析》](https://link.jianshu.com?t=http://www.blogfshare.com/android-apk-sign.html)。  
  
## MANIFEST.MF  
  
![image_1arn2sdp8ip3cj31j9m1vofcajm.png-81.6kB](http://static.zybuluo.com/shark0017/k2zg1d5l2b0is6e7jwel0jyi/image_1arn2sdp8ip3cj31j9m1vofcajm.png)  
  
每一个资源文件（res开头）下面都有一个SHA1-Digest的值。这个值为该文件SHA-1值进行base64编码后的结果。  
 如果要探究原理，可以看下[SignApk.java](https://link.jianshu.com?t=https://android.googlesource.com/platform/build/+/284e45a/tools/signapk/src/com/android/signapk/SignApk.java)。这个类中有一段main方法：  
  
```  
public static void main(String[] args) {  
    //...  
  
    // MANIFEST.MF  
    Manifest manifest = addDigestsToManifest(inputJar);  
    je = new JarEntry(JarFile.MANIFEST_NAME);  
    je.setTime(timestamp);  
    outputJar.putNextEntry(je);  
    manifest.write(outputJar);  
  
    //...  
}  
  
```  
  
```  
private static void writeSignatureFile(Manifest manifest, OutputStream out)  
        throws IOException, GeneralSecurityException {  
    Manifest sf = new Manifest();  
    Attributes main = sf.getMainAttributes();  
    main.putValue("Signature-Version", "1.0");  
    main.putValue("Created-By", "1.0 (Android SignApk)");  
    BASE64Encoder base64 = new BASE64Encoder();  
    MessageDigest md = MessageDigest.getInstance("SHA1");  
    PrintStream print = new PrintStream(  
            new DigestOutputStream(new ByteArrayOutputStream(), md),  
            true, "UTF-8");  
    // Digest of the entire manifest  
    manifest.write(print);  
    print.flush();  
    main.putValue("SHA1-Digest-Manifest", base64.encode(md.digest()));  
    Map<String, Attributes> entries = manifest.getEntries();  
    for (Map.Entry<String, Attributes> entry : entries.entrySet()) {  
        // Digest of the manifest stanza for this entry.  
        print.print("Name: " + entry.getKey() + "\r\n");  
        for (Map.Entry<Object, Object> att : entry.getValue().entrySet()) {  
            print.print(att.getKey() + ": " + att.getValue() + "\r\n");  
        }  
        print.print("\r\n");  
        print.flush();  
        Attributes sfAttr = new Attributes();  
        sfAttr.putValue("SHA1-Digest", base64.encode(md.digest()));  
        sf.getEntries().put(entry.getKey(), sfAttr);  
    }  
    sf.write(out);  
}  
  
```  
  
通过代码我们可以发现SHA1-Digest-Manifest是MANIFEST.MF文件的SHA1并base64编码的结果。  
  
## CERT.SF  
  
![image_1arn41udjrb91fku103b193a2qe13.png-68.3kB](http://static.zybuluo.com/shark0017/689c68sg6brmi396s37e7iwx/image_1arn41udjrb91fku103b193a2qe13.png)  
  
这里有一项SHA1-Digest-Manifest的值，这个值就是MANIFEST.MF文件的SHA-1并base64编码后的值。后面几项的值是对MANIFEST.MF文件中的每项再次SHA1并base64编码后的值。所以你会看到在manifest.mf中的资源名称在这里也出现了，比如`abc_btn_check_material`这个系统资源文件就出现了两次。  
  
MANIFEST.MF:  
  
![image_1arn47aeq15qc40910rt1u4cvm11g.png-39.3kB](http://static.zybuluo.com/shark0017/bx8twsape9bv0o9z5cck4u9u/image_1arn47aeq15qc40910rt1u4cvm11g.png)  
  
CERT.SF  
  
![image_1arn4bje01q921ih1plv15te1n871t.png-44kB](http://static.zybuluo.com/shark0017/mn2m0rapbch01ouh8h2yd1dt/image_1arn4bje01q921ih1plv15te1n871t.png)  
  
前者是：4XHnecusACTIgtImUjC7bQ9HNM8=，后者是YFDDnTUd6St4932sE/Xk6H0HMoc=。如果你把前一个文件打开在后面加上\n\r，然后进行编码，你就会得到CERT.SF中的值。  
  
```  
 Map<String, Attributes> entries = manifest.getEntries();  
 for (Map.Entry<String, Attributes> entry : entries.entrySet()) {  
     // Digest of the manifest stanza for this entry.  
     print.print("Name: " + entry.getKey() + "\r\n");  
     for (Map.Entry<Object, Object> att : entry.getValue().entrySet()) {  
         print.print(att.getKey() + ": " + att.getValue() + "\r\n");  
     }  
     print.print("\r\n");  
     print.flush();  
  
     Attributes sfAttr = new Attributes();  
     sfAttr.putValue("SHA1-Digest", base64.encode(md.digest()));  
     sf.getEntries().put(entry.getKey(), sfAttr);  
 }  
  
 sf.write(out);  
  
```  
  
## CERT.RSA  
  
CERT.RSA包含了公钥、所采用的加密算法等信息。它对前一步生成的MANIFEST.MF使用了SHA1-RSA算法，用开发者的私钥进行签名，在安装时使用公钥解密它。解密之后，将它与未加密的摘要信息（即，MANIFEST.MF文件）进行对比，如果相符，则表明内容没有被修改。这点和app瘦身就完全无关了，就是android的apk签名机制。这块我平时也没有仔细研究过，就不误人子弟了。具体的签名过程可以参考：[http://blog.csdn.net/asmcvc/article/details/9312123](https://link.jianshu.com?t=http://blog.csdn.net/asmcvc/article/details/9312123)  
  
## 优化建议  
  
通过分析得出，除了CERT.RSA没有压缩机会外，其余的两个文件都可以通过混淆资源名称的方式进行压缩。  
  
![image_1arn6q7ip1p71c1q1kv3s9rigi2a.png-49.6kB](http://static.zybuluo.com/shark0017/ti78rhdmn57txoz7weejv5va/image_1arn6q7ip1p71c1q1kv3s9rigi2a.png)  
  
# 优化res  
  
资源文件的优化一直是我们的重头戏。如果要和它进行对比，上文的META-INF文件的优化简直可以忽略不计。这里的优化会分为两块，一个是文本资源（shape、layout等）优化，还有一个就是图片资源优化。  
  
![image_1arn8pmpn1ofkdnk14o11hfp15es34.png-116.3kB](http://static.zybuluo.com/shark0017/21bzhlrbl7rvsjuibx8tz02x/image_1arn8pmpn1ofkdnk14o11hfp15es34.png)  
  
**说明：**  
 上图中有-v4，-v21这样的文件有些是app开发者自己写的，但大多都是系统在打包的时候自动生成的，所以你只需要考虑自己项目中的drawable-mdpi、drawable-hdpi、drawable-xhdpi、drawable-xxhdpi即可。  
  
## 通过as删除无用资源  
  
在as的任何文件中右击，选择清除无用资源即可删除没有用到的资源文件。  
  
![image_1arni457b1j7o1q3pu6f1gn62ju4b.png-57.2kB](http://static.zybuluo.com/shark0017/iuh0jix7fnzqvlk84m5cce3c/image_1arni457b1j7o1q3pu6f1gn62ju4b.png)  
  
不要勾选清除id！如果清除了id，会影响databinding等库的使用（id绝对占不了多少空间）  
  
![image_1arni765omt7bv01qrm1h2d19us4o.png-14.6kB](http://static.zybuluo.com/shark0017/oei0ww1hlia1k3132ugq9o3n/image_1arni765omt7bv01qrm1h2d19us4o.png)  
  
**Tips：**  
 做此操作之前，请务必产生一次commit，操作完成后一定要通过git看下diff。这样既方便查看被删除的文件，又可以利用git进行误删恢复。  
  
## 打包时剔除无用资源  
  
shrinkResources顾名思义————收缩资源。将它设置为true后，每次打包的时候就会自动排除无用的资源（不仅仅是图片）。有了它的帮忙，即使你忘记手动删除无用的资源文件也没事。  
  
```  
buildTypes {  
    release {  
        zipAlignEnabled true  
        minifyEnabled true  
  
        shrinkResources true // 是否去除无效的资源文件  
  
        proguardFiles getDefaultProguardFile('proguard-android.txt'), 'proguard-rules.txt'  
        signingConfig signingConfigs.release  
    }  
  
    rtm.initWith(buildTypes.release)  
    rtm {}  
  
    debug {  
        multiDexEnabled true  
    }  
}  
  
```  
  
## 删除无用的语言  
  
大部分应用其实并不需要支持几十种语言的，微信也做了根据地区选择性下载语言包的功能。作为国内应用，我们可以只支持中文。推荐在项目的build.gradle中进行如下配置：  
  
```  
android {  
  
    //...  
  
    defaultConfig {  
        resConfigs "zh"  
    }  
}  
```  
  
这样在打包的时候就会排除私有项目、android系统库和第三方库中非中文的资源文件了，效果还是比较显著的。  
  
## 控制raw中资源的大小  
  
*   assets目录允许下面有多级子目录，而raw下不允许存在目录结构  
*   assets中的文件不会产生R文件映射，但raw会  
*   如果你app最低支持的版本不是2.3的话，assets和raw应该都不会对资源文件的大小进行限制  
*   raw文件会生成R文件映射，可以被as的lint分析，而assets则不能  
*   raw缺少子目录的缺点让其无法成为存放大量文件的目录  
  
一般raw文件下会放音频文件。如果raw文件夹下有音频文件，尽量不要使用无损（如：wav）的音频格式，可以考虑同等质量但文件更小的音频格式。  
  
**ogg是一种较适合做音效的音频格式。**当年我初中做游戏的时候，我全都是用的mp3和png，最终游戏达到了2G。在换为ogg和jpg后，游戏缩小到了1G以内（因为游戏中音频和大图较多，所以效果比较夸张）。移动端的音频主要是音效和短小的音频，所以淘宝大量选择了ogg格式，微博的选择格式比较多，有wav、mp3、ogg，我更加推荐淘宝的做法。当然，你仍旧不要忘记opus格式，opus也是一种有损压缩格式，如果感兴趣的话也可以尝试一下。  
  
![image_1arn8i7lq1ub56r41a022mj11n32n.png-38.8kB](http://static.zybuluo.com/shark0017/8cukceuvo8zvtndflmi8yz86/image_1arn8i7lq1ub56r41a022mj11n32n.png)  
  
## 统一应用风格，减少shape文件  
  
一个应用的界面风格是必须要统一的，这个越早做越好，最基本的就是统一颜色和按钮的按压效果。无UI设计和扁平化风格流行后，倒是给应用瘦身带来了极大的的福利。界面变得越朴实，我们可以用shape画的东西就越多。  
  
![image_1arnj7vjtn1mr74eql8781qjg5v.png-44.5kB](http://static.zybuluo.com/shark0017/ebg3iy2g768vel8617p78gbm/image_1arnj7vjtn1mr74eql8781qjg5v.png)  
  
当你的app统一过每种颜色对应的按下颜色后，接下来就需要统一按钮的形状、按钮的圆角角度、有无阴影的样子、阴影投射角度，阴影范围等等，最后还要考虑是否支持水波纹效果。  
  
我简单将按钮分为下列元素：  
  
| 元素 | 属性01 | 属性02 | 属性03 | 属性04 |  
| --- | --- | --- | --- | --- |  
| 形状 | 正方形 | 三角形 | 圆角矩形 | 圆形 |  
| 颜色 | 红 | 黄 | 蓝 | 绿 |  
| 有无阴影 | 有 | 无 |  
| 阴影大小 | 3dp | 5dp |  
| 阴影角度 | 90° | 120° | 180° |  
| 水波纹效果 | 有 | 无 |  
  
上面的各个元素会产生大量的组合，shape和layer-list当然可以实现各种组合，但这样的话光按钮的背景文件就有多个，很不好维护。  
 一般为了开发方便，都会把需要用到的各种selector图片事先定义好，做业务的时候只需要去调用就行。但这大量的selector文件对于业务开发者来说也是有记忆难度的，所以我推荐使用[SelectorInjection](https://link.jianshu.com?t=https://github.com/tianzhijiexian/SelectorInjection)这个库，它可以将上面的每个元素进行各种组合，用最少的资源文件来实现大量的按压效果。  
  
用库虽然好，但库也会带来学习成本，所以引入者可以将上述的组合定义为按钮的一个个的style。因为style本身是支持继承的，对于这样的组合形态来说，继承简直是一大利器。当你的style有良好的命名后，调用者只需要知道引入什么style就行，至于你用了什么属性别人才不希望管呢。如果业务开发中有一些特别特殊的按压状态，没有任何复用的价值，那你就可以利用库提供的丰富属性在layout文件中进行实现，再也不用手忙脚乱的到处定义selector文件了。  
  
![image_1arnkegaj1eug1dvsafj1ao71e7p6p.png-37.7kB](http://static.zybuluo.com/shark0017/kt3477x0hrkzc5ptw4q249ub/image_1arnkegaj1eug1dvsafj1ao71e7p6p.png)  
  
我将不能继承和不灵活的shape变成了一个个单一的属性，通过库将多个属性进行组合，接着利用支持继承的style来将多个属性固定成一个配置文件，最后对外形成强制的规范性约束，至此便完成了减少selector文件的工作。  
  
## 使用toolbar，减少menu文件  
  
menu文件是ActionBar时代的产物，as虽然对于menu的支持做的还不错，但我很难爱上它。menu的设计初衷是解耦和抽象，但因为过度的解耦和定制的不方便，很多项目已经不再使用menu.xml作为actionbar的菜单了。  
 就目前的形势来看，toolbar是android未来的方向。我虽然作为一个对actionbar和actionbar的兼容处理相当了解的人，但我还是不得不承认actionbar的时代过去了。如果你不信，我可以告诉你淘宝的menu文件就3个，微博的menu文件就9个，如果你还是苦苦依恋着actionbar的配置模式，我推荐一个库[AppBar](https://link.jianshu.com?t=https://github.com/tianzhijiexian/AppBar)，它可以让你在用灵活的toolbar的同时也享受到配置menu的便利性。  
  
![image_1ars0g9partf41i1g1lnt8fogm.png-58.4kB](http://static.zybuluo.com/shark0017/9ao5ak2skh4lrtmflcmr7wgb/image_1ars0g9partf41i1g1lnt8fogm.png)  
  
## 限制灵活性，减少layout文件  
  
减少layout文件有两个方法：复用和融合（include）。  
  
### 复用layout文件  
  
把一些页面共用的布局抽出来，这无论是对layout文件的管理还是瘦身都是极为有用的。就比如说任何一个app的list页面是相当多的，从布局层面来说就是一个ListView或者RecyclerView，其背后还可能会有loading的view，空状态的view等等，所以我的建议是建立一个list_layout.xml，其余的list页面可以复用或者include它，这样会从很大程度上减少layout文件的数目。  
  
### 融合layout代码  
  
对于可以被复用的layout我们可以做统一管理，但是对于不会被复用的layout怎么办呢？假设一个页面是由两个区域组合而成的，fragment的做法是一个页面中放两个container，然后再写两个layout，但实际上这两个layout经常是没有任何复用价值的。我希望找到一种方式，在view区块还没有复用需求的时候用一个layout搞定，需要被复用的时候也可以快速、无痛的拆分出来。  
  
**1\. UiBlock**  
  
[UiBlock](https://link.jianshu.com?t=https://github.com/tianzhijiexian/UiBlock/)是一个类似于fragment的解耦库，它可以为同一个layout中不同区域的view进行逻辑解耦（因为layout可预览的特性，ui定位方面不是难题），它能帮我们尽可能少的建立layout文件。  
 如果未来需求发生了变动，layout文件中的一块view需要抽出成独立的layout文件的时候，UiBlock的逻辑代码几乎不用改动，你只需要把抽出的layout文件include进来，然后在`include`标签上定义一个id即可。而这个工作可以通过as的重构功能自动完成，绝不拖泥带水。  
  
![image_1asesb6fq1dfu1df71iu0117ps8c9.png-142.6kB](http://static.zybuluo.com/shark0017/zt22jbgg8en088jy83kufw3z/image_1asesb6fq1dfu1df71iu0117ps8c9.png)  
  
```  
<!-- 使用include -->  
<include  
    android:id="@+id/bottom_ub"  
    layout="@layout/demo_uiblock"  
    android:layout_width="match_parent"  
    android:layout_height="100dp"  
    />  
  
```  
  
**2\. ListHeader**  
  
![image_1arsqihk3bdg1p281ks0phricam.png-149.8kB](http://static.zybuluo.com/shark0017/jvqzie0hjb9qko3v4mih3qaw/image_1arsqihk3bdg1p281ks0phricam.png)  
  
```  
public void addHeaderToListView(ListView listView, View header) {  
    if (header == null) {  
        throw new IllegalArgumentException("Can't add a null header view to ListView");  
    }  
    ViewGroup viewParent = (ViewGroup) header.getParent();  
    viewParent.removeView(header);  
  
    AbsListView.LayoutParams params = new AbsListView.LayoutParams(  
            header.getLayoutParams().width,  
            header.getLayoutParams().height);  
    header.setLayoutParams(params);  
  
    listView.addHeaderView(header); // add  
}  
  
```  
  
我将listView和它的没有复用价值的header放到了同一个layout中，然后在activity中利用上述代码进行了操作，最终完成了用一个layout文件给listView加头的工作。**这段代码我很久没动过了，有利有弊，放在这里我也仅仅是举个例子，希望可以帮助大家扩展下思路。**  
  
## 动态下载图片  
  
做过滤镜和贴纸的同学应该会注意到贴纸、表情这类的东西是相当大的，对于这类的图片资源我强烈建议通过在线商店进行获取。这样既可以让你踏踏实实的卖贴纸，又可以减小应用的大小。这么做虽然有一定的复杂度和出错概率，但投入产出比还是很不错的。  
  
![image_1arnj0n951qd7ua1pl44uk16ak5i.png-103kB](http://static.zybuluo.com/shark0017/gja7oppa650cslgmcch8hcw5/image_1arnj0n951qd7ua1pl44uk16ak5i.png)  
  
## 分门别类放置不同分辨率的图片  
  
这个虽然不算是app大小的优化，但是如果你放错了图片，对于app启动时的[内存大小会有一定的影响](https://link.jianshu.com?t=http://blog.csdn.net/zhaokaiqiang1992/article/details/49787117)：  
  
> 思考一下，如果把一个本来应该放在drawable-xxhdpi里面的图片放在了drawable文件夹中会出现什么问题呢？  
>  在xxhdpi设备上，图片会被放大3倍，图片内存占用就会变为原来的9倍！  
  
国内也有很多人说可以用一套图片来做，不用出多套图，借此来达到app瘦身和给设计减负的目的。谷歌官方是建议为不同分辨率出不同的图片，为此国内也有不少文章讨论过这件事情，[这篇](https://www.jianshu.com/p/ec5a1a30694b)总结的不错推荐一读。  
  
每次说到这个话题的时候总有很多人有不同的看法，况且很多人还不知道.9图也是需要切多份的，所以这里我还是先分析一下大厂的放图策略，最后咱们再讨论下较优的方案。  
  
### 1\. 淘宝  
  
**mdpi：**  
  
![image_1arnmbr6n2sji90tas1s0k1g7176.png-84.4kB](http://static.zybuluo.com/shark0017/vyq2yeq853zqfnrogxyecg1w/image_1arnmbr6n2sji90tas1s0k1g7176.png)  
  
mdpi中存留了一些android原始的icon，这个从命名和前缀就能看出来。通过图片大小分析，这个目录下面都是一些很小的icon，还有一些没有用到的icon（这个launcher图片也很好的说明了淘宝的历史）。  
  
**hdp：**  
  
![image_1arnmlnf92cv1oca1riu196t1j7o7j.png-93.2kB](http://static.zybuluo.com/shark0017/l0wyw42e5tuua7llqcqm9u9y/image_1arnmlnf92cv1oca1riu196t1j7o7j.png)  
  
hdp中分为两部分：表情和其他图片。f+数字的图片都是表情图片，淘宝仅仅有一套表情图片，并且都放在这个目录下。除了少量的图片和mdip的图片一致（比如用户头像的place_holder）外，其余的图片和mdpi的图片完全不同。顺便说一下，此目录下除了表情之外，其余的都是一些小icon，绝对属于尺寸很小的那类。  
  
**xhdpi：**  
  
![image_1arnn1tc416n1s0h58f1bnc1dpi80.png-98.4kB](http://static.zybuluo.com/shark0017/g1b1jackegaodjl0t35qw3bm/image_1arnn1tc416n1s0h58f1bnc1dpi80.png)  
  
xhdpi又和hdpi不同了，它里面有大量的国家icon。除此之外就是一些对清晰度要求较高的icon。  
  
**xxhdpi：**  
  
![image_1arnnj2f0k8v1moek63ggej1s97.png-58.1kB](http://static.zybuluo.com/shark0017/nt595iiljnlkqrkp24ijyqb9/image_1arnnj2f0k8v1moek63ggej1s97.png)  
  
xxhdpi就没什么东西了，几张图而已。  
  
**其他：**  
  
![image_1arnnptoucj31n4h1g4n13ft1b0v9k.png-17kB](http://static.zybuluo.com/shark0017/rfmrx18outml40w2b01d3xg8/image_1arnnptoucj31n4h1g4n13ft1b0v9k.png)  
  
有后缀的文件夹中除了5张左右的淘宝自己的icon外，其余都是系统的图片，均以abc开头。我不清楚淘宝到底有没有使用到这些图片，但我可以肯定地说其中有着冗余图片，或许有着进一步优化的方案。  
  
**总结：**  
 淘宝的放置图片策略是大量的图片在hdpi，xhdpi中，比如表情图在hdpi中，国家图在xhdpi中，大多数图片都仅有一套，少数全局的icon是会有多张的情况（极少，估计只有十几张）。xxhdpi和mhdpi仅仅作为补充，没有太大的作用。  
  
淘宝最令人好奇的点在于它的资源文件很小，但是so文件相当大：  
  
![image_1arnuhm0o1k6813581o0dg5m1jung4.png-47.3kB](http://static.zybuluo.com/shark0017/2ra4dbm7q9aq2wis9esjjga8/image_1arnuhm0o1k6813581o0dg5m1jung4.png)  
  
### 2\. 微博  
  
微博是一个典型的android风格的app，它的drawable全都是有后缀的，完全符合安卓标准的默认打包策略，它还有根据像素密度的图片，甚至有ldpi的目录。  
  
![image_1arnoj1rl1h2m9trqiusqh5jva1.png-41.8kB](http://static.zybuluo.com/shark0017/hcnuj6yhoo9swan84rgk28ml/image_1arnoj1rl1h2m9trqiusqh5jva1.png)  
  
**mdpi-v4**  
  
![image_1arnopum9ogf121a68612ga4peae.png-94.7kB](http://static.zybuluo.com/shark0017/0plkf6yqzdmjg5e2g2advjkj/image_1arnopum9ogf121a68612ga4peae.png)  
  
mdpi中有大量的小icon，里面有个叫做share_wx的，从名字一下子就知道是微信分享的icon，但实际是微博的logo，比较有趣。其余的都是一些边边角角的图标，量不大，所以主力图肯定不在这里。  
  
**hdpi-v4**  
  
![image_1arnouv1n1cpgdqc1evj12gdmdoar.png-205.6kB](http://static.zybuluo.com/shark0017/a5vhswo52v29s44jovgguysr/image_1arnouv1n1cpgdqc1evj12gdmdoar.png)  
  
这是微博图片存放的主要目录，有很多大背景和表情，微博的表情图片和淘宝一样都是在hdpi中的，它以lxh，emoji等前缀开头，用来区分不同风格的表情。  
  
**xhdpi-v4和xxhdpi-v4***  
  
![image_1arnp6ag51jt7cka1b3d1slsal5b8.png-195.9kB](http://static.zybuluo.com/shark0017/7ryha1ohapsv2939zj81z7ev/image_1arnp6ag51jt7cka1b3d1slsal5b8.png)  
  
这里放了一些背景大图，我也发现了大量和hdpi中一样的图片，所以可以大胆的假设微博是做了不同像素的图片的。这也证实了我的想法——微博是很标准的android应用。  
  
**ldpi-v4**  
  
![image_1arnpeog7cik19hsage4u020ibl.png-58.3kB](http://static.zybuluo.com/shark0017/5z4kyyehmggusik68kys16on/image_1arnpeog7cik19hsage4u020ibl.png)  
  
这个目录的确没什么用，微博自身也不会维护这个目录，这全都是第三方库和应用商店给的图片，微博开发者只需要放进来就好。  
  
**sw400dp和sw32dp-400dp**  
  
![image_1arnpip3j1iulg7ecio1gtcs1fc2.png-48.7kB](http://static.zybuluo.com/shark0017/dyggnrbten5hdd5o8ndgp0fd/image_1arnpip3j1iulg7ecio1gtcs1fc2.png)  
  
这些目录放了一些为不同分辨率准备的长得相同的icon，当然还有微博自己的logo。  
  
### 3\. 微信  
  
通过上面的分析，我们是不是可以得出一些经验了呢？  
  
*   大量的图片都在hdpi和xdpi中  
*   表情图片在hdpi中  
*   anim目录中都是xml文件  
*   drawable目录中有大量的xml和少量的png和.9图  
*   layout文件中是全部的xml  
*   raw中放置音频  
*   svg图片在raw、drawable或assets中  
*   最大的文件夹是图片文件  
*   layout文件较小  
*   相同的图片，高分屏的肯定比低分屏的大  
  
ok，现在咱们就可以来看微信的资源了，混淆怕啥，友盟混淆的abcd代码都能看懂，微信的adcd资源也应该不难。  
  
**raw(a9)**  
  
![image_1arnqa3vfd76lrn78n3fsdfcf.png-64.3kB](http://static.zybuluo.com/shark0017/1i8jm5wtqjkeak38vfm3fek3/image_1arnqa3vfd76lrn78n3fsdfcf.png)  
  
这个目录中放置了大量的svg图片和mp3文件，从专业的角度来想，drawable目录下肯定不会放mp3，所以这个肯定是raw文件了。这个目录下有大量的svg，所以可以看出微信已经实现了全svg化，并且已经在线上稳定运行了。这点在微信早期的公众号上也可以得到佐证，详细请看[Android微信上的SVG](https://link.jianshu.com?t=https://mp.weixin.qq.com/s?__biz=MzAwNDY1ODY2OQ==&mid=207863967&idx=1&sn=3d7b07d528f38e9f812e8df7df1e3322)。  
  
文中提到了：  
  
第一步，拿到.svg后缀的资源文件（UI很容导出这种图片），放在raw目录下而不是drawable目录。  
 第二步，把 [R.drawable.xxx](https://link.jianshu.com?t=http://R.drawable.xxx) 换成 [R.raw.xxx](https://link.jianshu.com?t=http://R.raw.xxx)；把 @drawable/xxx 换成 @raw/xxx。  
  
**layout(f)**  
  
![image_1arnqqs2fbl9j932ie1llt1tgacs.png-14kB](http://static.zybuluo.com/shark0017/rmoal3vgs42nbjti0skohevm/image_1arnqqs2fbl9j932ie1llt1tgacs.png)  
  
现在有了两个线索，那么初步估计上面的三个目录肯定是我们常见的目录，否则不会那么大。  
  
![image_1arnr4ghkld6r0r1r9910e81ussd9.png-51.8kB](http://static.zybuluo.com/shark0017/gtydi8spbhd7tp66uw7ln75d/image_1arnr4ghkld6r0r1r9910e81ussd9.png)  
  
打开f后发现这个就是layout文件，所以其余的肯定就是图片文件了。  
  
**hdpi(y)**  
  
![image_1arnr8rn015lj3in76ou621tqadm.png-97.9kB](http://static.zybuluo.com/shark0017/w2yub23w0d3dxv0eky88s0wh/image_1arnr8rn015lj3in76ou621tqadm.png)  
  
y是2.9m，里面有大量的表情，所以我判断它是hdpi，为了更加证实这个猜测，我找到了两张相同的图片。  
  
a2中：  
  
![image_1arnrkkh21ooi1mquuq9sh614hueg.png-101.4kB](http://static.zybuluo.com/shark0017/epjp57g9rmzgaw8qolbfxnzp/image_1arnrkkh21ooi1mquuq9sh614hueg.png)  
  
y中：  
  
![image_1arnrng70t9s7vnpj36alf5het.png-61.4kB](http://static.zybuluo.com/shark0017/wp5irdjcvop64vnsdv03u3n2/image_1arnrng70t9s7vnpj36alf5het.png)  
  
y中图片是11kb，a2中图片是76kb，这明显说明y是hdpi，a2是xhdpi。  
  
**xhdpi(a2)：**  
  
![image_1arnrscp011v6ptk1r5q1tn01mudfa.png-95.8kB](http://static.zybuluo.com/shark0017/aaxorgftvehppsfg0gpjoacu/image_1arnrscp011v6ptk1r5q1tn01mudfa.png)  
  
xhdpi中的图片和hdpi中的图片相同的不多，微信在这里放的是一些大图。  
  
**drawable(k)**  
  
![image_1arns8oe9167th553bb66t857fn.png-72.5kB](http://static.zybuluo.com/shark0017/vyllzgid16shoza18tbgf63h/image_1arns8oe9167th553bb66t857fn.png)  
  
drawable本身没啥可以说的，但是微信的drawable中.9图份额很少，所以我在想是否svg可以在一定的程度上完成一些.9的工作呢？  
  
**总结：**  
  
1.  微信的表情都在hdpi中，仅有一套图片，这点几乎已经成为了标准  
2.  微信已经实现了svg化，svg图片在raw中  
3.  微信倾向于把较大的图片放在xhdpi中，仅出一套图  
4.  微信和淘宝一样都是尽量选择一套图来完成需求  
  
## 优化思路  
  
通过分析得出，传统的出多个分辨率图片的做法在大厂中已经发生了改变，阿里系、腾讯系的产品都采用了一套图走天下的路子。这样的做法还是有利有弊的，权衡之下我给出如下建议：  
  
*   聊天表情就出一套图，放在hdpi中  
*   纯色小icon用svg做  
*   背景等大图，出一套放在xhdpi中  
*   logo等权重较大的图片可针对hdpi，xhdpi做两套图  
*   如果某些图在真机中确实展示异常，那就用多套图  
*   如果遇到奇葩机型，可针对性的补图  
  
成年人不看对错，只看利弊，所以还请大家权衡一二。  
  
# 优化图片  
  
对于图片的优化应该是放在优化res一节中进行讲解的，但是因为图片这块比重太大了，所以我让其独立成为一节。本节主要会从图片格式、复用图片和压缩图片三个方面进行讲解。  
  
## 使用VectorDrawable  
  
想要做好图片的优化工作最重要的一点是知道应该选择什么样的图片格式，对于这点我推荐一个[视频](https://link.jianshu.com?t=http://v.youku.com/v_show/id_XMTU3ODIyNjcxMg==.html?beta&f=27314446)，方便大家进行深入的了解。  
  
![image_1ar7a260ogr2cldradmbl132o20.png-154.3kB](http://static.zybuluo.com/shark0017/1scgi5opkdt5stdrgu8ft04a/image_1ar7a260ogr2cldradmbl132o20.png)  
  
这是谷歌给出的建议，简单来说就是：VD->WebP->Png->JPG  
  
1.  如果是纯色的icon，那么用svg  
2.  如果是两种以上颜色的icon，用webp  
3.  如果webp无法达到效果，选择png  
4.  如果图片没有alpha通道，可以考虑jpg  
  
VD即VectorDrawable，android上的svg实现类。在经历了长达半年的缓慢兼容之路后，现在终于被support库兼容了，[官方文档](https://link.jianshu.com?t=http://android-developers.blogspot.sg/2016/02/android-support-library-232.html)中给出了这样一个例子：  
  
```  
 // Gradle Plugin 2.0+    
 android {    
   defaultConfig {    
     vectorDrawables.useSupportLibrary = true    
    }    
 }    
  
```  
  
```  
 <ImageView    
      android:layout_width="wrap_content"    
      android:layout_height="wrap_content"    
      app:srcCompat="@drawable/ic_add"   
    />    
  
```  
  
配置好后，我们就可以利用强大的svg来替换**纯色icon**了。  
  
### svg转VectorDrawable  
  
先去这里下载svg图片：[https://icomoon.io/app/#/select](https://link.jianshu.com?t=https://icomoon.io/app/#/select)  
  
![image_1as2a4ja5h64os6ujvtvj7gt13.png-39.4kB](http://static.zybuluo.com/shark0017/95wa62ajeumdhqw8bv0fwykb/image_1as2a4ja5h64os6ujvtvj7gt13.png)  
  
然后利用这个[在线工具](https://link.jianshu.com?t=http://inloop.github.io/svg2android/)转换成VectorDrawable。  
  
![image_1as29ve0pl661sn71po24df1cm09.png-60.8kB](http://static.zybuluo.com/shark0017/oj6hrjpfqhl5pwxj5dzfbxnh/image_1as29ve0pl661sn71po24df1cm09.png)  
  
### svg的兼容性  
  
support库的代码质量还是不错的，但是svg毕竟是一个图片格式，所以使用svg前还是需要格外慎重的。我写了一个demo，把用到的所有属性都做了示例，然后利用云测服务进行兼容性测试。  
  
![image_1as22vm0n1avk1bgvukv18561krt1g.png-199.2kB](http://static.zybuluo.com/shark0017/72cus1h8uwzmizy3dso5xp1d/image_1as22vm0n1avk1bgvukv18561krt1g.png)  
  
测试svg也挺简单的，首先看会不会崩溃，然后看各个分辨率、各个api下是否会有显示不正常的情况，如果都ok，那么就可以准备引入到项目里面了。  
 具体的测试代码在[SelectorInjection](https://link.jianshu.com?t=https://github.com/tianzhijiexian/SelectorInjection)，我测试下来100%通过。  
  
### svg的使用技巧  
  
#### 设置恰当的宽高  
  
svg图片是有默认宽高的，设计也会给出一个默认宽高，设置一个合适的默认宽高对以后的图片复用会有很大帮助。  
  
![image_1as915i10in41fqg71il71fifm.png-88.2kB](http://static.zybuluo.com/shark0017/kc1si1tagme6i9faafr9n4nh/image_1as915i10in41fqg71il71fifm.png)  
  
![image_1as912sge1hkm1jkgm411j5favp9.png-96kB](http://static.zybuluo.com/shark0017/y7becuj908eggu4b8ksq9coa/image_1as912sge1hkm1jkgm411j5favp9.png)  
  
TextView中drawableLeft等属性是不能设置图片的宽高的，但ImageView可以。如果你的图片会被复用，建议将图片的宽高设置为TextView中的drawable宽高。  
  
#### 利用padding和scaleType属性  
  
ImageView中的svg默认情况下是会随着控件的大小而改变的，它不会像png那样保持自己的原始大小。我们可以利用这一特性，再配合padding和scaleType属性来完成各种效果。  
  
![image_1as91pho95r229p14nd1siuhpt1g.png-4.7kB](http://static.zybuluo.com/shark0017/hzsq1fmgd3trh3dr6lzachs8/image_1as91pho95r229p14nd1siuhpt1g.png)  
  
1.  图1：60x60，svg自动铺满控件  
2.  图2：30x30，svg被压缩到原始大小以下  
3.  图3：60x60，使用scaleType，让svg保持原始宽度  
4.  图4：60x60，使用padding，对svg进行任意比例的压缩  
  
```  
<ImageView  
    android:layout_width="60dp"  
    android:layout_height="60dp"  
  
    android:tint="@color/blue"  
    app:srcCompat="@drawable/facebook"  
    />  
  
<ImageView  
    android:layout_width="30dp"  
    android:layout_height="30dp"  
  
    android:tint="@color/orange"  
    app:srcCompat="@drawable/facebook"  
    />  
  
<ImageView  
    android:layout_width="60dp"  
    android:layout_height="60dp"  
  
    android:scaleType="centerInside"  
    android:tint="@color/red"  
    app:srcCompat="@drawable/facebook"  
    />  
  
<ImageView  
    android:layout_width="60dp"  
    android:layout_height="60dp"  
  
    android:padding="10dp"  
    android:tint="@color/green"  
    app:srcCompat="@drawable/facebook"  
    />  
  
```  
  
### svg的问题和解决方案  
  
svg有很多好处，但也免不了一些问题，本小节中[写代码的猴子](https://link.jianshu.com?t=http://laobie.github.io/)提出了一些很实用的建议，感谢他的帮助。  
  
#### 容易写错属性  
  
svg的支持是要通过`app:srcCompat`这个属性来做的，如果稍微一不注意写成了src，那么就会出现低版本手机上不兼容的问题。你可以尝试通过配置Lint规则或是利用脚本进行文件的遍历等方式来防止出现因开发写错属性而崩溃的问题。  
  
#### 不兼容selector  
  
将svg放入selector中的时候可能会出现一些问题，stackoverflow上也给出了[解决方案](https://link.jianshu.com?t=http://stackoverflow.com/questions/36741036/android-selector-drawable-with-vectordrawables-srccompat)，就是下面这段代码放在Activity中。  
  
```  
static {  
    AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);  
}  
```  
  
开启这个flag后，你就可以正常的使用Selector这样的DrawableContainers了。  
  
#### 不支持自定义控件  
  
AppCompatActivity会自动将xml文件中的ImageView替换为AppCompatImageView，但是如果用了你的自定义控件，那么这种机制就无效了，所以自定义控件中尽量使用AppCompatImageView来代替ImageView，使用 setImageResource()来设置资源。如果你的自定义控件中需要获得drawable或者是有自定义需求，那么可以参考AppCompatImageView中的svg的helper类来编写。  
  
```  
public class AppCompatImageHelper {  
  
    private final ImageView mView;  
  
    public AppCompatImageHelper(ImageView view) {  
        mView = view;  
    }  
  
    public void loadFromAttributes(AttributeSet attrs, int defStyleAttr) {  
        TintTypedArray a = null;  
        try {  
            Drawable drawable = mView.getDrawable();  
  
            if (drawable == null) {  
                a = TintTypedArray.obtainStyledAttributes(mView.getContext(), attrs,  
                        R.styleable.AppCompatImageView, defStyleAttr, 0);  
  
                // If the view doesn't already have a drawable (from android:src), try loading  
                // it from srcCompat  
                final int id = a.getResourceId(R.styleable.AppCompatImageView_srcCompat, -1);  
                if (id != -1) {  
                    drawable = AppCompatResources.getDrawable(mView.getContext(), id);  
                    if (drawable != null) {  
                        mView.setImageDrawable(drawable);  
                    }  
                }  
            }  
  
            if (drawable != null) {  
                DrawableUtils.fixDrawable(drawable);  
            }  
        } finally {  
            if (a != null) {  
                a.recycle();  
            }  
        }  
    }  
  
    public void setImageResource(int resId) {  
        if (resId != 0) {  
            final Drawable d = AppCompatResources.getDrawable(mView.getContext(), resId);  
            if (d != null) {  
                DrawableUtils.fixDrawable(d);  
            }  
            mView.setImageDrawable(d);  
        } else {  
            mView.setImageDrawable(null);  
        }  
    }  
  
    boolean hasOverlappingRendering() {  
        final Drawable background = mView.getBackground();  
        if (Build.VERSION.SDK_INT >= 21  
                && background instanceof android.graphics.drawable.RippleDrawable) {  
            // RippleDrawable has an issue on L+ when used with an alpha animation.  
            // This workaround should be disabled when the platform bug is fixed. See b/27715789  
            return false;  
        }  
        return true;  
    }  
}  
```  
  
#### 不兼容第三方库  
  
市面上有很多优秀的图片加载库，它们一般都会支持多种图片路径的加载，比如磁盘图片，网络图片，res图片等等，对于svg这样的图片格式，它们是否支持就要大家结合自己的图片框架进行调研了。像glide目前（2016.09.10）就不支持加载本地的svg图片，详见： [Vector drawable can't be used as error drawable](https://link.jianshu.com?t=https://github.com/bumptech/glide/issues/1267) 。  
  
PS：因为第三方库更新十分频繁，建议在用到svg的时候再调研。  
  
#### 性能问题  
  
关于动画和性能方面的问题，[《Android Vector曲折的兼容之路》](https://www.jianshu.com/p/e3614e7abc03)中给出了具体的示例和建议：  
  
> 1.  Bitmap的绘制效率并不一定会比Vector高，它们有一定的平衡点，当Vector比较简单时，其效率是一定比Bitmap高的，所以为了保证Vector的高效率，Vector需要更加简单，PathData更加标准、精简，当Vector图像变得非常复杂时，就需要使用Bitmap来代替了。  
  
1.  Vector适用于icon、Button、ImageView的图标等小的icon，或者是需要的动画效果，由于Bitmap在GPU中有缓存功能，而Vector并没有，所以Vector图像不能做频繁的重绘。  
2.  Vector图像过于复杂时，不仅仅要注意绘制效率，初始化效率也是需要考虑的重要因素。这点可以参考[微信中的svg](https://link.jianshu.com?t=https://mp.weixin.qq.com/s?__biz=MzAwNDY1ODY2OQ==&mid=207863967&idx=1&sn=3d7b07d528f38e9f812e8df7df1e3322)。  
3.  SVG加载速度会快于PNG，但渲染速度会慢于PNG，毕竟PNG有硬件加速，但平均下来，加载速度的提升弥补了绘制的速度缺陷。  
  
#### 不便于管理  
  
svg是没办法在文件目录下进行预览的，其放置的目录也和其他图片不同，如果没有做好管理工作，未来的drawable目录就会变得越发的混乱。其实，对于目录或者包内文件的管理有个很简单的原则：**同目录多类型文件，以前缀区分；不同目录，同类型文件，以意义区分。**  
  
drawable目录下有多种类型的文件，我们利用英文排序的原则将这些文件简单分为svg、.9图、shape、layer-list这几类。  
  
![image_1asbj36rs1k0a13rn1bmb2p01dumm.png-15.9kB](http://static.zybuluo.com/shark0017/n53t5k0kxwhxw2p3kw7denit/image_1asbj36rs1k0a13rn1bmb2p01dumm.png)  
  
通过规范特定的前缀，就可以形成一个便于查找和理解的目录树，以达到分类的目的。  
  
![image_1asbk0j4417s53ed9vphma1cea13.png-81.3kB](http://static.zybuluo.com/shark0017/7lhqxa88ww62hfled7f15nay/image_1asbk0j4417s53ed9vphma1cea13.png)  
  
在特定前缀的规范下，次级分类的命名就可以按照功能或用途来做区分，比如button或share的icon就可以用不同的前缀来标识。强烈建议开发和设计定一个命名标准，这样开发就不用对设计出的图片进行重命名了，而且还可以保证两个部门有一致的认知。  
  
这个仅仅是一个简单的分类方法，在实际中需要灵活使用。如果一些文件都是用于某种特定类型的，那么可以自定义前缀。比如我对于按钮使用的形状就用了btn作为前缀，而忽略了它们本身的文件类型。  
  
![image_1ase39vaiud91foq188ilif1n219.png-13.8kB](http://static.zybuluo.com/shark0017/cfwkuhyq0ulb2ip216l9tvvk/image_1ase39vaiud91foq188ilif1n219.png)  
  
#### 不便于预览  
  
svg是一个特殊格式的文件，可预览性大大低于png等常用的图片格式，但幸好win下可以直接在文件目录下预览svg图像，效果十分不错。  
  
![image_1asbld3ar1ptn1pne1l3hrgl12v51g.png-14.6kB](http://static.zybuluo.com/shark0017/0roook6bw3av3qqnil9nvxm0/image_1asbld3ar1ptn1pne1l3hrgl12v51g.png)  
  
但是vd是一个xml文件，预览器很难识别出这是什么格式，所以对于vd的预览才是一个难点。我之后准备写一个as插件来解决这个问题。  
  
## 使用WebP  
  
[webp](https://link.jianshu.com?t=https://developers.google.com/speed/webp/docs/precompiled)作为一种新的图片格式，从Android4.0+开始原生支持，但是不支持包含透明度，直到4.2.1+才支持显示含透明度的webp，使用的时候要特别注意。  
 webp相比于png最明显的问题是加载稍慢，不过现在的智能设备硬件配置越来越高，这点差异越来越小。腾讯之前有一篇[对于webp的分析文](https://link.jianshu.com?t=http://isux.tencent.com/introduction-of-webp.html)十分不错，如果你准备要用webp了，那么它绝对值得一看。  
  
**注意：**如果你的项目最低支持到4.2.1，那么你可以继续阅读了，如果项目还需要支持到4.0版本，我建议暂时不要上webp，成本太高。  
  
### png转webp  
  
我们可以通过[智图](https://link.jianshu.com?t=http://zhitu.isux.us/)或者[isparta](https://link.jianshu.com?t=http://isparta.github.io/)将其它格式的图片转换成webP格式。  
  
![image_1asbgag1erb11i6b1pqg1gga18is9.png-1045.2kB](http://static.zybuluo.com/shark0017/gr0g6fkmamtoupb10ojw22y9/image_1asbgag1erb11i6b1pqg1gga18is9.png)  
  
### webp的问题  
  
#### 兼容性不好  
  
官方文档中说只有在4.2.1+以上的机型，才能解析无损或者有透明度调整的webp图片，4.0+才开始支持无透明度的webp图片。我通过云测发现，在4.0~4.2.1的系统中，带有透明度的webp图片虽然不会崩溃，但是完全无法显示。  
  
![image_1asbo6vvd1o9roku1keqa9r4om1t.png-33.5kB](http://static.zybuluo.com/shark0017/3mj8m6rpnyr1i912l4jkn4d5/image_1asbo6vvd1o9roku1keqa9r4om1t.png)  
  
[《APK瘦身记，如何实现高达53%的压缩效果》](https://link.jianshu.com?t=http://blog.csdn.net/alimobilesecurity/article/details/51025567)一文中也提到有alpha值的jpg图片，经过webp转换后，无法在4.0，4.1的Android系统上运行的问题，具体原因见[官方文档](https://link.jianshu.com?t=http://developer.android.com/guide/appendix/media-formats.html)：  
  
![image_1asbq6bejqva1mtuvaj3kc1al22n.png-14kB](http://static.zybuluo.com/shark0017/lehs2z4e56scwojrqvc3vqi7/image_1asbq6bejqva1mtuvaj3kc1al22n.png)  
  
除了兼容性问题外，webp在某些机型和rom上可能会出现一些“神奇”的问题。在三星的部分机型上，部分有alpha通道的图中会有一条很明显的黑线（三星的rom对于shape的alpha的支持也有问题，是红线）。在小米2刷成4.xx的手机上，系统未能正确识别xml文件中描述的webp图片，也会导致加载webp失败。  
  
#### 不便于预览  
  
因为webp的图片格式是很难预览的，as也没有办法直接预览webp格式，我一般是通过chrome浏览器打开webp，十分不方便。  
  
![image_1asbq487rk9b1ff514p7nrefe02a.png-115.1kB](http://static.zybuluo.com/shark0017/k5vn0hevgjcjxnk20yl9q32z/image_1asbq487rk9b1ff514p7nrefe02a.png)  
  
我们知道gradle在build时，有一个mergeXXXResource Task,它将项目的各个aar中所有的res资源统一整合到`/build/intermediates/res/flavorName/{buildType}`目录下。  
  
[webpConvertPlugin](https://link.jianshu.com?t=https://github.com/mogujie/WebpConvert_Gradle_Plugin)这个gradle插件可以在mergeXXXResource Task和processXXXResource Task之间插入一个task，这个task会将上述目录下的drawable进行统一处理，将项目目录里的png、jpg图片(不包含.9图片，webp转换后显示效果不佳)批量处理成webp图片，这样可以让我们在日常开发时用png、jpg，正式发包时用webp。  
  
## 复用图片  
  
### 复用相同的icon  
  
我们通过svg可以让一张图片适用于不同大小的容器中，以达到复用的目的。最常见的例子就是“叉”，除非你的x是有多种颜色的，那么这种表示关闭的icon可以复用到很多地方。  
  
![image_1arst9t8p1tq61k8117n31489eg22a.png-12.5kB](http://static.zybuluo.com/shark0017/f1xb2c7dk7260j5rvf4dcjvy/image_1arst9t8p1tq61k8117n31489eg22a.png)  
  
![image_1as1sslhf1r0a10mr18rnccg1m6o9.png-24.5kB](http://static.zybuluo.com/shark0017/zg3zr9seziyxej5kn5m3yg42/image_1as1sslhf1r0a10mr18rnccg1m6o9.png)  
  
上图中我通过组合的方式将长得一样的icon（facebook、renren等）复用到了不同的界面中，不仅实现了效果，可维护性也不错。  
  
### 使用Tint  
  
着色器（tint）是一个强大的工具，我将其和shape、svg等结合后产生了化学反应。TintMode共有6种，分别是:add，multiply，screen，srcatop，srcin（默认），src_over。下图是[一篇文章](https://link.jianshu.com?t=http://blog.qiji.tech/archives/6409)中的总结，说明了其灵活性  
  
![image_1as22gvi1jdc16ji1p67a3dqu613.png-67.8kB](http://static.zybuluo.com/shark0017/m380ukq3ayvahc99alxo9wlo/image_1as22gvi1jdc16ji1p67a3dqu613.png)  
  
一般用默认的模式就可以搞定大多数需求了，使用到的控件主要是TextView和ImageButton。ImageButton官方已经给出了支持方案，TextView因为有四个Drawable，官方的tint属性在低版本又不可用，所以我让SelectorTextView支持了一下。如果你想要了解具体的兼容方法，可以参考[库代码](https://link.jianshu.com?t=https://github.com/tianzhijiexian/SelectorInjection)或[《Drawable 着色的后向兼容方案》](https://link.jianshu.com?t=http://www.race604.com/tint-drawable/)。  
  
**ImageButton**  
  
```  
android:tint="@color/blue"  
```  
  
**SelectorTextView**  
  
```  
app:drawableLeftTint="@color/orange"  
app:drawableRightTint="@color/green"  
app:drawableTopTint="@color/green"  
app:drawableBottomTint="@color/green"  
```  
  
因为我用了SelectorTextView和SelectorImageButton，所以我对于背景的tint没有什么需求，也就没做兼容性测试，有兴趣的同学可以尝试一下。如果你决定要采用tint，一定要通过云测等手段做下兼容性测试，下图是我对于上述属性的测试结果：  
  
![image_1as2382s1sfleismn21q991h8b1t.png-218.6kB](http://static.zybuluo.com/shark0017/cc96thsl19s1ug3j2icxsnwv/image_1as2382s1sfleismn21q991h8b1t.png)  
  
### 复用按压效果  
  
一个应用中的list页面都应该做一定程度的统一，对于有限长度的list，我们可能偏向于用ScrollView做，对于无限长的list用RecyclerView做，但对于它们的按压效果我强烈建议采用同一个样式。  
  
![image_1arsu35djprokc73jpp3c1cj22n.png-75.1kB](http://static.zybuluo.com/shark0017/vtzskoj2bvit6l7paypqcbzp/image_1arsu35djprokc73jpp3c1cj22n.png)  
  
以微信为例，它的所有列表都是白色的item，我的优化思路如下：  
  
1.  列表由LinearLayout、RecyclerView组成  
2.  分割线用统一的shape进行绘制，不用切图  
3.  整个列表背景设置为白色  
4.  item的背景是一个selector文件，正常时颜色是**透明**，按下后出现灰色  
  
### 通过旋转来复用  
  
如果一个icon可以通过另一个icon的旋转变换来得到，那么我们就可以通过如下方法来实现：  
  
```  
<?xml version="1.0" encoding="utf-8"?>  
<rotate xmlns:android="http://schemas.android.com/apk/res/android"  
    android:drawable="@drawable/blue_btn_icon" // 原始icon  
    android:fromDegrees="180" // 旋转角度  
    android:pivotX="50%"  
    android:pivotY="50%"  
    android:toDegrees="180" />  
```  
  
![image_1art1llie1j2j1hac1hmtndbe1g9.png-28.3kB](http://static.zybuluo.com/shark0017/9aflqexzl7zj06lmvpn8hee4/image_1art1llie1j2j1hac1hmtndbe1g9.png)  
  
这种方法虽好，但是不要滥用。需要看代码的人知道这种思路，否则会出现不好维护的情况。当设计真的是认为两个图有如此的关系的时候才可这样实现，万不可耍小聪明。  
  
## 压缩图片  
  
图片的压缩策略是：  
  
1.  优先压大图，后小图  
2.  不压.9图（svg在侠义上不算图）  
3.  对于开屏大图片的压缩需注意力度，要和设计确认后再做  
4.  对于体积特别大(超过50k)的图片资源可以考虑有损压缩  
  
关于如何量化两张图片在视觉上的差别，Google 提供了一个叫[butteraugli](https://link.jianshu.com?t=https://github.com/google/butteraugli)的工具，有兴趣的同学可以尝试一下。  
  
### ImageOptim  
  
mac上超好用的图片压缩工具是[ImageOptim](https://link.jianshu.com?t=https://imageoptim.com/mac)，它集成了很多好用图片压缩库，很多blog中的图片也是用它来压缩的。  
  
![image_1ar7a83kr1lrns861di8q3mm6p2q.png-639.7kB](http://static.zybuluo.com/shark0017/f0zhpdvhkilcon8y3i3ui4qr/image_1ar7a83kr1lrns861di8q3mm6p2q.png)  
  
值得一提的是，借助Zopfli，它可以在不改变png图像质量的情况下使图片大小明显变小。  
  
### pngquant  
  
[pngquant](https://link.jianshu.com?t=https://pngquant.org/)也是一款著名的压缩工具，对于png的疗效还不错，但它不一定就适合app中那种背景透明的小icon，所以对比起tinypng来说，优势不明显。  
  
![image_1art6qlf812hk1c19l0h1te413u6m.png-56.6kB](http://static.zybuluo.com/shark0017/yjoplnyn5u3neysikfhsfkz9/image_1art6qlf812hk1c19l0h1te413u6m.png)  
  
上图的数据来自：[http://www.jianshu.com/p/a721fbaa62ab](https://www.jianshu.com/p/a721fbaa62ab)  
  
### tinypng  
  
[tinypng](https://link.jianshu.com?t=https://tinypng.com/)是一款相当著名的商用压缩工具，tinypng提供了开放接口供开发者开发属于自己的压缩工具（付费服务）。tinypng对于免费用户也算友好，每月可以免费压缩几百张图片。  
  
我用[gradle插件](https://link.jianshu.com?t=https://github.com/mogujie/TinyPIC_Gradle_Plugin)来使用tinypng，更加简单方便。我一般的做法是发版本前才做一次图片压缩，每次debug的时候是直接跳过这个task的，完全不影响日常的debug。  
  
```  
tinyinfo {  
    apiKey = 'xxxxxxxxx'  
    //编译时是否跳过此task  
    skip = true  
    //是否打印日志  
    isShowLog = true  
}  
```  
  
有人说tinypng的缺点是在压缩某些带有过渡效果（带alpha值）的图片时，图片可能会失真，对于这种图片你可以将png图片转换为webP格式。  
  
### 注意事项  
  
aapt默认会在打包时进行图片的压缩工作（无论你知不知道，它一直在默默的工作），如果你已经做了图片压缩了，那么建议手动禁止这个功能，否则“可能会”出现图片二次压缩后反而变大的情况，原因请看：[Smaller PNGs, and Android’s AAPT tool](https://link.jianshu.com?t=https://medium.com/@duhroach/smaller-pngs-and-android-s-aapt-tool-4ce38a24019d#.92wk6ew9t)。  
  
![image_1art7sk821fugmvh172i1tnkhi713.png-404.5kB](http://static.zybuluo.com/shark0017/2vubiu3cdqj3krt25exwmc2x/image_1art7sk821fugmvh172i1tnkhi713.png)  
  
```  
android {    
    defaultConfig {    
        //...  
    }    
  
    aaptOptions {    
        cruncherEnabled = false   
    }    
}   
```  
  
# 优化dex  
  
dex本身的体积还是很可观的，虽说代码这东西不占用多少存储空间，但是微信这样的大厂的dex已经达到了20多M。我大概估计了一下，如果你没有达到方法数上限，那么你的dex的大小大约是10M。纵观应用市场，没有用multiDex的又有几家呢？  
  
## 记录方法数和代码行数  
  
### dexcout  
  
要优化这部分，首先需要对公司的、android库的、第三方库的代码进行深入的了解，我用了[dexcount](https://link.jianshu.com?t=https://github.com/KeepSafe/dexcount-gradle-plugin)来记录项目的方法数：  
  
```  
dexcount {  
    format = "list"  
    includeClasses = false  
    includeFieldCount = true  
    includeTotalMethodCount = false  
    orderByMethodCount = false  
    verbose = false  
    maxTreeDepth = Integer.MAX_VALUE  
    teamCityIntegration = false  
}  
```  
  
![image_1arpdkoh618hc5ss1ud51buhf79ho.png-172.9kB](http://static.zybuluo.com/shark0017/emsk0s42zl1afp1vm1mwej8x/image_1arpdkoh618hc5ss1ud51buhf79ho.png)  
  
通过分析你可以知道代码的具体情况了，比如某个第三方库是否已经不用了、自己项目的哪个包的方法数最多、目前代码情况是否合理等等。  
  
### statistic  
  
我是通过`Statistic`这个as插件来评估项目中开发人员写的代码量的，它生成的报表也不错：  
  
![image_1arpdsqips6jp323969fivcqi5.png-123.4kB](http://static.zybuluo.com/shark0017/nkyu4c6ktyf2l4uqgx1mlv75/image_1arpdsqips6jp323969fivcqi5.png)  
  
![image_1arpduoqa107u8vn8cv1thm1nn0ii.png-145.2kB](http://static.zybuluo.com/shark0017/ntneimo7rj59ct9es6cw7r05/image_1arpduoqa107u8vn8cv1thm1nn0ii.png)  
  
现在我可以知道：  
 哪些类空行数太多，是不是没有按照代码规范来；  
 哪些类的代码量很少，是否有存在的必要；  
 哪些类行数过多，是否没有遵守单一职责原则，是否可以进行进一步的拆分  
  
### apk method  
  
你还可以用[apk-method-count](https://link.jianshu.com?t=http://inloop.github.io/apk-method-count/)这个工具来查看项目中各个包中的方法数，它会生成树形结构的文档，十分直观。  
  
![image_1arpedp5l1vrn191t1j7b1gkm7q2iv.png-258.2kB](http://static.zybuluo.com/shark0017/6ko0im3tastxklxhptg19ou5/image_1arpedp5l1vrn191t1j7b1gkm7q2iv.png)  
  
## 利用Lint分析无用代码  
  
如果你想删掉没有用到的代码，可以借助as中的`Inspect Code`对工程做静态代码检查。  
  
![image_1arpbjnn0gq41604v1p82g1p4dgh.png-52.8kB](http://static.zybuluo.com/shark0017/b68cinhqwjgmf9c12hbocdmz/image_1arpbjnn0gq41604v1p82g1p4dgh.png)  
  
![image_1arpbrhdp2221n1k7ob15ae1ijrgu.png-141.6kB](http://static.zybuluo.com/shark0017/75hw1yn3dejxqcy6revoj8i8/image_1arpbrhdp2221n1k7ob15ae1ijrgu.png)  
  
Lint是一个相当强大的工具，它能做的事情自然不限于检查无用资源和代码，它还能检测丢失的属性、写错的单位（dp/sp）、放错像素目录的图片、会引起内存溢出的代码等等。从eclipse时代发展到现在，lint真的是越来越方便了，我们现在只需要点一点就行。  
  
Lint的强大也会带来相应的缺点，缺点就是生成的信息量过多，不适合快速定位无用的代码。我推荐的流程是到下图中的结果中直接看无用的代码和方法。  
  
![image_1arpccgij2hjtgi1s7nuob8o5hb.png-160.3kB](http://static.zybuluo.com/shark0017/jq072t4ms5cd210g561kjdks/image_1arpccgij2hjtgi1s7nuob8o5hb.png)  
  
**注意：**  
 这种删除无用代码的工作需要反复多次进行（比如一月一次）。当你删除了无用代码后，这些代码中用到的资源也会被标记为无用，这时就可以通过上文提到的`Remove Unused Resources`来删除。  
  
## 通过proguard来删除无用代码  
  
手动删除无用代码的流程太繁琐了，如果是一两次倒还会带来删除代码的爽快感，但如果是专人机械性持续工作，那个人肯定要疯的。为了保证每次打包后的apk都包含尽可能少的无用代码，我们可以在build.gradle中进行如下配置：  
  
```  
android {  
    buildTypes {  
        release {  
            minifyEnabled true // 是否混淆  
        }  
    }  
}  
```  
  
虽然这种方式成果显著，但也需要配合正确的proguard配置才能起作用，推荐看下[读懂 Android 中的代码混淆](https://link.jianshu.com?t=http://droidyue.com/blog/2016/07/10/understanding-android-obfuscated-code-by-proguard/)一文。  
  
这种利用混淆来删除代码的方式是一种保险措施，真正治本的方法还是在开发过程中随手删除无用的代码，毕竟开发者才是最清楚一段代码该不该被删的。我之前就是随手清理了下没用的代码，然后就莫名其妙的不用使用mulitdex了。  
  
## 剔除测试代码  
  
我们在测试的时候可能会随便写点测试方法，比如main方法之类的，并且还会引入一些测试库。对于测试环境的代码gradle提供了很方便的androidTest和test目录来隔离生产环境。  
 对于测试时用到的大量库，可以进行test依赖，这样就可以保证测试代码不会污染线上代码，也可以防止把测试工具、代码等发布到线上等错误（微博就干过这样的事情）。  
  
```  
// Dependencies for local unit tests  
testCompile 'junit:junit:4.12'  
testCompile  'org.hamcrest:hamcrest-junit:2.0.0.0'  
  
// Android Testing Support Library's runner and rules  
androidTestCompile 'com.android.support:support-annotations:24.1.1'  
androidTestCompile 'com.android.support.test:runner:0.5'  
androidTestCompile 'com.android.support.test:rules:0.5'  
  
// Espresso UI Testing  
androidTestCompile('com.android.support.test.espresso:espresso-core:2.2.2', {  
    exclude group: 'com.android.support', module: 'support-annotations'  
})  
```  
  
PS：在layout中利用`tools`也是为了达到上述目的。  
  
## 区分debug/rtm/release模式  
  
debug模式是开发者的调试模式，这个模式下log全开，并且会有一些帮助调试的工具（比如：[leakcanary](https://link.jianshu.com?t=https://github.com/square/leakcanary)，[stetho](https://link.jianshu.com?t=https://github.com/facebook/stetho)），我们可以通过`debugCompile`和`releaseCompile`来做不同的依赖，有时候也会需要no-op（关于no-op的内容可以参考下[开发第三方库最佳实践](https://www.jianshu.com/p/0aacd419cb7e)）。  
  
```  
 dependencies {  
   debugCompile 'com.squareup.leakcanary:leakcanary-android:1.4-beta2'  
   releaseCompile 'com.squareup.leakcanary:leakcanary-android-no-op:1.4-beta2'  
   testCompile 'com.squareup.leakcanary:leakcanary-android-no-op:1.4-beta2'  
 }  
```  
  
debug和release是android本身自带的两种生产环境，在实际中我们可能需要有多个环境，比如提测环境、预发环境等，我以rtm（Release to Manufacturing 或者 Release to Marketing的简称）环境做例子。  
  
首先在目录下创建rtm文件：  
  
![image_1arpi1tfq1dd5pga35j10ntuhjjc.png-12.3kB](http://static.zybuluo.com/shark0017/syf7ft7skavwvwqd75vgnz3a/image_1arpi1tfq1dd5pga35j10ntuhjjc.png)  
  
复刻release的配置：  
  
```  
buildTypes {  
    release {  
        zipAlignEnabled true  
        minifyEnabled true  
        shrinkResources true // 是否去除无效的资源文件  
        proguardFiles getDefaultProguardFile('proguard-android.txt'), 'proguard-rules.txt'  
        signingConfig signingConfigs.release  
    }  
    rtm.initWith(buildTypes.release)  
    rtm {}  
    debug {  
        multiDexEnabled true  
    }  
}  
```  
  
配置rtm依赖：  
  
```  
ext {  
    leakcanaryVersion = '1.3.1'  
}  
  
dependencies {  
    debugCompile "com.squareup.leakcanary:leakcanary-android:$leakcanaryVersion"  
    rtmCompile "com.squareup.leakcanary:leakcanary-android-no-op:$leakcanaryVersion"  
    releaseCompile "com.squareup.leakcanary:leakcanary-android-no-op:$leakcanaryVersion"  
｝  
```  
  
rtm环境自然也有动态替换application文件的能力，我为了方便非开发者区分app类别，我做了启动icon的替换。  
  
```  
<?xml version="1.0" encoding="utf-8"?>  
<manifest package="com.kale.example"  
    xmlns:android="http://schemas.android.com/apk/res/android"  
    xmlns:tools="http://schemas.android.com/tools"  
    >  
  
    <application  
        android:name=".RtmApplication"  
        android:allowBackup="true"  
        android:icon="@drawable/rtm_icon"  
        tools:replace="android:name,android:icon"  
        />  
  
</manifest>  
```  
  
现在我可以将环境真正需要的代码打包，不需要的代码全部剔除，以达到瘦身的目的。  
  
## 使用拆分后的support库  
  
谷歌最近有意[将support-v4库进行拆分](https://link.jianshu.com?t=http://mp.weixin.qq.com/s?__biz=MzAxNjI3MDkzOQ==&mid=2654472617&idx=1&sn=f8a61e2232c40329d83b05e94d5a7159&scene=4#wechat_redirect)，但是无奈v4被引用的地方太多了，但这不失为一个好的开始。目前来看使用拆分后的support库是没有什么优点的，所以我也不建议现在就开始动手，当谷歌和第三方库作者都开始真的往这方面想的时候，你再开始吧。  
  
## 减少方法数，不用mulitdex  
  
mulitdex会进行分包，分包的结果自然比原始的包要大一些些，能不用mulitdex则不用。但如果方法数超了，除了插件化和[RN动态发包](https://link.jianshu.com?t=https://github.com/Microsoft/react-native-code-push)等奇淫巧技外我也没什么好办法了。  
  
## 使用更小库或合并现有库  
  
> 同一功能就用一个库，禁止一个app中有多个网络库、多个图片库的情况出现。如果一个库很大，并且申请了各种权限，那么就去考虑换掉他。  
  
话人人都会说，但如果一个项目是由多个项目成员合作完成的，很难避免重复引用库的问题，同一个功能用不同的库，或者一个库用不同版本的现象比比皆是，这也是很难去解决的。我的解决方案是部门之间多沟通，尽量做base层，base层由少数人进行维护，正如微信在so库层面的做法：  
  
> 1.  C++运行时库统一使用stlport_shared  
>      之前微信中的C++运行库大多使用静态编译方式，使用stlport_shared方式可减小APK包大小，相当于把大家公有的代码提取出来放一份，减少冗余。同时也会节省一点内存，加载so的时候动态库只会加载一次，静态库则随着so的加载被加载多份内存映像。  
  
1.  把公用的C++模块抽成功能库  
     其实与上面的思路是一致的，主要为了减少冗余模块。大家都用到的一些基础功能，应该抽成基础模块。  
  
# 总结  
  
app的瘦身是一个长期并且艰巨的工作，如果是小公司建议一个月做一次。大公司的话一般都会对app的大小进行持续的统计和追踪，有余力的小公司也可以多多借鉴一下。希望大家阅读完本文后可以着手对项目进行优化工作，带来真正的收益。  
  
2019-1-8  
