| Markdown版本笔记 | 我的GitHub首页 | 我的博客 | 我的微信 | 我的邮箱 |  
| :------------: | :------------: | :------------: | :------------: | :------------: |  
| [MyAndroidBlogs][Markdown] | [baiqiantao][GitHub] | [baiqiantao][博客] | bqt20094 | baiqiantao@sina.com |  
  
[Markdown]:https://github.com/baiqiantao/MyAndroidBlogs  
[GitHub]:https://github.com/baiqiantao  
[博客]:http://www.cnblogs.com/baiqiantao/  
  
发布库到仓库 maven jcenter JitPack MD  
***  
目录  
===  

- [通过 AS 创建 aar 的步骤](#通过-as-创建-aar-的步骤)
- [使用 mavenDeployer 发布到 GitHub 仓库【推荐】](#使用-mavendeployer-发布到-github-仓库【推荐】)
	- [创建项目](#创建项目)
	- [配置项目](#配置项目)
	- [构建并上传](#构建并上传)
	- [上传到 GitHub](#上传到-github)
	- [使用此库](#使用此库)
- [使用 JitPack 发布到自定义 maven 仓库【推荐】](#使用-jitpack-发布到自定义-maven-仓库【推荐】)
	- [创建及配置项目](#创建及配置项目)
	- [创建 GitHub release 版本](#创建-github-release-版本)
	- [提交 release 版本到 JitPack](#提交-release-版本到-jitpack)
	- [使用我们创建好的仓库](#使用我们创建好的仓库)
	- [提示我版本过低解决方案](#提示我版本过低解决方案)
- [使用 bintray-release 发布到 Jcenter 仓库【问题多】](#使用-bintray-release-发布到-jcenter-仓库【问题多】)
	- [注册 [bintray](https://bintray.com/signup/oss) 账号](#注册-bintrayhttpsbintraycomsignuposs-账号)
	- [创建 maven 仓库](#创建-maven-仓库)
	- [项目配置](#项目配置)
		- [project 的配置](#project-的配置)
		- [library 的配置](#library-的配置)
		- [publish 闭包的属性](#publish-闭包的属性)
	- [执行 gradlew 命令进行上传](#执行-gradlew-命令进行上传)
	- [关联到 Jcenter 并等待审核](#关联到-jcenter-并等待审核)
- [使用 gradle-bintray-plugin 发布到 Jcenter 仓库【麻烦】](#使用-gradle-bintray-plugin-发布到-jcenter-仓库【麻烦】)
	- [注册账号](#注册账号)
	- [project 的配置](#project-的配置)
	- [library 的配置](#library-的配置)
		- [定义 pom 并打包 aar](#定义-pom-并打包-aar)
		- [打包 javadocjar 和 sourcejar](#打包-javadocjar-和-sourcejar)
		- [上传到 Jcenter 仓库](#上传到-jcenter-仓库)
	- [执行 gradle 任务](#执行-gradle-任务)
	- [请求审核](#请求审核)
	- [第三方引用](#第三方引用)
  
# 通过 AS 创建 aar 的步骤  
`aar`文件是 Google 为 Android 开发所设计的一种`library`格式，全名为`Android Archive Library`，与`Java Jar Library`不同的是，aar 除了 java code 之外还包含资源文件。   
  
1、创建一个新的普通 Android 工程(New Project 而不是 New Module)，修改`module`层的`build.gradle`文件，删除 `applicationId`，将`apply plugin: 'com.android.application'` 改成 `apply plugin: 'com.android.library'`，然后同步  
  
> 如果嫌上述操作太麻烦，可以这么做：创建一个普通的 Android 工程，删除 app module，然后 new 一个 library module  
  
2、执行 gradle 命令`gradle assembleRelease`，或者点击菜单 Build > `Make Project` ，执行完以后可以看到 `project-name/module-name/build/outputs/aar/` 文件夹下生成了`.aar`文件  
![](index_files/84daca45-b68d-47d9-bf8e-621188eabb52.png)  
  
3、此`.aar`文件除了包含 class 文件外，还包含各种资源文件，例如我们解压后发现其包含如下内容：  
![](index_files/ebd503eb-c93f-4d4e-a1d0-c9718f825e7d.png)  
  
# 使用 mavenDeployer 发布到 GitHub 仓库【推荐】  
[参考1](https://blog.csdn.net/lindroid/article/details/80271502)  
[参考2](https://www.jianshu.com/p/ca53952f4212)  
[参考3](https://blog.csdn.net/u010818425/article/details/52441711)  
  
Maven作为目前Java界最好的Dependency管理系统，把jar包托管到Maven中央库，然后通过Maven Dependency使用是目前业界各种第三方库的普遍做法。如果你想把自己开发的一些库分享给别人使用，也可以遵循这样的套路。Gradle可以看做是升级版的Maven，其使用了Maven最优秀的Dependency管理系统，但是，又规避了Maven的 build pipeline 的刻板和xml格式配置文件等缺点，可以说是目前Java界最好的构建工具。下面就来说说，如何使用Gradle把自己开发的jar包部署到Maven中央库中。   
  
## 创建项目  
创建一个普通的 Android 项目，然后创建一个 library。  
因为这个项目中的 app 模块实际上是用不到的，所以，我们可以直接删除 app 模块，此时 `settings.gradle` 中只有我们的 library ：  
```groovy  
include 'log'  
```  
  
接下来，在 library 中编写你的代码即可。  
  
## 配置项目  
代码编写完成后，在 library 模块的`build.gradle`末尾添加以下代码：  
```groovy  
//**********************************************************************   打包发布  
apply plugin: 'maven'  
  
uploadArchives {  
    def GITHUB_REPO_PATH = "../" //生成的aar文件的保存目录，建议放在project根目录，也可以放在任意目录  
    repositories.mavenDeployer {  
        repository(url: "file://${file(GITHUB_REPO_PATH).absolutePath}")  
        pom.project {  //引用时的格式为【implementation 'com.bqt:aop-log:1.0.0'】  
            groupId 'com.bqt'  
            artifactId 'aop-log'  
            version '1.0.0' //当你的库需要修改时，只需更改此版本号即可  
        }  
    }  
}  
  
// 和源代码一起打包  
task androidSourcesJar(type: Jar) {  
    classifier = 'sources'  
    from android.sourceSets.main.java.sourceFiles  
}  
artifacts {  
    archives androidSourcesJar  
}  
```  
  
## 构建并上传  
在 Gradle > app > Tasks > upload > 双击 uploadArchives  
![](index_files/76b68754-3e42-4834-8d79-56caa10ad1fd.png)  
  
或者使用命令行进入到项目的根目录后，执行以下命令：  
```groovy  
gradlew uploadArchives  
```  
  
然后等待编译完成：  
```  
Executing tasks: [uploadArchives]  
...  
BUILD SUCCESSFUL in 6s  
26 actionable tasks: 23 executed, 3 up-to-date  
16:18:38: Task execution finished 'uploadArchives'.  
```  
  
编译完成后会在你指定的目录下生成以下四个文件：  
![](index_files/20574082-165d-4fb2-bfbc-2f17736efa25.png)  
  
## 上传到 GitHub  
在 Github 上创建一个仓库用于存放生成的 aar 等文件，例如 [aop_log](https://github.com/baiqiantao/aop_log.git)  
  
把仓库`clone`到本地，将生成的 aar 文件拷到这个仓库中，当然也可以将整个工程拷贝到这个仓库中(这样的话项目源码就可以和 aar 一起提交到一个项目中了)，然后 add、commit、push。  
![](index_files/88b17a9e-b138-41cd-a11d-ea1917377406.png)  
  
当升级版本时，只需将新版本的内容添加到仓库即可，不要删除旧版本的内容：  
![](index_files/81acf9fe-7a36-4e34-8f57-f5fa8e823413.png)  
  
## 使用此库  
在需要使用此库的 `project` 的 `build.gradle` 中添加以下代码：  
```groovy  
allprojects {  
    repositories {  
        //... 【baiqiantao】为用户名，【aop_log】为项目名，其他为固定值  
        maven { url "https://raw.githubusercontent.com/baiqiantao/aop_log/master" }  
    }  
}  
```  
  
在需要使用此库的module的 `build.gradle` 中添加如下依赖：  
```groovy  
implementation 'com.bqt:aop-log:1.0.2'  
```  
  
然后就可以愉快的使用库了。  
  
# 使用 JitPack 发布到自定义 maven 仓库【推荐】  
[官网](https://jitpack.io/)  
[GitHub](https://github.com/dcendents/android-maven-gradle-plugin)  
[官网文档](https://jitpack.io/docs/ANDROID/)  
  
> Easy to use package repository for Git  
> Publish your JVM and Android libraries  
> Modification修改 to the standard标准 Maven plugin to be compatible兼容 with android-library projects (aar).  
  
支持 GitHub 和 [码云](https://gitee.com/) 上的项目  
  
在 `jcenter`、`maven center` 平台上发布代码需要注册、登录、提交代码、审核.....等复杂冗长的流程，而 `JitPack` 实际上是一个自定义的 Maven 仓库，不过它的流程极度简化，在其平台上发布项目无需注册申请，只需要输入 Github 项目地址然后点击`get it`即可。  
  
## 创建及配置项目  
1、在 GitHub 上创建仓库代码，clone 项目到本地  
  
2、在此仓库中创建一个 Android 的项目，然后创建一个 library module，并删除 app 模块  
  
3、在项目的根目录的`build.gradle`里面添加：  
```groovy  
classpath 'com.android.tools.build:gradle:3.3.0'  
classpath 'com.github.dcendents:android-maven-gradle-plugin:2.1'  
```  
  
可用的版本及使用条件详见 [官方文档](https://github.com/dcendents/android-maven-gradle-plugin)  
  
> 注意，使用最新版的 gradle 时，很可能会因 JitPack 还没有适配导致失败！  
  
4、在 library 模块的`build.gradle`里面添加：  
```groovy  
apply plugin: 'com.github.dcendents.android-maven'  
```  
  
5、在 library 中编写代码，最后提交到 GitHub 仓库中即可  
  
## 创建 GitHub release 版本  
提交代码后返回到 GitHub 仓库后，点击`releases`创建一个发布版本  
![](index_files/837e10b8-1f9a-4a51-a2e1-888ca2d5c85e.png)  
  
填写版本号、标题、描述、是否是预发布等信息，最后点击确定即可。  
  
这时候我们就可以去 [JitPack官网](https://jitpack.io/) 操作了  
  
## 提交 release 版本到 JitPack  
拷贝GitHub上仓库地址，例如 [aop_log](https://github.com/baiqiantao/aop_log)，将拷贝的地址粘贴到搜索框，点击`Lookup`就会找到我们的项目：  
![](index_files/0a5cc584-3e9e-42e3-b72f-fb2257c52bc2.png)  
![](index_files/470388b9-ddb7-4e9f-8777-c15fac46e8fd.png)  
![](index_files/c2042657-71d6-44e9-bca4-419fc04d4a3a.png)  
![](index_files/7346d5ab-02a9-4af4-8454-61e225ed255b.png)  
  
上图中有四个 TAB，分别为：  
- `Releases`：代表一个发布版本，JitPack 不会自动帮我们构建，只有第一次被用户引用并构建完成才能使用  
- `Builds`：代表一次构建，JitPack 会自动帮我们构建，产生的版本不带`v`，例如`0.1`而非`v0.1`  
- `Branches`：代表某一分支，JitPack 不会自动帮我们构建  
- `Commits`：代表某一此提交，JitPack 不会自动帮我们构建  
  
其中有3个重要的字段：  
- `version`：指你在`Github`上发布项目时候填写的`release tag`，后面带着你提交是添加的日志  
- `log`：JitPack编译你的项目生成的日志文件，绿色表示 ok，红色表示编译错误；点击图标可以查看详细编译信息  
- `status`：表示当前项目的状态，如果编译通过显示的是绿色的`Get it`，表示可以使用，如果编译有问题，那么则显示`Report`，你可以点击`Report`去提交你的 log 并描述一下你的问题，Jitpack 会给你答复  
  
当你的 status 为绿色他的`Get it`的时候，那么说明是可以使用的。点击一下该按钮，根据提示使用就可以了。  
  
Jitpack 会为我们的项目生成一个带版本信息的小图片，点击此图片将显示的代码拷贝到你 GitHub 仓库的`Readme.md`里面去就可以显示徽章了，如下：  
[![](https://www.jitpack.io/v/baiqiantao/aartest.svg)](https://www.jitpack.io/#baiqiantao/aartest)  
  
当我们的库升级时，这个徽章会自动显示最新的版本。  
  
## 使用我们创建好的仓库  
1、根目录`build.gradle`添加：  
```groovy  
allprojects {  
    repositories {  
        google()  
        jcenter()  
        maven { url 'https://www.jitpack.io' }  
    }  
}  
```  
  
2、在需要引用此库的 module 的`build.gradle`里面添加：  
```groovy  
implementation 'com.github.baiqiantao:aartest:0.4'  
```  
  
然后即可使用库中的内容了  
  
## 提示我版本过低解决方案  
[其他可能遇到的问题](https://www.jianshu.com/p/fc4dcd149260)  
  
我一切都是使用的最新版，但一直报错，报错信息如下：  
****  
```groovy  
FAILURE: Build failed with an exception.  
  
* Where:  
Build file '/home/jitpack/build/bqtutils/build.gradle' line: 1  
  
* What went wrong:  
A problem occurred evaluating project ':bqtutils'.  
> Failed to apply plugin [id 'com.android.library']  
   > Minimum supported Gradle version is 4.10.1. Current version is 4.8.1. If using the gradle wrapper, try editing the distributionUrl in /home/jitpack/build/gradle/wrapper/gradle-wrapper.properties to gradle-4.10.1-all.zip  
```  
  
解决方案：  
- 先降低`gradle:3.3.0`为`gradle:3.1.2`  
- 同时降低`gradle-4.10.1`为`gradle-4.4`  
- 同时降低`android-maven-gradle-plugin:2.1`为`android-maven-gradle-plugin:2.0`  
- 同步之后发现OK了，然后再一个个都更新到最新版就可以了！  
  
# 使用 bintray-release 发布到 Jcenter 仓库【问题多】  
> 实际体验：从注册到配置各种垃圾异常，新版本 AndroidStudio + gradle 根本不成功！纯属垃圾东西！浪费时间！  
  
[bintray 官网](https://bintray.com)  
[GitHub](https://github.com/novoda/bintray-release)  
[仓库地址](https://mvnrepository.com/artifact/com.novoda/bintray-release)  
  
[参考1](https://blog.csdn.net/lmj623565791/article/details/51148825)  
[参考2](https://blog.csdn.net/dazhaodai/article/details/71155067)  
[参考3](https://www.jianshu.com/p/aa5532e3a586)  
  
> Super duper(极其出色的) easy way to release your Android and other artifacts to bintray.  
> This is a helper for releasing libraries to bintray. It is intended to help configuring stuff(材料) related to maven and bintray. At the moment(目前) it works with Android Library projects, plain(普通的) Java and plain Groovy projects, but our focus is to mainly support(主要支持) Android projects.  
  
## 注册 [bintray](https://bintray.com/signup/oss) 账号  
账号信息  
```  
baiqiantao@gmail.com  
baiqt  
```  
  
注意事项：  
- 注意，千万不要直接使用官网中的地址去注册，因为官网注册默认是组织，有30天试用期，之后要付费，而且发布时候各种问题，我们应该使用 [正确的注册地址](https://bintray.com/signup/oss) 去注册。如果不小心注册了企业账号，也可以注销再重新注册个人账号  
- 注意，这烂货支持的邮箱不全，不支持带数字的邮箱，用163和qq的都不行，实测gmail、sina、foxmail可以用  
- 注意，注册的时候可能非常非常慢，如果耗时过长，可以重新再试一次  
  
注册完成后，需要邮箱激活，然后就可以登录了。  
  
## 创建 maven 仓库  
登录完成后会进入个人主页面，看下自己的仓库下面有没有一个名为`maven`的仓库(一般都没有)，如果没有，我们需要创建一个名为`maven`的仓库。  
  
设置仓库为`Public`(个人版只能设为`Public`)，Name填写为`maven`，类型Type设置`Maven`，其他的随意。一般 Default Licenses 协议，选择`Apache-2.0`，Description 描述，自由填写。  
  
然后点击Create，稍等几秒钟，就完成创建，然后回到主页，在 Add New Repository 位置就可以看到创建的maven仓库。  
  
然后点击右上角头像 -> Edit Profile -> API Key -> 输入密码，然后就能看到你一段 key 字符串，把这个 copy 下放一边，一会上传要用。  
```  
f863932f739376a6b5199793c9110ece9566b7a3  
```  
  
## 项目配置  
### project 的配置  
在项目的`build.gradle`中添加如下配置  
1、添加`bintray-release`的`classpath`：  
```groovy  
buildscript {  
    //...  
    dependencies {  
        classpath 'com.novoda:bintray-release:0.9'  
    }  
}  
```  
  
2、为了能将统一的配置用在多个module中，我们可以添加一个 ext：  
```groovy  
ext {  
    userOrg = 'baiqt'//用户名，确定你这里用的是用户名  
    groupId = 'com.bqt'//Jcenter上的路径  
    publishVersion = '1.0.0'//版本号，如果需要更新，只需修改下版本号就可以了，其他什么都不用动  
    repoName='maven' //仓库名  
    desc = '日志埋点' //描述  
    website = 'http://www.cnblogs.com/baiqiantao' //网站  
    licences = ['Apache-2.0'] //协议  
}  
```  
  
### library 的配置  
1、在要上传的 module 的`build.gradle`中添加如下配置：  
```groovy  
apply plugin: 'com.novoda.bintray-release'  
publish {  
    artifactId = 'aop-log' //项目名称  
    userOrg = rootProject.userOrg  
    groupId = rootProject.groupId  
    publishVersion = rootProject.publishVersion  
    repoName = rootProject.repoName  
    desc = rootProject.desc  
    website = rootProject.website  
    licences = rootProject.licences  
}  
```  
  
如果同时需要上传多个moudle，例如编译时注解的项目一般至少会包括注解处理器module、注解定义module，我们在上传时，这2个module都要进行上传。那么这几个moudle的配置是一样的，唯一不同的就是publish中的`artifactId`，不同的module应该有它不同的名字。  
  
最终引入的方式为：  
```groovy  
implementation 'com.bqt:aop-log:1.0.0  
```  
  
2、解决常见问题的配置  
```groovy  
android {  
    //...  
    lintOptions {//错误的时候不停止  
        abortOnError false  
    }  
  
    allprojects {  
        tasks.withType(JavaCompile) { //注释中如果有中文，可能会出现异常，设置全局编码  
            options.encoding = "UTF-8"  
        }  
  
        tasks.withType(Javadoc) {  
            options.addStringOption('Xdoclint:none', '-quiet')  
            options.addStringOption('encoding', 'UTF-8')  
        }  
    }  
}  
```  
  
有的建议这么配置：  
```groovy  
allprojects {  
    //...  
    tasks.withType(Javadoc) {  
        options {  
            encoding "UTF-8"  
            charSet 'UTF-8'  
            links "http://docs.oracle.com/javase/7/docs/api"  
        }  
    }  
}  
```  
  
### publish 闭包的属性  
`publish`闭包包含所有这些属性。默认值为空，除非另有规定：  
 * `userOrg`: 包含用于上传的组织名称。[也就是bintray用户名]  
 * `repoName`: 代码仓库名称。 默认设置为“maven”。[即：支持非maven名称命名的maven类型仓库]  
 * `groupId`: 用于上传的group id。  
 * `artifactId`: 使用的artifactId。  
 * `publishVersion`: 含有版本号的字符串。 不能以“-SNAPSHOT”结尾，因为bintray不支持snapshots。  
 * `licences`: 项目的许可证标识符列表。 标识符可以在这里找到：http://spdx.org/licenses/，默认值为`['Apache-2.0']`。  
 * `uploadName`: bintray中包的显示名称。 如果没有设置，`artifactId`将用于作为包名称。  
 * `desc`: 在bintray中项目的简短描述。  
 * `website`: 一个与该项目网址相关的URL字符串。可以在这里使用Github 仓库。  
 * `issueTracker`: 配置该项目用于问题跟踪的URL。 如果`website`包含`'github.com'`那么这个属性默认被设置为`"${website}/issues"`。  
 * `repository`: 配置项目VCS的URL。 如果`website`包含`'github.com'`，则此项默认设置为 `"${website}.git"`。  
 * `autoPublish`: 此布尔值定义上传时是否自动发布包。 如果配置为“假”，该包仍将被上传到bintray，但您必须手动发布。 默认值为“true”。  
 * `bintrayUser`: 用于上传的用户名。  
 * `bintrayKey`: 用户帐户的bintray API密钥。 在这里可以找到这个值的说明https://bintray.com/docs/usermanual/interacting/interacting_apikeys.html  
 * `dryRun`: 如果设置为“true”，则将运行所有内容，但不会将包上传到bintray。 如果“false”，那么它将正常上传。  
 * `publications`: 用于上传的publication名称列表。 默认值为`['maven']`，这是这个插件为你创建的一个publication。 **您可以定义自己的出版物**，更多信息[在wiki页面中](https://github.com/novoda/bintray-release/wiki/Defining-a-custom-Publication)。  
  
## 执行 gradlew 命令进行上传  
执行下面的代码即可上传：  
```groovy  
gradlew clean build bintrayUpload -PbintrayUser=baiqt -PbintrayKey=f863932f739376a6b5199793c9110ece9566b7a3 -PdryRun=false  
```  
  
其中：  
- `user` 用户名，确定你这里用的是用户名  
- `key` 就是我们刚才的让你保存的key  
- `dryRun` 为false代表要上传  
  
当运行完成，看到`BUILD SUCCESSFUL`就没问题了，到此就上传完成了。  
  
注意：  
- 上传时可能需要科学上网  
- 在上传的过程中提示失败为`org.apache.http.conn.HttpHostConnectException: Connection to https://api.bintray.com refused`，这个是网络的问题，多重新上传几次，可能是要上传10多次  
- 上传时可能会遇到各种各样、各种各样、各种各样、各种各样、各种各样的异常，草泥马！  
  
上传后访问`https://bintray.com/用户名/maven`，例如 [baiqt](https://bintray.com/baiqt/maven) 即可看到我们上传的项目了，你可以点击进去看该库的一些信息。  
  
## 关联到 Jcenter 并等待审核  
注意此时我们的库还不能够直接被引用，点击进去该库，按照下图，点击`Add To jcenter`  
![](index_files/945bdf68-586d-4f18-abf4-519be1f8f996.jpg)  
  
然后随便写一下对你该库的描述，然后点击发送就可以了。接下来就是等 bintray 的工作人员审核了，审核通过会给你发送站内Message，并且`Add to Jcenter`那个按钮就消失了。  
  
你还可以根据你上传的groupId，访问`https://jcenter.bintray.com/你的groupId`例如 [baiqiantao](https://jcenter.bintray.com/com/baiqiantao/)  
  
如果同时上传了多个module，审核成功后你会发现这几个module都会被同步到jcenter上。  
  
如果在bintray中找不到 add to Jcenter，可能是因为你注册的是企业账号，可以取消重新注册为个人账号  
  
# 使用 gradle-bintray-plugin 发布到 Jcenter 仓库【麻烦】  
[gradle-bintray-plugin 仓库地址](https://mvnrepository.com/artifact/com.jfrog.bintray.gradle/gradle-bintray-plugin)  
[android-maven-gradle-plugin 仓库地址](https://mvnrepository.com/artifact/com.github.dcendents/android-maven-gradle-plugin)  
[android-maven-plugin 仓库地址](https://mvnrepository.com/artifact/com.github.dcendents/android-maven-plugin)  
  
[参考](http://www.cnblogs.com/qianxudetianxia/p/4322331.html)  
  
> 使用体验：上传个库麻烦到丧心病狂、令人发指的地步，为什么这样垃圾的东西都能存在？  
  
## 注册账号  
注册账号等步骤和上面的一样。  
  
## project 的配置  
在 project 的`build.gradle`中添加如下配置  
```groovy  
classpath 'com.jfrog.bintray.gradle:gradle-bintray-plugin:1.8.4'  
classpath 'com.github.dcendents:android-maven-plugin:1.2'  
//classpath 'com.github.dcendents:android-maven-gradle-plugin:1.5'  
```  
  
## library 的配置  
在要上传的 module 的`build.gradle`中添加如下配置：  
```groovy  
apply plugin: 'com.github.dcendents.android-maven'  
apply plugin: 'com.jfrog.bintray'  
```  
  
一些共用参数：  
```groovy  
def siteUrl = 'https://github.com/openproject/LessCode'                   // project homepage  
def gitUrl = 'https://github.com/openproject/LessCode.git'                // project git  
group = "com.jayfeng"                                                     // Maven Group ID  
```  
  
完整的`build.gradle`，请参考 [build.gradle](https://github.com/openproject/LessCode/blob/master/lesscode-core/build.gradle)  
  
### 定义 pom 并打包 aar  
上传到 jcenter 至少需要四个文件，除了打包的 aar 之外，还需要 pom 和 javadoc、source，否则是通不过jcenter审核的，不过这些我们都可以用脚本生成：  
```groovy  
install {  
    repositories.mavenInstaller {  
        pom {  
            project {  
                packaging 'aar'  
                name 'Less Code For Android'                              // project title  
                url siteUrl  
                licenses {  
                    license {  
                        name 'The Apache Software License, Version 2.0'  
                        url 'http://www.apache.org/licenses/LICENSE-2.0.txt'  
                    }  
                }  
                developers {  
                    developer {  
                        id 'jayfeng'                                     // your user id (you can write your nickname)  
                        name 'jian.feng'                                 // your user name  
                        email '673592063@qq.com'                         // your email  
                    }  
                }  
                scm {  
                    connection gitUrl  
                    developerConnection gitUrl  
                    url siteUrl  
                }  
            }  
        }  
    }  
}  
```  
  
### 打包 javadocjar 和 sourcejar  
这两个也是上传到jcenter必须要的：  
```groovy  
task sourcesJar(type: Jar) {  
    from android.sourceSets.main.java.srcDirs  
    classifier = 'sources'  
}  
  
task javadoc(type: Javadoc) {  
    source = android.sourceSets.main.java.srcDirs  
    classpath += project.files(android.getBootClasspath().join(File.pathSeparator))  
}  
  
task javadocJar(type: Jar, dependsOn: javadoc) {  
    classifier = 'javadoc'  
    from javadoc.destinationDir  
}  
  
artifacts {  
    // archives javadocJar  
    archives sourcesJar  
}  
```  
  
### 上传到 Jcenter 仓库  
上传到`jcenter`的网站 BinTray，需要用户验证，需要2个值：  
```groovy  
bintray.user=openproject  
bintray.apikey=c5434272d522d35d1a0123459981225564155753  
```  
  
因为这个属于个人隐私，一般不能传到网上去，所以需要在记录到 LessCode 下的`local.properties`中(利用`gitignore`忽略这个文件到git)，然后脚本再从`local.properties`中读取这两个值：  
```groovy  
Properties properties = new Properties()  
boolean isHasFile = false  
if (project.rootProject.file('local.properties') != null){  
    isHasFile = true  
    properties.load(project.rootProject.file('local.properties').newDataInputStream())  
}  
bintray {  
    user = isHasFile ? properties.getProperty("bintray.user") : System.getenv("bintray.user")  
    key = isHasFile ? properties.getProperty("bintray.apikey") : System.getenv("bintray.apikey")  
    configurations = ['archives']  
    pkg {  
        repo = "maven"  
        name = "lesscode"                                                 // #CONFIG# project name in jcenter  
        websiteUrl = siteUrl  
        vcsUrl = gitUrl  
        licenses = ["Apache-2.0"]  
        publish = true  
    }  
}  
```  
  
## 执行 gradle 任务  
根据上面的`build.gradle`，会产生几个特别的`gradle`任务，通过`gradew`执行这些任务即可：  
```groovy  
gradew javadocJar  
gradew sourcesJar  
gradew install  
gradew bintrayUpload  
```  
  
或者通过 AS 的最右边栏的 Gradle 窗口分别可视化操作，点击执行。  
  
## 请求审核  
当 bintrayUpload 成功之后，在 [我的主页](https://bintray.com/openproject/) 右下部分 Latest Activity 块，会看到你提交了一个项目，从这 Latest Activity 列表中点击你的项目，进入 [详情页](https://bintray.com/openproject/maven/lesscode-core/view)，找到 Maven Central 标签，鼠标放上去它会提示你去提交审核，点击进入后，随便写什么都可以了。  
  
只要上面没问题，一般审核很快，几个小时就差不多了。  
  
> 我的情况稍微波折一点，第一个版本0.0.1没有传javadocjar和sourcejar，等了半天，管理员站内信通知审核失败，再传的话需要升级到0.0.2，否则0.0.1已经存在不能重复上传。  
> 0.0.2版本上传后，等了半天，管理员告诉我0.0.2版本OK了，但是0.0.1版本还是没上传，于是我找了找能不能删了0.0.1版本，是可以删除的，这样的话其实前面没必要升级到0.0.2了，删了重新上传0.0.1也是可以的。  
> 删了之后，还是要按上面的步骤重新发请求审核，等了几个小时就通过了。  
  
这次审核通过了，后面升级就不用再审核了。  
  
## 第三方引用  
```groovy  
implementation 'com.jayfeng.lesscode:lesscode-core:0.1.2'  
```  
  
2019-1-30  
