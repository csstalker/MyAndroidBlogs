| Markdown版本笔记 | 我的GitHub首页 | 我的博客 | 我的微信 | 我的邮箱 |  
| :------------: | :------------: | :------------: | :------------: | :------------: |  
| [MyAndroidBlogs][Markdown] | [baiqiantao][GitHub] | [baiqiantao][博客] | bqt20094 | baiqiantao@sina.com |  
  
[Markdown]:https://github.com/baiqiantao/MyAndroidBlogs  
[GitHub]:https://github.com/baiqiantao  
[博客]:http://www.cnblogs.com/baiqiantao/  
  
gradlew 命令行 build 调试 构建错误 Manifest merger failed MD  
***  
目录  
===  
[TOC]  
  
# 解决 Manifest merger failed 问题  
在android开发的时候，有时候会遇到这样的问题：  
```  
Manifest merger failed with multiple errors, see logs  
```  
![](http://pfpk8ixun.bkt.clouddn.com/markdown-img-paste-20181005031541196.png)    
![](http://pfpk8ixun.bkt.clouddn.com/markdown-img-paste-20181005031548180.png)    
  
但是要命的是，你管你怎么找，除了这个log，再没有什么其他的有用信息了，怎么办？  
  
处理方式是这样的：  
  
首先进入命令行，输入命令：  
```  
gradlew processDebugManifest --stacktrace  
```  
  
> PS：    
    Run gradlew tasks to get a list of available tasks.     
    Run with 【--stacktrace】 option to get the stack trace.     
    Run with 【--info】 or 【--debug】 option to get more log output.     
    Run with 【--scan】 to get full insights.  
  
其中，`processDebugManifest` 是用于调试清单文件的，这个命令会获取更多的`log`信息。  
  
然后会输出很多信息，其中有用的是这一部分：  
```  
> Task :app:processDebugManifest FAILED  
C:\Android\_coder\_workspace_as\DDComponent_Simple\app\src\main\AndroidManifest.xml:6:5-20:19 Error:  
        tools:replace specified at line:6 for attribute android:theme, but no new value specified  
C:\Android\_coder\_workspace_as\DDComponent_Simple\app\src\main\AndroidManifest.xml Error:  
        Validation failed, exiting  
See http://g.co/androidstudio/manifest-merger for more information about the manifest merger.  
```  
  
卧槽，一看我就明白了，原来是这里写错了：  
```xml  
<application  
    android:name=".MyApplication"  
    android:allowBackup="false"  
    android:icon="@drawable/app_icon"  
    android:label="@string/app_app_name"  
    tools:ignore="GoogleAppIndexingWarning"  
    tools:replace="android:icon,android:theme,android:label,android:allowBackup">  
```  
本来 `replace` 是用于合并清单文件时替换依赖库或子模块中的配置的，但是这里因为没有指定 `theme` ，所以替换时就会因找不到 theme 而报错。  
  
修改方式当然相应的有两种，一种是指定 theme，另一种是不替换 theme。  
  
# 使用 gradlew 命令调试构建过程  
## gradlew 命令可配置的参数  
> 执行【gradlew --help】命令可获取 gradlew 命令可配置的参数    
  
```  
USAGE: gradlew [option...] [task...]  
  
-?, -h, --help Shows this help message.  
-a, --no-rebuild Do not rebuild project dependencies. [deprecated]  
-b, --build-file Specify the build file.  
--build-cache Enables the Gradle build cache. Gradle will try to reuse outputs from previous builds. [incubating]  
-c, --settings-file Specify the settings file.  
--configure-on-demand Configure necessary projects only. Gradle will attempt to reduce configuration time for large multi-project builds. [incubating]  
--console Specifies which type of console output to generate. Values are 'plain', 'auto' (default), 'rich' or 'verbose'.  
--continue Continue task execution after a task failure.  
-D, --system-prop Set system property of the JVM (e.g. -Dmyprop=myvalue).  
-d, --debug Log in debug mode (includes normal stacktrace).  
--daemon Uses the Gradle Daemon to run the build. Starts the Daemon if not running.  
--foreground Starts the Gradle Daemon in the foreground. [incubating]  
-g, --gradle-user-home Specifies the gradle user home directory.  
-I, --init-script Specify an initialization script.  
-i, --info Set log level to info.  
--include-build Include the specified build in the composite. [incubating]  
-m, --dry-run Run the builds with all task actions disabled.  
--max-workers Configure the number of concurrent workers Gradle is allowed to use. [incubating]  
--no-build-cache Disables the Gradle build cache. [incubating]  
--no-configure-on-demand Disables the use of configuration on demand. [incubating]  
--no-daemon Do not use the Gradle daemon to run the build. Useful occasionally if you have configured Gradle to always run with the daemon by default.  
--no-parallel Disables parallel execution to build projects. [incubating]  
--no-scan Disables the creation of a build scan. For more information about build scans, please visit https://gradle.com/build-scans. [incubating]  
--offline Execute the build without accessing network resources.  
-P, --project-prop Set project property for the build script (e.g. -Pmyprop=myvalue).  
-p, --project-dir Specifies the start directory for Gradle. Defaults to current directory.  
--parallel Build projects in parallel. Gradle will attempt to determine the optimal number of executor threads to use. [incubating]  
--profile Profile build execution time and generates a report in the <build_dir>/reports/profile directory.  
--project-cache-dir Specify the project-specific cache directory. Defaults to .gradle in the root project directory.  
-q, --quiet Log errors only.  
--recompile-scripts Force build script recompiling. [deprecated]  
--refresh-dependencies Refresh the state of dependencies.  
--rerun-tasks Ignore previously cached task results.  
-S, --full-stacktrace Print out the full (very verbose) stacktrace for all exceptions.  
-s, --stacktrace Print out the stacktrace for all exceptions.  
--scan Creates a build scan. Gradle will emit a warning if the build scan plugin has not been applied. (https://gradle.com/build-scans) [incubating]  
--status Shows status of running and recently stopped Gradle Daemon(s).  
--stop Stops the Gradle Daemon if it is running.  
-t, --continuous Enables continuous build. Gradle does not exit and will re-execute tasks when task file inputs change. [incubating]  
-u, --no-search-upward Don't search in parent folders for a settings file.  
-v, --version Print version info.  
-w, --warn Set log level to warn.  
-x, --exclude-task Specify a task to be excluded from execution.  
```  
  
## 获取所有 task  
> 执行【gradlew tasks】命令可获取所有 task     
    To see all tasks and more detail, run gradlew tasks --all    
    To see more detail about a task, run gradlew help --task <task>     
  
```  
> Task :tasks   
------------------------------------------------------------  
All tasks runnable from root project  
------------------------------------------------------------  
  
Android tasks  
-------------  
androidDependencies - Displays the Android dependencies of the project.  
signingReport - Displays the signing info for each variant.  
sourceSets - Prints out all the source sets defined in this project.  
  
Build tasks  
-----------  
assemble - Assembles all variants of all applications and secondary packages.  
assembleAndroidTest - Assembles all the Test applications.  
assembleDebug - Assembles all Debug builds.  
assembleRelease - Assembles all Release builds.  
build - Assembles and tests this project.  
buildDependents - Assembles and tests this project and all projects that depend on it.  
buildNeeded - Assembles and tests this project and all projects it depends on.  
clean - Deletes the build directory.  
cleanBuildCache - Deletes the build cache directory.  
compileDebugAndroidTestSources  
compileDebugSources  
compileDebugUnitTestSources  
compileReleaseSources  
compileReleaseUnitTestSources  
mockableAndroidJar - Creates a version of android.jar that's suitable for unit tests.  
  
Build Setup tasks  
-----------------  
init - Initializes a new Gradle build.  
wrapper - Generates Gradle wrapper files.  
  
Help tasks  
----------  
buildEnvironment - Displays all buildscript dependencies declared in root project 'RxJavaDemo'.  
components - Displays the components produced by root project 'RxJavaDemo'. [incubating]  
dependencies - Displays all dependencies declared in root project 'RxJavaDemo'.  
dependencyInsight - Displays the insight into a specific dependency in root project 'RxJavaDemo'.  
dependentComponents - Displays the dependent components of components in root project 'RxJavaDemo'. [incubating]  
help - Displays a help message.  
model - Displays the configuration model of root project 'RxJavaDemo'. [incubating]  
projects - Displays the sub-projects of root project 'RxJavaDemo'.  
properties - Displays the properties of root project 'RxJavaDemo'.  
tasks - Displays the tasks runnable from root project 'RxJavaDemo' (some of the displayed tasks may belong to subprojects).  
  
Install tasks  
-------------  
installDebug - Installs the Debug build.  
installDebugAndroidTest - Installs the android (on device) tests for the Debug build.  
uninstallAll - Uninstall all applications.  
uninstallDebug - Uninstalls the Debug build.  
uninstallDebugAndroidTest - Uninstalls the android (on device) tests for the Debug build.  
uninstallRelease - Uninstalls the Release build.  
  
Verification tasks  
------------------  
check - Runs all checks.  
connectedAndroidTest - Installs and runs instrumentation tests for all flavors on connected devices.  
connectedCheck - Runs all device checks on currently connected devices.  
connectedDebugAndroidTest - Installs and runs the tests for debug on connected devices.  
deviceAndroidTest - Installs and runs instrumentation tests using all Device Providers.  
deviceCheck - Runs all device checks using Device Providers and Test Servers.  
lint - Runs lint on all variants.  
lintDebug - Runs lint on the Debug build.  
lintRelease - Runs lint on the Release build.  
lintVitalRelease - Runs lint on just the fatal issues in the release build.  
test - Run unit tests for all variants.  
testDebugUnitTest - Run unit tests for the debug build.  
testReleaseUnitTest - Run unit tests for the release build.  
```  
  
## 获取 task 执行过程的详细信息  
可以使用以下几个参数中的一个【--stacktrace】【--info】【--debug】【--scan】    
例如【gradlew assembleDebug --stacktrace】可以获取 build 过程的详细信息    
例如【gradlew processDebugManifest --stacktrace】可以获取 Manifest 检查、合并过程的详细信息    
  
一般用来处理在build时报错但有没有提示具体是什么错时使用，根据经验，通过这种方式基本能获取非常精确的错误信息。  
> 第一次使用(并非是整个PC环境的第一次使用，往往是你AS每次启动后的第一次使用)的时候可能非常慢，因为需要下载大量的文件，不过一般都是正常下载的，应该很少出现下一半下载失败的问题。  
  
2018-8-20  
