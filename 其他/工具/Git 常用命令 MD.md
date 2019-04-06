| Markdown版本笔记 | 我的GitHub首页 | 我的博客 | 我的微信 | 我的邮箱 |  
| :------------: | :------------: | :------------: | :------------: | :------------: |  
| [MyAndroidBlogs][Markdown] | [baiqiantao][GitHub] | [baiqiantao][博客] | bqt20094 | baiqiantao@sina.com |  
  
[Markdown]:https://github.com/baiqiantao/MyAndroidBlogs  
[GitHub]:https://github.com/baiqiantao  
[博客]:http://www.cnblogs.com/baiqiantao/  
  
[Git官方教程 - 中文版](https://git-scm.com/book/zh/v2)  
[Git官方教程 - 中文版PDF下载](https://github.com/progit/progit2-zh/releases/download/2.1.16/progit_v2.1.16.pdf)  
[易百教程 - 逐个介绍Git命令](https://www.yiibai.com/git)  
[廖雪峰教程 - 介绍常用Git命令](https://www.liaoxuefeng.com/wiki/0013739516305929606dd18361248578c67b8067c8c017b000)  
***  
目录  
===  

- [Git 命令大全](#Git-命令大全)
	- [常用命令](#常用命令)
	- [基础命令](#基础命令)
		- [帮助 [help](https://www.yiibai.com/git/git_help.html)](#帮助-helphttpswwwyiibaicomgitgit_helphtml)
		- [配置 [config](https://www.yiibai.com/git/git_config.html)](#配置-confighttpswwwyiibaicomgitgit_confightml)
		- [状态信息 status](#状态信息-status)
	- [初始化](#初始化)
		- [新建仓库 [init](https://www.yiibai.com/git/git_init.html)](#新建仓库-inithttpswwwyiibaicomgitgit_inithtml)
		- [克隆仓库资源 clone](#克隆仓库资源-clone)
	- [增删改](#增删改)
		- [增加 [add](https://www.yiibai.com/git/git_add.html)](#增加-addhttpswwwyiibaicomgitgit_addhtml)
		- [删除 rm](#删除-rm)
		- [移动/重命名 mv](#移动重命名-mv)
	- [代码提交](#代码提交)
		- [提交到本地仓库 commit](#提交到本地仓库-commit)
	- [分支](#分支)
	- [标签](#标签)
	- [查看信息](#查看信息)
	- [远程同步](#远程同步)
	- [撤销](#撤销)
	- [其他](#其他)
  
# Git 命令大全  
## 常用命令  
基础命令：  
> git help  # 显示帮助，可以查看某一命令的帮助文档  
> git config -l [--system | --global | --local]  # 显示当前的Git配置  
> git config --global user.name "baiqiantao"  # 设置提交代码时的用户名  
> git config --global user.email "baiqiantao@sina.com"  # 设置提交代码时的用户邮箱  
  
初始化：  
> git init [directory] [--bare] # 新建一个Git代码仓库(作为纯净的仓库)  
> git clone [repository] [directory]   # 下载一个项目和它的整个代码历史到指定目录  
  
增删改：  
> git add . -v   # 添加当前目录的所有文件到暂存区  
> git rm --cached [file]   # 删除工作区文件(保留在工作区)  
> git mv -v [file-original] [file-renamed]  # 改名文件，并且将这个改名后的文件放入暂存区  
  
代码提交：  
> git commit -m [message]   # 提交暂存区到仓库区  
> git pull [remote] [branch]    # 取回远程仓库的变化，并与本地分支合并  
> git push [remote] [branch]    # 上传本地指定分支到远程仓库  
  
> git fetch [remote]    # 下载远程仓库的所有变动  
> git log    # 显示当前分支的版本历史  
  
分支：  
> git branch  # 列出所有本地分支（带*的为当前分支），【-r】远程分支，【-a】本地分支和远程分支  
> git branch [branch-name]  # 新建一个分支，但依然停留在当前分支,【-d】删除分支  
> git checkout -b [branch]  # 新建一个分支，并切换到该分支  
> git checkout [branch-name] # 切换到指定分支，并更新工作区  
> git merge [branch]  # 合并指定分支到当前分支  
  
> git tag            # 列出所有tag  
> git tag [tag]      # 新建一个tag在当前commit  
> git tag -d [tag]   # 删除本地tag  
  
> git fetch origin branch_name   # 更新远程代码到本地仓库  
  
## 基础命令  
### 帮助 [help](https://www.yiibai.com/git/git_help.html)  
显示有关Git的帮助信息  
> git help  # 显示常用命令列表  
> git help -a  # 显示所有可用的命令  
> git help <command>  # 查看某一命令如何使用  
> git help git  # 显示 git 手册页  
> git help config  # 查看 config 命令如何使用  
> git help help  # 查看 help 命令如何使用  
  
### 配置 [config](https://www.yiibai.com/git/git_config.html)  
获取并设置存储库或全局选项，这些变量可以控制Git的外观和操作的各个方面  
> git config --list [--system | --global | --local]  
> git config -l --global  # 显示当前的Git配置  
> git config --global user.name "baiqiantao"  # 设置提交代码时的用户名  
> git config --global user.email "baiqiantao@sina.com"  # 设置提交代码时的用户邮箱  
  
### 状态信息 status  
`git status`命令用于显示工作目录和暂存区的状态。使用此命令能看到那些修改被暂存到了, 哪些没有, 哪些文件没有被 Git tracked到。`git status`不显示已经`commit`到项目历史中去的信息。看项目历史的信息要使用`git log`.  
  
> git status  
  
  
## 初始化  
### 新建仓库 [init](https://www.yiibai.com/git/git_init.html)  
创建一个空的Git仓库或重新初始化一个现有仓库  
> git init [directory] # 重新初始化一个现有仓库  
> git init --bare # 新建一个Git代码仓库，作为纯净的仓库  
  
### 克隆仓库资源 clone  
> Clone a repository into a new directory  
  
下载一个项目和它的整个代码历史  
> git clone <repository> [directory]  
  
## 增删改  
### 增加 [add](https://www.yiibai.com/git/git_add.html)  
将文件内容添加到索引(将修改添加到暂存区)，也就是将要提交的文件的信息添加到索引库中  
> git add [file1] [dir1/file2] [dir2] ... -v  # 添加指定文件、目录到暂存区  
> git add . -v # 添加当前目录的所有文件到暂存区  
  
### 删除 rm  
> rm: Remove files from the working tree and from the index  
  
删除工作区文件(保留在工作区：`--cached`)  
> git rm --cached [file]  
  
### 移动/重命名 mv  
> mv: Move or rename a file, a directory, or a symlink  
  
改名文件，并且将这个改名后的文件放入暂存区  
> git mv -v [file-original] [file-renamed]  
  
## 代码提交  
### 提交到本地仓库 commit  
> Record changes to the repository  
  
提交暂存区的所有文件到仓库区  
> git commit -m [message]  
  
提交暂存区的指定文件到仓库区  
> git commit [file1] [dir1/file2] [dir2] ... -m [message]  
  
使用一次新的commit，替代上一次提交(如果代码没有任何新变化，则用来改写上一次commit的提交信息)：  
> git commit -v --amend -m [message]  
  
## 分支  
列出所有本地分支（带*的为当前分支）  
> git branch  
  
列出所有远程分支  
> git branch -r  
  
列出所有本地分支和远程分支  
> git branch -a  
  
新建一个分支，但依然停留在当前分支  
> git branch [branch-name]  
  
新建一个分支，并切换到该分支  
> git checkout -b [branch]  
  
新建一个分支，指向指定commit  
> git branch [branch] [commit]  
  
新建一个分支，与指定的远程分支建立追踪关系  
> git branch --track [branch] [remote-branch]  
  
切换到指定分支，并更新工作区  
> git checkout [branch-name]  
  
切换到上一个分支  
> git checkout -  
  
建立追踪关系，在现有分支与指定的远程分支之间  
> git branch --set-upstream [branch] [remote-branch]  
  
合并指定分支到当前分支  
> git merge [branch]  
  
选择一个commit，合并进当前分支  
> git cherry-pick [commit]  
  
删除分支  
> git branch -d [branch-name]  
  
删除远程分支  
> git push origin --delete [branch-name]  
> git branch -dr [remote/branch]  
  
## 标签  
列出所有tag  
> git tag  
  
新建一个tag在当前commit  
> git tag [tag]  
  
新建一个tag在指定commit  
> git tag [tag] [commit]  
  
删除本地tag  
> git tag -d [tag]  
  
删除远程tag  
> git push origin :refs/tags/[tagName]  
  
查看tag信息  
> git show [tag]  
  
提交指定tag  
> git push [remote] [tag]  
  
提交所有tag  
> git push [remote] --tags  
  
新建一个分支，指向某个tag  
> git checkout -b [branch] [tag]  
  
## 查看信息  
  
显示当前分支的版本历史  
> git log  
  
显示commit历史，以及每次commit发生变更的文件  
> git log --stat  
  
搜索提交历史，根据关键词  
> git log -S [keyword]  
  
显示某个commit之后的所有变动，每个commit占据一行  
> git log [tag] HEAD --pretty=format:%s  
  
显示某个commit之后的所有变动，其"提交说明"必须符合搜索条件  
> git log [tag] HEAD --grep feature  
  
显示某个文件的版本历史，包括文件改名  
> git log --follow [file]  
> git whatchanged [file]  
  
显示指定文件相关的每一次diff  
> git log -p [file]  
  
显示过去5次提交  
> git log -5 --pretty --oneline  
  
显示所有提交过的用户，按提交次数排序  
> git shortlog -sn  
  
显示指定文件是什么人在什么时间修改过  
> git blame [file]  
  
显示暂存区和工作区的差异  
> git diff  
  
显示暂存区和上一个commit的差异  
> git diff --cached [file]  
  
显示工作区与当前分支最新commit之间的差异  
> git diff HEAD  
  
显示两次提交之间的差异  
> git diff [first-branch]...[second-branch]  
  
显示今天你写了多少行代码  
> git diff --shortstat "@{0 day ago}"  
  
显示某次提交的元数据和内容变化  
> git show [commit]  
  
显示某次提交发生变化的文件  
> git show --name-only [commit]  
  
显示某次提交时，某个文件的内容  
> git show [commit]:[filename]  
  
显示当前分支的最近几次提交  
> git reflog  
  
## 远程同步  
下载远程仓库的所有变动  
> git fetch [remote]  
  
显示所有远程仓库  
> git remote -v  
  
显示某个远程仓库的信息  
> git remote show [remote]  
  
增加一个新的远程仓库，并命名  
> git remote add [shortname] [url]  
  
取回远程仓库的变化，并与本地分支合并  
> git pull [remote] [branch]  
  
上传本地指定分支到远程仓库  
> git push [remote] [branch]  
  
强行推送当前分支到远程仓库，即使有冲突  
> git push [remote] --force  
  
推送所有分支到远程仓库  
> git push [remote] --all  
  
## 撤销  
恢复暂存区的指定文件到工作区  
> git checkout [file]  
  
恢复某个commit的指定文件到暂存区和工作区  
> git checkout [commit] [file]  
  
恢复暂存区的所有文件到工作区  
> git checkout .  
  
重置暂存区的指定文件，与上一次commit保持一致，但工作区不变  
> git reset [file]  
  
重置暂存区与工作区，与上一次commit保持一致  
> git reset --hard  
  
重置当前分支的指针为指定commit，同时重置暂存区，但工作区不变  
> git reset [commit]  
  
重置当前分支的HEAD为指定commit，同时重置暂存区和工作区，与指定commit一致  
> git reset --hard [commit]  
  
重置当前HEAD为指定commit，但保持暂存区和工作区不变  
> git reset --keep [commit]  
  
新建一个commit，用来撤销指定commit  
后者的所有变化都将被前者抵消，并且应用到当前分支  
> git revert [commit]  
  
暂时将未提交的变化移除，稍后再移入  
> git stash  
> git stash pop  
  
## 其他  
生成一个可供发布的压缩包  
> git archive  
  
2018-12-19  
