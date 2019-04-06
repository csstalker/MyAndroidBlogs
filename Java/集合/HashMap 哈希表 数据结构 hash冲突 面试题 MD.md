| Markdown版本笔记 | 我的GitHub首页 | 我的博客 | 我的微信 | 我的邮箱 |  
| :------------: | :------------: | :------------: | :------------: | :------------: |  
| [MyAndroidBlogs][Markdown] | [baiqiantao][GitHub] | [baiqiantao][博客] | bqt20094 | baiqiantao@sina.com |  
  
[Markdown]:https://github.com/baiqiantao/MyAndroidBlogs  
[GitHub]:https://github.com/baiqiantao  
[博客]:http://www.cnblogs.com/baiqiantao/  
  
HashMap 哈希表 数据结构 hash冲突 面试题 MD  
***  
目录  
===  

- [HashMap概述](#HashMap概述)
- [HashMap的数据结构](#HashMap的数据结构)
- [HashMap源码分析](#HashMap源码分析)
	- [常量](#常量)
	- [关键属性](#关键属性)
	- [构造方法](#构造方法)
	- [put 方法分析【重点】](#put-方法分析【重点】)
		- [putForNullKey](#putForNullKey)
		- [hash](#hash)
		- [indexFor](#indexFor)
		- [容量大小的设计考虑](#容量大小的设计考虑)
		- [addEntry](#addEntry)
		- [resize](#resize)
	- [get 方法分析](#get-方法分析)
	- [Fail-Fast机制](#Fail-Fast机制)
- [深入理解哈希表](#深入理解哈希表)
	- [问题](#问题)
	- [哈希表概述](#哈希表概述)
	- [哈希函数](#哈希函数)
	- [Java 和 Redis 的解决方案](#Java-和-Redis-的解决方案)
	- [总结](#总结)
- [面试题](#面试题)
	- [HashMap 面试题](#HashMap-面试题)
	- [HashMap与HashTable区别](#HashMap与HashTable区别)
  
# HashMap概述  
HashMap是基于`哈希表`的 Map 接口的实现。  
除了不同步和允许使用 null 之外，HashMap 类与 Hashtable 大致相同。  
此类不保证映射的顺序，特别是它不保证该顺序恒久不变。  
值得注意的是HashMap不是线程安全的，如果想要线程安全的HashMap，可以通过Collections类的静态方法`synchronizedMap`获得线程安全的HashMap。  
```java  
Map<Object, Object> map = Collections.synchronizedMap(new HashMap<>());  
```  
  
# HashMap的数据结构  
在java编程语言中，最基本的结构就是两种，一个是`数组`，另外一个是`模拟指针`(引用)，`所有的数据结构`都可以用这两个基本结构来构造的，HashMap也不例外。HashMap实际上是一个`链表的数组`的数据结构，每个元素存放链表头结点的数组，即数组和链表的结合体。  
  
HashMap之所以有相当快的查询速度主要是因为它是通过计算`散列码`来决定`存储的位置`。HashMap中主要是通过`key的hashCode`来计算hash值的，只要hashCode相同，计算出来的hash值就一样。  
  
如果存储的对象对多了，就有可能`不同的对象所算出来的hash值是相同的`，这就出现了所谓的`hash冲突`。学过数据结构的同学都知道，解决hash冲突的方法有很多，HashMap底层是通过`链表`来解决hash冲突的。  
  
![](index_files/953f3039-a850-458a-a75b-a50478a0cb3e.jpg)  
![](https://images2015.cnblogs.com/blog/795730/201607/795730-20160702194159202-333261561.jpg)  
图中，0~15部分即代表`哈希表`，也称为`哈希数组`，数组的每个元素都是一个`单链表的头节点`，`链表是用来解决冲突的`，`如果不同的key映射到了数组的同一位置处，就将其放入单链表中`。  
  
从上图我们可以发现哈希表是由数组+链表组成的，一个长度为16的数组中，每个元素存储的是一个链表的头结点`Bucket桶`。那么这些元素是按照什么样的规则存储到数组中呢。一般情况是通过`hash(key)%len`获得，也就是元素的`key的哈希值对数组长度取模`得到。比如上述哈希表中，12%16=12,28%16=12,108%16=12,140%16=12。所以12、28、108以及140都存储在数组下标为12的位置。  
  
HashMap其实也是一个`线性的数组`实现的，所以可以理解为其存储数据的容器就是一个线性数组。这可能让我们很不解，一个线性的数组怎么实现按`键值对`来存取数据呢？这里HashMap有做一些处理。  
  
首先HashMap里面实现一个静态内部类`Entry`(这个类在 JDK 1.8 中已被改成 `Node` 了)，其重要的属性有 key , value, next，从属性 key、value 我们就能很明显的看出来 Entry 就是 HashMap 键值对实现的一个基础 bean，我们上面说到 HashMap 的基础就是一个线性数组，这个数组就是`Entry[]`，Map里面的`内容`都保存在`Entry[]`里面。  
  
我们看看HashMap中Entry类的代码：  
```java  
//Entry 是单向链表。它是 “HashMap链式存储法” 对应的链表。它实现了 Map.Entry 接口  
static class Entry<K, V> implements Map.Entry<K, V> {  
   final K key;  
   V value;  
   Entry<K, V> next; //指向下一个节点  
   final int hash;  
  
   public final boolean equals(Object o) {  
      if (!(o instanceof Map.Entry)) return false;  
      Map.Entry e = (Map.Entry) o;  
      Object k1 = getKey(), k2 = e.getKey();  
      if (k1 == k2 || (k1 != null && k1.equals(k2))) { // 仅当两个Entry的key和value都相等时才返回true  
         Object v1 = getValue(), v2 = e.getValue();  
         if (v1 == v2 || (v1 != null && v1.equals(v2))) return true;  
      }  
      return false;  
   }  
  
   public final int hashCode() {  
      return (key == null ? 0 : key.hashCode()) ^ (value == null ? 0 : value.hashCode());  
   }  
}  
```  
本质上，`哈希表可以说就是数组链表，HashMap就是一个数组`，但是这个数组中的元素是 `Map.Entry`，每个 `Map.Entry` 其实就是一个 `key-value` 对，同时它持有一个`指向下一个元素的引用`，所以这就构成了`链表`。  
  
# HashMap源码分析  
注意，JDK8 中的很多源码甚至实现方式都和下面介绍的不太一样，但是基本的思想并没有改变，基本上拿下面这些解析去解释 JDK8 中的 HashMap 的实现方式也是完全没有问题的。  
  
## 常量  
在 HashMap 中定义了几个常量：  
```java  
static final int DEFAULT_INITIAL_CAPACITY = 1 << 4; //默认初始容量 = 16  
static final float DEFAULT_LOAD_FACTOR = 0.75f; //默认加载因子  
static final int TREEIFY_THRESHOLD = 8; //默认为链表，当链表长度大于 8 时，有可能会转化成树  
static final int MIN_TREEIFY_CAPACITY = 64; //在转变成树之前，还会有一次判断，只有键值对数量大于 64 才会发生转换  
static final int UNTREEIFY_THRESHOLD = 6; //哈希表扩容时，如果发现链表长度小于 6，则会由树重新退化为链表  
static final int MAXIMUM_CAPACITY = 1 << 30; //最大容量(用不到)  
```  
  
这些常量的意义：  
- DEFAULT_INITIAL_CAPACITY：默认的初始容量，也就是`默认会创建 16 个箱子`，箱子的个数如果太少，很容易触发`扩容`，如果太多，遍历哈希表会比较慢。  
- MAXIMUM_CAPACITY：哈希表最大容量，一般情况下只要内存够用，哈希表不会出现问题。  
- DEFAULT_LOAD_FACTOR：默认的负载因子。因此初始情况下，当键值对的数量大于 `16 * 0.75 = 12` 时，就会触发扩容。  
- TREEIFY_THRESHOLD：如果哈希函数不合理，即使扩容也无法减少箱子中链表的长度，因此 Java 的处理方案是当链表太长时，转换成红黑树。这个值表示当某个箱子中链表长度大于 8 时，有可能会转化成树。  
- MIN_TREEIFY_CAPACITY：在转变成树之前，还会有一次判断，只有键值对数量大于 64 才会发生转换。这是为了避免在哈希表建立初期，多个键值对恰好被放入了同一个链表中而导致不必要的转化。  
- UNTREEIFY_THRESHOLD：在哈希表扩容时，如果发现链表长度小于 6，则会由树重新退化为链表。  
  
## 关键属性  
```java  
transient Entry<K,V>[] table; //存储元素的实体数组  
transient int size; //存放元素的个数。The number of key-value mappings contained in this map.  
final float loadFactor; //负载因子。The load factor for the hash table.  
int threshold; //临界值 = 加载因子*容量，当实际大小超过临界值时，会进行扩容。The next size value at which to resize (capacity * load factor).  
transient int modCount; //被修改的次数  
initialCapacity：HashMap的最大容量，即为底层【数组】的长度(而非键值对的个数)。  
loadFactor：负载因子，loadFactor定义为：散列表的实际元素数目(n)/ 散列表的容量(m)。  
```  
  
**负载因子 loadFactor**  
- 负载因子可以表示Hsah表中元素的填满的程度。  
- 负载因子越大，填满的元素越多，好处是`空间利用率高了`，但冲突的机会加大了，`链表长度会越来越长，查找效率降低`。  
- 反之，负载因子越小，填满的元素越少，好处是`冲突的机会减小了`，但`空间浪费多了`，表中的数据将过于稀疏。  
- 因此，必须在"冲突的机会"与"空间利用率"之间寻找一种平衡与折衷， 这种平衡与折衷本质上是数据结构中有名的`时-空`矛盾的平衡与折衷。  
- 如果机器内存足够，并且想要提高查询速度的话可以将负载因子设置小一点；相反如果机器内存紧张，并且对查询速度没有什么要求的话可以将负载因子设置大一点。  
- 不过一般我们都不用去设置它，让它取默认值0.75就好了。  
  
## 构造方法  
下面看看HashMap的几个构造方法(以下为 JDK8 中的源码)：  
```java  
public HashMap() {  
    this.loadFactor = DEFAULT_LOAD_FACTOR; // all other fields defaulted  
}  
public HashMap(int initialCapacity) {  
    this(initialCapacity, DEFAULT_LOAD_FACTOR);  
}  
public HashMap(int initialCapacity, float loadFactor) {  
    // ... 判断值是否合法  
    this.loadFactor = loadFactor;  
    this.threshold = tableSizeFor(initialCapacity);  
}  
```  
  
tableSizeFor 为计算容量的方法，这是一个很典型的算法，算法的逻辑是：给定一个值 cap，返回一个不小于 cap 的又同时是 `2^n` 的最小值(至于为什么要把容量设置为2的n次幂，我们等下再看)：  
```java  
static final int tableSizeFor(int cap) {  
    int n = cap - 1;  
    n |= n >>> 1; //右移  
    n |= n >>> 2;  
    n |= n >>> 4;  
    n |= n >>> 8;  
    n |= n >>> 16;  
    return (n < 0) ? 1 : (n >= MAXIMUM_CAPACITY) ? MAXIMUM_CAPACITY : n + 1;  
}  
```  
  
## put 方法分析【重点】  
下面看看HashMap存储数据的过程是怎样的：  
```java  
public V put(K key, V value) {  
   if (key == null) return putForNullKey(value);// 若key为null，则将该键值对添加到table[0]中  
   int hash = hash(key.hashCode());  // 若key不为null，则计算该key的哈希值，然后将其添加到该哈希值对应的链表中  
   int i = indexFor(hash, table.length); //搜索指定hash值在对应table中的索引  
   for (Entry<K, V> e = table[i]; e != null; e = e.next) {  // 循环遍历  
      Object k;  
      if (e.hash == hash && ((k = e.key) == key || key.equals(k))) { //说明是同一 key  
         V oldValue = e.value; // 若该key已经存在，则用新的value取代旧的value  
         e.value = value;  
         e.recordAccess(this);  
         return oldValue;  
      }  
   }  
   modCount++; //修改次数+1  
   addEntry(hash, key, value, i); //将key-value添加到table[i]处  
   return null;  
}  
```  
  
从上面程序中可以看出：当系统决定存储 HashMap 中的 key-value 对时，`完全没有考虑 Entry 中的 value`，仅仅只是根据 `key` 来计算并决定每个 Entry 的存储位置。这说明我们完全可以把 Map 集合中的 value 当成 key 的附属，当系统决定了 key 的存储位置之后，value 随之保存在那里即可。  
  
### putForNullKey  
`putForNullKey(value)`方法的作用就是处理key值为null的情况：  
```java  
private V putForNullKey(V value) {  
   for (Entry<K, V> e = table[0]; e != null; e = e.next) {  
      if (e.key == null) {  //如果有key为null的对象存在，则用新的value取代旧的value  
         V oldValue = e.value;  
         e.value = value;  
         e.recordAccess(this);  
         return oldValue;  
      }  
   }  
   modCount++;  
   addEntry(0, null, value, 0); //如果key为null的话，则hash值为0，存的key也为null，存储在数组中索引为0的位置  
   return null;  
}  
```  
  
### hash  
我们再回去看看put方法中第3行，它是通过key的hashCode值计算hash码，下面是计算hash码的函数：  
```java  
static int hash(int h) {  
   // This function ensures that hashCodes that differ only by constant multiples常数倍数 at each bit position  
   // have a bounded有限 number of collisions冲突 (approximately大约 8 at default load factor).  
   h ^= (h >>> 20) ^ (h >>> 12);  
   return h ^ (h >>> 7) ^ (h >>> 4);  
}  
```  
  
> 注意：Java8 中的计算hash的方法和上面的有较大的区别，但道理是一样的  
  
### indexFor  
得到hash码之后就会通过hash码去计算出应该存储在数组中的索引，计算索引的函数如下：  
```java  
static int indexFor(int h, int length) { //根据hash值和数组长度算出索引值  
    return h & (length-1);  //这里不能随便算取，用 hash&(length-1) 是有原因的，这样可以确保算出来的索引是在数组大小范围内，不会超出  
}  
```  
  
这个我们要重点说下，我们一般对哈希表的`散列`很自然地会想到用`hash值对length取模`(即除法散列法)，Hashtable中也是这样实现的，这种方法基本能保证元素在哈希表中散列的比较`均匀`。  
  
但除法运算效率很低，HashMap中则通过`h&(length-1)`的方法来代替取模，同样实现了均匀的散列，且效率要高很多，这也是HashMap对Hashtable的一个改进。  
  
### 容量大小的设计考虑  
接下来，我们分析下为什么哈希表的容量一定要是2的整数次幂。  
  
首先，length为2的整数次幂的话，`h&(length-1)`就相当于对length取模，这样便保证了散列的均匀，同时也提升了效率；  
  
其次，length为2的整数次幂为偶数，这样length-1为奇数，奇数的最后一位是1，这样便保证了`h&(length-1)`的最后一位可能为0，也可能为1(这取决于h的值)，即与后的结果可能为偶数，也可能为奇数，这样便可以保证散列的均匀性。  
  
而如果length为奇数的话，很明显length-1为偶数，它的最后一位是0，这样`h&(length-1)`的最后一位肯定为0，即只能为偶数，这样任何hash值都只会被散列到数组的偶数下标位置上，这便`浪费了一半的空间`。  
  
因此，length取2的整数次幂，是为了使不同hash值发生碰撞的概率较小，这样就能使元素在哈希表中均匀地散列。  
  
### addEntry  
继续看 addEntry() 方法：  
```java  
void addEntry(int hash, K key, V value, int bucketIndex) {  
    Entry<K,V> e = table[bucketIndex]; //如果要加入的位置有值，将该位置原先的值设置为新entry的next,也就是新entry链表的下一个节点  
    table[bucketIndex] = new Entry<>(hash, key, value, e);  
    if (size++ >= threshold) resize(2 * table.length); //如果大于临界值就以2的倍数扩容  
}  
```  
  
细节：  
- 参数 bucketIndex 就是indexFor函数计算出来的索引值  
- 第2行代码是取得数组中索引为 bucketIndex 的Entry对象  
- 第3行就是用 hash、key、value 构建一个新的 Entry 对象放到索引为 bucketIndex 的位置，并且将该位置原先的对象设置为新对象的next构成链表。  
- 第4行就是判断put后size是否达到了临界值threshold，如果达到了临界值就要进行扩容，HashMap扩容是扩为原来的两倍。  
  
根据上面 put 方法的源代码可以看出，当程序试图将一个key-value对放入HashMap中时，程序首先根据该 key 的 hashCode() 返回值决定该 Entry 的存储位置：  
- 如果这两个 Entry 的 key 的 hashCode() 相同，那它们的存储位置相同。  
    - 如果这两个 Entry 的 key 通过 equals 比较返回 true，新添加 Entry 的 value 将覆盖集合中原有 Entry 的 value  
    - 如果这两个 Entry 的 key 通过 equals 比较返回 false，新添加的 Entry 将与集合中原有 Entry 形成 Entry 链，而且新添加的 Entry 位于 Entry 链的头部  
  
### resize  
resize()方法如下：  
```java  
void resize(int newCapacity) {  
   Entry[] oldTable = table;  
   int oldCapacity = oldTable.length;  
   if (oldCapacity == MAXIMUM_CAPACITY) {  
      threshold = Integer.MAX_VALUE;  
      return;  
   }  
   Entry[] newTable = new Entry[newCapacity]; //重新调整HashMap的大小，newCapacity是调整后的大小  
   transfer(newTable);//用来将原先table的元素全部移到newTable里面  
   table = newTable;  //再将newTable赋值给table  
   threshold = (int) (newCapacity * loadFactor);//重新计算临界值  
}  
```  
  
新建了一个HashMap的底层数组，上面代码中第9行为调用`transfer`方法，`将HashMap的全部元素添加到新的HashMap中`，并`重新计算元素在新的数组中的索引位`置。  
  
当HashMap中的元素越来越多的时候，hash冲突的几率也就越来越高，因为数组的长度是固定的。  
所以为了提高查询的效率，就要对HashMap的数组进行扩容。  
数组扩容这个操作也会出现在`ArrayList`中，这是一个常用的操作。  
而在HashMap数组扩容之后，最消耗性能的点就出现了：`原数组中的数据必须重新计算其在新数组中的位置，并放进去`，这就是resize。  
  
那么HashMap什么时候进行扩容呢？  
当HashMap中的元素个数超过`数组大小*loadFactor`时，就会进行数组扩容。  
loadFactor的默认值为0.75，这是一个折中的取值。  
也就是说，默认情况下，数组大小为16，那么当HashMap中元素个数超过`16*0.75=12`的时候，就把数组的大小扩展为` 2*16=32`，即扩大一倍，然后重新计算每个元素在数组中的位置。  
扩容是需要进行数组复制的，复制数组是非常消耗性能的操作，所以如果我们已经预知HashMap中元素的个数，那么预设元素的个数能够有效的提高HashMap的性能。  
  
## get 方法分析  
数据读取：  
```java  
public V get(Object key) {  
   if (key == null) return getForNullKey(); //如果 key 为空，返回 key 为空的键值对  
   int hash = hash(key.hashCode()); //计算key的hashCode  
   for (Entry<K, V> e = table[indexFor(hash, table.length)];  
        e != null;  
        e = e.next) {  
      Object k; //先通过hash判断是否相同(为了节省判断时间)，再通过key的【equals】判断是否相同  
      if (e.hash == hash && ((k = e.key) == key || key.equals(k))) return e.value; //找到 key 的话返回  
   }  
   return null; //没找到的返回null  
}  
```  
  
从 HashMap 中get元素时，首先计算key的hashCode，找到数组中对应位置的某一元素，然后`通过key的equals方法`在对应位置的链表中找到需要的元素。  
  
归纳起来简单地说，HashMap 在底层将 key-value 当成一个整体进行处理，这个整体就是一个 Entry 对象。  
HashMap 底层采用一个 `Entry[]` 数组来保存所有的 key-value 对，当需要存储一个 Entry 对象时，会根据`hash`算法来决定其在`数组中的存储位置`，再根据`equals`方法决定其在该数组位置上的链表中的存储位置；当需要取出一个Entry时，也会根据`hash`算法找到其在数组中的存储位置，再根据`equals`方法从该位置上的链表中取出该Entry。  
  
## Fail-Fast机制  
我们知道 HashMap 不是线程安全的，因此如果在使用迭代器的过程中有其他线程修改了 map，那么将抛出 `ConcurrentModificationException`，这就是所谓 `fail-fast` 策略。  
  
这一策略在源码中是通过 `modCount` 域实现的，modCount 就是修改次数，对 HashMap 内容的修改都将增加这个值，那么在迭代器初始化过程中会将这个值赋给迭代器的 `expectedModCount`。  
  
```java  
private abstract class HashIterator<E> implements Iterator<E> {  
   Entry<K, V> next;    // next entry to return  
   int expectedModCount;    // For fast-fail  
   int index;        // current slot  
   Entry<K, V> current;    // current entry  
     
   HashIterator() {  
      expectedModCount = modCount;  
      if (size > 0) { // advance to first entry  
         Entry[] t = table;  
         while (index < t.length && (next = t[index++]) == null) ;//不执行任何语句  
      }  
   }  
     
   public final boolean hasNext() {  
      return next != null;  
   }  
     
   final Entry<K, V> nextEntry() {  
      if (modCount != expectedModCount) throw new ConcurrentModificationException(); //如果不相等就表示已经有其他线程修改了Map  
      Entry<K, V> e = next;  
      if (e == null) throw new NoSuchElementException();  
      if ((next = e.next) == null) {  
         Entry[] t = table;  
         while (index < t.length && (next = t[index++]) == null) ;//不执行任何语句  
      }  
      current = e;  
      return e;  
   }  
     
   public void remove() {  
      if (current == null)  throw new IllegalStateException();  
      if (modCount != expectedModCount) throw new ConcurrentModificationException(); //如果不相等就表示已经有其他线程修改了Map  
      Object k = current.key;  
      current = null;  
      HashMap.this.removeEntryForKey(k);  
      expectedModCount = modCount;  
   }  
}  
```  
  
在迭代过程中，判断modCount跟expectedModCount是否相等，如果不相等就表示已经有其他线程修改了Map  
  
注意到modCount声明为`volatile`，保证线程之间修改的可见性。  
  
在HashMap的API中指出：  
> 由所有HashMap类的 "collection 视图方法" 所返回的迭代器都是快速失败的：在迭代器创建之后，如果从结构上对映射进行修改，除非通过迭代器本身的 remove 方法，其他任何时间任何方式的修改，迭代器都将抛出 ConcurrentModificationException。因此，面对并发的修改，迭代器很快就会完全失败，而不冒在将来不确定的时间发生任意不确定行为的风险。  
  
> 注意，迭代器的快速失败行为不能得到保证，一般来说，存在非同步的并发修改时，不可能作出任何坚决的保证。快速失败迭代器尽最大努力抛出 ConcurrentModificationException。因此，编写依赖于此异常的程序的做法是错误的，正确做法是：迭代器的快速失败行为应该仅用于检测程序错误。  
  
# 深入理解哈希表  
## 问题  
有两个字典，分别存有 100 条数据和 10000 条数据，如果用一个不存在的 key (目的是去逐个遍历)去查找数据，在哪个字典中速度更快？  
  
有些计算机常识的读者都会立刻回答：一样快，底层都用了哈希表，查找的时间复杂度为 `O(1)`。  
  
然而实际情况真的是这样么？  
答案是否定的，存在少部分情况两者速度不一致。  
  
## 哈希表概述  
实际上绝大多数语言中`字典`都通过哈希表实现。  
- 哈希表的本质是一个数组  
- 数组中每一个元素称为一个箱子(bin)  
- 箱子中存放的是键值对  
  
哈希表的存储过程：  
- 根据 `key` 计算出它的哈希值 h。  
- 假设箱子的个数为 n，那么这个键值对应该放在第 (h % n) 个箱子中。  
- 如果该箱子中已经有了键值对，就使用开放寻址法或者拉链法`解决冲突`。  
  
在使用拉链法解决哈希冲突时，每个箱子其实是一个`链表`，属于同一个箱子的所有键值对都会排列在链表中。  
  
哈希表还有一个重要的属性：`负载因子(load factor)`，它用来衡量哈希表的 `空/满 程度`，一定程度上也可以体现查询的效率。  
负载因子越大，意味着哈希表越满，越容易导致冲突，性能也就越低。  
因此，一般来说，当负载因子大于某个常数(可能是 1，或者 0.75 等)时，哈希表将自动扩容。  
  
哈希表在自动扩容时，一般会创建`两倍`于原来个数的箱子，因此即使 key 的哈希值不变，对箱子个数取余的结果也会发生改变，因此`所有键值对的存放位置都有可能发生改变`，这个过程也称为`重哈希(rehash)`。  
  
`哈希表的扩容并不总是能够有效解决负载因子过大的问题`。  
假设所有 key 的哈希值都一样，那么即使扩容以后他们的位置也不会变化。虽然负载因子会降低，但实际存储在每个箱子中的链表长度并不发生改变，因此也就不能提高哈希表的查询性能。  
  
基于以上总结，细心的读者可能会发现哈希表的两个问题:  
- 如果哈希表中本来箱子就比较多，`扩容时需要重新哈希并移动数据，性能影响较大`。  
- 如果哈希函数设计不合理，哈希表在极端情况下会变成`线性表(链表)`，性能极低。  
  
我们分别通过 Java 和 Redis 的源码来理解以上问题，并看看他们的解决方案。  
  
## 哈希函数  
学过概率论的读者也许知道，理想状态下哈希表的每个箱子中，元素的数量遵守`泊松分布`，当负载因子为 0.75 时，箱子中元素个数和概率的关系如下：  
  
| 数量  | 概率 | 概率 |  
| :------------: | :------------: | :------------: |  
| 0  | 0.60653066 |  60.7% |  
| 1  | 0.30326533 |  30.3% |  
| 2  | 0.07581633 |  7.6% |  
| 3  | 0.01263606 |  1.3% |  
| 4  | 0.00157952 |  
| 5  | 0.00015795 |  
| 6  | 0.00001316 |  
| 7  | 0.00000094 |  
| 8  | 0.00000006 |  
| 6  | 0.00001316 |  
| 7  | 0.00000094 |  
| 8  | 0.00000006 |  
  
这就是为什么箱子中链表长度超过 8 以后要变成红黑树，因为在正常情况下出现这种现象的几率小到忽略不计，一旦出现，几乎可以认为是`哈希函数`设计有问题导致的。  
  
Java 对哈希表的设计一定程度上避免了不恰当的哈希函数导致的性能问题，每一个箱子中的链表可以与红黑树切换。  
  
## Java 和 Redis 的解决方案  
Java 的优点在于当哈希函数不合理导致链表过长时，会使用`红黑树`来保证插入和查找的效率。缺点是当哈希表比较大时，如果`扩容`会导致瞬时效率降低。  
  
Redis 通过`增量式扩容`解决了这个缺点，同时`拉链法`的实现(放在链表头部)值得我们学习。Redis 还提供了一个经过严格测试，表现良好的默认哈希函数，避免了链表过长的问题。  
  
Objective-C 的实现和 Java 比较类似，当我们需要重写 `isEqual()` 方法时，还需要重写 `hash` 方法。这两种语言并没有提供一个通用的、默认的哈希函数，主要是考虑到 `isEqual()` 方法可能会被重写，两个内存数据不同的对象可能在语义上被认为是相同的。如果使用默认的哈希函数就会得到不同的哈希值，这两个对象就会同时被添加到 `NSSet` 集合中，这可能违背我们的期望结果。  
  
根据我的了解，Redis 并不支持重写哈希方法，难道 Redis 就没有考虑到这个问题么？  
实际上还要从 Redis 的定位说起。由于它是一个高效的，Key-Value 存储系统，`它的 key 并不会是一个对象，而是一个用来唯一确定对象的标记`。  
一般情况下，如果要存储某个用户的信息，key 的值可能是这样`user:100001`。Redis 只关心 key 在内存中的数据，因此只要是可以用二进制表示的内容都可以作为 key，比如一张图片。  
Redis 支持的数据结构包括哈希表和集合(Set)，但是其中的数据类型只能是字符串。因此 `Redis 并不存在对象等同性的考虑`，也就可以提供默认的哈希函数了。  
  
Redis、Java、Objective-C 之间的异同再次证明了一点：没有完美的架构，只有满足需求的架构。  
  
## 总结  
回到文章开头的问题中来，完整的答案是：  
- 在`理想的哈希函数`下，无论字典多大，搜索速度都是一样快。  
- 在 `Redis` 中，得益于`自动扩容和默认哈希函数`，两者查找速度一样快。  
- 在 `Java` 和 Objective-C 中，如果哈希函数不合理，返回值过于集中，会导致`大字典更慢`。  
- Java 由于存在`链表和红黑树互换机制`，搜索时间呈`对数 O(log(n))`级增长，而非`线性O(n)`增长。  
  
# 面试题  
## HashMap 面试题  
**HashMap特性？**  
HashMap存储`键值对`，实现`快速存取`数据；允许`null`键/值；`非同步`；`不保证有序`(比如插入的顺序)，实现map接口。  
  
**HashMap的原理，内部数据结构？**  
- HashMap是基于hashing的原理，底层使用`哈希表`(`数组 + 链表`)实现  
- 里边最重要的两个方法put、get，使用`put(key, value)`存储对象到HashMap中，使用`get(key)`从HashMap中获取对象。   
- 存储对象时，我们将`K/V`传给put方法时，它调用`key的hashCode`计算hash从而得到bucket位置，进一步存储  
- HashMap会根据当前bucket的占用情况自动调整容量(超过Load Facotr则resize为原来的`2倍`)  
- 获取对象时，我们将K传给get，它调用hashCode计算hash从而得到bucket位置，`并进一步调用equals()方法确定键值对`  
- 如果发生碰撞的时候，Hashmap通过`链表`将产生碰撞冲突的元素组织起来，在Java 8中，如果一个bucket中碰撞冲突的元素超过某个限制(默认是`8`)，则使用`红黑树`来替换链表，从而提高速度。  
  
**讲一下 HashMap 中 put 方法过程？**  
- 对key的hashCode做hash操作，然后再计算在bucket中的index(1.5 HashMap的哈希函数)  
- 如果没碰撞直接放到bucket里；   
- 如果碰撞了，以链表的形式存在buckets后；   
- 如果节点已经存在就替换old value(保证key的唯一性)   
- 如果bucket满了(超过阈值，阈值=`loadfactor*current capacity`，load factor默认0.75)，就要resize。  
  
**get()方法的工作原理？**  
- 通过对key的hashCode()进行hashing，并计算下标( n-1 & hash)，从而`获得buckets的位置`。  
- 如果产生碰撞，则利用`key.equals()`方法去`链表`中查找对应的节点。  
  
**HashMap中hash函数怎么是是实现的？**  
- 对key的hashCode做hash操作：高16bit不变，低16bit和高16bit做了一个异或  
- 通过位操作得到下标index：`h & (length-1)`  
  
**还有哪些 hash 的实现方式？**  
还有数字分析法、平方取中法、分段叠加法、 除留余数法、 伪随机数法。  
  
**HashMap 怎样解决冲突？**  
HashMap中处理冲突的方法实际就是`链地址法`，内部数据结构是`数组+单链表`。  
  
**当两个键的hashcode相同会发生什么？**  
- 因为两个键的Hashcode相同，所以它们的bucket位置相同，会发生“碰撞”。  
- HashMap使用链表存储对象，这个Entry(包含有键值对的Map.Entry对象)会存储在链表中。  
  
**抛开 HashMap，hash 冲突有那些解决办法？**  
开放定址法、链地址法、再哈希法。  
  
**如果两个键的hashcode相同，你如何获取值对象？**  
- 重点在于理解hashCode()与equals()。   
- 通过对key的hashCode()进行hashing，并计算下标( n-1 & hash)，从而获得buckets的位置。  
- 两个键的hashcode相同会产生碰撞，则利用`key.equals()`方法去`链表或树`（java1.8）中去查找对应的节点。  
  
**针对 HashMap 中某个 Entry 链太长，查找的时间复杂度可能达到 O(n)，怎么优化？**  
- 将链表转为`红黑树`，实现 `O(logn)` 时间复杂度内查找。  
- JDK1.8 已经实现了。  
  
**如果HashMap的大小超过了负载因子(load factor)定义的容量，怎么办？**  
扩容。这个过程也叫作`rehashing`，大致分两步：  
- 扩容：容量扩充为原来的两倍（2 * table.length）；   
- 移动：对每个节点`重新计算哈希值`，`重新计算每个元素在数组中的位置`，将原来的元素`移动`到新的哈希表中。   
   
补充：   
- loadFactor：加载因子。默认值DEFAULT_LOAD_FACTOR = 0.75f；   
- capacity：容量；   
- threshold：阈值=`capacity*loadFactor`。当HashMap中存储数据的数量达到threshold时，就需要将HashMap的容量加倍；   
- size：HashMap的大小，它是HashMap保存的键值对的数量。  
  
**为什么String, Interger这样的类适合作为键？**  
- String, Interger这样的类作为HashMap的键是再适合不过了，而且String最为常用。   
- 因为String对象是`不可变的`，而且已经`重写了equals()和hashCode()`方法了。   
- 不可变性是必要的，因为为了要计算hashCode()，就要`防止键值改变`，如果键值在放入时和获取时返回不同的hashcode的话，那么就不能从HashMap中找到你想要的对象。  
- 不可变性还有其他的优点，如线程安全。   
- 因为获取对象的时候要用到equals()和hashCode()方法，那么键对象正确的重写这两个方法是非常重要的。`如果两个不相等的对象返回不同的hashcode的话，那么碰撞的几率就会小些，这样就能提高HashMap的性`能。  
  
## HashMap与HashTable区别  
Hashtable可以看做是线程安全版的HashMap，两者几乎“等价”（当然还是有很多不同）。  
Hashtable几乎在每个方法上都加上synchronized（同步锁），实现线程安全。  
HashMap可以通过 Collections.synchronizeMap(hashMap) 进行同步。  
  
区别  
- HashMap继承于AbstractMap，而Hashtable继承于Dictionary；   
- 线程安全不同。Hashtable的几乎所有函数都是同步的，即它是线程安全的，支持多线程。而HashMap的函数则是非同步的，它不是线程安全的。若要在多线程中使用HashMap，需要我们额外的进行同步处理；   
- null值。HashMap的key、value都可以为null。Hashtable的key、value都不可以为null；   
- 迭代器(Iterator)。HashMap的迭代器(Iterator)是fail-fast迭代器，而Hashtable的enumerator迭代器不是fail-fast的。所以当有其它线程改变了HashMap的结构（增加或者移除元素），将会抛出ConcurrentModificationException。   
- 容量的初始值和增加方式都不一样：HashMap默认的容量大小是16；增加容量时，每次将容量变为“原始容量x2”。Hashtable默认的容量大小是11；增加容量时，每次将容量变为“原始容量x2 + 1”；   
- 添加key-value时的hash值算法不同：HashMap添加元素时，是使用自定义的哈希算法。Hashtable没有自定义哈希算法，而直接采用的key的hashCode()。   
- 速度。由于Hashtable是线程安全的也是synchronized，所以在单线程环境下它比HashMap要慢。如果你不需要同步，只需要单一线程，那么使用HashMap性能要好过Hashtable。  
  
2018-7-14  
