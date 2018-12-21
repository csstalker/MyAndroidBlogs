| Markdown版本笔记 | 我的GitHub首页 | 我的博客 | 我的微信 | 我的邮箱 |  
| :------------: | :------------: | :------------: | :------------: | :------------: |  
| [MyAndroidBlogs][Markdown] | [baiqiantao][GitHub] | [baiqiantao][博客] | bqt20094 | baiqiantao@sina.com |  
  
[Markdown]:https://github.com/baiqiantao/MyAndroidBlogs  
[GitHub]:https://github.com/baiqiantao  
[博客]:http://www.cnblogs.com/baiqiantao/  
  
Git SVN 版本控制 简介 总结 MD  
***  
目录  
===  

- [Git 使用准备](#git-使用准备)
	- [主流的 Git 托管网站](#主流的-git-托管网站)
	- [Git 的安装](#git-的安装)
	- [TortoiseGit 的安装](#tortoisegit-的安装)
	- [Git 账户设置](#git-账户设置)
- [Git 的常用功能](#git-的常用功能)
	- [基本使用](#基本使用)
	- [分支管理](#分支管理)
	- [分支操作](#分支操作)
	- [让 TortoiseGit 记住用户名和密码](#让-tortoisegit-记住用户名和密码)
- [【192.168.20.9】【baiqiantao】【http://192.168.20.9/】](#【192168209】【baiqiantao】【http192168209】)
- [【git.oschina.net】【baiqiantao】【http://git.oschina.net/baiqiantao】](#【gitoschinanet】【baiqiantao】【httpgitoschinanetbaiqiantao】)
- [【code.csdn.net】【baiqiantao】【https://code.csdn.net/baiqiantao】](#【codecsdnnet】【baiqiantao】【httpscodecsdnnetbaiqiantao】)
- [【github.com】【baiqiantao】【https://github.com/baiqiantao】](#【githubcom】【baiqiantao】【httpsgithubcombaiqiantao】)
- [svn 项目迁移到 git](#svn-项目迁移到-git)
	- [建立 svn 账号与 git 账号的映射](#建立-svn-账号与-git-账号的映射)
	- [svn 转换为 git](#svn-转换为-git)
- [Git 的忽略文件 .gitignore](#git-的忽略文件-gitignore)
	- [添加忽略规则的三种方式](#添加忽略规则的三种方式)
	- [.gitignore 文件时的格式](#gitignore-文件时的格式)
- [Git 产生背景、基本功能简介](#git-产生背景、基本功能简介)
	- [Git的诞生](#git的诞生)
	- [集中式vs分布式](#集中式vs分布式)
	- [创建版本库](#创建版本库)
- [SVN 常用方式总结](#svn-常用方式总结)
	- [SVN 服务端 和 客户端 安装细节](#svn-服务端-和-客户端-安装细节)
	- [在 Eclipse中设置 SVN 忽略文件](#在-eclipse中设置-svn-忽略文件)
	- [在 TortoiseSVN 中设置忽略文件](#在-tortoisesvn-中设置忽略文件)
	- [在Eclipse上创建SVN控制的工程](#在eclipse上创建svn控制的工程)
	- [将SVN路径中的项目导入eclipse](#将svn路径中的项目导入eclipse)
	- [将本地SVN服务端工程导入eclipse](#将本地svn服务端工程导入eclipse)
	- [比较指定两个版本间的差异](#比较指定两个版本间的差异)
	- [通过VisualSvn Server创建代码库](#通过visualsvn-server创建代码库)
	- [迁入本地项目到SVN服务器代码库](#迁入本地项目到svn服务器代码库)
	- [使用eclipse解决文件冲突](#使用eclipse解决文件冲突)
	- [使用SVN解决文件冲突](#使用svn解决文件冲突)
	- [eclipse导入svn项目版本兼容问题](#eclipse导入svn项目版本兼容问题)
  
# Git 使用准备  
## 主流的 Git 托管网站  
- [GitLab](https://gitlab.com/)，主流网站，私有仓库也完全免费，功能更强大，页面精美，操作方便  
- [GitHub](https://github.com/)，最著名的免费Git托管网站，缺点是免费的不支持私有项目  
- [OSChina](https://git.oschina.net/)，开源中国代码托管，支持公有项目和私有项目，成员无限，项目1000个  
- [CSDN](https://code.csdn.net/) ，支持公有项目和私有项目，提供2G存储空间  
- [Bitbucket](https://bitbucket.org/)，Bitbucket是国外一个比较著名的Git托管网站，免费用户支持公有和私有项目  
  
## Git 的安装  
[Git 下载1](https://git-for-windows.github.io/)  
[Git 下载2](https://git-scm.com/)  
[国内镜像](https://pan.baidu.com/s/1kU5OCOB#list/path=%2Fpub%2Fgit)  
  
Git是什么？  
Git是目前世界上最先进的分布式版本控制系统（没有之一）。  
  
除了安装路径，全部按默认即可：  
--> 选择安装路径 --> 使用默认的组件 --> 创建开始菜单文件夹 --> 选择使用Git的命令行模式，选择默认`Git Bash`模式会创建一个快捷命令行：  
![](index_files/8822d6fa-9255-45c3-b81e-97349e4a4ac9.png)  
  
--> 选择换行格式，默认为第一个跨平台样式：  
![](index_files/ac03ae2f-f262-448b-9f28-3cac9e3769e9.png)  
  
--> Finish 安装完成，桌面会生成一个快捷图标，安装成功。  
  
在安装Git的过程中一般都会勾选`Git Gui Here`、`Git Bash Here`选项，如果你以后是使用`TortoiseGit`，可能并不需要桌面右击中有这两个选项：  
  
> Git Gui Here：是否要在这里面使用 git shell 的图形用户界面操作？  
Git Bash Here：是否要在这里面使用 git shell 的命令行工具操作？  
  
## TortoiseGit 的安装  
[TortoiseGit 下载](https://tortoisegit.org/download/)  
  
TortoiseGit 是什么？  
TortoiseGit 就是大名鼎鼎的小乌龟，一个图形化的Git操作程序。  
其实 TortoiseGit 只是一个程序壳，其必须依赖 Git Core，所以安装 TortoiseGit 前需要先安装 Git。  
  
如果你是一名软件开发人员，那请你一定要用命令行（如使用 Git Bash Here），不要用可视化工具，用可视化工具的速度你会难以忍受的。  
  
--> 选择SSH客户端，可以选择 TortoiseGitPlink(位于TortoiseGit安装目录`/bin` 下)，也可以选择 Git 默认的SSH客户端(位于Git安装目录`/bin/ssh.exe`，如果配置了 Path，那直接是 ssh.exe)。我们使用默认即可:  
![](index_files/e72ee8ff-fce8-4344-87b5-d77bf168a54c.png)  
  
--> 选择安装路径及组件 --> 点击Install，开始安装 --> Win 7下会弹出一个确认安装的提示框，确认就可以了 --> 安装完成。  
  
和SVN一样，官网有提供汉化包，注意要和 TortoiseGit 的版本一致才可以。  
  
## Git 账户设置  
Git 的设置文件有三个级别：本系统所有用户：`--system`，当前用户所有仓库：`--global`，本仓库：`local`  
- 1、位于Git安装目录下的`gitconfig`文件，如`C:\Program Files\Git\mingw64\etc\gitconfig`，包含了适用于系统所有用户和所有库的值。如果你传递参数选项`--system`给 `git config`，它将读写这个文件。  
- 2、位于用户目录下的`.gitconfig`文件 ，如`C:\Users\Administrator\.gitconfig`，包含了具体到某个用户配置的值。如果你传递参数选项`--global`给 `git config`，它将读写这个文件。  
- 3、位于某个由Git控制的`项目`下隐藏的`.git`目录下的`config`文件，包含了具体到某个项目的配置。  
  
# Git 的常用功能  
## 基本使用  
> repository [rɪˈpɑ:zətɔ:ri] n. 仓库; 贮藏室; 博物馆; 亲信;  
  
你可以将本地一个目录当做仓库(repository)，然后把另一个目录当做工作区。  
  
1、创建repository  
进入到你要当仓库的磁盘目录下，点鼠标右键，选择`Git Create repository here`创建仓库，把`Make it Bare`勾上就表示这是一个单纯的仓库，写代码的工作区放在另一个地方。  
选择【Git Init Here】或【Git Create repository here】命令但不勾选【Make it Bare】只会建立工作区（只生成.git文件夹）  
```c  
git init --bare  
```  
  
2、创建工作区  
进入到你要做工作区的那个磁盘的目录下，然后右键选择`Git Clone`，`URL`选择上面我们创建的库的目录，`Directory`选择我们的工作区，也就是你要放代码的目录，其他的不用管。成功之后会在此目录下生成一个隐藏的.git目录。  
```c  
git clone <repository> [directory]   
```  
  
3、向工作区中执行add、commit、push操作  
- add就是把这个文件加入到Git的关注列表之中，在目录的空白处点击右键选择TortoiseGit，也可以批量add文件。  
- add后必须commit才算是提交到了本地git服务器，commit的文件是保存在本地自己单独的工作区目录中的  
- commit后必须push才算是提交到了远程git服务器(仓库)，push的文件是保存在所有用户共享的repository仓库中的  
  
```c  
git add app/README.md doc  
git commit -v -m [message]  
```  
  
## 分支管理  
相比同类软件，Git有很多优点，其中很显著的一点，就是版本的【分支和合并】十分方便。一些传统的版本管理软件，其分支操作实际上是生成一份现有代码的`物理拷贝`，而Git是只生成一个指向当前版本（又称"快照"）的`指针`，因为少了大量文件的拷贝操作，所以分支合并是速度非常快。  
  
按如下分支管理的策略，可以使得版本库的演进保持简洁，主干清晰，各个分支各司其职、井井有条。  
  
**一个主分支master**  
代码库应该有一个、且仅有一个主分支。所有提供给用户使用的正式版本，都在这个主分支上发布。  
Git主分支的名字，默认叫做`master`，它是自动建立的，版本库初始化以后，默认就是在主分支在进行开发。  
  
**不同的开发分支develop**  
主分支只用来发布重大版本，日常开发应该在另一条分支上完成。我们把开发用的分支，叫做develop。这个分支可以用来生成代码的最新隔夜版本（nightly）。  
如果想正式对外发布，就在master分支上，对develop分支进行合并。  
  
## 分支操作  
> branch  [bræntʃ]  n. 分支; 树枝; 部门，分科; 支流;   vi. 分支形成; 分支扩张;  [计] 下分支的指令;   vt.  使分支; 使分叉;  
merge   [mɜ:rdʒ]  vt.融入; （使）混合; 相融; 渐渐消失在某物中;  
  
Git 的主体思路就是不断的建立分支，可靠以后再合并到主分支里面，从而使得整个版本不断更新。  
  
1、创建版本分支  
在根目录上右键【Create Branch】，在弹出对话框中为此分支版本起名，然后ok即可创建一个分支。  
  
2、切换版本分支  
在根目录上右键【Switch/Checkout，切换/检出】，通过【Switch To】下面的选项选择要切换到的分支，然后ok即可。  
  
3、修改版本分支  
更改后提交时会发现菜单变了，提交的目的地变成了刚才我们切换到的分支  
提交后，右键【Show log】，里面就出现了两条版本路线。push到服务器后，服务器也可以查看分支了。  
  
4、合并版本分支【在A分支合并B分支，可把B分支中更新的内容拉到A分支】  
比如，现在想把A分支合并到主版本 master 中，现在就要做如下操作：  
- 先切换到主版本分支：右键【Switch/Checkout，切换/检出】，弹出的对话框中选择 master，点击OK  
- 再和并A分支：切换后，在结果页中或右键点击【Merge，合并】，在弹出的对话框中【From】里选择A分支，填写日志，点击OK  
- 最后 push 到服务器。完成后，A分支中更新的内容就同步到了主版本 master 分支中了。  
  
## 让 TortoiseGit 记住用户名和密码  
TortoiseGit 在提交时总是会提示你输入用户名密码，非常麻烦，解决方案如下：  
Windows中添加一个变量名为【HOME】值为【%USERPROFILE%】的用户变量  
在开始 --> 运行中打开【%Home%】，新建一个名为【_netrc】的文件  
用记事本打开此文件，输入Git服务器名、用户名、密码，并保存，格式如下：  
```c  
#【192.168.20.9】【baiqiantao】【http://192.168.20.9/】  
#【git.oschina.net】【baiqiantao】【http://git.oschina.net/baiqiantao】  
#【code.csdn.net】【baiqiantao】【https://code.csdn.net/baiqiantao】  
#【github.com】【baiqiantao】【https://github.com/baiqiantao】  
machine github.com    #git服务器名称  
login baiqiantao      #git帐号  
password ********     #git密码  
```  
  
以后提交时就不用输入用户名密码了！  
  
以上是针对全局设置的，针对某个具体项目进行配置的话，还可以按照如下方式。  
  
修改项目根目录下隐藏的【.git】目录下的【config】文件，修改前的url为：  
```c  
url = https://github.com/baiqiantao/KotlinTest.git  
```  
我们将url修改为：  
```c  
url = https://用户名:密码@github.com/baiqiantao/KotlinTest.git  
```  
这样就能针对当前项目记录用户名和密码了。  
  
# svn 项目迁移到 git  
[参考1](https://www.lovelucy.info/codebase-from-svn-to-git-migration-keep-commit-history.html)  
[参考2](https://www.jianshu.com/p/a68ff08f5856)  
[参考3](https://www.cnblogs.com/rwxwsblog/p/5725710.html)  
  
最近在将公司老旧的 svn 管理的项目迁移到 gitlab，由于之前一直是由 svn 做版本控制，最简单的方式是将 svn 的内容 export 出来，然后添加到 gitlab 即可。但是，如果 svn 用的时间很长了，而且很多 commit，我们希望保存 svn 的 commit 信息以便做版本的控制和比较。下面就是详细的迁移步骤！  
  
## 建立 svn 账号与 git 账号的映射  
保留原SVN仓库的Commit等历史记录，需要获取到SVN使用的作者名字列表，可以通过如下方式获取：  
SVN -> 显示日志 -> 全部显示 -> 统计 -> 里面可以查看所有用户：  
  
格式：  
```c  
SVN用户名 = git用户名 <git邮箱>  
```  
  
例如：  
```c  
baiqiantao = baiqiantao <baiqiantao@sina.com>  
wanglulu = wanglulu <0909082401@163.com>  
tangyongchao = tangyongchao <baiqt@asp.citic.com>  
zhaowei = zhaowei <zhaow@asp.citic.com>  
keyangming = keyangming <keym@asp.citic.com>  
```  
  
网上有人说用这个命令可以自动获取到用户名列表并保存到指定文件中：  
```c  
svn log --xml | grep author | sort -u | perl -pe 's/.*>(.*?)<.*/$1 = /' > /root/users.txt  
```  
> 这会将日志输出为 XML 格式，然后保留作者信息行、去除重复、去除 XML 标记，然后将输出重定向到你的 users.txt 文件中，这样就可以在每一个记录后面加入对应的 Git 用户数据。  
  
但实际上运行后提示：'grep' 不是内部或外部命令，也不是可运行的程序或批处理文件。  
  
## svn 转换为 git  
现在从 SVN 仓库中拉取所有数据：  
```c  
git svn clone --stdlayout --no-metadata -A users.txt svn地址 存放git的地址  
```  
这个命令将会在你指定的目录中新建一个 Git repo，并开始从 SVN 中拉取代码。  
  
请注意`--stdlayout`参数表示你的项目在 SVN 中是常见的 `trunk/branches/tags` 目录结构，如果不是，那你需要使用 `--tags, --branches, --trunk` 参数（请通过 git svn help 自行了解）。  
  
再后面的参数是 SVN 的地址，一些常见协议都是支持的。注意这个 URL 应该指向项目的 base repository，不要指到了 /trunk, /tag 或 /branches 里。  
  
如果出现用户名没找到，更新你的 `users.txt` 文件，然后  
```c  
cd 存放git的地址  
git svn fetch  
```  
  
如果你的项目非常大，你可能需要重复上面的命令好几次，直到所有的 SVN commit 都被抓下来了  
  
完成后，Git 将会 checkout SVN 的 trunk 到一个新的 Git branch，而其他的 SVN branch 将会设为 Git remote。  
  
# Git 的忽略文件 .gitignore  
[.gitignore项目](https://github.com/github/gitignore)  
[适用 Android 的忽略文件](https://github.com/github/gitignore/blob/master/Android.gitignore)  
  
## 添加忽略规则的三种方式  
[添加忽略规则](https://help.github.com/articles/ignoring-files/)   
  
*From time to time, there are files you don't want Git to check in to GitHub. There are a few ways to tell Git which files to ignore.*  
有时候，有一些文件你不希望Git检入GitHub。有几种方法可以告诉Git忽略哪些文件。  
  
**局部 Create a local .gitignore**  
*If you create a file in your repository named .gitignore, Git uses it to determine which files and directories to ignore, before you make a commit.*  
如果您在存储库中创建一个名为`.gitignore`的文件，Git会在您进行提交之前使用它来确定忽略哪些文件和目录。  
  
*A .gitignore file should be committed into your repository, in order to share the ignore rules with any other users that clone the repository.*  
一个`.gitignore`文件应该被提交到你的仓库中，以便与任何其他克隆仓库的用户共享这个忽略规则。  
  
*GitHub maintains an official list of recommended .gitignore files for many popular operating systems, environments, and languages in the github/gitignore public repository.*  
GitHub在 [.gitignore](https://github.com/github/gitignore) 公共仓库中维护了一个官方的列表，(列表里面包含了在使用)许多流行的操作系统、环境和语言时的推荐的`.gitignore`文件。  
  
>In Terminal, navigate to the location of your Git repository.  
Enter `touch .gitignore` to create a .gitignore file.  
  
The Octocat has [a Gist containing some good rules](https://gist.github.com/octocat/9257657) to add to this file.  
  
*If you already have a file checked in, and you want to ignore it, Git will not ignore the file if you add a rule later. In those cases, you must untrack the file first, by running the following command in your terminal:*  
如果你想忽略一个已经出于检入状态的文件，即使你稍后添加了一个(忽略)规则，Git也将不会忽略这个文件。在这种情况下，您必须先在终端中运行以下命令，以解除文件：  
```  
git rm --cached FILENAME  # 停止追踪指定文件，但该文件会保留在工作区  
```  
  
**全局 Create a global .gitignore**  
*You can also create a global .gitignore file, which is a list of rules for ignoring files in every Git repository on your computer. For example, you might create the file at ~/.gitignore_global and add some rules to it.*  
您也可以创建一个全局的`.gitignore`文件，这是一个忽略计算机上每个Git仓库中文件的规则列表。例如，您可以在`~/.gitignore_global`中创建一个文件，并为其添加一些规则。  
  
> Open Terminal.  
Run the following command in your terminal:  
`git config --global core.excludesfile ~/.gitignore_global`  
  
The Octocat has [a Gist containing some good rules](https://gist.github.com/octocat/9257657) to add to this file.  
  
**个人 Explicit repository excludes**  
> explicit [ɪkˈsplɪsɪt]  adj. 明确的，清楚的; 直言的; 详述的; 不隐瞒的;  
exclude  [ɪk'sklu:d]  vt. 排斥;排除，不包括;驱除，赶出   
  
*If you don't want to create a .gitignore file to share with others, you can create rules that are not committed with the repository. You can use this technique for locally-generated files that you don't expect other users to generate, such as files created by your editor.*  
如果您不想创建一个与其他人共享的`.gitignore`文件，那么你可以创建一些不与仓库一起提交的规则。您可以将此技术用于不希望其他用户生成的本地生成的文件，例如编辑器创建的文件。  
  
*Use your favorite text editor to open the file called .git/info/exclude within the root of your Git repository. Any rule you add here will not be checked in, and will only ignore files for your local repository.*  
使用你最喜欢的文本编辑器打开Git仓库根目录下名为`.git/info/exclude`的文件。您在此处添加的任何规则都不会被检入，并且只会忽略本地仓库的文件。  
  
> In Terminal, navigate to the location of your Git repository.  
Using your favorite text editor, open the file `.git/info/exclude`.  
  
## .gitignore 文件时的格式  
基本规范：  
- 所有空行或者以注释符号`#`开头的行都会被 Git 忽略。   
- 匹配模式最后跟反斜杠`/`说明要忽略的是`目录`  
- 要`忽略`指定模式以外的文件或目录，可以在模式前加上惊叹号`!`取反  
- 可以使用标准的 glob 模式匹配  
  
所谓的 glob 模式是指 shell 所使用的简化了的正则表达式：  
- 星号`*`匹配`零个或多个`任意字符  
- 问号`?`只匹配`一个`任意字符  
- 方括号`[]`匹配`任何一个`列在方括号中的字符  
- 如果在方括号中使用短划线`-`分隔两个字符，表示所有在这两个字符范围内的都可以匹配，如`[0-9]`表示匹配所有 0 到 9 的数字  
  
# Git 产生背景、基本功能简介  
## Git的诞生  
很多人都知道，`Linus`在`1991`年创建了开源的`Linux`，从此，Linux系统不断发展，已经成为最大的服务器系统软件了。  
  
Linus虽然创建了Linux，但Linux的壮大是靠全世界热心的志愿者参与的，这么多人在世界各地为Linux编写代码，那Linux的代码是如何管理的呢？  
  
事实是，在`2002年以前`，世界各地的志愿者把源代码文件通过`diff`的方式发给Linus，然后由Linus本人通过手工方式合并代码！  
  
你也许会想，为什么Linus不把Linux代码放到版本控制系统里呢？不是有`CVS、SVN`这些免费的版本控制系统吗？因为Linus坚定地反对CVS和SVN，这些`集中式的版本控制系统`不但速度慢，而且必须联网才能使用。有一些商用的版本控制系统，虽然比CVS、SVN好用，但那是付费的，和Linux的开源精神不符。  
  
不过，到了2002年，Linux系统已经发展了十年了，代码库之大让Linus很难继续通过手工方式管理了，社区的弟兄们也对这种方式表达了强烈不满，于是Linus选择了一个商业的版本控制系统`BitKeeper`，BitKeeper的东家`BitMover`公司出于人道主义精神，授权Linux社区免费使用这个版本控制系统。  
  
安定团结的大好局面在2005年就被打破了，原因是Linux社区牛人聚集，不免沾染了一些梁山好汉的江湖习气。开发`Samba`的`Andrew`试图破解BitKeeper的协议（这么干的其实也不只他一个），被BitMover公司发现了（监控工作做得不错！），于是BitMover公司怒了，要收回Linux社区的免费使用权。  
  
Linus可以向BitMover公司道个歉，保证以后严格管教弟兄们，嗯，这是不可能的。实际情况是这样的：  
  
Linus花了`两周时间`自己用`C`写了一个`分布式版本控制系统`，这就是`Git`！`一个月`之内，Linux系统的源码已经由Git管理了！牛是怎么定义的呢？大家可以体会一下。  
  
Git迅速成为最流行的分布式版本控制系统，尤其是`2008年`，`GitHub`网站上线了，它为开源项目免费提供Git存储，无数开源项目开始迁移至GitHub，包括`jQuery，PHP，Ruby`等等。  
  
历史就是这么偶然，如果不是当年BitMover公司威胁Linux社区，可能现在我们就没有免费而超级好用的Git了。  
  
## 集中式vs分布式  
Linus一直痛恨的CVS及SVN都是`集中式`的版本控制系统，而Git是`分布式`版本控制系统，集中式和分布式版本控制系统有什么区别呢？  
  
先说集中式版本控制系统，`版本库是集中存放在中央服务器的`，而干活的时候，用的都是自己的电脑，所以要先从中央服务器取得最新的版本，然后开始干活，干完活了，再把自己的活推送给中央服务器。中央服务器就好比是一个图书馆，你要改一本书，必须先从图书馆借出来，然后回到家自己改，改完了，再放回图书馆。  
  
集中式版本控制系统最大的毛病就是`必须联网才能工作`，如果在局域网内还好，带宽够大，速度够快，可如果在互联网上，遇到网速慢的话，可能提交一个10M的文件就需要5分钟，这还不得把人给憋死啊。  
  
那分布式版本控制系统与集中式版本控制系统有何不同呢？首先，分布式版本控制系统根本`没有中央服务器`，`每个人的电脑上都是一个完整的版本库`，这样，你工作的时候，就不需要联网了，因为版本库就在你自己的电脑上。既然每个人电脑上都有一个完整的版本库，那多个人如何协作呢？比方说你在自己电脑上改了文件A，你的同事也在他的电脑上改了文件A，这时，你们俩之间只需`把各自的修改推送给对方`，就可以互相看到对方的修改了。  
  
和集中式版本控制系统相比，`分布式版本控制系统的安全性要高很多`，因为每个人电脑里都有完整的版本库，某一个人的电脑坏掉了不要紧，随便从其他人那里复制一个就可以了。而集中式版本控制系统的中央服务器要是出了问题，所有人都没法干活了。  
  
在实际使用分布式版本控制系统的时候，其实`很少在两人之间的电脑上推送版本库的修改`，因为可能你们俩不在一个局域网内，两台电脑互相访问不了，也可能今天你的同事病了，他的电脑压根没有开机。因此，`分布式版本控制系统通常也有一台充当“中央服务器”的电脑，但这个服务器的作用仅仅是用来方便“交换”大家的修改，没有它大家也一样干活，只是交换修改不方便而已`。  
  
当然，Git的优势不单是不必联网这么简单，后面我们还会看到Git`极其强大的分支管理`，把SVN等远远抛在了后面。  
  
`CVS作为最早的开源而且免费的集中式版本控制系统`，直到现在还有不少人在用。由于CVS自身设计的问题，会造成提交文件不完整，版本库莫名其妙损坏的情况。`同样是开源而且免费的SVN`修正了CVS的一些稳定性问题，是目前用得最多的集中式版本库控制系统。  
  
除了免费的外，还有收费的集中式版本控制系统，比如`IBM的ClearCase`（以前是Rational公司的，被IBM收购了），特点是安装比Windows还大，运行比蜗牛还慢，能用ClearCase的一般是世界500强，他们有个共同的特点是财大气粗，或者人傻钱多。  
  
`微软`自己也有一个集中式版本控制系统叫`VSS`，集成在Visual Studio中。由于其反人类的设计，连微软自己都不好意思用了。  
  
分布式版本控制系统除了Git以及促使Git诞生的BitKeeper外，还有类似Git的`Mercurial`和`Bazaar`等。这些分布式版本控制系统各有特点，但最快、最简单也最流行的依然是Git！  
  
## 创建版本库  
什么是版本库呢？`版本库又名仓库，英文名repository`，你可以简单理解成一个目录，这个目录里面的所有文件都可以被Git管理起来，每个文件的修改、删除，Git都能跟踪，以便任何时刻都可以追踪历史，或者在将来某个时刻可以“还原”。  
  
所以，创建一个版本库非常简单，首先，选择一个合适的地方，创建一个空目录  
如果你使用Windows系统，为了避免遇到各种莫名其妙的问题，请确保目录名（包括父目录）不包含中文。  
  
第二步，通过git init命令把这个目录变成Git可以管理的仓库：  
```c  
git init  
```  
  
瞬间Git就把仓库建好了，而且告诉你是一个空的仓库（empty Git repository），细心的读者可以发现当前目录下多了一个`.git`的目录，这个目录是Git来跟踪管理版本库的，没事千万不要手动修改这个目录里面的文件，不然改乱了，就把Git仓库给破坏了。  
  
如果你没有看到.git目录，那是因为这个目录默认是隐藏的，用`ls -ah`命令就可以看见。  
  
也不一定必须在空目录下创建Git仓库，选择一个已经有东西的目录也是可以的。  
  
**把文件添加到版本库**  
首先这里再明确一下，所有的版本控制系统，其实只能跟踪`文本文件`的改动，比如TXT文件，网页，所有的程序代码等等，Git也不例外。版本控制系统可以告诉你每次的改动，比如在第5行加了一个单词“Linux”，在第8行删了一个单词“Windows”。而图片、视频、Word、Excel这些`二进制文件`，虽然也`能由版本控制系统管理，但没法跟踪文件的变化`，只能把二进制文件每次改动串起来，也就是只知道图片从100KB改成了120KB，但到底改了啥，版本控制系统不知道，也没法知道。  
  
因为文本是有编码的，如果没有历史遗留问题，强烈建议使用标准的`UTF-8`编码，所有语言使用同一种编码，既没有冲突，又被所有平台所支持。  
  
使用Windows的童鞋要特别注意：  
千万不要使用Windows自带的记事本编辑任何文本文件。原因是Microsoft开发记事本的团队使用了一个非常弱智的行为来保存`UTF-8`编码的文件，他们自作聪明地在每个文件开头添加了`0xefbbbf`（十六进制）的字符，你会遇到很多不可思议的问题，比如，网页第一行可能会显示一个“?”，明明正确的程序一编译就报语法错误等等，都是由记事本的弱智行为带来的。  
  
言归正传，现在我们编写一个readme.txt文件，内容如下：  
  
    Git is a version control system.  
    Git is free software.  
  
把一个文件放到Git仓库只需要两步。  
  
第一步，用命令`git add`告诉Git，把文件添加到仓库：  
```c  
git add readme.txt  
```  
执行上面的命令，没有任何显示，这就对了，Unix的哲学是`没有消息就是好消息`，说明添加成功。  
  
第二步，用命令`git commit`告诉Git，把文件提交到仓库：  
```c  
git commit -m "wrote a readme file"  
```  
简单解释一下git commit命令，-m后面输入的是本次提交的说明，可以输入任意内容，当然最好是有意义的，这样你就能从历史记录里方便地找到改动记录。  
  
嫌麻烦不想输入-m "xxx"行不行？确实有办法可以这么干，但是强烈不建议你这么干，因为输入说明对自己对别人阅读都很重要。实在不想输入说明的童鞋请自行Google，我不告诉你这个参数。  
  
git commit 命令执行成功后会告诉你，1 file changed：1个文件被改动（我们新添加的readme.txt文件）；2 insertions：插入了两行内容（readme.txt有两行内容）。  
  
# SVN 常用方式总结  
[VisualSvn Server](https://www.visualsvn.com/server/download/)  
[TortoiseSVN](https://tortoisesvn.net/downloads.html)  
  
## SVN 服务端 和 客户端 安装细节  
VisualSvn Server 是Svn的服务器端，是免费的。  
VisualSvn 是Svn的客户端，和 Visual Studio 集成在一起，但不是免费的，通常我们用的客户端是 TortoiseSVN，是免费的。  
  
VisualSvn Server安装中需要注意的点：  
1、选择默认配置，服务和控制台组件方式：  
![](index_files/d3012dfa-fdf7-4896-8862-46ff31836665.png)  
  
2、安装标准版 Standard Edition：  
![](index_files/7c79924e-a19b-4d7e-ac55-707864021ecf.png)  
  
*   Location是程序的安装路径  
*   Repositories 是存放SVN仓库的路径(注意：指定一个空的文件夹)  
*   端口使用默认，协议有可能不能勾选https选项  
  
    妹的，装了一晚上都没成功，最后把https改成http后竟然就可以了，坑爹呀  
  
3、如果要卸载VisualSvn Server，需先进入`服务管理器`把 VisualSvn Server 服务停掉，不然在卸载中途会说进程还在运行不能卸载  
  
    开始-->运行-->输入 services.msc  
  
![](index_files/3628be58-c8aa-4d18-a042-69a5c5e35cef.png)  
  
客户端 [TortoiseSVN](https://tortoisesvn.net/downloads.html) 安装中需要注意的点：  
*   安装中Win7下会弹出一个对话框，问是否确认安装程序，选择"是"即可  
*   完成安装后会要求你重启电脑  
*   重启之后右键点击桌面选项菜单，如果发现中有TortoiseSVN，就表明安装成功了  
  
  
## 在 Eclipse中设置 SVN 忽略文件  
1、在eclipse里屏蔽不需要提交的文件  
Window -> Preferences --> Team -> Ignored Resources -> 在右侧添加如下文件【bin、 target、m2-target、gen、.classpath、.project】  
  
2、在Eclipse中设置忽略的.svn文件夹，此操作仅对此项目有效。  
右键 --> Properties --> Java Build Path -->  Source --> xxx/src --> Excluded --> 点右边的 Edit，在Exclusion patterns中加入`**/.svn/**`，让Eclipse忽略.svn目录即可。  
![](index_files/152b7ac5-158e-4bd8-8f7c-2da18d8f5008.png)  
  
3、在Eclipse的导航视图中，选中尚未加入版本控制的文件或目录，右键 --> Team --> `添加至SVN:ignore`。此操作仅对此项目有效。  
  
## 在 TortoiseSVN 中设置忽略文件  
1、设置某个项目下的忽略文件，此操作仅对此项目有效。  
在资源管理器中，右键一个未加入版本控制文件或目录，选择TortoiseSVN →Add to Ignore List，会出现一个子菜单，允许你仅选择忽略该文件或者忽略所有具有相同后缀的文件。  
  
如果你想从忽略列表中移除一个或多个条目，右击这些条目，选择TortoiseSVN →从忽略列表删除。  
  
2、设置全局忽略文件，此操作对所有的项目都有效。  
右键 --> TortoiseSVN --> 设置 --> 常规设置 --> 在如下输入框中添加忽略的文件类型  
![](index_files/6327129a-ba52-4835-8d60-12f50ba35937.png)  
  
## 在Eclipse上创建SVN控制的工程  
1、在eclipse上新建一个project，在project上右键 --> 【team】 --> 【share project】，选择 repository 类型为【SVN】 --> 点击next   
  
2、选择【使用已有资源库位置】或【创建新的资源库位置】--> 输入文件夹名(下方的选项代表放在指定子目录中) --> 点击Finish   
![](index_files/6d2878be-1a38-4dba-a0d0-c13fc8527bb8.png)  
  
3、输入用户名和密码(若有SVN服务器设有访问控制)，自由选择是否打开synchronize视图  
  
4、完成后切换到Java视图，这时若我们的项目各个文件及文件夹出现一个小问号，这说明我们已经 share 成功，只是还没提交到服务器  
![](index_files/f41d994e-e2aa-448a-b647-fdb124c5c5a4.png)  ![](index_files/f0879266-cb77-4da4-b4a5-6cec3c342fa9.png)  ![](index_files/2ad2cd3e-dd03-45d0-89a2-bf9bdee94779.png)  
  
从上面可以看到bin、gen目录并没有小问号，这是因为我在【Window -> Preferences --> Team -> Ignored Resources】中添加了此目录  
  
针对Android项目，我们应该添加如下文件或目录【bin、 target、m2-target、gen、.classpath、.project】  
  
5、代码更改后，右键 --> team --> 提交--> 写日志，点击OK --> 上传到服务器，成功后图标状态就改变了！  
  
## 将SVN路径中的项目导入eclipse  
正确的方式  
1、检出SVN服务器中的项目到本地  
*   在硬盘中新建一个文件夹，在文件夹内右键选择"SVN检出"  
*   选择要检出项目的URL、要检出到的位置、要检出的版本等  
*   点击确定后开始从服务器下载，一会就检出成功了  
  
2、像导入普通安卓项目一样导入本地项目到eclipse  
*   选择导入类别Android--Existing Android code into workspace  
*   复制本地项目路径，粘贴到Root Directory中，按回车（小键盘的回车不行）  
*   导入成功后，导入的工程已经受SVN控制了！  
  
- 不正确的方式一：通过eclipse中的SVN视图检出方式（检出为）  
- 不正确的方式二：通过eclipse中的右键--Import--SVN--从SVN检出项目  
- 不正确的方式三：通过eclipse中的SVN视图导出方式（导出）  
  
"检出为" 的两种方式  
*   第一种是"做为新项目检出，并使用新建项目向导进行配置"  
    仅当资源库中不存在.project工程文件时才使用，意思是如果代码库中有了这个工程文件，那么它就认为这是一个信息完整的工程，在导入的过程中就不需要再创建工程——选这选那的，因为.project工程文件中已包含了这一些列的信息，所以只有在代码库中不存在.project工程文件时才可选择。  
*   第二种是"做为工作空间中的项目检出"  
    这一选项不管代码库中是否有.project文件都是可以选择的，如果代码库中没有.project文件时，选择此种方式，会创建一个简单的工程，那不是Java工程，或其他代码工程，这种工程无不进行相应手工设置是不能进行相应开发的，所以在代码库上无.project工程文件导入时，最好选择前一选项，根据新建项目向导来进行相应设置并自动生成.project工程文件。  
  
## 将本地SVN服务端工程导入eclipse  
比如从老师那里拷来了一份SVN服务端的工程压缩包，我该怎么从中获取真正的Android工程呢？  
*   1、安装 VisualSVN Server（服务端的SVN软件）  
*   2、将本地SVN工程导入到VisualSVN Server 中，复制 VisualSVN Server 中的URL。  
*   3、后面的步骤就和上面【如何将SVN路径中的项目导入eclipse】一样了  
  
## 比较指定两个版本间的差异  
这个虽然很常用，但是经常找不到入口，蛋疼的很！  
  
在【文件夹】右键--> SVN显示日志 --> 在日志列表中的任一日志上右键-->【与前一版本比较差异】--> 选择要比较的两个版本  
![](index_files/2a3eafc2-4d1f-4fb7-a316-78baa7689d89.png)  
  
注意：一定要在文件夹上右键时才会出现此选项，在文件上右键不会出现此选项。  
  
细节：下面的版本号要比上面的大，这样才是比较后面的版本相比前面的版本有何改变。  
  
如果上面的版本号比下面的大，则代表的是"如果"将最新版本恢复到上面的版本时的变更记录。  
  
比如：若版本2相比版本1增加的一张图片，若上面为1下面为2，则变更记录为：![](index_files/29dc51c9-d916-4e64-9674-43f24465e2cc.jpg)  
  
相反，则变更记录为：![](index_files/2d8b6513-1632-440d-8962-03f03c2606fb.jpg)  
  
点击 ![](index_files/f9492096-a14b-4712-8c5e-dcda34e2427b.jpg) 可以在两种比较方式间切换。  
  
## 通过VisualSvn Server创建代码库  
1、打开VisualSvn Server，在Repository上右键 --> Create New Repository...  
  
2、选择常规方式  
![](index_files/f7dedbe2-00d3-47ef-9b60-56ec0e6365dd.jpg)  
  
3、命名  
  
4、选择初始化结构，选择下面的那个会创建trunk、branches、tags三个子目录  
![](index_files/a3fec15a-b04c-44aa-8eeb-dd7309024c42.png)  
  
5、点击配置权限，当然也可完成后右键Properties配置。新建用户或组，设置用户或组的权限  
  
6、右键【Copy URL to Clipboard】可以复制URL给团队成员  
  
## 迁入本地项目到SVN服务器代码库  
打开【源码】所在资源管理器目录，文件夹上右键 --> TortoiseSVN --> 导入 --> 输入上面创建代码库时生成的URL，确定 --> 输入账户密码（这个是在服务器设置的） --> 确定 --> 开始导入 --> 导入完成  
  
这时刷新服务器【VisualSvn Server】里的项目，便可看到导入的项目：  
  
![](index_files/134e62e2-339e-4a02-bb8a-37fc5226756c.png)  
  
## 使用eclipse解决文件冲突  
文件冲突和树冲突  
*   文件冲突：当多名开发人员修改了同一个文件中相邻或相同的行时就会发生文件冲突。  
*   树冲突：当一名开发人员移动、重命名、删除一个文件或文件夹，而另一名开发人员也对它们进行了移动、重命名、删除或者仅仅是修改时就会发生树冲突。  
  
这两种情形需要不同的步骤来解决冲突，[参考](http://blog.csdn.net/sqk1988/article/details/6926745)。    
  
对于每个冲突的文件， Subversion 在你的目录下生成了三个文件:  
*   【.mine】：这是你要提交的文件，在你更新你的工作副本之前存在于你的的工作副本中，没有冲突标志，也即除了你的最新修改外没有别的东西。  
*   【.r(xxx)】：这是在你更新你的工作副本之前的基础版本文件，也即它是在你做最后修改之前所检出的文件。  
*   【r(xxx+1)】：这个文件是当你更新你的工作副本时，你的 Subversion 客户端从服务器接收到的文件，也即这个文件是目前版本库中最新的版本。  
  
【二选一形式解决冲突】  
经过文件对比后，如果我们发现两者改动的是同一个位置或者是同一个功能点，那么我们可能需要选择性地保留一个，舍弃另一个。  
*   如果舍弃的是本地文件，请右键单击该文件，然后在关联菜单中点击【覆盖/更新】(Replace/Update)，此时Eclipse将提示"是否删除本地更改，并替换为资源库中的文件"，直接点击【Yes】即可舍弃本地更改，并更新为资源库中的最新版本。  
*   如果舍弃的是资源库中的文件，请右键单击该文件，然后在关联菜单中点击【标记为合并】，然后再点击【提交】即可。  
  
【合并解决复杂的冲突】  
如果本地和资源库中的文件改动都"各有所需"又"各有所弃"，那么我们只能在文件对比中，一处处对比不同之处，并逐步修改为最终版本。  
  
在修改与合并的过程中，有两个图标按钮比较重要。如下图所示的红色边框标注的两个图标，前者可以将右侧与本地文件没有产生冲突的改动复制到左侧的本地文件中，后者用于将鼠标光标当前所在的右侧改动区域复制到左侧相应位置。  
  
![](index_files/5f4d2c0b-def5-4b01-87aa-e32345c778a0.png)  
  
在确认改动完成之后，同样的点击右键关联菜单中的【标记为合并】，然后直接【提交】即可。  
  
## 使用SVN解决文件冲突  
使用TortoiseSVN解决文件冲突  
  
1、update时发现冲突。update后产生三个文件：  
  
2、右键找到Edit conflicts（编辑冲突），打开之后，窗口里边有三个文档，左右下。下方的是最后成果，你需要根据左右两份不同版本，合成一个最终版  
  
3、右键-Resolved（或在修改后保存时会有提示“是否标记为解决”，点击确认即可）  
  
4、黄色警告不见了，变回平时熟悉的已修改标记~~现在可以正常commit了  
  
## eclipse导入svn项目版本兼容问题  
eclipse中导入本地svn管理的Android项目时，弹出以下错误提示：  
  
![](index_files/d23e1ab4-f870-4030-8973-7573b9735516.jpg)  
  
```c  
org.apache.subversion.javahl.ClientException: Unsupported working copy format svn: This client is too old to work with the working copy at 'D:\95_bqt' (format 31).  
You need to get a newer Subversion client. For more details, see http://subversion.apache.org/faq.html#working-copy-format-change  
```  
  
原因是你的 Eclipse SVN 插件太旧了，更新安装 eclipse svn 插件就可以了！  
  
注意：这里并不是说你在文件管理器Explorer右键时使用的 TortoiseSVN 太旧了，而是 Eclipse 中使用的 SVN 插件太旧了。  
  
2017-11-6  
