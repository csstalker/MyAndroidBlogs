| Markdown版本笔记 | 我的GitHub首页 | 我的博客 | 我的微信 | 我的邮箱 |  
| :------------: | :------------: | :------------: | :------------: | :------------: |  
| [MyAndroidBlogs][Markdown] | [baiqiantao][GitHub] | [baiqiantao][博客] | bqt20094 | baiqiantao@sina.com |  
  
[Markdown]:https://github.com/baiqiantao/MyAndroidBlogs  
[GitHub]:https://github.com/baiqiantao  
[博客]:http://www.cnblogs.com/baiqiantao/  
  
集合 源码分析判断是否是同一元素的策略  
***  
目录  
===  

- [判断是否是同一元素的策略](#判断是否是同一元素的策略)
	- [List：equals](#list：equals)
	- [Tree：compare 或 compareTo](#tree：compare-或-compareto)
	- [Hash：hashCode + equals](#hash：hashcode--equals)
	- [特殊对象 String：字符序列](#特殊对象-string：字符序列)
- [测试代码](#测试代码)
	- [List](#list)
	- [Tree](#tree)
		- [Person](#person)
		- [不指定 Comparator](#不指定-comparator)
		- [指定 Comparator](#指定-comparator)
	- [Hash 集合](#hash-集合)
		- [Person](#person)
		- [HashSet](#hashset)
		- [HashMap](#hashmap)
		- [日志](#日志)
  
# 判断是否是同一元素的策略  
## List：equals  
对于List集合(ArrayList、LinkedList等)：仅仅是通过判断两个对象的【equals】方法是否为true。  
  
以下为 ArrayList 的部分源码：  
```java  
public boolean contains(Object o) {  
   return indexOf(o) >= 0;  
}  
  
public int indexOf(Object o) {  
   if (o == null) {  
      for (int i = 0; i < size; i++)  
         if (elementData[i] == null)  
            return i;  
   } else {  
      for (int i = 0; i < size; i++)  
         if (o.equals(elementData[i])) //核心代码：contains 时完全是根据 equals 方法来判断是否是同一元素  
            return i;  
   }  
   return -1;  
}  
```  
  
## Tree：compare 或 compareTo  
对于Tree集合(TreeSet、TreeMap)：  
- 如果构造时指定了 `Comparator`，则根据 Comparator 接口中的 `compare` 方法返回值是否为 0 来判断。  
- 如果构造时没有指定 Comparator，则其中的元素必须实现 `Comparable` 接口，此时根据 `compareTo` 方法返回值是否为 0 来判断。  
  
TreeSet、TreeMap部分源码：  
```java  
public TreeSet() {  
    this(new TreeMap<E,Object>());  
}  
  
public TreeSet(Comparator<? super E> comparator) {  
    this(new TreeMap<>(comparator));  
}  
  
public boolean contains(Object o) {  
    return m.containsKey(o);  
}  
```  
  
```java  
public boolean containsKey(Object key) {  
    return getEntry(key) != null;  
}  
  
final Entry<K,V> getEntry(Object key) {  
    if (comparator != null) return getEntryUsingComparator(key); //核心代码：如果指定了比较器......  
    if (key == null) throw new NullPointerException();  
    @SuppressWarnings("unchecked")  //核心代码：如果没有指定比较器，则将元素强转为 Comparable  
    Comparable<? super K> k = (Comparable<? super K>) key;  
    Entry<K,V> p = root;  
    while (p != null) {  
        int cmp = k.compareTo(p.key);   
        if (cmp < 0) p = p.left;  
        else if (cmp > 0) p = p.right;  
        else return p; //核心代码：如果 compareTo 的值为0 ，则返回此值，否则继续遍历  
    }  
    return null;  
}  
  
final Entry<K,V> getEntryUsingComparator(Object key) {  
    @SuppressWarnings("unchecked")  
    K k = (K) key;  
    Comparator<? super K> cpr = comparator;  
    if (cpr != null) {  
        Entry<K,V> p = root;  
        while (p != null) {  
        int cmp = cpr.compare(k, p.key);  
        if (cmp < 0)  p = p.left;  
        else if (cmp > 0) p = p.right;  
        else  return p; //核心代码：如果 compare 的值为0 ，则返回此值，否则继续遍历  
        }  
    }  
    return null;  
}  
```  
  
## Hash：hashCode + equals  
对于Hash系列的集合(TreeSet、HashMap)：  
- 先判断元素的 `hashCode` 的值是否相同，如果不同则认为是不同的元素。  
- 如果两个元素的 hashCode 的值相同，则再判断元素的 `equals` 返回值是否为 true，如果为 true 则是不同的元素，否则为相同的元素。  
  
HashSet、HashMap部分源码：  
```java  
public HashSet() {  
    map = new HashMap<>();  
}  
public boolean add(E e) {  
    return map.put(e, PRESENT)==null;  
}  
public boolean contains(Object o) {  
    return map.containsKey(o);  
}  
```  
  
```java  
public boolean containsKey(Object key) {  
    return getNode(hash(key), key) != null;  
}  
  
static final int hash(Object key) {  
    int h;  
    return (key == null) ? 0 : (h = key.hashCode()) ^ (h >>> 16);  
}  
  
final Node<K,V> getNode(int hash, Object key) {  
    Node<K,V>[] tab;   
    Node<K,V> first, e;   
    int n; K k;  
    if ((tab = table) != null && (n = tab.length) > 0 && (first = tab[(n - 1) & hash]) != null) {  
        if (first.hash == hash && ((k = first.key) == key || (key != null && key.equals(k))))  // always check first node  
        return first;  
        if ((e = first.next) != null) {  
        if (first instanceof TreeNode) return ((TreeNode<K,V>)first).getTreeNode(hash, key);  
        do {  
            if (e.hash == hash && ((k = e.key) == key || (key != null && key.equals(k)))) return e;  
        } while ((e = e.next) != null);  
        }  
    }  
    return null;  
}  
```  
  
## 特殊对象 String：字符序列  
String 类重写了 `hashCode` 和 `equals` 方法，如果两个 String 的内容相同则 hashCode 和 equals 方法也相同或为 true。  
```java  
private final char value[]; // The value is used for character storage.   
private int hash; // Default to 0  
  
public int hashCode() {  
    int h = hash;  
    if (h == 0 && value.length > 0) { //如果长度为 0 ，则其 hash 也为 0  
        char val[] = value;  
        for (int i = 0; i < value.length; i++) {  
            h = 31 * h + val[i]; //哈希值完全就是对字符串中所有字符通过某种算法计算后的结果，而且算法是稳定的  
        }  
        hash = h;  
    }  
    return h;  
}  
```  
  
```java  
public boolean equals(Object anObject) {  
    if (this == anObject) {  
        return true;  
    }  
    if (anObject instanceof String) {  //必须是 String  
        String anotherString = (String) anObject;  
        int n = value.length;  
        if (n == anotherString.value.length) {  
            char v1[] = value;  
            char v2[] = anotherString.value;  
            int i = 0;  
            while (n-- != 0) {  
                if (v1[i] != v2[i]) return false; //逐个比较字符，一旦发现对应位置有不相同的，则为不同的字符串  
                i++;  
            }  
            return true;  
        }  
    }  
    return false;  
}  
```  
  
# 测试代码  
## List   
测试代码  
```java  
class Person {  
    public String name;  
    public int age;  
  
    public Person(String name, int age) {  
        this.name = name;  
        this.age = age;  
    }  
  
    @Override  
    public boolean equals(Object o) {  
        Person p = (Person) o;  
        System.out.println("调用了equals方法，当前对象【" + toString() + "】被比较对象【" + p.toString() + "】");  
        return this.age == p.age || this.name.equals(p.name);  
    }  
  
    @Override  
    public String toString() {  
        return "{" + name + "," + age + "}";  
    }  
}  
```  
  
```java  
List<Person> list = Arrays.asList(new Person("a", 20), new Person("a", 21), new Person("b", 20), new Person("b", 21));  
System.out.println(list);  
System.out.println(list.contains(new Person("c", 20)) + "  " + list.contains(new Person("b", 10086)));  
```  
      
日志  
```java  
[{a,20}, {a,21}, {b,20}, {b,21}]  
调用了equals方法，当前对象【{c,20}】被比较对象【{a,20}】  
调用了equals方法，当前对象【{b,10086}】被比较对象【{a,20}】  
调用了equals方法，当前对象【{b,10086}】被比较对象【{a,21}】  
调用了equals方法，当前对象【{b,10086}】被比较对象【{b,20}】  
true  true  
```  
  
## Tree  
### Person  
```java  
class Person implements Comparable<Person> {  
    public String name;  
    public int age;  
  
    public Person(String name, int age) {  
        this.name = name;  
        this.age = age;  
    }  
  
    @Override  
    public boolean equals(Object p) {  
        System.out.println("调用了equals方法，当前对象【" + toString() + "】被比较对象【" + p.toString() + "】");  
        return this.name.equals(((Person) p).name);  
    }  
  
    @Override  
    public String toString() {  
        return "{" + name + "," + age + "}";  
    }  
  
    @Override  
    public int compareTo(Person o) {  
        System.out.println("调用了compareTo方法，当前对象【" + toString() + "】被比较对象【" + o.toString() + "】");  
        return this.age - o.age;  
    }  
}  
```  
  
### 不指定 Comparator  
```java  
TreeSet<Person> set = new TreeSet<>();  
System.out.println("添加第1个元素");  
set.add(new Person("a", 20));  
System.out.println("添加第2个元素"); // compareTo 方法返回值不为 0，判断为不同元素，所以添加进去  
set.add(new Person("a", 21));  
System.out.println("添加第3个元素"); // compareTo 方法返回值为 0，判断为相同元素，所以不添加进去  
set.add(new Person("b", 20));  
System.out.println("添加第4个元素"); // 相同元素，所以不添加进去  
set.add(new Person("b", 21));  
System.out.println(set);  
```  
      
日志：  
```java  
添加第1个元素  
调用了compareTo方法，当前对象【{a,20}】被比较对象【{a,20}】  
添加第2个元素  
调用了compareTo方法，当前对象【{a,21}】被比较对象【{a,20}】  
添加第3个元素  
调用了compareTo方法，当前对象【{b,20}】被比较对象【{a,20}】//先和第一个元素比较，因为相同，所以直接结束  
添加第4个元素  
调用了compareTo方法，当前对象【{b,21}】被比较对象【{a,20}】//先和第一个元素比较，不相同，继续和下一个元素比较  
调用了compareTo方法，当前对象【{b,21}】被比较对象【{a,21}】//再和第二个元素比较，因为相同，所以结束  
[{a,20}, {a,21}]  
```  
  
### 指定 Comparator  
```java  
TreeSet<Person> set = new TreeSet<>((p1, p2) -> {  
    System.out.println("调用了compare方法，对象1【" + p1.toString() + "】对象2【" + p2.toString() + "】");  
    return p1.name.compareTo(p2.name);  
});  
```  
      
日志：  
```java  
添加第1个元素  
调用了compare方法，对象1【{a,20}】对象2【{a,20}】  
添加第2个元素  
调用了compare方法，对象1【{a,21}】对象2【{a,20}】  
添加第3个元素  
调用了compare方法，对象1【{b,20}】对象2【{a,20}】  
添加第4个元素  
调用了compare方法，对象1【{b,21}】对象2【{a,20}】  
调用了compare方法，对象1【{b,21}】对象2【{b,20}】  
[{a,20}, {b,20}]  
```  
  
## Hash 集合  
### Person  
```java  
class Person {  
    public String name;  
    public int age;  
  
    public Person(String name, int age) {  
        this.name = name;  
        this.age = age;  
    }  
  
    @Override  
    public int hashCode() {  
        System.out.println("调用了hashCode方法，当前对象【" + toString() + "】");  
        return age;  
    }  
  
    @Override  
    public boolean equals(Object p) {  
        System.out.println("调用了equals方法，当前对象【" + toString() + "】被比较对象【" + p.toString() + "】");  
        return this.name.equals(((Person) p).name);  
    }  
  
    @Override  
    public String toString() {  
        return "{" + name + "," + age + "}";  
    }  
}  
```  
  
### HashSet  
```java  
HashSet<Person> set = new HashSet<>();  
System.out.println("添加第1个元素");  
set.add(new Person("a", 20));  
System.out.println("添加第2个元素");  
set.add(new Person("a", 21));// hashCode 不相同，直接判断为不同元素，所以直接添加进去  
System.out.println("添加第3个元素");  
set.add(new Person("b", 21)); // hashCode 相同，进一步判断 equals 方法是否相同；因为不相同，所以添加进去  
System.out.println("添加第4个元素");  
set.add(new Person("c", 21)); // hashCode 相同，进一步判断 equals 方法是否相同；因为不相同，所以添加进去  
System.out.println("添加第5个元素");  
set.add(new Person("b", 21)); // hashCode 相同，进一步判断 equals 方法是否相同；因为相同，所以不添加进去  
System.out.println(set); //[{a,20}, {a,21}, {b,21}, {c,21}]  
```  
      
### HashMap  
```java  
HashMap<Person, Integer> map = new HashMap<>();  
System.out.println("添加第1个元素");  
map.put(new Person("a", 20), 1);  
System.out.println("添加第2个元素");  
map.put(new Person("a", 21), 1);// hashCode 不相同，直接判断为不同元素，所以直接添加进去  
System.out.println("添加第3个元素");  
map.put(new Person("b", 21), 1); // hashCode 相同，进一步判断 equals 方法是否相同；因为不相同，所以添加进去  
System.out.println("添加第4个元素");  
map.put(new Person("c", 21), 1); // hashCode 相同，进一步判断 equals 方法是否相同；因为不相同，所以添加进去  
System.out.println("添加第5个元素");  
map.put(new Person("b", 21), 1); // hashCode 相同，进一步判断 equals 方法是否相同；因为相同，所以不添加进去  
System.out.println(map.keySet()); //[{a,20}, {a,21}, {b,21}, {c,21}]  
```  
  
### 日志  
以上两种方式具有完全相同的日志：  
```java  
添加第1个元素  
调用了hashCode方法，当前对象【{a,20}】  
添加第2个元素  
调用了hashCode方法，当前对象【{a,21}】  
添加第3个元素  
调用了hashCode方法，当前对象【{b,21}】  
调用了equals方法，当前对象【{b,21}】被比较对象【{a,21}】  
添加第4个元素  
调用了hashCode方法，当前对象【{c,21}】  
调用了equals方法，当前对象【{c,21}】被比较对象【{a,21}】//首先是和第一个具有相同 hashCode 的元素比较  
调用了equals方法，当前对象【{c,21}】被比较对象【{b,21}】//不一致时再和下一个具有相同 hashCode 的元素比较  
添加第5个元素  
调用了hashCode方法，当前对象【{b,21}】  
调用了equals方法，当前对象【{b,21}】被比较对象【{a,21}】//首先是和第一个具有相同 hashCode 的元素比较  
调用了equals方法，当前对象【{b,21}】被比较对象【{b,21}】//一旦发现具有相同 hashCode 的元素也相互 equals，就会立即结束  
[{a,20}, {a,21}, {b,21}, {c,21}]  
```  
  
2018-7-13  
