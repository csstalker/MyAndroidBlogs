| Markdown版本笔记 | 我的GitHub首页 | 我的博客 | 我的微信 | 我的邮箱 |  
| :------------: | :------------: | :------------: | :------------: | :------------: |  
| [MyAndroidBlogs][Markdown] | [baiqiantao][GitHub] | [baiqiantao][博客] | bqt20094 | baiqiantao@sina.com |  
  
[Markdown]:https://github.com/baiqiantao/MyAndroidBlogs  
[GitHub]:https://github.com/baiqiantao  
[博客]:http://www.cnblogs.com/baiqiantao/  
  
AS 常用插件  
***  
目录  
===  

- [AS 常用插件](#as-常用插件)
	- [必备插件](#必备插件)
	- [有兴趣的插件](#有兴趣的插件)
	- [没兴趣的插件](#没兴趣的插件)
  
# AS 常用插件  
安装插件：Settings -->【Plugins】-->搜索-->点击install-->重启AS  
如果插件过大，在AS可能很难下载成功，可以去 [这里](https://plugins.jetbrains.com/) 搜索后下载，也可以在AS中搜索到后，根据插件提示去指定位置下载  
  
禁用插件：右侧面板会显示出已经安装的插件列表，取消勾选即可禁用插件  
  
[AS插件合集](https://ydmmocoo.github.io/2016/06/28/Android-Studio%E6%8F%92%E4%BB%B6%E6%95%B4%E7%90%86)  
[Android开发工具及周边插件](http://mobdevgroup.com/tools/android#android-studio)  
  
## 必备插件  
- 【.ignore】4139K 为项目添加忽略文件目录。File或右键->New->.ignore file。项目中的.gitignore上右键->Add template  
- 【[ADB idea](https://github.com/pbreault/adb-idea)】常用ADB命令  
- 【[ADB WiFi Connect](https://github.com/appdictive)】使用wifi无线调试app。可以保存以前的设置，下次在同一WIFI下不连接数据线即可重新连接调试  
- 【Alibaba Java Coding Guidelines】80K，7.8 M，阿里出的Java代码规范  
- 【Android ButterKnife Zelezny】222K 配合ButterKnife可一键生成所有定义id的组件的声明及onclick事件  
- 【Android Parcelable Code Generator】164K 将当前JavaBean修改为Parcelable接口的实现类  
- 【CodeGlance】261K 在右边可以预览代码结构，实现快速定位  
- 【EventBus3 Intellij Plugin】1K EventBus3.0+的导航插件。在EventBus.post和@Subscribe或者onEventMainThread间跳转  
- 【Genymotion】1163K 启动图标在工具栏上最后那个位置  
- 【GsonFormat】214K 自动根据json字符串生成相应的JavaBean。在Generate中  
- 【InnerBuilder】27K Builder模式的自动化生成  
- 【[JsonViewer](https://github.com/potterhsu/JsonViewer)】格式化预览 Json  
- 【Markdown Navigator】预览 Markdown  
- 【statistic】分类统计项目代码总行数、空行数  
- 【TranslationPlugin】7K 中英互译插件。功能非常强大，支持单词朗读，历史记录，搜索，替换  
  
## 有兴趣的插件  
- 【CheckStyle-IDEA】848K (60M)一个检查代码风格的插件，比如像命名约定，类设计等方面进行代码规范和风格的检查。  
- 【lombok plugin】一个功能丰富的自动化生成代码的插件，包括get/set/constructor/toString/equals/hashCode/log/builder/data等  
- 【SelectorChapek for Android】34K 根据资源文件的名称自动生成相应的Selector文件。在drawable系列目录上右键菜单中  
- 【jRebel For Android】48K 更改代码后实时刷新。收费插件，需要破解  
- 【Android Studio Prettify】26K 自动生成View的声明，不使用注解。可选择生成成员变量或局部变量。在Generate中  
- 【ADB WIFI】67K 使用wifi无线调试app。用数据线连接电脑->菜单Tools->Android->ADB WIFI->ADB Restart->ADB USB to WIFI->拔掉数据线即可  
- 【[Android WIFI ADB](https://github.com/pedrovgs/AndroidWiFiADB)】在工具栏最后那个位置有一个图标，可一键完成授权  
- 【JsonOnlineViewer】24K 在AS中调试返回数据为JSON的接口，打开方式Menu-->View-->JsonViewer  
- 【ECTranslation】7K 中英互译插件，功能比较简单  
- 【FindBugs-IDEA】552K 查找bug的插件，AS也提供了代码审查的功能Analyze-Inspect Code…  
- 【Markdown Navigator】1769K markdown插件  
- 【[ideaVim](http://www.jianshu.com/p/43862126b88f)】1881K  
- 【[android-strings-search-plugin](https://github.com/konifar/android-strings-search-plugin)】功能很弱。通过输入string内容而非id字符串。这货竟然不支持中文  
- 【RemoveButterKnife】2K 功能很弱。在Edit菜单下。删除对butterknife的引用并生成findviewbyid语句。只能对当前类操作，且onclick等事件去不掉  
- 【[AndroidLocalizationer](https://github.com/westlinkin/AndroidLocalizationer)】 功能很弱。自动对string文件进行翻译(本地化)。支持根据首位字符进行过滤，支持语言引擎的选择，但仅支持将English转换为其他语言    
  
## 没兴趣的插件  
- 【GenerateSerialVersionUID】152K 没啥用。为实现Serializable接口的bean生成serialVersionUID  
- 【Android Drawable Importer】188K 没啥用。图片导入插件。导入Android与Material图标的Drawable ，批量导入，多源导入  
- 【Material Theme UI】216K 没啥用。添加Material主题到你的AS  
- 【Android Code Generator】45K 没啥用。根据布局文件快速生成对应的Activity，Fragment，Adapter，Menu  
- 【Android Methods Count】25K 没啥用。显示依赖库中的方法数量  
- 【Lifecycle Sorter】14K 没啥用。根据Activity、fragment的生命周期对其生命周期方法位置进行先后排序  
- 【WakaTime】135K 没啥用。记录你在IDE上的时间，需要去[官网](https://wakatime.com/)注册并获取key，要去官网才能查看  
- 【Android Styler】6K 根据xml自动生成style代码  
- 【[LeakCanary](https://www.liaohuqiu.net/cn/posts/leak-canary-read-me/)】帮助你在开发阶段方便的检测出内存泄露的问题  
- 【Android Postfix Completion】36K 可根据后缀快速完成代码，如果能通过原作者的代码自己定制功能，那就更爽了  
- 【Android Holo Colors Generator】29K 通过自定义Holo主题颜色生成对应的Drawable和布局文件  
- 【[dagger-intellij-plugin](https://github.com/square/dagger-intellij-plugin)】dagger可视化辅助工具  
- 【[GradleDependenciesHelperPlugin](https://github.com/ligi/GradleDependenciesHelperPlugin)】maven gradle 依赖支持自动补全    
- 【[AndroidProguardPlugin](https://github.com/zhonghanwen/AndroidProguardPlugin )】一键生成项目混淆代码插件   
- 【[otto-intellij-plugin](https://github.com/square/otto-intellij-plugin)】otto事件导航工具    
- 【[idea-markdown](https://github.com/nicoulaj/idea-markdown)】markdown插件   
- 【Sexy Editor】27K 设置AS代码编辑区的背景图  
- 【[folding-plugin](https://github.com/dmytrodanylyk/folding-plugin)】布局文件分组的插件    
- 【[Android-DPI-Calculator](https://github.com/JerzyPuchalski/Android-DPI-Calculator)】DPI计算插件    
- 【[gradle-retrolambda](https://github.com/evant/gradle-retrolambda)】在java 6 7中使用 lambda表达式插件    
- 【PermissionsDispatcher plugin】3K 自动生成6.0权限的代码  
- 【[BorePlugin](https://github.com/boredream/BorePlugin)】自动生成布局代码插件    
- 【jimu Mirror】29K 能够实时预览Android布局，它会监听布局文件的改动，如果有代码变化，就会立即刷新UI  
- 【Codota】33K 搜索最好的Android代码  
- 【LayoutFormatter】3K 一键格式化你的 XML 文件  
- 【Exynap】6K 帮助开发者自动生成样板代码  
- 【[gradle-cleaner-intellij-plugin](https://github.com/Softwee/gradle-cleaner-intellij-plugin)】强制结束gradle任务     
- 【MVPHelper】4K 为MVP生成接口以及实现类  
  
2019-1-8  
