| Markdown版本笔记 | 我的GitHub首页 | 我的博客 | 我的微信 | 我的邮箱 |  
| :------------: | :------------: | :------------: | :------------: | :------------: |  
| [MyAndroidBlogs][Markdown] | [baiqiantao][GitHub] | [baiqiantao][博客] | bqt20094 | baiqiantao@sina.com |  
  
[Markdown]:https://github.com/baiqiantao/MyAndroidBlogs  
[GitHub]:https://github.com/baiqiantao  
[博客]:http://www.cnblogs.com/baiqiantao/  
  
Proxy 动态代理 InvocationHandler CGLIB MD  
***  
目录  
===  

- [InvocationHandler 接口](# invocationhandler 接口)
- [Proxy 代理类](# proxy 代理类)
	- [简介](# 简介)
	- [代理类具有的属性](# 代理类具有的属性)
	- [代理实例具有的属性](# 代理实例具有的属性)
	- [在多代理接口中重复的方法](# 在多代理接口中重复的方法)
	- [API](# api)
	- [底层实现原理](# 底层实现原理)
- [CGLIB 简介](# cglib 简介)
  
# InvocationHandler 接口  
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
  
# Proxy 代理类  
```java  
public class java.lang.reflect.Proxy extends Object implements java.io.Serializable  
```  
  
## 简介  
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
  
## 代理类具有的属性  
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
  
  
## 代理实例具有的属性  
- Given a 代理实例 _proxy_ and one of the interfaces implemented by its 代理类 _Foo_, 表达式 `proxy instanceof Foo` 将返回 true，并且 `(Foo) proxy` 的强制转换操作将会成功，而不抛出 ClassCastException。  
- Each `proxy instance` has an associated `invocation handler`, the one that was passed to its constructor. The static `Proxy.getInvocationHandler` method will return the `invocation handler` associated with the `proxy instance` passed as its argument.  
    > System.out.println(Proxy.getInvocationHandler(proxy) == handler);//true  
- An `interface method invocation` on a `proxy instance` will be encoded and dispatched to the `invocation handler's invoke method` as described in the documentation for that method.  
- An invocation of the `hashCode, equals, or toString` methods declared in java.lang.Object on a `proxy instance` will be encoded and dispatched to the `invocation handler's invoke method` in the same manner as `interface method invocations` are encoded and dispatched, as described above.   
    > 重写了Object类的equals、hashCode、toString，它们都只是简单的调用了InvocationHandler的invoke方法，即可以对其进行特殊的操作，也就是说JDK的动态代理还可以代理上述三个方法。  
- The declaring class of the Method object passed to invoke will be `java.lang.Object`.   
- Other public methods of a `proxy instance` inherited from `java.lang.Object` are not overridden by a proxy class, so `invocations of those methods behave` like they do for instances of `java.lang.Object`.  
  
  
## 在多代理接口中重复的方法  
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
  
## 底层实现原理  
关于JDK的动态代理，最为人熟知要可能要数Spring AOP的实现，默认情况下，Spring AOP的实现对于`接口`来说就是使用的JDK的动态代理来实现的，而对于`类`的代理使用CGLIB来实现。  
  
JDK从1.3开始支持动态代理。JDK的动态代理，就是在程序运行的过程中，根据被代理的接口来动态生成代理类的class文件，并加载运行的过程。  
  
代理类继承了Proxy类，实现了代理的接口，由于java不能多继承，所以代理类不能再继承其他的类，所以JDK的动态代理不支持对类的代理，只支持对接口的代理。  
  
在代理类所实现的接口方法的内部，其只是简单的调用了InvocationHandler的invoke方法，而没有调用委托类所实现的接口的方法，所以对使用者来说，其效果就是：委托类的接口方法被代理类所代理了(或者说拦截了)。  
  
如果我们在InvocationHandler的invoke方法中进行一些特殊操作，甚至可以直接返回，这基本上就是AOP的一个简单实现了。当然通常我们的做法是，在invoke方法中`通过反射调用目标对象的接口方法`，并可以根据需要在目标对象的方法执行前后插入任何需要的逻辑以进行功能的增强。    
  
# CGLIB 简介  
[GitHub主页](https://github.com/cglib/cglib)    
[WIKI](https://github.com/cglib/cglib/wiki)  
  
需要添加两个jar包：    
[CGLib 2.2](https://mvnrepository.com/artifact/cglib/cglib/2.2)    
[ASM 3.3.1](https://mvnrepository.com/artifact/asm/asm/3.3.1)    
  
> `Byte Code Generation Library` is `high level API` to `generate and transform` JAVA `byte code`. It is used by `AOP`, `testing`, `data access frameworks` to generate `dynamic proxy objects` and `intercept field access`.   
  
JDK从1.3版本起就提供了一个动态代理，它使用起来非常简单，但是有个明显的缺点：`需要目标对象实现一个或多个接口`。使用CGLIB即使代理类没有实现任何接口也可以实现动态代理功能。  
  
cglib是一个功能强大，高性能的代码生成库，它简单易用，它的`运行速度要远远快于JDK的动态代理`。在实现内部，CGLIB库使用了`ASM`这一个`轻量但高性能`的字节码操作框架来转化字节码，产生新类。ASM使用了一个类似于`SAX`分析器的机制来达到高性能。PS:使用ASM需要对JVM非常了解，包括类文件格式和指令集。  
  
CGLIB被广泛使用在基于代理的`AOP`框架提供方法拦截（例如 Spring AOP 和 dynaop）。Hibernate作为最流行的ORM工具也同样使用CGLIB库来代理单端关联。EasyMock和jMock作为流行的Java测试库，它们提供Mock对象的方式来支持测试，都使用了CGLIB来对没有接口的类进行代理。  
  
cglib的原理是通过`字节码`技术为一个类`创建子类`，并在子类中采用`方法拦截`的技术`拦截所有父类方法的调用`。由于是`通过创建子类来代理父类`，因此不能代理被`final`修饰的类(代理`final`修饰的类会抛异常，代理`final`修饰的方法只会原样执行委托类的方法而不能做任何拦截)。  
  
但是cglib有一个很致命的缺点：cglib的底层是采用著名的`ASM`字节码生成框架，使用字节码技术生成代理类，也就是通过操作字节码来生成的新的.class文件，而我们在`android`中加载的是`优化后的.dex文件`，也就是说我们需要可以动态生成.dex文件代理类，因此cglib在`Android`中是不能使用的。  
  
2018-10-7  
