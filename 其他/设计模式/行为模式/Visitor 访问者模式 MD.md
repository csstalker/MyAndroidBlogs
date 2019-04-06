| Markdown版本笔记 | 我的GitHub首页 | 我的博客 | 我的微信 | 我的邮箱 |  
| :------------: | :------------: | :------------: | :------------: | :------------: |  
| [MyAndroidBlogs][Markdown] | [baiqiantao][GitHub] | [baiqiantao][博客] | bqt20094 | baiqiantao@sina.com |  
  
[Markdown]:https://github.com/baiqiantao/MyAndroidBlogs  
[GitHub]:https://github.com/baiqiantao  
[博客]:http://www.cnblogs.com/baiqiantao/  
  
  
***  
目录  
===  

- [访问者模式](#访问者模式)
	- [简介](#简介)
	- [案例1](#案例1)
	- [案例2](#案例2)
		- [被访问者](#被访问者)
		- [访问者](#访问者)
		- [对象结构](#对象结构)
		- [客户端调用](#客户端调用)
		- [总结](#总结)
  
# 访问者模式  
## 简介  
  
访问者模式是设计模式中相对`比较复杂`的一个，项目中可能见得非常少。  
  
定义：封装某些作用于某种数据结构中各元素的操作，它可以在不改变数据结构的前提下，定义作用于这些元素的新的操作。  
- 表示一个作用于某对象结构中的各元素的操作。  
- 它使你可以在不改变各元素类的前提下定义作用于这些元素的新操作。  
- 从定义可以看出结构对象是使用访问者模式必备条件，而且这个结构对象必须存在遍历自身各个对象的方法。这便类似于Java语言当中的Collection概念了。  
  
> 一脸懵逼  
  
结构  
- 抽象访问者(Visitor): 为该对象结构中`具体元素角色`声明一个`访问操作`接口，`声明访问者可以访问哪些元素`，具体到程序中就是通过visit方法中的参数来定义哪些对象是可以被访问的  
- 具体访问者(Concrete Visitor): 实现Visitor声明的方法，它影响到访问者访问到一个类后该干什么，要做什么事情  
- 抽象元素(Element): 定义一个接受访问方法accept()，`通过accept方法中的参数定义接受哪一类访问者访问`  
- 具体元素(Concrete Element): 实现抽象元素类所声明的accept方法，通常实现都是`visitor.visit(this)`  
- 对象结构(Object Structure): 提供一个高层接口以允许访问者访问它的元素  
  
特点  
- 访问者模式把数据结构和作用于结构上的操作解耦合，使得操作集合可相对自由地演化。  
- 访问者模式适用于`数据结构相对稳定、算法又易变化的系统`。因为访问者模式使得`算法操作增加变得容易`。若系统数据结构对象易于变化，经常有新的数据对象增加进来，则不适合使用访问者模式。  
- 优点是`增加操作很容易`。如果操作的逻辑改变，我们只需要增加或改变访问者的实现就够了，而不用去修改其他所有的商品类。  
- 另一个优点是，添加新类别的商品到系统变得容易，只需要改变一下访问者接口以及其实现，已经存在的商品类别不会被干扰影响。  
- 缺点是`增加新的数据结构很困难`。  
- 另一个缺点是，如果访问者接口的实现太多，系统的扩展性就会下降。  
  
## 案例1  
  
元素角色  
```java  
public interface IElement {  
    accept(IVisitor visitor);  
}  
```  
  
具体元素角色  
```java  
class StringElement implements IElement {  
    public String name;  
    public String stringValue;  
    public StringElement(String name, String stringValue) {  
        this.name = name;  
        this.stringValue = stringValue;  
    }  
    @Override  
    public void accept(IVisitor visitor) {  
        visitor.visit(this);//固定写法  
    }  
}  
```  
  
```java  
class FloatElement implements IElement {  
    public String name;  
    public float floatValue;  
    public FloatElement(String name, float floatValue) {  
        this.name = name;  
        this.floatValue = floatValue;  
    }  
    @Override  
    public void accept(IVisitor visitor) {  
        visitor.visit(this);  
    }  
}  
```  
  
抽象访问者  
```java  
public interface IVisitor {  
    visit(StringElement stringE); //声明访问者可以访问哪些元素，几个具体元素角色类就定义几个方法  
    visit(FloatElement floatE);  
}  
```  
  
具体访问者  
```java  
class ConcreteVisitor implements IVisitor {  
    public String name;  
    public ConcreteVisitor(String name) {  
        this.name = name;  
    }  
    @Override  
    public void visit(StringElement element) {  
        System.out.println("访问者 " + name + "访问元素 StringElement，元素的值为：" + element.stringValue);  
    }  
    @Override  
    public void visit(FloatElement element) {  
        System.out.println("访问者 " + name + "访问元素 FloatElement，元素的值为：" + element.floatValue);  
    }  
}  
```  
  
> 扩展、修改访问者是非常容易的，只需要增加或改变访问者的实现就够了，而不用去修改其他所有的元素类。  
  
对象结构角色  
为了方便访问多个元素，创建一个对象结构，在其内部管理元素集合，并且可以迭代这些元素供访问者访问  
```java  
public class ObjectStructure {  
    private List<IElement> elements = new ArrayList<IElement>();//元素集合  
  
    public ObjectStructure() {  
        elements.add(new StringElement("字符串11", "abc"));  
        elements.add(new StringElement("字符串12", "abc"));  
        elements.add(new StringElement("字符串13", "abcdefg"));  
        elements.add(ew FloatElement("浮点数", 1.5f));  
    }  
      
    public void accept(IVisitor visitor) {  
        for (IElement e : elements) {  
            e.accept(visitor);  
        }  
    }  
}  
```  
      
```java  
public class Test {  
    public static void main(String[] args) {  
        IVisitor visitor = new ConcreteVisitor("包青天");  
          
        new StringElement("字符串", "abc").accept(visitor);  
        new FloatElement("浮点数", 1.5f).accept(visitor);  
  
        new ObjectStructure().accept(visitor);  
    }  
}  
```  
  
## 案例2  
就举公司的年终考核来说。假设一个公司的基层结构很稳定，就是工程师和经理。那么不同的高层来考核就要访问他们不同的东西。  
  
工程师和经理是被考核者，可以看成被访问者。  
CEO和CTO是考核者，可以看成访问者，他们的考核指标不一样：  
- CEO访问工程师和经理时，要获取他们的KPI作为考核依据  
- CTO访问工程师时要获取代码量作为考核依据，访问经理时要获取项目个数作为考核依据  
  
### 被访问者  
员工的基类  
```java  
public abstract class Staff {  
    public String name;  
    public int kpi;  
  
    public Staff(String name) {  
        this.name = name;  
        kpi = new Random().nextInt(10);  
    }  
  
    public abstract void accept(Visitor visitor);//定义一个抽象的受访问方法，通传参来确定是哪个Visitor  
}  
```  
  
工程师  
```java  
public class Engineer extends Staff {  
  
    public Engineer(String name) {  
        super(name);  
    }  
  
    @Override  
    public void accept(Visitor visitor) {  
        visitor.visit(this); //实现受访问方法，里面调用访问者的访问方法  
    }  
  
    public int getCodeLines(){  
        return new Random().nextInt(1000000);  
    }  
}  
```  
  
经理  
```java  
public class Manager extends Staff {  
    public Manager(String name) {  
        super(name);  
    }  
  
    @Override  
    public void accept(Visitor visitor) {  
        visitor.visit(this);  
    }  
  
    public int getProducts(){  
        return new Random().nextInt(10);  
    }  
}  
```  
  
### 访问者  
访问者的基类，为每一个被访问者都提供可一个访问方法。  
```java  
public interface Visitor {  
    void visit(Engineer engineer); //访问Engineer  
    void visit(Manager manager); //访问Manager  
}  
```  
  
CTO  
实现每一个访问元素的方法，访问不同的元素进行不同的操作  
```java  
public class CTO implements Visitor {  
    @Override  
    public void visit(Engineer engineer) {  
        System.out.println("我CTO考察工程师"+engineer.name+"的代码量是"+engineer.getCodeLines());  
    }  
  
    @Override  
    public void visit(Manager manager) {  
        System.out.println("我CTO考察经理"+manager.name+"的产品量是"+manager.getProducts());  
    }  
}  
```  
  
CEO  
```java  
public class CEO implements Visitor {  
    @Override  
    public void visit(Engineer engineer) {  
        System.out.println("我CEO考察工程师"+engineer.name+"的KPI是"+engineer.kpi);  
    }  
  
    @Override  
    public void visit(Manager manager) {  
        System.out.println("我CEO考察经理"+manager.name+"的KPI是"+manager.kpi);  
    }  
}  
```  
  
### 对象结构  
内部通过遍历调用每一个元素的接受访问方法来生成报表。  
```java  
public class Report {  
    List<Staff> list = new ArrayList<>();  
  
    public Report() {  
        list.add(new Engineer("小王"));  
        list.add(new Engineer("大王"));  
        list.add(new Engineer("老王"));  
        list.add(new Manager("小张"));  
        list.add(new Manager("大张"));  
        list.add(new Manager("老张"));  
    }  
  
    public void showReport(Visitor visitor){  
        for (Staff staff:list) {  
            staff.accept(visitor);  
        }  
    }  
}  
```  
  
### 客户端调用  
```java  
public class Client {  
    public static void main(String[] args) {  
        Report report = new Report();  
        report.showReport(new CTO());  
        System.out.println("---------");  
        report.showReport(new CEO());  
    }  
}  
```  
  
### 总结  
到这里能感觉到访问者模式最大的好处就是：`当被访问者是固定的时候，拓展访问者非常容易`。  
  
比如现在COO、CMO、CAO也要对员工进行考核，那么只需增加相应的Visitor实现类，实现具体的访问方法就可以了，其他地方都不用修改。  
  
优点  
- 角色分离，各司其职，符合单一职责原则  
- 具有优秀的拓展性  
- 使数据结构和作用于结构上的操作解耦，使操作集合可以独立变化。  
  
缺点  
- 具体元素对访问者公布细节，违反了迪米特原则。  
- 具体元素修改的成本太大。  
- 违反了依赖倒置原则，为了达到区别对待依赖了具体而不是抽象。   
  
2016-03-21  
