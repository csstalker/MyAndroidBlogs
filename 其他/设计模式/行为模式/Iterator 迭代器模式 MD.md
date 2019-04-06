| Markdown版本笔记 | 我的GitHub首页 | 我的博客 | 我的微信 | 我的邮箱 |  
| :------------: | :------------: | :------------: | :------------: | :------------: |  
| [MyAndroidBlogs][Markdown] | [baiqiantao][GitHub] | [baiqiantao][博客] | bqt20094 | baiqiantao@sina.com |  
  
[Markdown]:https://github.com/baiqiantao/MyAndroidBlogs  
[GitHub]:https://github.com/baiqiantao  
[博客]:http://www.cnblogs.com/baiqiantao/  
  
  
***  
目录  
===  

- [迭代器模式](#迭代器模式)
	- [简介](#简介)
	- [案例](#案例)
  
# 迭代器模式  
  
## 简介  
  
Iterator模式是行为模式之一，它把对容器中包含的内部对象的访问【委让】给外部类，使用Iterator按顺序进行遍历访问。  
  
在程序设计中，经常有这种情况：需要从大量的数据集合中一个个地取出数据加以处理。Iterator模式就是为了有效地处理按顺序进行遍历访问的一种设计模式，简单地说，Iterator模式提供一种有效的方法，可以屏蔽聚集对象集合的容器类的实现细节，而能对容器内包含的对象元素按顺序进行有效的遍历访问。  
  
Iterator模式的应用场景可以归纳为满足以下几个条件：  
- 访问容器中包含的内部对象  
- 按顺序访问  
  
下面是一种比较经典的Iterator模式实现方案，该实现方案基于接口设计原则，设计了以下几个接口或类：  
- 迭代器接口Iterator：该接口必须定义实现迭代功能的最小定义方法集，比如提供`hasNext()`和`next()`方法。  
- 迭代器实现类：迭代器接口Iterator的实现类。可以根据具体情况加以实现。  
- 容器接口：定义基本功能以及提供类似Iterator `iterator()`的方法。  
- 容器实现类：容器接口的实现类。  
  
优点  
- 实现功能分离，简化容器接口。让容器只实现本身的基本功能，把迭代功能委让给外部类实现，符合类的设计原则。  
- 为容器或其子容器提供了一个统一接口，一方面方便调用；另一方面使得调用者不必关注迭代器的实现细节。  
  
## 案例  
定义一个自定义的集合的功能  
```java  
public interface MyList {  
    MyIterator iterator();//自定义迭代器  
    Object get(int index);  
    int getSize();  
    void add(Object obj);  
}  
```  
  
```java  
public class MyListImpl implements MyList {  
    private Object[] list;  
    private int index;  
    private int size;  
    public MyListImpl() {  
        index = 0;  
        size = 0;  
        list = new Object[100];  
    }  
    @Override  
    public MyIterator iterator() {  
        return new MyIteratorImpl(this); //将自身的引用传给了迭代器  
    }  
    @Override  
    public Object get(int index) {  
        if (index > size - 1) throw new RuntimeException("超出边界了");  
        return list[index];  
    }  
    @Override  
    public int getSize() {  
        return this.size;  
    }  
    @Override  
    public void add(Object obj) {  
        list[index++] = obj;  
        size++;  
    }  
}  
```  
  
迭代器。定义访问和遍历元素的接口  
```java  
public interface MyIterator {  
    Object next();  
    boolean hasNext();  
}  
```  
  
```java  
public class MyIteratorImpl implements MyIterator {  
    private MyList list;//自定义集合  
    private int index;  
    public MyIteratorImpl(MyList list) {  
        index = 0;  
        this.list = list;  
    }  
    @Override  
    public Object next() {  
        Object obj = list.get(index);  
        index++;  
        return obj;  
    }  
    @Override  
    public boolean hasNext() {  
        return index < list.getSize();  
    }  
}  
```  
  
适用性  
- 访问一个聚合对象的内容而无需暴露它的内部表示  
- 支持对聚合对象的多种遍历  
- 为遍历不同的聚合结构提供一个统一的接口(即支持多态迭代)  
  
```java  
public class Test {  
    public static void main(String[] args) {  
        MyList list = new MyListImpl();  
        list.add("a");  
        list.add("b");  
        list.add("c");  
  
        //第一种迭代方式  
        for (int i = 0; i < list.getSize(); i++) {  
            System.out.println(list.get(i));  
        }  
  
        //第二种迭代方式  
        MyIterator it = list.iterator();  
        while (it.hasNext()) {  
            System.out.println(it.next());  
        }  
    }  
}  
```  
  
2016-04-21  
