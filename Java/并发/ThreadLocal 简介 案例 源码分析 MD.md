| Markdown版本笔记 | 我的GitHub首页 | 我的博客 | 我的微信 | 我的邮箱 |  
| :------------: | :------------: | :------------: | :------------: | :------------: |  
| [MyAndroidBlogs][Markdown] | [baiqiantao][GitHub] | [baiqiantao][博客] | bqt20094 | baiqiantao@sina.com |  
  
[Markdown]:https://github.com/baiqiantao/MyAndroidBlogs  
[GitHub]:https://github.com/baiqiantao  
[博客]:http://www.cnblogs.com/baiqiantao/  
  
ThreadLocal 简介 案例 源码分析 MD  
***  
目录  
===  

- [先来看基本用法](#先来看基本用法)
- [对ThreadLocal的理解](#对threadlocal的理解)
- [ThreadLocal 详解](#threadlocal-详解)
	- [正确理解 ThreadLocal](#正确理解-threadlocal)
	- [ThreadLocal 源码分析](#threadlocal-源码分析)
		- [构造方法](#构造方法)
		- [set 方法](#set-方法)
		- [ThreadLocalMap](#threadlocalmap)
		- [get 方法](#get-方法)
	- [总结](#总结)
	- [一个类型转换的坑](#一个类型转换的坑)
  
# 先来看基本用法  
```java  
class Person {  
    ThreadLocal<Long> longLocal = new ThreadLocal<Long>(); //创建一个用于保存 Long 类型数据的 ThreadLocal  
    ThreadLocal<String> stringLocal = new ThreadLocal<String>() {//创建一个用于保存 String 类型数据的 ThreadLocal  
        @Override  
        protected String initialValue() {  
            return Thread.currentThread().getName(); //初始化数据第一种方式：重写 initialValue 方法(推荐方式)  
        }  
    };  
    //ThreadLocal.withInitial(()-> "返回初始值"); //初始化数据第三种方式，在1.8中添加的API  
  
    public void setLongValue() {  
        longLocal.set(Thread.currentThread().getId()); //初始化数据第二种方式：调用 set 方法  
    }  
  
    public long getLong() {  
        return longLocal.get();  
    }  
  
    public String getString() {  
        return stringLocal.get();  
    }  
}  
```  
```java  
public class Test {  
  
    public static void main(String[] args) throws InterruptedException {  
        final Person person = new Person();  
  
        person.setLongValue(); //设置main线程中的对象的值  
        System.out.println("A：" + person.getLong()); //1  
        System.out.println("A：" + person.getString()); //main  
  
        Thread thread = new Thread() {  
            public void run() {  
                person.setLongValue(); //设置子线程中的对象的值  
                System.out.println("B：" + person.getLong()); //10  
                System.out.println("B：" + person.getString()); //Thread-0  
            };  
        };  
        thread.start();  
        thread.join(); //效果等同于同步  
  
        System.out.println("C：" + person.getLong()); //1  
        System.out.println("C：" + person.getString()); //main  
    }  
}  
```  
  
打印结果：  
```java  
A：1  
A：main  
B：10  
B：Thread-0  
C：1  
C：main  
```  
  
# 对ThreadLocal的理解  
[参考](https://blog.csdn.net/puppylpg/article/details/80433271)  
  
常用的几个 API  
```java  
ThreadLocal<Long> longLocal = new ThreadLocal<Long>();  
protected T initialValue() //一般是用来在使用时进行重写的，它是一个延迟加载方法  
  
public void set(T value)  
public T get()  
public void remove()  
```  
  
ThreadLocal提供了`线程独有的局部变量`，可以`在整个线程存活的过程中随时取用`，极大地方便了一些逻辑的实现。常见的ThreadLocal用法有：   
- `存储单个线程上下文信息`  
- `使变量线程安全`：变量既然成为了每个线程内部的局部变量，自然就不会存在并发问题了  
- `减少参数传递`  
  
**原理**  
ThreadLocal里类型的变量，其实是放入了当前Thread里。每个Thread都有一个`threadLocals`，它是一个map，这个map的entry是`ThreadLocal.ThreadLocalMap.Entry`，具体的key和value类型分别是`ThreadLocal`和Object。  
> 注：实际是ThreadLocal的弱引用 `WeakReference<ThreadLocal<?>>`，但可以先简单理解为ThreadLocal。  
  
对于一个普通的map，取其中某个key对应的值分两步：   
- 找到这个map；   
- 在map中，给出key，得到value。  
  
想取出我们存放在当前线程里的map里的值同样需要这两步，但是，我们不需要告诉jvm map在哪儿，因为jvm知道当前线程，也知道其局部变量map。所以最终的get操作只需要知道key(即`ThreadLocal`)就行了：`longLocal.get()`。  
  
**为什么key使用弱引用**  
不妨反过来想想，如果使用强引用，当ThreadLocal对象(假设为ThreadLocal@123456)的引用(即longLocal，是一个强引用，指向ThreadLocal@123456)被回收了，ThreadLocalMap本身依然还持有ThreadLocal@123456的强引用，如果没有手动删除这个key，则ThreadLocal@123456不会被回收，所以只要当前线程不消亡，ThreadLocalMap引用的那些对象就不会被回收，可以认为这导致Entry内存泄漏。  
  
那使用弱引用的好处呢？  
  
如果使用弱引用，那指向ThreadLocal@123456对象的引用就两个：longLocal强引用，和ThreadLocalMap中Entry的弱引用。一旦longLocal被回收，则指向ThreadLocal@123456的就只有弱引用了，在下次gc的时候，这个ThreadLocal@123456就会被回收。  
  
那么问题来了，ThreadLocal@123456对象只是作为ThreadLocalMap的一个`key`而存在的，现在它被回收了，但是它对应的`value`并没有被回收，内存泄露依然存在！而且key被删了之后，变成了`null`，value更是无法被访问到了！针对这一问题，ThreadLocalMap类的设计本身已经有了这一问题的解决方案，那就是在每次`get()/set()/remove()`ThreadLocalMap中的值的时候，会自动清理key为null的value。如此一来，value也能被回收了。  
  
**为什么不对value使用弱引用**  
答案显而易见，假设往ThreadLocalMap里存了一个value，gc过后value便消失了，那就无法使用ThreadLocalMap来达到存储全线程变量的效果了。  
  
**内存泄漏问题**  
弱引用一定程度上回收了无用对象，但前提是开发者手动清理掉ThreadLocal对象的强引用(如longLocal)。只要线程一直不死，ThreadLocalMap的key-value一直在涨。  
解决方法是，当某个ThreadLocal变量不再使用时，调用 `remove()` 方法删除该key。  
  
**使用线程池的问题**  
使用线程池可以达到线程复用的效果，但是归还线程之前记得清除`ThreadLocalMap`，要不然再取出该线程的时候，`ThreadLocal`变量还会存在。这就不仅仅是内存泄露的问题了，整个业务逻辑都可能会出错。  
  
所以ThreadLocal最好还是不要和线程池一起使用。  
  
# ThreadLocal 详解  
[参考](https://www.iteye.com/topic/103804)  
  
## 正确理解 ThreadLocal  
首先，ThreadLocal`不是用来解决共享对象的多线程访问问题的`，一般情况下，通过`ThreadLocal.set()`到线程中的对象是该线程自己使用的对象，其他线程是不需要访问的，也访问不到的，各个线程中访问的是不同的对象。   
  
另外，说ThreadLocal使得各线程能够`保持各自独立的一个对象`，并`不是通过ThreadLocal.set()来实现的`，而是通过每个线程中的`new`对象的操作来创建的对象，每个线程创建一个，`不是什么对象的拷贝或副本`。通过`ThreadLocal.set()`将这个新创建的对象的引用保存到各线程的自己的一个map中，每个线程都有这样一个map，执行`ThreadLocal.get()`时，各线程从自己的map中取出放进去的对象，因此取出来的是各自自己线程中的对象，`ThreadLocal实例是作为map的key来使用的`。   
  
如果`ThreadLocal.set()`进去的东西本来就是多个线程共享的同一个对象，那么多个线程的`ThreadLocal.get()`取得的还是这个共享对象本身，还是有并发访问问题。  
  
下面来看一个hibernate中典型的ThreadLocal的应用：   
```java  
private static final ThreadLocal threadSession = new ThreadLocal();  
  
public static Session getSession() throws InfrastructureException {  
    Session s = (Session) threadSession.get();  
    try {  
        if (s == null) {  
            s = getSessionFactory().openSession(); //创建一个session  
            threadSession.set(s);  
        }  
    } catch (HibernateException ex) {  
        throw new InfrastructureException(ex);  
    }  
    return s;  
}  
```  
  
可以看到在`getSession()`方法中，首先判断当前线程中有没有放进去session，如果还没有，那么通过`sessionFactory().openSession()`来创建一个session，再将session set到线程中，实际是放到当前线程的`ThreadLocalMap`这个map中，这时，对于这个session的唯一引用就是当前线程中的那个`ThreadLocalMap`，而threadSession作为这个值的`key`，要取得这个session可以通过`threadSession.get()`来得到，里面执行的操作实际是先取得当前线程中的`ThreadLocalMap`，然后将threadSession作为`key`将对应的值取出。  
  
这个session相当于线程的私有变量，而不是public的。显然，其他线程中是取不到这个session的，他们也只能取到自己的`ThreadLocalMap`中的东西。要是session是多个线程共享使用的，那还不乱套了。   
  
试想如果不用ThreadLocal怎么来实现呢？可能就要在action中创建session，然后把session一个个传到service和dao中，这可够麻烦的。或者可以自己定义一个`静态的map`，将当前thread作为key，创建的session作为值，put到map中，应该也行，这也是一般人的想法。但事实上，ThreadLocal的实现刚好相反，它是在`每个线程中有一个map`，而将ThreadLocal实例作为key，这样`每个map中的项数很少`，而且`当线程销毁时相应的东西也一起销毁了`。   
  
总之，ThreadLocal`不是用来解决对象共享访问问题的`，而主要是提供了保持对象的方法和`避免参数传递`的方便的对象访问方式。  
  
归纳了两点：   
- 每个线程中都有一个自己的`ThreadLocalMap`类对象，可以将线程自己的对象保持到其中，各管各的，线程可以正确的访问到自己的对象。   
- 将一个共用的`ThreadLocal静态实例`作为key，将`不同对象的引用`保存到`不同线程的ThreadLocalMap`中，然后在线程执行的各处通过这个`静态ThreadLocal实例`的get()方法取得自己线程保存的那个对象，避免了将这个对象作为参数传递的麻烦。   
  
当然如果要把本来线程共享的对象通过`ThreadLocal.set()`放到线程中也可以，可以实现避免参数传递的访问方式，但是要注意`get()`到的是那`同一个共享对象`，并发访问问题要靠其他手段来解决。但一般来说线程共享的对象通过设置为某类的`静态变量`就可以实现方便的访问了，似乎没必要放到线程中。   
  
ThreadLocal的应用场合，我觉得最适合的是`按线程多实例`(每个线程对应一个实例)的对象的访问，并且这个对象很多地方都要用到。   
  
## ThreadLocal 源码分析  
API  
```java  
ThreadLocal<Long> longLocal = new ThreadLocal<Long>();  
protected T initialValue() //一般是用来在使用时进行重写的，它是一个延迟加载方法  
  
public void set(T value)  
public T get()  
public void remove()  
```  
  
变量  
```java  
private final int threadLocalHashCode = nextHashCode();  //唯一的实例变量，而且还是不可变的  
private static int nextHashCode = 0;  //静态变量，表示即将分配的下一个ThreadLocal实例的threadLocalHashCode的值  
private static final int HASH_INCREMENT = 0x61c88647; //常量，表示了连续分配的两个ThreadLocal实例的threadLocalHashCode值的增量  
```  
  
### 构造方法  
可以来看一下创建一个 ThreadLocal 实例即 new ThreadLocal() 时做了哪些操作  
```java  
//Creates a thread local variable. @see #withInitial(java.util.function.Supplier)  
public ThreadLocal() {  
}  
```  
从上面看到构造函数 ThreadLocal() 里什么操作都没有，唯一的操作是这句：   
```java  
private final int threadLocalHashCode = nextHashCode();    
```  
而 nextHashCode() 就是将 ThreadLocal 类的下一个 hashCode 值即 nextHashCode 的值赋给实例的 threadLocalHashCode，然后 nextHashCode 的值增加 HASH_INCREMENT 这个值。   
  
因此，ThreadLocal实例的变量只有这个`threadLocalHashCode`，而且是final的，`用来区分不同的ThreadLocal实例`，ThreadLocal类主要是作为`工具类`来使用，那么`ThreadLocal.set()`进去的对象是放在哪儿的呢？   
  
### set 方法  
看一下set()方法：  
```java  
//Sets the current thread's copy of this thread-local variable to the specified value.    
//Most subclasses will have no need to override this method, relying solely on the initialValue method to set the values of thread-locals.  
//@param value ：the value to be stored in the current thread's copy of this thread-local.  
public void set(T value) {  
    Thread t = Thread.currentThread(); //取得当前线程  
    ThreadLocalMap map = t.threadLocals; //返回当前线程t中的一个成员变量 threadLocals  
    if (map != null) map.set(this, value); //将该 ThreadLocal 实例(而不是当前线程)作为key，要保持的对象作为值  
    else t.threadLocals = new ThreadLocalMap(this, value);  
}  
```  
也就是将该 ThreadLocal 实例作为key，要保持的对象作为值，设置到当前线程的 ThreadLocalMap 中  
  
### ThreadLocalMap  
这个 ThreadLocalMap 类是 ThreadLocal 中定义的内部类，但是它的实例却用在Thread类中：   
```java  
public class Thread implements Runnable {    
    //ThreadLocal values pertaining to this thread. This map is maintained by the ThreadLocal class.  
    ThreadLocal.ThreadLocalMap threadLocals = null;  
}    
```  
  
我们继续取看 ThreadLocalMap 的实现  
```java  
static class ThreadLocalMap {  
    //The entries in this hash map extend WeakReference, using its main ref field as the key (which is always a ThreadLocal object).  
    //Note that null keys mean that the key is no longer referenced, so the entry can be expunged擦去、移除 from table.    
    //Such entries are referred to as "stale陈旧的 entries" in the code that follows.  
    static class Entry extends WeakReference<ThreadLocal<?>> {  
        Object value;//The value associated with this ThreadLocal.  
  
        Entry(ThreadLocal<?> k, Object v) {  
            super(k);  
            value = v;  
        }  
    }  
    //...  
}  
```  
可以看到 ThreadLocalMap 的 Entry 继承了 WeakReference，并且使用 ThreadLocal 作为键值。  
  
### get 方法  
看一下get()方法：  
```java  
//Returns the value in the current thread's copy of this thread-local variable.  
//If the variable has no value for the current thread, it is first initialized to the value returned by an invocation of the initialValue method.  
//@return the current thread's value of this thread-local  
public T get() {  
    Thread t = Thread.currentThread();  
    ThreadLocalMap map = t.threadLocals;  
    if (map != null) {  
        ThreadLocalMap.Entry e = map.getEntry(this); //注意这里获取键值对传进去的是 ThreadLocal 实例，而不是当前线程 t  
        if (e != null) return (T)e.value;  
    }  
    return setInitialValue();  
}  
```  
  
再看 setInitialValue() 方法的具体实现：  
```java  
//Variant of set() to establish initialValue. Used instead of set() in case user has overridden the set() method.  
//@return the initial value  
private T setInitialValue() {  
    T value = initialValue();  
    Thread t = Thread.currentThread();  
    ThreadLocalMap map = t.threadLocals;   
    if (map != null) map.set(this, value);  
    else t.threadLocals = new ThreadLocalMap(this, value);  
    return value;  
}  
```  
可以看到，除了添加第一行和最后一行外，其他逻辑和 set 方法完全一样。  
  
第一行是调用 初始化方法 initialValue() 获取初始值，中间就是保持的这个值，最后一行就是返回这个值。  
```java  
protected T initialValue() {  
    return null;  
}  
```  
  
## 总结  
- 通过 ThreadLocal 创建的对象是存储在每个`线程`自己的 threadLocals 集合中的  
- 集合 threadLocals 的类型为 ThreadLocalMap，存储的实体为`WeakReference<ThreadLocal<?>>`，键为 ThreadLocal 对象  
- set 方法就是将该 ThreadLocal 实例作为 key，将要保持的对象作为值，设置到当前线程的 threadLocals 中  
- 在进行 get 之前，必须先 set，或者重写 initialValue() 方法，否则返回的是 null  
  
## 一个类型转换的坑  
如下代码的执行结果是什么：  
```java  
public class Test {  
  
    public static void main(String[] args) {  
        System.out.println(new Person().getLong());  
        System.out.println(new Person().getLong2());  
    }  
}  
  
class Person {  
    ThreadLocal<Long> longLocal = new ThreadLocal<Long>();  
  
    public Long getLong() {  
        return longLocal.get();  
    }  
  
    public long getLong2() {  
        return longLocal.get();  
    }  
}  
```  
  
空指针异常  
```java  
null  
Exception in thread "main" java.lang.NullPointerException  
    at Person.getLong2(Test.java:17)  
    at Test.main(Test.java:5)  
```  
  
第二种写法(返回基本类型 long 而非包装类型 Long)是不是感觉很坑？  
  
2019-1-21  
