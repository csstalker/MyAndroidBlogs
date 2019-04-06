| Markdown版本笔记 | 我的GitHub首页 | 我的博客 | 我的微信 | 我的邮箱 |  
| :------------: | :------------: | :------------: | :------------: | :------------: |  
| [MyAndroidBlogs][Markdown] | [baiqiantao][GitHub] | [baiqiantao][博客] | bqt20094 | baiqiantao@sina.com |  
  
[Markdown]:https://github.com/baiqiantao/MyAndroidBlogs  
[GitHub]:https://github.com/baiqiantao  
[博客]:http://www.cnblogs.com/baiqiantao/  
  
  
***  
目录  
===  

- [装饰模式](#装饰模式)
	- [简介](#简介)
	- [案例](#案例)
  
# 装饰模式  
## 简介  
  
装饰模式以对客户端透明的方式【扩展】对象的功能，客户端并不会觉得对象在装饰前和装饰后有什么不同。  
> PS：对客户端透明的意思是，因为装饰类和原始类实现了相同的接口，所以你只需像使用原始类一样使用装饰类即可。  
> 换句话说，你不需要学习新知识，就能完全知道装饰类有什么用  
  
装饰模式是【继承】关系的一个替代方案。  
  
若`只为增加功能`而使用继承，当基类较多时会导致继承体系越来越臃肿，装饰模式可以在不创造更多子类的情况下，将对象的功能加以扩展。   
  
装饰对象和真实对象有相同的接口，这样客户端对象就可以以和真实对象相同的方式和装饰对象交互。  
装饰对象包含一个真实对象的引用，它接收所有来自客户端的请求，并把这些请求`转发`给真实的对象，并在转发这些请求之前或之后可以附加一些功能。  
  
角色  
- 抽象构件角色（Component）：给出一个抽象接口，以规范准备接收附加责任的对象。   
- 具体构件角色（Concrete Component）：定义将要接收附加责任的类。   
- 装饰角色（Decorator）：持有一个构件（Component）对象的引用，并定义一个与抽象构件接口一致的接口。   
- 具体装饰角色（Concrete Decorator）：负责给构件对象“贴上”附加的责任。   
  
优点 ：  
- 解决【类膨胀】【类爆炸】【继承体系臃肿】的问题   
- 扩展性非常好   
- 比继承更灵活   
  
## 案例  
  
原始接口只具有work功能：  
```java  
interface IWork {  
    public void work();  
}  
```  
  
此接口有很多不同类型的实现类：  
```java  
class People implements IWork {  
    @Override  
    public void work() {  
        System.out.println("人开始干活.....................");  
    }  
}  
  
class Robot implements IWork {  
    @Override  
    public void work() {  
        System.out.println("机器人开始干活.....................");  
    }  
}  
  
class AI implements IWork {  
    @Override  
    public void work() {  
        System.out.println("AI开始干活.....................");  
    }  
}  
```  
  
如果使用继承来扩展原始类的功能：  
```java  
class PeopleV2 extends People {  
    @Override  
    public void work() {  
        System.out.println("先检查零件是否正常");//增强的功能  
        super.work();  
        System.out.println("干完活收拾下房间\n");//增强的功能  
    }  
}  
  
class RobotV2 extends Robot {  
    @Override  
    public void work() {  
        System.out.println("先检查零件是否正常");//增强的功能  
        super.work();  
        System.out.println("干完活收拾下房间\n");//增强的功能  
    }  
}  
  
class AIV2 extends AI {  
    @Override  
    public void work() {  
        System.out.println("先检查零件是否正常");//增强的功能  
        super.work();  
        System.out.println("干完活收拾下房间\n");//增强的功能  
    }  
}  
```  
  
可以看到，为了在需要的时候（注，不能直接更改原始类的work()方法，因为这些增强功能不是任何时候都需要的）增强原始类的功能，需要`为每IWork接口的所有实现类都添加一个子类`，在子类中实现对原始类功能的增强。当IWork接口的实现类很多时，会造成类体系迅速膨胀！  
  
如果使用装饰模式来扩展原始类的功能，则只需定义一个装饰角色类Decorator：  
```java  
class Decorator implements IWork {  
    private IWork work;  
    public Decorator(IWork work) {  
        this.work = work;  
    }  
      
    @Override  
    public void work() {  
        System.out.println("先检查零件是否正常");//增强的功能  
        work.work();  
        System.out.println("干完活收拾下房间\n");//增强的功能  
    }  
}  
```  
  
动态地给对象添加一些额外的职责。就增加功能来说，Decorator模式相比生成子类更为灵活。  
  
客户端调用  
```java  
public class Test {  
    public static void main(String args[]) {  
        IWork work = new People();  
        Decorator decorator = new Decorator(work);  
        decorator.work();  
    }  
}  
```  
  
2016-10-31  
