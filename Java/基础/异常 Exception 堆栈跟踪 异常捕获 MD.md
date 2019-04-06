| Markdown版本笔记 | 我的GitHub首页 | 我的博客 | 我的微信 | 我的邮箱 |  
| :------------: | :------------: | :------------: | :------------: | :------------: |  
| [MyAndroidBlogs][Markdown] | [baiqiantao][GitHub] | [baiqiantao][博客] | bqt20094 | baiqiantao@sina.com |  
  
[Markdown]:https://github.com/baiqiantao/MyAndroidBlogs  
[GitHub]:https://github.com/baiqiantao  
[博客]:http://www.cnblogs.com/baiqiantao/  
  
异常 Exception 堆栈跟踪 简介 MD  
***  
目录  
===  

- [异常简介](#异常简介)
- [官方文档](#官方文档)
	- [Throwable](#Throwable)
	- [Error](#Error)
	- [Exception](#Exception)
	- [RuntimeException](#RuntimeException)
- [异常捕获](#异常捕获)
	- [API](#API)
	- [UncaughtExceptionHandler 类](#UncaughtExceptionHandler-类)
	- [JAVA 测试案例](#JAVA-测试案例)
	- [Android 中的一个实用案例](#Android-中的一个实用案例)
- [堆栈跟踪 StackTraceElement](#堆栈跟踪-StackTraceElement)
	- [StackTraceElement 文档](#StackTraceElement-文档)
	- [示例1：获取 StackTraceElement[]](#示例1：获取-StackTraceElement)
	- [示例2：通过 printStackTrace 打印](#示例2：通过-printStackTrace-打印)
  
# 异常简介  
异常就是java通过面向对象的思想将`不正常情况`封装成了对象，用异常类对其进行描述。  
  
try catch finally 语句：  
- try语句中若检测到异常(Throwable)会将相关的信息封装成异常对象，然后传递给catch，catch捕获到后对异常进行处理。  
- try中是一个独立的代码块，在其中定义的变量只在该代码块中有效，如果要在try以外继续使用，需要在try外进行声明。  
- try中如果可能会捕获到多个异常，那么应该(建议)有对应个数的catch进行针对性处理。  
- 一个try对应多个catch语句时，`后面的catch子句中的参数类型只能是前面catch字句中参数类型的同级或父类`，而不能是子类，也不能是同一个类型。  
- 一个try对应多个catch语句时，若前面的catch子句中的参数类型和实际捕获到的异常类型一致，则只执行此catch语句，`而不会再匹配或执行下面的catch字句`。  
- finally语句里通常用来关闭资源。比如：数据库资源，IO资源等。  
- try语句如果被执行到，finally语句中的代码只有一种情况不会被执行，就是在之前执行了`System.exit(0)`。  
  
子类在覆盖父类方法时：  
- 子类的方法只能抛出父类的方法抛出的异常或者该异常的`子类`，而不能抛出该异常的父类或其他类型的异常  
- 如果父类的方法抛出多个异常，那么子类的该方法只能抛出父类该方法抛出的异常的`子集`  
- 如果父类的方法没有抛出异常，那么子类覆盖父类的该方法时也不能抛，如果有异常只能使用 try catch 语句  
  
throw  
- throw 语句用于抛出异常对象，一条 throw 语句只能抛出一个异常对象，throw 语句抛出的异常也可以被 try 语句捕获  
- throw 语句之后不能再有其他语句，因为 `throw 语句后面的代码是执行不到的`。  
- RuntimeException 以及其子类可以在函数中通过 throw 抛出而不用在函数上声明。  
  
# 官方文档  
## Throwable  
```  
继承自：Object  
实现的接口：Serializable  
直接已知子类：Error, Exception  
```  
  
Throwable 类是 Java 语言中所有错误或异常的`超类`。只有当对象是此类（或其子类之一）的实例时，才能通过 Java 虚拟机或者 Java `throw` 语句抛出。类似地，只有此类或其子类之一才可以是 `catch` 子句中的参数类型。  
  
两个子类的实例，Error 和 Exception，通常用于指示发生了异常情况。通常，这些实例是在异常情况的上下文中新近创建的，因此包含了相关的信息（比如堆栈跟踪数据）。  
  
Throwable 包含了其线程创建时线程执行堆栈的快照，它还包含了给出有关错误更多信息的消息字符串。最后，它还可以包含 cause（原因）：另一个导致此 throwable 抛出的 throwable。此 cause 设施在 1.4 版本中首次出现。它也称为 `异常链` 设施，因为 cause 自身也会有 cause，依此类推，就形成了异常链，每个异常都是由另一个异常引起的。  
  
导致 throwable cause 的一个理由是，抛出它的类构建在低层抽象之中，而高层操作由于低层操作的失败而失败。  
导致 throwable cause 的另一个理由是，抛出它的方法必须符合通用接口，而通用接口不允许方法直接抛出 cause。  
  
在版本 1.4 中还引入了 getStackTrace() 方法，它允许通过各种形式的 printStackTrace() 方法编程访问堆栈跟踪信息，这些信息以前只能以文本形式使用。此信息已经添加到该类的序列化表示形式，因此 getStackTrace 和 printStackTrace 将可在反序列化时获得的 throwable 上正确操作。  
  
**构造方法**  
```java  
Throwable(String message, Throwable cause) //构造一个带指定详细消息和 cause 的新 throwable  
Throwable(String message)  //Cause未初始化，可在以后通过调用 initCause(Throwable) 来初始化  
Throwable(Throwable cause)  //构造一个将 null 作为其详细消息的新 throwable  
Throwable()  //构造一个将 null 作为其详细消息的新 throwable  
```  
- 注意，与 cause 相关的详细消息不是自动合并到这个 throwable 的详细消息中的。调用 fillInStackTrace() 方法来初始化新创建的 throwable 中的堆栈跟踪数据。  
- 参数：message - 详细消息。保存此消息，以便以后通过 getMessage() 方法获取它  
- 参数：cause - 原因。保存此 cause，以便以后通过 getCause() 方法获取它。允许 null 值，指出 cause 是不存在的或是未知的。  
  
**普通 get/set 方法**  
- `String  getMessage()`  返回此 throwable 的详细消息字符串。  
- `String  getLocalizedMessage()`  创建此 throwable 的本地化描述。  
    - 子类可以重写此方法，以便生成特定于语言环境的消息。  
    - 对于不重写此方法的子类，默认实现返回与 getMessage() 相同的结果。  
- `String  toString()`  返回此 throwable 的简短描述。  
    - 如果 getLocalizedMessage 返回 null，则只返回类名称。  
    - 格式【此对象的类的 name: (冒号和一个空格) 调用此对象 getLocalizedMessage() 方法的结果】，如【java.lang.Throwable: 异常信息】  
- `Throwable  getCause()`  返回此 throwable 的 cause；如果 cause 不存在或未知，则返回 null。该 Cause 是导致抛出此 throwable 的throwable。  
- `Throwable  initCause(Throwable cause)`  将此 throwable 的 cause 初始化为指定值。  
    - 允许 null 值，指出 cause 是不存在的或是未知的。  
    - 此方法至多可以调用一次。  
    - 此方法通常从构造方法中调用，或者在创建 throwable 后立即调用。  
    - 如果此 throwable 通过 Throwable(Throwable) 或 Throwable(String,Throwable) 创建，此方法一次也不能调用。  
  
**操作堆栈跟踪信息**  
- `Throwable  fillInStackTrace()`  在异常堆栈跟踪中填充。此方法在 Throwable 对象信息中记录有关当前线程堆栈帧的当前状态。  
- `StackTraceElement[]  getStackTrace()`  提供编程访问由 printStackTrace() 输出的堆栈跟踪信息。  
- `void  setStackTrace(StackTraceElement[] stackTrace)`  设置将由 getStackTrace() 返回，并由 printStackTrace() 和相关方法输出的堆栈跟踪元素。  
  
**打印堆栈跟踪信息**  
- void  printStackTrace()  将此 throwable 及其追踪输出至标准错误流System.err。  
- void  printStackTrace(PrintStream s)  将此 throwable 及其追踪输出到指定的输出流。  
- void  printStackTrace(PrintWriter s)  将此 throwable 及其追踪输出到指定的PrintWriter。  
  
注意，不必 重写任何 PrintStackTrace 方法，应通过调用 getCause 方法来确定 throwable 的 cause。  
通过 printStackTrace 输出的堆栈跟踪信息的格式：第一行包含此对象的 `toString()` 方法的结果，剩余行表示以前由方法 `fillInStackTrace()` 记录的数据。  
  
## Error  
Error 是 Throwable 的子类，用于指示合理的应用程序`不应该试图捕获`的`严重问题`。大多数这样的错误都是异常条件。虽然 `ThreadDeath` 错误是一个“正规”的条件，但它也是 Error 的子类，因为大多数应用程序都不应该试图捕获它。  
  
在执行该方法期间，无需在其 `throws` 子句中声明可能抛出但是未能捕获的 Error 的任何子类，`因为这些错误【可能是再也不会发生】的异常条件`。  
  
常见的直接子类：  
- `OutOfMemoryError`：因为`内存溢出`或没有可用的内存提供给垃圾回收器时，Java 虚拟机无法分配一个对象，这时抛出该异常。  
- `StackOverflowError`：当应用程序`递归太深`而发生`堆栈溢出`时，抛出该错误。  
- `NoClassDefFoundError`：当 Java 虚拟机或 ClassLoader 实例试图在类的定义中加载，但`无法找到该类的定义`时，抛出此异常。  
- `UnsatisfiedLinkError`：当 Java 虚拟机无法找到声明为 native 的方法的适当本地语言定义时，抛出该错误。  
- `IOError`：当发生严重的 I/O 错误时，抛出此错误。  
- LinkageError：LinkageError 的子类指示一个类在一定程度上依赖于另一个类；但是，在编译前一个类之后，后一个类发生了不相容的改变。  
- ClassCircularityError：当初始化类时检测到类的循环调用的时候，抛出该错误。  
- ClassFormatError：当 Java 虚拟机试图读取类文件并确定该文件存在格式错误或无法解释为类文件时，抛出该错误。  
- ExceptionInInitializerError：静态初始化程序中发生意外异常的信号。抛出此错误表明在计算静态初始值或静态变量的初始值期间发生异常。  
- IncompatibleClassChangeError：在某些类定义中出现不兼容的类更改时抛出该异常。某些目前执行的方法所依赖的类定义已发生了变化。  
- VerifyError：当“校验器”检测到一个类文件虽然格式正确，但包含着一些内部不一致性或安全性问题时，抛出该错误。  
- `ThreadDeath`：调用 Thread 类中带有零参数的 `stop` 方法时(此方法已过时)，受害线程将抛出一个 ThreadDeath 实例。仅当应用程序在被异步终止后必须清除时才应该捕获这个类的实例。  
- VirtualMachineError：当 Java 虚拟机崩溃或用尽了它继续操作所需的资源时，抛出该错误。  
- InternalError：该异常指示 Java 虚拟机中出现一些意外的内部错误。  
- UnknownError：当 Java 虚拟机中出现一个未知但严重的异常时，抛出该错误。  
  
## Exception  
Exception 类及其子类是 Throwable 的一种形式，它指出了合理的应用程序想要捕获的条件。  
  
直接已知子类：  
```  
AclNotFoundException, ActivationException, AlreadyBoundException, ApplicationException, AWTException, BackingStoreException, BadAttributeValueExpException, BadBinaryOpValueExpException, BadLocationException, BadStringOperationException, BrokenBarrierException, CertificateException, ClassNotFoundException, CloneNotSupportedException, DataFormatException, DatatypeConfigurationException, DestroyFailedException, ExecutionException, ExpandVetoException, FontFormatException, GeneralSecurityException, GSSException, IllegalAccessException, IllegalClassFormatException, InstantiationException, InterruptedException, IntrospectionException, InvalidApplicationException, InvalidMidiDataException, InvalidPreferencesFormatException, InvalidTargetObjectTypeException, InvocationTargetException, IOException, JAXBException, JMException, KeySelectorException, LastOwnerException, LineUnavailableException, MarshalException, MidiUnavailableException, MimeTypeParseException, MimeTypeParseException, NamingException, NoninvertibleTransformException, NoSuchFieldException, NoSuchMethodException, NotBoundException, NotOwnerException, ParseException, ParserConfigurationException, PrinterException, PrintException, PrivilegedActionException, PropertyVetoException, RefreshFailedException, RemarshalException, RuntimeException, SAXException, ScriptException, ServerNotActiveException, SOAPException, SQLException, TimeoutException, TooManyListenersException, TransformerException, TransformException, UnmodifiableClassException, UnsupportedAudioFileException, UnsupportedCallbackException, UnsupportedFlavorException, UnsupportedLookAndFeelException, URIReferenceException, URISyntaxException, UserException, XAException, XMLParseException, XMLSignatureException, XMLStreamException, XPathException  
```  
  
## RuntimeException  
[RuntimeException](http://tool.oschina.net/uploads/apidocs/jdk-zh/java/lang/RuntimeException.html)  
  
RuntimeException 是那些可能`在 Java 虚拟机正常运行期间抛出的异常的超类`。  
可能在执行方法期间抛出但未被捕获的 RuntimeException 的任何子类都`无需在【throws】子句中进行声明`。  
  
直接已知子类：  
```java  
AnnotationTypeMismatchException  
ArithmeticException  
ArrayStoreException  
BufferOverflowException  
BufferUnderflowException  
CannotRedoException  
CannotUndoException  
ClassCastException  
CMMException  
ConcurrentModificationException  
DOMException  
EmptyStackException  
EnumConstantNotPresentException  
EventException  
IllegalArgumentException  
IllegalMonitorStateException  
IllegalPathStateException  
IllegalStateException  
ImagingOpException  
IncompleteAnnotationException  
IndexOutOfBoundsException  
JMRuntimeException  
LSException  
MalformedParameterizedTypeException  
MirroredTypeException  
MirroredTypesException  
MissingResourceException  
NegativeArraySizeException  
NoSuchElementException  
NoSuchMechanismException  
NullPointerException  
ProfileDataException  
ProviderException  
RasterFormatException  
RejectedExecutionException  
SecurityException  
SystemException  
TypeConstraintException  
TypeNotPresentException  
UndeclaredThrowableException  
UnknownAnnotationValueException  
UnknownElementException  
UnknownTypeException  
UnmodifiableSetException  
UnsupportedOperationException  
WebServiceException  
```  
  
# 异常捕获  
## API  
**setUncaughtExceptionHandler**  
```java  
public void setUncaughtExceptionHandler(Thread.UncaughtExceptionHandler eh)  
```  
设置该线程由于未捕获到异常而突然终止时调用的处理程序。  
通过明确设置未捕获到的异常处理程序，线程可以完全控制它对未捕获到的异常作出响应的方式。   
如果没有设置这样的处理程序，则该线程的 ThreadGroup 对象将充当其处理程序。  
  
```java  
public Thread.UncaughtExceptionHandler getUncaughtExceptionHandler()  
```  
返回该线程由于未捕获到异常而突然终止时调用的处理程序。  
如果该线程尚未明确设置未捕获到的异常处理程序，则返回该线程的 ThreadGroup 对象，除非该线程已经终止，在这种情况下，将返回 null。  
  
**setDefaultUncaughtExceptionHandler**  
```java  
public static void setDefaultUncaughtExceptionHandler(Thread.UncaughtExceptionHandler eh)  
```  
设置当线程由于未捕获到异常而突然终止，并且没有为该线程定义其他处理程序时所调用的默认处理程序。  
  
未捕获到的异常处理首先由线程控制，然后由线程的 ThreadGroup 对象控制，最后由未捕获到的默认异常处理程序控制。  
  
如果线程不设置明确的未捕获到的异常处理程序，并且该线程的线程组（包括父线程组）未特别指定其 uncaughtException 方法，则将调用默认处理程序的 uncaughtException 方法。  
  
通过设置未捕获到的默认异常处理程序，应用程序可以为那些已经接受系统提供的任何“默认”行为的线程改变未捕获到的异常处理方式（如记录到某一特定设备或文件）。  
  
请注意，未捕获到的默认异常处理程序通常不应顺从该线程的 ThreadGroup 对象，因为这可能导致无限递归。  
  
```java  
public static Thread.UncaughtExceptionHandler getDefaultUncaughtExceptionHandler()  
```  
返回线程由于未捕获到异常而突然终止时调用的默认处理程序。如果返回值为 null，则没有默认处理程序。  
  
## UncaughtExceptionHandler 类  
```java  
public static interface Thread.UncaughtExceptionHandler  
```  
所有已知实现类：ThreadGroup  
  
当 Thread 因未捕获的异常而突然终止时，调用处理程序的接口。  
  
当某一线程因未捕获的异常而即将终止时，Java 虚拟机将使用 Thread.getUncaughtExceptionHandler() 查询该线程以获得其 UncaughtExceptionHandler 的线程，并调用处理程序的 uncaughtException 方法，将线程和异常作为参数传递。  
  
如果某一线程没有明确设置其 UncaughtExceptionHandler，则将它的 ThreadGroup 对象作为其 UncaughtExceptionHandler。如果 ThreadGroup 对象对处理异常没有什么特殊要求，那么它可以将调用转发给默认的未捕获异常处理程序。  
  
```java  
void uncaughtException(Thread t, Throwable e)   
```  
当给定线程因给定的未捕获异常而终止时，调用该方法。  
Java 虚拟机将忽略该方法抛出的任何异常。  
  
## JAVA 测试案例  
```java  
public class Test {  
    public static void main(String[] args) {  
        setDefaultUncaughtExceptionHandler();  
        test();  
    }  
  
    private static void test() {  
        new Thread(new Runnable() {  
            @Override  
            public void run() {  
                System.out.println("子线程异常前");  
                System.out.println(1 / 0);  
            }  
        }).start();  
        System.out.println("当前线程异常前");  
        System.out.println(1 / 0);  
        System.out.println("异常后的代码不能执行了");  
    }  
  
    private static void setDefaultUncaughtExceptionHandler() {  
        UncaughtExceptionHandler currentHandler = new UncaughtExceptionHandler() {  
            @Override  
            public void uncaughtException(Thread t, Throwable e) {  
                System.out.println("【当前线程的Handler处理异常信息】" + t.toString() + "\n" + e.getMessage());  
            }  
        };  
        UncaughtExceptionHandler defaultHandler = new UncaughtExceptionHandler() {  
            @Override  
            public void uncaughtException(Thread t, Throwable e) {  
                StringWriter writer = new StringWriter();  
                PrintWriter printWriter = new PrintWriter(writer);  
                printWriter.write("start------------\n");  
                e.printStackTrace(printWriter);  
                printWriter.write("------------end");  
                printWriter.close();  
                System.out.println("【默认的Handler处理异常信息】" + writer.getBuffer().toString());  
            }  
        };  
        Thread.currentThread().setUncaughtExceptionHandler(currentHandler);  
        Thread.setDefaultUncaughtExceptionHandler(defaultHandler);  
    }  
}  
```  
  
运行结果  
```java  
子线程异常前  
当前线程异常前  
【当前线程的Handler处理异常信息】Thread[main,5,main]  
/ by zero  
【默认的Handler处理异常信息】start------------  
java.lang.ArithmeticException: / by zero  
    at Test$1.run(Test.java:16)  
    at java.lang.Thread.run(Thread.java:745)  
------------end  
```  
  
## Android 中的一个实用案例  
异常处理类  
```java  
/**  
 * Desc：采集崩溃日志  
 *  
 * @author <a href="http://www.cnblogs.com/baiqiantao">白乾涛</a><p>  
 * @tag 崩溃日志<p>  
 * @date 2018/5/2 14:12 <p>  
 */  
public class CrashHandler implements Thread.UncaughtExceptionHandler {  
    private static final String LOG_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/crashLog/";  
    private Application application;  
      
    private static CrashHandler instance = new CrashHandler();  
      
    private CrashHandler() {//构造方法私有  
    }  
      
    public static CrashHandler getInstance() {  
        return instance;  
    }  
      
    public void init(Application application) {  
        this.application = application;  
        Thread.setDefaultUncaughtExceptionHandler(instance);//设置该CrashHandler为系统默认的  
    }  
      
    @Override  
    public void uncaughtException(Thread thread, Throwable ex) {  
        saveInfoToFile(collectCrashInfo(ex));//保存错误信息  
        new Thread() {  
            @Override  
            public void run() {  
                Looper.prepare();  
                Toast.makeText(application, "程序开小差了，将会在2秒后退出", Toast.LENGTH_SHORT).show();//使用Toast来显示异常信息  
                Looper.loop();  
            }  
        }.start();  
        SystemClock.sleep(2000);//延迟2秒杀进程  
        android.os.Process.killProcess(android.os.Process.myPid());  
        System.exit(0);  
    }  
      
    private String collectCrashInfo(Throwable ex) {  
        if (ex == null) return "";  
          
        Writer writer = new StringWriter();  
        PrintWriter printWriter = new PrintWriter(writer);  
        ex.printStackTrace(printWriter);  
        Throwable throwable = ex.getCause();  
        while (throwable != null) {  
            throwable.printStackTrace(printWriter);  
            throwable = throwable.getCause();//逐级获取错误信息  
        }  
        String crashInfo = writer.toString();  
        Log.i("bqt", "【错误信息】" + crashInfo);  
        printWriter.close();  
        return crashInfo;  
    }  
      
    private void saveInfoToFile(String crashInfo) {  
        try {  
            File dir = new File(LOG_PATH);  
            if (!dir.exists()) {  
                dir.mkdirs();  
            }  
            String date = new SimpleDateFormat("yyyy.MM.dd_HH_mm_ss", Locale.getDefault()).format(new Date());  
            String fileName = LOG_PATH + "crash_" + date + ".txt";  
            FileWriter writer = new FileWriter(fileName);//如果保存失败，很可能是没有写SD卡权限  
            writer.write(crashInfo);  
            writer.close();  
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
    }  
}  
```  
  
获取的异常信息  
```java  
java.lang.NullPointerException: Attempt to invoke virtual method 'int java.lang.String.length()' on a null object reference  
        at com.bqt.test.MainActivity.onListItemClick(MainActivity.java:34)  
        at android.app.ListActivity$2.onItemClick(ListActivity.java:319)  
        at android.widget.AdapterView.performItemClick(AdapterView.java:339)  
        at android.widget.AbsListView.performItemClick(AbsListView.java:1705)  
        at android.widget.AbsListView$PerformClick.run(AbsListView.java:4171)  
        at android.widget.AbsListView$13.run(AbsListView.java:6735)  
        at android.os.Handler.handleCallback(Handler.java:751)  
        at android.os.Handler.dispatchMessage(Handler.java:95)  
        at android.os.Looper.loop(Looper.java:154)  
        at android.app.ActivityThread.main(ActivityThread.java:6682)  
        at java.lang.reflect.Method.invoke(Native Method)  
        at com.android.internal.os.ZygoteInit$MethodAndArgsCaller.run(ZygoteInit.java:1534)  
        at com.android.internal.os.ZygoteInit.main(ZygoteInit.java:1424)  
```  
  
# 堆栈跟踪 StackTraceElement  
  
## StackTraceElement 文档  
此类在 `java.lang` 包下  
```java  
public final class StackTraceElement extends Object implements Serializable  
```  
  
堆栈跟踪元素，它由 `Throwable.getStackTrace()` 返回。每个元素表示单独的一个【堆栈帧】。所有的堆栈帧（堆栈顶部的那个堆栈帧除外）都表示一个【方法调用】。堆栈顶部的帧表示【生成堆栈跟踪的执行点】。通常，这是创建对应于堆栈跟踪的 throwable 的点。  
  
**构造方法**  
```java  
public StackTraceElement(String declaringClass, String methodName, String fileName, int lineNumber)  
```  
创建表示指定【执行点】的【堆栈跟踪元素】。  
参数：  
- declaringClass - 类的完全限定名，该类包含由堆栈跟踪元素所表示的执行点  
- methodName - 方法名，该方法包含由堆栈跟踪元素所表示的执行点  
- fileName - 文件名，该文件包含由堆栈跟踪元素所表示的执行点；如果该信息不可用，则该参数为 null  
- lineNumber - 源代码行的行号，该代码行包含由堆栈跟踪元素所表示的执行点；如果此信息不可用，则该参数为负数。值 -2 表示包含执行点的方法是一个本机方法  
  
**普通方法**  
- String  getClassName()  返回类的完全限定名，该类包含由该堆栈跟踪元素所表示的执行点。  
- String  getFileName()  返回源文件名，该文件包含由该堆栈跟踪元素所表示的执行点。  
- int  getLineNumber()  返回源行的行号，该行包含由该堆栈该跟踪元素所表示的执行点。  
- String  getMethodName()  返回方法名，此方法包含由该堆栈跟踪元素所表示的执行点。  
- boolean  isNativeMethod()  如果包含由该堆栈跟踪元素所表示的执行点的方法是一个本机方法，则返回 true。  
  
**重写的Object的方法**  
- boolean  equals(Object obj)  如果指定的对象是另一个 StackTraceElement 实例，并且该对象表示的【执行点】与该实例的相同，则返回 ture。  
- int  hashCode()  返回此堆栈跟踪元素的哈希码值。  
- String  toString()  返回表示该堆栈跟踪元素的字符串。  
  
该字符串的格式取决于实现，但是可将`MyClass.mash(MyClass.java:9)`视为典型的格式，其中：  
- `MyClass` 是类的完全限定名，该类包含由该堆栈跟踪元素所表示的执行点；  
- `mash` 是包含执行点的方法的名字；  
- `MyClass.java` 是包含执行点的源文件；  
- `9` 是包含执行点的源行的行号。  
  
其他格式  
- `MyClass.mash(MyClass.java)` - 同上，但是行号不可用。  
- `MyClass.mash(Unknown Source)` - 同上，但是文件名和行号都不可用。  
- `MyClass.mash(Native Method)` - 同上，但是文件名和行号都不可用，并且已知包含执行点的方法是本机方法。  
  
## 示例1：获取 StackTraceElement[]  
```java  
public class Test {  
    public static void main(String[] args) {  
        printCallStatck();  
    }  
  
    public static void printCallStatck() {  
        StackTraceElement[] stackElements = Thread.currentThread().getStackTrace();  
        //StackTraceElement[] stackElements = new Throwable().getStackTrace();  
        if (stackElements != null) {  
            for (StackTraceElement stackTraceElement : stackElements) {  
                System.out.println("ClassName:" + stackTraceElement.getClassName());  
                System.out.println("FileName:" + stackTraceElement.getFileName());  
                System.out.println("LineNumber:" + stackTraceElement.getLineNumber());  
                System.out.println("MethodName:" + stackTraceElement.getMethodName());  
                System.out.println("isNativeMethod:" + stackTraceElement.isNativeMethod());  
                System.out.println("信息:" + stackTraceElement.toString());  
                System.out.println("-----------------------------------");  
            }  
        }  
    }  
}  
```  
  
结果：  
```java  
ClassName:java.lang.Thread  
FileName:null  
LineNumber:-1  
MethodName:getStackTrace  
isNativeMethod:false  
信息:java.lang.Thread.getStackTrace(Unknown Source)  
-----------------------------------  
ClassName:Test  
FileName:Test.java  
LineNumber:7  
MethodName:printCallStatck  
isNativeMethod:false  
信息:Test.printCallStatck(Test.java:7)  
-----------------------------------  
ClassName:Test  
FileName:Test.java  
LineNumber:3  
MethodName:main  
isNativeMethod:false  
信息:Test.main(Test.java:3)  
-----------------------------------  
```  
  
注意：  
1、数组中元素的个数是不确定个的，这完全取决于打印堆栈信息时经过了几步的调用。  
2、并非最后一个元素的就一定是我们最想要的信息，因为在框架中，后续可能还会有很多层的调用，比如在Android中打印日志的一个案例：  
```java  
dalvik.system.VMStack:getThreadStackTrace:-2  
java.lang.Thread:getStackTrace:1566  
com.yibasan.lizhifm.ad.Lg:log:58  
com.yibasan.lizhifm.ad.Lg:i:34//这里调用了Log.i  
com.yibasan.lizhifm.ad.SplashAdManager:canShowAd:66//我们是在这里调用了封装好的Lg类中的Lg.i方法，所以这里的元素是我们最想要的  
com.yibasan.lizhifm.activities.BaseActivity:onResume:518  
com.yibasan.lizhifm.activities.fm.NavBarActivity:onResume:375  
android.app.Instrumentation:callActivityOnResume:1276  
android.app.Activity:performResume:6937  
android.app.ActivityThread:performResumeActivity:3468  
android.app.ActivityThread:handleResumeActivity:3531  
android.app.ActivityThread$H:handleMessage:1569  
android.os.Handler:dispatchMessage:102  
android.os.Looper:loop:154  
android.app.ActivityThread:main:6209  
java.lang.reflect.Method:invoke:-2  
com.android.internal.os.ZygoteInit$MethodAndArgsCaller:run:900  
com.android.internal.os.ZygoteInit:main:790  
```  
  
## 示例2：通过 printStackTrace 打印  
```java  
public class Test {  
    public static void main(String[] args) {  
        printCallStatck();  
    }  
  
    public static void printCallStatck() {  
        new Throwable("创建一个Throwable对象").printStackTrace();  
    }  
}  
```  
结果  
```java  
java.lang.Throwable: 创建一个Throwable对象  
    at Test.printCallStatck(Test.java:7)  
    at Test.main(Test.java:3)  
```  
  
2018-6-8  
