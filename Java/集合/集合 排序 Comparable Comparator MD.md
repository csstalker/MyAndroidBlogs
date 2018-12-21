| Markdown版本笔记 | 我的GitHub首页 | 我的博客 | 我的微信 | 我的邮箱 |  
| :------------: | :------------: | :------------: | :------------: | :------------: |  
| [MyAndroidBlogs][Markdown] | [baiqiantao][GitHub] | [baiqiantao][博客] | bqt20094 | baiqiantao@sina.com |  
  
[Markdown]:https://github.com/baiqiantao/MyAndroidBlogs  
[GitHub]:https://github.com/baiqiantao  
[博客]:http://www.cnblogs.com/baiqiantao/  
  
集合 排序 Comparable Comparator  
***  
目录  
===  

- [接口 Comparable 类的自然排序](#接口-comparable-类的自然排序)
	- [官方文档](#官方文档)
	- [compareTo 方法](#compareto-方法)
	- [使用须知](#使用须知)
- [接口 Comparator 集合的比较器](#接口-comparator-集合的比较器)
	- [官方文档](#官方文档)
	- [compare方法](#compare方法)
	- [equals 方法](#equals-方法)
	- [使用须知](#使用须知)
- [Object 的 equals 和 hashCode 方法](#object-的-equals-和-hashcode-方法)
	- [equals 方法](#equals-方法)
	- [hashCode 方法](#hashcode-方法)
- [案例](#案例)
	- [探究两个接口方法的返回值](#探究两个接口方法的返回值)
	- [元素为空的情况](#元素为空的情况)
	- [Comparator 的使用案例](#comparator-的使用案例)
  
# 接口 Comparable 类的自然排序  
## 官方文档  
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
  
## compareTo 方法  
`int compareTo(T o)` //比较此对象与指定对象的顺序  
为防止记乱，可以这么记： `a.compareTo(b)` 代表的是 `a-b` 的值，正值代表 `a>b` ，所以被比较的是小对象，那么被比较的对象应该放前面，这时就需要做一次调换位置的操作了。  
- 参数：o - 要比较的对象。  
- 返回：如果该对象小于、等于或大于指定对象，则分别返回负整数、零或正整数。  
- 抛出：ClassCastException - 如果指定对象的类型不允许它与此对象进行比较。  
  
## 使用须知  
`sgn(expression)` //表示 signum 数学函数，该函数根据 expression 的值是负数、零还是正数，分别返回 -1、0 或 1 中的一个值  
- 实现类必须确保对于所有的 x 和 y 都存在 `sgn(x.compareTo(y)) == -sgn(y.compareTo(x))` 的关系。这意味着如果 `y.compareTo(x)` 抛出一个异常，则 `x.compareTo(y)` 也要抛出一个异常。  
- 实现类还必须确保关系是可传递的：`(x.compareTo(y)>0 && y.compareTo(z)>0)` 意味着 `x.compareTo(z)>0` 。  
- 实现者必须确保 `x.compareTo(y)==0` 意味着对于所有的 z，都存在 `sgn(x.compareTo(z)) == sgn(y.compareTo(z))`。   
- 强烈推荐 `(x.compareTo(y)==0) == (x.equals(y))` 这种做法，但并不是 严格要求这样做。一般来说，任何实现 Comparable 接口和违背此条件的类都应该清楚地指出这一事实。推荐如此阐述：“注意：此类具有与 equals 不一致的自然排序。”  
  
# 接口 Comparator 集合的比较器  
## 官方文档  
`public interface java.util.Comparator<T>`  
类型参数：T - 此 Comparator 可以比较的对象类型  
  
强行对某个对象 Collection 进行`整体排序`的比较函数。可以将 Comparator 传递给 sort 方法(如 `Collections.sort` 或 `Arrays.sort`)，从而允许在排序顺序上实现精确控制。还可以使用 Comparator 来控制某些数据结构(如有序集合 `SortedSet<E>` 或有序映射 `SortedMap<K,V>` )的顺序，或者为那些没有自然顺序的对象 Collection 提供排序。  
  
当且仅当对于一组元素 S 中的每个 e1 和 e2 而言，`c.compare(e1, e2)==0` 与 `e1.equals(e2)` 具有相等的布尔值时，Comparator c 强行对 S 进行的排序才叫做与 equals 一致的排序。  
  
当使用具有与 equals 不一致的强行排序能力的 Comparator 对有序 set(或有序映射)进行排序时，应该小心谨慎。假定一个带显式 Comparator c 的有序 set(或有序映射)与从 set S 中抽取出来的元素(或键)一起使用。如果 c 强行对 S 进行的排序是与 equals 不一致的，那么有序 set(或有序映射)将是行为“怪异的”。尤其是有序 set(或有序映射)将违背根据 equals 所定义的 set(或映射)的常规协定。  
  
例如，假定使用 Comparator c 将满足 `(a.equals(b) && c.compare(a, b) != 0)` 的两个元素 a 和 b 添加到一个空 TreeSet 中，则第二个 add 操作将返回 true(树 set 的大小将会增加)，因为从树 set 的角度来看，a 和 b 是不相等的，即使这与 Set.add 方法的规范相反。  
  
注：通常来说，让 Comparator 也实现 `java.io.Serializable` 是一个好主意，因为它们在可序列化的数据结构(像 TreeSet、TreeMap)中可用作排序方法。为了成功地序列化数据结构，Comparator(如果已提供)必须实现 Serializable。  
  
## compare方法  
`int compare(T o1, T o2)` //比较用来排序的两个参数。  
为防止记乱，可以这么记： `compare(b, a)` 代表的是 `a-b` 的值，正值代表 `a>b` ，所以 b 是小对象，那么 b 应该放前面，这时就需要做一次调换位置的操作了。  
- 参数：o1 - 要比较的第一个对象。o2 - 要比较的第二个对象。  
- 返回：根据第一个参数小于、等于或大于第二个参数分别返回负整数、零或正整数。  
- 抛出：ClassCastException - 如果参数的类型不允许此 Comparator 对它们进行比较。  
  
## equals 方法  
`boolean equals(Object obj)` // 指示某个其他对象是否“等于”此 Comparator  
注意，这里的 equals 指的是比较器 Comparator 的 equals，也即比较器之间是否 equals，而不是比较器要比较的对象之间是否 equals。  
- 覆盖：类 Object 中的 equals  
- 参数：obj - 要进行比较的引用对象。  
- 返回：仅当指定的对象也是一个 Comparator，并且强行实施与此 Comparator 相同的排序时才返回 true。  
- 另请参见：Object.equals(Object), Object.hashCode()  
  
此方法必须遵守 `Object.equals(Object)` 的常规协定。此外， 仅当 指定的对象也是一个 Comparator，并且强行实施与此 Comparator 相同的排序时，此方法才返回 true。因此，`comp1.equals(comp2)` 意味着对于每个对象引用 o1 和 o2 而言，都存在 `sgn(comp1.compare(o1, o2)) ==sgn (comp2.compare(o1, o2))`。  
  
注意，不 重写 `Object.equals(Object)` 方法总是 安全的。然而，在某些情况下，重写此方法可以允许程序确定两个不同的 Comparator 是否强行实施了相同的排序，从而提高性能。  
  
## 使用须知  
`sgn(expression)` //表示 signum 数学函数，该函数根据 expression 的值是负数、零还是正数，分别返回 -1、0 或 1 中的一个值  
- 实现程序必须确保对于所有的 x 和 y 而言，都存在 `sgn(compare(x, y)) == -sgn(compare(y, x))`。这意味着如果 `compare(y, x)` 抛出一个异常，则 `compare(x, y)` 也要抛出一个异常。  
- 实现程序还必须确保关系是可传递的：`((compare(x, y)>0) && (compare(y, z)>0))` 意味着 `compare(x, z)>0`。  
- 实现程序必须确保 `compare(x, y)==0` 意味着对于所有的 z 而言，都存在 `sgn(compare(x, z)) == sgn(compare(y, z))`。  
- 强烈推荐 `(compare(x, y)==0) == (x.equals(y))` 这种做法，但并不是 严格要求这样做。一般说来，任何违背这个条件的 Comparator 都应该清楚地指出这一事实。推荐的语言是“注意：此 Comparator 强行进行与 equals 不一致的排序。”  
  
# Object 的 equals 和 hashCode 方法  
## equals 方法  
`public boolean equals(Object obj)` //指示其他某个对象是否与此对象“相等”  
- 参数：obj - 要与之比较的引用对象。  
- 返回：如果此对象与 obj 参数相同，则返回 true；否则返回 false。  
- 另请参见：hashCode(), Hashtable  
  
equals 方法在非空对象引用上实现相等关系：  
- 自反性：对于任何非空引用值 x，x.equals(x) 都应返回 true。  
- 对称性：对于任何非空引用值 x 和 y，当且仅当 y.equals(x) 返回 true 时，x.equals(y) 才应返回 true。  
- 传递性：对于任何非空引用值 x、y 和 z，如果 x.equals(y) 返回 true，并且 y.equals(z) 返回 true，那么 x.equals(z) 应返回 true。  
- 一致性：对于任何非空引用值 x 和 y，多次调用 x.equals(y) 始终返回 true 或始终返回 false，前提是对象上 equals 比较中所用的信息没有被修改。  
- 对于任何非空引用值 x，x.equals(null) 都应返回 false。  
  
Object 类的 equals 方法实现对象上差别可能性最大的相等关系；即，对于任何非空引用值 x 和 y，当且仅当 x 和 y 引用同一个对象时，此方法才返回 true（x == y 具有值 true）。  
  
注意：当此方法被重写时，通常有必要重写 hashCode 方法，以维护 hashCode 方法的常规协定，该协定声明相等对象必须具有相等的哈希码。  
  
## hashCode 方法  
`public int hashCode()` //返回该对象的哈希码值  
- 返回：此对象的一个哈希码值。  
- 另请参见：equals(java.lang.Object), Hashtable  
  
支持此方法是为了提高哈希表（例如 java.util.Hashtable 提供的哈希表）的性能。  
  
hashCode 的常规协定是：  
- 在 Java 应用程序执行期间，在对同一对象多次调用 hashCode 方法时，必须一致地返回相同的整数，前提是将对象进行 equals 比较时所用的信息没有被修改。从某一应用程序的一次执行到同一应用程序的另一次执行，该整数无需保持一致。  
- 如果根据 equals(Object) 方法，两个对象是相等的，那么对这两个对象中的每个对象调用 hashCode 方法都必须生成相同的整数结果。  
- 如果根据 equals(Object) 方法，两个对象不相等，那么对这两个对象中的任一对象上调用 hashCode 方法不 要求一定生成不同的整数结果。但是，程序员应该意识到，为不相等的对象生成不同整数结果可以提高哈希表的性能。  
  
实际上，由 Object 类定义的 hashCode 方法确实会针对不同的对象返回不同的整数。这一般是通过将该对象的内部地址转换成一个整数来实现的，但是 JavaTM 编程语言不需要这种实现技巧。  
  
# 案例  
Comparable 接口是让类自身具有排序功能。  
Comparator 接口是对 Collection 进行整体排序的功能。  
  
## 探究两个接口方法的返回值  
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
  
## 元素为空的情况  
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
  
## Comparator 的使用案例  
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
