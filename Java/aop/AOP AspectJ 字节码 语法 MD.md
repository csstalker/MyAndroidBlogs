| Markdown版本笔记 | 我的GitHub首页 | 我的博客 | 我的微信 | 我的邮箱 |  
| :------------: | :------------: | :------------: | :------------: | :------------: |  
| [MyAndroidBlogs][Markdown] | [baiqiantao][GitHub] | [baiqiantao][博客] | bqt20094 | baiqiantao@sina.com |  
  
[Markdown]:https://github.com/baiqiantao/MyAndroidBlogs  
[GitHub]:https://github.com/baiqiantao  
[博客]:http://www.cnblogs.com/baiqiantao/  
  
AOP AspectJ 字节码 语法 MD  
***  
目录  
===  

- [闲谈 AOP](#闲谈-AOP)
- [AspectJ 介绍](#AspectJ-介绍)
	- [Join Points 执行点](#Join-Points-执行点)
	- [Pointcuts 切入点](#Pointcuts-切入点)
		- [测试代码](#测试代码)
		- [测试案例](#测试案例)
		- [直接针对JPoint的选择](#直接针对JPoint的选择)
		- [间接针对JPoint的选择](#间接针对JPoint的选择)
	- [advice 执行时机](#advice-执行时机)
	- [参数传递和 JPoint 信息](#参数传递和-JPoint-信息)
	- [JoinPoint 信息收集](#JoinPoint-信息收集)
	- [总结](#总结)
- [使用AOP的例子](#使用AOP的例子)
	- [打印Log](#打印Log)
	- [检查权限](#检查权限)
- [其他](#其他)
	- [如何使用 sjc 编译源文件](#如何使用-sjc-编译源文件)
	- [在 Android 中编译](#在-Android-中编译)
	- [总结](#总结)
  
[参考](https://blog.csdn.net/innost/article/details/49387395)  
[AspectJ 官网](https://eclipse.org/aspectj/)  
[AspectJ 官方详细文档](https://www.eclipse.org/aspectj/doc/next/progguide/index.html)    
[AspectJ Development Environment Guide](https://eclipse.org/aspectj/doc/released/devguide/index.html)  
  
[语法大全1](http://www.eclipse.org/aspectj/doc/released/quick5.pdf)  
[语法大全2](http://www.eclipse.org/aspectj/doc/released/progguide/semantics.html)  
  
[AspectJ 类库参考文档](http://www.eclipse.org/aspectj/doc/released/runtime-api/index.html)  
[AspectJ 文档](http://www.eclipse.org/aspectj/doc/released/aspectj5rt-api/index.html)  
  
# 闲谈 AOP  
大家都知道OOP，即`Object Oriented Programming`，面向对象编程。而本文要介绍的是AOP。AOP是`Aspect Oriented Programming`的缩写，中译文为面向切向编程。OOP和AOP是什么关系呢？  
- OOP和AOP都是`方法论`。我记得在刚学习C++的时候，最难学的并不是C++的语法，而是C++所代表的那种看问题的方法，即OOP。同样，今天在AOP中，我发现其难度并不在利用AOP干活，而是从AOP的角度来看待问题，设计解决方法。这就是为什么我特意强调AOP是一种方法论的原因！  
- 在OOP的世界中，问题或者功能都被划分到一个一个的模块里边。每个模块专心干自己的事情，模块之间通过设计好的接口交互。非常好理解，而且确实简化了我们所处理问题的难度。  
  
OOP的精髓是把功能或问题`模块化`，每个模块处理自己的家务事。但在现实世界中，并不是所有问题都能完美得划分到模块中。举个最简单而又常见的例子：现在想为每个模块加上`日志功能`，要求模块运行时候能输出日志。在不知道AOP的情况下，一般的处理都是：先设计一个日志输出模块，这个模块提供日志输出API，比如Android中的Log类。然后，其他模块需要输出日志的时候调用Log类的几个函数，比如e(TAG,...)，w(TAG,...)，d(TAG,...)，i(TAG,...)等。  
  
但是，从OOP角度看，除了日志模块本身，其他模块的家务事都不应该包含日志输出功能。什么意思？以 ActivityManagerService 为例，你能说它的家务事里包含日志输出吗？显然 ActivityManagerService 不包含。但实际上，软件中的众多模块确实又需要打印日志。这个日志输出功能，从整体来看，都是一个`面`上的，而这个`面`的范围，就不局限在单个模块里了，而是`横跨多个模块`。  
  
在没有AOP之前，各个模块要打印日志，就是自己处理。反正日志模块的那几个API都已经写好了，你在其他模块的任何地方，任何时候都可以调用。功能是得到了满足，但是好像没有Oriented的感觉了。是的，随意加日志输出功能，使得其他模块的代码和日志模块耦合非常紧密，而且，将来要是日志模块修改了API，则使用它们的地方都得改。这种搞法，一点也不酷。  
  
AOP的目标就是解决上面提到的不cool的问题。在AOP中：  
- 第一，我们要认识到OOP世界中，有些功能是横跨并嵌入众多模块里的，比如打印日志，比如统计某个模块中某些函数的执行时间等。这些功能在各个模块里分散得很厉害，可能到处都能见到。  
- 第二，AOP的目标是把这些功能集中起来，`放到一个统一的地方来控制和管理`。如果说，OOP如果是`把问题划分到单个模块`的话，那么AOP就是`把涉及到众多模块的某一类问题进行统一管理`。比如我们可以设计两个Aspects，一个是管理某个软件中所有模块的日志输出的功能，另外一个是管理该软件中一些特殊函数调用的权限检查。  
  
# AspectJ 介绍  
AOP 虽然是方法论，但就好像 OOP 中的 Java 一样，一些先行者也开发了一套语言来支持 AOP。目前用得比较火的就是 AspectJ 了，它是一种几乎和Java完全一样的语言，而且完全兼容Java（AspectJ应该就是一种扩展Java，但它不是像Groovy那样的拓展）。  
  
当然，除了使用AspectJ特殊的语言外，AspectJ还支持原生的Java，只要加上对应的AspectJ注解就好。  
  
所以，使用AspectJ有两种方法：  
- 完全使用AspectJ的语言。这语言和Java几乎一样，也能在AspectJ中调用Java的任何类库，AspectJ只是多了一些关键词罢了。  
- 使用纯Java语言开发，然后使用AspectJ注解，简称`@AspectJ`。  
  
Anyway，不论哪种方法，最后都需要AspectJ的`编译工具 ajc`来编译。由于AspectJ扩展自Java，所以ajc工具也能编译java源码。  
  
## Join Points 执行点  
`Join Points`（以后简称JPoints）是AspectJ中最关键的一个概念。什么是JPoints呢？JPoints就是程序运行时的一些`执行点`。那么，一个程序中，哪些执行点是JPoints呢？比如：一个函数(或代码段)的调用、一个函数(或代码段)的执行可、设置一个变量或者读取一个变量都可以看做是一个JPoints。  
  
理论上说，一个程序中很多地方都可以被看做是JPoint，但是AspectJ中，只有如下表所示的几种执行点被认为是 JPoints：  
  
| Join Points | 说明 | 示例 |  
| :------------: | :------------: | :------------: |  
| method call | 函数调用 | 比如调用 Log.e() |  
| method execution | 函数执行 | 比如 Log.e() 的执行内部 |  
| constructor call | 构造函数调用 | 和 method call 类似 |  
| constructor execution | 构造函数执行 | 和 method execution 类似 |  
| field get | 获取某个变量 | 比如读取 Log.debug 成员 |  
| field set | 设置某个变量 | 比如设置 Log.debug 成员 |  
| pre-initialization | 对象在构造函数中做的一些工作 | 很少使用，详情见下面的例子 |  
| initialization | 对象在构造函数中做的一些工作 | 详情见下面的例子 |  
| static initialization | 类初始化 | 比如类的 static{} 代码块 |  
| handler | 异常处理 | 比如 try/catch 中 catch 内的执行 |  
| advice execution | AspectJ 的内容，稍后再说 |  AspectJ的内容，稍后再说 |  
  
> 注意：method call 是调用某个函数的地方，而 method execution 是某个函数执行的内部。   
  
为什么AspectJ首先要定义好JoinPoint呢？大家仔细想想就能明白，以打印log的AopDemo为例，log在哪里打印？自然是在一些关键点去打印。而谁是关键点？AspectJ定义的这些类型的JPoint就能满足我们绝大部分需求。  
  
> 注意，要是想在一个for循环中打印一些日志，而AspectJ没有这样的JPoint，所以这个需求我们是无法利用AspectJ来实现了。另外，不同的软件框架对上表中的JPoint类型支持也不同。比如Spring中，不是所有AspectJ支持的JPoint都有。  
  
## Pointcuts 切入点  
从前面介绍的内容可知，一个程序会有很多的JPoints，即使是同一个函数，还分为call类型和execution类型的JPoint。显然，不是所有的JPoint，也不是所有类型的JPoint都是我们关注的。怎么从一堆一堆的JPoints中选择自己想要的JPoints呢？恩，这就是Pointcuts的功能。  
  
一句话，Pointcuts的目标是提供一种方法使得开发者能够`选择自己感兴趣的JoinPoints`。  
  
AspectJ中，pointcut有一套标准语法，涉及的东西很多，还有一些比较高级的玩法，我们只需要了解一些常用的用法就行了，而那些复杂的case则可以在实践中，确实有需求了，再查阅文档去学习即可。  
  
### 测试代码  
```java  
package test;  
public class Test {  
    public static class TestBase {  
        static {  
            int x = 0;  
        }  
        int base = 0;  
        public TestBase(int index) {  
            base = index;  
        }  
    }  
    public static class TestDerived extends TestBase {  
        public int derived = 0;  
        public TestDerived() {  
            super(0);  
            this.derived = 1000;  
            //不要改变行号  
        }  
        public void testMethod() {  
            try {  
                byte[] test = null;  
                test[1] = 0x33;  
            } catch (Exception e) {  
            }  
        }  
        static int getFixedIndex() {  
            return 1000;  
        }  
    }  
    public static void main(String args[]) {  
        System.out.println("Test begin ...");  
        TestDerived derived = new TestDerived();  
        derived.testMethod();  
        derived.base = 1;  
        System.out.println("Test end ...");  
    }  
}  
```  
  
### 测试案例  
怎么把示例代码中调用println的Joinpoint选择出来呢？用到的pointcut格式为：  
```java  
public pointcut  testAll(): call(public * *.println(..)) && !within(TestAspect) ;  
```  
  
我们来看看上述代码  
- 最开始`public`的表示这个pointcut是public访问，这主要和aspect的`继承`关系有关，属于AspectJ的高级玩法  
- 后面的`pointcut`是`关键词`，表示这里定义的是一个pointcut  
- 后面的`testAll()`是pointcut的`名字`，在AspectJ中，定义pointcut可分为有名和匿名两种办法  
- 接下来是一个`冒号`，冒号后面是定义JPoint的`选择条件`的，支持`&&、||、!`三种逻辑组合操作符  
- 第一个选择条件：`call(public * *.println(..))`  
    - `call`表示我们选择的JPoint类型为call类型，它对应的目标JPoint一定是个函数，所以call后面括号内的作用就是找到这些函数  
    - 首先是匹配`访问类型`，public表示目标JPoint的访问类型必须是public的  
    - 然后是匹配`返回值类型`，使用通配符`*`表示目标JPoint的返回值类型可以是任意类型  
    - 然后是匹配`包名`，使用通配符`*`表示不限定目标JPoint的包名  
    - 然后是匹配`方法名`，这里表明我们选择的函数的名字叫println  
    - 最后是匹配`方法参数列表`，这里的`..`代表方法可以有任意个数、任意类型的参数  
- 第二个选择条件：`!within(TestAspectJ)`  
    - 操作符`!`表示不满足某个条件  
    - `within`是另外一种`类型选择`方法  
  
上例中的pointcut合起来就是：选择那些调用 println(不考虑println函数的参数)，并且调用者的类型不是 TestAspect 的 Joinpoint  
  
### 直接针对JPoint的选择  
pointcuts中最常用的`选择条件`和Joinpoint的`类型`密切相关，比如：  
  
![](https://img-blog.csdn.net/20151024194434462?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQv/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/Center)  
  
| JPoint类别  | pointcuts中的选择类型  | pointcuts中的目标函数  |  
| :------------: | :------------: | :------------: |  
| 方法执行 | **execution** | MethodSignature |  
| 方法调用 | **call** | MethodSignature |  
| 构造器执行 | execution | ConstructorSignature |  
| 构造器调用 | call | ConstructorSignature |  
| 类初始化 | staticinitialization | TypeSignature |  
| 属性读操作 | get | FieldSignature |  
| 属性写操作 | set | FieldSignature |  
| 例外处理执行 | handler | TypeSignature |  
| 对象初始化 | initialization | ConstructorSignature |  
| 对象预先初始化 | preinitialization | ConstructorSignature |  
| Advice执行 | adviceexecution |  
  
比如如果我们想选择`类型`为 Method execution 的JPoint，那么pointcuts就得使用 execution(XXX) 来限定。  
除了指定JPoint类型外，我们还要更进一步选择`目标函数`，选择的根据就是图中列出的什么`MethodSignature，ConstructorSignature，TypeSinature，FieldSignature`等。  
比如上面那个println例子，首先它的JPoint类型是call，所以它的查询条件是根据 MethodSignature 来表达。  
  
**MethodSignature**的完整表达式为：  
```java  
@注解 访问权限 返回值的类型 包名.函数名(参数)    
```  
- `@注解`和`访问权限`(public/private/protect/static/final)属于可选项，如果不设置它们，则默认都会选择  
- `返回值类型`就是普通的函数的返回值类型，如果不限定类型的话，就用通配符`*`表示    
- `包名.函数名`用于查找匹配的函数，可以使用通配符`*`(匹配除`.`号之外的任意字符)`..`(表示任意子包)和`+`(表示子类)  
    - `java.*.Date`可以表示java.sql.Date，也可以表示java.util.Date    
    - `Test*`可以表示TestBase，也可以表示TestDervied    
    - `java..*`表示java任意子类    
    - `java..*Model+`表示Java任意package中名字以Model结尾的子类，比如TabelModel，TreeModel    
- `函数的参数`主要是匹配参数类型  
    - `(int, char)`表示参数只有两个，并且第一个参数类型是int，第二个参数类型是char    
    - `(String, ..)`表示至少有一个参数，并且第一个参数类型是String，后面参数类型不限  
    - `..`代表任意参数个数和类型    
    - `(String ...)`表示不定个数的参数，且类型都是String，这里的`...`不是通配符，而是Java中代表不定参数的意思    
  
**ConstructorSignature**和MethodSignature类似，只不过构造函数没有返回值，而且函数名必须叫`new`：    
```java  
@注解 访问权限 返回值的类型 包名.new(参数)  
public *..TestDerived.new(..)  
```  
  
**FieldSignature**  
```java  
@注解 访问权限 成员变量类型 类名.成员变量名   
```  
  
**TypeSignature**  
```java  
staticinitialization(test..TestBase) //表示TestBase类的static block    
handler(NullPointerException) //表示catch到NullPointerException的JPoint。  
```  
> 注意，由于JPointer的查询匹配是`静态`的，即在编译过程中进行的匹配，所以对于案例代码中第23行的`catch (Exception e)`而言，使用`handler(NullPointerException)`在运行时并不能真正被截获，只有改成`handler(Exception)`，或者把源码第23行改成`catch (NullPointerException e)`才行。  
  
### 间接针对JPoint的选择  
除了根据前面提到的Signature信息来匹配JPoint外，AspectJ还提供其他一些选择方法来选择JPoint，比如某个类中的所有JPoint，每一个函数执行流程中所包含的JPoint。  
  
下表列出了一些常用的非JPoint选择方法：  
  
| 关键词 | 说明 | 示例 |  
| :------------: | :------------: | :------------: |  
| within(TypePattern) | 表示某个`Package或者类中`的所有JPoint，可以使用通配符 | 比如`within(Test)`表示Test类中(包括内部类)所有JPoint |  
| withincode(Method Signature) | 表示某个`函数执行过程中`涉及到的JPoint | 比如 `withinCode(* Test.test(..))` 表示test方法执行过程中涉及的Jpoint |  
| withincode(Constructor Signature) | 表示某个`构造函数执行过程中`涉及到的JPoint | 比如 `withinCode(*.Test.new(..))` 表示Test构造函数执行过程中涉及的Jpoint |  
| cflow(pointcuts) | cflow是 call flow 的意思，cflow的条件是一个pointcut | 比如，`cflow(call Test.test)`：表示调用 Test.test 函数时所包含的JPoint，包括test的call这个JPoint本身 |  
| cflowbelow(pointcuts) | 和cflow一样 | 和cflow一样，区别是不包括test的call这个JPoint本身 |  
| this(Type) | JPoint的this对象是Type类型，其实就是判断Type是不是某种类型，不能使用通配符 | JPoint从语法上说，它都属于一个类。如果这个类的类型是Type标示的类型，则和它相关的JPoint将全部被选中 |  
| target(Type) | 判断JPoint的target对象是不是Type类型，不能使用通配符 | target一般用在call的情况。call一个函数，这个函数可能定义在其他类，比如test方法是Test类定义的。那么target(Test)就会搜索到调用test方法的地方。不包括test的execution Jpoint |  
| args(TypeSignature) | 用来对JPoint的参数进行条件搜索 | 比如`args(int,..)`表示第一个参数是int，后面参数个数和类型不限的JPoint。 |  
  
注意，不是所有的AOP实现都支持本节所说的查询条件。比如Spring就不支持withincode查询条件。  
  
## advice 执行时机  
现在，我们知道如何通过 pointcuts 来选择合适的 JPoint，那么，下一步工作就很明确了，选择这些JPoint后，我们肯定是需要干一些事情的，比如前面例子中的输出都有before，after之类的，这其实JPoint在执行前，执行后，都执行了一些我们设置的代码。在AspectJ中，这段代码叫 advice。简单点说，`advice就是一种Hook`。  
  
ASpectJ中有好几个Hook，主要是根据JPoint执行时机的不同而不同。  
  
**AspectJ所支持的Advice的类型**  
- `before()`：表示在JPoint执行之前需要干的事情  
- `after()`：表示JPoint自己执行完了后需要干的事情。注意，after()默认包括returning和throwing两种情况  
- `after():returning(返回值类型)`和`after():throwing(异常类型)`：假设JPoint是一个函数调用的话，那么函数调用执行完有两种方式退出，一个是正常的return，另外一个是抛异常。  
- `返回值类型 around()`：before和after是指JPoint执行前或执行后备触发，而around就替代了原JPoint。around是替代了原JPoint，如果要执行原JPoint的话，需要调用`proceed`。  
  
> PS:  
returning和throwing后面都可以指定具体的类型，如果不指定的话则匹配的时候不限定类型。  
after和before没有返回值，但是around的目标是替代原JPoint的，所以它一般会有返回值，而且返回值的类型需要匹配被选中的JPoint。  
  
**案例**  
![](index_files/44e0a96a-4bf6-4288-91ab-82466714b9cd.png)  
  
图中：  
- 第一个红框的testMethod中，肯定会抛出一个空指针异常。  
- 第二个红框是我们配置的advice，除了before以外，还加了一个around，我们重点来看around，它的返回值是Object。虽然匹配的JPoint是testMethod，其定义的返回值是void，但是AspectJ考虑的很周到，在around里，可以设置返回值类型为Object来表示返回任意类型的返回值，AspectJ在真正返回参数的时候，会自动进行转换。比如，假设一个方法定义了int作为返回值类型，我们在around里可以返回一个Integer，AspectJ会自动转换成int作为返回值。  
- 再看around中的proceed()这句话。这代表调用真正的JPoint函数，即testMethod。由于这里我们屏蔽了proceed，所以testMethod真正的内容并未执行，故运行的时候空指针异常就不会抛出来。也就是说，我们完全截获了testMethod的运行，甚至可以任意修改它，让它执行别的函数都没有问题。  
  
注意：从技术上说，around是完全可以替代before和after的。上图中第二个红框还把after给注释掉了。如果不注释掉，编译时候报错：  
```java  
[error]circular advice precedence: can't determine precedence between two or morepieces of advice that apply to the same join point: method-execution(voidtest.Test$TestDerived.testMethod())  
```  
  
我猜测其中的原因是`around和after冲突了`。around本质上代表了目标JPoint，比如此处的testMethod。而after是testMethod之后执行，那么这个testMethod到底是around还是原testMethod呢？真是傻傻分不清楚！  
  
## 参数传递和 JPoint 信息  
前面介绍的advice都是没有参数信息的，而JPoint肯定是或多或少有参数的。而且advice既然是对JPoint的截获或者hook也好，肯定需要利用传入给JPoint的参数干点什么事情。比方所around advice，我可以对传入的参数进行检查，如果参数不合法，我就直接返回，根本就不需要调用proceed做处理。  
  
往advice传参数比较简单，就是利用前面提到的`this(),target(),args()`等方法，具体步骤如下：  
  
1、在pointcuts定义时候指定参数类型和名字，并在this、target或args中绑定参数名  
```java  
pointcut testAll(Test.TestDerived derived,int x): //在testAll中定义参数类型和参数名  
    call(*Test.TestDerived.testMethod(..))    
    && target(derived) //此处的target和args括号中用得是参数名；注意，不再是参数类型，而是参数名  
    && args(x)    
```  
  
注意，增加参数并不会影响pointcuts对JPoint的匹配，下面的pointcuts选择和上面是一样的  
```java  
pointcut testAll():  
    call(*Test.TestDerived.testMethod(..))   
    && target(Test.TestDerived)   
    && args(int)  
```  
  
2、修改advice，在冒号后面的pointcuts中绑定参数名，并在advice的代码中使用参数名：  
```java  
Object around(Test.TestDerived derived,int x):testAll(derived,x){    
     System.out.println("   arg1=" + derived); //在advice的代码中使用参数名  
     System.out.println("   arg2=" + x);    
     return proceed(derived,x); //注意，proceed必须把所有参数传进去。    
}    
```  
  
advice的定义现在也和函数定义一样，把参数类型和参数名传进来。  
接着把参数名传给pointcuts，此处是testAll。注意，advice必须和使用的pointcuts在参数类型和名字上保持一致。  
然后在advice的代码中，你就可以引用参数了，比如derived和x，都可以打印出来。  
  
## JoinPoint 信息收集  
我们前面示例中都打印出了JPoint的信息，比如当前调用的是哪个函数，JPoint位于哪一行代码，这些都属于JPoint的信息。AspectJ为我们提供如下信息：  
- thisJoinpoint对象：在advice代码中可直接使用，代表JPoint每次被触发时的一些动态信息，比如参数啊之类的  
- thisJoinpointStatic对象：在advice代码中可直接使用，代表JPoint中那些不变的东西，比如这个JPoint的类型，JPoint所处的代码位置等  
- thisEnclosingJoinPointStaticPart对象：在advice代码中可直接使用，也代表JPoint中不可变的部分，但是它包含的东西和JPoint的类型有关，比如对一个call类型JPoint而言，它代表包含调用这个JPoint的函数的信息；对一个handler类型的JPoint而言，它代表包含这个try/catch的函数的信息。  
  
关于thisJoinpoint，建议大家直接查看API文档，非常简单。  
  
## 总结  
- AspectJ中存在各种类型的JoinPoint，JPoint是一个程序的关键执行点，也是我们关注的重点。  
- pointcuts：提供了一种方法来选择目标JPoint。程序有很多JPoint，但是需要一种方法来让我们选择我们关注的JPoint，这个方法就是利用pointcuts来完成的。  
- 通过pointcuts选择了目标JPoint后，我们总得干点什么吧？这就用上了advice。advice包括好几种类型，一般情况下都够我们用了。  
  
上面这些东西都有点像函数定义，在Java中，这些东西都是要放到一个class里的，在AspectJ中，也有类似的数据结构，叫aspect。  
```java  
public aspect 名字 {//aspect关键字和class的功能一样，文件名以.aj结尾  
    pointcuts定义...  
    advice定义...  
}  
```  
  
你看，通过这种方式，定义一个aspect类，就把相关的JPoint和advice包含起来，是不是形成了一个`关注面`？比如：  
- 我们定义一个LogAspect，在LogAspect中，我们在关键JPoint上设置advice，这些advice就是打印日志  
- 再定义一个SecurityCheckAspect，在这个Aspect中，我们在关键JPoint上设置advice，这些advice将检查调用app是否有权限。  
  
通过这种方式，我们在原来的JPoint中，就不需要写log打印的代码，也不需要写权限检查的代码了，所有这些关注点都挪到对应的Aspectj文件中来控制。恩，这就是AOP的精髓。  
  
# 使用AOP的例子  
现在正式回到我们的AndroidAopDemo这个例子来，我们的目标是为AopDemoActivity的几个Activity生命周期函数加上log，另外为checkPhoneState加上权限检查，一切都用AOP来集中控制。  
  
前面提到说AspectJ需要编写`aj`文件，然后把AOP代码放到aj文件中，但是在Android开发中，我建议不要使用aj文件，因为aj文件只有AspectJ编译器才认识，而`Android编译器不认识这种文件`，所以当更新了aj文件后，编译器认为源码没有发生变化，所以不会编译它。  
  
当然，这种问题在其他不认识aj文件的java编译环境中也存在。所以，AspectJ提供了一种基于`注解`的方法来把AOP实现到一个普通的Java文件中，这样我们就把AOP当做一个普通的Java文件来编写、编译就好。  
  
## 打印Log  
```java  
import org.aspectj.lang.JoinPoint;  
import org.aspectj.lang.annotation.Aspect;  
import org.aspectj.lang.annotation.Before;  
import org.aspectj.lang.annotation.Pointcut;  
  
@Aspect   //必须使用@AspectJ标注，这样class DemoAspect就等同于 aspect TestAspect了  
public class TestAspect {  
      
    //此处的logForActivity()其实它代表了这个pointcut的名字。  
    //如果要带参数，则把参数类型和名字放到代表pointcut名字的logForActivity中，然后在@Pointcut注解中使用参数名。  
    //注意，这个函数必须要有实现，否则Java编译器会报错。基本和以前一样，只是写起来比较奇特一点。  
    @Pointcut("execution(* com.androidaop.demo.AopDemoActivity.onCreate(..)) ||"  
        + "execution(* com.androidaop.demo.AopDemoActivity.onStart(..))")  
    public void logForActivity() {  
    }  
      
    //@Before：这就是Before的advice，其他几个注解为@After，@AfterReturning，@AfterThrowing，@Around  
    //Before后面跟的是pointcut名字，然后其代码块由一个函数来实现。比如此处的log。  
    @Before("logForActivity()")  
    public void log(JoinPoint joinPoint) {  
        //对于使用Annotation的AspectJ而言，JoinPoint就不能直接在代码里得到了，而需要通过参数传递进来。  
        Log.e("bqt", joinPoint.toShortString());  
    }  
}  
```  
  
上面的例子仅仅是列出了onCreate和onStart两个函数的日志，如果想在所有的`onXXX`这样的函数里加上log，该怎么改呢？  
```java  
@Pointcut("execution(* *..AopDemoActivity.on*(..))")  
public void logForActivity(){};  
```  
  
## 检查权限  
检查权限这个功能的实现也可以采用刚才打印log那样，但是这样就没有太多意思了，我们玩点高级的，不过这个高级的玩法也是来源于现实需求：  
- 权限检查一般是针对API的，比如调用者是否有权限调用某个函数。  
- API往往是通过SDK发布的。一般而言，我们会在这个函数的注释里说明需要调用者声明哪些权限。  
- 然后我们在API检查调用者是不是申明了文档中列出的权限。  
  
如果我有10个API，10个不同的权限，那么在10个函数的注释里都要写，太麻烦了。怎么办？这个时候我想到了注解。注解的本质是源代码的描述。权限声明，从语义上来说，其实是属于API定义的一部分，二者是一个统一体，而不是分离的。  
  
Java提供了一些默认的注解，不过此处我们要使用自己定义的注解：  
```java  
@Target(ElementType.METHOD) //@Target表示这个注解只能给函数使用  
@Retention(RetentionPolicy.RUNTIME) //@Retention表示注解内容需要包含的Class字节码里，属于运行时需要的  
public @interface SecurityCheckAnnotation { //@interface用于定义一个注解。  
    String declaredPermission();  //declarePermssion是一个函数，其实代表了注解里的参数  
}  
```  
  
怎么使用注解呢？接着看代码：    
```java  
//为checkPhoneState使用SecurityCheckAnnotation注解，并指明调用该函数的人需要声明的权限  
@SecurityCheckAnnotation(declaredPermission = "android.permission.READ_PHONE_STATE")  
private void checkPhoneState() {  
    //如果不使用AOP，就得自己来检查权限  
}  
```  
  
下面，我们来看看如何在AspectJ中，充分利用这注解信息来帮助我们检查权限。  
- 首先，它在选择Jpoint的时候，把`@SecurityCheckAnnotation`使用上了，这表明所有那些public的、并且携带有这个注解的API都是目标JPoint。  
- 接着，由于我们希望`在函数中获取注解的信息`，所有这里的poincut函数有一个参数，参数类型是 SecurityCheckAnnotation，参数名为ann ，这个参数我们需要在后面的`advice`里用上，所以pointcut还使用了`@annotation(ann)`这种方法来告诉 AspectJ，这个ann是一个注解。  
- 接下来是advice，advice的真正功能由check函数来实现，这个check函数第二个参数就是我们想要的注解。在实际运行过程中，AspectJ会把这个信息从JPoint中提出出来并传递给check函数。  
  
```java  
@Aspect  
public class TestAspect {  
    @Pointcut("execution(@SecurityCheckAnnotation public * *..*.*(..)) && @annotation(ann)")  
    public void checkPermssion(SecurityCheckAnnotation ann) {  
    }  
      
    @Before("checkPermssion(securityCheckAnnotation)")  
    public void check(JoinPoint joinPoint, SecurityCheckAnnotation ann) {  
        String neededPermission = ann.declaredPermission();//从注解信息中获取声明的权限。  
        Log.e("bqt", joinPoint.toShortString());  
        Log.e("bqt", "needed permission is " + neededPermission);  
    }  
}  
```  
  
如此这般，我们在API源码中使用的`注解信息`，现在就可以在AspectJ中使用了。这样，我们在源码中定义注释，然后利用AspectJ来检查。  
  
刚才权限检查只是简单得打出了日志，但是并没有真正去做权限检查。如何处理？这就涉及到AOP如何与一个程序中其他模块交互的问题了。以此例的权限检查为例，我们需要：  
- 把真正进行权限检查的地方封装到一个模块里，比如SecurityCheck中。  
- SecurityCheck往往在一个程序中只会有一个实例，所以可以为它提供一个函数，比如getInstance以获取SecurityCheck实例对象。  
- 然后我们就可以在DemoAspect中获取这个对象，然后调用它的check函数，把最终的工作由SecurityCheck来检查了。  
  
恩，这其实是Aspect的真正作用：`它负责收集Jpoint，设置advice。一些简单的功能可在Aspect中来完成，而一些复杂的功能，则只是有Aspect来统一收集信息，并交给专业模块来处理`。  
  
# 其他  
## 如何使用 sjc 编译源文件  
- 下载 aspectj 的安装包，比如 [aspectj-1.9.1.jar](http://mirrors.ustc.edu.cn/eclipse/tools/aspectj/aspectj-1.9.1.jar)   
- 执行命令`java -jar aspectj-1.9.1.jar`或双击后选择通过JAVA打开，然后就会弹出安装 aspectj 软件的界面  
- 选择JRE的路径，选择安装位置，安装完后，会提示你配置 `CLASSPATH` 和 `PATH` 环境：  
  
> The automatic installation process is complete. We recommend you complete the installation as follows:  
- Add 【C:\Android\aspectj1.9\lib\aspectjrt.jar】 to your `CLASSPATH`. This small `.jar` file contains classes required by any program compiled with the `ajc` compiler.  
- Modify your `PATH` to include 【C:\Android\aspectj1.9\bin】. This will make it easier to run `ajc` and `ajbrowser`.  
- These steps are described in more detail in 【C:\Android\aspectj1.9\README-AspectJ.html】.  
  
- 配置完可以测试一下环境是否配置成功，比如编译工具ajc：  
```c  
ajc -version  
AspectJ Compiler 1.9.1 (1.9.1 - Built: Friday Apr 20, 2018 at 16:47:33 GMT) - Eclipse Compiler #abe06abe4ce1(Apr2018), 3.14  
```  
  
- 使用 `ajc` 编译Java源文件：`ajc Test.java`，和java命令类似，编译完成后就生成了相应的class字节码文件。  
  
## 在 Android 中编译  
AspectJ比较强大，除了支持对`source文件`(即aj文件、或`@AspectJ`注解的Java文件，或普通java文件)直接进行编译外，还能对Java`字节码文件`(即class文件)进行处理。有感兴趣的同学可以对aspectj编译过的class文件进行反编译，你会发现AspectJ无非是在被选中的JPoint的地方加一些`hook函数`，例如Before就是在调用JPoint之前加，After就是在JPoint返回之前加。而AspectJ更高级的做法是当class文件被加载到虚拟机后，`由虚拟机根据AOP的规则进行hook`。  
  
在Android里边，我们用的是第二种方法，即对`class文件`进行处理。来看看代码：  
```java  
//AOP需要执行的脚本，每一个application和library都需要  
import org.aspectj.bridge.MessageHandler  
import org.aspectj.tools.ajc.Main  
def aop(variants) {  
    variants.all { variant ->  
        JavaCompile javaCompile = variant.javaCompile  
        String[] args;  
        javaCompile.doFirst {  
            args = ["-showWeaveInfo",  
                    "-1.8", //1.8是为了兼容java 8。请根据自己java的版本合理设置它  
                    "-inpath", javaCompile.destinationDir.toString(),  
                    "-aspectpath", javaCompile.classpath.asPath,  
                    "-d", javaCompile.destinationDir.toString(),  
                    "-classpath", path,  
                    "-bootclasspath", project.android.bootClasspath.join(File.pathSeparator)]  
        }  
  
  
        javaCompile.doLast { //对上一步生成的class文件进行aspectj处理(参数args不能改)  
            new Main().run(args, new MessageHandler(false));  
        }  
    }  
}  
```  
  
主要利用了 [ajc-ref](https://eclipse.org/aspectj/doc/released/devguide/ajc-ref.html) 中 The AspectJ compiler API 一节的内容。  
  
## 总结  
除了hook之外，AspectJ还可以`为目标类添加变量`。另外，AspectJ也有抽象，继承等各种更高级的玩法，AspectJ肯定对原程序是有影响的，如若贸然使用高级用法，则可能带来一些未知的后果。  
  
最后再来看一个图:  
![](index_files/d131eb2a-53fc-48cb-bed1-d0ad4f616c20.jpg)  
  
图中，左边是一个程序的三个基于OOP而划分的模块：安全、业务逻辑、交易管理。这三个模块在设计图上一定是互相独立，互不干扰的。  
  
但是在右图实现的时候，`这三个模块就搅在一起了`，这和我们在AndroidAopDemo中检查权限的例子中完全一样，在业务逻辑的时候，需要显示调用安全检查模块。  
  
自从有了AOP，我们就可以去掉业务逻辑中显示调用安全检查的内容，使得代码归于干净，各个模块又能各司其职。而这之中千丝万缕的联系，都由AOP来连接和管理，岂不美哉？！  
  
2018-4-27  
