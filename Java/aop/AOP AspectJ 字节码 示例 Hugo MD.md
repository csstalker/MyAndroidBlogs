| Markdown版本笔记 | 我的GitHub首页 | 我的博客 | 我的微信 | 我的邮箱 |  
| :------------: | :------------: | :------------: | :------------: | :------------: |  
| [MyAndroidBlogs][Markdown] | [baiqiantao][GitHub] | [baiqiantao][博客] | bqt20094 | baiqiantao@sina.com |  
  
[Markdown]:https://github.com/baiqiantao/MyAndroidBlogs  
[GitHub]:https://github.com/baiqiantao  
[博客]:http://www.cnblogs.com/baiqiantao/  
  
AOP AspectJ 简介 示例 Hugo MD  
***  
目录  
===  

- [AspectJ 简介](#aspectj-简介)
- [沪江封装的库集成配置](#沪江封装的库集成配置)
	- [简介](#简介)
	- [配置](#配置)
- [官方库集成配置](#官方库集成配置)
- [AspectJ 使用案例](#aspectj-使用案例)
	- [定义拦截点击事件的逻辑](#定义拦截点击事件的逻辑)
	- [定义拦截自定义注解的逻辑](#定义拦截自定义注解的逻辑)
	- [工具类 ObjToStringUtils](#工具类-objtostringutils)
- [Pointcut 语法格式](#pointcut-语法格式)
- [常用的 Pointcut 案例](#常用的-pointcut-案例)
- [Hugo 简介](#hugo-简介)
	- [简介](#简介)
	- [使用步骤](#使用步骤)
	- [实现原理简介](#实现原理简介)
  
# AspectJ 简介  
[AspectJ 官网](https://eclipse.org/aspectj/)  
[AspectJ 官方详细文档](https://www.eclipse.org/aspectj/doc/next/progguide/index.html)    
[AspectJ Development Environment Guide](https://eclipse.org/aspectj/doc/released/devguide/index.html)  
  
[语法大全1](http://www.eclipse.org/aspectj/doc/released/quick5.pdf)  
[语法大全2](http://www.eclipse.org/aspectj/doc/released/progguide/semantics.html)  
  
[AspectJ 类库参考文档](http://www.eclipse.org/aspectj/doc/released/runtime-api/index.html)  
[AspectJ 文档](http://www.eclipse.org/aspectj/doc/released/aspectj5rt-api/index.html)  
  
> 相关词汇  
Aspect [ˈæspekt] n.方面; 面貌; 方位，方向; 形势;  
oriented ['ɔ:rɪəntɪd] adj.导向的; 定向的; 以…为方向的; 定方向;  
OOP：Object Oriented Programming，面向对象编程  
AOP：Aspect Oriented Programming，面向切向编程  
  
AspectJ 意思就是 Java 的 Aspect。它就是一个`代码编译器`，在`Java编译器`的基础上增加了一些它自己的关键字识别和编译方法。  
  
AspectJ编译器在`编译期`对所切点所在的目标类进行了重构，在编译层将AspectJ程序与目标程序进行双向关联，`生成新的目标字节码`，即将AspectJ的切点和其余辅助的信息类段插入目标方法和目标类中，同时也传回了目标类以及其实例引用，这样便能够在AspectJ程序里对目标程序进行监听甚至操控。  
  
AspectJ 的优点  
- 非侵入式监控： 可以在不修监控目标的情况下监控其运行，截获某类方法，甚至可以修改其参数和运行轨迹！   
- 学习成本低： 它就是Java，只要会Java就可以用它。   
- 功能强大，可拓展性高： 它就是一个编译器+一个库，可以让开发者最大限度的发挥，实现形形色色的AOP程序！  
  
  
  
  
  
  
  
  
# 沪江封装的库集成配置  
[AspectJX](https://github.com/HujiangTechnology/gradle_plugin_android_aspectjx)  
[使用 AspectJX 实现的动态权限配置框架](https://github.com/firefly1126/android_permission_aspectjx)  
  
## 简介  
一个基于AspectJ并在此基础上扩展出来可应用于Android开发平台的AOP框架，可作用于`java源码`，`class文件`及`jar包`，同时支持`kotlin`的应用。  
> A Android gradle plugin that effects AspectJ on Android project and can hook methods in `Kotlin`, `aar` and `jar` file.  
  
**开发**AspectJX**的初衷**  
- 目前的开源库中还没有发现可应用于Android平台的比较好的AOP框架或者工具，虽然 [xposed](https://github.com/rovo89/Xposed)，[dexposed](https://github.com/alibaba/dexposed) 非常强大，但基于严重的碎片化现状，`兼容问题`永远是一座无法逾越的大山。  
- 目前其他的`AspectJ`相关插件和框架都`不支持AAR或者JAR切入`的，对于目前在Android圈很火爆的`Kotlin`更加无能为力。  
  
## 配置  
1、在项目根目录的`build.gradle`里依赖`AspectJX`和官方`AspectJ`  
```groovy  
classpath 'com.hujiang.aspectjx:gradle-android-plugin-aspectjx:2.0.4'  
classpath 'org.aspectj:aspectjtools:1.8.13'  
```  
  
2、在`app`项目的`build.gradle`里应用插件  
```groovy  
apply plugin: 'android-aspectjx' //或者apply plugin: 'com.hujiang.android-aspectjx'  
aspectjx {  
    enabled true //enabled默认为true，即默认AspectJX生效  
    exclude 'android.support' //排除所有package路径中包含`android.support`的class文件及jar文件  
}  
```  
  
> AspectJX默认会处理所有的二进制代码文件和库，为了提升编译效率及规避部分第三方库出现的编译兼容性问题，AspectJX提供`include`、`exclude`命令来过滤需要处理的文件及排除某些文件(包括class文件及jar文件)。  
  
> 注意，匹配时时通过`package`路径来匹配class文件及jar文件的，支持`*`和`**`匹配，例如：  
`exclude '*'`//忽略所有的class文件及jar文件，相当于`AspectJX`不生效  
  
3、如果`AspectJ`的代码写在某个library模块中，则必须添加如下配置：  
3.1、在使用`AspectJ`的模块中添加依赖(不必在意版本)  
```groovy  
implementation 'org.aspectj:aspectjrt:1.8.13'  
```  
  
3.2、让app模块依赖此module，这一步是必须要做的，否则运行时立即崩溃  
```groovy  
implementation project(path: ':log')  
```  
  
# 官方库集成配置  
[AspectJ Weaver 仓库地址](https://mvnrepository.com/artifact/org.aspectj/aspectjweaver)  
[AspectJ Tools 仓库地址](https://mvnrepository.com/artifact/org.aspectj/aspectjtools)  
[AspectJ 官网下载](http://www.eclipse.org/aspectj/downloads.php)  
  
**已知问题**  
- 不支持kotlin  
- 不能拦截jar包中的类  
- 拦截规则不能写在jar包中  
- 需要在每一个module都配置脚本  
  
**配置**  
1、在【project】的`build.gradle`中配置：  
```groovy  
buildscript {  
    repositories {  
        mavenCentral()  
    }  
    dependencies {  
        classpath 'org.aspectj:aspectjtools:1.8.9'  
        classpath 'org.aspectj:aspectjweaver:1.8.9'  
    }  
}  
```  
  
2、在【project】的`build.gradle`最后面添加脚本：  
注意，直接粘贴到build.gradle文件的末尾即可，不要嵌套在别的指令中  
```groovy  
//AOP需要执行的脚本，每一个application和library都需要  
import org.aspectj.bridge.MessageHandler  
import org.aspectj.tools.ajc.Main  
  
def aop(variants) {  
    variants.all { variant ->  
        JavaCompile javaCompile = variant.javaCompile  
        String[] args;  
        javaCompile.doFirst {  
            args = ["-showWeaveInfo",  
                    "-1.8",  
                    "-inpath", javaCompile.destinationDir.toString(),  
                    "-aspectpath", javaCompile.classpath.asPath,  
                    "-d", javaCompile.destinationDir.toString(),  
                    "-classpath", path,  
                    "-bootclasspath", project.android.bootClasspath.join(File.pathSeparator)]  
        }  
  
        javaCompile.doLast {  
            new Main().run(args, new MessageHandler(false));  
        }  
    }  
}  
```  
  
3、在【module】的`build.gradle`中添加依赖：  
注意，只在基础module中使用api方式依赖即可  
```groovy  
api 'org.aspectj:aspectjrt:1.8.9'//只在基础module中使用api方式依赖  
```  
  
4、在【module】的`build.gradle`中添加编译脚本：  
注意，直接粘贴到build.gradle文件的开始位置或末尾即可，不要嵌套在别的指令中  
如果是【application】  
```groovy  
rootProject.aop(project.android.applicationVariants)  
```  
如果是【library】  
```groovy  
rootProject.aop(project.android.libraryVariants)  
```  
  
# AspectJ 使用案例  
[测试案例](https://github.com/baiqiantao/Test)  
  
## 定义拦截点击事件的逻辑  
拦截所有View或其子类的【onClick方法】以及通过ButterKnife的注解添加的点击事件  
```java  
@Aspect  
public class OnClickAspect {  
    @Pointcut("execution(* android.view.View.On*Listener.on*Click(..)) ")//定义匹配范围：执行指定方法时拦截  
    public void onClick() {//匹配View.OnClickListener中的onClick方法和View.OnLongClickListener中的OnLongClickListener方法  
    }  
      
    @Pointcut("execution(* *.on*ItemClick(..)) ")//如果有多个匹配范围，可以定义多个，多个规则之间通过 || 或 && 控制  
    public void onItemClick() {//匹配任意名字以on开头以ItemClick结尾的方法  
    }  
      
    @Pointcut("execution(@butterknife.OnClick * *(..))")//匹配通过butterknife的OnClick注解添加的点击事件  
    public void butterKnifeOnClick() {  
    }  
      
    @Around("onClick() || onItemClick() || butterKnifeOnClick()")//@Around 拦截方法，这个注解可以同时拦截方法的执行前后  
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {  
        long beginTime = SystemClock.currentThreadTimeMillis();  
        printJoinPointInfo(joinPoint);  
          
        if (joinPoint.getSignature() instanceof MethodSignature) {  
            MethodSignature signature = (MethodSignature) joinPoint.getSignature();//要根据Pointcut匹配的类型强转  
            printMethodSignatureInfo(signature);  
            printArgs(joinPoint);  
            printParameterInfo(joinPoint);  
        }  
          
        Object result = joinPoint.proceed();  
        Log.i("bqt", "【@Around】返回值=" + ObjToStringUtils.toString(result)// null  
                + "  方法执行耗时=" + (SystemClock.currentThreadTimeMillis() - beginTime));//2  
        return result;  
    }  
      
    //必须是静态方法  
    private static void printJoinPointInfo(ProceedingJoinPoint joinPoint) {  
        Log.i("bqt", "【@Around】MethodSignature"  
                + "\nKind=" + joinPoint.getKind()//method-execution  
                + "\nArgs=" + ObjToStringUtils.toString(joinPoint.getArgs())//[android.widget.TextView{d090a1d V.ED..C.. ...P.... 0,0-1440,210}]  
                + "\nSignature=" + ObjToStringUtils.toString(joinPoint.getSignature())//void com.bqt.aop.MainActivity.1.onClick(View)  
                + "\nSourceLocation=" + ObjToStringUtils.toString(joinPoint.getSourceLocation())//MainActivity.java:25  
                + "\nStaticPart=" + ObjToStringUtils.toString(joinPoint.getStaticPart())//execution(void com.bqt.aop.MainActivity.1.onClick(View))  
                + "\nTarget=" + ObjToStringUtils.toString(joinPoint.getTarget())//com.bqt.aop.MainActivity$1@d5c5492  
                + "\nThis=" + ObjToStringUtils.toString(joinPoint.getThis()));//com.bqt.aop.MainActivity$1@d5c5492  
    }  
      
    private static void printMethodSignatureInfo(MethodSignature signature) {  
        //下面通过MethodSignature的方式获取方法的详细信息，也基本都可以通过Method对象获取  
        Log.i("bqt", "【@Around】MethodSignature"  
                + "\n方法=" + ObjToStringUtils.toString(signature.getMethod())// public void com.bqt.aop.MainActivity$1.onClick(android.view.View)  
                + "\n方法名=" + signature.getName()// onClick  
                + "\n返回值类型=" + ObjToStringUtils.toString(signature.getReturnType())// void  
                + "\n声明类型=" + ObjToStringUtils.toString(signature.getDeclaringType())// class com.bqt.aop.MainActivity$1  
                + "\n声明类型名=" + signature.getDeclaringTypeName()// com.bqt.aop.MainActivity$1  
                + "\n异常类型=" + ObjToStringUtils.toString(signature.getExceptionTypes())// []  
                + "\n修饰符=" + signature.getModifiers()// 1，对应为 public static final int PUBLIC  = 0x00000001  
                + "\n参数名=" + ObjToStringUtils.toString(signature.getParameterNames())// ["v"]  
                + "\n参数类型=" + ObjToStringUtils.toString(signature.getParameterTypes()));// [class android.view.View]  
    }  
      
    private static void printArgs(ProceedingJoinPoint joinPoint) {  
        String[] parameterNames = ((MethodSignature) joinPoint.getSignature()).getParameterNames();//获取参数名列表  
        Object[] parameterValues = joinPoint.getArgs();//获取参数值列表  
          
        StringBuilder builder = new StringBuilder("");  
        for (int i = 0; i < parameterValues.length; i++) {  
            builder.append("\n")  
                    .append(parameterNames[i])  
                    .append("=")//拼接参数名  
                    .append(ObjToStringUtils.toString(parameterValues[i]));//拼接参数值  
        }  
        Log.i("bqt", "【@Around】参数列表" + builder.toString());//v=android.widget.TextView{d090a1d V.ED..C.. ...P.... 0,0-1440,210}  
    }  
      
    private static void printParameterInfo(ProceedingJoinPoint joinPoint) {  
        Object[] parameterValues = joinPoint.getArgs();//获取参数值列表  
        for (Object obj : parameterValues) {  
            if (obj instanceof TextView) {  
                TextView textView = (TextView) obj;  
                Log.i("bqt", "【@Around】TextView的信息"  
                        + "  文字=" + textView.getText()  
                        + "  所属界面=" + textView.getContext().getClass().getSimpleName()  
                        + "  ID=" + textView.getId()  
                        + "  父页面名称=" + textView.getParent().getClass().getSimpleName()  
                );  
            }  
        }  
    }  
}  
```  
  
## 定义拦截自定义注解的逻辑  
定义注解  
```java  
@Target({METHOD, CONSTRUCTOR})  
@Retention(RetentionPolicy.RUNTIME)  
public @interface CustomEvent {  
    String value();  
}  
```  
  
定义拦截规则(注意要更改为正确的包名)  
```java  
@Aspect  
public class CustomEventAspect {  
    @Pointcut("within(@com.icitic.basic.log.aop.CustomEvent *)")  
    public void withinAnnotatedClass() {  
    }  
      
    @Pointcut("execution(* *(..)) && withinAnnotatedClass()")  
    public void methodInsideAnnotatedType() {  
    }  
      
    @Pointcut("execution(*.new(..)) && withinAnnotatedClass()")  
    public void constructorInsideAnnotatedType() {  
    }  
      
    @Pointcut("execution(@com.icitic.basic.log.aop.CustomEvent * *(..)) || methodInsideAnnotatedType()")  
    public void method() {  
    }  
      
    @Pointcut("execution(@com.icitic.basic.log.aop.CustomEvent *.new(..)) || constructorInsideAnnotatedType()")  
    public void constructor() {  
    }  
      
    @Before("method() || constructor()")  
    public void before(JoinPoint joinPoint) {  
        SourceLocation location = joinPoint.getSourceLocation();  
        Log.i("bqt", "【自定义事件" + "(" + location.getFileName() + ":" + location.getLine() + ")");  
    }  
      
    @After("method() || constructor()")  
    public void after(JoinPoint joinPoint) {  
        //before、after不能和around同时使用  
    }  
}  
```  
  
## 工具类 ObjToStringUtils  
```java  
public class ObjToStringUtils {  
  
    public static String toString(Object obj) {  
        if (obj == null) return "null";  
        if (obj instanceof CharSequence) return '"' + printableToString(obj.toString()) + '"';  
          
        Class<?> cls = obj.getClass();  
        if (Byte.class == cls) return byteToString((Byte) obj);  
        if (cls.isArray()) return arrayToString(cls.getComponentType(), obj);  
          
        return obj.toString();  
    }  
      
    private static String printableToString(String string) {  
        int length = string.length();  
        StringBuilder builder = new StringBuilder(length);  
        for (int i = 0; i < length; ) {  
            int codePoint = string.codePointAt(i);  
            switch (Character.getType(codePoint)) {  
                case Character.CONTROL:  
                case Character.FORMAT:  
                case Character.PRIVATE_USE:  
                case Character.SURROGATE:  
                case Character.UNASSIGNED:  
                    switch (codePoint) {  
                        case '\n':  
                            builder.append("\\n");  
                            break;  
                        case '\r':  
                            builder.append("\\r");  
                            break;  
                        case '\t':  
                            builder.append("\\t");  
                            break;  
                        case '\f':  
                            builder.append("\\f");  
                            break;  
                        case '\b':  
                            builder.append("\\b");  
                            break;  
                        default:  
                            builder.append("\\u").append(String.format("%04x", codePoint).toUpperCase(Locale.US));  
                            break;  
                    }  
                    break;  
                default:  
                    builder.append(Character.toChars(codePoint));  
                    break;  
            }  
            i += Character.charCount(codePoint);  
        }  
        return builder.toString();  
    }  
      
    private static String arrayToString(Class<?> cls, Object obj) {  
        if (byte.class == cls) return byteArrayToString((byte[]) obj);  
        if (short.class == cls) return Arrays.toString((short[]) obj);  
        if (char.class == cls) return Arrays.toString((char[]) obj);  
        if (int.class == cls) return Arrays.toString((int[]) obj);  
        if (long.class == cls) return Arrays.toString((long[]) obj);  
        if (float.class == cls) return Arrays.toString((float[]) obj);  
        if (double.class == cls) return Arrays.toString((double[]) obj);  
        if (boolean.class == cls) return Arrays.toString((boolean[]) obj);  
        return arrayToString((Object[]) obj);  
    }  
      
    private static String byteArrayToString(byte[] bytes) {  
        StringBuilder builder = new StringBuilder("[");  
        for (int i = 0; i < bytes.length; i++) {  
            if (i > 0) builder.append(", ");  
            builder.append(byteToString(bytes[i]));  
        }  
        return builder.append(']').toString();  
    }  
      
    private static String byteToString(Byte b) {  
        if (b == null) return "null";  
        return "0x" + String.format("%02x", b).toUpperCase(Locale.US);  
    }  
      
    private static String arrayToString(Object[] array) {  
        StringBuilder buf = new StringBuilder();  
        arrayToString(array, buf, new HashSet<Object[]>());  
        return buf.toString();  
    }  
      
    private static void arrayToString(Object[] array, StringBuilder builder, Set<Object[]> seen) {  
        if (array == null) {  
            builder.append("null");  
            return;  
        }  
          
        seen.add(array);  
        builder.append('[');  
        for (int i = 0; i < array.length; i++) {  
            if (i > 0) builder.append(", ");  
              
            Object element = array[i];  
            if (element == null) {  
                builder.append("null");  
            } else {  
                Class elementClass = element.getClass();  
                if (elementClass.isArray() && elementClass.getComponentType() == Object.class) {  
                    Object[] arrayElement = (Object[]) element;  
                    if (seen.contains(arrayElement)) builder.append("[...]");  
                    else arrayToString(arrayElement, builder, seen);  
                } else {  
                    builder.append(toString(element));  
                }  
            }  
        }  
        builder.append(']');  
        seen.remove(array);  
    }  
}  
```  
  
# Pointcut 语法格式  
Pointcut可以由下列方式来定义或者通过 `&& ||  !` 的方式进行组合。  
```java  
args()  
@args()  
execution()  
this()  
target()  
@target()  
within()  
@within()  
@annotation  
```  
  
其中 execution 是用的最多的，其格式为：  
```java  
execution(modifiers-pattern? ret-type-pattern declaring-type-pattern? name-pattern(param-pattern) throws-pattern?)  
execution(修饰符? 返回值的类型 所属的类? 方法名(参数列表) 抛出的异常?)  
```  
  
后面带 `?` 的表示可以省略，`returning type pattern` 和 `name pattern` 和 `parameters pattern` 是必须的。  
- modifiers-pattern：指定方法的修饰符，支持通配符，该部分可以省略  
- ret-type-pattern：指定方法的返回值类型，支持通配符，可以使用 `*` 通配符来匹配所有的返回值类型  
- declaring-type-pattern：指定方法所属的类，支持通配符，该部分可以省略  
- name-pattern：指定匹配指定的方法名，支持通配符，可以使用 `*` 通配符来匹配所有方法，也可以用 `set*` 匹配以 set 开头的所有方法  
- param-pattern：指定方法声明中的形参列表，支持两个通配符，其中 `*` 代表一个任意类型的参数，而 `..` 代表零个或多个任意类型的参数。  
```java  
() 匹配一个不接受任何参数的方法  
(..) 匹配一个接受任意数量参数的方法  
(*) 匹配了一个接受一个任何类型的参数的方法  
(*,String) 匹配了一个接受两个参数的方法，其中第一个参数是任意类型，第二个参数必须是String类型  
```  
- throws-pattern：指定方法声明抛出的异常，支持通配符，该部分可以省略  
  
通配符  
```java  
* 表示任何数量的字符，除了(.)   
.. 表示任何数量的字符包括任何数量的(.)   
+ 描述指定类型的任何子类或者子接口  
```  
  
操作符，同java一样，提供了一元和二元的条件表达操作符：  
```java  
一元操作符：!  
二元操作符：||和&&  
优先权同java  
```  
  
# 常用的 Pointcut 案例  
类和包  
- 任意公共方法的执行：`execution(public * *(..))`  
- 任何一个以 set 开始的方法的执行：`execution(* set*(..))`  
- 接口或类 com.a.b.Demo 中的任意方法的执行：`execution(* com.a.b.Demo.*(..))`  
- 定义在 com.a.b 包里的任意方法的执行：`execution(* com.a.b.*.*(..))`  
- 定义在 com.a.b 包和所有子包里的任意类的任意方法的执行：`execution(* com.a.b..*.*(..))`  
- 定义在 com.a.b 包和所有子包里的 Demo 类的任意方法的执行：`execution(* com.a.b..Demo.*(..))")`  
  
方法和构造器  
- 特定的某个方法：`public void Account.debit(float) throws InsufficientBalanceException`  
- Account中以set开头，并且只有一个参数类型的方法：`public void Account.set*(*)`  
- Account中所有的没有参数的public void 方法：`public void Account.*()`  
- Account中所有没有参数的public 方法：`public * Account.*()`  
- Account中所有的public 方法：`public * Account.*(..)`  
- Account中所有的方法：`* Account.*(..)`  
- Account中的所有的方法，包括子类的方法：`* Account+.*(..)`  
- Account中所有的非public方法：`!public * Account.*(..)`  
- 所有的read方法：`* java.io.Reader.read(..)`  
- 所有以`read(char[])`开始的方法，包括`read(char[])`和`read(char[],int,int)`：`* java.io.Reader.read(char[],..)`  
- 所有以add开始以Listener结尾的方法，参数为EventListener或子类：`* javax..*.add*Listener(EventListener+)`  
- 抛出RemoteException的所有方法：`* *.*(..) throws RemoteException`  
  
构造器，同上面，没有参数的构造器方法：`public Account.new()`  
属性签名模式，同方法一样，属性也查不多  
- 所有Account的属性：`* Account.*  `  
- 所有非public static 的属性，在abc的包或者子包中：`!public static * abc..*.*`  
  
类型签名样式  
- `Account`  类型Account  
- `*Account`  使用Account名称结束的类型，如SavingsAccount和CheckingAccount  
- `java.*.Date`  类型Date在任何直接的java子包中，如java.util.Date和java.sql.Date  
- `java..*`  任何在java包或者所有子包中的类型，如java.awt和java.util或者java.awt.event 和java.util.logging  
- `javax..*Model+`  所有javax包或者子包中以Model结尾的类型和其所有子类，如TableModel,TreeModel。  
- `! vector`  所有除了Vector的类型  
- `Vector || Hashtable`  Vector或者Hashtable类型  
- `java.util.RandomAccess+ && java.util.List+`  实现RandomAccess和List的所有子类  
  
# Hugo 简介  
[hugo](https://github.com/JakeWharton/hugo)  
  
Hugo是JakeWharton大神开发的一个`通过注解触发的Debug日志库`。它是一个非常好的`AOP`框架，在Debug模式下，Hugo利用`aspectj`库来进行切面编程，插入日志代码，用极小的代价帮我们实现优雅的`函数监控`。  
  
## 简介  
Annotation-triggered注解触发 method call logging for your debug builds.  
  
As a programmer, you often add log statements to print method calls, their arguments, their return values, and the time it took to execute.   
  
This is not a question. Every one of you does this. Shouldn't it be easier?  
  
Simply add `@DebugLog` to your methods and you will automatically get all of the things listed above logged for free.  
  
```java  
@DebugLog  
public String getName(String first, String last) {  
  SystemClock.sleep(15); // Don't ever really do this!  
  return first + " " + last;  
}  
```  
```java  
V/Example: ⇢ getName(first="Jake", last="Wharton")  
V/Example: ⇠ getName [16ms] = "Jake Wharton"  
```  
  
The logging will only happen in debug builds and the annotation itself is never present in the compiled class file注解不存在于编译后的类文件中 for any build type.   
  
This means you can keep the annotation and check it into source control. It has zero effect on non-debug builds.  
  
Add it to your project today!  
  
## 使用步骤  
1、在 project 的 build.gradle 中添加：  
```groovy  
buildscript {  
  repositories {  
    mavenCentral()  
  }  
  
  dependencies {  
    classpath 'com.jakewharton.hugo:hugo-plugin:1.2.1'  
  }  
}  
```  
  
2、在module的 build.gradle 中添加：  
```groovy  
apply plugin: 'com.jakewharton.hugo'  
```  
  
3、Disable logging temporarily暂时:  
```  
Hugo.setEnabled(true|false)  
```  
  
4、在需要的的类、方法、构造方法上加上注解即可  
```  
@DebugLog  
```  
  
## 实现原理简介  
[Hugo源码分析](https://blog.csdn.net/xxxzhi/article/details/53048476)   
[Hugo探究](https://blog.csdn.net/hp910315/article/details/52701809)  
  
Hugo可以用极小的代价帮我们实现优雅的函数监控。  
  
Hugo给我们提供了一种思路，即在Android中，也可以利用`AOP`的思路实现优雅的面向切面编程，例如用来统计打点等。  
  
其实，从源码上来看，除了一个 Hugo 类外，这个库没有其他什么内容了。  
  
一个注解 DebugLog：  
```java  
@Target({TYPE, METHOD, CONSTRUCTOR})  //注意类是TYPE的一种)和方法，所以可以用在类上  
@Retention(CLASS)  //保留策略为CLASS，即编译器将把注解记录在类文件中，因为不是RUNTIME，所以运行时注解丢掉了  
//注意，如果我们需要在运行时获取注解信息(比如注解中有字段且需要在运行时获取到此字段的值)，则Retention必须设为【RUNTIME】  
public @interface DebugLog {  
}  
```  
  
一个类 [Hugo.java](https://github.com/JakeWharton/hugo/blob/master/hugo-runtime/src/main/java/hugo/weaving/internal/Hugo.java)   
也就一百行代码，其中与我们业务相关的核心代码为 logAndExecute(ProceedingJoinPoint)：  
```java  
@Around("method() || constructor()")  
public Object logAndExecute(ProceedingJoinPoint joinPoint) throws Throwable {  
    enterMethod(joinPoint);  //ProceedingJoinPoint有参数信息，输出参数的值  
  
    long startNanos = System.nanoTime();//函数执行前记录时间，像我们手动做的一样  
    Object result = joinPoint.proceed();//这里代表我们监控的函数  
    long stopNanos = System.nanoTime();//函数执行结束时，打点记录时间，并计算耗时  
    long lengthMillis = TimeUnit.NANOSECONDS.toMillis(stopNanos - startNanos);  
  
    exitMethod(joinPoint, result, lengthMillis);//输出函数的值，执行耗时  
  
    return result;  
}  
```  
  
2018-4-27  
