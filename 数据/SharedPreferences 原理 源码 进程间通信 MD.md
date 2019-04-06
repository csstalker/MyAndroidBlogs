| Markdown版本笔记 | 我的GitHub首页 | 我的博客 | 我的微信 | 我的邮箱 |  
| :------------: | :------------: | :------------: | :------------: | :------------: |  
| [MyAndroidBlogs][Markdown] | [baiqiantao][GitHub] | [baiqiantao][博客] | bqt20094 | baiqiantao@sina.com |  
  
[Markdown]:https://github.com/baiqiantao/MyAndroidBlogs  
[GitHub]:https://github.com/baiqiantao  
[博客]:http://www.cnblogs.com/baiqiantao/  
  
SharedPreferences 原理 源码分析 进程间通信 MD  
***  
目录  
===  

- [SharedPreferences](#SharedPreferences)
	- [简介](#简介)
	- [跨进程通信时的问题](#跨进程通信时的问题)
	- [持久化数据的更新](#持久化数据的更新)
	- [MODE_MULTI_PROCESS 标记](#MODE_MULTI_PROCESS-标记)
  
# SharedPreferences  
  
## 简介  
在Android中, SharePreferences是一个`轻量级`的存储类，特别适合用于保存软件`配置参数`。使用SharedPreferences保存数据，其背后是用`xml`文件存放数据，文件 存放在`/data/data/ <package name> /shared_prefs`目录下.  
  
之所以说SharedPreference是一种轻量级的存储方式，是因为它在创建的时候会`一次性把整个文件全部加载进内存`，如果SharedPreference文件比较大，会带来以下问题：  
- 第一次从sp中获取值的时候，有可能阻塞主线程，使界面卡顿、掉帧。  
- 解析sp的时候会产生大量的临时对象，导致频繁GC，引起界面卡顿。  
- 这些key和value会永远存在于内存之中，占用大量内存。  
  
优化建议  
- 不要存放大的key和value，会引起界面卡、频繁GC、占用内存等等。  
- 毫不相关的配置项就不要放在在一起，文件越大读取越慢。  
- 读取频繁的key和不易变动的key尽量不要放在一起，影响速度。  
- 不要乱edit和apply，尽量批量修改一次提交，多次apply会阻塞主线程。  
- 尽量不要存放JSON和HTML，这种场景请直接使用JSON。  
- SharePreferences的commit与apply一个是同步一个是异步  
- `SharedPreference无法进行跨进程通信`  
  
## 跨进程通信时的问题  
**问题**  
先启动主线程并获取SharedPreferences对象，然后对值进行修改，然后再启动其它进程并获取SharedPreferences对象，能够获取修改后的值，`但此时如果对此值进行修改，不能对其他进程产生作用`，必须等到进程重启或者app重启才能与其他进程进行数据同步。  
  
**原因**  
只有在创建SharedPreferences对象的时候才会从磁盘中进行读取，读取完以后值保存在内存(HashMap)当中，下次获取SharedPreferences对象优先从缓存当中获取，所以在当前进程修改了SharedPreferences的值，其他进程的SharedPreferences对象的值并不会改变。只有把当前另外的进程关闭（如：关闭手机、或杀死该app重新进入），再次创建进程时才会重新从磁盘中再次读取文件。  
  
**源码分析**  
通常我们获取SharedPreferences都是通过Context中的getSharedPreference方法来获取SharedPreferences对象，在Context中，getSharedPreference方法是一个抽象方法，没有具体实现：  
```java  
public abstract SharedPreferences getSharedPreferences(String name, @PreferencesMode int mode);  
```  
检索并保存首选项文件“name”的内容，返回SharedPreferences，您可以通过它来检索和修改其值。只有一个SharedPreferences对象实例返回给任何相同名称的调用者，这意味着他们一旦完成就会看到彼此的编辑。  
> Retrieve and hold the contents of the preferences file 'name', returning a SharedPreferences through which you can retrieve and modify its values. Only `one instance` of the SharedPreferences object is returned to any callers for the same name, meaning they will see each other's edits as soon as they are made.  
  
我们知道Context的实现类是ContextImpl，所以直接找到ContextImpl的getSharedPreference方法。  
```java  
@Override  
public SharedPreferences getSharedPreferences(String name, int mode) {  
    File file;  
    synchronized (ContextImpl.class) {  
        if (mSharedPrefsPaths == null) {  
            mSharedPrefsPaths = new ArrayMap<>(); //class ArrayMap implements Map  
        }  
        file = mSharedPrefsPaths.get(name); //根据 name 获取对应的文件  
        if (file == null) { //首次访问可能连文件都不存在，那么还需要创建 xml 文件  
            file = getSharedPreferencesPath(name); //创建文件，文件名为【name + ".xml"】  
            mSharedPrefsPaths.put(name, file); //保存起来  
        }  
    }  
    return getSharedPreferences(file, mode);  
}  
```  
  
```java  
@Override  
public SharedPreferences getSharedPreferences(File file, int mode) {  
    SharedPreferencesImpl sp;  
    synchronized (ContextImpl.class) {  
        final ArrayMap<File, SharedPreferencesImpl> cache = getSharedPreferencesCacheLocked();  
        sp = cache.get(file); //获取缓存对象  
        if (sp == null) { //如果内存中不存在，会创建SharedPreferencesImpl  
            checkMode(mode);  
            sp = new SharedPreferencesImpl(file, mode); //创建SharedPreferences的实例对象  
            cache.put(file, sp); //缓存起来  
            return sp;  
        }  
    }  
    if ((mode & Context.MODE_MULTI_PROCESS) != 0 ||   
        getApplicationInfo().targetSdkVersion < android.os.Build.VERSION_CODES.HONEYCOMB) { //11  
        //If somebody else (some other process) changed the prefs file behind our back, we reload it.  
        //This has been the historical (if ndocumented) behavior.  
        //如果其他人(其他进程)在我们后面更改了prefs文件，我们会重新加载它。这是历史(如果没有文档记录)的行为。  
        sp.startReloadIfChangedUnexpectedly();  
    }  
    return sp; //如果内存中已经存在，那么直接返回  
}  
```  
  
可以看到，这里将SharedPreferences的实例对象SharedPreferencesImpl的先通过Map缓存起来，以后每次获取如果内存已经存在，那么直接返回，如果不存在才会重新创建。  
  
## 持久化数据的更新  
通常更新SharedPreferences的时候是首先获取一个`SharedPreferences.Editor`，利用它缓存一批操作，之后当做事务提交，有点类似于数据库的批量更新。  
  
Editor是一个接口，这里的实现是一个`EditorImpl`对象，它首先批量预处理更新操作，之后再提交更新。在提交事务的时候有两种方式，一种是apply，另一种commit，两者的区别在于：前者是异步的，后者是同步的。Google推荐使用前一种，因为，就单进程而言，只要保证内存缓存正确就能保证运行时数据的正确性，而持久化，不必太及时，这种手段在Android中使用还是很常见的，比如权限的更新也是这样，况且，Google并不希望SharePreferences用于多进程，因为不安全。  
  
无论调用哪一个方法都会调用 commitToMemory 和 enqueueDiskWrite 方法。commitToMemory 方法就是将值提交到内存当中，enqueueDiskWrite 将修改后的内容写入到磁盘当中，所以下一次取出的值是正确的。  
  
```java  
@Override  
public void apply() {  
    final MemoryCommitResult mcr = commitToMemory(); //提交到内存当中  
    final Runnable awaitCommit = new Runnable() {  
            @Override  
            public void run() {  
                try {  
                    mcr.writtenToDiskLatch.await();  
                } catch (InterruptedException ignored) {  
                }  
            }  
        };  
  
    QueuedWork.addFinisher(awaitCommit);  
  
    Runnable postWriteRunnable = new Runnable() { //提交一个事务到已给线程池，之后直接返回  
            @Override  
            public void run() {  
                awaitCommit.run();  
                QueuedWork.removeFinisher(awaitCommit);  
            }  
        };  
  
    SharedPreferencesImpl.this.enqueueDiskWrite(mcr, postWriteRunnable); //将修改后的内容写入到磁盘当中  
  
    // Okay to notify the listeners before it's hit disk because the listeners should always get the same SharedPreferences instance back, which has the changes reflected in memory.  
    notifyListeners(mcr);  
}  
```  
  
## MODE_MULTI_PROCESS 标记  
**结论**  
`MODE_MULTI_PROCESS`只是保证了在 API 11 以前的系统上，如果sp已经被读取进内存，再次获取这个SharedPreference的时候，如果有这个flag，会重新读一遍文件，仅此而已，并不能保证跨进程通信。  
  
**解释**  
SharePreferences在新建时有个mode参数，可以指定它的加载模式，`MODE_MULTI_PROCESS`是Google提供的一个多进程模式，但是这种模式并不是我们说的支持多进程同步更新等，它的作用只会在`getSharedPreferences`的时候，才会重新从xml重加载，如果我们在一个进程中更新xml，但是没有通知另一个进程，那么另一个进程的SharePreferences是不会自动更新的。  
  
```java  
@Override  
public SharedPreferences getSharedPreferences(File file, int mode) {  
    //...  
    if ((mode & Context.MODE_MULTI_PROCESS) != 0 ||   
        getApplicationInfo().targetSdkVersion < android.os.Build.VERSION_CODES.HONEYCOMB) { //11  
        //If somebody else (some other process) changed the prefs file behind our back, we reload it.  
        //This has been the historical (if ndocumented) behavior.  
        //如果其他人(其他进程)在我们后面更改了prefs文件，我们会重新加载它。这是历史(如果没有文档记录)的行为。  
        sp.startReloadIfChangedUnexpectedly();  
    }  
    return sp; //如果内存中已经存在，那么直接返回  
}  
```  
  
也就是说`MODE_MULTI_PROCESS`只是个鸡肋Flag，对于多进程的支持几乎为0，下面是Google文档，简而言之，就是：不要用。  
  
**文档描述**  
```java  
@Deprecated  
public static final int MODE_MULTI_PROCESS = 0x0004;  
```  
  
SharedPreference loading标志：设置后，即使已在此过程中加载了共享首选项实例，也会检查磁盘上的文件是否已修改。 在应用程序具有多个进程的情况下，有时需要此行为，所有进程都写入相同的SharedPreferences文件。 但是，通常在进程之间存在更好的通信形式。  
> SharedPreference loading flag: when set, the file on disk will be checked for modification even if the shared preferences instance is already loaded in this process.  This behavior is sometimes desired in cases where the application has multiple processes, all writing to the same SharedPreferences file. Generally there are better forms of communication between processes, though.  
  
这是Gingerbread（Android 2.3）之前的遗留（但未记录）行为，并且在针对此类版本时隐含了此标志。 对于针对SDK版本大于 Android 2.3的应用程序，如果需要，必须明确设置此标志。  
> This was the legacy (but undocumented) behavior in and before Gingerbread (Android 2.3) and this flag is implied when targetting such releases.  For applications targetting SDK versions greater than Android 2.3, this flag must be explicitly set if desired.  
  
`MODE_MULTI_PROCESS`在某些Android版本中无法可靠地工作，而且没有提供任何协调跨进程的并发修改的机制。 应用程序不应尝试使用它。 相反，他们应该使用明确的跨进程数据管理方法，例如ContentProvider。  
> MODE_MULTI_PROCESS does not work reliably in some versions of Android, and furthermore does not provide any mechanism for reconciling concurrent modifications across processes.  Applications should not attempt to use it.  Instead, they should use an explicit cross-process data management approach such as {@link android.content.ContentProvider ContentProvider}.  
  
2019-2-20  
