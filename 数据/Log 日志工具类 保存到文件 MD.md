| Markdown版本笔记 | 我的GitHub首页 | 我的博客 | 我的微信 | 我的邮箱 |  
| :------------: | :------------: | :------------: | :------------: | :------------: |  
| [MyAndroidBlogs][Markdown] | [baiqiantao][GitHub] | [baiqiantao][博客] | bqt20094 | baiqiantao@sina.com |  
  
[Markdown]:https://github.com/baiqiantao/MyAndroidBlogs  
[GitHub]:https://github.com/baiqiantao  
[博客]:http://www.cnblogs.com/baiqiantao/  
  
Log 日志工具类 保存到文件 MD  
***  
目录  
===  

- [简介](#简介)
- [日志工具类](#日志工具类)
	- [超强工具类](#超强工具类)
		- [实验案例](#实验案例)
		- [实验结果](#实验结果)
	- [一个专门保存日志到文件的工具类](#一个专门保存日志到文件的工具类)
	- [一个简单封装的日志工具类](#一个简单封装的日志工具类)
	- [一个保存日志到文件的工具类](#一个保存日志到文件的工具类)
  
日志工具类  
几个比较流行的开源库  
[logger](https://github.com/orhanobut/logger)  8K  
[hugo](https://github.com/JakeWharton/hugo)  6K   
[timber](https://github.com/JakeWharton/timber) 5.5K   
[XLog](https://github.com/elvishew/XLog/blob/master/README_ZH.md)   1.5K  
[KLog](https://github.com/ZhaoKaiQiang/KLog)   1.5K  
  
# 简介  
```java  
public final class android.util.Log extends Object  
```  
  
API for sending log output.  
  
Generally, use the Log.v() Log.d() Log.i() Log.w() and Log.e() methods.  
The order in terms of verbosity(冗长，赘言), from least to most is ERROR, WARN, INFO, DEBUG, VERBOSE.   
  
Verbose should never be compiled into an application except during development.   
Debug logs are compiled in but stripped at runtime. Error, warning and info logs are always kept.  
  
Tip: A good convention约定 is to declare a TAG constant in your class:  
```java  
private static final String TAG = "MyActivity";  
```  
and use that in subsequent随后的 calls to the log methods.  
  
Tip: Don't forget that when you make a call like  
```java  
Log.v(TAG, "index=" + i);  
```  
that when you're building the string to pass into Log.d, the compiler uses a `StringBuilder` and at least three allocations分配 occur: the StringBuilder itself, the buffer, and the String object. Realistically, there is also another buffer allocation and copy, and even more pressure on the gc. That means that if your log message is filtered out, you might be doing significant work and incurring significant overhead.  
  
# 日志工具类  
## 超强工具类  
一个可打印并可跳转到"日志所在类、所在行"的工具类  
效果如下：  
![](index_files/53c42892-7b28-45eb-9cc3-2ca838740cc5.png)  
![](index_files/8f0400f7-3903-4082-addc-771747ee513d.png)  
  
```java  
public class L {  
    private static boolean OPEN_LOG = BuildConfig.DEBUG;  
      
    public static void isOpenLog(boolean isOpenLog) {  
        OPEN_LOG = isOpenLog;  
    }  
      
    public static void v(Object... message) {  
        if (OPEN_LOG) log(Log.VERBOSE, formatMessage(message));  
    }  
      
    public static void d(Object... message) {  
        if (OPEN_LOG) log(Log.DEBUG, formatMessage(message));  
    }  
      
    public static void i(Object... message) {  
        if (OPEN_LOG) log(Log.INFO, formatMessage(message));  
    }  
      
    public static void w(Object... message) {  
        log(Log.WARN, formatMessage(message));  
    }  
      
    public static void e(Object... message) {  
        log(Log.ERROR, formatMessage(message));  
    }  
      
    public static void json(String json) {  
        json = new GsonBuilder()  
            .setPrettyPrinting()  
            .create()  
            .toJson(new JsonParser().parse(json));  
        log(Log.DEBUG, formatMessage(json));  
    }  
      
    /**  
     * 根据type输出日志消息，包括方法名，方法行数，Message  
     *  
     * @param type    日志类型，如Log.INFO  
     * @param message 日志内容  
     */  
    private static void log(int type, String message) {  
        StackTraceElement stackTrace = Thread.currentThread().getStackTrace()[4]; //核心一，获取到执行log方法的代码位置  
        String className = stackTrace.getClassName();  
        String tag = className.substring(className.lastIndexOf('.') + 1); //不必须的tag  
        StringBuilder sb = new StringBuilder();  
          
        sb.append("日志")  
            .append("(") //核心二，通过(fileName:lineNumber)格式，即可在点击日志后跳转到执行log方法的代码位置  
            .append(stackTrace.getFileName())//文件名  
            .append(":")  
            .append(stackTrace.getLineNumber())//行号  
            .append(")")  
            .append("_") //后面的属于不必须的信息  
            .append(stackTrace.getMethodName())//方法名  
            .append("【")  
            .append(message)//消息  
            .append("】");  
          
        switch (type) {  
            case Log.DEBUG:  
                Log.d(tag, sb.toString());  
                break;  
            case Log.INFO:  
                Log.i(tag, sb.toString());  
                break;  
            case Log.WARN:  
                Log.w(tag, sb.toString());  
                break;  
            case Log.ERROR:  
                Log.e(tag, sb.toString());  
                break;  
            case Log.VERBOSE:  
                Log.v(tag, sb.toString());  
                break;  
        }  
    }  
      
    private static String formatMessage(Object... messages) {  
        StringBuilder sb = new StringBuilder();  
        for (Object mobj : messages) {  
            sb.append(objToString(mobj, -1)).append("\n");  
        }  
        return sb.substring(0, sb.length() - 1).trim();  
    }  
      
    //******************************************************************************************  
      
    private static String objToString(Object obj, int currentLvel) {  
        if (obj == null) return "null";  
        currentLvel++;  
        StringBuilder sb = new StringBuilder();  
        if (obj instanceof String) {//字符串  
            sb.append(getSpace(currentLvel)).append(obj);  
        } else if (obj instanceof Object[]) {//数组  
            sb.append("数组：").append("\n");  
            for (Object mobj : (Object[]) obj) {  
                sb.append(objToString(mobj, currentLvel)).append("\n");//递归  
            }  
        } else if (obj instanceof Collection) {//集合  
            sb.append("集合：").append("\n");  
            for (Object mobj : (Collection) obj) {  
                sb.append(objToString(mobj, currentLvel)).append("\n");//递归  
            }  
        } else {//其他对象  
            sb.append(getSpace(currentLvel)).append(obj.toString());  
        }  
        return sb.toString();  
    }  
      
    /***  
     * 格式化目录  
     *  
     * @param level 目录层次，也即"| _ _"的个数  
     */  
    private static String getSpace(int level) {  
        StringBuilder sb = new StringBuilder();  
        for (int i = 0; i < level; i++) {  
            sb.append("|__");  
        }  
        return sb.toString();  
    }  
}  
```  
  
### 实验案例  
```java  
L.i("包青天");  
L.i(5);  
L.i(true);  
L.json("{\"name\":\"bqt\",\"age\":30,\"more\":[true,985,\"河南\"] }");  
L.i(new Person("白乾涛"));  
L.i("----------------------连续---------------------");  
L.i("包青天", 5, true, new Person("白乾涛"));  
L.i("----------------------数组---------------------");  
Object[] array = {"包青天", 5, true, new Person("白乾涛")};  
L.i(array);  
L.i("-----------------------集合--------------------");  
L.i(Arrays.asList(array));  
L.i("---------------------嵌套----------------------");  
List<Object> mList = new ArrayList<>();  
mList.add("这是一级元素");  
mList.add(10086);  
mList.add(new Person("包青天"));  
mList.add(array);  
mList.add(Arrays.asList(array));  
L.i(mList);  
```  
  
### 实验结果  
```java  
日志(MainActivity.java:53)_test【包青天】  
日志(MainActivity.java:54)_test【5】  
日志(MainActivity.java:55)_test【true】  
日志(MainActivity.java:56)_test【{  
  "name": "bqt",  
  "age": 30,  
  "more": [  
    true,  
    985,  
    "河南"  
  ]  
}】  
日志(MainActivity.java:57)_test【Person{name='白乾涛'}】  
日志(MainActivity.java:58)_test【----------------------连续---------------------】  
日志(MainActivity.java:59)_test【包青天  
5  
true  
Person{name='白乾涛'}】  
日志(MainActivity.java:60)_test【----------------------数组---------------------】  
日志(MainActivity.java:62)_test【包青天  
5  
true  
Person{name='白乾涛'}】  
日志(MainActivity.java:63)_test【-----------------------集合--------------------】  
日志(MainActivity.java:64)_test【集合：  
|__包青天  
|__5  
|__true  
|__Person{name='白乾涛'}】  
日志(MainActivity.java:65)_test【---------------------嵌套----------------------】  
日志(MainActivity.java:72)_test【集合：  
|__这是一级元素  
|__10086  
|__Person{name='包青天'}  
数组：  
|__|__包青天  
|__|__5  
|__|__true  
|__|__Person{name='白乾涛'}  
  
集合：  
|__|__包青天  
|__|__5  
|__|__true  
|__|__Person{name='白乾涛'}】  
```  
  
## 一个专门保存日志到文件的工具类  
```java  
public class LaunchLog {  
    private static long tempTime;  
    private static FileOutputStream outputStream;  
    private static final String DIR = Environment.getExternalStorageDirectory().getAbsolutePath() + "/bqtlog";  
      
    public static void write(String text) {  
        try {  
            File dir = new File(DIR);  
            if (!dir.exists()) {  
                dir.mkdirs();  
            }  
            String date = new SimpleDateFormat("HH:mm:ss_SSS", Locale.getDefault()).format(new Date());  
            if (outputStream == null) {  
                outputStream = new FileOutputStream(new File(dir, "log.txt"), true);  
                outputStream.write(("\n------------------------- " + date + " ------------------------\n").getBytes());  
                tempTime = System.currentTimeMillis();  
            }  
            outputStream.write(("\n" + date + "\t" + text + "(耗时" + (System.currentTimeMillis() - tempTime) + "毫秒)").getBytes());  
            outputStream.flush();  
            tempTime = System.currentTimeMillis();  
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
    }  
      
    public static void close() {  
        if (outputStream != null) {  
            try {  
                outputStream.close();  
            } catch (IOException e) {  
                e.printStackTrace();  
            }  
        }  
    }  
}  
```  
  
## 一个简单封装的日志工具类  
```java  
public class L {  
    /**  
     * 是否需要打印bug，可以在application的onCreate函数里面初始化    
     */  
    public static boolean isDebug = true;  
    private static final String TAG = "bqt";  
    private L() {  
    }  
//**********************************************下面四个是默认tag的函数  ********************************************  
    public static void i(String msg) {  
        if (isDebug) Log.i(TAG, msg);  
    }  
    public static void d(String msg) {  
        if (isDebug) Log.d(TAG, msg);  
    }  
    public static void e(String msg) {  
        if (isDebug) Log.e(TAG, msg);  
    }  
    public static void v(String msg) {  
        if (isDebug) Log.v(TAG, msg);  
    }  
//******************************************下面是传入自定义tag的函数  ************************************************  
    public static void i(String tag, String msg) {  
        if (isDebug) Log.i(tag, msg);  
    }  
    public static void d(String tag, String msg) {  
        if (isDebug) Log.i(tag, msg);  
    }  
    public static void e(String tag, String msg) {  
        if (isDebug) Log.i(tag, msg);  
    }  
    public static void v(String tag, String msg) {  
        if (isDebug) Log.i(tag, msg);  
    }  
}  
```  
  
## 一个保存日志到文件的工具类  
产生的文件内容  
![](index_files/6778ca56-d908-4579-839e-c7758973b3ed.png)  
  
```java  
public class MyLog {  
    private static boolean MYLOG_SWITCH = true; // 是否打印日志及是否写入到文件的总开关    
    private static boolean MYLOG_WRITE_TO_FILE = true;// 是否将日志写入到文件的开关  
    private static char MYLOG_TYPE = 'v';// 输入日志类型，仅输出比自己级别高的日志，日志级别ERROR, WARN, INFO, DEBUG, VERBOSE  
    private static String MYLOG_PATH_SDCARD_DIR = Environment.getExternalStorageDirectory().getPath() + "/bqt";// 日志文件的路径  
    private static String MYLOGFILEName = "Log-";// 日志文件名称①  
    private static SimpleDateFormat logfile = new SimpleDateFormat("yyyy-MM-dd");// 日志文件名称②  
    //**************************************************************************************************************************  
    public static void w(String tag, String text) {  
        log(tag, text, 'w');  
    }  
    public static void e(String tag, String text) {  
        log(tag, text, 'e');  
    }  
    public static void d(String tag, String text) {  
        log(tag, text, 'd');  
    }  
    public static void i(String tag, String text) {  
        log(tag, text, 'i');  
    }  
    public static void v(String tag, String text) {  
        log(tag, text, 'v');  
    }  
    //**************************************************************************************************************************  
    //根据tag, msg和等级输出日志   
    private static void log(String tag, String msg, char level) {  
        if (MYLOG_SWITCH) {  
            if ('e' == level) Log.e(tag, msg);  
            else if ('w' == level && ('w' == MYLOG_TYPE || 'i' == MYLOG_TYPE || 'd' == MYLOG_TYPE || 'v' == MYLOG_TYPE)) Log.w(tag, msg);  
            else if ('i' == level && ('i' == MYLOG_TYPE || 'd' == MYLOG_TYPE || 'v' == MYLOG_TYPE)) Log.i(tag, msg);  
            else if ('d' == level && ('d' == MYLOG_TYPE || 'v' == MYLOG_TYPE)) Log.d(tag, msg);  
            else if ('v' == level && 'v' == MYLOG_TYPE) Log.v(tag, msg);  
            if (MYLOG_WRITE_TO_FILE) writeLogtoFile(String.valueOf(level), tag, msg);  
        }  
    }  
    //将日志写入文件，文件名格式为MYLOGFILEName + logfile.format(nowtime) + ".txt"，如【Log-2015-12-25.txt】  
    private static void writeLogtoFile(String mylogtype, String tag, String text) {  
        Date nowtime = new Date();  
        SimpleDateFormat myLogSdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  
        String myLogs = myLogSdf.format(nowtime) + "    " + mylogtype + "    " + tag + "    " + text;  
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) return;//判断sdcard是否插入  
        File fileDir = new File(MYLOG_PATH_SDCARD_DIR);  
        if (!fileDir.exists()) fileDir.mkdirs();//创建文件夹，这一步不能少  
        File file = new File(fileDir, MYLOGFILEName + logfile.format(nowtime) + ".txt");  
        try {  
            FileWriter filerWriter = new FileWriter(file, true);//true代表在文件中原来的数据后面添加，不进行覆盖  
            BufferedWriter bufWriter = new BufferedWriter(filerWriter);  
            bufWriter.write(myLogs);  
            bufWriter.newLine();  
            bufWriter.close();  
        } catch (IOException e) {  
            e.printStackTrace();  
        }  
    }  
}  
```  
  
2017-7-11  
