| Markdown版本笔记 | 我的GitHub首页 | 我的博客 | 我的微信 | 我的邮箱 |  
| :------------: | :------------: | :------------: | :------------: | :------------: |  
| [MyAndroidBlogs][Markdown] | [baiqiantao][GitHub] | [baiqiantao][博客] | bqt20094 | baiqiantao@sina.com |  
  
[Markdown]:https://github.com/baiqiantao/MyAndroidBlogs  
[GitHub]:https://github.com/baiqiantao  
[博客]:http://www.cnblogs.com/baiqiantao/  
  
  
***  
目录  
===  

- [策略模式](#策略模式)
	- [简介](#简介)
	- [案例](#案例)
  
# 策略模式  
## 简介  
  
策略模式定义了一系列`算法`，并将每一个算法封装起来，而且使它们可以`相互替换`。策略模式让算法独立于使用它的客户端而独立变化  
  
使用这个模式来将一组`算法`封装成一系列对象，通过传递这些对象可以灵活的改变程序的功能。  
  
使用场景  
- 针对同一类型问题的多种处理方式，仅仅是具体行为有差别时  
- 需要安全地封装多种同一类型的操作时  
- 出现同一抽象类有多个子类，而又需要通过`if-else`或者`switch-case`来选择具体的子类时  
  
使用例子：  
Android中属性动画的`时间差值器`分为线性差值器、加速减速差值器等，这些差值器里面就用到了策略模式来隔离不同的动画速率计算算法。  
  
---  
  
在软件开发中也常常遇到类似的情况，实现某一个功能有多种`算法`或者策略，我们可以根据环境或者条件的不同选择不同的算法或者策略来完成该功能，如查找、排序等。  
  
一种常用的方法是硬编码，在一个类中如需要提供多种查找算法，可以将这些算法写到一个类中，在该类中提供多个方法，每一个方法对应一个具体的查找算法；当然也可以将这些查找算法封装在一个统一的方法中，通过`if…else`或者`switch`等条件判断语句来进行选择。  
  
这两种实现方法我们都可以称之为硬编码，如果需要`增加`一种新的查找算法，需要修改封装算法类的源代码。更换查找算法，也需要修改客户端调用代码。  
  
在这个算法类中封装了大量查找算法，该类代码较复杂，维护较为困难。  
  
如果我们将这些策略包含在客户端，这种做法更不可取，将导致客户端程序庞大而且难以维护，如果存在大量可供选择的算法时问题将变得更加严重。  
  
---  
  
模式的组成  
- 环境类(Context)：用一个ConcreteStrategy对象来配置。维护一个对Strategy对象的引用。可定义一个接口来让Strategy访问它的数据。  
- 抽象策略类(Strategy)：定义所有支持的算法的公共接口。Context使用这个接口来调用某ConcreteStrategy定义的算法。  
- 具体策略类(ConcreteStrategy)：以Strategy接口实现某具体算法。  
  
总结  
- 策略模式主要用来分离算法，在相同的行为抽象下有不同的具体实现策略。  
- 策略模式很好地展示了开闭原则，也就是定义抽象，注入不同的实现，从而达到很好的扩展性  
- 当我们在实现一个功能时遇到很多if-else或是switch-case的时候就可以考虑下这里是不是可以用策略模式了  
  
## 案例  
  
下面我们以计算不同交通工具的车费来简单看看策略模式的实现  
  
首先抽象出计算车费的操作，因为不同的策略最后都会计算车费并返回结果  
```java  
public interface CalculateStragety {  
    int calculatePrice(int km); //根据公里数计算价格  
}  
```  
  
然后我们分别实现具体的策略，比如计算公交车的车费的策略  
```java  
public class BusStragety implements CalculateStragety {  
  
    @Override  
    public int calculatePrice(int km) {  
        return 2; //省略具体的算法  
    }  
}  
```  
  
再加一个出租车的车费计算策略  
```java  
public class TaxiStragety implements CalculateStragety {  
    @Override  
    public int calculatePrice(int km) {  
        return 10 + km * 2; //省略具体的算法  
    }  
}  
```  
  
然后我们看看怎么根据需要注入不同的策略，并把具体的计算委托给注入的策略  
```java  
public class TrafficCalculator {  
    private CalculateStragety mCalculateStragety;  
  
    public void setCalculateStragety(CalculateStragety calculateStragety) {  
        mCalculateStragety = calculateStragety; //根据需要注入相应的策略  
    }  
  
    public int calculatePrice(int km) {  
       return mCalculateStragety.calculatePrice(km); //把具体的计算委托给注入的策略  
    }  
}  
```  
  
测试  
```java  
class Test {  
    public static void main(String[] args) {  
        TrafficCalculator trafficCalculator = new TrafficCalculator();  
        trafficCalculator.setCalculateStragety(new BusStragety());  
        trafficCalculator.calculatePrice(66);  
    }  
}  
```  
  
在上面的例子中我们通过在TrafficCalculator中动态注入计算公交车车费的策略来计算公交车费，当然我们也可以计算出租车费，只需要把注入的策略改为出租车车费计算策略就可以了。  
  
2016-04-21  
