| Markdown版本笔记 | 我的GitHub首页 | 我的博客 | 我的微信 | 我的邮箱 |  
| :------------: | :------------: | :------------: | :------------: | :------------: |  
| [MyAndroidBlogs][Markdown] | [baiqiantao][GitHub] | [baiqiantao][博客] | bqt20094 | baiqiantao@sina.com |  
  
[Markdown]:https://github.com/baiqiantao/MyAndroidBlogs  
[GitHub]:https://github.com/baiqiantao  
[博客]:http://www.cnblogs.com/baiqiantao/  
  
  
***  
目录  
===  

- [原型模式](#原型模式)
	- [简介](#简介)
	- [案例](#案例)
  
# 原型模式  
## 简介   
定义：用原型实例指定创建对象的种类，并通过`拷贝`这些原型创建新的对象。  
   
原型模式主要用于对象的复制，它的核心是原型类`Prototype`。  
  
Prototype类需要具备以下两个条件：  
- 实现`Cloneable`接口。Cloneable接口中没有声明任何方法，其作用只有一个，就是在运行时通知虚拟机可以安全地在实现了此接口的类上使用`clone()`方法。在java虚拟机中，只有实现了这个接口的类才可以被拷贝，否则在运行时会抛出CloneNotSupportedException异常。   
- 重写Object类中的clone()方法。Java中，超类Object中有一个clone方法，作用是返回对象的一个拷贝，但是其作用域是`protected`类型的，一般的类无法调用，因此，Prototype类需要将clone方法的作用域修改为`public`类型。   
  
```java  
protected native Object clone() throws CloneNotSupportedException;  
```  
  
**原型模式的优点**  
使用原型模式创建对象比直接new一个对象在`性能`上要好的多，因为Object类的clone方法是一个本地方法，它直接操作内存中的二进制流，特别是复制大对象时，性能的差别非常明显。  
  
使用原型模式的另一个好处是`简化`对象的创建，使得创建对象就像我们在编辑文档时的复制粘贴一样简单。  
  
因为以上优点，在需要重复地创建相似对象时可以考虑使用原型模式。比如需要在一个循环体内创建对象，假如对象创建过程比较复杂或者循环次数很多的话，使用原型模式不但可以简化创建过程，而且可以使系统的整体性能提高很多。  
  
**原型模式与构造方法**  
注意，使用原型模式复制对象`不会调用类的构造方法`。因为对象的复制是通过调用Object类的clone方法来完成的，它直接在内存中复制数据，因此不会调用到类的构造方法。  
  
不但构造方法中的代码不会执行，甚至连访问权限都对原型模式无效。  
  
还记得单例模式吗？单例模式中，只要将构造方法的访问权限设置为private型，就可以实现单例。但是clone方法直接无视构造方法的权限，所以，`单例模式与原型模式是冲突的`，在使用时要特别注意。  
  
**浅拷贝**  
注意，Object类提供的clone方法只是拷贝本对象，其对象内部的数组、引用对象等都不拷贝，还是指向原生对象的内部元素的地址，这种拷贝就叫做浅拷贝。  
  
这种情况下，由于clone对象和原始对象共享了一个私有变量，一处修改处处修改，所以是一个种非常不安全的方式。  
  
为了实现真正的拷贝对象，而不是纯粹引用，我们需要对要拷贝的对象的所有成员都可复制化。  
  
> PS：八大基本类型和String(String 没有实现Cloneable接口)等都会被拷贝的。  
  
## 案例  
原型类  
```java  
class Student implements Cloneable {  
    public ArrayList<String> courseList = new ArrayList<String>();  
    public Address address;  
  
    @SuppressWarnings("unchecked")  
    @Override  
    public Object clone() {  
        Student stu = null;  
        try {  
            stu = (Student) super.clone();  
            //如果不写下面两行代码，则只会对Student进行clone而不会对其成员clone，这种情况下其只是【浅拷贝】  
            //为了实现Student真正的拷贝对象，而不是纯粹引用复制，我们需要使Student的所有成员都可Cloneable化  
            stu.address = (Address) address.clone();//自定义的Address需要我们手动实现Cloneable接口，并重写其clone方法  
            stu.courseList = (ArrayList<String>) courseList.clone();//ArrayList本身就已实现了Cloneable接口，所以可以直接调用clone方法  
        } catch (CloneNotSupportedException e) {  
            e.printStackTrace();  
        }  
        return stu;  
    }  
}  
```  
  
原型类的成员变量也需要为原型类  
```java  
class Address implements Cloneable {  
    public String add;  
  
    public Address(String add) {  
        this.add = add;  
    }  
  
    @Override  
    public Object clone() {  
        Address addr = null;  
        try {  
            addr = (Address) super.clone();  
        } catch (CloneNotSupportedException e) {  
            e.printStackTrace();  
        }  
        return addr;  
    }  
}  
```  
  
测试  
```java  
public class Test {  
    public static void main(String args[]) {  
        Student stu = new Student();  
        stu.address = new Address("广州");  
        stu.courseList.add("语文");  
  
        //复制stu  
        Student cloneStudent = (Student) stu.clone();  
        System.out.println("原始的Student：" + stu.address + " " + stu.address.add + " " + stu.courseList);  
        System.out.println("cloneStudent：" + cloneStudent.address + " " + cloneStudent.address.add + " " + cloneStudent.courseList);  
  
        System.out.println("\n=========更改cloneStudent后========\n");  
        cloneStudent.address.add = "深圳";  
        cloneStudent.courseList.clear();  
        cloneStudent.courseList.add("数学");  
        System.out.println("原始的Student：" + stu.address + " " + stu.address.add + " " + stu.courseList);  
        System.out.println("cloneStudent：" + cloneStudent.address + " " + cloneStudent.address.add + " " + cloneStudent.courseList);  
    }  
}  
```  
  
**测试结果一，深拷贝**  
因为cloneStudent中的成员和原始的Student的成员没有任何联系，所以，当更改cloneStudent的成员后，原始的Student的成员并没有改变！  
```java  
原始的Student：Address@17cb0a16 广州 [语文]  
cloneStudent：Address@26f04d94 广州 [语文]  
  
=========更改cloneStudent后========  
  
原始的Student：Address@17cb0a16 广州 [语文]  
cloneStudent：Address@26f04d94 深圳 [数学]  
```  
  
**测试结果二，浅拷贝**  
因为cloneStudent中的成员和原始的Student的成员指向的是通过一个对象，所以，当更改cloneStudent的成员后，原始的Student的成员肯定也一起改变了！  
```java  
原始的Student：Address@670b5064 广州 [语文]  
cloneStudent：Address@670b5064 广州 [语文]  
  
=========更改cloneStudent后========  
  
原始的Student：Address@670b5064 深圳 [数学]  
cloneStudent：Address@670b5064 深圳 [数学]  
```  
  
2016-10-31  
