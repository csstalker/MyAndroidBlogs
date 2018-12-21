| Markdown版本笔记 | 我的GitHub首页 | 我的博客 | 我的微信 | 我的邮箱 |  
| :------------: | :------------: | :------------: | :------------: | :------------: |  
| [MyAndroidBlogs][Markdown] | [baiqiantao][GitHub] | [baiqiantao][博客] | bqt20094 | baiqiantao@sina.com |  
  
[Markdown]:https://github.com/baiqiantao/MyAndroidBlogs  
[GitHub]:https://github.com/baiqiantao  
[博客]:http://www.cnblogs.com/baiqiantao/  
  
对象引用 方法传参 值传递 引用传递 易错点  
***  
目录  
===  

- [概念](#概念)
- [方法传递](#方法传递)
	- [案例一](#案例一)
	- [案例二](#案例二)
- [对象引用](#对象引用)
	- [案例一(核心案例)](#案例一核心案例)
	- [案例二](#案例二)
	- [案例三](#案例三)
	- [案例四](#案例四)
  
# 概念  
Java中有没有引用传递？  
> 答：Java中只有按值传递，没有按引用传递！  
  
当一个对象被当作参数传递到一个方法中后，在此方法中可改变这个对象的属性，并可返回变化后的结果，那么这里到底是值传递还是引用传递?  
> 答：是值传递！  
- 不管是原始类型还是引用类型，传递的都是【副本】，也可以说是【值】。  
- 如果参数类型是【原始类型】，那么传过来的就是这个参数的值，如果在函数中改变了副本的值不会改变原始的值。  
- 如果参数类型是【引用类型】，那么传过来的就是这个引用参数的副本(对象的引用)，这个副本存放的是参数的【地址】。如果在函数中没有改变这个副本的地址，而是改变了地址中的值，那么在函数内的改变会影响到传入的参数；如果在函数中改变了副本的地址，如new一个，那么副本就指向了一个新的地址，此时传入的参数还是指向原来的地址，所以不会改变参数的值。  
  
# 方法传递  
## 案例一  
```java  
public class Test {  
    public static void main(String[] args) {  
        int a = 1, b = 2;  
        System.out.println(a + "，" + b);  
        swap(a, b);  
        System.out.println(a + "，" + b);  
    }  
  
    public static void swap(int a, int b) {  
        int tem = a;  
        a = b;  
        b = tem;  
    }  
}  
```  
  
## 案例二  
```java  
public class Test {  
    public static void main(String[] args) {  
        Person p1 = new Person("1");  
        Person p2 = new Person("2");  
        System.out.println(p1 + " " + p2 + "，" + p1.name + " " + p2.name);//Person@15db9742 Person@6d06d69c，1 2  
        test(p1, p2);  
        System.out.println(p1 + " " + p2 + "，" + p1.name + " " + p2.name);//Person@15db9742 Person@6d06d69c，2 1  
        test2(p1, p2);  
        System.out.println(p1 + " " + p2 + "，" + p1.name + " " + p2.name);//Person@15db9742 Person@6d06d69c，3 4  
        test3(p1, p2);  
        System.out.println(p1 + " " + p2 + "，" + p1.name + " " + p2.name);//Person@15db9742 Person@6d06d69c，6 5  
        test4(p1, p2);  
        System.out.println(p1 + " " + p2 + "，" + p1.name + " " + p2.name);//Person@15db9742 Person@6d06d69c，6 5  
    }  
  
    private static void test(Person person1, Person person2) {  
        String temp = person1.name;  
        person1.name = person2.name;  
        person2.name = temp;  
    }  
  
    private static void test2(Person person1, Person person2) {  
        person1.name = "3";  
        person2.name = "4";  
        Person person = person1;  
        person1 = person2;  
        person2 = person;  
    }  
  
    private static void test3(Person person1, Person person2) {  
        Person person = person1;  
        person1 = person2;  
        person2 = person;  
        person1.name = "5";  
        person2.name = "6";  
    }  
  
    private static void test4(Person person1, Person person2) {  
        person1 = new Person("7");  
        person2 = new Person("8");  
    }  
}  
  
class Person {  
    public String name;  
  
    public Person(String name) {  
        this.name = name;  
    }  
}  
```  
  
# 对象引用  
## 案例一(核心案例)  
```java  
public class Test {  
    public static void main(String[] args) {  
        Work work = new Work();  
        work.valuse = 1;  
        Person person = new Person();  
        person.work = work;  
        work.valuse = 2;  
        System.out.println(person.work.valuse); //2  
        work = new Work();// new 之后 Person 中引用的 Work 就不再是 work 所引用的 Work 了  
        work.valuse = 3;  
        System.out.println(person.work.valuse); //2  
        person.work = work; //将 Person 中引用的 Work 更改为 work 所引用的 Work  
        System.out.println(person.work.valuse); //3  
    }  
}  
class Work {  
    int valuse;  
}  
class Person {  
    Work work;  
}  
```  
  
## 案例二  
```java  
public class Test {  
    public static void main(String[] args) {  
        Work work = new Work();  
        work.valuse = 1;  
        ArrayList<Work> list = new ArrayList<Work>();  
        list.add(work);  
        Person person = new Person();  
        person.setList(list);  
        work.valuse = 2;  
        System.out.println(person.list.get(0).valuse); //2  
        person.list.get(0).valuse = 3;  
        System.out.println(list.get(0).valuse); //3  
        System.out.println(person.list.get(0).valuse); //3  
        System.out.println("---------以上并没有更改引用的对象地址--------");  
        work = new Work();  
        work.valuse = 4;  
        System.out.println(list.get(0).valuse); //3  
        System.out.println(person.list.get(0).valuse); //3  
        list.set(0, work);  
        System.out.println(list.get(0).valuse); //4  
        System.out.println(person.list.get(0).valuse); //4  
    }  
}  
class Work {  
    int valuse;  
}  
class Person {  
    List<Work> list;  
    public void setList(List<Work> list) {  
        this.list = list;  
    }  
}  
```  
  
## 案例三  
```java  
public class Test {  
    public static void main(String[] args) {  
        List<Integer> list = new ArrayList<Integer>();  
        list.add(1);  
        A a = new A();  
        a.setList(list);  
        list.add(2);  
        System.out.println(a.list1.toString()); //[1, 2]  
        System.out.println(a.list2.toString()); //[1]  
        System.out.println(a.list3.toString()); //[1]  
        a.setList(list);  
        System.out.println(a.list1.toString()); //[1, 2]  
        System.out.println(a.list2.toString()); //[1, 2]  
        System.out.println(a.list3.toString()); //[1, 2]  
    }  
}  
class A {  
    public List<Integer> list1 = new ArrayList<Integer>();  
    public List<Integer> list2 = new ArrayList<Integer>();  
    public List<Integer> list3 = new ArrayList<Integer>();  
    public void setList(List<Integer> list) {  
        this.list1 = list;  
        this.list2 = new ArrayList<Integer>(list);  
        list3.clear();  
        this.list3.addAll(list);  
    }  
}  
```  
  
## 案例四  
```java  
public class Test {  
    public static void main(String[] args) {  
        List<Person> list = new ArrayList<>();//一个集合  
        list.add(new Person("包青天", 1, new Work("百度", 1)));//添加数据  
        String name = list.get(0).name;  
        int age = list.get(0).age;  
        Work work = list.get(0).work;  
        name = "2";//修改数据。注意，这里的name局部变量和person对象中的name字段指向的是不同的实例对象  
        age = 2;  
        work = new Work("2", 2);  
        System.out.println(list.get(0).name + "," + list.get(0).age + "," + list.get(0).work.company + "," + list.get(0).work.time);//包青天,1,百度,1  
        list.get(0).name = "3";  
        list.get(0).age = 3;  
        list.get(0).work.company = "3";  
        list.get(0).work.time = 3;  
        System.out.println(list.get(0).name + "," + list.get(0).age + "," + list.get(0).work.company + "," + list.get(0).work.time);//3,3,3,3  
        Person temPerson = list.get(0);  
        temPerson.name = "4";  
        temPerson.age = 4;  
        Work tempWork = temPerson.work;  
        tempWork.company = "4";  
        tempWork.time = 4;  
        System.out.println(list.get(0).name + "," + list.get(0).age + "," + list.get(0).work.company + "," + list.get(0).work.time);//4,4,4,4  
    }  
}  
class Work {  
    int time;  
    String company;  
    public Work(String company, int time) {  
        this.time = time;  
        this.company = company;  
    }  
}  
class Person {  
    int age;  
    String name;  
    Work work;  
    public Person(String name, int age, Work work) {  
        this.age = age;  
        this.name = name;  
        this.work = work;  
    }  
}  
```  
2017-7-4  
