| Markdown版本笔记 | 我的GitHub首页 | 我的博客 | 我的微信 | 我的邮箱 |  
| :------------: | :------------: | :------------: | :------------: | :------------: |  
| [MyAndroidBlogs][Markdown] | [baiqiantao][GitHub] | [baiqiantao][博客] | bqt20094 | baiqiantao@sina.com |  
  
[Markdown]:https://github.com/baiqiantao/MyAndroidBlogs  
[GitHub]:https://github.com/baiqiantao  
[博客]:http://www.cnblogs.com/baiqiantao/  
  
自动化打包 Jenkins 持续集成 Git Gradle MD    
***  
目录  
===  

- [环境要求](#环境要求)
- [Jenkins 环境配置](#Jenkins-环境配置)
	- [Jenkins 安装](#Jenkins-安装)
	- [全局工具配置](#全局工具配置)
	- [Android SDK 路径配置](#Android-SDK-路径配置)
	- [邮件配置](#邮件配置)
- [创建并配置打包任务](#创建并配置打包任务)
	- [参数化构建](#参数化构建)
	- [工作空间配置](#工作空间配置)
	- [源码管理](#源码管理)
	- [构建触发器](#构建触发器)
	- [构建](#构建)
	- [上传apk到蒲公英](#上传apk到蒲公英)
	- [构建后操作](#构建后操作)
- [打包](#打包)
  
# 环境要求  
**JDK**  
打开cmd，输入`java -version`，可以看到打印的Java版本信息。如果没有，请配置java环境变量。  
  
**Git**  
打开cmd，输入`git --version`，可以看到打印的Git版本信息。如果没有，请配置Git环境变量。  
  
**Gradle**  
打开cmd，输入`gradle -v`，可以看到打印的Gradle版本信息。如果没有，请配置Gradle环境变量。  
    
> 配置方式：环境变量--系统变量--Path--将你自己的AS安装目录下的gradle目录复制过来贴上，例如:【C:\Android\AS_2.2.2\gradle\gradle-4.4\bin】    
  
**Tomcat**  
双击运行Tomcat的bin目录下的`startup.bat`脚本将Tomcat运行起来，日志中最后会提示服务开启成功：`Server startup in 3475 ms`。    
Tomcat的conf目录下的`server.xml`文件可以配置端口号，默认是`8080`，如果跟其他项目有冲突可以自行修改。    
  
# Jenkins 环境配置  
Jenkins是一个很受欢迎的CI持续集成工具，能够实现项目的自动构建、打包、测试、发布等。还可以在构建失败、构建不稳定等状态后发送邮件通知。  
  
## Jenkins 安装  
[Jenkins下载](http://mirrors.tuna.tsinghua.edu.cn/jenkins/war-stable/2.138.2/jenkins.war)    
下载完之后，解压压缩包运行安装程序即可完成安装。  
  
在Jenkins安装目录下的`jenkins.xml`文件中可以配置编码格式、端口号等参数：  
```xml  
<arguments>-Xrs -Xmx256m -Dfile.encoding=utf-8 -Dhudson.lifecycle=hudson.lifecycle.WindowsServiceLifecycle -jar "%BASE%\jenkins.war" --httpPort=8081 --webroot="%BASE%\war"</arguments>  
```  
  
[war包下载](http://101.110.118.21/mirrors.jenkins-ci.org/windows-stable/jenkins-2.138.1.zip)    
将下载的war包放到Tomcat的`webapps`目录下，双击运行Tomcat的bin目录下的`startup.bat`脚本将Tomcat运行起来(若Tomcat已经启动，先关闭再重新启动)，此时正常会提示：`Jenkins is fully up and running`。  
  
然后在浏览器访问`http://localhost:8080/jenkins/`即可进入Jenkins页面。    
  
首次启动时会提示安装插件，则可能需要一点时间，一般我们安装推荐的插件即可，以后我们还可以在`系统管理 -> 插件管理`界面安装或卸载插件。  
  
然后，按照提示路径获取密码`57f6b65f714348b7947c966d2378f9a0`后输入。  
然后到用户名设置界面，这个界面你有两个选择可以操作：  
- 直接点击 Continue as Admin 安装，默认此时的超级管理员为admin    
- 输入用户名，密码，确认密码，全名，电子邮件地址。然后点击 Save and Finish    
  
至此，Jenkins算是初步安装成功了。  
  
## 全局工具配置   
在Jenkins主界面，点击进入`系统管理 -> 全局工具配置`界面。     
- JDK的配置：别名自取，取消选中`自动安装`，JAVA_HOME中输入自己电脑上jdk所在的路径，例如`C:\Android\jdk1.8.0_171`。    
- Git的配置，同样需要将自己电脑上git.exe所在的路径复制粘贴上来，例如`C:\Android\Git\bin\git.exe`。   
- Gradle的配置，例如`C:\Android\AS_2.2.2\gradle\gradle-4.4`。    
  
> 一定要注意，这里和在Path中配置的路径不一样，这里的路径要为bin的上一级目录，因为这个事我被坑惨了。  
  
设置完以后保存即可。  
  
## Android SDK 路径配置  
在Jenkins主界面，点击进入`系统管理 -> 系统设置`页面。点击`全局属性 -> 环境变量`，这里的和操作系统上配置的环境变量一致，通过勾选的名称也知道，指的就是系统环境变量。例如【ANDROID_HOME】【D:\software\android_sdk】    
![mark](http://pfpk8ixun.bkt.clouddn.com/blog/181014/51JBK2BbI6.png?imageslim)    
  
## 邮件配置  
参考：[邮件配置](https://blog.csdn.net/fullbug/article/details/53024562)    
参考：[邮件配置](https://blog.csdn.net/lsyz0021/article/details/72683275)    
  
# 创建并配置打包任务  
接下来针对某个Android APP项目创建专门的打包任务。    
  
- 点击创建一个新任务 -> 输入任务名(不支持中文) -> 选择“构建一个自由风格的软件项目”  
- 如果想根据一个已经存在的任务创建，可以使用下面的复制选项。    
- 之后会进入到项目配置页，这些设置以后还可以更改：    
  
设置之后点击确定便进入了任务配置页面，这些配置在以后都可以进行修改。  
  
## 参数化构建  
在实际的项目开发过程中，可能会这对不同的环境、配置进行打包，比如选择不同的环境、渠道、签名或第三方工具的key，这些都有可能需要定制。`参数化构建`就是实现这种效果的。我们所要做的就是就是`把选择提供出来`，各种选择可以通过`参数`的形式传递给打包服务，然后根据打包时的选择，来对应出包。    
![mark](http://pfpk8ixun.bkt.clouddn.com/blog/181014/AF7DmLhk8D.png?imageslim)    
  
通过上图的下拉列表的选择，可以看到支持的各种参数类型，例如我们选择`选项参数`来提供指定环境的选择。    
![mark](http://pfpk8ixun.bkt.clouddn.com/blog/181014/Ce34caBKjF.png?imageslim)    
  
> PS:有些配置如果我们不知道怎么填，可以点击后面的蓝色问号按钮，可以显示提示，再点一下就会关闭提示。  
  
## 工作空间配置   
配置完上一步后，点击右下角的“高级”按钮，配置我们的工作空间。以后Android项目代码以及打出的包都会在这个路径下。     
![mark](http://pfpk8ixun.bkt.clouddn.com/blog/181014/EiL5fmK6Kl.png?imageslim)    
  
## 源码管理  
这一步是配置我们的源码，我们的项目可以托管在SVN上也可以在Git上。    
如果安装了git插件，在源码管理会出现Git，我们选择以`Git`的方式管理，然后配置代码地址和要编译的分支。例如项目地址`https://github.com/baiqiantao/LifecycleTest.git`，分支使用默认的`*/master`。  
![mark](http://pfpk8ixun.bkt.clouddn.com/blog/181014/Hc7fk5iK4G.png?imageslim)    
  
然后点击`Add`添加`Credentials`(证书)，有以下几种方式：    
![mark](http://pfpk8ixun.bkt.clouddn.com/blog/181014/j1h3gDbEeB.png?imageslim)    
  
我们一般选择`用户名+密码` 或`SSH`方式，配置后就可以自动验证了。    
  
## 构建触发器  
触发器是决定什么时候触发构建，可以设置为定时构建：    
![mark](http://pfpk8ixun.bkt.clouddn.com/blog/181014/jGmIIBjKiC.png?imageslim)    
  
还可以设置定时检查代码更新，有更新则编译，否则不编译：    
![mark](http://pfpk8ixun.bkt.clouddn.com/blog/181014/l1kAe1LHcI.png?imageslim)    
  
定时规则案例：  
- 每十五分钟【H/15 * * * *】  
- 每小时的0-29分钟内每十分钟一次【H(0-29)/10 * * * *】  
- 周一到周五9:00到16:00之间，每小时的第45分钟构建，每两小时构建一次【45 9-16/2 * * 1-5】  
- 周一到周五9:00到16:00之间，每两个小时一次【H H(9-16)/2 * * 1-5】  
- 除了12月之外，每个月的1日和15日每天一次【H H 1,15 1-11 *】  
  
## 构建  
点击`构建`中的`增加构建步骤`，可以在这里利用`shell`来修改源码中的配置文件，例如可以修改文件上传路径、访问地址或输出的包名等。  
  
如果安装`Gradle`插件成功的话，应该会出现下图的`Invoke Gradle script`，我们选择这个：  
![mark](http://pfpk8ixun.bkt.clouddn.com/blog/181014/H2IAFfKF89.png?imageslim)    
  
然后选择`Gradle Version`，就是我们之前配置的`Gradle`的路径，直接下拉选择好版本就可以了。    
  
注意，这里有一个bug：  
> 为了在Jenkins中使用gradle,需要选择`Invoke Gradle`选项，然后还必须在`Use gradle wrapper`选项中配置你要跑的任务才能成功的利用gradle程序构建你的程序，但是你千万不能选择该选项，否则Jenkins会默认调用gradlew程序执行任务，但是一般你配置gradle后，没有该程序，就会报错。  
  
在Tasks可以配置【clean assembleDebug】或【clean build --stacktrace】等你想执行的命令。  
![mark](http://pfpk8ixun.bkt.clouddn.com/blog/181014/aC9AIiAhdc.png?imageslim)    
  
其他地方都不需要配置。  
> 注意：一般来说`local.properties`不会添加到版本库，所以可能需要你手动copy到相应的目录下。  
  
## 上传apk到蒲公英  
官方文档：[curl上传](https://www.pgyer.com/doc/view/jenkins) ， [插件上传](https://www.pgyer.com/doc/view/jenkins_plugin)  
  
如果没有蒲公英的账号，先 [注册](https://www.pgyer.com/user/register) 一个，然后在“账户设置”中找到“API信息”，获取`API Key`【6f14e9f0420bcf9cb2ae45e29afd15db】和`User Key`【e9fbc97a0d37793aabf62bd629c9fb78】  
  
然后需要使用 [curl](https://curl.haxx.se/download.html) 上传文件。下载后直接解压即可，不需要安装，然后像其他配置环境变量一样将curl目录加入Path即可，例如【C:\_Web\curl\curl-7.61.1】，然后在cmd中输入【curl --version】，若能显示版本信息，说明安装成功了。  
  
点击`构建`中的`增加构建步骤`，添加`执行shell`，然后配置下面的上传命令：【curl -F  "file=@app/build/outputs/apk/debug/app-debug.apk"  -F  "uKey=e9fbc97a0d37793aabf62bd629c9fb78"  -F  "_api_key=6f14e9f0420bcf9cb2ae45e29afd15db" http://www.pgyer.com/apiv1/app/upload】  
  
配置完后，在下次构建后即可自动上传到蒲公英。  
  
## 构建后操作  
构建时可能会生成多个文件，我们可以设置只将自己需要的文件做存档。在存档文件中输入需要存档的文件路径，多个文件以逗号分割，存档文件默认路径为 WORKSPACE ,存档文件的相对路径既是 WORKSPACE 并且是以正则表达式路径去匹配需要存档的文件。  
![mark](http://pfpk8ixun.bkt.clouddn.com/blog/181014/35FGilKDlF.png?imageslim)    
  
如果是apk包，可填写`【**/*.apk】`或`【app\build\outputs\apk\*.apk】`。  
  
> 您可以使用`module/dist/**/*.zip`之类的通配符。请参阅Ant文件集的includes属性以获取确切的格式。基目录是workspace。 您只能归档workspace中的文件。  
  
在打包完成后，可以显示我们存档的文件：  
![mark](http://pfpk8ixun.bkt.clouddn.com/blog/181014/i7A7bD3AGD.png?imageslim)    
  
# 打包  
一切配置完成后，点击最右边的按钮即可通过jenkins进行打包：  
![mark](http://pfpk8ixun.bkt.clouddn.com/blog/181014/136KHJg9DL.png?imageslim)  
  
打包过程：  
![mark](http://pfpk8ixun.bkt.clouddn.com/blog/181014/iCC6mGC25j.png?imageslim)  
  
至此，所有流程都走通了。  
  
遗留问题：  
- 邮件配置  
- 存档不是保存每次生成的apk吗？从目前的配置来看，因为没保存到指定路径，很容易被覆盖呀！  
