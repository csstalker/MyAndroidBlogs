| Markdown版本笔记 | 我的GitHub首页 | 我的博客 | 我的微信 | 我的邮箱 |  
| :------------: | :------------: | :------------: | :------------: | :------------: |  
| [MyAndroidBlogs][Markdown] | [baiqiantao][GitHub] | [baiqiantao][博客] | bqt20094 | baiqiantao@sina.com |  
  
[Markdown]:https://github.com/baiqiantao/MyAndroidBlogs  
[GitHub]:https://github.com/baiqiantao  
[博客]:http://www.cnblogs.com/baiqiantao/  
  
依赖注入 DI 控制反转 IOC  
***  
目录  
===  

- [依赖注入 Dependency injection](#依赖注入-dependency-injection)
	- [Step1 设计](#step1-设计)
	- [Step2 设计](#step2-设计)
	- [Step3 设计](#step3-设计)
	- [总结](#总结)
- [控制反转 Inversion Of Control](#控制反转-inversion-of-control)
- [JAVA中依赖注入的几种方式](#java中依赖注入的几种方式)
  
# 依赖注入 Dependency injection  
这里通过一个简单的案例来说明。  
在公司里有一个常见的案例："把任务指派个程序员完成"。  
  
把这个案例用面向对象(OO)的方式来设计，我们创建两个类：Task 和 Phper (php 程序员)  
  
## Step1 设计  
```java  
public class Phper {  
    private String name;  
    public Phper(String name){  
        this.name=name;  
    }  
    public void writeCode(){  
        System.out.println(this.name + " is writing php code");  
    }  
}  
```  
  
```java  
public class Task {  
    private String name;  
    private Phper owner;  
    public Task(String name){  
        this.name =name;  
        this.owner = new Phper("zhang3");//关键看这里  
    }  
    public void start(){  
         System.out.println(this.name+ " started");  
         this.owner.writeCode();  
    }  
}  
```  
  
测试：  
```java  
public class MyFramework {  
     public static void main(String[] args) {  
         Task t = new Task("Task #1");  
         t.start();  
     }  
}  
```  
运行结果:  
  
    Task #1 started  
    zhang3is writing php code  
  
我们看一看这个设计有什么问题。  
如果同事仰慕你的设计，要重用你的代码，你把程序打成一个类库(jar包)发给同事。  
现在问题来了：同事发现这个Task 类 和 程序员 zhang3 绑定在一起，他所有创建的Task，都是程序员zhang3负责，他要把一些任务指派给Lee4， 就需要修改Task的源程序， 如果没有Task的源程序，就无法把任务指派给他人。  
  
而类库(jar包)的使用者通常不需要也不应该来修改类库的源码，我们很自然的想到，应该让用户来指派任务负责人，于是有了新的设计。  
  
## Step2 设计  
Phper不变。  
  
```java  
public class Task {  
    private String name;  
    private Phper owner;  
    public Task(String name){  
        this.name =name;  
    }  
    public void setOwner(Phper owner){//关键看这里。用户可在使用时按需指派特定的PHP程序员  
        this.owner = owner;  
    }  
    public void start(){  
         System.out.println(this.name+ " started");  
         this.owner.writeCode();  
    }  
}  
```  
  
测试：  
```java  
public class MyFramework {  
     public static void main(String[] args) {  
         Task t = new Task("Task #1");  
         Phper owner = new Phper("lee4");  
         t.setOwner(owner);//用户在使用时按需指派特定的PHP程序员  
         t.start();  
     }  
}  
```  
  
这样用户就可在使用时指派特定的PHP程序员。  
  
我们知道，Task类依赖Phper类，之前，Task类绑定特定的实例，现在这种依赖可以在使用时按需绑定，这就是依赖注入(DI)。  
这个例子，我们通过方法setOwner注入依赖对象，另外一个常见的注入办法是在Task的构造函数注入：  
```java  
public Task(String name，Phper owner){  
    this.name = name;  
    this.owner = owner;  
}  
```  
  
在Java开发中，把一个对象实例传给一个新建对象的情况十分普遍，通常这就是注入依赖，Step2 的设计实现了依赖注入。  
  
我们来看看Step2 的设计有什么问题。  
  
如果公司是一个单纯使用PHP的公司，所有开发任务都有Phper 来完成，这样这个设就已经很好了，不用优化。但是随着公司的发展，有些任务需要JAVA来完成，公司招了写Javaer(java程序员)，现在问题来了，这个Task类库的的使用者发现，任务只能指派给Phper，一个很自然的需求就是，Task应该即可指派给Phper也可指派给Javaer。  
  
## Step3 设计  
我们发现不管Phper 还是 Javaer 都是Coder(程序员)， 把Task类对Phper类的依赖改为对 Coder 的依赖即可。  
  
新增Coder接口  
```java  
public interface Coder {  
   void writeCode();  
}  
  
```  
修改Phper类实现Coder接口  
```java  
public class Phper implements Coder {  
   private String name;  
   public Phper(String name){  
      this.name=name;  
   }  
    @Override  
   public void writeCode(){  
      System.out.println(this.name + " is writing php code");  
   }  
}  
```  
  
新类Javaer实现Coder接口  
```java  
public class Javaer implements Coder {  
   private String name;  
   public Javaer(String name){  
      this.name=name;  
   }  
    @Override  
   public void writeCode(){  
      System.out.println(this.name + " is writing Java code");  
   }  
}  
```  
  
修改Task由对Phper类的依赖改为对Coder的依赖  
```java  
public class Task {  
    private String name;  
    private Coder owner;  
    public Task(String name) {  
        this.name = name;  
    }  
    public void setOwner(Coder owner) {  
        this.owner = owner;  
    }  
    public void start() {  
        System.out.println(this.name + " started");  
        this.owner.writeCode();  
    }  
}  
```  
  
测试  
```java  
public class MyFramework {  
    public static void main(String[] args) {  
        Task t = new Task("Task #1");  
        Coder owner = new Phper("lee4");  
        //Coder owner = new Javaer("Wang5");  
        t.setOwner(owner);  
        t.start();  
    }  
}  
```  
现在用户可以和方便的把任务指派给Javaer 了，如果有新的Pythoner加入，没问题，类库的使用者只需让Pythoner实现Coder接口，就可把任务指派给Pythoner， 无需修改Task 源码， 提高了类库的可扩展性。  
  
## 总结  
回顾一下，我们开发的Task类：  
- 在Step1 中，Task与特定实例绑定(zhang3 Phper)  
- 在Step2 中，Task与特定类型绑定(Phper)  
- 在Step3 中，Task与特定接口绑定(Coder)  
  
虽然都是绑定， 从Step1，Step2 到 Step3 灵活性、可扩展性是依次提高的。  
Step1 作为反面教材不可取， 至于是否需要从Step2 提升为Step3， 要看具体情况。  
  
依赖注入(DI)实现了控制反转(IoC)的思想，看看怎么反转的：  
- Step1 设计中 任务Task依赖负责人owner， 所以就主动新建一个 Phper 赋值给owner，这里可能是新建，也可能是在容器中获取一个现成的Phper，是新建还是获取无关紧要，关键是主动赋值。  
- 在Step2 和 Step3中， Task 的 owner 是被动赋值的，谁来赋值，Task自己不关心，可能是类库的用户，也可能是框架或容器，Task交出赋值权，从主动赋值到被动赋值， 这就是控制反转。  
  
# 控制反转 Inversion Of Control  
什么是控制反转 ?  
简单的说，从主动变被动就是控制反转。  
上文以依赖注入的例子，对控制反转做了个简单的解释。  
  
控制反转是一个很广泛的概念， 依赖注入是控制反转的一个例子，但控制反转的例子还很多，甚至与软件开发无关。  
传统的程序开发，人们总是从main 函数开始，调用各种各样的库来完成一个程序。这样的开发，开发者控制着整个运行过程。  
而现在人们使用框架(Framework)开发，使用框架时，框架控制着整个运行过程。  
  
对比以下的两个简单程序。  
  
1、简单java程序  
```java  
public class Activity {  
    public Activity() {  
        this.onCreate();//开发者主动调用onCreate()方法  
    }  
    public void onCreate() {  
        System.out.println("onCreate called");  
    }  
    public void sayHi() {  
        System.out.println("Hello world!");  
    }  
  
    public static void main(String[] args) {  
        Activity a = new Activity();  
        a.sayHi();  
    }  
}  
```  
  
2、简单Android程序  
```java  
public class MainActivity extends Activity {  
    @Override  
    public void onCreate(Bundle savedInstanceState) {  
        super.onCreate(savedInstanceState);  
        System.out.println("onCreate called");  
    }  
      
    public void sayHi(View view) {//某个View的点击事件  
        System.out.println("Hello world!");  
    }  
}  
```  
  
这两个程序最大的区别就是，前者程序的运行完全由开发控制，后者程序的运行由Android框架控制。  
  
虽然两个程序都有个onCreate方法，但在前者程序中，如果开发者觉得onCreate名称不合适，想改为Init，没问题，直接就可以改； 相比下，后者的onCreate名称就不能修改，因为后者使用了框架，享受框架带来福利的同时，就要遵循框架的规则。  
  
这就是控制反转。  
  
可以说， 控制反转是所有框架最基本的特征，也是框架和普通类库最大的不同点。  
  
通过框架可以把许多共用的逻辑放到框架里，让用户专注自己程序的逻辑，这也是为什么现在，无论手机开发，网页开发，还是桌面程序， 也不管是Java、PHP还是Python，框架无处不在。  
  
控制反转还有一个漂亮的比喻，好莱坞原则(Hollywood principle)：  
  
> "don't call us， we'll call you。"   
"不要打电话给我们，我们会打给你(如果合适)"   
  
这是好莱坞电影公司对面试者常见的答复。  
事实上，不只电影行业，基本上所有公司人力资源部对面试者都会说类似的话，让面试者从主动联系转换为被动等待。  
  
# JAVA中依赖注入的几种方式  
通过set方法注入  
```java  
public class ClassA {  
    ClassB classB;  
    public void setClassB(ClassB b) {  
        classB = b;  
    }  
}  
```  
  
通过构造器注入  
```java  
public class ClassA {  
    ClassB classB;  
    public void ClassA(ClassB b) {  
        classB = b;  
    }  
}  
```  
  
通过注解注入  
```java  
public class ClassA {  
    @inject ClassB classB;//此时并不会完成注入，还需要依赖注入框架的支持，如RoboGuice，Dagger2  
    ...  
    public ClassA() {}  
}  
```  
  
通过接口注入，形式一：  
```java  
interface InterfaceB {  
    void doIt();  
}  
```  
  
```java  
public class ClassA {  
    InterfaceB clzB;  
    public void doSomething() {  
        clzB = (InterfaceB) Class.forName("...").newInstance();//根据预先在配置文件中设定的实现类的类名动态加载实现类  
        clzB.doIt();  
    }  
}  
```  
  
此种接口注入方式因为具备侵入性，它要求组件必须与特定的接口相关联，因此实际使用有限。  
  
通过接口注入，形式二：  
```java  
public interface InjectB {  
    void injectB(ClassB b);  
}  
```  
  
```java  
class A implements InjectB {  
    ClassB classB;  
    @Override  
    public void injectB(ClassB b) {  
      classB = b;  
    }  
}  
```  
  
2017-9-14  
