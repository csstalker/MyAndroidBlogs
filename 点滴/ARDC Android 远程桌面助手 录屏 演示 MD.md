| Markdown版本笔记 | 我的GitHub首页 | 我的博客 | 我的微信 | 我的邮箱 |
| :------------: | :------------: | :------------: | :------------: | :------------: |
| [MyAndroidBlogs][Markdown] | [baiqiantao][GitHub] | [baiqiantao][博客] | bqt20094 | baiqiantao@sina.com |

[Markdown]:https://github.com/baiqiantao/MyAndroidBlogs
[GitHub]:https://github.com/baiqiantao
[博客]:http://www.cnblogs.com/baiqiantao/

ARDC Android 远程桌面助手 录屏 演示 MD  
ARDC：Android Remote Displayer and Controller
***
目录
===
[TOC]

# 界面功能与快捷键
特点：  
> 依赖adb，无需root，画面显示清晰且可调，支持自动保存截图，支持Ctrl+C复制画面到剪贴板，画面显示可缩放可全屏，支持拖拽文件到/sdcard/Download目录，Ctrl+拖拽APK可直接安装apk，鼠标中键模拟Home键，鼠标右键模拟Power键，支持鼠标滚轮操作，支持键盘输入，Alt+D显示设备信息窗口，支持快速重启、进入fastboot及清除logcat。

## File
![](http://pfpk8ixun.bkt.clouddn.com/markdown-img-paste-20181001163229244.png)  

- 【Alt+T】保持/取消 在其他窗口上面显示
- 【Alt+C】在窗口中间显示
- 【Alt+D】显示/隐藏 设备窗口，最右侧的【<<】也可以
- 【Alt+B】显示/隐藏 窗口，老板键
- 【Alt+P】暂停/取消 屏幕镜像，意思是保持设备当前屏幕镜像，不再刷新(但仍可以继续操作屏幕)
- 【Alt+J】自动捕获屏幕
- 【PrtSc】【Alt+PrtSc】截屏
- 【Alt+V】屏幕录像
- 【Alt+X】打开本应用程序在PC中所在的目录
- 【Ctrl+C】复制当前画面到剪贴板
- 【Ctrl+V】把剪贴板中的文本内容粘贴到手机上的输入框中
- 【Ctrl+K】杀死当前onResume的前台应用
- 【Ctrl+F】显示性能检测浮窗，可以查看CPU、内存、当前应用的包名
- 【Alt+N】在NPP上显示
- 【Alt+F4】关闭应用

## EXP 文件浏览器
支持在任何界面下将文件拖拽文件到SD卡的Download目录，支持在任何界面下通过【Ctrl+拖拽APK】直接安装APK。  
![](http://pfpk8ixun.bkt.clouddn.com/markdown-img-paste-2018100116331555.png)

注意：手机SD卡目录可能是在如下位置，打开后支持`拖拽文件到SD卡相应目录`  
![](http://pfpk8ixun.bkt.clouddn.com/markdown-img-paste-20181001163336428.png)

- 【Ctrl+E】打开手机的文件浏览器
- 【Ctrl+R】刷新手机的文件浏览器
- 【Ctrl+M】变更文件或目录的权限，一般没有权限修改
- 【双击】打开文件或目录
- 【Del】删除文件
- 【RB】不知道干嘛的
- 【+或-】展开、折叠当前目录
- 【左右方向键】展开、折叠当前目录，选中上/下一层的文件
- 【上下方向键】上/下一个文件

## VIew 画面设置
![](http://pfpk8ixun.bkt.clouddn.com/markdown-img-paste-20181001163426198.png)  

- 【Alt+M】屏幕截屏类，不知道干嘛的
- 【F1,F2,F3,F4】对应 FPS-5/15/30/Max，设置屏幕刷新帧率，数值越大，刷新越快(反应越灵敏)
- 【Alt+1/2/3/4】对应Qity-JPEG 100/95/80/20，屏幕显示质量，数值越大，清晰度越高
- 【Zoom】缩放，注意缩放并非相对于当前比例缩放，而是相对于原始尺寸缩放

Devices 里面会列出所有连接的设备，你可以选择要显示的设备

## Key 模拟按键
![](http://pfpk8ixun.bkt.clouddn.com/markdown-img-paste-20181001163522328.png)  

支持鼠标滚轮操作，支持使用PC键盘输入
- 【Poweroff】一键关机，没有确定步骤，关机后就不能再控制手机了
- 【Reboot】一键重启手机，没有确定步骤，关机后就不能再控制手机了
- 【Settings】打开设置界面
- 【右键长按】查看最近使用的应用
- 【右键单击，ESC键】返回
- 【Enter】进入、确定等
- 【滚轮键】回到桌面
- 【Alt+W】长按关机键，一般会弹出选择"关机、重启..."界面
- 【Alt+R】旋转屏幕
- 【Alt+单击左键】减小音量
- 【Alt+单击中键】静音模式
- 【Alt+单击右键】增大音量

# Devices 窗口
![](http://pfpk8ixun.bkt.clouddn.com/markdown-img-paste-20181001163550522.png)  
![](http://pfpk8ixun.bkt.clouddn.com/markdown-img-paste-20181001163814442.png)  

- 【WIFI(adb)】查看当前设备的IP，使用 WIFI 方式连接，实际使用不了的
- 【dmesg】查看开机信息、printk产生的信息等，文件保存在【Logs】目录中
- 【logcat】查看最近一段时间的全部logcat日志信息，文件保存在【Logs】目录中  
- 【NPP】貌似是打开自带的 notepad++ 编辑器让你写东西用的
- 【CMD】打开命令行窗口  
- 【APK】选择要调试的应用，点击下拉箭头就可以根据包名选择，勾选【SYS】可以选择系统应用
- 【Dump】Dump文件是用来给驱动程序编写人员调试驱动程序用的进程内存镜像文件。
    > 会打印100多行的信息，详情见附件
- 【Export】不知道干什么的
    > Export to .\Files\Android
- 【Foreground】选定当前在前台的应用
    > package:/data/app/com.bqt.test.module-2/base.apk
- 【Kill】中断、删除不必要的程序，一般没效果。
    > killall: pid 22507: Operation not permitted  
    > killall: com.bqt.test.module: Operation not permitted
- 【ps】ps命令是最基本同时也是非常强大的进程查看命令，使用该命令可以确定有哪些进程正在运行和运行的状态、进程是否结束、进程有没有僵死、哪些进程占用了过多的资源等等。
    > u0_a453 22507 6223 2073628 96816 SyS_epoll_ 0000000000 S com.bqt.test.module
- 【Pull】不知道干什么的
    > Pulling com.bqt.test.module-2 C:Android_toolsAndroid...
- 【Start】启动应用
- 【Stop】关闭应用
- 【Remove】卸载应用
- 【CMD】在编辑框中输入CMD命令后按Enter键可以执行CMD命令
    > List of devices attached  
    > 83af4942354e3430 device

2018-9-9