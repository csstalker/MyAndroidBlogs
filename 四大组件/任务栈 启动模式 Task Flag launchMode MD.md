| Markdown版本笔记 | 我的GitHub首页 | 我的博客 | 我的微信 | 我的邮箱 |  
| :------------: | :------------: | :------------: | :------------: | :------------: |  
| [MyAndroidBlogs][Markdown] | [baiqiantao][GitHub] | [baiqiantao][博客] | bqt20094 | baiqiantao@sina.com |  
  
[Markdown]:https://github.com/baiqiantao/MyAndroidBlogs  
[GitHub]:https://github.com/baiqiantao  
[博客]:http://www.cnblogs.com/baiqiantao/  
  
任务栈 启动模式 Task Flag launchMode MD  
***  
目录  
===  

- [任务栈](#任务栈)
	- [Activity的四种启动模式](#Activity的四种启动模式)
	- [基本概念](#基本概念)
	- [Intent中常用的Flag](#Intent中常用的Flag)
	- [taskAffinity属性](#taskAffinity属性)
	- [任务栈简单了解](#任务栈简单了解)
  
# 任务栈  
  
## Activity的四种启动模式  
Activity启动模式有四种，可以根据实际的需求为Activity设置对应的启动模式，从而可以避免创建大量重复的Activity等问题。  
设置Activity的启动模式，只需要在AndroidManifest.xml里对应的activity标签设置`android:launchMode`属性。  
  
只记住以下内容就够了：  
- standard：默认模式，可以不用写配置。在这个模式下，都会默认创建一个新的实例。因此，在这种模式下，可以有多个相同的实例，也允许多个相同Activity叠加。  
- singleTop：可以有多个实例，但是不允许多个相同Activity叠加。即如果Activity在栈顶的时候，启动相同的Activity，不会创建新的实例，而会调用其onNewIntent方法。  
- singleTask：只有一个实例。  
    - 如果在同一个应用程序中启动它，若Activity不存在，则会在当前task创建一个新的实例，若存在，则会把task中在其之上的其它Activity全部destory掉并调用它的onNewIntent方法。  
    - 如果是在别的应用程序中启动它（比如跳转到QQ），则会新建一个task，并在该task中启动这个Activity。  
    - 可以使用singleTask来退出整个应用：将主Activity设为SingTask模式，然后在要退出的Activity中跳转到主Activity，然后重写主Activity的onNewIntent函数，并在函数中加上一句finish。  
    - 注意：和singleInstance不同的是，singleTask允许别的Activity与其在一个task中共存，也就是说，如果我在这个singleTask的实例中再打开新的Activity，这个新的Activity还是会在singleTask的实例的task中。  
- singleInstance：只有一个实例，并且这个实例独立运行在一个task中，这个task只有这个实例，不允许有别的Activity存在。  
  
## 基本概念  
栈（Stack）是一种常用的数据结构，栈只允许访问栈顶的元素，栈就像一个杯子，每次都只能取杯子顶上的东西，而对于栈就只能每次访问它的栈顶元素，从而可以达到保护栈顶元素以下的其他元素  
  
"先进后出"或"后进先出"就是栈的一大特点，先进入栈的元素总是要等到后进入栈的元素出栈以后才能出栈。  
  
递归就是利用到了系统栈，暂时保存临时结果，对临时结果进行保护．  
  
栈的基本操作：压栈、弹栈  
  
任务栈简单的说就是一组以栈的模式聚集在一起的Activity组件集合，类似于一个填充了Activity的容器，最先加入的Activity会处于容器最下面，最后加入的处于容器最上面，而从Task中取出Activity时是从最顶端先取出，最后取出的是最开始添加Activity，这就是后进先出模式。而Activity在Task中的顺序是可以控制的，在Activity跳转时用Intent Flag可以设置新建activity的创建方式。  
  
## Intent中常用的Flag  
Flag表示Intent的标志位，常用于Activity的场景中，它和Activity的启动模式有着密切的联系。  
  
下面列举的是和Activity启动模式相关的Flag属性：  
- `FLAG_ACTIVITY_NEW_TASK`：系统会检查当前所有已创建的Task中是否有该要启动的Activity的Task，若有，则在该Task上【创建】新的Activity；若没有则新建一个Task，并在该新建的Task上创建Activity。  
- `FLAG_ACTIVITY_SINGLE_TOP`：这个FLAG就相当于启动模式中的singletop，比如说原来栈中情况是ABCD，在D中启动D，栈中的情况还是ABCD  
- `FLAG_ACTIVITY_CLEAR_TOP`：这个FLAG就相当于启动模式中的SingleTask，这种FLAG启动的Activity会把要启动的Activity之上的Activity全部弹出栈空间。比如：原来栈中的情况是ABCD，这个时候从D中跳转到B，这个时候栈中的情况就是AB了  
- `FLAG_ACTIVITY_NO_HISTORY`：这个标记顾名思义意思就是说，用这个FLAG启动的Activity，一旦【不可见】，他就不会存在于任务栈中。比如，原来是ABC，这个时候在C中以这个FLAG启动D的 ，D再启动E，这个时候栈中情况为ABCE。  
- `FLAG_ACTIVITY_BROUGHT_TO_FRONT`：比方说我现在在A中启动B，启动B时在Intent中加上这个标记，此时在B中再正常启动CD，此时栈的情况是ABCD。如果这个时候在D中再启动B，这个时候栈的情况是 ACDB。  
- `FLAG_ACTIVITY_REORDER_TO_FRONT`：如果在A,B,C,D正常启动的话，不管B有没有用FLAG_ACTIVITY_BROUGHT_TO_FRONT启动，此时在D中启动B的话，还是会变成A,C,D,B的。  
- `FLAG_ACTIVITY_RESET_TASK_IF_NEEDED`：这个不知道具体怎么用  
- `FLAG_ACTIVITY_NO_USER_ACTION`：onUserLeaveHint()是activity周期的一部分，它在activity因为用户要跳转到别的activity而使当前activity要退到background时调用。比如，在用户按下Home键（用户的选择）时，它将被调用；但如果是有电话进来（不属于用户的选择，是系统行为），它就不会被调用。那么系统如何区分让当前activity退到background时使用是用户的choice？它是根据促使当前activity退到background的那个新启动的Activity的Intent里是否有FLAG_ACTIVITY_NO_USER_ACTION来确定的。注意：通过调用finish()使该activity销毁时不会调用该函数  
  
## taskAffinity属性  
清单文件中，activity 的属性android:allowTaskReparenting用于设定：Activity是否能够从启动它的任务栈中【转移到】另一个与启动它的任务栈有相同taskAffinity属性值的任务栈中，转移时机是在另一个任务栈被带到前台的时候。如果设置为true，则能够转移，如果设置了false，则这个Activity必须要保留在启动它的那个任务栈中。  
  
**实验**  
  
1、新建两个工程，App1和App2  
App1和App2都设置android:taskAffinity="aaa.aaa"    android:allowTaskReparenting="true"  
先运行App1，然后点击home键，让App1运行在后台  
再运行App2，会发现这时显示的是App1的mainActivity，并且长按home键，会发现运行过的程序只有App1。  
  
2、紧接着又在此基础上做了另外一个实验  
在App1上新建一个secondActivity，设置android:taskAffinity="aaa.bbb"    android:allowTaskReparenting="true"  
在mainActivity中startActivity时，设置Intent中flag属性为FLAG_ACTIVITY_NEW_TASK。  
然后运行App1，点击进入secondActivity，这时长按home键，会发现运行过的程序中有两个App1，并且一个显示的是mainActivity另一个显示的是secondActivity。  
这时点击home键，让程序回到后台  
然后运行App2，会发现这时显示的是App1的mainActivity，此时点击返回会直接返回home。  
注意，虽然此时App1的mainActivity被finish了，但App1的secondActivity还在后台等着呢。  
同样，长按home键，会发现运行过的程序只有App1的两个任务栈，并没有App2。  
  
3、在此基础上对App1再次修改  
在App1上新建一个thirdActivity，设置属性android:taskAffinity="aaa.aaa"    android:allowTaskReparenting="true"  
并在secondActivity中startActivity时，设置Intent中flag属性为FLAG_ACTIVITY_NEW_TASK；  
运行App1，点击进入secondActivity，再进入thirdActivity，此时长按home键，会发现运行过的程序中有两个App1，并且一个显示的是secondActivity另一个显示的是thirdActivity。  
此时点击返回，会回到mainActivity，再点击返回，会回到secondActivity，再点击返回，回到home页面。  
以上实验中，如果startActivity时不设置Intent中flag属性为FLAG_ACTIVITY_NEW_TASK，则若先启动的是App1，那么不管是App1中的Activity还是App2中的mainActivity，都是运行App1的一个任务栈中。  
  
  
## 任务栈简单了解  
我们在开发项目的过程中，一般都需要在本应用中多个Activity组件之间的跳转，也可能需要在本应用中打开其它应用的可复用的Activity。如我们可能需要跳转到原来某个Activity实例，此时我们更希望这个Activity可以被重用而不是创建一个新的 Activity，但根据Android系统的默认行为，确实每次都会为我们创建一个新的Activity并添加到Task中，这样android系统是不是很傻？还有一点就是在我们每开启一次页面加入到任务栈Task中后，一个Activity的数据和信息状态都将会被保留，这样会造成数据冗余, 重复数据太多, 最终还可能导致内存溢出的问题(OOM)。  
  
- android任务栈又称为Task，它是一个栈结构，具有后进先出的特性，用于存放我们的Activity组件。   
- 我们每次打开一个新的Activity或者退出当前Activity都会在一个称为任务栈的结构中添加或者减少一个Activity组件，因此一个任务栈包含了一个activity的集合, android系统可以通过Task有序地管理每个activity，并决定哪个Activity与用户进行交互：只有在任务栈栈顶的activity才可以跟用户进行交互。   
- 在我们退出应用程序时，必须把所有的任务栈中所有的activity清除出栈时任务栈才会被销毁。当然任务栈也可以移动到后台, 并且保留了每一个activity的状态. 可以有序的给用户列出它们的任务, 同时也不会丢失Activity的状态信息。   
- 需要注意的是，一个App中可能不止一个任务栈，某些特殊情况下，单独一个Actvity可以独享一个任务栈。还有一点就是一个Task中的Actvity可以来自不同的App，同一个App的Activity也可能不在一个Task中。  
  
  
android系统提供了一套Activity的启动模式来修改系统Activity的默认启动行为。目前启动模式有四种，接下来我们将分别介绍这四种模式。  
  
**Standard 模式**  
又称为标准模式，也是系统的默认模式（可以不指定），在这样模式下，每启动一个Activity都会重新创建一个Activity的新实例，并且将其加入任务栈中，而且完全不会去考虑这个实例是否已存在。  
   
在standard模式下启动三次MainActivity后，都会生成了不同的新实例，并添加到同一个任务栈中。这个时候Activity的onCreate、onStart、onResume方法都会被调用。  
  
**singleTop 模式**  
又称栈顶复用模式，顾名思义，在这种模式下，如果有新的Activity已经存在任务栈的栈顶，那么此Activity就不会被重新创建新实例，而是复用已存在任务栈栈顶的Activity。这里重点是位于栈顶，才会被复用，如果新的Activity的实例已存在但没有位于栈顶，那么新的Activity仍然会被重建。需要注意的是，Activity的`onNewIntent`方法会被调用。  
  
这种模式通常比较适用于接收到消息后显示的界面，如qq接收到消息后弹出Activity界面，如果一次来10条消息，总不能一次弹10个Activity吧？再比如新闻客户端收到了100个推送，你每次点一下推送他都会进入某个activiy界面（显示新闻只用一个activity，只是内容不同而已），这时也比较适合使用singleTop模式。  
  
**singleTask 模式**  
又称为栈内复用模式。这是一种单例模式，与singTop点类似，只不过singTop是检测栈顶元素是否为需要启动的Activity，而singTask则是检测`整个栈中`是否存在当前需要启动的Activity，`如果存在就直接将该Activity置于栈顶，并将该Activity以上的Activity都从任务栈中移出销毁，同时也会回调onNewIntent方法`。  
  
singleTask 模式比较适合应用的主界面activity，里面有好多fragment，一般不会被销毁，它可以跳转其它的activity界面再回主架构界面，此时其他Activity就销毁了。当然singTask还有一些比较特殊的场景这个我们后面会一一通过情景代码分析。  
  
**singleInstance 模式**  
在singleInstance模式下，该Activity在`整个android系统内存中`有且只有一个实例，而且该实例`单独尊享一个Task`。换句话说，A应用需要启动的 MainActivity 是singleInstance模式，当A启动后，系统会为它创建一个新的任务栈，然后A单独在这个新的任务栈中，如果此时B应用也要激活MainActivity，由于栈内复用的特性，则不会重新创建，而是两个应用共享一个Activity的实例。  
  
2019-2-27  
