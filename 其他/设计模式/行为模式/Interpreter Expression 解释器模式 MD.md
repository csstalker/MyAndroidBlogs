| Markdown版本笔记 | 我的GitHub首页 | 我的博客 | 我的微信 | 我的邮箱 |  
| :------------: | :------------: | :------------: | :------------: | :------------: |  
| [MyAndroidBlogs][Markdown] | [baiqiantao][GitHub] | [baiqiantao][博客] | bqt20094 | baiqiantao@sina.com |  
  
[Markdown]:https://github.com/baiqiantao/MyAndroidBlogs  
[GitHub]:https://github.com/baiqiantao  
[博客]:http://www.cnblogs.com/baiqiantao/  
  
  
***  
目录  
===  

- [解释器模式](#解释器模式)
	- [简介](#简介)
	- [案例](#案例)
		- [环境角色](#环境角色)
		- [抽象表达式角色](#抽象表达式角色)
		- [终结符表达式角色](#终结符表达式角色)
		- [非终结符表达式角色](#非终结符表达式角色)
		- [使用演示](#使用演示)
	- [案例二](#案例二)
  
# 解释器模式  
  
## 简介  
  
Interpreter模式也叫解释器模式，是行为模式之一，它是一种特殊的设计模式，它建立一个`解释器`，对于特定的计算机程序设计语言，用来`解释预先定义的文法`。  
  
应用环境：  
- 如果一种特定类型的问题发生的频率足够高，那么可能就值得将该问题的各个实例表述为一个简单语言中的句子。这样就可以构建一个解释器，该解释器`通过解释这些句子来解决该问题`。而且当文法简单、效率不是关键问题的时候效果最好。  
- 当有一个语言需要解释执行，并且你可将该语言中的句子表示为一个抽象语法树，可以使用解释器模式。  
  
角色：  
- 抽象表达式角色(AbstractExpression): 声明一个抽象的解释操作，这个接口为所有具体表达式角色都要实现的  
- 终结符表达式角色(TerminalExpression): 实现与文法中的元素相关联的解释操作，通常一个解释器模式中只有一个终结符表达式，但有多个实例对应不同的终结符  
- 非终结符表达式角色(NonterminalExpression): 文法中的每条规则对应于一个非终结表达式，非终结表达式根据逻辑的复杂程度而增加，原则上每个文法规则都对应一个非终结符表达式  
- 环境角色(Context): 包含解释器之外的一些全局信息  
  
优点：  
- 解释器是一个简单语法分析工具，它最显著的优点就是【扩展性】，修改语法规则只要修改相应的【非终结符表达式】就可以了，若扩展语法，则只要增加【非终结符类】就可以了。  
  
缺点：  
- 解释器模式会引起【类膨胀】，每个语法都要产生一个非终结符表达式，语法规则比较复杂时，可能产生大量的类文件，难以维护  
- 解释器模式采用【递归调用】方法，它导致调试非常复杂  
- 解释器由于使用了大量的循环和递归，所以当用于解析复杂、冗长的语法时，【效率】是难以忍受的  
  
注意事项：  
尽量不要在重要模块中使用解释器模式，因为维护困难。在项目中，可以使用shell,JRuby,Groovy等`脚本语言来代替解释器模式`。  
  
作用：用一组类代表某一规则  
  
这个模式通常定义了一个语言的语法，然后解析相应语法的语句。  
```java  
java.util.Pattern  
java.text.Format  
```  
  
## 案例  
  
### 环境角色  
```java  
class Context {  
    private Map<String, Integer> valueMap = new HashMap<String, Integer>();  
    public void addValue(String key, int value) {  
        valueMap.put(key, value);  
    }  
    public int getValue(String key) {  
        return valueMap.get(key);  
    }  
}  
```  
  
### 抽象表达式角色  
声明一个抽象的解释操作，这个接口为所有具体表达式角色都要实现的  
```java  
abstract class AbstractExpression {  
    public abstract int interpreter(Context context);  
}  
```  
  
### 终结符表达式角色  
实现与文法中的元素相关联的解释操作，通常一个解释器模式中只有一个终结符表达式，但有多个实例对应不同的终结符  
Terminal 终结符,末期的,晚期的。终结符是语言中用到的基本元素，一般不能再被分解  
```java  
class TerminalExpression extends AbstractExpression {  
    private int i;  
    public TerminalExpression(int i) {  
        this.i = i;  
    }  
    @Override  
    public int interpreter(Context context) {//不进行任何操作  
        return i;  
    }  
}  
```  
  
### 非终结符表达式角色  
文法中的每条规则对应于一个非终结表达式，非终结表达式根据逻辑的复杂程度而增加  
  
加法操作  
```java  
class AddNTExpression extends AbstractExpression {  
    private AbstractExpression left;  
    private AbstractExpression right;  
    public AddNTExpression(AbstractExpression left, AbstractExpression right) {  
        this.left = left;  
        this.right = right;  
    }  
    @Override  
    public int interpreter(Context context) {  
        return left.interpreter(context) + right.interpreter(context);  
    }  
}  
```  
  
减法操作  
```java  
class SubtractNTExpression extends AbstractExpression {  
    private AbstractExpression left;  
    private AbstractExpression right;  
    public SubtractNTExpression(AbstractExpression left, AbstractExpression right) {  
        this.left = left;  
        this.right = right;  
    }  
    @Override  
    public int interpreter(Context context) {  
        return left.interpreter(context) - right.interpreter(context);  
    }  
}  
```  
  
乘法操作  
```java  
class MultiplyNTExpression extends AbstractExpression {  
    private AbstractExpression left;  
    private AbstractExpression right;  
    public MultiplyNTExpression(AbstractExpression left, AbstractExpression right) {  
        this.left = left;  
        this.right = right;  
    }  
    @Override  
    public int interpreter(Context context) {  
        return left.interpreter(context) * right.interpreter(context);  
    }  
}  
```  
  
除法操作  
```java  
class DivisionNTExpression extends AbstractExpression {  
    private AbstractExpression left;  
    private AbstractExpression right;  
    public DivisionNTExpression(AbstractExpression left, AbstractExpression right) {  
        this.left = left;  
        this.right = right;  
    }  
    @Override  
    public int interpreter(Context context) {  
        int value = right.interpreter(context);  
        if (value != 0) return left.interpreter(context) / value;  
        return -1111;  
    }  
}  
```  
  
### 使用演示  
```java  
public class Test {  
    public static void main(String[] args) {  
        //计算(7*8)/(7-8+2)的值  
        Context context = new Context();  
        context.addValue("a", 7);  
        context.addValue("b", 8);  
        context.addValue("c", 2);  
  
        AbstractExpression multiplyValue = new MultiplyNTExpression(new TerminalExpression(context.getValue("a")), new TerminalExpression(context.getValue("b")));//计算a*b  
        AbstractExpression subtractValue = new SubtractNTExpression(new TerminalExpression(context.getValue("a")), new TerminalExpression(context.getValue("b")));//计算a-b  
  
        AbstractExpression addValue = new AddNTExpression(subtractValue, new TerminalExpression(context.getValue("c")));//计算(a-b)+c  
  
        AbstractExpression divisionValue = new DivisionNTExpression(multiplyValue, addValue);//计算(a*b)/(a-b+c)  
  
        System.out.println(divisionValue.interpreter(context));  
    }  
}  
```  
  
## 案例二  
  
创建一个表达式接口  
```java  
public interface Expression { //表达式接口  
   boolean interpret(String context); //解释指定的内容  
}  
```  
  
创建实现了上述接口的实体类  
```java  
public class TerminalExpression implements Expression {  
   private String data;  
   public TerminalExpression(String data){  
      this.data = data;  
   }  
   @Override  
   public boolean interpret(String context) {  
      if(context.contains(data)){ //包含  
         return true;  
      }  
      return false;  
   }  
}  
```  
  
```java  
public class OrExpression implements Expression {  
  
   private Expression expr1 = null;  
   private Expression expr2 = null;  
  
   public OrExpression(Expression expr1, Expression expr2) {  
      this.expr1 = expr1;  
      this.expr2 = expr2;  
   }  
  
   @Override  
   public boolean interpret(String context) {  
      return expr1.interpret(context) || expr2.interpret(context); //两个表达式是否有一个能解释指定内容  
   }  
}  
```  
  
```java  
public class AndExpression implements Expression {  
  
   private Expression expr1 = null;  
   private Expression expr2 = null;  
  
   public AndExpression(Expression expr1, Expression expr2) {  
      this.expr1 = expr1;  
      this.expr2 = expr2;  
   }  
  
   @Override  
   public boolean interpret(String context) {  
      return expr1.interpret(context) && expr2.interpret(context); //两个表达式是否都能解释指定内容  
   }  
}  
```  
  
测试  
```java  
public class Test {  
    public static void main(String[] args) {  
        //使用 Expression 类来创建规则，并解析它们。  
        Expression robert = new TerminalExpression("Robert");  
        Expression john = new TerminalExpression("John");  
        Expression or = new OrExpression(robert, john);  
        System.out.println(or.interpret("John")); //true  
          
        Expression julie = new TerminalExpression("Julie");  
        Expression married = new TerminalExpression("Married");  
        Expression and = new AndExpression(julie, married);  
        System.out.println(and.interpret("Married Julie")); //true  
    }  
}  
```  
  
2016-08-24  
