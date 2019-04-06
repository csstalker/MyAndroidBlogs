| Markdown版本笔记 | 我的GitHub首页 | 我的博客 | 我的微信 | 我的邮箱 |  
| :------------: | :------------: | :------------: | :------------: | :------------: |  
| [MyAndroidBlogs][Markdown] | [baiqiantao][GitHub] | [baiqiantao][博客] | bqt20094 | baiqiantao@sina.com |  
  
[Markdown]:https://github.com/baiqiantao/MyAndroidBlogs  
[GitHub]:https://github.com/baiqiantao  
[博客]:http://www.cnblogs.com/baiqiantao/  
  
AS 功能 设置 SDK AVD 简介 MD  
***  
目录  
===  

- [AS 基本使用](#AS-基本使用)
	- [安装及首次运行](#安装及首次运行)
	- [Android SDK Manager](#Android-SDK-Manager)
	- [模拟器加速器](#模拟器加速器)
	- [AVD Manager](#AVD-Manager)
	- [AS项目的目录结构](#AS项目的目录结构)
	- [AS中项目的展示方式](#AS中项目的展示方式)
- [AS 断点调试](#AS-断点调试)
	- [基本介绍](#基本介绍)
	- [上侧工具栏](#上侧工具栏)
	- [左侧工具栏](#左侧工具栏)
	- [变量面板](#变量面板)
- [模板 Templates](#模板-Templates)
	- [文件模板 File and Code Templates](#文件模板-File-and-Code-Templates)
		- [文件模板](#文件模板)
- [if (${PACKAGE_NAME} != "")package ${PACKAGE_NAME};#end](#if-PACKAGE_NAME--package-PACKAGE_NAMEend)
- [if (${IMPORT_BLOCK} != "")${IMPORT_BLOCK}](#if-IMPORT_BLOCK--IMPORT_BLOCK)
- [end](#end)
- [parse("File Header.java")](#parseFile-Headerjava)
- [if (${VISIBILITY} == "PUBLIC")public #end #if (${ABSTRACT} == "TRUE")abstract #end #if (${FINAL} == "TRUE")final #end class ${NAME} #if (${SUPERCLASS} != "")extends ${SUPERCLASS} #end #if (${INTERFACES} != "")implements ${INTERFACES} #end {](#if-VISIBILITY--PUBLICpublic-end-if-ABSTRACT--TRUEabstract-end-if-FINAL--TRUEfinal-end-class-NAME-if-SUPERCLASS--extends-SUPERCLASS-end-if-INTERFACES--implements-INTERFACES-end-)
		- [文件头 Includes【重要】](#文件头-Includes【重要】)
		- [预定义的变量](#预定义的变量)
	- [代码模板 Live Templates【重要】](#代码模板-Live-Templates【重要】)
- [AS 进行单元测试](#AS-进行单元测试)
	- [简介](#简介)
	- [Java测试用例](#Java测试用例)
	- [Android测试用例](#Android测试用例)
  
# AS 基本使用  
[AS下载地址](http://www.android-studio.org/index.php/download)  
[SDK下载地址](http://www.androiddevtools.cn/ )  
[SDK基础工具包](http://pan.baidu.com/s/1dEEoALN)  
[离线帮助文档](http://pan.baidu.com/s/1gfsja9t)  
[官方安卓示例离线版](http://pan.baidu.com/s/1nuUjIu1)  
[CPU虚拟化检测软件](http://pan.baidu.com/s/1c27HHFM )  
  
## 安装及首次运行  
安装AS前，请先安装`JDK 8`环境，并正确使用 `JAVA_HOME` 来配置环境变量  
  
安装时，注意选择 Android Studio 和 Android SDK 的安装目录时，尽量放在同一个位置，并保证磁盘空间足够大，因为后续这个目录占用的空间非常大（10G+）。另外路径中千万不要有中文。  
  
除此之外，安装过程没啥需要注意的，大约3分钟就可以安装完毕。  
  
首次运行前，要先做以下最重要的事情！  
在Android Studio安装目录下的 bin 目录下，找到 `idea.properties` 文件，在文件最后追加`disable.android.first.run=true`  
如果不这么搞的话，有人会卡很长时间，并最终弹出以下界面：  
![](index_files/5bfde492-7acb-4a9c-8ea4-9c34f9106112.png)  
  
出现这个问题原因就是：网络连接有问题（防火墙的问题）。我们可以通过以上配置跳过这一步。  
  
首次运行时，会让你选择是否导入Android Studio的配置文件，如果你以前使用过Android Studio，可以选择导入以前的版本。  
除此之外，也没啥可注意的。  
但是一定要有个心理准备，启动后会各种提示你更新AS、更新Gradle、更新SDK、更新......这一套下来，在网络不好的情况下，可能一天就过去了（毫不夸张）。  
  
## Android SDK Manager  
Android SDK Manager 是Android软件开发工具包的管理器，用于从服务器下载安卓开发所需工具到本地。  
AVD Manager 是Android虚拟驱动管理器，用来创建安卓模拟器，安卓模拟器所需的镜像也是通过SDK Manager来下载的。  
  
打开Android SDK Manager后，其列出的各工具作用如下：  
```  
━━Tools目录（必须的工具）：  
　　┣ ━━Android SDK Tools（必须，只需下载一个版本，一般选最新版本）：基础工具包，版本号带rc字样的是预览版。  
　　┣ ━━Android SDK Platform-tools（必须，只需下载一个版本，一般选最新版本）：从android2.3开始划出此目录，存放公用开发工具，比如adb、sqlite3等，被划分到了这里。  
　　┗━━ Android SDK Build-tools（必须，可以安装多个版本）：Android项目构建工具。  
━━Android xxx（API xx）目录（可选的各平台开发工具）：　  
　　┣ ━━Documentation for Android Sdk（可选）：安卓开发者官网的一些离线文档，不过下载下来打开也很慢，后面会提供另外一个离线版。  
　　┣ ━━SDK Platform（必须）：对应平台的开发工具，需要在哪个版本的平台下开发就下载哪个。  
　　┣ ━━Samples for SDK（可选，此项在高版本tools中已不提供，需要在IDE里通过Import Sample引入，当然也可以下载离线版）：内置的安卓示例程序，推荐安装。  
　　┣ ━━Sources for Android SDK（可选）：安卓API的源代码，推荐安装。  
　　┗━━ xxxxxxxx  Image（可选）：各个以Image结尾的东西是支持相应平台的模拟器（使用真机调试或使用其它模拟器的话不需要安装）  
━━Extras目录（可选的扩展）：  
　　┣ ━━Android Support Libraries（需要，高版本tools中已不见了，应该是集成到了别的地方）：在低版本平台实现高版本平台控件效果时提供支持。  
　　┣ ━━Android Support Repository（需要）：主要是方便在gradle中使用Android Support Libraries，因为Google并没有把这些库发布到maven center或者jcenter去，而是使用了Google自己的maven仓库。  
　　┗━━ Intel x86 Emulator Accelerator(HAXM installer)（一般需要，但需要CPU支持虚拟化技术支持）：windows平台的Intel x86模拟器加速工具，配合Intel x86 atom/atom_64 System Image使用可加快模拟器的运行速度。  
```  
  
>如果获取不到工具列表，或下载不了工具，请设置代理或翻墙，或百度一下……  
Google开头的文件一般用不到，而且使用需FQ。  
  
## 模拟器加速器  
最后那个模拟器加速器的使用，其只支持`Intel x86`架构系列模拟器，且下载后还需单独安装一次。  
首先下载个小软件 [CPU虚拟化检测软件](http://pan.baidu.com/s/1c27HHFM )，监测你的CPU是否支持加速，如果检测成功且已开启，则会提示：此处理器支持虚拟化技术，BIOS中已启用。  
  
如果提示你的CPU支持虚拟化但未开启，那么请自行开启虚拟化即可，至于如何开启，不同的主板进入BIOS的设置方式不一样，但都大同小异，自行搜索即可。  
  
注意，有些杀毒软件会利用CPU虚拟化技术对电脑进行防护，比如360的核晶防护功能，这会造成虚拟机启动失败。所以如果你有类似的软件开启了类似的功能，请先关闭此类防护功能并重启电脑。  
![](index_files/a5b0757d-ff3e-45fe-8f51-f843e431545397231736.jpg)  
  
如果Intel系列模拟器无法使用，就改用ARM模拟器。ARM模拟器缺点是慢....  
  
## AVD Manager  
模拟器器参数设置：  
![](index_files/9cb1e6a9-be4f-47df-bdd9-18ef9301545397231745.png)  
  
启动模拟器  
![](index_files/8524bea5-0a95-4e65-a57d-bcf03081545397231751.png)  
  
avd默认位置是在【C:\Users\用户名\.android】,更改AVD存储位置的方法：  
*   在你希望存放到的路径下建立【.android】文件夹，在.android文件夹下建立avd文件夹。  
*   可以将原路径下的.android文件夹及avd文件夹拷贝到新的路径下，将【.ini】文件下的原路径更改为新的路径。  
![](index_files/006ccc2d-cebf-493c-8d65-f6020a21545397231760.png)  
  
*   新建名称为【ANDROID_SDK_HOME】的系统环境变量，将值设为你刚才建立的路径。  
![](index_files/6e904708-0b89-41be-87e5-a58400e1545397231766.png)  
  
设置完后，打开AVD Manager-->Tools-->Optaions可以看到如下信息  
![](index_files/07343a73-0694-4072-b2c6-b7ae9091545397231772.png)  
  
## AS项目的目录结构  
新建工程项目后AS项目的目录结构如下所示：  
![](index_files/f1ce0150-22ae-42fe-8c60-2624d7a1545397231779.png)  
  
各目录（文件）的作用如下：  
![](index_files/1dedd4af-2e80-479a-99c5-3565fa21545397231785.png)  
  
app目录下各目录（文件）的作用如下：  
![](index_files/e2a3977d-84ef-4e56-9bac-ab0fa511545397231791.png)  
  
## AS中项目的展示方式  
- `Project方式`：用于浏览项目文件， Project 面板会显示当前的所有的 module，`android application module` 会显示一个手机图标，`android library module` 会显示一个书架图标；`java library module` 会显示一个咖啡图标。这种方式的展示更类似于Eclispe，开发时一般都是采用这种方式。  
- `Android方式`：以app形式展示，manifests文件夹存放清单文件。这种方式相比Project方式减少了很多层级，有利于快速找到java和res目录下的文件。这是新建一个工程后使用的默认的展示方式。  
- Project File方式：以和在window资源管理器中相同的样式展示。  
- Package方式：自己看图吧，感觉没啥卵用。  
- Production：产量，产品，生产，制作。没啥卵用。  
- Tests、Local Unit Tests、Android Instrument Tests：和测试相关的展示方式，平时没啥卵用  
  
# AS 断点调试   
## 基本介绍  
此种调试方式是通过冻结应用运行的状态，仿佛时间停止了一般，然后我们逐一观察此时程序的各个参数是否符合我们的预期。  
  
这种调试方法适用于对时间不敏感的程序。也就是说被调试的程序线程不需要依赖别的线程，即使暂时停止工作也不会影响别的工作线程或者受别的工作线程影响。  
  
在希望代码暂停运行的地方打断点，在代码前点击一下，出现一个红色的圆点，如果想取消，再点击一次即可。  
  
点击顶部工具栏图标开启调试模式。  
  
Frame区域是程序的方法调用栈区，这个区域中显示了程序执行到断点处所调用过的所有方法，越下面的方法被调用的越早。  
  
由此顺序可了解Android系统启动流程，也即是怎么一步步调用到Activity的onCreate方法的：  
![](index_files/a5f3c640-82bf-4058-916d-f6f3d8af8861.png)  
  
## 上侧工具栏  
![](index_files/52e26ba7-d277-40dd-95f1-6901f6722cf7.png)  
  
调试时的正确姿势是：  
- 如果想一步步监测程序的运行过程，就一直点击【step over】  
- 如果只想监测运行到某几行关键代码时的状态，就设置多个断点，然后一直点击【run to cursor】  
- 如果想看看某个方法里面的运行过程，就点击一次【step into】或【force step into】  
- 如果想跳出这个方法，就点击一次【step out】  
  
命令解释：  
- step over：【一步步往下走】程序向下执行一行，如果当前行有方法调用，这个方法将被执行完毕返回，然后到下一行。  
- step into：【看到方法往里走】程序向下执行一行，如果该行有自定义方法，则运行进入自定义方法（不会进入官方类库的方法）。  
- force step into：【所有方法看完整】能进入任何方法，这个可以看到你所调用的所有方法的实现，研究源码使用非常方便，平时使用不到。  
- step out：【下一个断点或跳出方法】  
    - 如果在调试的时候你进入了一个方法，并觉得该方法没有问题，你就可以使用 step out 跳出该方法，返回到该方法被调用处的下一行语句；注意该方法已执行完毕。  
    - 如果后面有断点则走到下一个断点；如果后面没有断点，而是在一个调用的方法当中，则会跳出这个方法，并继续走。  
- run to cursor：【下一个断点】和 step out 中的【下一个断点】功能相同，但是因为功能专一，所以不容易出现莫名其妙的跳转。  
- drop frame：【回退到上一个调用的方法】返回到当前方法的调用处重新执行，并且所有上下文变量的值也回到那个时候。只要调用链中还有上级方法，可以跳到其中的任何一个方法。所谓的Frame，其实对应的就是一次方法调用压栈的信息。  
  
## 左侧工具栏  
![](index_files/c9e9238b-79f2-49c7-8c13-4c138603fdec.png)  
  
- resume program：程序继续往下执行，直到遇到下一个断点（感觉和 run to cursor 没任何区别呀）  
- pause program：程序暂停往下执行  
- stop app：停止调试，App会关闭，你再次打开时不再是调试模式  
- view breakpoints：查看所有设置的断点及其位置，也可以设置断点的一些属性  
- mute breakpoints：暂时使所有断点失效  
- get thread dump：获取线程堆栈信息  
- restore layout：恢复调试版本为默认的布局  
  
## 变量面板  
1、直接在代码中就可以看到代码执行到当前位置时各个变量的值  
  
2、在Variables面板中也能查看  
![](index_files/22df2c41-3673-45ec-a003-e1be7cc22666.png)  
  
左边几个图标代表的功能分别是：添加、删除变量，上下移动变量的位置，复制(duplicate)，将要观察的变量放在另一个tab栏，如下效果：  
![](index_files/06522517-eae0-4a29-b8e6-d7aaf4799205.png)  
  
3、如果我们仅仅想观察指定几个变量的值的变化，可以点击Watches，点击＋号，然后输入变量的名称回车就OK了。  
- 如果变量名比较长我们可以选择Variables中的变量名然后右键，选择［Add to Watches]，然后Watches面板中就有了  
- 我们可以通过快速设置变量的值来加快调试速度，选择［Variables］中的变量名右键，选择［Set Value..]。  
- 比如，我们在一次100次的for循环中，想观察第90次循环后的执行情况，就可以将循环次数手动改为90，这样就不用将循环控制变量从0点到90了。  
  
# 模板 Templates  
## 文件模板 File and Code Templates  
文件模板：Settings –> Editor –>【File and Code Templates】，或者在右键new时选择子菜单【Edite File Templates...】  
  
### 文件模板  
–> 【Files】，可用来修改、添加新建某类型文件(比如Class、Interface、C++)时的文件模板  
此文件保存位置【C:\Users\Administrator\.AndroidStudio2.3\config\fileTemplates\internal】  
  
以下为新建Class文件的模板：  
```java  
#if (${PACKAGE_NAME} != "")package ${PACKAGE_NAME};#end  
  
#if (${IMPORT_BLOCK} != "")${IMPORT_BLOCK}  
#end  
#parse("File Header.java")  
  
#if (${VISIBILITY} == "PUBLIC")public #end #if (${ABSTRACT} == "TRUE")abstract #end #if (${FINAL} == "TRUE")final #end class ${NAME} #if (${SUPERCLASS} != "")extends ${SUPERCLASS} #end #if (${INTERFACES} != "")implements ${INTERFACES} #end {  
}  
```  
  
注意：上面代码中的【File Header.java】文件即为下面的文件头  
![](index_files/85ebe92e-e380-4de3-941e-281a1fdecc74.png)  
  
*This is a built-in template used each time you create a new Java class, by selecting New | Java Class | Class from the popup menu in one of the project views. The template is editable. Along with Java expressions and comments, you can also use predefined variables (listed below) that will then be expanded like macros into the corresponding values. It is also possible to specify an arbitrary number of custom variables in the format ${<VARIABLE_NAME>}. In this case, before the new file is created, you will be prompted with a dialog where you can define particular values for all custom variables. Using the #parse directive, you can include templates from the Includes tab, by specifying the full name of the desired template as a parameter in quotation marks. For example: #parse("File Header.java")*   
  
这是每次创建新的Java类时使用的内置模板，方法是选择New | Java类| 一个项目视图中的弹出菜单中的类。该模板是可编辑的。 除了Java表达式和注释外，您还可以使用预定义的变量（如下所示），然后将其扩展为宏类型到相应的值。 也可以以 $ {<VARIABLE_NAME>} 格式指定任意数量的自定义变量。 在这种情况下，在创建新文件之前，将出现一个对话框，您可以在其中定义所有自定义变量的特定值。 使用 #parse 指令，可以通过将引用中的参数指定为所需模板的全名，从 Includes 选项卡中包含模板。 例如：#parse("File Header.java")   
  
### 文件头 Includes【重要】  
–> 【Includes】，可添加、修改文件头  
此文件保存位置【C:\Users\Administrator\.AndroidStudio2.3\config\fileTemplates\includes】  
```java  
/**  
 * Desc：  
 * @author <a href="http://www.cnblogs.com/baiqiantao">白乾涛</a><p>  
 * @date ${DATE} ${HOUR}:${MINUTE} <p>  
 */  
```  
  
This is a built-in template 内置的模板. It contains a code fragment片段 that can be included into file templates (Templates tab) with the help of the #parse directive指令. The template is editable. Along with除了 static text, code and comments注释, you can also use predefined预定义的 variables变量 that will then be expanded like macros宏 into the corresponding相应的 values.  
  
### 预定义的变量  
Predefined variables will take the following values:  
```java  
${PACKAGE_NAME}    //name of the package in which the new file is created  
${USER}    //current user system login name  
${DATE}    //current system date  
${TIME}    //current system time  
${YEAR}    //current year  
${MONTH}    //current month  
${MONTH_NAME_SHORT}    //first 3 letters of the current month name. Example: Jan, Feb, etc.  
${MONTH_NAME_FULL}    //full name of the current month. Example: January, February, etc.  
${DAY}    //current day of the month  
${HOUR}    //current hour  
${MINUTE}    //current minute  
${PROJECT_NAME}    //the name of the current project  
```  
  
例如：`${DATE} ${HOUR}:${MINUTE}`的结果为`2017/9/20 10:28`  
  
## 代码模板 Live Templates【重要】  
Setting->Editor->【Live Templates】  
此文件保存位置【C:\Users\Administrator\.AndroidStudio2.3\config\templates】目录下，文件名和你在AS中设置的名字一样，但是会忽略掉名字中的中文。  
  
添加步骤  
1、Setting->Editor->【Live Templates】->点击+，选择【Template Group】创建一个自定义模板组  
  
2、选中刚刚创建的group，点击+，选择【Live Template】创建一个自定义模板  
*   Abbreviation：表示这个模板的快捷方式，你敲这些【字符+回车】后，模板就自动输出了  
*   Description：表示这个模板描述  
*   Template text：模板的内容  
  
3、点击下面蓝色的字"change"，设置你这个快捷键在哪里生效，一般把java或XML勾上就行了  
  
4、点击右侧的按钮"Edit variables"，编辑模板信息中的变量  
如我的Template text中定义了四个变量 bqt、date、name、bqt2，变量名字可以随意定义（只能是字母或数字）  
![](index_files/482ca31f-2532-47d4-9622-a4c0eac417c3.png)  
*   bqt：可将鼠标定位在此位置  
*   date、name：点击下三角【选择】合适的方法，可自动生成日期和类名  
*   skip if defined：表示当自动生成的值不为空时，光标不停留在此方法处。建议选中  
  
5、如上例，当我输入【快捷键+回车】后输出的内容为：  
![](index_files/c1b7122a-99f6-4bbf-8f70-ec47bc983e6e.png)  
*   焦点首先在定位的第一个方法处，即bqt处；当我输入内容后按下回车，或不输入内容直按回车  
*   焦点转移到下一个定义的方法处，即date处；当按下回车  
*   这时焦点应该会转移到name处，但因为勾选了skip if defined，且此处有自动生成内容，所以焦点移到下一方法处  
*   即转移到bqt2处，此时我再按下回车键，则焦点转移到整个模板的最后（不会添加换行符）  
  
# AS 进行单元测试  
以下为本人在AndroidStudio 2.0 上实测后得出的结论，不像网上那一堆堆的误人子弟的文章，都是过时的或者根本就是不对的。  
  
## 简介  
和eclipse需要配置清单文件不同，AndroidStudio自带的单元测试功能是不需要修改`AndroidManifest.xml`或`gradle`文件的，直接编写测试用例即可。  
  
使用AS新建一个工程时，在src目录下会自动创建三个目录：  
![](index_files/10ed2d47-d060-4282-8ab3-2ba1ef0a77a8.png)  
  
mian目录下为项目代码，androidTest目录下为编写Android测试用例使用，test目录下为编写Java测试用例使用。  
  
gradle文件默认也已配置【testCompile 'junit:junit:4.12'】  
![](index_files/c932929b-c4d8-429f-95f6-3b28344f2d20.png)  
  
## Java测试用例  
默认test目录下有一个现成的ExampleUnitTest类，我们稍加修改  
```java  
/**  
 * To work on unit tests, switch the Test Artifact in the Build Variants view.  
 */  
public class ExampleUnitTest {  
    @Test  
    public void addition_isCorrect() throws Exception {  
        int sum = 2 + 2;  
        System.out.println("结果为：" + sum);  
        assertEquals(4, sum);  
    }  
}  
```  
  
打开测试用例类，切换到Structure面板，鼠标选中要测的方法，`右键–Run [方法名]`，即可进行Java用例测试。  
![](index_files/972f5ad8-a708-4ced-974d-64ab8f8be438.png)  
![](index_files/94413a75-80b5-4333-84d8-38d47238dab3.png)  
  
如果运行结果正确，Run面板会是绿色的  
![](index_files/7badc88a-5c5c-434d-a812-4218414cf112.png)  
  
否则是红色的，比如我们将其中一行代码修改为：`assertEquals(5, 2 + 2);`  
![](index_files/a0e92403-ef67-4f8d-b557-f1f6fa9e32c3.png)  
  
我们也可以添加自己的测试类或测试方法，只不过我们的测试方法上要满足以下条件  
*   方法要为【public】访问权限  
*   要添加【@org.junit.Test】的注解  
*   方法不能带参数（因为你没办法传递参数啊）  
  
## Android测试用例  
同样，默认androidTest目录下有一个现成的ApplicationTest类，我们稍加修改  
```java  
/**  
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>  
 */  
public class ApplicationTest extends ApplicationTestCase<Application> {  
    public ApplicationTest() {  
        super(Application.class);  
    }  
  
    //方法名必须以test开头，并且方法不能带参数  
    public void testSimple() {  
        int width = getScreenWidth(getContext());  
        Log.i("bqt", "屏幕宽：" + width);  
        Toast.makeText(getContext(), "屏幕宽：" + width, Toast.LENGTH_SHORT).show();  
        //assertEquals(4, 2 + 2);//可以没有声明语句  
    }  
  
    /**  
     * 获取屏幕宽  
     */  
    private int getScreenWidth(Context context) {  
        DisplayMetrics metric = new DisplayMetrics();  
        ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(metric);  
        return metric.widthPixels;  
    }  
}  
```  
  
运行后  
![](index_files/80fcc632-98cc-4033-ad2b-47b3c524cf96.png)  
  
并且可以生成与运行环境（即Context）有关的日志（这才是最重要的用途，我们可以在不运行APP的情况下获取App所有数据）  
![](index_files/491b7ff3-eb6b-4b8e-bd88-155320d49b86.png)  
  
不过，很遗憾，我们不能直接操作UI（吐司没有弹出来）  
我们的测试方法上要满足以下条件  
*   方法要为【public】访问权限  
*   所有的测试方法必须以test作为前缀（上面的Java测试用例是要求有@Test的注解）  
*   方法不能带参数（因为你没办法传递参数啊）  
  
不过很奇怪的是，ApplicationTestCase类提示过时了，但是又没提示用哪个替换……而且这是你自动帮我生成的呀……  
![](index_files/f7a319e9-3839-440c-8f76-3ccc65694d0a.png)  
  
2018-1-6  
