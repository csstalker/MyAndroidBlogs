| Markdown版本笔记 | 我的GitHub首页 | 我的博客 | 我的微信 | 我的邮箱 |  
| :------------: | :------------: | :------------: | :------------: | :------------: |  
| [MyAndroidBlogs][Markdown] | [baiqiantao][GitHub] | [baiqiantao][博客] | bqt20094 | baiqiantao@sina.com |  
  
[Markdown]:https://github.com/baiqiantao/MyAndroidBlogs  
[GitHub]:https://github.com/baiqiantao  
[博客]:http://www.cnblogs.com/baiqiantao/  
  
异常 try catch finally return 执行关系 MD  
***  
目录  
===  

- [探讨finally语句的执行与return的关系](#探讨finally语句的执行与return的关系)
  
# 探讨finally语句的执行与return的关系  
Java异常捕获机制`try...catch...finally`块中的`finally`语句是不是一定会被执行？不一定，至少有两种情况下finally语句是不会被执行的：  
- try语句没有被执行到，如在try语句之前就返回了，这样finally语句就不会执行，这也说明了finally语句被执行的必要而非充分条件是：相应的try语句一定被执行到。  
- 在try块中有`System.exit(0)`这样的语句，`System.exit(0)`是终止Java虚拟机JVM的，连JVM都停止了，所有都结束了，当然finally语句也不会被执行到。  
  
**1、finally语句是在try的return语句执行之后，return返回之前执行的**  
测试案例：  
```java  
public class TestFinally {  
    public static void main(String[] args) {  
        System.out.println(test());  
    }  
  
    public static String test() {  
        try {  
            System.out.println("try block");  
            if (new Random().nextBoolean()) {  
                return "直接返回";  
            } else {  
                return test2();  
            }  
        } finally {  
            System.out.println("finally block");  
        }  
    }  
  
    public static String test2() {  
        System.out.println("return statement");  
        return "调用方法返回";  
    }  
}  
```  
  
运行结果：  
```  
try block  
finally block  
直接返回  
```  
或  
```java  
try block  
return statement  
finally block  
调用方法返回  
```  
  
说明try中的return语句先执行了，但并没有立即返回，而是等到finally执行结束后再返回。  
  
这里大家可能会想：如果finally里也有return语句，那么是不是就直接返回了，try中的return就不能返回了？看下面。  
  
**2、finally块中的return语句会覆盖try块中的return返回**  
  
```java  
public class TestFinally {  
    public static void main(String[] args) {  
        System.out.println(test());  
    }  
  
    public static String test() {  
        try {  
            System.out.println("try block");  
            return "在try中返回";  
        } finally {  
            System.out.println("finally block");  
            return "在finally中返回";  
        }  
        // return "finally外面的return就变成不可到达语句，需要注释掉否则编译器报错";  
    }  
}  
```  
  
运行结果：  
```java  
try block  
finally block  
在finally中返回  
```  
  
这说明finally里的return直接返回了，就不管try中是否还有返回语句。  
  
这里还有个小细节需要注意，finally里加上return过后，finally外面的return b就变成不可到达语句了，也就是永远不能被执行到，所以需要注释掉否则编译器报错。  
  
**3、如果finally语句中没有return语句覆盖返回值，那么原来的返回值可能因为finally里的修改而改变，也可能不变**  
测试用例：  
```java  
public class TestFinally {  
    public static void main(String[] args) {  
        System.out.println(test());  
    }  
  
    public static int test() {  
        int b = 20;  
        try {  
            System.out.println("try block");  
            return b += 80;  
        } finally {  
            b += 10;  
            System.out.println("finally block");  
        }  
    }  
}  
```  
  
运行结果：  
```java  
try block  
finally block  
100  
```  
  
测试用例2：  
```java  
public class TestFinally {  
    public static void main(String[] args) {  
        System.out.println(test());  
    }  
  
    public static List<Integer> test() {  
        List<Integer> list = new ArrayList<Integer>();  
        list.add(10086);  
        try {  
            System.out.println("try block");  
            return list;  
        } finally {  
            list.add(10088);  
            System.out.println("finally block");  
        }  
    }  
}  
```  
运行结果：  
```java  
try block  
finally block  
[10086, 10088]  
```  
  
这其实就是Java到底是传值还是传址的问题了，简单来说就是：Java中只有传值没有传址。  
  
这里大家可能又要想：是不是每次返回的一定是try中的return语句呢？那么finally外的return不是一点作用没吗？请看下面  
  
**4、try块里的return语句在异常的情况下不会被执行，这样具体返回哪个看情况**  
```java  
public class TestFinally {  
    public static void main(String[] args) {  
        System.out.println(test());  
    }  
  
    public static int test() {  
        int b = 0;  
        try {  
            System.out.println("try block");  
            b = b / 0;  
            return b += 1;  
        } catch (Exception e) {  
            b += 10;  
            System.out.println("catch block");  
        } finally {  
            b += 100;  
            System.out.println("finally block");  
        }  
        return b;  
    }  
}  
```  
  
运行结果是：  
```java  
try block  
catch block  
finally block  
110  
```  
  
这里因 为在return之前发生了异常，所以`try中的return不会被执行到`，而是接着执行捕获异常的 catch 语句和最终的 finally 语句，此时两者对b的修改都影响了最终的返回值，这时最后的 return b 就起到作用了。  
  
这里大家可能又有疑问：如果catch中有return语句呢？当然只有在异常的情况下才有可能会执行，那么是在 finally 之前就返回吗？看下面。  
  
**5、当发生异常后，catch中的return执行情况与未发生异常时try中return的执行情况完全一样**  
```java  
public class TestFinally {  
    public static void main(String[] args) {  
        System.out.println(test());  
    }  
  
    public static int test() {  
        int b = 0;  
        try {  
            System.out.println("try block");  
            b = b / 0;  
            return b += 1;  
        } catch (Exception e) {  
            b += 10;  
            System.out.println("catch block");  
            return 10086;  
        } finally {  
            b += 100;  
            System.out.println("finally block");  
        }  
        //return b;  
    }  
}  
```  
  
运行结果：  
```java  
try block  
catch block  
finally block  
10086  
```  
  
说明了发生异常后，catch中的return语句先执行，确定了返回值后再去执行finally块，执行完了catch再返回，也就是说情况与try中的return语句执行完全一样。  
  
**总结：**  
- finally块的语句在try或catch中的return语句执行之后返回之前执行  
- 且finally里的修改语句可能影响也可能不影响try或catch中return已经确定的返回值  
- 若finally里也有return语句则覆盖try或catch中的return语句直接返回  
  
2019-3-4  
