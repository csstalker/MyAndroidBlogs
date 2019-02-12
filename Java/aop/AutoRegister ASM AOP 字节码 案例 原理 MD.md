| Markdown版本笔记 | 我的GitHub首页 | 我的博客 | 我的微信 | 我的邮箱 |  
| :------------: | :------------: | :------------: | :------------: | :------------: |  
| [MyAndroidBlogs][Markdown] | [baiqiantao][GitHub] | [baiqiantao][博客] | bqt20094 | baiqiantao@sina.com |  
  
[Markdown]:https://github.com/baiqiantao/MyAndroidBlogs  
[GitHub]:https://github.com/baiqiantao  
[博客]:http://www.cnblogs.com/baiqiantao/  
  
AutoRegister ASM AOP 字节码 案例 原理   
***  
目录  
===  

- [AutoRegister 自动注册插件简介](#autoregister-自动注册插件简介)
- [使用案例](#使用案例)
	- [配置](#配置)
	- [测试一](#测试一)
	- [测试二](#测试二)
- [原理](#原理)
	- [前言](#前言)
	- [准备工作](#准备工作)
	- [实现过程](#实现过程)
		- [构建插件工程](#构建插件工程)
		- [添加类扫描相关的代码](#添加类扫描相关的代码)
		- [记录目标类所在的文件](#记录目标类所在的文件)
		- [修改目标类的字节码](#修改目标类的字节码)
		- [接收并处理扩展参数](#接收并处理扩展参数)
  
# AutoRegister 自动注册插件简介  
[GitHub](https://github.com/luckybilly/AutoRegister)  
  
此插件要解决的问题是：  
- 工厂模式中工厂创建的`产品`需要注册到工厂类中，每新增一个产品要修改一次工厂类的代码  
- 策略模式中所有`策略`需要注册到管理类中，每新增一种策略需要修改一次管理类的代码  
- 接口及其管理类在基础库中定义，其`实现类`在不同的module中，需要在主工程中进行注册，每新增一个实现类注册需要手动添加  
  
> 比如在做`组件化`开发时，组件注册到主 app 中需要在主 app 的代码中 import 组件类进行注册，代码侵入性强  
  
使用此插件后，**在编译期（代码混淆之前）扫描所有打到apk包中的类**，将`符合条件`的类收集起来，并生成注册代码到指定的类的 static 块中，自动完成注册  
  
配置项：  
- `scanInterface`         : (必须)字符串，完整接口名，所有直接实现此接口的类将会被收集  
- `codeInsertToClassName` : (必须)字符串，完整类名，通过编译时生成代码的方式将收集到的类注册到此类的 codeInsertToMethodName 方法中  
- `registerMethodName`    : (必须)字符串，方法名，静态方法，方法的参数为 scanInterface  
- `codeInsertToMethodName`: 字符串，方法名，注册代码将插入到此方法中。若未指定，则默认为 static 块  
- `scanSuperClasses`      : 字符串或字符串数组，完整类名，所有直接继承此类的子类将会被收集  
- `include`               : 数组，需要扫描的类，正则表达式，包分隔符用/代替，默认为所有的类  
- `exclude`               : 数组，不需要扫描的类，正则表达式，包分隔符用/代替，默认不排除任何类  
  
功能：  
- 在apk打包过程中，对编译后的所有 class 进行扫描，将 `scanInterface` 的实现类 或 `scanSuperClasses` 的子类扫描出来，并在 `codeInsertToClassName` 类的 static 块 或 `codeInsertToMethodName` 方法中生成注册代码  
- 生成的代码为： `codeInsertToClassName.registerMethodName(scanInterface)`  
- `codeInsertToMethodName` 与 `registerMethodName` 需要同为 static 或非 static  
- 扫描到的类不能为接口或抽象类，并且提供 public 的无参构造方法（abstract 类或 scanInterface 的子接口需要添加到  exclude 中）  
- 扫描的类包含：通过 maven 依赖的库、module 依赖的 library、aar包、jar包、AIDL编译后的类及当前module中的java类  
  
# 使用案例  
## 配置  
1、在工程根目录的`build.gradle`中添加依赖：  
```groovy  
buildscript {  
    dependencies {  
        classpath 'com.android.tools.build:gradle:3.0.0'  
        classpath 'com.billy.android:autoregister:1.4.1' //添加  
    }  
}  
```  
  
2、在 app 的`build.gradle`中添加配置信息：  
```groovy  
apply plugin: 'auto-register'  
project.ext.registerInfoList = [  
        [  
                'scanInterface'          : 'com.billy.app_lib_interface.ICategory'  
                , 'scanSuperClasses'     : ['com.billy.android.autoregister.demo.BaseCategory']  
                , 'codeInsertToClassName': 'com.billy.app_lib_interface.CategoryManager'  
                , 'registerMethodName'   : 'register' //未指定 codeInsertToMethodName，默认插入到static块中，故此处register必须为static方法  
                , 'exclude'              : [  
                //scanSuperClasses 会自动被加入到exclude中，下面的exclude只作为演示，其实可以不用手动添加  
                'com.billy.android.autoregister.demo.BaseCategory'.replaceAll('\\.', '/') //排除这个基类  
                ]  
        ],  
        [  
                'scanInterface'           : 'com.billy.app_lib.IOther'  
                , 'codeInsertToClassName' : 'com.billy.app_lib.OtherManager'  
                , 'codeInsertToMethodName': 'init' //非static方法  
                , 'registerMethodName'    : 'registerOther' //非static方法  
        ]  
]  
  
autoregister {  
    registerInfo = registerInfoList  
    cacheEnabled = true  
}  
```  
  
## 测试一  
1、app 模块中相关的Java类：  
```java  
public abstract class BaseCategory implements ICategory {}  
public class CategoryB extends BaseCategory {} //实现类  
```  
  
2、app 依赖的 app_lib 模块中相关的Java类：  
```java  
public class CategoryA implements ICategory {} //实现类  
```  
  
3、app_lib 依赖的 app_lib_interface 模块中相关的Java类：  
```java  
public interface ICategory {} //接口  
```  
  
```java  
public class CategoryManager {  
    private static HashMap<String, ICategory> CATEGORIES = new HashMap<>();  
      
    static void register(ICategory category) {  
        if (category != null) {  
            CATEGORIES.put(category.getName(), category);  
        }  
    }  
}  
```  
  
4、相关配置为：  
- `scanInterface`          : `com.billy.app_lib_interface.ICategory`  
- `scanSuperClasses`     : [`com.billy.android.autoregister.demo.BaseCategory`]  
- `codeInsertToClassName`: `com.billy.app_lib_interface.CategoryManager`  
- `registerMethodName`   : `register`  
  
5、打包 APK 后反编译 `CategoryManager` 后发现其多了如下一个 static 代码块：  
```java  
static {  
    register(new CategoryA()); //自动添加注册的方法  
    register(new CategoryB());  
}  
```  
  
![](index_files/b1fb111d-9c7a-462d-a8ee-e844493604ca.jpg)  
  
可以发现，虽然在编译时 app_lib_interface 模块中的`CategoryManager`不能直接引用 app_lib 模块中的 `CategoryA` 和 app 模块中的 `CategoryB` ，但是在编译后进行打包时，各个模块之间的类就可以相互引用了。  
  
PS：编译后生成的 class 文件中是没有添加注册的代码的  
![](index_files/9c25cd31-53c6-455f-b69a-e38322f7bc29.png)  
  
## 测试二  
1、app 模块中相关的Java类：  
```java  
public class OtherB implements IOther {}  
```  
  
2、app 依赖的 app_lib 模块中相关的Java类：  
```java  
public interface IOther {}  
public class OtherA implements IOther {}  
```  
  
```java  
public class OtherManager {  
    private final List<IOther> LIST = new ArrayList<>();  
  
    public OtherManager() {  
        init();  
    }  
  
    private void init () {  
    }  
  
    private void registerOther(IOther other) {  
        if (other != null) {  
            LIST.add(other);  
        }  
    }  
}  
```  
  
3、相关配置为：  
- `scanInterface`         : `com.billy.app_lib.IOther`  
- `codeInsertToClassName` : `com.billy.app_lib.OtherManager`  
- `codeInsertToMethodName`: `init`  
- `registerMethodName`    : `registerOther`  
  
4、打包 APK 后反编译 `OtherManager` 后发现其 `init` 方法多了如下代码：  
生成的代码为： `codeInsertToClassName.registerMethodName(scanInterface)`  
```java  
private void init() {  
    registerOther(new OtherA());  
    registerOther(new OtherB());  
}  
```  
  
![](index_files/569ce4c8-80e8-4433-a1c9-8c06d41b3cbe.jpg)  
  
# 原理  
[原文](https://juejin.im/post/5a2b95b96fb9a045284669a9)  
  
在编译时，扫描即将打包到apk中的所有类，将所有组件类收集起来，通过`修改字节码`的方式生成注册代码到组件管理类中，从而实现编译时自动注册的功能，不用再关心项目中有哪些组件类了。   
  
特点：  
- 不需要注解，不会增加新的类  
- 性能高，不需要反射，运行时直接调用组件的构造方法  
- 能扫描到所有类，不会出现遗漏  
- 支持分级按需加载功能的实现  
  
## 前言  
最近在公司做android组件化开发框架的搭建，采用组件总线的方式进行通信：提供一个基础库，各组件（IComponent接口的实现类）都注册到组件管理类（组件总线：ComponentManager）中，组件之间在同一个app内时，通过ComponentManager转发调用请求来实现通信。  
  
但在实现过程中遇到了一个问题：**如何将不同module中的组件类自动注册到ComponentManager中**？  
  
目前市面上比较常用的解决方案是使用`annotationProcessor`：通过`编译时注解`动态生成组件映射表代码的方式来实现。但尝试过后发现有问题，因为编译时注解的特性只在`源码编译`时生效，`无法扫描到aar包里的注解`（project依赖、maven依赖均无效），也就是说必须每个module编译时生成自己的代码，然后要想办法将这些分散在各aar种的类找出来进行集中注册。  
  
ARouter的解决方案是：  
*   每个module都生成自己的java类，这些类的包名都是`com.alibaba.android.arouter.routes`  
*   然后在`运行时`通过读取每个dex文件中的这个包下的所有类通过`反射`来完成映射表的注册，详见 [ClassUtils.java](https://github.com/alibaba/ARouter/blob/master/arouter-api/src/main/java/com/alibaba/android/arouter/utils/ClassUtils.java) 源码  
  
运行时通过读取所有dex文件遍历每个entry查找指定包内的所有类名，然后反射获取类对象。这种效率看起来并不高。_  
  
ActivityRouter的解决方案是：  
*   在主app module中有一个`@Modules({"app", "sdk"})`注解用来标记当前 app 内有多少组件，根据这个注解生成一个`RouterInit`类  
*   在`RouterInit`类的 init 方法中生成调用同一个包内的`RouterMapping_app.map`  
*   每个 module 生成的类(RouterMapping_app.java 和 RouterMapping_sdk.java)都放在`com.github.mzule.activityrouter.router`包内（在不同的 aar 中，但包名相同）  
*   在 RouterMapping_sdk 类的 map() 方法中根据扫描到的当前module内所有路由注解，生成了调用`Routers.map(...)`方法来注册路由的代码  
*   在Routers的所有api接口中最终都会触发`RouterInit.init()`方法，从而实现所有路由的映射表注册  
  
这种方式用一个RouterInit类组合了所有module中的路由映射表类，运行时效率比扫描所有dex文件的方式要高，但需要额外在主工程代码中维护一个组件名称列表注解: `@Modules({"app", "sdk"})`_  
  
**有没有一种方式可以更高效地管理这个列表呢？**  
  
联想到之前用ASM框架自动生成代码的方式做了个 [AndAop插件](https://github.com/luckybilly/AndAOP) 用于`自动插入指定代码到任意类的任意方法中`，于是写了一个自动生成注册组件的gradle插件。  
  
大致思路是：在编译时，扫描所有类，将符合条件的类收集起来，并通过修改字节码生成注册代码到指定的管理类中，从而实现编译时自动注册的功能，不用再关心项目中有哪些组件类了。不会增加新的class，不需要反射，运行时直接调用组件的构造方法。  
  
性能方面：由于使用效率更高的ASM框架来进行字节码分析和修改，并过滤掉`android/support`包中的所有类(还支持设置自定义的扫描范围），经公司项目实测，未代码混淆前所有dex文件总计12MB左右，扫描及代码插入的总耗时在`2s-3s`之间，相对于整个apk打包所花3分钟左右的时间来说可以忽略不计(运行环境：MacBookPro 15吋高配 Mid 2015)。  
  
开发完成后，考虑到这个功能的通用性，于是升级组件扫描注册插件为通用的自动注册插件 [AutoRegister](https://github.com/luckybilly/AutoRegister)，支持配置多种类型的扫描注册，此插件现已用到组件化开发框架: [CC](https://github.com/luckybilly/CC) 中  
  
升级后，AutoRegister插件的完整功能描述是：  
> 在编译期扫描即将打包到apk中的所有类，并将指定接口的实现类(或指定类的子类)通过字节码操作自动注册到对应的管理类中。尤其适用于命令模式或策略模式下的映射表生成。  
  
在组件化开发框架中，可有助于实现分级按需加载的功能：  
*   在组件管理类中生成组件自动注册的代码  
*   在组件框架第一次被调用时加载此注册表  
*   若组件中有很多功能提供给外部调用，可以将这些功能包装成多个Processor，并将它们自动注册到组件中进行管理  
*   组件被初次调用时再加载这些Processor  
  
## 准备工作  
准备工作  
- 了解`groovy`、`gradle`的基础知识  
- 知道 [如何使用Android Studio开发Gradle插件](http://blog.csdn.net/sbsujjbcy/article/details/50782830)  
- 了解 `Transform API`：Transform API 是从Gradle 1.5.0版本之后提供的，它允许第三方在打包Dex文件之前的编译过程中修改java字节码；自定义插件注册的transform会在`ProguardTransform`和`DexTransform`之前执行，所以自动注册的类不需要考虑混淆的情况  
- 了解字节码修改框架（相比于Javassist框架，ASM较难上手，但性能更高）  
  
## 实现过程  
### 构建插件工程  
按照[如何使用Android Studio开发Gradle插件](http://blog.csdn.net/sbsujjbcy/article/details/50782830)文章中的方法创建好插件工程并发布到本地maven仓库（我是放在工程根目录下的一个文件夹中），这样我们就可以在本地快速调试了  
  
build.gradle文件的部分内容如下：  
```groovy  
apply plugin: 'groovy'  
apply plugin: 'maven'  
  
dependencies {  
    compile gradleApi()  
    compile localGroovy()  
}  
  
repositories {  
    mavenCentral()  
}  
dependencies {  
    compile 'com.android.tools.build:gradle:2.2.0'  
}  
  
//加载本地maven私服配置（在工程根目录中的local.properties文件中进行配置）  
Properties properties = new Properties()  
properties.load(project.rootProject.file('local.properties').newDataInputStream())  
def artifactory_user = properties.getProperty("artifactory_user")  
def artifactory_password = properties.getProperty("artifactory_password")  
def artifactory_contextUrl = properties.getProperty("artifactory_contextUrl")  
def artifactory_snapshot_repoKey = properties.getProperty("artifactory_snapshot_repoKey")  
def artifactory_release_repoKey = properties.getProperty("artifactory_release_repoKey")  
  
def maven_type_snapshot = true  
// 项目引用的版本号，比如compile 'com.yanzhenjie:andserver:1.0.1'中的1.0.1就是这里配置的。  
def artifact_version='1.0.1'  
// 唯一包名，比如compile 'com.yanzhenjie:andserver:1.0.1'中的com.yanzhenjie就是这里配置的。  
def artifact_group = 'com.billy.android'  
def artifact_id = 'autoregister'  
def debug_flag = true //true: 发布到本地maven仓库， false： 发布到maven私服  
  
task sourcesJar(type: Jar) {  
    from project.file('src/main/groovy')  
    classifier = 'sources'  
}  
  
artifacts {  
    archives sourcesJar  
}  
uploadArchives {  
    repositories {  
        mavenDeployer {  
            //deploy到maven仓库  
            if (debug_flag) {  
                repository(url: uri('../repo-local')) //deploy到本地仓库  
            } else {//deploy到maven私服中  
                def repoKey = maven_type_snapshot ? artifactory_snapshot_repoKey : artifactory_release_repoKey  
                repository(url: "${artifactory_contextUrl}/${repoKey}") {  
                    authentication(userName: artifactory_user, password: artifactory_password)  
                }  
            }  
  
            pom.groupId = artifact_group  
            pom.artifactId = artifact_id  
            pom.version = artifact_version + (maven_type_snapshot ? '-SNAPSHOT' : '')  
  
            pom.project {  
                licenses {  
                    license {  
                        name 'The Apache Software License, Version 2.0'  
                        url 'http://www.apache.org/licenses/LICENSE-2.0.txt'  
                    }  
                }  
            }  
        }  
    }  
}  
```  
  
根目录的build.gradle文件中要添加本地仓库的地址及dependencies  
```groovy  
buildscript {  
    repositories {  
        maven{ url rootProject.file("repo-local") }  
        maven { url 'http://maven.aliyun.com/nexus/content/groups/public/' }  
        google()  
        jcenter()  
    }  
    dependencies {  
        classpath 'com.android.tools.build:gradle:3.0.1'  
        classpath 'com.github.dcendents:android-maven-gradle-plugin:1.4.1'  
        classpath 'com.billy.android:autoregister:1.4.1'  
    }  
}  
```  
  
### 添加类扫描相关的代码  
在Transform类的transform方法中添加类扫描相关的代码  
```groovy  
// 遍历输入文件  
inputs.each { TransformInput input ->  
    // 遍历jar  
    input.jarInputs.each { JarInput jarInput ->  
        String destName = jarInput.name  
        // 重名名输出文件,因为可能同名,会覆盖  
        def hexName = DigestUtils.md5Hex(jarInput.file.absolutePath)  
        if (destName.endsWith(".jar")) {  
            destName = destName.substring(0, destName.length() - 4)  
        }  
        // 获得输入文件  
        File src = jarInput.file  
        // 获得输出文件  
        File dest = outputProvider.getContentLocation(destName + "_" + hexName, jarInput.contentTypes, jarInput.scopes, Format.JAR)  
  
        //遍历jar的字节码类文件，找到需要自动注册的component  
        if (CodeScanProcessor.shouldProcessPreDexJar(src.absolutePath)) {  
            CodeScanProcessor.scanJar(src, dest)  
        }  
        FileUtils.copyFile(src, dest)  
  
        project.logger.info "Copying\t${src.absolutePath} \nto\t\t${dest.absolutePath}"  
    }  
  
    // 遍历目录  
    input.directoryInputs.each { DirectoryInput directoryInput ->  
        // 获得产物的目录  
        File dest = outputProvider.getContentLocation(directoryInput.name, directoryInput.contentTypes, directoryInput.scopes, Format.DIRECTORY)  
        String root = directoryInput.file.absolutePath  
        if (!root.endsWith(File.separator)) root += File.separator  
        //遍历目录下的每个文件  
        directoryInput.file.eachFileRecurse { File file ->  
            def path = file.absolutePath.replace(root, '')  
            if(file.isFile()){  
                CodeScanProcessor.checkInitClass(path, new File(dest.absolutePath + File.separator + path))  
                if (CodeScanProcessor.shouldProcessClass(path)) {  
                    CodeScanProcessor.scanClass(file)  
                }  
            }  
        }  
        project.logger.info "Copying\t${directoryInput.file.absolutePath} \nto\t\t${dest.absolutePath}"  
        // 处理完后拷到目标文件  
        FileUtils.copyDirectory(directoryInput.file, dest)  
    }  
}  
```  
  
CodeScanProcessor是一个工具类，其中`CodeScanProcessor.scanJar(src, dest)`和`CodeScanProcessor.scanClass(file)`分别是用来扫描jar包和class文件的，扫描的原理是利用ASM的`ClassVisitor`来查看每个类的父类类名及所实现的接口名称，与配置的信息进行比较，如果符合我们的过滤条件，则记录下来，在全部扫描完成后将调用这些类的无参构造方法进行注册  
```Java  
static void scanClass(InputStream inputStream) {  
    ClassReader cr = new ClassReader(inputStream)  
    ClassWriter cw = new ClassWriter(cr, 0)  
    ScanClassVisitor cv = new ScanClassVisitor(Opcodes.ASM5, cw)  
    cr.accept(cv, ClassReader.EXPAND_FRAMES)  
    inputStream.close()  
}  
  
static class ScanClassVisitor extends ClassVisitor {  
    ScanClassVisitor(int api, ClassVisitor cv) {  
        super(api, cv)  
    }  
    void visit(int version, int access, String name, String signature,  
               String superName, String[] interfaces) {  
        super.visit(version, access, name, signature, superName, interfaces)  
        RegisterTransform.infoList.each { ext ->  
            if (shouldProcessThisClassForRegister(ext, name)) {  
                if (superName != 'java/lang/Object' && !ext.superClassNames.isEmpty()) {  
                    for (int i = 0; i < ext.superClassNames.size(); i++) {  
                        if (ext.superClassNames.get(i) == superName) {  
                            ext.classList.add(name)  
                            return  
                        }  
                    }  
                }  
                if (ext.interfaceName && interfaces != null) {  
                    interfaces.each { itName ->  
                        if (itName == ext.interfaceName) {  
                            ext.classList.add(name)  
                        }  
                    }  
                }  
            }  
        }  
  
    }  
}  
```  
  
### 记录目标类所在的文件  
记录目标类所在的文件，因为我们接下来要修改其字节码，将注册代码插入进去  
```java  
 static void checkInitClass(String entryName, File file) {  
     if (entryName == null || !entryName.endsWith(".class")) return  
     entryName = entryName.substring(0, entryName.lastIndexOf('.'))  
     RegisterTransform.infoList.each { ext ->  
         if (ext.initClassName == entryName) ext.fileContainsInitClass = file  
     }  
 }  
```  
  
### 修改目标类的字节码  
扫描完成后，开始修改目标类的字节码(使用ASM的MethodVisitor来修改目标类指定方法，若未指定则默认为static块，即`<clinit>`方法)，生成的代码是直接调用扫描到的类的无参构造方法，**并非通过反射**  
  
*   class文件： 直接修改此字节码文件（其实是重新生成一个class文件并替换掉原来的文件）  
*   jar文件：复制此jar文件，找到jar包中目标类所对应的JarEntry，修改其字节码，然后替换原来的jar文件  
  
```java  
class CodeInsertProcessor {  
    RegisterInfo extension  
  
    private CodeInsertProcessor(RegisterInfo extension) {  
        this.extension = extension  
    }  
  
    static void insertInitCodeTo(RegisterInfo extension) {  
        if (extension != null && !extension.classList.isEmpty()) {  
            CodeInsertProcessor processor = new CodeInsertProcessor(extension)  
            File file = extension.fileContainsInitClass  
            if (file.getName().endsWith('.jar'))  
                processor.insertInitCodeIntoJarFile(file)  
            else  
                processor.insertInitCodeIntoClassFile(file)  
        }  
    }  
  
    //处理jar包中的class代码注入  
    private File insertInitCodeIntoJarFile(File jarFile) {  
        if (jarFile) {  
            def optJar = new File(jarFile.getParent(), jarFile.name + ".opt")  
            if (optJar.exists())  
                optJar.delete()  
            def file = new JarFile(jarFile)  
            Enumeration enumeration = file.entries()  
            JarOutputStream jarOutputStream = new JarOutputStream(new FileOutputStream(optJar))  
  
            while (enumeration.hasMoreElements()) {  
                JarEntry jarEntry = (JarEntry) enumeration.nextElement()  
                String entryName = jarEntry.getName()  
                ZipEntry zipEntry = new ZipEntry(entryName)  
                InputStream inputStream = file.getInputStream(jarEntry)  
                jarOutputStream.putNextEntry(zipEntry)  
                if (isInitClass(entryName)) {  
                    println('codeInsertToClassName:' + entryName)  
                    def bytes = referHackWhenInit(inputStream)  
                    jarOutputStream.write(bytes)  
                } else {  
                    jarOutputStream.write(IOUtils.toByteArray(inputStream))  
                }  
                inputStream.close()  
                jarOutputStream.closeEntry()  
            }  
            jarOutputStream.close()  
            file.close()  
  
            if (jarFile.exists()) {  
                jarFile.delete()  
            }  
            optJar.renameTo(jarFile)  
        }  
        return jarFile  
    }  
  
    boolean isInitClass(String entryName) {  
        if (entryName == null || !entryName.endsWith(".class"))  
            return false  
        if (extension.initClassName) {  
            entryName = entryName.substring(0, entryName.lastIndexOf('.'))  
            return extension.initClassName == entryName  
        }  
        return false  
    }  
  
    /**  
     * 处理class的注入  
     * @param file class文件  
     * @return 修改后的字节码文件内容  
     */  
    private byte[] insertInitCodeIntoClassFile(File file) {  
        def optClass = new File(file.getParent(), file.name + ".opt")  
  
        FileInputStream inputStream = new FileInputStream(file)  
        FileOutputStream outputStream = new FileOutputStream(optClass)  
  
        def bytes = referHackWhenInit(inputStream)  
        outputStream.write(bytes)  
        inputStream.close()  
        outputStream.close()  
        if (file.exists()) {  
            file.delete()  
        }  
        optClass.renameTo(file)  
        return bytes  
    }  
  
    //refer hack class when object init  
    private byte[] referHackWhenInit(InputStream inputStream) {  
        ClassReader cr = new ClassReader(inputStream)  
        ClassWriter cw = new ClassWriter(cr, 0)  
        ClassVisitor cv = new MyClassVisitor(Opcodes.ASM5, cw)  
        cr.accept(cv, ClassReader.EXPAND_FRAMES)  
        return cw.toByteArray()  
    }  
  
    class MyClassVisitor extends ClassVisitor {  
  
        MyClassVisitor(int api, ClassVisitor cv) {  
            super(api, cv)  
        }  
  
        void visit(int version, int access, String name, String signature,  
                   String superName, String[] interfaces) {  
            super.visit(version, access, name, signature, superName, interfaces)  
        }  
        @Override  
        MethodVisitor visitMethod(int access, String name, String desc,  
                                  String signature, String[] exceptions) {  
            MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions)  
            if (name == extension.initMethodName) { //注入代码到指定的方法之中  
                boolean _static = (access & Opcodes.ACC_STATIC) > 0  
                mv = new MyMethodVisitor(Opcodes.ASM5, mv, _static)  
            }  
            return mv  
        }  
    }  
  
    class MyMethodVisitor extends MethodVisitor {  
        boolean _static;  
  
        MyMethodVisitor(int api, MethodVisitor mv, boolean _static) {  
            super(api, mv)  
            this._static = _static;  
        }  
  
        @Override  
        void visitInsn(int opcode) {  
            if ((opcode >= Opcodes.IRETURN && opcode <= Opcodes.RETURN)) {  
                extension.classList.each { name ->  
                    if (!_static) {  
                        //加载this  
                        mv.visitVarInsn(Opcodes.ALOAD, 0)  
                    }  
                    //用无参构造方法创建一个组件实例  
                    mv.visitTypeInsn(Opcodes.NEW, name)  
                    mv.visitInsn(Opcodes.DUP)  
                    mv.visitMethodInsn(Opcodes.INVOKESPECIAL, name, "<init>", "()V", false)  
                    //调用注册方法将组件实例注册到组件库中  
                    if (_static) {  
                        mv.visitMethodInsn(Opcodes.INVOKESTATIC  
                                , extension.registerClassName  
                                , extension.registerMethodName  
                                , "(L${extension.interfaceName};)V"  
                                , false)  
                    } else {  
                        mv.visitMethodInsn(Opcodes.INVOKESPECIAL  
                                , extension.registerClassName  
                                , extension.registerMethodName  
                                , "(L${extension.interfaceName};)V"  
                                , false)  
                    }  
                }  
            }  
            super.visitInsn(opcode)  
        }  
        @Override  
        void visitMaxs(int maxStack, int maxLocals) {  
            super.visitMaxs(maxStack + 4, maxLocals)  
        }  
    }  
}  
```  
  
### 接收并处理扩展参数  
接收扩展参数，获取需要扫描类的特征及需要插入的代码  
  
找了很久没找到gradle插件接收自定义对象数组扩展参数的方法，于是退一步改用`List<Map>`接收后再进行转换的方式来实现，以此来接收多个扫描任务的扩展参数  
  
```java  
import org.gradle.api.Project  
  
class AutoRegisterConfig {  
  
    public ArrayList<Map<String, Object>> registerInfo = []  
  
    ArrayList<RegisterInfo> list = new ArrayList<>()  
  
    Project project  
  
    AutoRegisterConfig(){}  
  
    void convertConfig() {  
        registerInfo.each { map ->  
            RegisterInfo info = new RegisterInfo()  
            info.interfaceName = map.get('scanInterface')  
            def superClasses = map.get('scanSuperClasses')  
            if (!superClasses) {  
                superClasses = new ArrayList<String>()  
            } else if (superClasses instanceof String) {  
                ArrayList<String> superList = new ArrayList<>()  
                superList.add(superClasses)  
                superClasses = superList  
            }  
            info.superClassNames = superClasses  
            info.initClassName = map.get('codeInsertToClassName') //代码注入的类  
            info.initMethodName = map.get('codeInsertToMethodName') //代码注入的方法（默认为static块）  
            info.registerMethodName = map.get('registerMethodName') //生成的代码所调用的方法  
            info.registerClassName = map.get('registerClassName') //注册方法所在的类  
            info.include = map.get('include')  
            info.exclude = map.get('exclude')  
            info.init()  
            if (info.validate())  
                list.add(info)  
            else {  
                project.logger.error('auto register config error: scanInterface, codeInsertToClassName and registerMethodName should not be null\n' + info.toString())  
            }  
  
        }  
    }  
}  
```  
  
封装可配置参数  
```java  
import java.util.regex.Pattern  
  
class RegisterInfo {  
    static final DEFAULT_EXCLUDE = [  
            '.*/R(\\$[^/]*)?'  
            , '.*/BuildConfig$'  
    ]  
    //以下是可配置参数  
    String interfaceName = ''  
    ArrayList<String> superClassNames = []  
    String initClassName = ''  
    String initMethodName = ''  
    String registerClassName = ''  
    String registerMethodName = ''  
    ArrayList<String> include = []  
    ArrayList<String> exclude = []  
  
    //以下不是可配置参数  
    ArrayList<Pattern> includePatterns = []  
    ArrayList<Pattern> excludePatterns = []  
    File fileContainsInitClass //initClassName的class文件或含有initClassName类的jar文件  
    ArrayList<String> classList = new ArrayList<>()  
  
    RegisterInfo(){}  
  
    boolean validate() {  
        return interfaceName && registerClassName && registerMethodName  
    }  
  
  
    void init() {  
        if (include == null) include = new ArrayList<>()  
        if (include.empty) include.add(".*") //如果没有设置则默认为include所有  
        if (exclude == null) exclude = new ArrayList<>()  
        if (!registerClassName)  
            registerClassName = initClassName  
  
        //将interfaceName中的'.'转换为'/'  
        if (interfaceName)  
            interfaceName = convertDotToSlash(interfaceName)  
        //将superClassName中的'.'转换为'/'  
        if (superClassNames == null) superClassNames = new ArrayList<>()  
        for (int i = 0; i < superClassNames.size(); i++) {  
            def superClass = convertDotToSlash(superClassNames.get(i))  
            superClassNames.set(i, superClass)  
            if (!exclude.contains(superClass))  
                exclude.add(superClass)  
        }  
        //interfaceName添加到排除项  
        if (!exclude.contains(interfaceName))  
            exclude.add(interfaceName)  
        //注册和初始化的方法所在的类默认为同一个类  
        initClassName = convertDotToSlash(initClassName)  
        //默认插入到static块中  
        if (!initMethodName)  
            initMethodName = "<clinit>"  
        registerClassName = convertDotToSlash(registerClassName)  
        //添加默认的排除项  
        DEFAULT_EXCLUDE.each { e ->  
            if (!exclude.contains(e))  
                exclude.add(e)  
        }  
        initPattern(include, includePatterns)  
        initPattern(exclude, excludePatterns)  
    }  
  
    private static String convertDotToSlash(String str) {  
        return str ? str.replaceAll('\\.', '/').intern() : str  
    }  
  
    private static void initPattern(ArrayList<String> list, ArrayList<Pattern> patterns) {  
        list.each { s ->  
            patterns.add(Pattern.compile(s))  
        }  
    }  
}  
```  
  
最后，在主app module的build.gradle文件中添加扩展参数即可。  
  
2019-1-9  
