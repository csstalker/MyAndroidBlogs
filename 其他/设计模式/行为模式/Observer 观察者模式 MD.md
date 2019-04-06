| Markdown版本笔记 | 我的GitHub首页 | 我的博客 | 我的微信 | 我的邮箱 |  
| :------------: | :------------: | :------------: | :------------: | :------------: |  
| [MyAndroidBlogs][Markdown] | [baiqiantao][GitHub] | [baiqiantao][博客] | bqt20094 | baiqiantao@sina.com |  
  
[Markdown]:https://github.com/baiqiantao/MyAndroidBlogs  
[GitHub]:https://github.com/baiqiantao  
[博客]:http://www.cnblogs.com/baiqiantao/  
  
Observer 观察者模式 设计模式  
***  
目录  
===  

- [简介](#简介)
- [最简单的观察者模式](#最简单的观察者模式)
	- [抽象观察者](#抽象观察者)
	- [抽象主题](#抽象主题)
	- [具体观察者](#具体观察者)
	- [具体主题](#具体主题)
	- [演示案例](#演示案例)
- [观察者模式详细案例](#观察者模式详细案例)
	- [产品对象](#产品对象)
	- [抽象主题 Subject](#抽象主题-Subject)
	- [具体主题 ConcreteSubject](#具体主题-ConcreteSubject)
	- [抽象观察者 Observer](#抽象观察者-Observer)
	- [具体观察者 ConcreteObserver](#具体观察者-ConcreteObserver)
	- [演示](#演示)
- [利用系统提供的两个类](#利用系统提供的两个类)
	- [Observer 源码](#Observer-源码)
	- [Observable 源码](#Observable-源码)
	- [利用系统类演示观察者模式](#利用系统类演示观察者模式)
		- [被观察者](#被观察者)
		- [观察者](#观察者)
		- [演示](#演示)
  
# 简介   
观察者模式有时又被称为发布-订阅（`publish－Subscribe`）模式、模型-视图（`model－View`）模式、源-收听者(`Source-listener`)模式，或从属者模式。在此种模式中，一个【目标物件】管理所有相依于它的观察者物件，并且在它本身的状态改变时【主动】发出通知。这通常通过调用各观察者所提供的接口来实现。此种模式通常被用来实现`事件处理`系统。  
  
观察者模式定义了对象间的【一对多】关系，当一个对象的行为、状态发生变化时，所有订阅者能够收到相应的通知。  
  
观察者模式一般有2种，一种【推模式】，一个【拉模式】。推模式即当被订阅者对象发生改变时，会`主动`将变化的消息【推给】订阅者，而不考虑每个订阅者当时的处理能力；另一种是拉模式，即订阅者持有被观察者的实例，当被订阅者的行为发生改变时，拉模式会主动的根据引用【获取】变化的相关数据。  
  
观察者模式中涉及的角色  
- 抽象主题[Subject]：即被观察者ObServable  
- 具体主题[ConcreteSubject]：在具体主题内部状态改变时，通知观察者   
- 抽象观察者[Observer]：为所有具体观察者定义接口   
- 具体观察者[ConcreteObserver]：可以保存指向具体主题对象的引用  
  
使用场景 ：当一个系统中【某个】对象的改变需要同时改变其他【多个】对象，且它不知道具体有多少对象有待改变时候 使用。  
作用：通知对象状态改变 。  
  
# 最简单的观察者模式    
## 抽象观察者  
```Java    
interface Watcher {    
    void update(String str);    
}    
```    
  
## 抽象主题  
```Java    
interface Watched {    
    void addWatcher(Watcher watcher);    
    void removeWatcher(Watcher watcher);    
    void notifyWatchers(String str);    
}    
```    
  
## 具体观察者  
```Java    
class ConcreteWatcher implements Watcher {    
    @Override    
    public void update(String str) {    
        Log.i("bqt", "观察者收到被观察者的消息：" + str);    
    }    
}    
```    
  
## 具体主题  
```Java    
class ConcreteWatched implements Watched {    
    private List<Watcher> list = new ArrayList<>();    
  
    @Override    
    public void addWatcher(Watcher watcher) {    
        if (list.contains(watcher)) {    
            Log.i("bqt", "失败，被观察者已经注册过此观察者");    
        } else {    
            Log.i("bqt", "成功，被观察者成功注册了此观察者");    
            list.add(watcher);    
        }    
    }    
  
    @Override    
    public void removeWatcher(Watcher watcher) {    
        if (list.contains(watcher)) {    
            boolean success = list.remove(watcher);    
            Log.i("bqt", "此观察者解除注册观察者结果：" + (success ? "成功" : "失败"));    
        } else {    
            Log.i("bqt", "失败，此观察者并未注册到被观察者");    
        }    
    }    
  
    @Override    
    public void notifyWatchers(String str) {    
        for (Watcher watcher : list) {    
            watcher.update(str);    
        }    
    }    
}    
```    
  
## 演示案例   
```Java    
private Watcher watcher;    
private Watched watched;    
//...    
watcher = new ConcreteWatcher();    
watched = new ConcreteWatched();    
//...    
watched.addWatcher(watcher);    
watched.notifyWatchers("救命啊");    
watched.removeWatcher(watcher);    
```    
  
# 观察者模式详细案例  
## 产品对象  
发布者要发布什么给订阅者？订阅者要订阅什么？这中间需要定义一个产品对象：  
```java  
class Product {  
    public boolean changed = false;  
    public String message = "";  
    public int num = -1;  
}  
```  
  
## 抽象主题 Subject  
也叫抽象被观察者Observable。抽象主题把所有对观察者对象的引用保存在一个集合里，每个主题都可以有任意数量的观察者。抽象主题可以增加和删除观察者对象  
```java  
interface Subject {  
    public void attach(Observer observer); //注册观察者对象  
    public void detach(Observer observer); //移除/取消注册观察者对象  
    /**－－－－－－－－－上面两个是最基本的方法，下面的方法都不是必须的－－－－－－－－*/  
    public void nodifyObservers(); //通知所有注册的观察者对象  
    public void setProduct(Product product); //把通知的内容封装到一个对象中  
    public Product getProduct(); //获取产品对象  
}  
```  
  
## 具体主题 ConcreteSubject  
```java  
class ConcreteSubject implements Subject {  
    private List<Observer> list = new ArrayList<Observer>(); //用来保存注册的观察者对象  
    private Product product;  
      
    @Override  
    public void attach(Observer observer) {  
        list.add(observer);  
    }  
      
    @Override  
    public void detach(Observer observer) {  
        list.remove(observer);  
    }  
      
    @Override  
    public void nodifyObservers() {  
        if (product != null && product.changed) {  
            System.out.println("具体主题状态发生改变，于是通知观察者：" + product.message + "-" + product.num);  
            for (Observer observer : list) {  
                observer.update(product);  
            }  
        }  
    }  
      
    @Override  
    public void setProduct(Product product) {  
        this.product = product;  
    }  
      
    @Override  
    public Product getProduct() {  
        return product;  
    }  
}  
```  
  
## 抽象观察者 Observer  
为所有的具体观察者定义一个接口，在得到主题的通知时更新自己，这个接口叫做更新接口。  
```java  
interface Observer {  
    public void update(Product product);  
}  
```  
  
## 具体观察者 ConcreteObserver  
实现抽象观察者所要求的更新接口。如果需要，具体观察者角色可以保持一个指向具体主题对象的引用  
```java  
class ConcreteObserver implements Observer {  
    public String name;  
    public ConcreteObserver(String name) {  
        this.name = name;  
    }  
      
    @Override  
    public void update(Product product) {  
        if (product == null) return;  
        System.out.println("具体观察者【" + name + "】收到消息：" + product.message + "-" + product.num);  
    }  
}  
```  
  
## 演示  
```java  
public class Test {  
    public static void main(String[] args) throws InterruptedException {   
          
        Subject subject = new ConcreteSubject();//主题对象  
        Observer observer1 = new ConcreteObserver("包青天");//观察者对象  
        Observer observer2 = new ConcreteObserver("白乾涛");  
          
        subject.attach(observer1);//将观察者对象注册到主题对象上  
        subject.attach(observer2);  
          
        Product product = new Product(); //主题发布的产品  
        subject.setProduct(product);  
          
        //当主题发布的产品对象的状态改变时，主题通知观察者  
        product.changed = true;  
        product.message = "下载进度";  
        product.num = 15;  
        subject.nodifyObservers();  
          
        //当观察者对象不再需要监控此主题对象时，将观察者对象取消注册到主题对象上  
        subject.detach(observer1);  
    }  
}  
```  
  
# 利用系统提供的两个类  
## Observer 源码  
```java  
package java.util;  
  
public interface Observer {  
    void update(Observable o, Object arg);  
}  
```  
  
## Observable 源码  
```java  
package java.util;  
  
public class Observable {  
    private boolean changed = false;//标志被观察者是否改变  
    private Vector obs;//保存注册的观察者对象  
  
    public Observable() {  
        obs = new Vector();  
    }  
  
    public synchronized void addObserver(Observer o) {  
        if (o == null) throw new NullPointerException();  
        if (!obs.contains(o)) {  
            obs.addElement(o);  
        }  
    }  
  
    public synchronized void deleteObserver(Observer o) {  
        obs.removeElement(o);  
    }  
  
    public void notifyObservers() {  
        notifyObservers(null);  
    }  
  
    public void notifyObservers(Object arg) {  
        Object[] arrLocal;  
  
        synchronized (this) {  
            if (!changed) return;  
            arrLocal = obs.toArray();  
            clearChanged();  
        }  
  
        for (int i = arrLocal.length-1; i>=0; i--)  
            ((Observer)arrLocal[i]).update(this, arg);  
    }  
  
    public synchronized void deleteObservers() {  
        obs.removeAllElements();  
    }  
  
    protected synchronized void setChanged() {  
        changed = true;  
    }  
  
    protected synchronized void clearChanged() {  
        changed = false;  
    }  
  
    public synchronized boolean hasChanged() {  
        return changed;  
    }  
  
    public synchronized int countObservers() {  
        return obs.size();  
    }  
}  
```  
  
## 利用系统类演示观察者模式  
### 被观察者  
继承自Observable  
```java  
class HanFeiZi extends Observable {  
    public void haveBreakfast() {  
        System.out.println("韩非子大吼一声：开饭！");  
        setChanged();  
        notifyObservers("韩非子开始吃饭了");  
    }  
  
    public void haveFun() {  
        System.out.println("韩非子大吼一声：侍寝！");  
        setChanged();  
        notifyObservers("韩非子开始娱乐了");  
    }  
}  
```  
  
### 观察者  
观察者一  
```java  
class LiSi implements Observer {  
    @Override  
    public void update(Observable observable, Object obj) {  
         //李斯是个观察者，一旦韩非子有活动，他就收到了谍报  
         System.out.println("【李斯】报告秦老板！臣收到谍报，韩非子有新动向--->" + obj.toString());   
    }  
}  
```  
  
观察者二  
```java  
class MrsHan implements Observer {  
    public void update(Observable observable, Object obj) {  
        System.out.println("【韩夫人】因为--->" + obj.toString() + "——所以我悲伤呀");  
    }  
}  
```  
  
### 演示  
```java  
public class Test {  
    public static void main(String[] args) {  
        //定义被观察者  
        HanFeiZi hanFeiZi = new HanFeiZi();  
  
        //定义观察者  
        Observer liSi = new LiSi();  
        Observer mrsHan = new MrsHan();  
  
        //观察者注册被观察者   
        hanFeiZi.addObserver(liSi);  
        hanFeiZi.addObserver(mrsHan);  
  
        //然后观察者就可以监视被观察者韩非子的动向  
        hanFeiZi.haveBreakfast();  
        hanFeiZi.haveFun();  
    }  
}  
```  
  
2018-2-21  
