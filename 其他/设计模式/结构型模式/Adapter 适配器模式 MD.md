| Markdown版本笔记 | 我的GitHub首页 | 我的博客 | 我的微信 | 我的邮箱 |  
| :------------: | :------------: | :------------: | :------------: | :------------: |  
| [MyAndroidBlogs][Markdown] | [baiqiantao][GitHub] | [baiqiantao][博客] | bqt20094 | baiqiantao@sina.com |  
  
[Markdown]:https://github.com/baiqiantao/MyAndroidBlogs  
[GitHub]:https://github.com/baiqiantao  
[博客]:http://www.cnblogs.com/baiqiantao/  
  
  
***  
目录  
===  

- [适配器模式](#适配器模式)
	- [简介](#简介)
	- [案例](#案例)
  
# 适配器模式  
## 简介   
适配器模式把一个类的接口变换成客户端所期待的另一种接口，从而使原本因接口不匹配而无法在一起工作的两个类能够在一起工作。  
  
用插板转换头做例子，笔记本电脑的插头一般都是三相的，即除了阳极、阴极外，还有一个地极。而有些地方的电源插座却只有两极，没有地极；或者虽然也是三相的，但是形状不同。所以，电源插座与笔记本电脑的电源插头，可能会由于不匹配，使得笔记本电脑无法使用。这时候一个转换器（适配器）就能解决此问题，而这正像是本模式所做的事情。  
  
模式中的角色  
- 目标接口（Target）：客户所期待的接口。目标可以是具体的或抽象的类，也可以是接口  
- 受改造者（Adaptee）：需要适配的类  
- 适配器（Adapter）：把原接口转换成目标接口  
  
实现方式  
- 类适配器，采用`继承`方式实现；继承需要适配的类，同时实现目标接口  
- 对象适配器，采用`组合`方式实现；传入需要适配的对象，同时实现目标接口  
  
适用情况  
- 你想使用一个已经存在的类，而它的接口不符合你的需求。   
- 你想创建一个可以复用的类，该类可以与其他不相关的类或不可预见的类（即那些接口可能不一定兼容的类）协同工作。   
  
JDK中体现：  
```java  
java.io.InputStreamReader(InputStream)  
java.io.OutputStreamWriter(OutputStream)  
java.util.Arrays#asList()  
```  
  
## 案例  
目标接口Target：客户所期待的接口  
```java  
public interface ITarget {  
    void request();  
}  
```  
  
受改造者Adaptee：需要适配的类（已有的接口）  
```java  
public class Adaptee {  
    public int num = 3;  
    public void action() {  
        System.out.println("受改造者Adaptee，插座孔数：" + num);  
    }  
}  
```  
  
适配器（转换头）：通过包装一个需要适配的对象，把原接口转换成目标接口  
类适配器，【继承】需要适配的类，同时实现目标接口  
```java  
public class ClassAdapter extends Adaptee implements ITarget {  
    @Override  
    public void request() {  
        this.num = 2;//改造被适配类Adaptee  
        this.action();//实际上是受改造者Adaptee在工作  
    }  
      
    @Override  
    public void action() {  
        System.out.println("对Adaptee进行改造后" + "，插座孔数：" + num + "，已经是我们需要的接口了");  
    }  
}  
```  
  
对象适配器：传入需要适配的对象，同时实现目标接口  
```java  
public class ObjectAdapter implements ITarget {  
    private Adaptee adaptee;  
    public ObjectAdapter(Adaptee adaptee) {  
        this.adaptee = adaptee;  
    }  
      
    @Override  
    public void request() {  
        adaptee.num = 2;//改造被适配对象adaptee  
        adaptee.action();//实际上是受改造者Adaptee在工作  
    }  
}  
```  
  
客户端使用  
```java  
public class Test {  
    public static void main(String[] args) {  
        ITarget target = new ClassAdapter(); //类适配器  
        target.request();  
          
        Adaptee adaptee=new Adaptee(); //需要适配的对象  
        ITarget target2 = new ObjectAdapter(adaptee); //通过对象适配器ObjectAdapter将需要适配的对象adaptee转换为目标对象ITarget  
        target2.request();//这样就可以调用目标对象的方法，其实际却是Adaptee在工作  
    }  
}  
```  
  
2016-10-31  
