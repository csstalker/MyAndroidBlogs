| Markdown版本笔记 | 我的GitHub首页 | 我的博客 | 我的微信 | 我的邮箱 |  
| :------------: | :------------: | :------------: | :------------: | :------------: |  
| [MyAndroidBlogs][Markdown] | [baiqiantao][GitHub] | [baiqiantao][博客] | bqt20094 | baiqiantao@sina.com |  
  
[Markdown]:https://github.com/baiqiantao/MyAndroidBlogs  
[GitHub]:https://github.com/baiqiantao  
[博客]:http://www.cnblogs.com/baiqiantao/  
  
Singleton 单例模式 设计模式 Single  
  
***  
目录  
===  

- [简介](#简介)
- [单例模式的几种方式](#单例模式的几种方式)
	- [饿汉式：简单安全，但浪费资源](#饿汉式：简单安全，但浪费资源)
	- [懒汉式](#懒汉式)
		- [简单懒汉式：高效，但不安全](#简单懒汉式：高效，但不安全)
		- [加同步锁方式：性能差](#加同步锁方式：性能差)
		- [双重检查加锁模式：高效且安全](#双重检查加锁模式：高效且安全)
			- [基本形式](#基本形式)
			- [存在的安全隐患：指令重排序](#存在的安全隐患：指令重排序)
			- [添加 volatile 后的终极形式](#添加-volatile-后的终极形式)
	- [静态内部类方式：【推荐】](#静态内部类方式：【推荐】)
	- [枚举式：天然的防止反射](#枚举式：天然的防止反射)
  
# 简介  
作用：保证类只有一个实例；提供一个全局访问点  
JDK中的案例：  
```java  
java.lang.Runtime#getRuntime()  
```  
  
# 单例模式的几种方式  
## 饿汉式：简单安全，但浪费资源  
- 优点：以空间换时间。因为静态变量会在类加载时初始化，此时不会涉及多个线程对象访问该对象的问题，虚拟机保证只会加载一次该类，肯定不会发生并发访问的问题，因此，可以省略 `synchronized` 关键字。  
- 问题：如果只是加载本类，而不调用 `getInstance()`，甚至永远都不调用，则会造成`资源浪费`！  
  
```java  
class Single {  
    private static Single SINGLETON = new Single();//类加载的时候会连带着创建实例  
      
    private Single() {  
        System.out.println("创建了实例");  
    }  
      
    public static Single getInstance() {  
        return SINGLETON;  
    }  
}  
```  
  
## 懒汉式  
  
### 简单懒汉式：高效，但不安全  
- 优点：以时间换空间。延时加载，懒加载，用的时候才加载，不会像饿汉式那样可能造成资源浪费！  
- 问题：在多线程环境下存在风险  
  
```java  
class Single {  
    private static Single SINGLETON;  
  
    private Single() {  
        System.out.println("创建了实例");  
    }  
  
    public static Single getInstance() {  
        if (SINGLETON == null) SINGLETON = new Single();  
        return SINGLETON;  
    }  
}  
```  
  
**线程安全问题出现场景**  
如果两个线程A和B同时调用了该方法，然后以如下方式执行：  
- A进入if判断，此时 instance 为 null，因此进入if内  
- B进入if判断，此时A还没有创建 instance，因此 instance 也为 null，因此B也进入if内  
- A创建了一个instance并返回  
- 虽然此时 instance 不为 null，但因为B已经进入了if判断，所以B也会创建一个instance并返回  
- 此时问题出现了，我们的单例被创建了两次！  
  
**验证测试代码**  
  
验证逻辑很简单，首先在单例的`构造方法`中打印一条日志，然后我们创建几十上百个线程，并发的去调用单例模式的`getInstance()`方法，通过日志打印来判断到底执行了几次构造方法，依次来验证此种单例模式是否是安全的。  
  
```java  
public class Test {  
    public static void main(String[] args) {  
        Runnable runnable = new Runnable() {  
  
            @Override  
            public void run() {  
                Single.getInstance();  
            }  
        };  
        for (int i = 0; i < 100; i++) {  
            new Thread(runnable).start();  
        }  
    }  
}  
```  
  
经过测试，这种场景下多次创建Single实例并非是小概率事件，反而是大概率事件！  
  
### 加同步锁方式：性能差  
  
以上问题最直观的解决办法就是给`getInstance`方法加上一个`synchronize`锁，这样每次只允许一个线程调用getInstance方法：  
  
```java  
class Single {  
    private static Single SINGLETON;  
  
    private Single() {  
        System.out.println("创建了实例");  
    }  
  
    public static synchronized Single getInstance() {  
        if (SINGLETON == null) SINGLETON = new Single();  
        return SINGLETON;  
    }  
}  
```  
  
这种解决办法的确可以防止错误的出现，但是它却很影响性能：每次调用`getInstance`方法的时候都必须获得`Singleton.class`锁！  
  
而实际上，仅仅在创建实例时有线程安全问题，而当单例实例被创建以后，其后的请求没有必要再使用互斥机制了。  
  
### 双重检查加锁模式：高效且安全  
  
#### 基本形式  
  
目前大量人使用的都是这个`double-checked locking`的解决方案：  
  
```java  
class Single {  
    private static Single SINGLETON; //没添加【volatile】关键字之前  
  
    private Single() {  
        System.out.println("创建了实例");  
    }  
  
    public static Single getInstance() {  
        if (SINGLETON == null) { //目的是为了提高性能，避免非必要加锁  
            synchronized (Single.class) { //加锁保证现场安全  
                if (SINGLETON == null) SINGLETON = new Single(); //避免重复创建实例  
            }  
        }  
        return SINGLETON;  
    }  
}  
```  
  
让我们来看一下这个代码是如何工作的：  
- 当一个线程调用`getInstance()`方法后，首先会先检查SINGLETON是否为null，如果不是则直接返回其内容，这样避免了进入synchronized块所需要花费的资源。  
- 其次，即使上面提到的情况发生了，即两个线程同时进入了第一个if判断，那么他们也必须按照顺序执行`synchronized`块中的代码，第一个进入代码块的线程会创建一个新的Single实例，而后续的线程则因为无法通过if判断，而不会创建多余的实例。  
  
#### 存在的安全隐患：指令重排序  
  
上述描述似乎已经解决了我们面临的所有问题，但实际上，从JVM的角度讲，这些代码仍然可能发生错误。  
  
对于JVM而言，它执行的是一个个Java指令。在Java指令中`创建对象和赋值操作是分开进行的`，也就是说`SINGLETON = new Single();`语句是分两步执行的，但是JVM并不保证这两个操作的先后顺序，也就是说`有可能JVM会先为新的Single实例分配空间，然后直接赋值给SINGLETON，然后再去初始化这个Single实例`。即`先赋值指向了内存地址，再初始化`，这样就使出错成为了可能。  
  
我们仍然以A、B两个线程为例：  
- A、B线程同时进入了第一个 if 判断  
- A首先进入`synchronized`块，由于SINGLETON为null，所以它执行`SINGLETON = new Single();`  
- 由于JVM内部的优化机制，JVM先划出了一些分配给Single实例的`空白内存`，并赋值给SINGLETON成员(注意此时JVM没有开始初始化这个实例)，然后A离开了synchronized块。  
- 然后B进入synchronized块，由于SINGLETON此时不是null，因此它马上离开了synchronized块并将结果返回给调用该方法的程序。  
- 此时B线程打算使用Single实例，`却发现它没有被初始化`，于是错误发生了。  
  
---  
**下面参考另一篇文章的描述，意思是一样的**  
  
问题主要在于`SINGLETON = new Single();`这段代码并不是原子操作，原子性：即一个操作或者多个操作，要么全部执行并且执行的过程不会被任何因素打断，要么就都不执行(事务)。  
  
`SINGLETON = new Single();`其实做了三件事情：  
- 给 SINGLETON 实例分配内存  
- 初始化 Single() 实例  
- 将SINGLETON对象指向 new Single() 分配的内存空间  
  
问题就出在这儿了，因为JVM中有指令重排序的优化，所以呢正常情况按照1，2，3的顺序来，没毛病，但是也可能按照1，3，2的顺序来，这个时候就有问题了，调用的时候判断 `SINGLETON != null` 就直接返回 SINGLETON 实例，但是这个时候并没有进行初始化工作，所以在后续的调用中肯定就会报错了。  
  
所以这里需要引入了 volatile 修饰符修饰 SINGLETON 对象，因为 volatile 能够禁止指令重排序的功能，所以能解决我们的这个问题  
  
> 以上情况只是理论分析，实际我经过大量测试发现，这种情况根本展示不出来。但是面试时这个是非常重要的知识点。  
  
#### 添加 volatile 后的终极形式  
  
在`JDK1.5`之后，官方也发现了这个问题，故而具体化了 `volatile`，即在`JDK1.6`及以后，只要定义为`private volatile static`就可解决 DCL 失效问题。  
  
volatile 确保 INSTANCE 每次均在主内存中读取，这样虽然会牺牲一点效率，但也无伤大雅。  
  
```java  
class Single {  
    private static volatile Single SINGLETON; //添加【volatile】关键字  
  
    private Single() {  
        System.out.println("创建了实例");  
    }  
  
    public static Single getInstance() {  
        if (SINGLETON == null) { //目的是为了提高性能，避免非必要获取锁  
            synchronized (Single.class) { //加锁  
                if (SINGLETON == null) SINGLETON = new Single(); //线程安全  
            }  
        }  
        return SINGLETON;  
    }  
}  
```  
  
## 静态内部类方式：【推荐】  
JVM内部的机制能够保证当一个类被加载的时候，这个类的加载过程是线程互斥的。这样当我们第一次调用getInstance的时候，JVM能够帮我们保证INSTANCE只被创建一次，并且会保证把赋值给INSTANCE的内存初始化完毕，这样我们就不用担心上面的问题。  
  
另外，INSTANCE是在第一次加载SingleHolder时被创建的，而SingleHolder则只在调用getInstance方法的时候才会被加载，因此也实现了懒加载。  
  
总结：不会像饿汉式那样立即加载对象，且加载类时是线程安全的，从而兼具了`并发高效调用和延迟加载`的优势！  
  
```java  
class Single {  
      
    private Single() {  
        System.out.println("创建了实例");  
    }  
      
    public static Single getInstance() { //只有调用getInstance时才会加载静态内部类  
        return SingleHolder.SINGLETON;  
    }  
      
    private static class SingleHolder {  
        private static final Single SINGLETON = new Single();  
    }  
}  
```  
  
这种方法不仅能确保线程安全，也能保证单例的唯一性，同时也延迟了单例的实例化。  
  
## 枚举式：天然的防止反射  
  
线程安全、调用效率高，但不能延时加载，并且可以天然的防止反射和反序列化漏洞！  
  
```java  
enum Single {  
    SINGLETON;//定义一个枚举的元素，它就代表了Single的一个实例。元素的名字随意。  
    Single() {  
        System.out.println("创建了实例");  
    }  
}  
```  
  
2017-11-27  
