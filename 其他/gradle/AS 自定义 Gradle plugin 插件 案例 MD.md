| Markdown版本笔记 | 我的GitHub首页 | 我的博客 | 我的微信 | 我的邮箱 |  
| :------------: | :------------: | :------------: | :------------: | :------------: |  
| [MyAndroidBlogs][Markdown] | [baiqiantao][GitHub] | [baiqiantao][博客] | bqt20094 | baiqiantao@sina.com |  
  
[Markdown]:https://github.com/baiqiantao/MyAndroidBlogs  
[GitHub]:https://github.com/baiqiantao  
[博客]:http://www.cnblogs.com/baiqiantao/  
  
AS 自定义 Gradle plugin 插件 案例 MD  
***  
目录  
===  

- [AS 中自定义 Gradle plugin](#as-中自定义-gradle-plugin)
	- [编写插件](#编写插件)
	- [传递参数](#传递参数)
	- [发布插件到仓库](#发布插件到仓库)
	- [使用插件](#使用插件)
  
# AS 中自定义 Gradle plugin  
[参考1](https://github.com/helen-x/gradle-plugin-demo)  
[参考2](http://blog.csdn.net/sbsujjbcy/article/details/50782830)  
  
结合 AndroidStudio，自定义Gradle plugin可以完成很多功能，比如：添加编译依赖、进行Aspecj编译、自动生成混淆配置。  
  
项目中引入自定义 Gradle plugin 一般有三种方法:  
- 直接写在 build.gradle 中，这种方式的缺点是无法复用插件代码，在其他项目中还得复制一遍代码  
- plugin 源码放到 `rootProjectDir/buildSrc/src/main/groovy` 目录下(没用过)  
- plugin 打包后发布到 maven 仓库, 然后项目通过依赖的形式引入  
  
下面介绍的是第 3 种方式  
  
##  编写插件  
1、创建插件 module  
新建一个Android工程，在这个工程里面新建一个`Android Library`，先起名叫 cooker-plugin 吧，我们将会用这个 library 写 Gradle plugin   
  
2、建立 plugin 的目录结构  
把这个 cooker-plugin 中除了build.gradle文件外的默认文件都删除，然后按照下面新建文件：  
- 在新建的module中新建文件夹src，接着在src文件目录下新建main文件夹，在main目录下新建groovy目录，这时候groovy文件夹会被Android识别为groovy源码目录。  
- 除了在main目录下新建groovy目录外，你还要在main目录下新建resources目录，同理resources目录会被自动识别为资源文件夹。  
- 在groovy目录下新建项目包名，就像Java包名那样。  
- 在resources目录下新建文件夹META-INF，META-INF文件夹下新建gradle-plugins文件夹。  
  
这样，就完成了gradle 插件的项目的整体搭建，之后就是小细节了。目前，项目的结构是这样的：  
![](index_files/001e25a9-3e19-45e2-b0b4-97d88488bc39.png)  
  
3、声明 plugin 信息  
在 `src/main/resources/META-INF/gradle-plugins` 里声明 plugin 信息，比如新建`cooker-plugin.properties`文件(文件名 `cooker-plugin` 是插件名称)，在其中指定插件的实现类的全路径类名：  
```groovy  
implementation-class=com.helen.plugin.CookerPlugin  
```  
  
4、配置 build.gradle  
在 build.gradle 中声明用 groovy 开发  
```groovy  
apply plugin: 'groovy'  
  
dependencies {  
    compile gradleApi()  
    compile localGroovy()  
}  
  
repositories {  
    mavenCentral()  
}  
```  
  
5、编写插件逻辑  
插件代码放在 `src/main/groovy` 下，实现 plugin，其实就是实现 `Plugin<Project>` 接口  
```java  
package com.helen.plugin  
  
import org.gradle.api.Plugin  
import org.gradle.api.Project  
  
class CookerPlugin implements Plugin<Project> {  
    @Override  
    void apply(Project project) {  
        println "这里实现plugin的逻辑!"  
        project.task('cooker-test-task').doLast { variant ->  
            println "自定义任务-doLast"  
        }.doFirst { variant ->  
            println "自定义任务-doFirst"  
        }  
    }  
}  
```  
  
6、生成 plugin 插件  
在 cooker-plugin 项目中，build 一下  
![](index_files/450f29aa-c10a-4de6-8249-5b78863a368f.png)  
  
任务完成以后，就能在`build/libs`下生成对应的 plugin 插件了  
![](index_files/9f2fd4b9-ae1d-4c2e-9688-ad5dae0f2363.png)  
  
现在这个插件就能使用了，可以发布在本地仓库或者 Maven 仓库  
  
## 传递参数  
接下来我们介绍如何获得自定义的参数  
  
1、新建 PluginExtension.groovy，用于定义我们可以支持的参数：  
```groovy  
class PluginExtension {  
    def param1 = "param1 defaut"  
    def param2 = "param2 defaut"  
    def param3 = "param3 defaut"  
}  
```  
  
我们希望能传入嵌套的参数，所以再新建一个 PluginNestExtension.groovy：  
```groovy  
class PluginNestExtension {  
    def nestParam1 = "nestParam1 defaut"  
    def nestParam2 = "nestParam2 defaut"  
    def nestParam3 = "nestParam3 defaut"  
}  
```  
  
2、新建一个 CustomTask.groovy，继承 `DefaultTask` 类，使用 `@TaskAction` 注解标注实现的方法：  
```groovy  
class CustomTask extends DefaultTask {  
  
    @TaskAction  
    void output() {  
        println "param1 is ${project.pluginExt.param1}"  
        println "param2 is ${project.pluginExt.param2}"  
        println "param3 is ${project.pluginExt.param3}"  
        println "nestparam1 is ${project.pluginExt.nestExt.nestParam1}"  
        println "nestparam2 is ${project.pluginExt.nestExt.nestParam2}"  
        println "nestparam3 is ${project.pluginExt.nestExt.nestParam3}"  
    }  
}  
```  
  
这里我们只是做了拿到了参数，然后做最简单的输出操作，使用 `${project.pluginExt.param1}` 和 `${project.pluginExt.nestExt.nestParam1}` 等拿到使用者设置的值。  
  
3、在 apply 方法中建立映射关系：  
```groovy  
project.extensions.create('pluginExt', PluginExtension)  
project.pluginExt.extensions.create('nestExt', PluginNestExtension)  
project.task('customTask', type: CustomTask)  
```  
  
4、定义外部参数，这里我们定义了param1,param2,nestParam1,nestParam2，而param3和nestParam3保持默认。  
```groovy  
pluginExt {  
    param1 = 'app param1'  
    param2 = 'app param2'  
    nestExt {  
        nestParam1 = 'app nestParam1'  
        nestParam2 = 'app nestParam2'  
    }  
}  
```  
  
这样之后，在执行`customTask`时就会输出使用者对自定义的参数设置的值  
  
## 发布插件到仓库  
发布到仓库的方式有很多，下面只介绍利用 mavenDeployer 插件发布在本地仓库  
  
1、引入 mavenDeplayer 插件  
修改 cooker-plugin 的 build.gradle，添加如下内容:  
  
```java  
apply plugin: 'maven'//添加maven plugin，用于发布我们的jar  
uploadArchives {  
    repositories {  
        mavenDeployer {  
            pom.groupId = 'com.helen.plugin'  
            pom.artifactId = 'cooker-plugin'  
            pom.version = 1.0  
            repository(url: uri('../release'))   //文件发布目录(相对当前 build.gradle 的路径)  
        }  
    }  
}  
```  
  
2、用 uploadArchices 发布   
运行 uploadArchives 就能在设置的仓库路径中生成 cooker-plugin 了  
![](index_files/bc00f060-90a8-4aa4-863a-f86b3be6d1a9.png)  
  
## 使用插件  
1、在 build.gradle 中引入 cooker-plugin  
```java    
buildscript {  
    repositories {  
        jcenter()  
        maven {  
            url uri('release') //cooker-plugin 所在的仓库，这里是本地目录(相对当前 build.gradle 的路径)  
        }  
    }  
    dependencies {  
        classpath 'com.android.tools.build:gradle:3.2.1'  
        classpath 'com.helen.plugin:cooker-plugin:1.0'  //引入cooker-plugin  
    }  
}  
```  
  
```groovy  
apply plugin: 'cooker-plugin'  
```  
  
2、我们编译 App 的时候 cooker-plugin 就会介入了  
每次 `clean/build` 时, 在 Gradle Console 可以看到我们的 log  
```  
这里实现plugin的逻辑!  
```  
  
3、使用 cooker-plugin 中定义的 task  
前面demo中，我们新建了两个task: `cooker-test-task`和`customTask`, 我们可以通过两种方式运行这两个task，一种方式是双击 app > other 分类下的 task 名，一种是通过 gradlew 命令  
  
cooker-test-task 打印的日志：  
```groovy  
Executing tasks: [cooker-test-task]  
  
这里实现plugin的逻辑!  
:app:cooker-test-task  
自定义任务-doFirst  
自定义任务-doLast  
```  
  
customTask 打印的日志：  
```groovy  
Executing tasks: [customTask]  
  
这里实现plugin的逻辑!  
:app:customTask  
param1 is app param1  
param2 is app param2  
param3 is param3 defaut  
nestparam1 is app nestParam1  
nestparam2 is app nestParam2  
nestparam3 is nestParam3 defaut  
```  
  
到此为止, 自定义Gradle plugin的基本过程就介绍完了。  
  
2019-2-11  
