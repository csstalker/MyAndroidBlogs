| Markdown版本笔记 | 我的GitHub首页 | 我的博客 | 我的微信 | 我的邮箱 |  
| :------------: | :------------: | :------------: | :------------: | :------------: |  
| [MyAndroidBlogs][Markdown] | [baiqiantao][GitHub] | [baiqiantao][博客] | bqt20094 | baiqiantao@sina.com |  
  
[Markdown]:https://github.com/baiqiantao/MyAndroidBlogs  
[GitHub]:https://github.com/baiqiantao  
[博客]:http://www.cnblogs.com/baiqiantao/  
  
  
***  
目录  
===  

- [建造者模式](#建造者模式)
	- [简介](#简介)
	- [案例](#案例)
  
# 建造者模式  
## 简介   
建造者模式的概念：将一个复杂的对象的构建与它的表示分离，使得同样的构建过程可以有不同的表示。  
大概的意思，就是一套的构建过程可以有不同的产品（表示）出来。这些产品（表示）都按照这一套的构建过程被生产出来。  
  
建造者模式通常包括以下这几个角色：  
- Builder：给出一个抽象接口，规范建造者对于生产的产品的各个组成部分的建造。这个接口只是定一个规范，不涉及具体的建造，具体的建造让继承于它的子类去实现。   
- **ConcreteBuilder**：实现builder接口，针对不同的业务逻辑，具体化各类型对象各个组成部分的建造，最后返回一个建造好的产品。   
- Director：导演，顾名思义，负责规范流程之用。在指导中不涉及产品的创建，只负责保证复杂对象各部分被创建或按某种`顺序`创建。   
- Product：要构造的复杂对象。   
  
定义了一个新的类来构建另一个类的实例，以简化复杂对象的创建。建造模式通常也使用方法链接来实现。  
```java  
java.lang.StringBuilder#append()  
```  
  
作用：  
- 将构造逻辑提到单独的类中   
- 分离类的构造逻辑和表现   
  
## 案例  
一个被构造的复杂对象。  
```java  
public class Productor {  
    public String head;  
    public String body;  
    public String foot;  
    //...  
}  
```  
  
抽象的Builder，为创建一个Product对象的各个部件指定抽象接口。  
```java  
public interface IBuilder {  
    void buildHead();  
    void buildBody();  
    void buildFoot();  
    Productor buildPerson();  
}  
```  
  
**具体的建造者**  
对于客户端而言，只需要关心具体的建造者，无需关心产品内部构建流程。  
如果客户端需要其他的复杂产品对象，只需要选择其他的建造者即可；也即，如果后续需要扩展，则只需要添加一个新的builder就行。  
  
```java  
//具体的建造者。实现Builder接口以构造和装配该产品的各个部件。  
public class ConcreteBuilder implements IBuilder {  
    Productor person;  
    public ConcreteBuilder() {  
        person = new Productor();  
    }  
    // 在此创建出部件  
    public void buildBody() {  
        System.out.println( "在这里根据客户端的需要建造男人的身体");  
        person.body = "身体";  
    }  
    public void buildFoot() {  
        System.out.println( "在这里根据客户端的需要建造男人的脚");  
        person.foot = "脚";  
    }  
    public void buildHead() {  
        System.out.println( "在这里根据客户端的需要建造男人的头");  
        person.head = "头";  
    }  
    // 返回复杂产品对象  
    public Productor buildPerson() {  
        return person;  
    }  
}  
```  
  
导演，负责流程规范，在导演类中可以注入建造者对象。  
```java  
public class Director {  
    private IBuilder builder;  
    public Director(IBuilder builder) {  
        this.builder = builder; // 构造方法中传递builder  
    }  
    // 这个方法用来规范流程，产品构建和组装方法  
    public Productor construct() {  
        builder.buildHead();  
        builder.buildBody();  
        builder.buildFoot();  
        return builder.buildPerson();  
    }  
}  
```  
  
客户端使用  
```java  
public class Test {  
    public static void main(String[] args) {  
        IBuilder builder = new ConcreteBuilder(); //具体的建造者  
        Director director = new Director(builder); //把建造者注入导演  
        Productor person = director.construct(); //导演负责流程把控  
        System.out.println(person.body);  
    }  
}  
```  
  
2016-10-31  
