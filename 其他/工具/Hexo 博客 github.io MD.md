| Markdown版本笔记 | 我的GitHub首页 | 我的博客 | 我的微信 | 我的邮箱 |  
| :------------: | :------------: | :------------: | :------------: | :------------: |  
| [MyAndroidBlogs][Markdown] | [baiqiantao][GitHub] | [baiqiantao][博客] | bqt20094 | baiqiantao@sina.com |  
  
[Markdown]:https://github.com/baiqiantao/MyAndroidBlogs  
[GitHub]:https://github.com/baiqiantao  
[博客]:http://www.cnblogs.com/baiqiantao/  
  
  
***  
目录  
===  

- [Hexo](#Hexo)
- [博客网站搭建](#博客网站搭建)
	- [本地环境配置](#本地环境配置)
	- [配置 SSH keys](#配置-SSH-keys)
	- [发布项目](#发布项目)
- [Deployment](#Deployment)
	- [Docs: https://hexo.io/docs/deployment.html](#Docs-httpshexoiodocsdeploymenthtml)
	- [源码结构](#源码结构)
- [创建文章](#创建文章)
	- [布局 layout](#布局-layout)
- [这里是添加的文章内容](#这里是添加的文章内容)
	- [文件名称 title](#文件名称-title)
	- [草稿 drafts](#草稿-drafts)
	- [Front-matter](#Front-matter)
	- [资源文件夹 asset](#资源文件夹-asset)
	- [模版 scaffolds](#模版-scaffolds)
	- [分类和标签](#分类和标签)
- [命令](#命令)
	- [服务器 server](#服务器-server)
	- [生成文件 generate](#生成文件-generate)
	- [部署 deploy](#部署-deploy)
- [永久链接 permalinks](#永久链接-permalinks)
	- [示例](#示例)
	- [多语种支持](#多语种支持)
- [=> source/_posts/tw/Hello-World.md](#-source_poststwHello-Worldmd)
- [[主题 themes](https://hexo.io/zh-cn/docs/themes)](#主题-themeshttpshexoiozh-cndocsthemes)
	- [国际化 i18n](#国际化-i18n)
	- [推荐主题](#推荐主题)
	- [使用主题](#使用主题)
- [[插件 plugins](https://hexo.io/plugins/)](#插件-pluginshttpshexoioplugins)
	- [链接持久化插件](#链接持久化插件)
	- [自动生成分类插件](#自动生成分类插件)
- [Generate categories from directory-tree](#Generate-categories-from-directory-tree)
- [配置文件 config](#配置文件-config)
- [Site 网站](#Site-网站)
- [URL 网址](#URL-网址)
- [Directory 目录](#Directory-目录)
- [Writing 文章](#Writing-文章)
- [Home page setting 首页设置](#Home-page-setting-首页设置)
- [Category & Tag 分类 & 标签](#Category-&-Tag-分类-&-标签)
- [Date / Time format 日期 / 时间格式，Hexo使用Moment.js来解析和显示日期](#Date--Time-format-日期--时间格式，Hexo使用Momentjs来解析和显示日期)
- [Pagination 分页](#Pagination-分页)
- [Deployment 部署](#Deployment-部署)
- [Extensions 扩展](#Extensions-扩展)
  
# Hexo  
[Hexo 官网中文文档](https://hexo.io/zh-cn/docs/)  
[我的首页](https://baiqiantao.github.io)  
[使用技巧](http://muyunyun.cn/posts/f55182c5/#more)  
  
Hexo 是一个快速、简洁且高效的博客框架。Hexo 使用 [Markdown](http://daringfireball.net/projects/markdown/)（或其他渲染引擎）解析文章，在几秒内，即可利用靓丽的主题生成静态网页。  
  
Hexo 是高效的静态站点生成框架，她基于 [Node.js](https://nodejs.org/)。 通过 Hexo 你可以轻松地使用 Markdown 编写文章，除了 Markdown 本身的语法之外，还可以使用 Hexo 提供的 [标签插件](https://hexo.io/zh-cn/docs/tag-plugins.html) 来快速的插入特定形式的内容。  
  
> 标签插件和 Front-matter 中的标签不同，它们是用于在文章中快速插入特定内容的插件。  
  
# 博客网站搭建  
  
使用 hexo + github 搭建博客网站过程  
  
## 本地环境配置  
  
**环境配置**  
- 打开cmd，输入`git --version`，可以看到打印的Git版本信息。如果没有，请配置Git环境变量。  
- 打开cmd，输入`node -v`和`npm -v`，可以看到打印的Node和npm版本信息。如果没有，请配置Node环境变量。  
- 打开cmd，输入`hexo -v`，检查hexo是否安装成功，若没有安装，可输入`npm install hexo -g`安装。  
  
由于Hexo的很多操作都涉及到命令行，您可以考虑始终使用`Git Bash`来进行操作。  
  
**项目配置**  
- 在任意目录创建一个文件夹，然后通过命令行进入到该文件夹里面  
- 输入`hexo init`初始化，最后会看到成功提示`INFO  Start blogging with Hexo!`  
- 输入`npm install`安装所需要的组件  
- 输入`hexo g`或`hexo generate`生成html、js、css、png等网页相关的文件  
- 输入`hexo s`或`hexo server`开启服务器，在浏览器中输入 [http://localhost:4000](http://localhost:4000) 便可正式体验 Hexo  
  
> 在服务器启动期间，Hexo 会监视文件变动(包括文件内容变更以及新增删除文件变更)并自动更新，您无须重启服务器  
> 我们可以通过`Ctrl+c`停止服务器，  
> 如果您想要更改端口，或是在执行时遇到了 `EADDRINUSE` 错误，可以输入`hexo server -p 端口号`来改变端口号  
  
## 配置 SSH keys  
  
**创建 SSH Keys**  
- 在任意目录右键，点击`git bash here`，然后通过`cd ~/.ssh`命令可快速定位到`C盘->用户->自己的用户名->.ssh`目录  
- 如果`C盘->用户->自己的用户名`下没有`.ssh`目录，可通过`cd ~`然后`mkdir .ssh`命令创建  
- 查看`.ssh`目录下是否有`id_rsa`、`id_rsa.pub`文件，也可通过`ls`命令查看，如果没有需要按以下步骤生成  
- 输入`ssh-keygen -t rsa -C "baiqiantao@sina.com"`生成密钥，密钥类型可以用`-t`选项指定，这里使用的是rsa；`-C`用来指定注释，这里输入自己的邮箱或者其他都行。  
- 之后会让你确认`文件名`和输入`密语(passphrase)`，空表示没有密语；**什么都不要输入**，连输三个回车就行。  
- 然后会发现在当前目录中生成了两个文件，其中`id_rsa`是私钥文件，`id_rsa.pub`文件是公钥文件  
- 打开你的`id_rsa.pub`文件，复制下里面的内容  
```c  
ssh-rsa AAAAB3NzaC1yc2EAAAADAQABAAABAQCy8mYQSRxSi3RdisKxEPM2QUDcuyEI5KR3XS7pLhO9nXqKCRSCZk+MPdQQiIGW56Y7zNz5eUvzIOSW79OReZoEVHqLD5z4L56n13HhvCfFO/HXujOBYsWB1pzI6Lj25MHMZDyUtnJXzbvs8Kiqh3rFi9gy0ogdOEETG6WuTlkOijGE4ly7Ll6KyWRJRBm7X7Sof4jyOuNwholLiU0Zidaykac41/OXVyp/MpX6PeCyv1h5f36Uhsgut82olAOUN+AsYsVOrN9ab/C24jS1BmTXcjGlw8wHUQ9h/URMnOGk8jsJ+fJQf2MIv7LpdpPFANEx6nNW0IrIgFzttPm4wfWR baiqiantao@sina.com  
```  
  
**配置到 GitHub**  
- 进入Github首页，右上角头像旁边的三角形 -> Settings -> SSH and GPG keys -> New SSH key  
- 在Title处填入任意的标识，在Key部分里面添加刚才复制的`id_rsa.pub`文件里的内容，点击添加即可  
- 验证 SSH Keys 是否添加成功：`ssh -T git@github.com`  
  
> 如果是第一次的会提示是否continue，输入Yes后如果看到如下提示就表示已成功连上Github：  
> Hi baiqiantao! You've successfully authenticated, but GitHub does not provide shell access.  
  
## 发布项目  
  
**创建 GitHub 项目**  
- 新建项目(New repositor)，项目名称必须要严格遵守如下格式：`账户名.github.io`，比如我的为`baiqiantao.github.io`，不然接下来会有很多麻烦。  
- 创建完之后点击`Settings`并找到`GitHub Pages`一栏，你会看到那边有个网址，[点击访问它](https://baiqiantao.github.io)，你将会惊奇的发现该项目已经被部署到网络上，能够通过外网来访问它。  
- 点击`Choose a theme`，还可以选择自己喜欢的主题，选择之后保存，再访问刚才那个网站，会发现选择已经生效了。  
  
**将 Hexo 部署到 Github**  
先安装git插件：  
```c  
npm install hexo-deployer-git --save  
```  
  
然后在项目文件夹根目录中，找到`_config.yml`文件，修改末尾的`deploy`为如下：  
```c  
# Deployment  
## Docs: https://hexo.io/docs/deployment.html  
deploy:  
  type: git  
  repo: git@github.com:baiqiantao/baiqiantao.github.io.git  
  branch: master  
```  
  
然后输入`hexo d`或`hexo deploy`，完成以后，你就能在你的Github上看到上传的代码了，这时看到的应该是纯静态的一个站点。可以发现，GitHub上上传的仅仅是`public`目录下的文件。  
  
> 注意，hexo d 只会上传 public 目录中生成的文件，而不会生成静态文件，所以你可能需要先执行 hexo g 命令生成静态文件。  
  
然后等待几分钟，访问 [baiqiantao.github.io](https://baiqiantao.github.io) 即可发现自己的博客可以访问了！到此为止，最基本的也是最全面的`hexo + github`搭建博客网站过程完结。  
  
## 源码结构  
  
| 文件/文件夹 | 说明 |  
| --- | --- |  
| `_config.yml` | [配置文件](https://hexo.io/zh-cn/docs/configuration)，不管是博客的基础配置，还是模板，都是修改这个文件 |  
| `package.json` | 应用程序的信息 |  
| public | 生成的静态文件，这个目录最终会发布到服务器(上传到GitHub仓库) |  
| scaffolds | 模板文件夹。当您新建文章时，Hexo 会根据 scaffold 来建立文件。Hexo的模板是指在新建的 Markdown 文件中默认填充的内容，一般用于配置`title、date、tags`等信息。 |  
| source | 资源文件夹。我们日常操作的文件夹，`_drafts`为草稿文件，`_posts`为发布的文章。除`_posts`文件夹之外，开头命名为`_`的文件、文件夹和隐藏的文件将会被忽略。Markdown 和 HTML 文件会被解析并放到`public`文件夹，而其他文件会被拷贝过去。 |  
| themes | 主题 文件夹。Hexo 会根据主题来生成静态页面。 |  
  
> 注意，如果出现乱码情况，很可能是你的markdown文件或`_config.yml`配置文件不是以`utf-8`编码格式保存的。  
  
# 创建文章  
  
官方新编写BLOG，建议的是使用命令  
```  
hexo new [layout] <title>  
```  
  
其实，你可以直接在`source/_drafts`新建草稿文章，在`source/_posts`新建发布文章。然后在文章顶部加上相关的描述，如:  
```  
---  
title: new 命令中输入的 title  
date: 2019-03-14 17:38:41  
---  
```  
  
这个效果和用命令新建文件是一样一样的。  
  
> 注意，从其他地方拷文件到`source/_posts`时，需要保证文件头有必要的描述(一般title是必须的)，否则此文件可能会被忽略  
> 注意，如果发布后出现乱码情况，很可能是你的文章或`_config.yml`配置文件不是以`utf-8`编码格式保存的  
  
## 布局 layout  
Hexo 有三种默认布局：`post`、`page` 和 `draft`，它们分别对应不同的`路径`，而您自定义的其他布局和`post`相同，都将储存到 `source/_posts` 文件夹。  
  
| 布局 | 路径 |  
| --- | --- |  
| `post` | `source/_posts`，默认title即是文件名 |  
| `page` | `source`，默认title是文件夹名，文件名为`index.md` |  
| `draft` | `source/_drafts`，默认title即是文件名 |  
  
如果你不想你的文章被处理，你可以将 Front-Matter 中的`layout:` 设为 `false`。  
  
比如使用命令`hexo new false 测试layout`创建一篇文章，执行后会在`source\_posts`中生成`测试layout.md`文件，文件内容为：  
```c  
---  
layout: 'false'  
title: 测试layout  
date: 2019-03-15 13:37:40  
tags:  
---  
  
# 这里是添加的文章内容    
此内容*不会*被渲染，**当然**，原始的`markdown`还是被渲染成了`html`。其中`false`可以*不加*引号。  
```  
  
然后执行`hexo g -d`，完成后发现访问此文章，发现此文章没有任何被设置任何主题等效果(当然，原始的markdown还是被渲染成了html)  
![](index_files/a1ffd44b-d259-4224-aef2-f0f34f6555ea.png)  
  
## 文件名称 title  
Hexo 默认以标题做为文件名称(默认值`:title.md`)，但您可编辑 `new_post_name` 参数来改变默认的文件名称。  
  
| 变量 | 描述 |  
| --- | --- |  
| `:title` | 标题。小写，空格将会被替换为短杠，默认值为`:title.md` |  
| `:year` | 建立的年份，比如， `2015` |  
| `:month` | 建立的月份（有前导零），比如， `04` |  
| `:i_month` | 建立的月份（无前导零），比如， `4` |  
| `:day` | 建立的日期（有前导零），比如， `07` |  
| `:i_day` | 建立的日期（无前导零），比如， `7` |  
  
## 草稿 drafts  
刚刚提到了 Hexo 的一种特殊布局：`draft`，这种布局在建立时会被保存到 `source/_drafts` 文件夹。您可通过 `hexo publish [layout] <filename>` 命令将草稿中的指定文件(不带后缀名) **移动** 到 `source/_posts` 文件夹，该命令的使用方式与 `new` 十分类似，您也可在命令中指定 `layout` 来指定布局。  
  
草稿默认不会显示在页面中，您可在执行时加上`--draft`参数，或是把`render_drafts`参数设为`true`来预览草稿。  
  
## Front-matter  
Front-matter 是文件最上方以 `---` 分隔的区域，用于指定个别文件的`变量`，举例来说：  
```c  
title: Hello World  
date: 2013/7/13 20:46:25  
---  
```  
  
以下是预先定义的参数，您可在`模板`中`使用这些参数值`并加以利用。  
  
| 参数 | 描述 | 默认值 |  
| --- | --- | --- |  
| `layout` | 布局 | |  
| `title` | 标题 | |  
| `date` | 建立日期 | 文件建立日期 |  
| `updated` | 更新日期 | 文件更新日期 |  
| `comments` | 开启文章的评论功能 | true |  
| `tags` | 标签（不适用于分页） | |  
| `categories` | 分类（不适用于分页） |  |  
| `permalink` | 覆盖文章网址 |   |  
  
例如，如果你在模板中正文部分添加如下内容：  
```  
布局为 {{ layout }}，标题为 {{ title }}，建立日期为 {{ date }}，更新日期为 {{ updated }}  
开启评论功能 {{ comments }}，标签为 {{ tags }}，分类为 {{ categories }}，覆盖文章网址 {{ permalink }}  
```  
  
那么，在发布后实际的效果为(PS：通过此模板创建的文章的内容并没有改变，只是发布后内容才改变)：  
![](index_files/35306093-0808-49b5-906d-389b577afa0f.png)  
  
## 资源文件夹 asset  
资源（Asset）代表 `source` 文件夹中除了文章以外的所有文件，例如图片、CSS、JS 文件等。比方说，如果你的Hexo项目中只有少量图片，那最简单的方法就是将它们放在 `source/images` 文件夹中。然后通过类似于 `![](/images/image.jpg)` 的方法访问它们。  
  
> 以下几种方式貌似都可以【images/image.jpg】【/images/image.jpg】【../images/image.jpg】  
  
**文章资源文件夹**  
对于那些想要更有规律地提供图片和其他资源，以及想要将他们的资源分布在各个文章上的人来说，Hexo也提供了更组织化的方式来管理资源。这个稍微有些复杂但是管理资源非常方便的功能可以通过将 `config.yml`文件中的 `post_asset_folder` 选项设为 `true` 来打开。  
  
当资源文件管理功能打开后，Hexo将会在你每一次通过 `hexo new [layout] <title>` 命令创建新文章时，自动在此文章同一目录中创建一个同名的文件夹。将所有与你的文章有关的资源放在这个关联文件夹中之后，你可以通过相对路径来引用它们，这样你就得到了一个更简单而且方便得多的工作流。  
  
**相对路径引用的标签插件**  
```  
{% asset_path slug %}        //打印相对路径  
{% asset_img slug [title] %}        //显示图片  
{% asset_link slug [title] %}        //生成图片链接  
```  
  
比如说：当你打开文章资源文件夹功能后，你把一个 `example.jpg` 图片放在了你的资源文件夹中，如果通过使用相对路径的常规 markdown 语法 `![](/example.jpg)` ，它将 _不会_ 出现在首页上。（但是它会在文章中按你期待的方式工作）  
  
正确的引用图片方式是使用下列的标签插件而不是 markdown ：  
```html  
{% asset_img example.jpg 鼠标在图片上悬浮时显示的内容 %}  
```  
通过这种方式，图片将会同时出现在文章和主页以及归档页中。  
  
## 模版 scaffolds  
在新建文章时，Hexo 会根据`scaffolds`文件夹内相对应的文件来建立文件，例如：  
```c  
hexo new abc "My Gallery"  
```  
  
在执行这行指令时，Hexo 会尝试在 `scaffolds` 文件夹中寻找 `abc.md`，并根据其内容建立文章。  
  
你可以在模板中的`Front-matter`中直接使用以下三个值(其他 Front-matter 不支持)：  
```c  
---  
title: {{ title }}  
date: {{ date }}  
layout: {{ layout }}  
---  
```  
  
这样之后，你通过此模板创建的文章中的这三个变量就有值了。  
  
## 分类和标签  
只有文章支持分类和标签，您可以在 Front-matter 中设置。在其他系统中，分类和标签听起来很接近，但是在 Hexo 中两者有着明显的差别：**分类具有顺序性和层次性**，也就是说 `Foo, Bar` 不等于 `Bar, Foo`；而标签没有顺序和层次。  
```c  
categories:  
- Android  
tags:  
- 小经验  
- 面试心得  
```  
  
**category_map 和 tag_map**  
在`_config.yml`中，我们可以通过`category_map`给一个 category 设置别名，然后在组合 url 的时候，用这个 slug 来替代 category 名称。`tag_map`的功能类似。  
  
例如：  
```c  
category_map: # 分类别名  
  baiqiantao: bqt    #在组合 url 时，用 bqt 代替 baiqiantao  
```  
  
# 命令  
  
```c  
hexo init [folder]    //新建网站。如果没有设置 folder，Hexo 默认在目前的文件夹建立网站  
hexo new [layout] <title>    //新建一篇文章。如果没有设置 layout，默认使用 default_layout 参数代替。如果标题包含空格，请使用引号括起来  
hexo generate  //可以简写为 hexo g，生成静态文件  
hexo publish [layout] <filename>    //发表草稿  
hexo server    //可以简写为 hexo s，启动服务器  
hexo deploy //可以简写为 hexo d，部署网站  
hexo render <file1> [file2] ...    //渲染文件。`-o`设置输出路径  
hexo migrate <type>    //从其他博客系统迁移内容  
hexo clean    //清除缓存文件(db.json)和已生成的静态文件(public)  
hexo list <type>    //列出网站资料  
hexo version    //显示 Hexo 版本  
hexo --safe    //安全模式。在安全模式下，不会载入插件和脚本。当您在安装新插件遭遇问题时，可以尝试以安全模式重新执行  
hexo --debug    //调试模式。在终端中显示调试信息并记录到`debug.log`  
hexo --silent    //简洁模式。隐藏终端信息  
hexo --config custom.yml    //自定义配置文件的路径  
hexo --draft    //显示`source/_drafts`文件夹中的草稿文章  
hexo --cwd /path/to/cwd    //自定义当前工作目录的路径  
```  
  
## 服务器 server  
**[hexo-server](https://github.com/hexojs/hexo-server)**  
Hexo 3.0 把服务器独立成了个别模块，您必须先安装 hexo-server 才能使用。  
```html  
npm install hexo-server --save  
```  
  
安装完成后，输入以下命令以启动服务器，您的网站会在 http://localhost:4000 下启动。在服务器启动期间，Hexo 会监视文件变动并自动更新，您无须重启服务器。  
```c  
hexo server    //可简写为【hexo s】  
```  
  
如果您想要更改端口，或是在执行时遇到了 EADDRINUSE 错误，可以在执行时使用 -p 选项指定其他端口，如下：  
```c  
hexo s -p 5000  
```  
  
**静态模式**  
在静态模式下，服务器只处理 public 文件夹内的文件，而不会处理文件变动，在执行时，您应该先自行执行 hexo generate，此模式通常用于生产环境下。  
```c  
hexo s -s  
```  
  
**自定义 IP**  
服务器默认运行在 `0.0.0.0`，您可以覆盖默认的 IP 设置，如下：  
```c  
hexo s -i 192.168.1.1  
```  
  
指定这个参数后，您就只能通过该IP才能访问站点。  
例如，对于一台使用无线网络的笔记本电脑，除了指向本机的`127.0.0.1`外，通常还有一个`192.168.*.*`的局域网IP，如果像上面那样使用`-i`参数，就不能用`127.0.0.1`来访问站点了。  
对于有公网IP的主机，如果您指定一个局域网IP作为`-i`参数的值，那么就无法通过公网来访问站点。  
  
## 生成文件 generate  
使用 Hexo 生成静态文件快速而且简单。  
```c  
hexo generate    //可简写为【hexo g】  
```  
  
Hexo 能够监视文件变动并立即重新生成静态文件，在生成时会比对文件的 SHA1 checksum，只有变动的文件才会写入。  
```c  
hexo g --watch    //可简写为【hexo g -w】  
```  
  
您可执行下列的其中一个命令，让 Hexo 在生成完毕后自动部署网站，两个命令的作用是相同的。  
```c  
hexo g -d  
hexo d -g  
```  
  
## 部署 deploy  
Hexo 提供了快速方便的一键部署功能，让您只需一条命令就能将网站部署到服务器上。  
```c  
hexo deploy    //可简写为【hexo d】  
```  
  
在开始之前，您必须先在 `_config.yml` 中修改参数，一个正确的部署配置中至少要有 type 参数，例如：  
```c  
deploy:  
  type: git  
```  
  
您可同时使用多个 deployer，Hexo 会依照顺序执行每个 deployer。  
```c  
deploy:  
- type: git  
  repo:  
- type: heroku  
  repo:  
```  
  
> YAML依靠`缩进`来确定元素间的从属关系。因此，请确保每个deployer的缩进长度相同，并且使用`空格`缩进。  
  
# 永久链接 permalinks  
您可以在 `_config.yml` 配置中调整网站的永久链接 **或者在每篇文章的 Front-matter 中指定**。  
  
除了下列变量外，您还 **可使用 Front-matter 中的所有属性**。  
  
| 变量 | 描述 |  
| --- | --- |  
| `:year` | 文章的发表年份（4 位数） |  
| `:month` | 文章的发表月份（2 位数） |  
| `:i_month` | 文章的发表月份（去掉开头的零） |  
| `:day` | 文章的发表日期 (2 位数) |  
| `:i_day` | 文章的发表日期（去掉开头的零） |  
| `:title` | 文件名称 |  
| `:post_title` | 文章标题 |  
| `:id` | 文章 ID |  
| `:category` | **分类。如果文章没有分类，则是 `default_category` 配置信息。** |  
  
您可在 `permalink_defaults` 参数下调整永久链接中各变量的默认值：  
```c  
permalink_defaults:  
  lang: en  
```  
  
## 示例  
假设 `source/_posts` 文件夹中有个 `hello-world.md`，包含以下内容：  
```c  
title: Hello World  
date: 2013-07-14 17:01:34  
bqtdir: 包青天  
categories:  
- foo  
- bar  
```  
  
| 参数 | 结果 | 备注 |  
| --- | --- | --- |  
| `:year/:month/:day/:title/` | 2018/07/14/hello-world |  有多个层级 |   
| `:year-:month-:day-:title.html` | 2018-07-14-hello-world.html | 只有一个层级 |   
| `:category/:title` | foo/bar/hello-world | 不是以【/】结尾的 |   
| `:bqtdir/` | 包青天/ | 完全自定义的变量 |   
  
## 多语种支持  
若要建立一个多语种的网站，您可修改 `new_post_name` 和 `permalink` 参数，如下：  
```c  
new_post_name: :lang/:title.md  
permalink: :lang/:title/  
```  
  
当您建立新文章时，文章会被储存到：  
```c  
hexo new "Hello World" --lang tw  
# => source/_posts/tw/Hello-World.md  
```  
  
而网址会是：http://localhost:4000/tw/hello-world/  
  
# [主题 themes](https://hexo.io/zh-cn/docs/themes)  
[主题列表](https://hexo.io/themes)  
  
创建 Hexo 主题非常容易，您只要在 themes 文件夹内，新增一个任意名称的文件夹，并修改 `_config.yml` 内的 theme 设定，即可切换主题。一个主题可能会有以下的结构：  
```html  
├── _config.yml        主题的配置文件，不同的主题可配置的选项也各不相同。  
├── languages        语言文件夹，请先在项目的`_config.yml`中调整 language 的值  
├── layout            布局文件夹，用于存放主题的模板文件，决定了网站内容的呈现方式  
├── scripts            脚本文件夹，在启动时，Hexo 会载入此文件夹内的 JavaScript 文件  
└── source            资源文件夹，除了模板以外的 Asset，例如 css、js 文件等，都应该放在这个文件夹中  
```  
  
## 国际化 i18n  
internationalization，i18n  
若要让您的网站以不同语言呈现，您可使用国际化功能。请先在`_config.yml`中调整 language 设定，这代表的是预设语言，您也可设定多个语言来调整预设语言的顺位。  
```html  
language:   
- zh-tw  
- en  
```  
  
您可在`front-matter`中指定该页面的语言，也可在`_config.yml`中修改`i18n_dir`设定，让 Hexo 自动侦测。  
i18n_dir 的预设值是 `:lang`，这样 Hexo 会捕获网址中的第一段以检测语言。  
  
## 推荐主题   
官方 [主题推荐](https://hexo.io/themes/) 里面会列出很多主题，点击图片会进入到主题作者的博客里面(预览效果)，点击标题可以进入到主题GitHub仓库里面(使用说明)。目前推荐的有 246 个主题。  
  
也可直接在GitHub [搜索](https://github.com/search?o=desc&q=hexo&s=stars&type=Repositories) 关键字`hexo`，也会精准的搜索出大量高星的主题。  
  
几个流行的主题：  
- [landscape](https://github.com/hexojs/hexo-theme-landscape)：默认主题  
- [NexT](https://github.com/iissnan/hexo-theme-next)：14K，星星最多的主题，[中文文档](http://theme-next.iissnan.com/getting-started.html)  
- [Yilia](https://github.com/litten/hexo-theme-yilia)：6K  
- [indigo](https://github.com/yscoder/hexo-theme-indigo)：2K  
  
其他主题：  
- [material](https://github.com/viosey/hexo-theme-material)：3.5K  
- [tranquilpeak](https://github.com/LouisBarranqueiro/hexo-theme-tranquilpeak)：1.5K  
- [icarus](https://github.com/ppoffice/hexo-theme-icarus)：2K  
- [Yelee](https://github.com/MOxFIVE/hexo-theme-yelee)：1K  
- [Hueman](https://github.com/ppoffice/hexo-theme-hueman)：1K  
- [Jacman](https://github.com/wuchong/jacman)：1K  
  
## 使用主题  
  
这里以使用主题 [Concise](https://github.com/hmybmny/hexo-theme-concise) 为例进行介绍  
  
1、将主题下载到themes目录，如：  
```  
git clone https://github.com/HmyBmny/hexo-theme-concise.git themes/concise   
```  
  
2、修改`_config.yml`中的theme：  
```  
theme: concise  # 默认为landscape  
```  
  
3、必要的设置  
不同的模板，会有很多不同的设置，这里就需要针对模板的使用说明进行修改了。建议通过github找到模板，看下readme。比如`Concise`主题的部分设置：  
```html  
menu:  
  About: http://www.cnblogs.com/baiqiantao  
  Archives: /archives  
```  
  
基本上，这样就能在本机进行预览使用了。  
  
> 修改了模板以后不生效，建议先 hexo clean，然后再 hexo g，只执行 hexo g 可能模板后者静态文件不会被替换。  
  
# [插件 plugins](https://hexo.io/plugins/)  
> 一般不建议使用杂七杂八的插件！  
  
Hexo 有强大的[插件系统](https://hexo.io/zh-cn/docs/plugins)，使您能轻松扩展功能而不用修改核心模块的源码。  
  
在 Hexo 中有两种形式的插件：  
- 脚本 Scripts：如果您的代码很简单，建议您编写脚本，您只需要把 JavaScript 文件放到 `scripts` 文件夹，在启动时就会自动载入。  
- 插件 Packages：如果您的代码较复杂，或是您想要发布到 NPM 上，建议您编写插件。  
  
安装插件：`npm install 插件名称 --save`  
卸载插件：`npm uninstall 插件名称 -save`  
  
## 链接持久化插件  
[permalink-pathed-title](https://github.com/EqualMa/hexo-plugin-permalink-pathed-title)  
> 实现方案：使用文件的相对路径生成链接(推荐)  
  
[abbrlink](https://github.com/rozbo/hexo-abbrlink)，[相关介绍](https://post.zz173.com/detail/hexo-abbrlink.html)  
> 实现方案：对`标题`+`时间`进行`md5`然后再转`base64`，然后给每篇文章的`front-matter`中自动增加`abbrlink`属性  
> 类似：【abbrlink: 4a17b156】  
  
[Hexo-UUID](https://github.com/chekun/hexo-uuid)，[相关介绍](https://chekun.me/post/hexo-uuid/)  
> 实现方案：hook `hexo new`命令(意味着必须在命令行中创建文章)，然后给每篇文章的`front-matter`中自动增加`uuid`属性  
> 类似：【uuid: 2f2dd790-1e71-11e6-95e1-ffc09ba0b003】  
  
Tips：  
> 百度蜘蛛抓取网页的规则: 对于蜘蛛说网页权重越高、信用度越高抓取越频繁，例如网站的首页和内页。蜘蛛先抓取网站的首页，因为首页权重更高，并且大部分的链接都是指向首页。然后通过首页抓取网站的内页，并不是所有内页蜘蛛都会去抓取。  
  
> 搜索引擎认为对于一般的中小型站点，3层足够承受所有的内容了，所以蜘蛛经常抓取的内容是前三层，而超过三层的内容蜘蛛认为那些内容并不重要，所以不经常爬取。出于这个原因所以permalink后面跟着的最好不要超过2个斜杠。  
  
## 自动生成分类插件  
[auto-category](https://github.com/xu-song/hexo-auto-category)  
[auto-category 介绍](https://blog.eson.org/pub/e2f6e239/)  
[类似插件：directory-category](https://github.com/zthxxx/hexo-directory-category)  
  
一种自动生成 categories 的插件，根据 Markdown 文件在文件目录自动分类，即自动生成 Markdown 的`front-matter`中的`categories`变量。  
  
例如，对于博客 `source/_post/web/framework/hexo.md`，该插件会自动生成以下categories：  
```c  
categories:  
  - web  
  - framework  
```  
  
安装：`npm install hexo-auto-category --save`  
修改配置文件，添加如下内容：  
```c  
# Generate categories from directory-tree  
auto_category:  
 enable: true  
 depth:  # the depth of directory-tree you want to generate, should > 0  
```  
  
# 配置文件 config  
```yml  
# Site 网站  
title: 白乾涛的技术博客  # 网站标题  
subtitle: 白乾涛的Android开发之旅  # 网站副标题  
description: Android Java 前端 程序员 技术 博客 GitHub baiqiantao  # 主要用于SEO  
keywords: Android Java 前端 程序员 技术 博客 GitHub baiqiantao # 主要用于SEO  
author: 白乾涛  # 用于主题中显示文章的作者  
language: zh-Hans # 网站使用的语言，中文一般为【zh-CN】或【zh-Hans】，可用值决于你themes\concise\languages下有哪些文件  
timezone:  # 网站时区，默认使用您电脑的时区  
  
# URL 网址  
url: http://yoursite.com  # 网址  
root: /  # 如果您的网站存放在子目录中，例如 http://bqt.com/blog，则请将您的 url 设为 http://bqt.com/blog 并把 root 设为 /blog/  
permalink: :bqtdir/  # 文章的永久链接，格式【:year/:month/:day/:title/】注意，要以/或.html结尾  
permalink_defaults:  # 永久链接中各部分的默认值  
  
# Directory 目录  
source_dir: source  # 资源文件夹，这个文件夹用来存放内容。  
public_dir: public  # 公共文件夹，这个文件夹用于存放生成的站点文件。  
tag_dir: tags  # 标签文件夹  
archive_dir: archives  # 归档文件夹  
category_dir: categories  # 分类文件夹  
code_dir: downloads/code  # Include code 文件夹  
i18n_dir: :lang  # 国际化文件夹，捕获网址中的第一段以检测语言  
skip_render:  # 跳过指定文件的渲染，您可使用 glob 表达式来匹配路径。  
  
# Writing 文章  
new_post_name: :title.md # 新文章的文件名称，默认值【:title.md】  
default_layout: post  # 预设布局，【post】  
auto_spacing: false  # 在中文和英文之间加入空格，false  
titlecase: false # 把标题转换为 title case，false  
external_link: true # 在新标签中打开链接，true  
filename_case: 0  # 把文件名称转换为 (1) 小写或 (2) 大写，0  
render_drafts: false  # 显示草稿，false  
post_asset_folder: false  # 启动 Asset 文件夹，【false】  
relative_link: false  # 把链接改为与根目录的相对位址，false  
future: true  # 显示未来的文章，true  
highlight:  # 代码块高亮设置  
  enable: true  
  line_number: true  
  auto_detect: false  
  tab_replace:  
  
# Home page setting 首页设置  
index_generator:  
  path: '' # 博客索引页面(index)的根路径，默认=''  
  per_page: 10 # 每页显示的帖子，0 =禁用分页  
  order_by: -date # 排序方式，默认情况下按日期降序排序  
  
# Category & Tag 分类 & 标签  
default_category: blog # 当没有分类时的默认分类，uncategorized  
category_map: # 分类别名，要在配置文件中获取category的值请使用【:category】，不要使用【:categories】  
  baiqiantao: bqt  
tag_map: # 标签别名  
  
# Date / Time format 日期 / 时间格式，Hexo使用Moment.js来解析和显示日期  
date_format: YYYY-MM-DD  
time_format: HH:mm:ss  
  
# Pagination 分页  
per_page: 10 # 每页显示的文章量，0 = 关闭分页功能，10  
pagination_dir: page # 分页目录，page  
  
# Deployment 部署  
deploy:  
  type: git  
  repo: git@github.com:baiqiantao/baiqiantao.github.io.git  
  branch: master  
  
# Extensions 扩展  
theme: next  # 当前主题名称，值为false时禁用主题，默认为【landscape】  
```  
  
2019-3-14  
