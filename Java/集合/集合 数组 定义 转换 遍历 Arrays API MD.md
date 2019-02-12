| Markdown版本笔记 | 我的GitHub首页 | 我的博客 | 我的微信 | 我的邮箱 |  
| :------------: | :------------: | :------------: | :------------: | :------------: |  
| [MyAndroidBlogs][Markdown] | [baiqiantao][GitHub] | [baiqiantao][博客] | bqt20094 | baiqiantao@sina.com |  
  
[Markdown]:https://github.com/baiqiantao/MyAndroidBlogs  
[GitHub]:https://github.com/baiqiantao  
[博客]:http://www.cnblogs.com/baiqiantao/  
  
集合 数组 定义 转换 遍历 Arrays API  
***  
目录  
===  

- [数组和集合的区别](#数组和集合的区别)
- [常用操作](#常用操作)
	- [数组常用操作](#数组常用操作)
		- [定义数组](#定义数组)
		- [List转数组](#list转数组)
			- [常规用法](#常规用法)
			- [常见的坑](#常见的坑)
		- [遍历数组](#遍历数组)
	- [集合常用操作](#集合常用操作)
		- [数组转 List](#数组转-list)
		- [遍历List、Set](#遍历list、set)
		- [遍历Map](#遍历map)
	- [空数组、空集合的处理](#空数组、空集合的处理)
		- [转换](#转换)
		- [打印](#打印)
		- [遍历](#遍历)
- [Arrays 数组工具类的 API](#arrays-数组工具类的-api)
	- [asList 方法](#aslist-方法)
		- [实现原理](#实现原理)
		- [使用注意事项](#使用注意事项)
	- [sort 方法](#sort-方法)
	- [binarySearch 方法](#binarysearch-方法)
		- [源码分析](#源码分析)
		- [二分查找](#二分查找)
	- [copyOf、copyOfRange 方法](#copyof、copyofrange-方法)
		- [相关的几个方法](#相关的几个方法)
		- [源码分析](#源码分析)
		- [测试代码](#测试代码)
	- [equals 和 deepEquals](#equals-和-deepequals)
	- [fill 方法](#fill-方法)
	- [toString deepToString 方法](#tostring-deeptostring-方法)
	- [hashCode deepHashCode 方法](#hashcode-deephashcode-方法)
  
# 数组和集合的区别  
- 数组的`大小是固定的`，只能存放`同一类型`(所声明的类型及其子类)的数据，可以存放基本类型和引用类型数据，数组定义后，元素都`有默认值`(原始类型为原始类型的默认值，引用类型为null)。  
- 集合的`大小是可变的`，可以存储`任意类型`的元素(但是不建议这么做，建议指定`泛型`以限定可以存储的元素类型)，并且只能存储引用类型的数据(存储基本类型数据时，会被自动装包成相应的包装类)，集合定义后，元素`没有默认值`(必须先声明才能引用)。  
  
# 常用操作  
## 数组常用操作  
### 定义数组  
第一种方式，先声明，再设置大小并初始化：  
```java  
String[] array = null;  
array = new String[5];//设置大小并初始化  
array[0] = "呵呵";  
```  
  
第二种方式，一气呵成：  
```java  
String[] array = new String[] { "a", "b", "c", "d", "e" };  
String[] array2 = { "a", "b", "c", "d", "e" };  
```  
  
下面这两种是定义了一个长度为 0 的数组，由于数组长度不可变，所以以后不能有任何对数组元素的操作：  
```java  
String[] array = new String[] {};  
String[] array = new String[0];  
```  
  
下面这几种定义方式不合法，因为定义数组必须指定数组的长度，且不能带小括号：  
```java  
String[] array = new String[];  
String[] array = new String();  
String[] array = new String[]();  
String[] array = new String()[];  
```  
  
### List转数组  
#### 常规用法  
```java  
List<String> list = new ArrayList<>();  
list.add("a");  
list.add("b");  
list.add("c");  
String[] array = list.toArray(new String[list.size()]);  
System.out.println(Arrays.toString(array)); //[a, b, c]  
```  
  
#### 常见的坑  
**对于普通的 ArrayList**  
```java  
List<String> list = new ArrayList<>();  
list.add("a");  
list.add("b");  
list.add("c");  
```  
坑1：  
```java  
String[] array = list.toArray(new String[0]);  
System.out.println(Arrays.toString(array));  //结果仍为[a, b, c]  
```  
坑2：  
```java  
String[] array = list.toArray(new String[list.size() + 1]);  
System.out.println(Arrays.toString(array));  //结果为[a, b, c, null]  
```  
坑3：  
```java  
String[] temp = new String[] { "aa", "bb", "cc", "dd", "ee" };  
String[] array = list.toArray(temp);  
System.out.println(Arrays.toString(array)); //[a, b, c, null, ee]  
```  
坑4：  
```java  
String[] array = (String[]) list.toArray();  
// 异常 ClassCastException: [Ljava.lang.Object; cannot be cast to [Ljava.lang.String;  
```  
  
**对于通过`Arrays.asList`转换过来的`Arrays$ArrayList`**  
```java  
List<String> list = Arrays.asList("a", "b", "c");  
System.out.println(list.getClass().getName()); //java.util.Arrays$ArrayList  
System.out.println(list instanceof ArrayList); //false  
  
String[] array = (String[]) list.toArray();  
System.out.println(Arrays.toString(array)); // 不会异常[a, b, c]  
```  
  
### 遍历数组  
使用 `Arrays.toString`：  
```java  
System.out.println(Arrays.toString(arr));//直接打印数组本身的话不会打印数组中的内容  
```  
  
使用普通 for 循环：  
```java  
for (int i = 0, j = arr.length; i < j; i++) {//普通for  
    System.out.println(i + " - " + arr[i]);  
}  
```  
  
使用增强 for 循环：  
```java  
for (String string : arr) {//增强for  
    System.out.println(string);  
}  
```  
  
## 集合常用操作  
### 数组转 List  
使用 `Arrays.asList` 方法：  
```java  
List<String> mList = Arrays.asList("包青天a","包青天b","包青天c");//可变参数  
List<String> mList = Arrays.asList();//结果不是null，而是长度为0的集合。return new ArrayList<>(list);  
  
String[] array = { "包青天a", "包青天b", "包青天c" };  
List<String> mList = Arrays.asList(array);//直接传一个数组和直接传内容是一样的  
List<String[]> mList = Arrays.asList(array, array);//以可变参数的形式传多个数组的话，集合中的元素就不是String而是String[]了  
```  
  
### 遍历List、Set  
直接打印：  
```java  
System.out.println(mList); //和数组不一样，直接打印集合其实就是打印集合中的元素  
```  
  
使用普通 for 循环(只有 List 才可以这样操作，Set 是不能像 List 那样操作下标的)：  
```java  
for (int i = 0, j = mList.size(); i < j; i++) {  
    System.out.println(i + " - " + mList.get(i));  
}  
```  
  
使用增强 for 循环：  
```java  
for (String string : mList) {  
    System.out.println(string);  
}  
```  
  
使用 Iterator ：  
```java  
Iterator<String> it = mList.iterator();  
while (it.hasNext()) {  
    System.out.println(it.next());  
}  
```  
      
使用 ListIterator(只有 List 才可以这样操作)：  
```java  
ListIterator<String> it = mList.listIterator(); //可以双向遍历  
while (it.hasNext()) {  
    System.out.println(it.next());//执行完后指针移到了最后，这有这样才能使用hasPrevious遍历  
}  
  
while (it.hasPrevious()) {  
    System.out.println(it.previous());  
}  
```  
  
### 遍历Map  
直接打印：  
```java  
System.out.println(map);  
```  
  
使用增强 for 循环(先遍历 keySet，再组装 K-V)：  
```java  
for (String key : map.keySet()) {  
    String value = map.get(key);  
    System.out.println(key + "---" + value);  
}  
```  
  
使用增强 for 循环(直接遍历 K-V)：  
```java  
for (Map.Entry<String, String> entry : map.entrySet()) {  
    System.out.println(entry);  
}  
```  
  
使用 Iterator(先获取 keySet 的 Iterator，再组装 K-V)：  
```java  
Iterator<String> iterator = map.keySet().iterator();  
while (iterator.hasNext()) {  
    String key = iterator.next();  
    String value = map.get(key);  
    System.out.println(key + ":" + value);  
}  
```  
  
使用 Iterator(通过 entrySet 直接遍历 K-V)：  
```java  
Iterator<Map.Entry<String, String>> iterator2 = map.entrySet().iterator();  
while (iterator2.hasNext()) {  
    System.out.println(iterator2.next());  
}  
```  
  
## 空数组、空集合的处理  
### 转换  
```java  
System.out.println(Arrays.asList().size()); //0  
  
String[] array = null;  
System.out.println(Arrays.asList(array).size()); // NullPointerException  
  
System.out.println(Arrays.asList(null).size()); // NullPointerException  
System.out.println(Arrays.asList(null, null).size()); //2  
  
System.out.println(new ArrayList<>().toArray().length); //0  
```  
  
### 打印  
```java  
String[] arr = null;  
System.out.println(Arrays.toString(arr));//调用 Arrays.toString 不会异常，打印 null  
  
List<String> mList = null;  
System.out.println(mList); //null  
System.out.println(new ArrayList<String>()); //[]  
```  
  
### 遍历  
```java  
String[] arr = null;  
for (String string : arr) { //这里就会报 NullPointerException ，不管是数组还是集合都一样，遍历前必须判空  
    System.out.println(string);  
}  
String[] arr = null;  
for (int i = 0, j = arr == null ? 0 : arr.length; i < j; i++) { //加上这么一个判断就很安全了，不管是数组还是集合都一样  
    System.out.println(i + " - " + arr[i]);  
}  
```  
  
# Arrays 数组工具类的 API  
此类包含用来操作数组(比如排序和搜索)的各种方法。此类还包含一个允许`将数组作为List`来查看的静态工厂。  
除非特别注明，否则如果指定数组引用为 null，则此类中的方法都会抛出 NullPointerException。  
此类是 `Java Collections Framework` 的成员。  
  
## asList 方法  
`static <T> List<T>  asList(T... a)`    返回一个受指定数组支持的固定大小的列表。  
- 对返回列表的更改会“直接写”到数组。  
- 此方法同 Collection.toArray() 一起，充当了基于数组的 API 与基于 collection 的 API 之间的桥梁。  
- 返回的列表是可序列化的，并且实现了 RandomAccess。  
- 此方法还提供了一个创建固定长度的列表的便捷方法，该列表被初始化为包含多个元素：`List<String> stooges = Arrays.asList("Larry", "Moe", "Curly")`;  
  
### 实现原理  
```java  
public static <T> List<T> asList(T... a) {  
    return new ArrayList<>(a);//注意，这里的ArrayList不是我们通常用的ArrayList，而是Arrays类中定义的一个内部类  
}  
  
private static class ArrayList<E> extends AbstractList<E> implements RandomAccess, java.io.Serializable {  
    private final E[] a;  
    ArrayList(E[] array) {  
        a = Objects.requireNonNull(array);//如果 array 为 null 抛出 NullPointerException，否则直接将 array 的引用赋给 a  
        //因为 a 和 array 这两个数组引用的是同一块内存地址，且后续对此 ArrayList 的操作实际都是对 a 的操作，所以也会影响到原始数组  
    }  
}  
  
//因为此 ArrayList 没有重新 AbstractList 中的 add、remove、clear等方法，所以不能调用这几个方法，否则都是直接抛出异常  
public abstract class AbstractList<E> extends AbstractCollection<E> implements List<E> {  
    public void add(int index, E element) {  
        throw new UnsupportedOperationException();  
    }  
}  
```  
  
### 使用注意事项  
```java  
String[] array = new String[] { "a", "b", };  
List<String> mList = Arrays.asList(array); //如下问题和通过 Arrays.asList("a", "b") 方式来生成集合是完全一样的  
  
//1、不可以将 List 强转为 ArrayList .从上述源码分析可知，这两个 ArrayList 并不是同一个类，所以不能强转  
ArrayList<String> list2 = (ArrayList<String>) list;//ClassCastException: java.util.Arrays$ArrayList cannot be cast to java.util.ArrayList  
  
//2、对返回列表的更改会“直接写”到数组，所以当修改转换后的集合中的元素时，原始数组中的元素也会更改  
mList.set(0, "包青天");  
System.out.println(Arrays.toString(array));//[包青天, b]  
  
//3、因为数组的长度不能改变，所以对转换而来的集合的长度改变的操作都是不合法的，会报 java.lang.UnsupportedOperationException  
mList.add("111"); // Arrays.asList("a", "b").add("111"); 也同样会报异常  
mList.remove(0);//  
list.clear();  
```  
  
## sort 方法  
注意：默认都是按升序排序的，如数字类型按照 数字由小到大 的顺序，字符串按照 abcd 的顺序。  
- static void  sort(byte[] a)  对指定的 byte、char、double、float、int、long、short 型数组按数字升序进行排序。  
- static void  sort(byte[] a, int fromIndex, int toIndex)   指定范围，包括fromIndex ，不包括toIndex  
- static void  sort(Object[] a)  根据元素的自然顺序对指定对象数组按升序进行排序。  
- static <T> void  sort(T[] a, Comparator<? super T> c)  根据指定比较器产生的顺序对指定对象数组进行排序。  
  
```java  
String[] array = new String[] { "c", "a", "b", "ab", "aa", "a" };  
Arrays.sort(array);//集合中不能有 null ，否则会报NullPointerException，这种情况下需要自定义比较器  
System.out.println(Arrays.toString(array));//[a, a, aa, ab, b, c]  
  
array = new String[] { "c", "a", "b", "ab", "aa", "a" };  
Arrays.sort(array, 0, 3);//只对 0-2 这三个元素排序  
System.out.println(Arrays.toString(array));//[a, b, c, ab, aa, a]  
```  
  
## binarySearch 方法  
`static int  binarySearch(byte[] a, byte key)`  使用二分搜索法来搜索指定的 byte、char、double、float、int、long、short、Object、T 型数组，以获得指定的值。  
`static int  binarySearch(byte[] a, int fromIndex, int toIndex, byte key)`  搜索指定范围内的元素，包括fromIndex ，不包括toIndex  
- 必须在进行此调用之前通过 Arrays.sort(byte[]) 方法对数组进行排序，如果没有对数组进行排序，则结果是不确定的。  
- 返回：  
    - 如果它包含在数组中，则返回搜索键的索引；否则返回【-(插入点) - 1】  
    - 插入点 被定义为将键插入数组的那一点：即第一个大于此键的元素索引，如果数组中的所有元素都小于指定的键，则为 a.length。注意，这保证了当且仅当此键被找到时，返回的值将 >= 0。  
    - 如果数组包含多个带有指定值的元素，则无法保证找到的是哪一个。  
- 注意：使用前必须先使用sort(arr)方法对数组进行排序，否则，任何返回的结果都是不明确的。  
  
### 源码分析  
```java  
public static int binarySearch(byte[] a, byte key) {  
   return binarySearch0(a, 0, a.length, key);  
}  
  
public static int binarySearch(byte[] a, int fromIndex, int toIndex, byte key) {  
   rangeCheck(a.length, fromIndex, toIndex);//检查 fromIndex 和 toIndex 的值是否合法，不合法抛出相应的异常  
   return binarySearch0(a, fromIndex, toIndex, key);  
}  
  
private static int binarySearch0(byte[] a, int fromIndex, int toIndex, byte key) {  
   int low = fromIndex;//最小位置  
   int high = toIndex - 1;//最大位置  
     
   while (low <= high) {  
      int mid = (low + high) >>> 1;//中间位置  
      byte midVal = a[mid];//中间位置的值  
        
      if (midVal < key) {//若中间位置的值小于要查询的值，向右边继续找  
         low = mid + 1;//最小位置=中间位置+1，最大位置不变  
      } else if (midVal > key) {//若中间位置的值大于要查询的值，向左边继续找  
         high = mid - 1;//最大位置=中间位置-1，最小位置不变  
      } else {  
         return mid; //否则这就是要找到的值  
      }  
   }  
   return -(low + 1);  //如果没找到返回的值  
}  
```  
  
### 二分查找  
二分查找又称折半查找，优点是比较次数少，查找速度快，平均性能好；  
缺点是要求待查表为有序表，且插入删除困难。  
因此，折半查找方法适用于不经常变动而查找频繁的有序列表。  
  
**查找过程**  
(假设表中元素是按升序排列)：  
- 首先，将表中间位置记录的关键字与查找关键字比较  
- 如果两者相等，则查找成功，否则利用中间位置记录将表分成前、后两个子表  
- 如果中间位置记录的关键字大于查找关键字，则进一步查找前一子表，否则进一步查找后一子表  
- 重复以上过程，直到找到满足条件的记录，则查找成功；或直到子表不存在为止，此时查找不成功  
  
**时间复杂度**  
- 二分查找的基本思想是将 n 个元素分成大致相等的两部分，首先将 `a[n/2]` 与 x 做比较：  
    - 如果 `x=a[n/2]`，则找到 x，算法中止；  
    - 如果 `x<a[n/2]`，则只要在数组 a 的左半部分继续搜索 x；  
    - 如果 `x>a[n/2]`，则只要在数组a的右半部搜索x。  
- 时间复杂度无非就是 while 循环的次数！总共有 n 个元素，渐渐比较下去就是 `n,n/2,n/4,....n/2^k`(值代表剩余的可比较元素的个数)，其中k就是循环的次数。由于你 `n/2^k` 取整后 >=1，即令 `n/2^k=1`，可得`k=log2n`(是以2为底，n的对数)，所以时间复杂度可以表示`O()=O(logn)`  
  
**使用注意事项**  
- 必须在调用之前对数组进行升序排序(必须是升序排序，使用自定义排序得到的结果可能是不满足预期的)，否则任何查询结果都是不确定的(原因看源码)  
- 如果数组包含多个带有指定值的元素，(即使排序后也)无法保证找到的是哪一个(原因看源码)  
- 如果没找到目标， 它产生负返回值，表示若要保持数组的排序状态此元素所应该插入的位置，返回值为【-(插入点下标+1)】(原因看源码)  
  
```java  
String[] array = new String[] { "c", "a", "b", "ab", "aa", "a" };  
Arrays.sort(array);  
System.out.println(Arrays.toString(array));//[a, a, aa, ab, b, c]  
System.out.println(Arrays.binarySearch(array, "b"));//4  
System.out.println(Arrays.binarySearch(array, "a"));//0  
System.out.println(Arrays.binarySearch(array, "ac"));//-5【-(4+1)】  
```  
  
## copyOf、copyOfRange 方法  
`Array.copyOf()`方法通常用于通过复制指定数组内容并以默认值填充未知的内容，以达到扩容的目的  
  
### 相关的几个方法  
`static boolean[]  copyOf(boolean[] original, int newLength)`    复制指定的 `byte、char、double、float、int、long、short、T` 型数组，截取或用 `0或false或字符null或null` 填充(如有必要)，以使副本具有指定的长度。  
- 对于在原数组和副本中都有效的所有索引，这两个数组将包含相同的值。  
- 对于在副本中有效而在原数组无效的所有索引，副本将包含 `0或false或字符null或null`。  
- 当且仅当指定长度大于原数组的长度时，这些索引存在。  
- 所得数组和原数组属于完全相同的类。  
- 参数：original - 要复制的数组；newLength - 要返回的副本的长度  
- 返回：原数组的副本，截取或用 `0或false或字符null或null` 填充以获得指定的长度  
  
`static <T,U> T[]  copyOf(U[] original, int newLength, Class<? extends T[]> newType)`  复制指定的数组，截取或用 null 填充(如有必要)，以使副本具有指定的长度。所得数组属于 newType 类。  
  
`static boolean[]  copyOfRange(boolean[] original, int from, int to)`  指定范围，包括from ，不包括to  
  
`static <T,U> T[]  copyOfRange(U[] original, int from, int to, Class<? extends T[]> newType)`  指定范围，包括from，不包括to  
  
### 源码分析  
```java  
public static long[] copyOf(long[] original, int newLength) {  
    long[] copy = new long[newLength];//先定义一个指定长度的数组  
    //再通过 System.arraycopy() 方法将原始数组中的内容复制到上面定义的数组，然后直接返回  
    System.arraycopy(original, 0, copy, 0, Math.min(original.length, newLength));  
    // 从源数组 original 的 0 位置开始复制，复制到 copy 的 0 位置，当复制 length 个后结束复制  
    return copy;  
}  
```  
  
对于有泛型的两个方法：  
```java  
public static <T> T[] copyOf(T[] original, int newLength) {  
    return (T[]) copyOf(original, newLength, original.getClass());  
}  
  
//定义数组不能用泛型，所以这里需要传一个参数 newType 告诉我们要构造的数组的类型，这种方式可以学习一下！  
public static <T,U> T[] copyOf(U[] original, int newLength, Class<? extends T[]> newType) {  
    @SuppressWarnings("unchecked") //因为我们直接强转的，所以这里有一个 unchecked 的 SuppressWarnings  
    T[] copy = ((Object)newType == (Object)Object[].class) ? (T[]) new Object[newLength] //直接构造 Object数组  
        : (T[]) Array.newInstance(newType.getComponentType(), newLength); //通过反射方式构造指定类型的数组  
    System.arraycopy(original, 0, copy, 0, Math.min(original.length, newLength)); //和上面一样  
    // 从源数组 original 的 0 位置开始复制，复制到 copy 的 0 位置，当复制 length 个后结束复制  
    return copy;  
}  
```  
  
由此可见，copyOf 的核心算法还是 `System.arraycopy` 方法。  
  
再稍微瞅一下 copyOfRange 方法：  
```java  
public static <T,U> T[] copyOfRange(U[] original, int from, int to, Class<? extends T[]> newType) {  
    int newLength = to - from;  
    if (newLength < 0) throw new IllegalArgumentException(from + " > " + to);  
    @SuppressWarnings("unchecked")  
    T[] copy = ((Object)newType == (Object)Object[].class) ? (T[]) new Object[newLength]  
        : (T[]) Array.newInstance(newType.getComponentType(), newLength);  
    System.arraycopy(original, from, copy, 0, Math.min(original.length - from, newLength));  
    // 从源数组 original 的 from 位置开始复制，复制到 copy 的 0 位置，当复制 length 个后结束复制  
    return copy;  
}  
```  
  
### 测试代码  
```java  
String[] array = new String[] { "c", "a", };  
String[] array2 = Arrays.copyOf(array, 1);  
String[] array3 = Arrays.copyOf(array, 3);  
String[] array4 = Arrays.copyOfRange(array, 0, 1);  
System.out.println(Arrays.toString(array2) + "  " + Arrays.toString(array3) + "  " + Arrays.toString(array4));//[c]  [c, a, null]  [c]  
```  
  
```java  
Object[] array = new Object[] { 1, 2 };  
Integer[] array2 = Arrays.copyOf(array, 3, Integer[].class); //不能用于 int[] 等基本类型的数组，因为泛型不支持基本类型  
System.out.println(Arrays.toString(array2));//[1, 2, null]  
```  
  
## equals 和 deepEquals  
`static boolean  equals(boolean[] a, boolean[] a2)` 如果指定 byte、char、double、float、int、long、short、Object 型数组相等，返回 true。  
- 如果两个数组包含相同数量的元素，并且两个数组中的所有相应元素对都是相等的，则认为这两个数组是相等的。  
- 换句话说，如果两个数组以相同顺序包含相同的元素，则两个数组是相等的。  
- 此外，如果两个数组引用都为 null，则认为它们是相等的。  
  
`static boolean  deepEquals(Object[] a1, Object[] a2)`    如果指定 byte、char、double、float、int、long、short、Object 数组是深层相等的，返回 true  
- 与 equals(Object[],Object[]) 方法不同，此方法适用于任意深度的嵌套数组。  
- 如果两个数组引用均为 null，或者它们引用了包含相同元素数量的数组，并且两个数组中的所有相应元素对都是深层相等的，则认为这两个数组引用是深层相等的。  
- 如果指定数组中的任意一个数组，直接或间接通过一个或多个数组级别，包含数组本身作为其元素，则此方法的行为是不确定的。  
  
测试代码(具体规律简单看一下就明白了)：  
```java  
String[] b = { "b" };  
String[] b2 = { "b" };  
String[] qt = { "qt" };  
String[][] name1 = { b, qt };  
String[][] name2 = { b, qt };  
System.out.println(Arrays.equals(name1, name2) + "  " + Arrays.deepEquals(name1, name2));// true true  
System.out.println(Arrays.equals(b2, b) + "  " + Arrays.deepEquals(b2, b));// true  true  
  
String[][] name3 = { { "b" }, { "qt" } };  
String[][] name4 = { { "b" }, { "qt" } };  
System.out.println(Arrays.equals(name3, name4) + "  " + Arrays.deepEquals(name3, name4));// false  true  
  
String[][] name5 = { b };  
String[][] name6 = { b2 };  
System.out.println(Arrays.equals(name5, name6) + "  " + Arrays.deepEquals(name5, name6));// false  true  
```  
  
如果要比较多维数组的内容是否相同，必须使用deepEquals方法，否则结果可能不符合预期！  
  
## fill 方法  
`static void fill(boolean[] a, boolean val)`  将指定的 byte、char、double、float、int、long、short、Object 值分配给指定 ** 类型数组的每个元素。  
`static void fill(boolean[] a, int fromIndex, int toIndex, boolean val)`  指定范围，包括fromIndex ，不包括toIndex。注意，如果 fromIndex==toIndex，则填充范围为空。  
```java  
String[] b = { "b", "t", "t" };  
Arrays.fill(b, "包");  
System.out.println(Arrays.toString(b));//[包, 包, 包]  
Arrays.fill(b, 1, 2, "青");// toIndex 不能用 1 ，因为 fromIndex==toIndex 时填充范围为空  
Arrays.fill(b, 2, b.length, "天");//toIndex 不能大于 b.length，否则报 ArrayIndexOutOfBoundsException  
System.out.println(Arrays.toString(b));//[包, 青, 天]  
```  
  
## toString deepToString 方法  
`static String  toString(boolean[] a)`  Returns a string representation表示 of the contents of the specified指定的 byte、char、double、float、int、long、short、Object array.  
- 对于参数为基本类型数据的重载方法：  
    - The string representation consists of由..组成 a list of the array's elements, enclosed in包围在 square brackets方括号 ("[]"). Adjacent elements相邻的元素 are separated分割 by the characters ", " (a comma逗号 followed by a space空格).   
    - Elements are converted转换 to strings as by String.valueOf(boolean). Returns "null" if a is null.  
- 对于参数为 Object 的重载方法：  
    - If the array contains other arrays as elements, they are converted to strings by the Object.toString() method inherited from继承自 Object, which describes描述 their identities标识 rather than而不是 their contents.   
    - The value returned by this method is equal to the value that would be returned by Arrays.asList(a).toString(), unless a is null, in which case "null" is returned.  
- Returns: a string representation of a  
  
`static String  deepToString(Object[] a)`  返回指定数组“深层内容”的字符串表示形式。  
- 如果数组包含作为元素的其他数组，则字符串表示形式包含其内容(注意，这种情况toString不包含其内容，而只包含其描述)。  
- 此方法是为了将多维数组转换为字符串而设计的。  
- 如果元素 e 是一个基本类型的数组，则通过调用 Arrays.toString(e) 的适当重载将它转换为字符串。如果元素 e 是一个引用类型的数组，则通过递归调用此方法将它转换为字符串。  
- 为了避免无限递归，如果指定数组包含本身作为其元素，或者包含通过一个或多个数组级别对其自身的间接引用，则将自引用转换为字符串 "[...]"。  
- 如果指定数组为 null，则此方法返回 "null"。  
  
```java  
String[][] name = { { "b" }, { "qt" } };  
System.out.println(Arrays.toString(name) + "  " + Arrays.deepToString(name));  
//[[Ljava.lang.String;@15db9742, [Ljava.lang.String;@6d06d69c]  [[b], [qt]]  
```  
  
## hashCode deepHashCode 方法  
`static int  hashCode(boolean[] a)`  基于指定 byte、char、double、float、int、long、short、Object 数组的内容返回哈希码。  
- 对于任何两个满足 Arrays.equals(a, b) 的非 null 型数组 a 和 b，也可以说 Arrays.hashCode(a) == Arrays.hashCode(b)。  
- 此方法返回的值与在 List 上调用 hashCode 方法获得的值相同，该 List 包含以相同顺序表示 a 数组元素的 Integer 实例的序列。  
- 如果 a 为 null，则此方法返回 0。  
  
`static int  deepHashCode(Object[] a)`  基于指定数组的“深层内容”返回哈希码。  
  
```java  
String[][] name = { { "b" }, { "qt" } };  
System.out.println(Arrays.hashCode(name) + "  " + Arrays.deepHashCode(name));//312355675  8610  
```  
  
2018-7-4  
