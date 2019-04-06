| Markdown版本笔记 | 我的GitHub首页 | 我的博客 | 我的微信 | 我的邮箱 |  
| :------------: | :------------: | :------------: | :------------: | :------------: |  
| [MyAndroidBlogs][Markdown] | [baiqiantao][GitHub] | [baiqiantao][博客] | bqt20094 | baiqiantao@sina.com |  
  
[Markdown]:https://github.com/baiqiantao/MyAndroidBlogs  
[GitHub]:https://github.com/baiqiantao  
[博客]:http://www.cnblogs.com/baiqiantao/  
  
服务 AIDL 定向tag IPC Parcelable 案例 MD  
***  
目录  
===  

- [AIDL 基础知识](#AIDL-基础知识)
	- [AIDL 是什么](#AIDL-是什么)
	- [基本语法](#基本语法)
	- [定向 tag](#定向-tag)
	- [为何要实现 Parcelable 接口](#为何要实现-Parcelable-接口)
	- [手动添加 readFromParcel() 方法](#手动添加-readFromParcel-方法)
	- [提示找不到 Java 类怎么办](#提示找不到-Java-类怎么办)
- [测试案例](#测试案例)
	- [实现 Parcelable 接口](#实现-Parcelable-接口)
	- [AIDL 文件](#AIDL-文件)
	- [自动生成的 Java 类](#自动生成的-Java-类)
	- [服务端代码](#服务端代码)
	- [客户端代码](#客户端代码)
	- [演示三种定向 tag](#演示三种定向-tag)
	- [源码分析](#源码分析)
		- [BookManager.Stub.Proxy](#BookManagerStubProxy)
		- [BookManager.Stub.onTransact](#BookManagerStubonTransact)
  
# AIDL 基础知识  
  
## AIDL 是什么  
> IPC：InterProcess Communication，跨进程通信  
> AIDL：Android Interface Definition Language，Android接口定义语言  
  
在Android系统中，因为每个应用程序都运行在自己的进程中，所以进程之间一般是无法直接进行数据交换的，为了实现跨进程，Android给我们提供了`Binder`机制，而这个机制使用的接口语言就是`AIDL`。  
  
每一个进程都有自己的`Dalvik VM`实例，都有自己的一块独立的内存，都在自己的内存上存储自己的数据，执行着自己的操作，都在自己的那片狭小的空间里过完自己的一生。每个进程之间都你不知我，我不知你，就像是隔江相望的两座小岛一样，都在同一个世界里，但又各自有着自己的世界。而`AIDL`，就是两座小岛之间沟通的桥梁。  
  
设计这门语言的目的就是为了实现`进程间通信`。  
  
但是，如果仅仅是要进行跨进程通信的话，其实我们还有其他的一些选择，比如 `BroadcastReceiver` , `Messenger` 等，为什么要用AIDL呢？  
  
因为 BroadcastReceiver 占用的系统资源比较多，如果是频繁的跨进程通信的话显然是不可取的；Messenger 进行跨进程通信时请求队列是同步进行的，无法并发执行，在有些要求多进程的情况下不适用，这种情况下就需要使用 AIDL 了。  
  
总之，通过这门语言，我们可以愉快的在一个进程访问或修改另一个进程的数据，以及调用它的一些特定的方法。  
  
## 基本语法  
这种接口语言并非真正的编程语言，只是定义两个进程间的`通信接口`而已，而据此生成的`Java接口文件`则是由 Android SD K中`platform-tools`目录下的`aidl.exe`工具自动生成的。  
  
其实AIDL的语法和 Java 中的接口定义的语法基本是一样的，只是在一些细微处有些许差别：  
- AIDL中定义的`接口名字`需要与aidl`文件名`相同，后缀名是`.aidl`  
- 接口和方法前面不要加任何Java修饰符  
  
**数据类型**  
AIDL默认支持一些数据类型，在使用这些数据类型的时候是不需要导包的，但是除了这些类型之外的数据类型，在使用之前必须`导包`，就算目标文件与当前正在编写的.aidl 文件在同一个包下（在 Java 中，这种情况是不需要导包的）。  
  
除了默认支持的数据类型之外的其他类型，都需要实现`Parcelable`接口。  
  
**默认支持的数据类型**  
默认支持的数据类型包括  
- 八种基本数据类型：byte，short，int，long，float，double，boolean，char。  
- CharSequence、String  
- List：List中的所有元素必须是AIDL支持的类型之一，或者是一个其他AIDL生成的接口，或者是定义的parcelable。`List可以使用泛型`。  
- Map：Map中的所有元素必须是AIDL支持的类型之一，或者是一个其他AIDL生成的接口，或者是定义的parcelable。`Map不支持泛型`。  
  
**两种类型的AIDL文件**  
AIDL文件可以分为两类，一类是用来定义`parcelable`对象，以供其他AIDL文件使用AIDL中的；一类是用来定义`方法接口`，以供系统使用来完成跨进程通信的。  
  
可以看到，这两类AIDL文件都是在"定义"些什么，而不涉及具体的实现，这就是为什么它叫做`Android接口定义语言`。  
  
## 定向 tag   
Android官网上在讲到AIDL的地方关于定向tag是这样介绍的：  
> All non-primitive parameters require a directional tag indicating which way the data goes. //所有的非基本数据类型的参数都需要一个定向tag来指出数据的流向  
> Either in , out , or inout. //可以是 in , out ,或者 inout  
> Primitives are in by default , and connot be otherwise.  //基本数据类型参数的定向tag默认是，并且只能是 in  
  
除了默认支持的数据类型之外的其他类型，均需要之定一个定向tag。  
  
AIDL中的定向 tag 表示了在跨进程通信中`数据的流向`，其中 in 表示`数据只能由客户端流向服务端`， out 表示数`据只能由服务端流向客户端`，而 inout 则表示`数据可在服务端与客户端之间双向流通`。  
  
其中，数据流向是针对在客户端中的那个传入方法的对象而言的：  
- in 为定向 tag 的话表现为服务端将会接收到一个那个`对象的完整数据`，但是客户端的那个对象不会因为服务端对传参的修改而发生变动；  
- out 的话表现为服务端将会接收到那个对象的`参数为空的对象`，但是在服务端对接收到的空对象有任何修改之后`客户端将会同步变动`；  
- inout 为定向 tag 的情况下，服务端将会接收到客户端传来对象的完整信息，`并且`客户端将会同步服务端对该对象的任何变动。  
  
## 为何要实现 Parcelable 接口  
由于不同的进程有着不同的内存区域，并且它们只能访问自己的那一块内存区域，所以我们不能像平时那样，传一个句柄过去就完事了（句柄指向的是一个内存区域），现在目标进程根本不能访问源进程的内存，那把它传过去又有什么用呢？所以我们必须将要传输的数据转化为能够`在内存之间流通`的形式。这个转化的过程就叫做序列化与反序列化。  
  
简单来说是这样的：比如现在我们要将一个对象的数据从客户端传到服务端去，我们就可以在客户端对这个对象进行`序列化`的操作，将其中包含的数据转化为序列化流，然后将这个序列化流传输到服务端的内存中去，再在服务端对这个数据流进行`反序列化`的操作，从而还原其中包含的数据。通过这种方式，我们就达到了在一个进程中访问另一个进程的数据的目的。  
  
Android中实现序列化有两种方式，一种是通过Java提供的`Serializable`接口，另一种是通过Android SDK提供的`Parcelable`接口。相比Serializable，Parcelable更加轻量级，性能更好，速度更快。  
  
Parcelable和Serializable的比较  
- 在内存的使用中，Parcelable在性能方面要强于Serializable。  
- Serializable在序列化操作的时候会产生大量的`临时变量`(原因是使用了`反射`机制)，从而导致`GC`的频繁调用，因此在性能上会稍微逊色。  
- Parcelable是以`Ibinder`作为信息载体的，在内存上的开销比较小，因此`在内存之间进行数据传递`的时候，Android推荐使用Parcelable。  
- 在读写数据的时候，Parcelable是在`内存`中直接进行读写，而Serializable是通过使用`IO流`的形式将数据读写入在`硬盘`上。  
  
虽然Parcelable的性能要强于Serializable，但是仍然有特殊的情况需要使用Serializable，而不去使用Parcelable，因为Parcelable无法将数据进行`持久化`，因此在将数据保存在`磁盘`的时候，仍然需要使用后者。  
  
在我们通过AIDL进行跨进程通信的时候，选择的序列化方式就是实现 Parcelable 接口。  
> 若AIDL文件中涉及到的所有数据类型均为默认支持的数据类型，则无此步骤，因为默认支持的那些数据类型都是可序列化的。  
  
## 手动添加 readFromParcel() 方法  
实现 Parcelable 接口一般有两种方式：  
  
方法一：根据AS的提示半自动创建  
- 首先，创建一个类，正常的书写其成员变量，建立getter和setter并添加一个无参构造方法。  
- 然后 implements Parcelable ，接着 as 就会报错，将鼠标移到那里，按下 alt+enter 让它自动解决错误，在弹出来的框里选择所有的成员变量，然后确定。  
- 你会发现类里多了一些代码，但是现在还是会报错，Book下面仍然有一条小横线，再次将鼠标移到那里，按下 alt+enter 让它自动解决错误。  
- 这次解决完错误之后就不会报错了，这个 Book 类也基本上实现了 Parcelable 接口，可以执行序列化操作了。  
  
方法二：使用`Android Parcelable Code Generator`等AS插件，你只需定义其成员变量，他就可以全自动的帮你将当前JavaBean修改为Parcelable接口的实现类。  
  
**注意，这里有一个坑**  
以上这两种方式生成的模板类只支持定向 tag 为 `in` 的对象。  
  
为什么呢？因为默认生成的类里面只有`writeToParcel()`方法，而如果要支持为 `out` 或者 `inout` 的定向 tag 的话，还需要实现`readFromParcel()`方法，但是而这个方法其实并没有在 `Parcelable` 接口里面定义，而只是**AIDL的规范**！如果你不写的话，可以发现，在自动生成的AIDL接口文件对于的Java文件中，其中有这么一行错误提示：  
![](index_files/3d6fd618-44b4-4efd-9c1d-5821b7593f6e.png)  
  
那么这个`readFromParcel()`方法应当怎么写呢？其实，你把以上两种方式自动生成的有参构造方法中的代码拷贝过来就行了。  
  
例如自动生成的构造方法为：  
```java  
public Book(Parcel in) {  
    name = in.readString();  
    price = in.readInt();  
}  
```  
  
那么我们需要添加的 readFromParcel 方法为：  
```java  
public void readFromParcel(Parcel in) {  
    name = in.readString();  
    price = in.readInt();  
}  
```  
  
像上面这样添加了 `readFromParcel()` 方法之后，我们的 Book 类的对象在 AIDL 文件里就可以用 `out` 或者 `inout` 来作为它的定向 tag 了。  
  
## 提示找不到 Java 类怎么办  
**注意：这里又有一个巨大的坑！**  
  
大家可能注意到了，在写 aidl 文件中，我一直在强调：Book.aidl 与 Book.java 的包名应当是一样的。这似乎理所当然的意味着这两个文件应当是在同一个包里面的，事实上，很多比较老的文章里就是这样说的，他们说最好都在 aidl 包里同一个包下，方便移植。  
  
然而在 Android Studio 里并不是这样！如果这样做的话，系统根本就找不到 Book.java 文件，从而在其他的AIDL文件里面使用 Book 对象的时候会报 Symbol not found 的错误。  
![](index_files/8da2787d-ac4e-4346-a9a4-b3aa21f2b00b.png)  
  
为什么会这样呢？因为 Gradle 。大家都知道，Android Studio 是默认使用 Gradle 来构建 Android 项目的，而 Gradle 在构建项目的时候会通过 sourceSets 来配置不同文件的访问路径，从而加快查找速度。问题就出在这里，Gradle 默认是将 java 代码的访问路径设置在 java 包下的，这样一来，如果 java 文件是放在 aidl 包下的话那么理所当然系统是找不到这个 java 文件的。那应该怎么办呢？  
  
方法一：（推荐）修改 build.gradle 文件，在 android{} 中间加上下面的内容：  
```groovy  
sourceSets {  
    main {  
        java.srcDirs = ['src/main/java', 'src/main/aidl']  
    }  
}  
```  
  
也就是把 java 代码的访问路径设置成了 java 包和 aidl 包，这样一来系统就会到 aidl 包里面去查找 java 文件，也就达到了我们的目的。  
  
方法二：（不推荐）把 `Book.java` 文件放到 java 目录下的包里，`保持其包名与 Book.aidl 一致`。只要它的包名不变，Book.aidl 就能找到 Book.java ，而只要 Book.java 在 java 包下，那么系统也是能找到它的。  
  
但是这样做的话也有一个问题，就是在移植相关 .aidl 文件和 .java 文件的时候没那么方便，不能直接把整个 aidl 文件夹拿过去完事儿了，还要单独将 .java 文件放到 java 文件夹里去。  
  
# 测试案例  
[客户端](https://github.com/baiqiantao/AIDLTagDemo2.git)    
[服务端](https://github.com/baiqiantao/AIDLTagDemo2.git)  
  
## 实现 Parcelable 接口  
定义要传输的 Java 类，必须实现 Parcelable 接口。  
  
```java  
package com.bqt.aidl;  
  
public class Book implements Parcelable {  
      
    private String name;  
    private int price;  
      
    public void readFromParcel(Parcel in) { //一定要注意，需要手动添加 readFromParcel() 方法  
        this.name = in.readString();  
        this.price = in.readInt();  
    }  
      
    @Override  
    public int describeContents() {  
        return 0;  
    }  
      
    @Override  
    public void writeToParcel(Parcel dest, int flags) {  
        dest.writeString(this.name);  
        dest.writeInt(this.price);  
    }  
      
    public Book() { //必须有无参构造方法  
    }  
      
    protected Book(Parcel in) {  
        this.name = in.readString();  
        this.price = in.readInt();  
    }  
      
    public static final Creator<Book> CREATOR = new Creator<Book>() {  
        @Override  
        public Book createFromParcel(Parcel source) {  
            return new Book(source);  
        }  
          
        @Override  
        public Book[] newArray(int size) {  
            return new Book[size];  
        }  
    };  
      
    //********************************************  可选的  **********************************************  
      
    public Book(String name, int price) {  
        this.name = name;  
        this.price = price;  
    }  
      
    public String getName() {  
        return name;  
    }  
      
    public void setName(String name) {  
        this.name = name;  
    }  
      
    public int getPrice() {  
        return price;  
    }  
      
    public void setPrice(int price) {  
        this.price = price;  
    }  
      
    @Override  
    public String toString() {  
        return "{name=" + name + "，price=" + price + "}";  
    }  
}  
```  
  
## AIDL 文件  
![](index_files/35f155b7-282d-4991-91e4-a6770a37b0a6.png)  
  
如果在java目录上`右键 --> new --> AIDL`方式创建一个 aidl 文件，此文件会默认生成到 aidl 目录下。  
  
定义一个可序列化对象 Book 供其他的 AIDL 文件使用  
```java  
package com.bqt.aidl; //Book.aidl 与 Book.java 的包名必须是一样的  
parcelable Book; //每一个要传输的 Java 类都对应一个 aidl 文件，注意parcelable是小写  
```  
  
定义方法接口  
```java  
package com.bqt.aidl;  
import com.bqt.aidl.Book; //必须导入所有需要使用的非默认支持数据类型的类的包，即使他们在同一包下  
  
interface BookManager {  
    Book addBookIn(in Book book); //定义参数时，除了Java基本类型以及String、CharSequence之外的类型，都需要在前面加上定向tag  
    Book addBookOut(out Book book);  
    Book addBookInout(inout Book book);  
}  
```  
  
配置源码目录  
```  
sourceSets {  
    main {  
        java.srcDirs = ['src/main/java', 'src/main/aidl']  
    }  
}  
```  
  
我们需要保证，在客户端和服务端中都有我们需要用到的所有 .aidl 文件和其中涉及到的 .java 文件，因此不管在哪一端写的这些东西，写完之后我们都要把这些文件复制到另一端去。  
  
## 自动生成的 Java 类  
在我们写完 AIDL 文件并 clean 或者 rebuild 项目之后，编译器会根据 AIDL 文件为我们生成一个与 AIDL 文件同名的 .java 文件：  
![](index_files/d6d45aa8-0935-4762-83ff-db167446dbf7.png)  
![](index_files/737104ad-91dc-4a44-8301-3ca1e38a9faa.png)  
  
此自动生成的 Java 类中有一个名为`Stub`的内部类，此内部类继承自`Binder`，且实现此 BookManager 接口：  
```java  
public static abstract class Stub extends android.os.Binder implements com.bqt.aidl.BookManager  
```  
  
此内部类有一个`asInterface(IBinder)`的静态方法，通过此方法可以将`IBinder`类型的对象转换成此 BookManager 类型的对象：  
```java  
public static com.bqt.aidlservice.IAidlBinderInterface asInterface(android.os.IBinder obj)  
```  
```java  
class MyServiceConnection implements ServiceConnection {  
    @Override  
    public void onServiceConnected(ComponentName name, IBinder service) {  
      BookManager mBookManager = BookManager.Stub.asInterface(service);  
    }  
}  
```  
  
通过此内部类，我们便可以在进程间通讯  
  
## 服务端代码  
服务端的逻辑是，首先接受客户端连接的请求，并把服务端处理好的`BookManager.Stub`的IBinder接口回传给客户端。在BookManager.Stub实现的方法里面，主要是接收客户端传过来的Book对象，并试图对其进行修改，然后把修改过的对象再传回去。  
  
![](index_files/035b0e09-1220-475c-bdb4-0102c3ec0126.png)  
  
Service 代码：  
```java  
public class AIDLService extends Service {  
      
    @Override  
    public IBinder onBind(Intent intent) {  
        Log.i("bqt", "【Service-onBind】");  
        return new MyBind();  
    }  
      
    //由之前【extends Binder implements BookManager】改为【extends BookManager.Stub】  
    private class MyBind extends BookManager.Stub {  
          
        @Override  
        public synchronized Book addBookIn(Book book) throws RemoteException {  
            modifyBook(book, 100, "Service-In");  
            return book;  
        }  
          
        @Override  
        public synchronized Book addBookOut(Book book) throws RemoteException {  
            modifyBook(book, 200, "Service-Out");  
            return book;  
        }  
          
        @Override  
        public synchronized Book addBookInout(Book book) throws RemoteException {  
            modifyBook(book, 300, "Service-Inout");  
            return book;  
        }  
          
        private void modifyBook(Book book, int i, String s) {  
            if (book != null) {  
                Log.i("bqt", "【Service-接收到的Book】" + book);  
                book.setPrice(i);//修改book，观察客户端的反馈  
                book.setName(s);  
                Log.i("bqt", "【Service-返回的Book】" + book);  
            }  
        }  
    }  
}  
```  
  
清单文件  
```xml  
<!-- 声明权限 -->  
<permission  
    android:name="com.bqt.permission"  
    android:protectionLevel="normal"/>  
  
<!-- 隐式服务 -->  
<service  
    android:name=".AIDLService"  
    android:permission="com.bqt.permission">  
    <intent-filter>  
        <action android:name="com.bqt.service.aidl"/>  
    </intent-filter>  
</service>  
```  
  
常用属性：  
- `enabled`：是否这个service能被系统实例化  
- `exported`：是否其它应用组件能调用这个service或同它交互  
- `permission`：为了启动这个service或绑定到它一个实体必须要有的权限的名称  
  
## 客户端代码  
客户端需先将远程服务的【.aidl文件】以及所有相关的Java文件连同包名拷贝到项目中。  
![](index_files/1d3e4373-7a8c-46cb-bb22-0eae811dcbe5.png)  
  
要先【安装】远程服务App再安装调用者App才可以。  
绑定服务时，即使服务端APP没有启动，客户端也能调起服务端的Service(使用 Context.BIND_AUTO_CREATE 的 flag)。  
  
Client 代码：  
```java  
public class MainActivity extends ListActivity {  
    private BookManager mBookManager;  
    private MyServiceConnection mServiceConnection;  
      
    protected void onCreate(Bundle savedInstanceState) {  
        super.onCreate(savedInstanceState);  
        String[] array = {  
            "addBookIn",  
            "addBookOut",  
            "addBookInout"};  
        setListAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, array));  
          
        mServiceConnection = new MyServiceConnection();  
        Intent intent = new Intent();  
        intent.setAction("com.bqt.service.aidl");  
        intent.setPackage("com.bqt.aidl2");  
        bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE); //flags:绑定时如果Service还未创建是否自动创建  
    }  
      
    @Override  
    protected void onDestroy() {  
        super.onDestroy();  
        if (mServiceConnection != null) {  
            unbindService(mServiceConnection);//多次调用会报IllegalArgumentException异常，但是并不崩溃  
        }  
        mBookManager = null;  
    }  
      
    @Override  
    protected void onListItemClick(ListView l, View v, int position, long id) {  
        if (mBookManager != null) {  
            try {  
                Book book = null, returnBook = null;  
                switch (position) {  
                    case 0:  
                        book = new Book("客户端-In", 10);  
                        Log.i("bqt", "【客户端-传进去的Book-执行前】" + book);  
                        returnBook = mBookManager.addBookIn(book);  
                        break;  
                    case 1:  
                        book = new Book("客户端-Out", 20);  
                        Log.i("bqt", "【客户端-传进去的Book-执行前】" + book);  
                        returnBook = mBookManager.addBookOut(book);  
                        break;  
                    case 2:  
                        book = new Book("客户端-Inout", 30);  
                        Log.i("bqt", "【客户端-传进去的Book-执行前】" + book);  
                        returnBook = mBookManager.addBookInout(book);  
                        break;  
                }  
                  
                Log.i("bqt", "【客户端-returnBook】" + returnBook);  
                Log.i("bqt", "【客户端-传进去的Book-执行后】" + book);  
            } catch (RemoteException e) {  
                e.printStackTrace();  
            }  
        }  
    }  
      
    private class MyServiceConnection implements ServiceConnection {  
        @Override  
        public void onServiceConnected(ComponentName name, IBinder service) {  
            Toast.makeText(MainActivity.this, "服务已连接", Toast.LENGTH_SHORT).show();  
            mBookManager = BookManager.Stub.asInterface(service); //核心代码  
        }  
          
        @Override  
        public void onServiceDisconnected(ComponentName name) {  
            Toast.makeText(MainActivity.this, "服务已断开", Toast.LENGTH_SHORT).show();  
            mBookManager = null;  
        }  
    }  
}  
```  
  
清单文件  
```xml  
<!-- 声明启动服务所需要的权限 -->  
<uses-permission android:name="com.bqt.permission"/>  
```  
  
## 演示三种定向 tag  
In  
```  
【客户端-传进去的Book-执行前】{name=客户端-In，price=10}  
【Service-接收到的Book】{name=客户端-In，price=10}    //收到了客户端原始对象的值【in】  
【Service-返回的Book】{name=Service-In，price=100}  
【客户端-returnBook】{name=Service-In，price=100}  
【客户端-传进去的Book-执行后】{name=客户端-In，price=10}   //客户端原始对象的值没有改变  
```  
  
Out  
```  
【客户端-传进去的Book-执行前】{name=客户端-Out，price=20}  
【Service-接收到的Book】{name=null，price=0}             //没有收到客户端原始对象的值  
【Service-返回的Book】{name=Service-Out，price=200}  
【客户端-returnBook】{name=Service-Out，price=200}  
【客户端-传进去的Book-执行后】{name=Service-Out，price=200}   //修改了客户端原始对象的值【out】  
```  
  
InOut  
```  
【客户端-传进去的Book-执行前】{name=客户端-Inout，price=30}  
【Service-接收到的Book】{name=客户端-Inout，price=30}    //收到了客户端原始对象的值【in】  
【Service-返回的Book】{name=Service-Inout，price=300}  
【客户端-returnBook】{name=Service-Inout，price=300}  
【客户端-传进去的Book-执行后】{name=Service-Inout，price=300}   //修改了客户端原始对象的值【out】  
```  
  
## 源码分析  
![](index_files/457f99ba-4c67-4b36-a5cc-1579caa9485a.png)  
  
在 AIDL 文件生成的 java 文件中，在进行远程调用的时候基本的调用顺序是：  
- 先从 `Proxy` 类中调用对应的方法  
- 然后在这些方法中调用 `transact()` 方法  
- 然后 Stub 中的 `onTransact()` 方法就会被调用  
- 然后在这个方法里面再调用具体的`业务逻辑`的方法  
- 当然，在这几个方法调用的过程中，总是会有一些关于数据的写入读出的操作，因为这些是跨进程操作，必须将数据序列化传输。  
  
### BookManager.Stub.Proxy  
```java  
private static class Proxy implements com.bqt.aidl.BookManager {  
    private android.os.IBinder mRemote;  
      
    Proxy(android.os.IBinder remote) {  
        mRemote = remote;  
    }  
      
    @Override  
    public android.os.IBinder asBinder() {  
        return mRemote;  
    }  
      
    public java.lang.String getInterfaceDescriptor() {  
        return DESCRIPTOR;  
    }  
      
    @Override  
    public com.bqt.aidl.Book addBookIn(com.bqt.aidl.Book book) throws android.os.RemoteException {  
        android.os.Parcel _data = android.os.Parcel.obtain(); //代表从客户端流向服务端的数据流  
        android.os.Parcel _reply = android.os.Parcel.obtain(); //代表从服务端流向客户端的数据流  
        com.bqt.aidl.Book _result; //代表客户端调用服务端方法后的返回值，返回值没什么可研究的  
        try {  
            _data.writeInterfaceToken(DESCRIPTOR);  
            if ((book != null)) {  
                _data.writeInt(1); //如果book不为空，则_data写入int值1  
                book.writeToParcel(_data, 0); //将book中的数据写入_data中(客户端的数据被完整保留了起来)  
            } else {  
                _data.writeInt(0); //如果book为空，则_data写入int值0  
            }  
            mRemote.transact(Stub.TRANSACTION_addBookIn, _data, _reply, 0); //间接调用onTransact方法  
            _reply.readException();  
            if ((0 != _reply.readInt())) {  
                _result = com.bqt.aidl.Book.CREATOR.createFromParcel(_reply);  
            } else {  
                _result = null;  
            }  
            //可以看到，在返回_result之前，没有修改客户端传递过来的book对象，所以客户端原始对象不会被改变  
        } finally {  
            _reply.recycle();  
            _data.recycle();  
        }  
        return _result;  
    }  
      
    @Override  
    public com.bqt.aidl.Book addBookOut(com.bqt.aidl.Book book) throws android.os.RemoteException {  
        android.os.Parcel _data = android.os.Parcel.obtain();  
        android.os.Parcel _reply = android.os.Parcel.obtain();  
        com.bqt.aidl.Book _result;  
        try {  
            _data.writeInterfaceToken(DESCRIPTOR);  
            //可以看到，这里没有将book对象写入_data流就开始传输了，也即客户端传过来的数据内容被丢弃了  
            mRemote.transact(Stub.TRANSACTION_addBookOut, _data, _reply, 0);  
            _reply.readException();  
            if ((0 != _reply.readInt())) {  
                _result = com.bqt.aidl.Book.CREATOR.createFromParcel(_reply);  
            } else {  
                _result = null;  
            }  
            if ((0 != _reply.readInt())) {  
                book.readFromParcel(_reply); //从_reply读取数据然后写入book中(客户端的数据被改变了)  
                //可以看到，在返回_result之前，会修改客户端传递过来的book对象，所以客户端原始对象会被改变  
            }  
        } finally {  
            _reply.recycle();  
            _data.recycle();  
        }  
        return _result;  
    }  
      
    @Override  
    public com.bqt.aidl.Book addBookInout(com.bqt.aidl.Book book) throws android.os.RemoteException {  
        android.os.Parcel _data = android.os.Parcel.obtain();  
        android.os.Parcel _reply = android.os.Parcel.obtain();  
        com.bqt.aidl.Book _result;  
        try {  
            _data.writeInterfaceToken(DESCRIPTOR);  
            if ((book != null)) {  
                _data.writeInt(1);  
                book.writeToParcel(_data, 0); //将book中的数据写入_data中(客户端的数据被完整保留了起来)  
            } else {  
                _data.writeInt(0);  
            }  
            mRemote.transact(Stub.TRANSACTION_addBookInout, _data, _reply, 0);  
            _reply.readException();  
            if ((0 != _reply.readInt())) {  
                _result = com.bqt.aidl.Book.CREATOR.createFromParcel(_reply);  
            } else {  
                _result = null;  
            }  
            if ((0 != _reply.readInt())) {  
                book.readFromParcel(_reply);//从_reply读取数据然后写入book中(客户端的数据被改变了)  
            }  
        } finally {  
            _reply.recycle();  
            _data.recycle();  
        }  
        return _result;  
    }  
}  
```  
  
### BookManager.Stub.onTransact  
```java  
@Override  
public boolean onTransact(int code, android.os.Parcel data, android.os.Parcel reply, int flags) throws android.os.RemoteException {  
    //data代表从客户端流向服务端的数据流，reply代表从服务端流向客户端的数据流  
    switch (code) {  
        case INTERFACE_TRANSACTION: {  
            reply.writeString(DESCRIPTOR);  
            return true;  
        }  
        case TRANSACTION_addBookIn: {  
            data.enforceInterface(DESCRIPTOR);  
            com.bqt.aidl.Book _arg0; //_arg0代表客户端输入的book对象  
            if ((0 != data.readInt())) {  
                _arg0 = com.bqt.aidl.Book.CREATOR.createFromParcel(data);//获取客户端输入的book对象  
            } else {  
                _arg0 = null;  
            }  
            com.bqt.aidl.Book _result = this.addBookIn(_arg0);//这里才是真正调用服务端写好的实现的地方【代理】  
            //_result代表客户端调用服务端方法后的返回值，返回值没什么可研究的  
            reply.writeNoException();  
            if ((_result != null)) {  
                reply.writeInt(1);  
                _result.writeToParcel(reply, android.os.Parcelable.PARCELABLE_WRITE_RETURN_VALUE); //将返回值写入reply  
            } else {  
                reply.writeInt(0);  
            }  
            //执行完方法之后就结束了，对_arg0的修改没有同步到reply  
            return true;  
        }  
        case TRANSACTION_addBookOut: {  
            data.enforceInterface(DESCRIPTOR);  
            com.bqt.aidl.Book _arg0;  
            _arg0 = new com.bqt.aidl.Book(); //可以看到，此时没有从data里读取book对象的操作，而是直接new了一个book对象，这就是为什么服务端收不到客户端传过来的数据  
            com.bqt.aidl.Book _result = this.addBookOut(_arg0);//【代理】  
            reply.writeNoException();  
            if ((_result != null)) {  
                reply.writeInt(1);  
                _result.writeToParcel(reply, android.os.Parcelable.PARCELABLE_WRITE_RETURN_VALUE); //将返回值写入reply  
            } else {  
                reply.writeInt(0);  
            }  
            //将_arg0写入reply中  
            if ((_arg0 != null)) {  
                reply.writeInt(1);  
                _arg0.writeToParcel(reply, android.os.Parcelable.PARCELABLE_WRITE_RETURN_VALUE);//同步_arg0  
            } else {  
                reply.writeInt(0);  
            }  
            return true;  
        }  
        case TRANSACTION_addBookInout: {  
            data.enforceInterface(DESCRIPTOR);  
            com.bqt.aidl.Book _arg0;  
            if ((0 != data.readInt())) {  
                _arg0 = com.bqt.aidl.Book.CREATOR.createFromParcel(data);//获取客户端输入的book对象  
            } else {  
                _arg0 = null;  
            }  
            com.bqt.aidl.Book _result = this.addBookInout(_arg0);//【代理】  
            reply.writeNoException();  
            if ((_result != null)) {  
                reply.writeInt(1);  
                _result.writeToParcel(reply, android.os.Parcelable.PARCELABLE_WRITE_RETURN_VALUE);  
            } else {  
                reply.writeInt(0);  
            }  
              
            if ((_arg0 != null)) {  
                reply.writeInt(1);  
                _arg0.writeToParcel(reply, android.os.Parcelable.PARCELABLE_WRITE_RETURN_VALUE);//同步_arg0  
            } else {  
                reply.writeInt(0);  
            }  
            return true;  
        }  
    }  
    return super.onTransact(code, data, reply, flags);  
}  
```  
  
2017-11-2  
