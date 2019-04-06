| Markdown版本笔记 | 我的GitHub首页 | 我的博客 | 我的微信 | 我的邮箱 |  
| :------------: | :------------: | :------------: | :------------: | :------------: |  
| [MyAndroidBlogs][Markdown] | [baiqiantao][GitHub] | [baiqiantao][博客] | bqt20094 | baiqiantao@sina.com |  
  
[Markdown]:https://github.com/baiqiantao/MyAndroidBlogs  
[GitHub]:https://github.com/baiqiantao  
[博客]:http://www.cnblogs.com/baiqiantao/  
  
集合 排序 判断对象相等 Comparable Comparator  
***  
目录  
===  

- [集合判断是否是同一元素的策略](#集合判断是否是同一元素的策略)
	- [List：equals](#List：equals)
	- [Tree：compare 或 compareTo](#Tree：compare-或-compareTo)
	- [Hash：hashCode + equals](#Hash：hashCode--equals)
	- [特殊对象 String：字符序列](#特殊对象-String：字符序列)
- [测试代码](#测试代码)
	- [List](#List)
	- [Tree](#Tree)
		- [Person](#Person)
		- [不指定 Comparator](#不指定-Comparator)
		- [指定 Comparator](#指定-Comparator)
	- [Hash 集合](#Hash-集合)
		- [Person](#Person)
		- [HashSet](#HashSet)
		- [HashMap](#HashMap)
		- [日志](#日志)
- [Comparable 和 Comparator](#Comparable-和-Comparator)
	- [接口 Comparable：类的自然排序](#接口-Comparable：类的自然排序)
		- [官方文档](#官方文档)
		- [compareTo 方法](#compareTo-方法)
		- [使用须知](#使用须知)
	- [接口 Comparator：集合的比较器](#接口-Comparator：集合的比较器)
		- [官方文档](#官方文档)
		- [compare方法](#compare方法)
		- [equals 方法](#equals-方法)
		- [使用须知](#使用须知)
	- [案例](#案例)
		- [探究两个接口方法的返回值](#探究两个接口方法的返回值)
		- [元素为空的情况](#元素为空的情况)
		- [Comparator 的使用案例](#Comparator-的使用案例)
  
# 集合判断是否是同一元素的策略  
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
  
注意上面的 `compare` 方法的目的并不是判断对象相同的，而是给对象排序的。  
  
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
  
# Comparable 和 Comparator  
## 接口 Comparable：类的自然排序  
### 官方文档  
`public interface java.lang.Comparable<T>`  
类型参数：T - 可以与此对象进行比较的那些对象的类型  
  
此接口强行对实现它的每个类的对象进行整体排序。这种排序被称为类的`自然排序`，类的 compareTo 方法被称为它的`自然比较方法`。  
  
实现此接口的对象列表和数组可以通过 `Collections.sort` 和 `Arrays.sort` 进行自动排序。实现此接口的对象可以用作有序映射 `SortedMap<K,V>` 中的键或有序集合 `SortedSet<E>` 中的元素，无需指定比较器。  
  
划重点，Comparable 的最重要的适用场景为：  
```java  
Collections.sort  
Arrays.sort  
SortedMap<K,V>  
SortedSet<E>  
```  
  
对于类 C 的每一个 e1 和 e2 来说，当且仅当 `e1.compareTo(e2) == 0` 与 `e1.equals(e2)` 具有相同的 boolean 值时，类 C 的自然排序才叫做与 `equals` 一致。  
  
注意，null 不是任何类的实例，即使 `e.equals(null)` 返回 false，`e.compareTo(null)` 也将抛出 `NullPointerException`。  
  
`建议最好使自然排序与 equals 一致`。这是因为在使用自然排序与 equals 不一致的元素(或键)时，没有显式比较器的有序集合(和有序映射表)行为表现“怪异”。尤其是，这样的有序集合(或有序映射表)违背了根据 equals 方法定义的集合(或映射表)的常规协定。  
  
例如，如果将两个键 a 和 b 添加到没有使用显式比较器的有序集合中，使 `(!a.equals(b) && a.compareTo(b) == 0)`，那么第二个 add 操作将返回 false(有序集合的大小没有增加)，因为从有序集合的角度来看，a 和 b 是相等的。  
  
实际上，所有实现 Comparable 的 Java 核心类都具有与 equals 一致的自然排序。`java.math.BigDecimal` 是个例外，它的自然排序将值相等但精确度不同的 BigDecimal 对象(比如 4.0 和 4.00)视为相等。  
  
### compareTo 方法  
`int compareTo(T o)` //比较此对象与指定对象的顺序  
为防止记乱，可以这么记： `a.compareTo(b)` 代表的是 `a-b` 的值，正值代表 `a>b` ，所以被比较的是小对象，那么被比较的对象应该放前面，这时就需要做一次调换位置的操作了。  
- 参数：o - 要比较的对象。  
- 返回：如果该对象小于、等于或大于指定对象，则分别返回负整数、零或正整数。  
- 抛出：ClassCastException - 如果指定对象的类型不允许它与此对象进行比较。  
  
### 使用须知  
`sgn(expression)` //表示 signum 数学函数，该函数根据 expression 的值是负数、零还是正数，分别返回 -1、0 或 1 中的一个值  
- 实现类必须确保对于所有的 x 和 y 都存在 `sgn(x.compareTo(y)) == -sgn(y.compareTo(x))` 的关系。这意味着如果 `y.compareTo(x)` 抛出一个异常，则 `x.compareTo(y)` 也要抛出一个异常。  
- 实现类还必须确保关系是可传递的：`(x.compareTo(y)>0 && y.compareTo(z)>0)` 意味着 `x.compareTo(z)>0` 。  
- 实现者必须确保 `x.compareTo(y)==0` 意味着对于所有的 z，都存在 `sgn(x.compareTo(z)) == sgn(y.compareTo(z))`。   
- 强烈推荐 `(x.compareTo(y)==0) == (x.equals(y))` 这种做法，但并不是 严格要求这样做。一般来说，任何实现 Comparable 接口和违背此条件的类都应该清楚地指出这一事实。推荐如此阐述：“注意：此类具有与 equals 不一致的自然排序。”  
  
## 接口 Comparator：集合的比较器  
### 官方文档  
`public interface java.util.Comparator<T>`  
类型参数：T - 此 Comparator 可以比较的对象类型  
  
强行对某个对象 Collection 进行`整体排序`的比较函数。可以将 Comparator 传递给 sort 方法(如 `Collections.sort` 或 `Arrays.sort`)，从而允许在排序顺序上实现精确控制。还可以使用 Comparator 来控制某些数据结构(如有序集合 `SortedSet<E>` 或有序映射 `SortedMap<K,V>` )的顺序，或者为那些没有自然顺序的对象 Collection 提供排序。  
  
当且仅当对于一组元素 S 中的每个 e1 和 e2 而言，`c.compare(e1, e2)==0` 与 `e1.equals(e2)` 具有相等的布尔值时，Comparator c 强行对 S 进行的排序才叫做与 equals 一致的排序。  
  
当使用具有与 equals 不一致的强行排序能力的 Comparator 对有序 set(或有序映射)进行排序时，应该小心谨慎。假定一个带显式 Comparator c 的有序 set(或有序映射)与从 set S 中抽取出来的元素(或键)一起使用。如果 c 强行对 S 进行的排序是与 equals 不一致的，那么有序 set(或有序映射)将是行为“怪异的”。尤其是有序 set(或有序映射)将违背根据 equals 所定义的 set(或映射)的常规协定。  
  
例如，假定使用 Comparator c 将满足 `(a.equals(b) && c.compare(a, b) != 0)` 的两个元素 a 和 b 添加到一个空 TreeSet 中，则第二个 add 操作将返回 true(树 set 的大小将会增加)，因为从树 set 的角度来看，a 和 b 是不相等的，即使这与 Set.add 方法的规范相反。  
  
注：通常来说，让 Comparator 也实现 `java.io.Serializable` 是一个好主意，因为它们在可序列化的数据结构(像 TreeSet、TreeMap)中可用作排序方法。为了成功地序列化数据结构，Comparator(如果已提供)必须实现 Serializable。  
  
### compare方法  
`int compare(T o1, T o2)` //比较用来排序的两个参数。  
为防止记乱，可以这么记： `compare(b, a)` 代表的是 `a-b` 的值，正值代表 `a>b` ，所以 b 是小对象，那么 b 应该放前面，这时就需要做一次调换位置的操作了。  
- 参数：o1 - 要比较的第一个对象。o2 - 要比较的第二个对象。  
- 返回：根据第一个参数小于、等于或大于第二个参数分别返回负整数、零或正整数。  
- 抛出：ClassCastException - 如果参数的类型不允许此 Comparator 对它们进行比较。  
  
### equals 方法  
`boolean equals(Object obj)` // 指示某个其他对象是否“等于”此 Comparator  
注意，这里的 equals 指的是比较器 Comparator 的 equals，也即比较器之间是否 equals，而不是比较器要比较的对象之间是否 equals。  
- 覆盖：类 Object 中的 equals  
- 参数：obj - 要进行比较的引用对象。  
- 返回：仅当指定的对象也是一个 Comparator，并且强行实施与此 Comparator 相同的排序时才返回 true。  
- 另请参见：Object.equals(Object), Object.hashCode()  
  
此方法必须遵守 `Object.equals(Object)` 的常规协定。此外， 仅当 指定的对象也是一个 Comparator，并且强行实施与此 Comparator 相同的排序时，此方法才返回 true。因此，`comp1.equals(comp2)` 意味着对于每个对象引用 o1 和 o2 而言，都存在 `sgn(comp1.compare(o1, o2)) ==sgn (comp2.compare(o1, o2))`。  
  
注意，不 重写 `Object.equals(Object)` 方法总是 安全的。然而，在某些情况下，重写此方法可以允许程序确定两个不同的 Comparator 是否强行实施了相同的排序，从而提高性能。  
  
### 使用须知  
`sgn(expression)` //表示 signum 数学函数，该函数根据 expression 的值是负数、零还是正数，分别返回 -1、0 或 1 中的一个值  
- 实现程序必须确保对于所有的 x 和 y 而言，都存在 `sgn(compare(x, y)) == -sgn(compare(y, x))`。这意味着如果 `compare(y, x)` 抛出一个异常，则 `compare(x, y)` 也要抛出一个异常。  
- 实现程序还必须确保关系是可传递的：`((compare(x, y)>0) && (compare(y, z)>0))` 意味着 `compare(x, z)>0`。  
- 实现程序必须确保 `compare(x, y)==0` 意味着对于所有的 z 而言，都存在 `sgn(compare(x, z)) == sgn(compare(y, z))`。  
- 强烈推荐 `(compare(x, y)==0) == (x.equals(y))` 这种做法，但并不是 严格要求这样做。一般说来，任何违背这个条件的 Comparator 都应该清楚地指出这一事实。推荐的语言是“注意：此 Comparator 强行进行与 equals 不一致的排序。”  
  
## 案例  
Comparable 接口是让类自身具有排序功能。  
Comparator 接口是对 Collection 进行整体排序的功能。  
  
### 探究两个接口方法的返回值  
定义一个对象  
```java  
class MyBean implements Comparable<MyBean> {  
    public Integer age;  
    public MyBean(int age) {  
        super();  
        this.age = age;  
    }  
    @Override  
    public int compareTo(MyBean o) {  
        return -1; //下面案例中更改的都是这个值  
    }  
    @Override  
    public String toString() {  
        return age + "";  
    }  
}  
```  
  
Comparable：  
```java  
List<MyBean> list = Arrays.asList(new MyBean(2), new MyBean(3), new MyBean(1), new MyBean(2));  
Collections.sort(list);  
System.out.println(list);   
```  
      
```java  
原始排序为 [2, 3, 1, 2]  
【-1】// [2, 1, 3, 2]，一定要注意，返回 -1 的意义不是 this.age - o.age 的值一直为 -1 ，这两种情况的排序结果是完全不一样的  
【1】// [2, 3, 1, 2]  
【0】// [2, 3, 1, 2]  
【this.age.compareTo(o.age)】//[1, 2, 2, 3]  
【this.age - o.age】// [1, 2, 2, 3]  
【o.age.compareTo(this.age)】//[3, 2, 2, 1]  
```  
  
Comparator：  
```java  
Integer[] array = { 2, 3, 1, 2 };  
//MyBean[] array = { new MyBean(2), new MyBean(3), new MyBean(1), new MyBean(2) }; //这种方式和上面的结果也是完全一样的  
Arrays.sort(array, (o1, o2) -> -1); //下面案例中更改的都是这个值  
System.out.println(Arrays.toString(array));  
```  
  
```java  
原始排序为 [2, 3, 1, 2]  
【-1】// [2, 1, 3, 2]，一定要注意，返回 -1 的意义不是 o1 - o2 的值一直为 -1 ，这两种情况的排序结果是完全不一样的  
【1】// [2, 3, 1, 2]  
【0】// [2, 3, 1, 2]  
【o1.compareTo(o2)】//[1, 2, 2, 3]  
【o1 - o2】// [1, 2, 2, 3]  
【o2.compareTo(o1)】//[3, 2, 2, 1]  
```  
  
Tips：  
- 可以发现，(一般情况下) Comparator 比较的结果和 Comparable 是完全一致的。  
- 以上两种情况，经测试，使用集合与使用数组排序后的结果是完全一致的。  
- 当指定比较器 Comparator 时，仅会通过指定比较器对元素排序，实现 Comparable 的元素的自然排序将不再有任何意义。  
  
### 元素为空的情况  
Comparable：如果需要用到 compareTo 方法去比较元素，则不允许除了首个元素外其余任何元素为空。  
```java  
class A implements Comparable<A> {  
    @Override  
    public int compareTo(A o) {  
        return 1;  
    }  
}  
class B implements Comparable<B> {  
    @Override  
    public int compareTo(B o) {  
        return -1;  
    }  
}  
```  
      
```java  
// 首个元素为空的情况  
A[] array1 = { null, new A() };  
B[] array2 = { null, new B() };  
Arrays.sort(array1);   
Arrays.sort(array2);   
System.out.println(Arrays.toString(array1)); //[null, A@15db9742]  
System.out.println(Arrays.toString(array2)); //[B@7852e922, null]  
```  
  
```java  
//非首个元素为空的情况  
A[] array1 = { new A(), null };  
B[] array2 = { new B(), null };  
Arrays.sort(array1); //NullPointerException  
Arrays.sort(array2); //NullPointerException  
```  
  
Comparator：完全由你控制是否可以有元素为空，所以这个功能更强大、兼容性更强。  
```java  
class C { }  
C[] array = { null, new C(), null, null };  
Arrays.sort(array, (o1, o2) -> 1); //对象的比较完全由你在 compare 方法中控制  
```  
  
### Comparator 的使用案例  
Person  
```java  
class Person {  
    public String name;  
    public int age;  
    public Person(String name, int age) {  
        this.name = name;  
        this.age = age;  
    }  
    @Override  
    public String toString() {  
        return "{age=" + age + " ,name=" + name + "}";  
    }  
}  
```  
  
Comparator  
```java  
class MyComparator implements Comparator<Person> {  
    @Override  
    public int compare(Person o1, Person o2) {  
        if (o2 == null) { //如果后面的一个元素为空，则保持其位置  
            return -1; //这一行代码的意义并不是将 (后面的)空元素 放在最后一个位置，而仅仅是保持元素的位置，不进行任何换位操作  
        } else if (o1 == null) {   
            return 1; //如果前面的一个元素为空，则和后一个元素更换其位置  
        } else { //如果两者都不为空，则先比较年龄，再比较姓名  
            if (o1.age != o2.age) { //如果年龄不相等，则先按年龄由小到大排序  
                return o1.age - o2.age; //其实这种表达式就是最标准的自然排序方式  
            } else {  
                if (o2.name == null) { //判断姓名时也同样先判断姓名是否为空  
                    return -1; //如果后面的一个元素的姓名为空，则保持其位置  
                } else if (o1.name == null) {  
                    return 1; //如果前面的一个元素的姓名为空，则和后一个元素更换其位置  
                } else {  
                    return o1.name.compareTo(o2.name); //如果都不为空，则直接用String的自然排序方式排序  
                }  
            }  
        }  
    }  
}  
```  
      
测试代码  
```java  
Person[] array = { null, new Person(null, 1), new Person(null, 2), new Person("ab", 1), //  
                new Person("ab", 2), null, new Person("ac", 2), new Person("a", 2) };  
Arrays.sort(array, new MyComparator());  
System.out.println(Arrays.toString(array));  
//[{age=1 ,name=ab}, {age=1 ,name=null},   
//{age=2 ,name=a}, {age=2 ,name=ab}, {age=2 ,name=ac},   
//{age=2 ,name=null}, null, null]  
```  
  
2018-7-12  
