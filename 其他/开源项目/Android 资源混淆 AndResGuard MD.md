| Markdown版本笔记 | 我的GitHub首页 | 我的博客 | 我的微信 | 我的邮箱 |  
| :------------: | :------------: | :------------: | :------------: | :------------: |  
| [MyAndroidBlogs][Markdown] | [baiqiantao][GitHub] | [baiqiantao][博客] | bqt20094 | baiqiantao@sina.com |  
  
[Markdown]:https://github.com/baiqiantao/MyAndroidBlogs  
[GitHub]:https://github.com/baiqiantao  
[博客]:http://www.cnblogs.com/baiqiantao/  
  
Android 资源混淆 AndResGuard  
***  
目录  
===  

- [资源混淆工具库简介](#资源混淆工具库简介)
- [使用](#使用)
	- [配置 build.gradlle](#配置-buildgradlle)
	- [配置 whiteList 与 mappingFile](#配置-whiteList-与-mappingFile)
	- [启动任务](#启动任务)
	- [生成的文件](#生成的文件)
  
# 资源混淆工具库简介  
[AndResGuard](https://github.com/shwenzhang/AndResGuard)  
  
AndResGuard 是一个帮助你缩小 APK 大小的工具，他的原理类似 Java `Proguard`，但是`只针对资源`。他会`将原本冗长的资源路径变短`，例如将 `res/drawable/activity_advanced_setting_for_test` 变为 `r/d/a`。  
  
AndResGuard`不涉及编译过程`，只需输入一个 apk(无论签名与否，debug版，release版均可，在处理过程中会直接将原签名删除)，可得到一个实现资源混淆后的 apk(若在配置文件中输入签名信息，可自动重签名并对齐，得到可直接发布的 apk)以及对应资源 ID 的 mapping 文件。  
  
底层原理详见 [WeMobileDev公众号文章](http://mp.weixin.qq.com/s?__biz=MzAwNDY1ODY2OQ==&mid=208135658&idx=1&sn=ac9bd6b4927e9e82f9fa14e396183a8f#rd)  
点击查看 [更多细节和命令行使用方法](https://github.com/shwenzhang/AndResGuard/blob/master/doc/how_to_work.zh-cn.md)  
  
已知问题：当时在使用7zip压缩的APK时，调用AssetManager#list(String path)返回结果的首个元素为空字符串. [#162](https://github.com/shwenzhang/AndResGuard/issues/162)  
  
最佳实践  
*   如果不是对 APK size 有极致的需求，请不要把 resource.asrc 添加进 compressFilePattern. ([#84](https://github.com/shwenzhang/AndResGuard/issues/84)  [#233](https://github.com/shwenzhang/AndResGuard/issues/233))  
*   对于发布于 Google Play 的 APP，建议不要使用 7Zip 压缩，因为这个会导致 Google Play 的优化 Patch 算法失效. ([#233](https://github.com/shwenzhang/AndResGuard/issues/233))  
  
因为 andresGuard 只混淆资源，所以可以和阿里百川的 Hotfix 完美集成(2.0以前的hotfix只能热修复代码)。  
  
# 使用  
## 配置 build.gradlle  
在 module(app) 中的 build.gradlle 中添加以下代码：  
```groovy  
apply plugin: 'com.android.application'  
apply plugin: 'AndResGuard' //应用AndResGuard插件  
  
buildscript {  
    repositories {  
        jcenter()  
    }  
    dependencies {  
        classpath 'com.tencent.mm:AndResGuard-gradle-plugin:1.2.13' //添加AndResGuard依赖  
    }  
}  
  
android {  
    signingConfigs { //配置签名信息，注意，要放在buildTypes之前  
        release {  
            storeFile file("../keystore/bea.keystore") //签名文件位置，【.】代表当前目录，【..】代表父目录  
            storePassword "beachinambk"  
            keyAlias "bea.keystore"  
            keyPassword "beachinambk"  
            v2SigningEnabled true  
        }  
  
        debug {  
            storeFile file("../keystore/debug.keystore")  
        }  
    }  
  
    buildTypes {  
        release { //配置构建选项  
            minifyEnabled true  
            proguardFiles getDefaultProguardFile('proguard-android.txt'), 'proguard-rules.pro'  
            shrinkResources true  
            zipAlignEnabled true  
            pseudoLocalesEnabled true  
            signingConfig signingConfigs.release //签名文件  
        }  
    }  
}  
  
//-----------------------------配置 AndResGuard-----------------------------  
andResGuard {  
    mappingFile = null // = file("./resource_mapping.txt") //用于keep住资源的路径的mapping文件所在路径  
    use7zip = true    //启用压缩。为true时，useSign必须为true  
    useSign = true    // 启用签名。为true时，需要配置signConfig  
    keepRoot = false // 为true时，会keep住所有资源的原始路径，只混淆资源的名字  
    whiteList = [ //白名单，支持通配符，【+】代表1个或多个，【?】代表0个或1个，【*】代表0个或多个  
        "R.mipmap.ic_launcher", "R.drawable.icon",// for your icon  
        "R.string.com.crashlytics.*",// for fabric，详见https://docs.fabric.io/android/crashlytics/build-tools.html  
        "R.string.google_app_id",// for google-services  
        "R.id.*",//任意id  
    ]  
    compressFilePattern = [ //需要压缩的文件的匹配规则，一般这里不需要动。支持 ? + * 通配符  
        "*.png",  
        "*.jpg",  
        "*.jpeg",  
        "*.gif",  
        "resources.arsc"  
    ]  
    sevenzip { //配置7Zip，只需设置 artifact 或 path；支持同时设置，但此时以 path 的值为优先  
        artifact = 'com.tencent.mm:SevenZip:1.2.13'   
        //path = "/usr/local/bin/7za"  //path指本地安装的7za(7zip命令行工具)  
    }  
  
    // finalApkBackupPath = "${project.rootDir}/final.apk" //可选，指定生成的apk的保存路径  
    // digestalg = "SHA-256" //可选: 指定v1签名时生成jar文件的摘要算法，默认值为“SHA-1”  
}  
```  
## 配置 whiteList 与 mappingFile  
默认 andResGuard 会混淆所有资源文件，而白名单则允许我们指定不被混淆的资源文件。  
例如友盟、融云在 sdk 里面写死了资源名，所以如果被我们混淆之后就找不到指定资源，则运行时就会崩溃。  
另外，所有使用 getIdentifier 访问的资源也都需要加入白名单。  
```java  
getResources().getIdentifier(“com.test.demo:drawable/icon”,null,null);//包名 : 资源文件夹名 / 资源名  
getResources().getIdentifier(“icon”, “drawable”, “com.test.demo”);//ID名，资源属性，包名  
```  
  
可以在 [white_list.md](https://github.com/shwenzhang/AndResGuard/blob/master/doc/white_list.md) 查看更多第三方 sdk 的白名单配置。  
  
白名单机制只作用于资源的 specsName，不会 keep 住资源的路径。如果想 keep 住资源的路径，可以使用 mappingFile。  
例如我想 keep 住所有 folder 中的 icon，可以在 mappingFile 指向的文件添加：  
```  
res path mapping:  
    res/mipmap-hdpi-v4 -> res/mipmap-hdpi-v4  
    res/mipmap-mdpi-v4 -> res/mipmap-mdpi-v4  
    res/mipmap-xhdpi-v4 -> res/mipmap-xhdpi-v4  
    res/mipmap-xxhdpi-v4 -> res/mipmap-xxhdpi-v4  
    res/mipmap-xxxhdpi-v4 -> res/mipmap-xxxhdpi-v4  
```  
  
## 启动任务  
使用 Android Studio 的同学可以在 andresguard 下找到相关的构建任务，命令行可直接运行./gradlew resguard[BuildType | Flavor]，如：  
```  
gradlew.bat resguardRelsese    //Windows  
./gradlew reguardRelease    //OS X or Linux  
```  
  
这里的任务命令规则和 build 中的 assemble 一致。  
双击或右键 Run... 下图中的选中任务，构建以及混淆就会开始：  
![mark](http://phz2kt37i.bkt.clouddn.com/blog/181125/5ac6LLfgKC.png?imageslim)  
  
完成后会提示你混淆后的 apk 生成目录：  
> Backup Final APk(V2) to ..\Test\app\build\outputs\apk\release\app-release.apk  
<--AndResGuard Done! You can find the output in ***\Test\app\build\outputs\apk\release\AndResGuard_app-release  
  
如果不设置 finalApkBackupPath 则会如上面一样默认覆盖 assemble 输出的apk，如果配置则输出至 finalApkBackupPath 配置的路径。  
例如配置为【finalApkBackupPath = "${project.rootDir}/final.apk"】后的输出为：  
> Backup Final APk(V2) to ..\Test\final.apk  
<--AndResGuard Done! You can find the output in ***\Test\app\build\outputs\apk\release\AndResGuard_app-release  
  
## 生成的文件  
默认会生成4种apk，我们选择签名、压缩、对齐后的 apk 即可，后缀名是`*_7zip_aligned_signed.apk`  
![mark](http://phz2kt37i.bkt.clouddn.com/blog/181125/CcHHB5hFFk.png?imageslim)  
  
混淆前 apk 解压后的资源文件：  
![mark](http://phz2kt37i.bkt.clouddn.com/blog/181125/hlffi30l68.png?imageslim)  
  
混淆后 apk 解压后的资源文件：  
![mark](http://phz2kt37i.bkt.clouddn.com/blog/181125/m8aiigIjij.png?imageslim)  
  
2018-7-11  
