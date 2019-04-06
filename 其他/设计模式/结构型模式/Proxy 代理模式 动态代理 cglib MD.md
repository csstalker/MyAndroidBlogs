| Markdown版本笔记 | 我的GitHub首页 | 我的博客 | 我的微信 | 我的邮箱 |  
| :------------: | :------------: | :------------: | :------------: | :------------: |  
| [MyAndroidBlogs][Markdown] | [baiqiantao][GitHub] | [baiqiantao][博客] | bqt20094 | baiqiantao@sina.com |  
  
[Markdown]:https://github.com/baiqiantao/MyAndroidBlogs  
[GitHub]:https://github.com/baiqiantao  
[博客]:http://www.cnblogs.com/baiqiantao/  
  
Proxy 代理模式 静态代理 动态代理 JDK cglib 结构型  
  
***  
目录  
===  

- [代理模式](#代理模式)
- [静态代理](#静态代理)
	- [静态代理简介](#静态代理简介)
	- [静态代理案例](#静态代理案例)
- [动态代理](#动态代理)
	- [JDK 动态代理案例](#JDK-动态代理案例)
	- [JDK 动态代理的局限性](#JDK-动态代理的局限性)
- [Cglib动态代理](#Cglib动态代理)
	- [CGLIB 简介](#CGLIB-简介)
	- [Cglib动态代理案例](#Cglib动态代理案例)
		- [Cglib代理类实现方式一](#Cglib代理类实现方式一)
		- [Cglib代理类实现方式二](#Cglib代理类实现方式二)
- [JDK 动态代理补充](#JDK-动态代理补充)
	- [底层实现原理](#底层实现原理)
	- [InvocationHandler 接口](#InvocationHandler-接口)
	- [Proxy 代理类](#Proxy-代理类)
		- [简介](#简介)
		- [代理类具有的属性](#代理类具有的属性)
		- [代理实例具有的属性](#代理实例具有的属性)
		- [在多代理接口中重复的方法](#在多代理接口中重复的方法)
	- [API](#API)
  
# 代理模式  
  
代理模式是一种结构型设计模式，其目的就是为对象提供一个代理以控制对其访问。代理类负责为委托类预处理消息，过滤消息并转发消息，以及进行消息被委托类执行后的后续处理。    
  
- 代理模式是用一个`简单的对象`来代替一个`复杂的对象`。    
- 代理模式给某一个对象提供一个代理对象，并`由代理对象控制对原对象的访问`。    
- 代理模式主要使用了 Java 的`多态`，干活的是被代理类，代理类主要是接活。    
  
为什么不直接访问原对象而要加个代理呢？    
- 采用代理模式可以有效的将`具体的实现`与`调用方`进行解耦，通过`面向接口`进行编程完全`将具体的实现隐藏在内部`。    
- 更通俗的说，代理解决的问题是：当两个类需要通信时，引入`第三方代理类`，将两个类的关系解耦，让我们只了解代理类即可；而且代理的出现还可以让我们完成与另一个类之间的关系的`统一管理`。  
  
代理模式按照`代理类的创建时期`，可以分为两种：  
- `静态代理`：代理类在程序`运行前`就已经被开发者在Java代码中写好了的。  
- `动态代理`：代理类并不是在Java代码中定义的，而是在程序`运行时`运用`反射`机制`动态创建`而成。  
  
# 静态代理  
## 静态代理简介  
静态代理的优点：  
- 客户端不需要知道委托类是什么、怎么做的，只需知道代理类即可，也即可以隐藏委托类的实现细节  
- 可以实现客户与委托类间的解耦，在不修改委托类代码的情况下能够做一些额外的处理  
  
静态代理的缺点：  
- 代理类和委托类实现了相同的接口，代理类通过委托类实现了相同的方法，但是因为`代理类中接口的方法往往是没什么逻辑的`，它通常只是调用了委托类的同名方法而已，所以这就导致出现了`大量重复、冗余的代码`。  
- 如果接口中增加或修改了某个方法，除了所有委托类需要修改代码外，所有代理类也需要修改代码，`增加了代码维护的复杂度`。  
- 代理对象只服务于`一种类型`的对象，即一个静态代理类只能为特定的`某一个接口`服务，如想要为`多个接口`服务则需要建立多个代理类，这在大型系统中大大增加了复杂度。  
  
静态代理的一般实现过程：  
- 首先定义一个接口  
- 然后创建实现这个接口的具体实现类，具体实现类中需要将接口中定义的方法的业务逻辑功能实现  
- 再创建一个代理类同样实现这个接口，代理类中接口的方法只要调用具体类中的对应方法即可  
  
## 静态代理案例  
首先定义一个接口    
```java  
interface IUser {  
 void eat(String s);  
 void run();  
}  
```  
然后创建实现这个接口的具体实现类  
```java  
class UserImpl implements IUser {  
    private String name;  
  
    public UserImpl(String name) {  
        this.name = name;  
    }  
  
    @Override  
    public void eat(String s) {  
        System.out.println(name + "吃" + s);  
        //省去代码三千行  
    }  
  
    @Override  
    public void run() {  
        System.out.println(name + "开始跑");  
        //省去代码三千行  
    }  
}  
```  
再创建一个代理类同样实现这个接口  
```java  
class UserProxy implements IUser { //核心1：代理类和被代理类要实现相同的接口  
    private IUser user; //核心2：要在代理类中指定具体的被代理人  
  
    public UserProxy() {  
        this.user = new UserImpl("包青天"); //客户端可以不指定具体的被代理类对象  
    }  
  
    public UserProxy(IUser user) {  
        this.user = user; //客户端也可以指定具体的被代理类对象，当然也可以通过set方式指定被代理类对象  
    }  
  
    @Override  
    public void eat(String s) {  
        System.out.println("eat前的操作");  
        user.eat(s);//核心3：在调用代理类的方法时，实际是调用的被代理类的方法，利用的就是多态的特性  
        System.out.println("eat后前的操作");  
    }  
  
    @Override  
    public void run() {  
        user.run();  
    }  
}  
```  
使用案例  
```java  
new UserProxy().eat("苹果"); //不指定委托类的对象  
new UserProxy(new UserImpl("baiqiantao")).run(); //指定委托类的对象  
```  
  
# 动态代理  
静态代理是在`编译时`就将接口、实现类、代理类一股脑儿全部手动完成。但如果我们需要很多的代理，每一个都这么手动的去创建实属浪费时间，而且会有大量的重复代码，且难以扩展，此时我们就可以采用动态代理。  
  
动态代理可以在程序`运行时`根据需要`动态的创建代理类及其实例`来完成具体的功能。主要用的是 JAVA 的`反射机制`。动态代理中的代理类并不是在 Java 代码中定义的，而是在`运行时`根据我们在 Java 代码中的"指示"动态生成的。  
  
> 注意：不是动态生成实例，而是动态生成类！  
  
## JDK 动态代理案例  
首先定义一个或多个接口：  
```java  
interface IUser {  
    void eat(String s);  
    void run();  
}  
```  
```java  
interface IWork {  
    String work(String s);  
}  
```  
  
然后创建实现这个(些)接口的具体实现类：  
```java  
class UserImpl implements IUser, IWork {  
    private String name;  
  
    public UserImpl(String name) {  
        this.name = name;  
    }  
  
    @Override  
    public void eat(String s) {  
        System.out.println(name + "吃" + s);  
        //省去代码三千行  
    }  
  
    @Override  
    public void run() {  
        System.out.println(name + "开始跑");  
        //省去代码三千行  
    }  
  
    @Override  
    public String work(String s) {  
        System.out.println(name + "工作：" + s);  
        //省去代码三千行  
        return "走上人生巅峰";  
    }  
}  
```  
  
然后创建一个 `InvocationHandler` ，目的是获取代理对象：  
> 注意，和静态代理不同的是，我们创建的这个`InvocationHandler`可以代理任何的委托类，而不必为每一个接口或抽象类创建单独的代理类，除非我们需要在 `invoke` 方法中针对不同的委托类做不同额外的处理。  
  
```java  
class MyInvocationHandler implements InvocationHandler {  
    private Object target;//持有委托类的引用，但并不限定委托类必须是某一接口或某一抽象类  
  
    public MyInvocationHandler(Object target) {//这里是用Object接收的，所以可以传递任何类型的对象，而不局限与IUser或IWork  
        this.target = target;  
    }  
  
    @Override  
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {  
        System.out.println("------------------------before------------------------"); //在目标对象的方法执行之前可以执行一些逻辑  
        Object returnObj = method.invoke(target, args); //【核心点】通过反射执行某个类的某方法  
        System.out.println("------------------------after------------------------"); //在目标对象的方法执行之后也可以执行一些逻辑  
        System.out.println("【代理类】" + proxy.getClass().getName() //【$Proxy0】  
                + "\n【代理类的超类】" + proxy.getClass().getSuperclass().getName() //【java.lang.reflect.Proxy】  
                + "\n【代理的方法声明】" + method.toGenericString() //【public abstract void IUser.eat(java.lang.String)】  
                + "\n【传入方法的实参】" + Arrays.toString(args) //【苹果】  
                + "\n【方法执行后的返回值】" + returnObj); //【null】  
        return returnObj;  
    }  
  
    //获取目标对象的代理对象  
    public Object getProxy() {  
        return Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), //也可以使用【this.getClass().getClassLoader()】  
                target.getClass().getInterfaces(), this);  
    }  
  
    //为什么要提供这么一个方法？等会看一下下面的解释就明白了！  
    public Object getProxy(Class<?>[] interfaces) {  
        return Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), interfaces, this);  
    }  
}  
```  
  
然后我们就可以愉快的玩耍了，比如这样：  
```java  
IUser user = new UserImpl("包青天"); //委托类  
IUser proxy = (IUser) new MyInvocationHandler(user).getProxy(); //代理类  
proxy.eat("苹果");  
```  
![](index_files/a5b78e65-c17d-4831-b0b5-9b9836cf4493.png)  
  
或者这样：  
```java  
IUser user = new UserImpl("包青天"); //委托类  
IWork proxy = (IWork) new MyInvocationHandler(user).getProxy(); //代理类  
proxy.work("Android开发");  
```  
![](index_files/e99f00f1-714a-44d4-a04e-e0f8483b287d.png)  
  
甚至这样：  
```java  
CheckBox checkBox = findViewById(R.id.check_box);  
Checkable proxy = (Checkable) new MyInvocationHandler(checkBox).getProxy(new Class[]{Checkable.class});  
checkBox.setOnCheckedChangeListener((view, isCheck) -> proxy.setChecked(isCheck)); //代理了CheckBox的选择状态改变事件  
```  
![](index_files/c1c48681-0555-4776-8bec-4ddc9063ce67.png)  
  
## JDK 动态代理的局限性  
我们先不从原理层面去分析这个问题，我们先从 Proxy 这个类的 newProxyInstance 方法的设计上去分析。    
  
newProxyInstance 方法的参数列表如下：    
```java  
newProxyInstance(ClassLoader loader, Class<?>[] interfaces, InvocationHandler h)  
```  
这里面要求提供三个参数，第一个是 `ClassLoader`，ClassLoader这个东西我还不是特别懂，但是你只需要知道系统提供了很多种类型的ClassLoader，你还可以自定义ClassLoader。比如你如果要加载网络上的一个`class`文件，在这样的情况下，默认的ClassLoader就不能满足我们的需求了，你就需要定义自己的ClassLoader。所以，为了应对各种业务场景，这里让你提供一个ClassLoader是很可以理解的。  
  
然后再说一个第二个参数`interfaces`，几乎所有的代码都是通过这样来获取接口列表的：  
```java  
target.getClass().getInterfaces()  
```  
你有没有想过，如果这样就可以了的话，为何不让你直接提供一个委托类的对象`target`，甚至为何不直接去掉这个参数呢？你看我们在构建最后那个那个参数`InvocationHandler`时已经传进去了一个委托类的对象`target`，那我们就完全没有必要再单独把`target`传进去了呀！  
  
要解释这个问题，我们从下面一段代码说起：  
```java  
interface I1 {}  
interface I2 {}  
class A implements I1 {}  
class B extends A implements I2 {}  
```  
那么请问，通过下面的代码获取到的都有哪些接口呢？  
```java  
new B().getClass().getInterfaces();  
```  
答案是只有 I2 而没有其父类实现的接口 I1。    
如果你觉得是我用错方法了，你觉得API中应该有另一个方法可以获取类 B 实现的接口以及类 B 的父类所实现的接口，那么我很遗憾的告诉你，没有你要的这个API！  
  
当然我们可以通过类似如下方式自己遍历所有父类实现的接口：  
```java  
private static Class[] getInterfaces(Object object) {  
    List<Class> list = new ArrayList<Class>();  
    Class clazz = object.getClass();  
    do {  
        list.addAll(Arrays.asList(clazz.getInterfaces()));  
        clazz = clazz.getSuperclass();  
    } while (clazz != Object.class);  
  
    return list.toArray(new Class[list.size()]);  
}  
```  
  
但是总觉得不是很舒服，因为这完全是API应该设计好的，为何让我们自己费这么大劲去遍历呢！如果你知道不提供这个API的原因，希望你可以告诉我。  
  
我们继续回到刚才那个问题，(可能是)因为API没有提供方法让我们获取一个类所实现的接口以及其父类所实现的接口的集合，所以为了将动态代理设计的更简单以及更通用，系统干脆让我们自己去定义这个接口集合好了。  
  
说了这么多，其实还没说到局限性到底局限到哪呢！其实看过上面的废话你也已经明白了，局限就局限在这个接口集合上，如果一个类没有实现接口，或者一个类中的某个方法不是重写的接口中的方法，这时我们就没办法提供接口集合，所以我们也就没办法使用动态代理了！  
  
实际上，JDK的动态代理是非常有局限性的，特别是在Android这种大量基于事件的系统中！  
  
# Cglib动态代理  
  
[GitHub主页](https://github.com/cglib/cglib)    
[WIKI](https://github.com/cglib/cglib/wiki)  
  
要使用cglib，需要添加两个jar包：    
[CGLib 2.2](https://mvnrepository.com/artifact/cglib/cglib/2.2)    
[ASM 3.3.1](https://mvnrepository.com/artifact/asm/asm/3.3.1)    
  
## CGLIB 简介  
  
cglib是一个功能强大，高性能的代码生成包。  
  
> `Byte Code Generation Library` is high level API to `generate and transform` JAVA byte code. It is used by `AOP`, testing, data access frameworks to generate `dynamic proxy objects` and `intercept field access`.   
  
JDK从1.3版本起就提供了一个动态代理，它使用起来非常简单，但是有个明显的缺点：`需要目标对象实现一个或多个接口`。使用CGLIB即使代理类没有实现任何接口也可以实现动态代理功能。  
  
cglib是一个功能强大，高性能的代码生成库，它简单易用，它的`运行速度要远远快于JDK的动态代理`。在实现内部，CGLIB库使用了`ASM`这一个`轻量但高性能`的字节码操作框架来转化字节码，产生新类。ASM使用了一个类似于`SAX`分析器的机制来达到高性能。  
  
CGLIB被广泛使用在基于代理的`AOP`框架提供方法拦截（例如 Spring AOP 和 dynaop）。Hibernate作为最流行的ORM工具也同样使用CGLIB库来代理单端关联。EasyMock和jMock作为流行的Java测试库，它们提供Mock对象的方式来支持测试，都使用了CGLIB来对没有接口的类进行代理。  
  
cglib的原理是通过`字节码`技术为一个类`创建子类`，并在子类中采用`方法拦截`的技术`拦截所有父类方法的调用`。由于是`通过创建子类来代理父类`，因此不能代理被`final`修饰的类。  
> 代理`final`修饰的类会抛异常，代理`final`修饰的方法只会原样执行委托类的方法而不能做任何拦截。  
  
但是cglib有一个很致命的缺点：cglib的底层是采用著名的`ASM`字节码生成框架，使用字节码技术生成代理类，也就是通过操作字节码来生成的新的.class文件，而我们在`android`中加载的是`优化后的.dex文件`，也就是说我们需要可以动态生成.dex文件代理类，因此cglib在`Android`中是不能使用的。  
  
## Cglib动态代理案例  
委托类：  
```java  
class UserImpl {  
    private String name;  
  
    public UserImpl() {  
    }  
  
    public UserImpl(String name) {  
        this.name = name;  
    }  
  
    public String work(String s) {  
        System.out.println(name + "工作：" + s);  
        //省去代码三千行  
        return "走上人生巅峰";  
    }  
  
    public final void finalWork(String s) {  
        System.out.println("==========================" + name + "工作：" + s);  
    }  
}  
```  
  
### Cglib代理类实现方式一  
这种方式类似于JDK动态代理的方式，都是直接调用委托类对象的方法：  
```java  
class CglibProxy<T> implements MethodInterceptor {  
    private T target;  
  
    @SuppressWarnings("unchecked")  
    public T getProxy(T target) {  
        this.target = target;  
        Enhancer enhancer = new Enhancer(); //Cglib 核心类，生成代理对象  
        enhancer.setSuperclass(target.getClass());//设置代理对象的父类  
        enhancer.setCallback(this);//设置增强功能的类对象，拦截器  
        return (T) enhancer.create();//获取委托类的代理对象，调用这个方法要求委托类必须有【无参构造方法】，否则必须带参数的 create 方法  
    }  
  
    @Override  
    public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {  
        System.out.println("------------------------before------------------------"); //在目标对象的方法执行之前可以执行一些逻辑  
        Object returnObj = proxy.invoke(target, args); //和JDK动态代理完全一样，通过反射调用委托类对象的方法  
        System.out.println("------------------------after------------------------"); //在目标对象的方法执行之后也可以执行一些逻辑  
        System.out.println("【代理类】" + obj.getClass().getName() //【UserImpl$$EnhancerByCGLIB$$22569189】  
                + "\n【代理类的超类】" + obj.getClass().getSuperclass().getName() //【UserImpl】  
                + "\n【代理的方法声明】" + method.toGenericString() //【public abstract void IUser.eat(java.lang.String)】  
                + "\n【传入方法的实参】" + Arrays.toString(args) //【Android开发】  
                + "\n【方法执行后的返回值】" + returnObj); //【走上人生巅峰】  
        return returnObj;  
    }  
}  
```  
  
使用案例：  
```java  
UserImpl user = new UserImpl("包青天") //委托类  
UserImpl proxy = new CglibProxy<UserImpl>().getProxy(user); //代理类  
proxy.work("JavaEE开发");  
proxy.finalWork("Android开发");  
```  
![](index_files/2fdf58dc-b732-414f-8fe0-692a138a92a5.png)    
  
### Cglib代理类实现方式二  
这种方式不需要提供委托类的对象，  
```java  
class CglibProxy<T> implements MethodInterceptor {  
    @SuppressWarnings("unchecked")  
    public T getProxy(Class<T> clazz) {  
        Enhancer enhancer = new Enhancer(); //Cglib 核心类，生成代理对象  
        enhancer.setSuperclass(clazz);//设置代理对象的父类  
        enhancer.setCallback(this);//设置增强功能的类对象，拦截器  
        return (T) enhancer.create();//获取委托类的代理对象，调用这个方法要求委托类必须有无参构造方法  
    }  
  
    @SuppressWarnings("unchecked")  
    public T getProxy(Class<T> clazz, Class[] args, Object[] argsValue) {  
        Enhancer enhancer = new Enhancer();  
        enhancer.setSuperclass(clazz);  
        enhancer.setCallback(this);  
        return (T) enhancer.create(args, argsValue);//【new Class[] { String.class }】【 new Object[] { "包青天" }】  
    }  
  
    @Override  
    public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {  
        System.out.println("------------------------before------------------------"); //在目标对象的方法执行之前可以执行一些逻辑  
        Object returnObj = proxy.invokeSuper(obj, args); //通过反射执行某个类的某方法，注意不能调用【invoke】方法  
        System.out.println("------------------------after------------------------"); //在目标对象的方法执行之后也可以执行一些逻辑  
        System.out.println("【代理类】" + obj.getClass().getName() //【UserImpl$$EnhancerByCGLIB$$22569189】  
                + "\n【代理类的超类】" + obj.getClass().getSuperclass().getName() //【UserImpl】  
                + "\n【代理的方法声明】" + method.toGenericString() //【public abstract void IUser.eat(java.lang.String)】  
                + "\n【传入方法的实参】" + Arrays.toString(args) //【Android开发】  
                + "\n【方法执行后的返回值】" + returnObj); //【走上人生巅峰】  
        return returnObj;  
    }  
}  
```  
  
使用案例：  
```java  
UserImpl proxy = new CglibProxy<UserImpl>().getProxy(UserImpl.class);  
proxy.work("JavaEE开发");  
  
UserImpl proxy2 = new CglibProxy<UserImpl>().getProxy(UserImpl.class, new Class[] { String.class }, new Object[] { "包青天" });  
proxy2.finalWork("Android开发");  
```  
![](index_files/ce738fc9-c40f-4538-b3c1-4829a71c6bc4.png)    
  
  
# JDK 动态代理补充  
  
## 底层实现原理  
关于JDK的动态代理，最为人熟知要可能要数Spring AOP的实现，默认情况下，Spring AOP的实现对于`接口`来说就是使用的JDK的动态代理来实现的，而对于`类`的代理使用CGLIB来实现。  
  
JDK从1.3开始支持动态代理。JDK的动态代理，就是在程序运行的过程中，根据被代理的接口来动态生成代理类的class文件，并加载运行的过程。  
  
代理类继承了Proxy类，实现了代理的接口，由于java不能多继承，所以代理类不能再继承其他的类，所以JDK的动态代理不支持对类的代理，只支持对接口的代理。  
  
在代理类所实现的接口方法的内部，其只是简单的调用了InvocationHandler的invoke方法，而没有调用委托类所实现的接口的方法，所以对使用者来说，其效果就是：委托类的接口方法被代理类所代理了(或者说拦截了)。  
  
如果我们在InvocationHandler的invoke方法中进行一些特殊操作，甚至可以直接返回，这基本上就是AOP的一个简单实现了。当然通常我们的做法是，在invoke方法中`通过反射调用目标对象的接口方法`，并可以根据需要在目标对象的方法执行前后插入任何需要的逻辑以进行功能的增强。    
  
## InvocationHandler 接口  
```java  
java.lang.reflect.InvocationHandler  
```  
`InvocationHandler` is the interface implemented by the `invocation handler` of a `proxy instance`.  
  
Each `proxy instance` has an `associated invocation handler`. When a method is invoked on a `proxy instance`, the method invocation is encoded and `dispatched` to the invoke method of its `invocation handler`.    
> 对代理实例调用方法时，将对方法调用进行编码并将其指派到它的调用处理程序的 invoke 方法。  
  
定义的方法：  
```java  
public Object invoke(Object proxy, Method method, Object[] args) throws Throwable;  
```  
Processes a `method invocation` on a `proxy instance` and returns the result. This method will be invoked on an `invocation handler` when a method is invoked on a `proxy instance` that it is associated with.    
> 在代理实例上处理方法调用并返回结果。在与方法关联的代理实例上调用方法时，将在调用处理程序上调用此方法。  
  
参数：   
- `proxy` - the `proxy instance` that the method was invoked on  
    > 在其上调用方法的代理实例  
- `method` - the `Method instance` corresponding to the `interface method` invoked on the `proxy instance`.   
    > 对应于在代理实例上调用的接口方法的 Method 实例。  
- `args` - an array of objects containing the values of the arguments passed in the `method invocation` on the `proxy instance`, or null if `interface method` takes no arguments.  
    > 包含传入`代理实例`上`方法调用`的参数值的对象数组，如果接口方法不使用参数，则为 null。  
  
返回值：    
the value to return from the `method invocation` on the `proxy instance`.  
> 从代理实例的方法调用返回的值。  
  
## Proxy 代理类  
```java  
public class java.lang.reflect.Proxy extends Object implements java.io.Serializable  
```  
  
### 简介  
Proxy provides static methods for creating `dynamic proxy classes and instances`, and it is also the `superclass of all dynamic proxy classes` created by those methods.    
  
To create a proxy for some interface Foo:  
```java  
InvocationHandler handler = new MyInvocationHandler(new FooImpl());//FooImpl是实现Foo接口的委托类  
Foo f = (Foo) Proxy.getProxyClass(Foo.class.getClassLoader(), new Class[] { Foo.class })  
    .getConstructor(new Class[] { InvocationHandler.class })  
    .newInstance(new Object[] { handler });  
```  
  
or more simply:  
```java  
Foo proxy = (Foo) Proxy.newProxyInstance(Foo.class.getClassLoader(),//定义了由哪个ClassLoader对象来对生成的代理对象进行加载  
        new Class[] { Foo.class },//表示的是我将要给我需要代理的对象提供一组什么接口，之后我这个代理对象就会实现了这组接口  
        handler);//表示的是当我这个动态代理对象在调用方法的时候，会关联到哪一个InvocationHandler对象上  
```  
  
A `dynamic proxy class` is a class that implements a list of interfaces `specified at runtime` when the class is created, with behavior as described below.   
- A `proxy interface` is such an interface that is implemented by a proxy class.   
- A `proxy instance` is an instance of a proxy class.   
- Each proxy instance has an associated `invocation handler` object, which implements the interface `InvocationHandler`.   
- A method invocation on a `proxy instance` through one of its `proxy interfaces` will be dispatched to the invoke method of the instance's `invocation handler`, passing the `proxy instance`, a `java.lang.reflect.Method` object identifying the method that was invoked, and an array of type Object containing the arguments.   
- The `invocation handler` processes the encoded method invocation as appropriate and the result that it returns will be returned as the result of the `method invocation` on the `proxy instance`.  
  
### 代理类具有的属性  
- 代理类是 public, final, 并且不是 abstract.  
    > System.out.println(Modifier.toString(proxy.getClass().getModifiers()));//final  
    > System.out.println(Modifier.toString(Proxy.class.getModifiers()));//public  
- 未指定代理类的非限定名称。但是，以字符串 "$Proxy" 开头的类名空间应该为代理类保留。  
    > System.out.println(proxy.getClass().getName());//【$Proxy0】  
- 代理类都 extends java.lang.reflect.Proxy.  
- 代理类会按同一顺序准确地实现其创建时指定的接口。  
- 如果代理类实现了非公共接口，那么它将在与该接口相同的包中定义。否则，代理类的包也是未指定的。注意，包密封将不阻止代理类在运行时在特定包中的成功定义，也不会阻止相同类加载器和带有特定签名的包所定义的类。  
- 由于代理类将实现所有在其创建时指定的接口，所以对其 Class 对象调用 getInterfaces 将返回一个包含相同接口列表的数组，对其 Class 对象调用 getMethods 将返回一个包括这些接口中所有方法的 Method 对象的数组，并且调用 getMethod 将会在代理接口中找到期望的一些方法。  
- 如果 Proxy.isProxyClass 方法传递代理类（由 Proxy.getProxyClass 返回的类，或由 Proxy.newProxyInstance 返回的对象的类），则该方法返回 true，否则返回 false。  
- 代理类的 java.security.ProtectionDomain 与由引导类加载器（如 java.lang.Object）加载的系统类相同，原因是代理类的代码由受信任的系统代码生成。此保护域通常被授予 java.security.AllPermission。  
- 每个代理类都有一个可以带一个参数（接口 InvocationHandler 的实现）的公共构造方法，用于设置代理实例的调用处理程序。并非必须使用反射 API 才能访问公共构造方法，通过调用 Proxy.newInstance 方法（将调用 Proxy.getProxyClass 的操作和调用带有调用处理程序的构造方法结合在一起）也可以创建代理实例。  
  
  
### 代理实例具有的属性  
- Given a 代理实例 _proxy_ and one of the interfaces implemented by its 代理类 _Foo_, 表达式 `proxy instanceof Foo` 将返回 true，并且 `(Foo) proxy` 的强制转换操作将会成功，而不抛出 ClassCastException。  
- Each `proxy instance` has an associated `invocation handler`, the one that was passed to its constructor. The static `Proxy.getInvocationHandler` method will return the `invocation handler` associated with the `proxy instance` passed as its argument.  
    > System.out.println(Proxy.getInvocationHandler(proxy) == handler);//true  
- An `interface method invocation` on a `proxy instance` will be encoded and dispatched to the `invocation handler's invoke method` as described in the documentation for that method.  
- An invocation of the `hashCode, equals, or toString` methods declared in java.lang.Object on a `proxy instance` will be encoded and dispatched to the `invocation handler's invoke method` in the same manner as `interface method invocations` are encoded and dispatched, as described above.   
    > 重写了Object类的equals、hashCode、toString，它们都只是简单的调用了InvocationHandler的invoke方法，即可以对其进行特殊的操作，也就是说JDK的动态代理还可以代理上述三个方法。  
- The declaring class of the Method object passed to invoke will be `java.lang.Object`.   
- Other public methods of a `proxy instance` inherited from `java.lang.Object` are not overridden by a proxy class, so `invocations of those methods behave` like they do for instances of `java.lang.Object`.  
  
  
### 在多代理接口中重复的方法  
乱七八糟的，这个问题设计时最好规避。  
  
## API  
字段  
> `protected InvocationHandler h` 此代理实例的调用处理程序。  
  
构造方法  
> `protected Proxy(InvocationHandler h)` 从子类(通常是动态代理类)构造一个新的Proxy实例，并为其调用处理程序指定值。  
  
  
公共方法  
- `static InvocationHandler getInvocationHandler(Object proxy)` 返回指定代理实例的调用处理程序。  
- `static boolean isProxyClass(Class<?> cl)` 当且仅当指定的类通过 `getProxyClass` 方法或 `newProxyInstance` 方法动态生成为代理类时，返回 true。  
- static Class<?> getProxyClass(ClassLoader loader, Class<?>... interfaces) 返回代理类的 java.lang.Class 对象，并向其提供类加载器和接口数组。    
- `static Object newProxyInstance(ClassLoader loader, Class<?>[] interfaces, InvocationHandler h)` 返回一个指定接口的代理类实例，该接口可以将方法调用指派到指定的调用处理程序。  
>     
    返回值：一个带有代理类的指定调用处理程序的代理实例，它由指定的类加载器定义，并实现指定的接口    
    参数：  
    - loader - 定义代理类的类加载器。定义了由哪个ClassLoader对象来对生成的代理对象进行加载  
    - interfaces - 代理类要实现的接口列表。表示的是我将要给我需要代理的对象提供一组什么接口，之后我这个代理对象就会实现了这组接口  
    - h - 指派方法调用的调用处理程序。表示的是当我这个动态代理对象在调用方法的时候，会关联到哪一个InvocationHandler对象上  
  
2016-10-31  
