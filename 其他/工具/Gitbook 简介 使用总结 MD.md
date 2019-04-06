| Markdown版本笔记 | 我的GitHub首页 | 我的博客 | 我的微信 | 我的邮箱 |  
| :------------: | :------------: | :------------: | :------------: | :------------: |  
| [MyAndroidBlogs][Markdown] | [baiqiantao][GitHub] | [baiqiantao][博客] | bqt20094 | baiqiantao@sina.com |  
  
[Markdown]:https://github.com/baiqiantao/MyAndroidBlogs  
[GitHub]:https://github.com/baiqiantao  
[博客]:http://www.cnblogs.com/baiqiantao/  
  
Gitbook 简介 使用总结 MD  
***  
目录  
===  

- [Gitbook](#Gitbook)
	- [环境准备](#环境准备)
	- [基本使用](#基本使用)
	- [项目目录结构](#项目目录结构)
		- [章节文件：SUMMARY.md](#章节文件：SUMMARYmd)
- [Summary](#Summary)
	- [Part I](#Part-I)
	- [Part II](#Part-II)
		- [配置文件：book.json](#配置文件：bookjson)
	- [使用插件](#使用插件)
	- [输出PDF格式文档](#输出PDF格式文档)
	- [常用命令](#常用命令)
	- [报错：找不到 fontsettings.js](#报错：找不到-fontsettingsjs)
  
# Gitbook  
Gitbook 是基于`Node.js`的命令行工具，用来创建漂亮的电子书，它使用`Markdown`或`AsciiDoc`语法来撰写内容，用`Git`进行版本控制，且可以托管在`Github`上。Gitbook 可以将作品编译成网站、PDF、ePub 和 MOBI 等多重格式。  
  
如果你不擅长自己搭建 gitbook 环境，还可以使用 [gitbook.com](https://www.gitbook.com) 在线服务来创建和托管你的作品，他们还提供了基于桌面的编辑器。  
  
## 环境准备  
**安装 Node.js**  
GitBook 是一个基于 Node.js 的命令行工具，下载安装 Node.js，安装完成之后，你可以使用下面的命令来检验是否安装成功。  
```  
node -v  
```  
  
**安装 GitBook**  
输入下面的命令来安装 GitBook。  
```  
npm install gitbook-cli -g  
```  
  
安装完成之后，你可以使用下面的命令来检验是否安装成功。  
```c  
gitbook -V  
```  
  
**安装 GitBook Editor**  
[GitBook Editor 下载](https://legacy.gitbook.com/editor)  
Write, publish, and collaborate seamlessly. The Editor brings the GitBook workflow you love to your desktop.  
  
> 不翻墙可能无法登陆注册，但是可以离线使用  
> 实际发现并没有什么卵用  
  
## 基本使用  
1、GitBook 准备工作做好之后，我们进入一个你要写书的目录，输入如下命令：  
```c  
gitbook init  
  
warn: no summary file in this book  
info: create README.md  
info: create SUMMARY.md  
info: initialization is finished  
```  
如果目录中存在`SUMMARY.md`文件，则会根此文件的目录结构初始化各个章节文件，否则他会会自动创建`SUMMARY.md`文件。项目中一般还会包含一个`README.md`说明文档。  
  
2、运行服务，在编辑内容后实时预览：：  
```c  
gitbook serve  
Live reload server started on port: 35729  
Press 【CTRL+C】 to quit ...  
...  
Serving book on http://localhost:4000  
```  
  
首次运行该命令后，会在项目文件夹中生成一个`_book`文件夹，里面的内容即为生成的html文件，然后在浏览器地址栏中输入 [http://localhost:4000](http://localhost:4000) 便可预览书籍。  
  
3、我们可以使用下面命令来生成网页而不开启服务器：  
```c  
gitbook build  
```  
  
## 项目目录结构  
GitBook 项目基本的目录结构如下所示：  
```c  
├── book.json  
├── README.md  
├── SUMMARY.md  
├── chapter-1/  
|   ├── README.md  
|   └── something.md  
└── chapter-2/  
    ├── README.md  
    └── something.md  
```  
  
下面我们主要来讲讲 book.json 和 SUMMARY.md 文件。  
  
### 章节文件：SUMMARY.md  
这个文件主要决定 GitBook 的`章节目录`，它通过 Markdown 中的`列表语法`来表示文件的父子关系。我们可以通过使用`标题`或者`水平分割线`将 GitBook 分为几个不同的部分，下面是一个简单的示例：  
```  
# Summary  
  
## Part I  
  
* [Introduction](README.md)  
* [Writing is nice](part1/writing.md)  
* [GitBook is nice](part1/gitbook.md)  
  
## Part II  
  
* [We love feedback](part2/feedback_please.md)  
* [Better tools for authors](part2/better_tools.md)  
  
---  
  
* [Last part without title](part3/title.md)  
```  
  
### 配置文件：book.json  
该文件主要用来存放配置信息，常用配置如下：  
- title：本书标题  
- author：本书作者  
- description：本书描述  
- language：本书语言，中文设置 "zh-hans" 即可  
- gitbook：指定使用的 GitBook 版本  
- styles：自定义页面样式  
- structure：指定 Readme、Summary、Glossary 和 Languages 对应的文件名  
- links：在左侧导航栏添加链接信息，例如个人站点  
- plugins：配置使用的插件，例如分享、GitHub  
- pluginsConfig：配置插件的属性，比如GitHub的仓库地址  
  
```json  
{  
    "title": "包青天的GitBook",  
    "author": "包青天",  
    "description": "欢迎访问包青天的GitBook",  
    "language": "zh-hans",  
    "gitbook": "3.2.3",  
    "styles": {  
        "website": "./styles/website.css"  
    },  
    "structure": {  
        "readme": "README.md"  
    },  
    "links": {  
        "sidebar": {  
            "我的博客": "http://www.cnblogs.com/baiqiantao",  
            "我的GitHub": "https://github.com/baiqiantao"  
        }  
    },  
    "plugins": [  
        "-sharing",  
        "anchors",  
        "github"  
    ],  
    "pluginsConfig": {  
        "github": {  
            "url": "https://github.com/baiqiantao"  
        }  
    }  
}  
```  
  
## 使用插件  
[插件官网](https://plugins.gitbook.com)  
  
Gitbook 本身功能丰富，但同时可以使用插件来进行个性化定制。  
  
GitBook默认带有 5 个插件：`highlight、search、sharing、font-settings、livereload`，如果要去除自带的插件，可以在插件名称前面加`-`。  
  
Gitbook目前一共有700个左右的插件，如果要使用其他插件，可以在`plugins`中添加插件名称，然后在`pluginsConfig`中配置插件。  
  
如果在执行build或server命令时提示要使用的插件不存在，只需在终端输入 `gitbook install ./` 即可自动安装缺少的插件。如果要指定插件的版本，可以使用`plugin@0.3.1`。  
  
**默认插件**  
- [sharing](https://plugins.gitbook.com/plugin/sharing)：默认的分享插件。  
- [fontsettings](https://plugins.gitbook.com/plugin/fontsettings)：默认的字体、字号、颜色设置插件。  
- [search](https://plugins.gitbook.com/plugin/search)：默认搜索插件。  
- [highlight](https://plugins.gitbook.com/plugin/highlight)：默认的代码高亮插件，通常会使用 prism 来替换。  
- lunr  
  
**必备插件**  
- [anchor-navigation-ex](https://plugins.gitbook.com/plugin/anchor-navigation-ex)：导航扩展，增加锚点，返回顶部，显示序号  
- [anchor-navigation](https://plugins.gitbook.com/plugin/anchor-navigation)：锚点导航(为什么不能用？)  
- [donate](https://plugins.gitbook.com/plugin/donate)：捐赠打赏按钮插件  
- [splitter](https://plugins.gitbook.com/plugin/splitter)：在左侧目录和右侧内容之间添加一个可以拖拽的栏，用来调整两边的宽度。  
- [tbfed-pagefooter](https://plugins.gitbook.com/plugin/tbfed-pagefooter)：自定义页脚，显示版权和最后修订时间。  
- [ad](https://plugins.gitbook.com/plugin/ad)：在每个页面顶部和底部添加广告或任何自定义内容。  
  
**常用插件**  
- [editlink](https://plugins.gitbook.com/plugin/editlink)：内容顶部显示`编辑本页`链接。  
- [book-summary-scroll-position-saver](https://plugins.gitbook.com/plugin/book-summary-scroll-position-saver)：自动保存左侧目录区域导航条的位置。  
- [prism](https://plugins.gitbook.com/plugin/prism)：基于 [Prism](http://prismjs.com/) 的代码高亮。  
- [atoc](https://plugins.gitbook.com/plugin/atoc)：插入 TOC 目录。  
- [ace](https://plugins.gitbook.com/plugin/ace)：插入代码高亮编辑器。  
- [include-codeblock](https://plugins.gitbook.com/plugin/include-codeblock)：通过引用文件插入代码。  
- [expandable-chapters](https://plugins.gitbook.com/plugin/expandable-chapters)：收起或展开章节目录中的父节点。  
- [anchors](https://plugins.gitbook.com/plugin/anchors)：标题带有 github 样式的锚点。  
- [github](https://plugins.gitbook.com/plugin/github)：在右上角显示 github 仓库的图标链接。  
- [github-buttons](https://plugins.gitbook.com/plugin/github-buttons)：显示 github 仓库的 star 和 fork 按钮。  
  
**可能有用的**  
- [redirect](https://plugins.gitbook.com/plugin/redirect)：页面跳转(重定向)。  
- [sectionx](https://plugins.gitbook.com/plugin/sectionx)：分离各个段落，并提供一个展开收起的按钮。  
- [favicon](https://plugins.gitbook.com/plugin/favicon)：为网站添加了favicon和Apple Touch图标。  
- [image-captions](https://plugins.gitbook.com/plugin/image-captions)：抓取内容中图片的 `alt` 或 `title` 属性，在图片下面显示标题。  
- [mermaid](https://plugins.gitbook.com/plugin/mermaid)：使用流程图。  
- [latex-codecogs](https://plugins.gitbook.com/plugin/latex-codecogs)：使用数学方程式。  
- [disqus](https://plugins.gitbook.com/plugin/disqus)：添加 disqus 评论插件。  
- [sitemap](https://plugins.gitbook.com/plugin/sitemap)：生成站点地图。  
- [baidu](https://plugins.gitbook.com/plugin/baidu)：使用百度统计。  
- [ga](https://plugins.gitbook.com/plugin/ga)：添加 Google 统计代码。  
- [duoshuo](https://plugins.gitbook.com/plugin/duoshuo)：使用多说评论。  
  
**一般用不到的**  
- [chart](https://plugins.gitbook.com/plugin/chart)：使用 C3.js 图表。  
- [youtubex](https://plugins.gitbook.com/plugin/youtubex)：插入 YouTube 视频。  
- [fbqx](https://plugins.gitbook.com/plugin/fbqx)：使用填空题。  
- [mcqx](https://plugins.gitbook.com/plugin/mcqx)：使用选择题。  
- [spoiler](https://plugins.gitbook.com/plugin/spoiler)：隐藏答案，当鼠标划过时才显示。  
- [styles-sass](https://plugins.gitbook.com/plugin/styles-sass)：使用 SASS 替换 CSS。  
- [styles-less](https://plugins.gitbook.com/plugin/styles-less)：使用 LESS 替换 CSS。  
  
## 输出PDF格式文档  
下载插件 [calibre-ebook](https://calibre-ebook.com/download)  
  
安装后，程序会自动将包含`ebook-convert.exe`的文件夹添加到PATH变量中(如果没有，请手动添加)。  
  
```  
npm install svgexport -g  
npm install ebook-convert -g  
gitbook pdf/epub/mobi  //生成电子书  
```  
  
个性化配置：  
- `pageNumbers`：是否添加页码，默认是true  
- `fontSize`：字体大小，默认是12  
- `fontFamily`：字体，默认字体是Arial  
- `paperSize`：default is a4, options are 'a0', 'a1', 'a2', 'a3', 'a4', 'a5', 'a6', 'b0', 'b1', 'b2', 'b3', 'b4', 'b5', 'b6', 'legal', 'letter'  
- `margin.top/bottom/right/left`：Top margin (default is 56/62)  
  
```json  
{  
    "pdf": {  
        "fontFamily": "Arial",  
        "fontSize": 12,  
        "margin": {  
            "bottom": 56,  
            "left": 62,  
            "right": 62,  
            "top": 56  
        },  
        "pageNumbers": true,  
        "paperSize": "a4"  
    },  
    "pluginsConfig": {  
        "fontSettings": {  
            "family": "msyh",  
            "size": 2,  
            "theme": "white"  
        },  
        "plugins": [  
            "yahei",  
            "katex",  
            "-search"  
        ]  
    }  
}  
```  
  
## 常用命令  
**常用命令**  
```c  
gitbook init //初始化目录文件  
gitbook build [书籍路径] [输出路径] //构建书籍，生成静态网页，默认输出到 _book 目录  
gitbook serve [--port 端口号]  //包含build命令，生成静态网页并运行服务器  
gitbook pdf ././mybook.pdf  //生成 pdf/epub/mobi 格式的电子书  
  
gitbook help //列出gitbook所有的命令  
gitbook --help //输出gitbook-cli的帮助信息  
gitbook ls //列出本地所有的gitbook版本  
gitbook ls-remote //列出远程可用的gitbook版本  
gitbook fetch 标签/版本号 //安装对应的gitbook版本  
gitbook update //更新到gitbook的最新版本  
gitbook uninstall 2.0.1 //卸载对应的gitbook版本  
```  
  
**gitbook --help**  
```c  
Usage: gitbook [options] [command]  
  
Options:  
-v, --gitbook [version]  specify GitBook version to use  
-d, --debug              enable verbose error  
-V, --version            Display running versions of gitbook and gitbook-cli  
-h, --help               output usage information  
  
Commands:  
ls                        List versions installed locally  
current                   Display currently activated version  
ls-remote                 List remote versions available for install  
fetch [version]           Download and install a <version>  
alias [folder] [version]  Set an alias named <version> pointing to <folder>  
uninstall [version]       Uninstall a version  
update [tag]              Update to the latest version of GitBook  
help                      List commands for GitBook  
*                         run a command with a specific gitbook version  
```  
  
**gitbook help**  
```c  
build [book] [output]       build a book  
    --log                   Minimum log level to display (Default is info; Values are debug, info, warn, error, disabled)  
    --format                Format to build to (Default is website; Values are website, json, ebook)  
    --[no-]timing           Print timing debug information (Default is false)  
  
serve [book] [output]       serve the book as a website for testing  
    --port                  Port for server to listen on (Default is 4000)  
    --lrport                Port for livereload server to listen on (Default is 35729)  
    --[no-]watch            Enable file watcher and live reloading (Default is true)  
    --[no-]live             Enable live reloading (Default is true)  
    --[no-]open             Enable opening book in browser (Default is false)  
    --browser               Specify browser for opening book (Default is )  
    --log                   Minimum log level to display (Default is info; Values are debug, info, warn, error, disabled)  
    --format                Format to build to (Default is website; Values are website, json, ebook)  
  
install [book]    --log              install all plugins dependencies  
parse [book]    --log                parse and print debug information about a book  
init [book]    --log                 setup and create files for chapters  
pdf [book] [output]    --log         build a book into an ebook file  
epub [book] [output]    --log        build a book into an ebook file  
mobi [book] [output]    --log        build a book into an ebook file  
```  
  
## 报错：找不到 fontsettings.js  
> Error: ENOENT: no such file or directory, stat `...\_book\gitbook\gitbook-plugin-fontsettings\fontsettings.js`  
  
原来是一个[Bug](https://github.com/GitbookIO/gitbook/issues/1309)（Vesion：3.2.3）。  
  
解决办法：  
在用户目录下找到以下文件：`<user>\.gitbook\versions\3.2.3\lib\output\website\copyPluginAssets.js`  
替换：`confirm: true` 为 `confirm: false`  
  
2019-3-12  
