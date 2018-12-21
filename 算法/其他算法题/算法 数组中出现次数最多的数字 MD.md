| Markdown版本笔记 | 我的GitHub首页 | 我的博客 | 我的微信 | 我的邮箱 |  
| :------------: | :------------: | :------------: | :------------: | :------------: |  
| [MyAndroidBlogs][Markdown] | [baiqiantao][GitHub] | [baiqiantao][博客] | bqt20094 | baiqiantao@sina.com |  
  
[Markdown]:https://github.com/baiqiantao/MyAndroidBlogs  
[GitHub]:https://github.com/baiqiantao  
[博客]:http://www.cnblogs.com/baiqiantao/  
  
算法 数组中出现次数最多的数字  
***  
目录  
===  

- [数组中出现次数最多的数字](#数组中出现次数最多的数字)
	- [方式一：快速排序](#方式一：快速排序)
	- [方式二：两次循环(通用)](#方式二：两次循环通用)
	- [方式三：一次循环(推荐)](#方式三：一次循环推荐)
  
# 数组中出现次数最多的数字  
给定一个int数组，找出出现次数最多的数字（出现次数超过数组长度的一半）  
  
## 方式一：快速排序  
先对这个数组进行排序，在已排序的数组中，位于中间位置的数字就是超过数组长度一半的那个数。  
```java  
public class Test {  
  
    public static void main(String[] args) throws Exception {  
        int[] array = { 1, 1, 1, 1, 5, 1, 5, 1, 5, 5, 5 };  
        qsort(array);  
        System.out.println(Arrays.toString(array));  
    }  
  
    public static void qsort(int[] arr) {  
        qsort(arr, 0, arr.length - 1);  
    }  
  
    public static void qsort(int[] arr, int low, int high) {  
        if (low < high) {  
            int pivot = partition(arr, low, high);//将表一分为二  
            qsort(arr, low, pivot);//对低子表【递归】排序  
            qsort(arr, pivot + 1, high);//递归对高子表递归排序  
        }  
    }  
  
    private static int partition(int[] arr, int low, int high) {  
        int pivotkey = arr[low];//选择一个【基准元素】，通常选择第一个元素或者最后一个元素  
        while (low < high) {//从表的两端【交替】地向中间扫描  
            //将比基准元素小的交换到低端  
            while (low < high && arr[high] >= pivotkey) {  
                high--;  
            }  
            swap(arr, low, high);  
            //将比基准元素大的交换到高端  
            while (low < high && arr[low] <= pivotkey) {  
                low++;  
            }  
            swap(arr, low, high);  
        }  
        return low;//此时基准元素在其排好序后的正确位置  
    }  
  
    public static void swap(int[] arr, int i, int j) {  
        if (i == j) return;  
        int temp = arr[i];  
        arr[i] = arr[j];  
        arr[j] = temp;  
    }  
}  
```  
  
时间复杂度为 `O(n*lgN)`  
空间复杂度为 `O(n*lgN)`  
  
## 方式二：两次循环(通用)  
第一次循环是为了记录各个数字出现的次数  
第二次循环是为了比较各个数字出现的次数  
这种方式没有利用`出现次数超过数组长度的一半`这个特殊条件，可以在任何数组中找出出现次数最多的数字。  
  
```java  
public class Test {  
  
    public static void main(String[] args) throws Exception {  
        int[] array = { 1, 1, 1, 1, 5, 1, 5, 1, 5, 5, 5 };  
        System.out.println(mostNum(array));  
    }  
  
    public static int mostNum(int[] array) {  
        HashMap<Integer, Integer> map = new HashMap<Integer, Integer>();  
        for (int i = 0; i < array.length; i++) {  
            int key = array[i];  
            if (map.containsKey(key)) {  
                map.replace(key, map.get(key) + 1);  
            } else {  
                map.put(key, 1);  
            }  
        }  
  
        int key = array[0];  
        for (Entry<Integer, Integer> entry : map.entrySet()) {  
            if (entry.getValue() > map.get(key)) {  
                key = entry.getKey();  
            }  
        }  
        return key;  
    }  
}  
```  
  
时间复杂度为`O(n)`  
空间复杂度为`O(n)`  
  
## 方式三：一次循环(推荐)  
取巧的做法，仅当`出现次数超过数组长度的一半`这种条件下才保证正确。  
  
```java  
public class Test {  
  
    public static void main(String[] args) throws Exception {  
        int[] array = { 1, 1, 1, 1, 5, 1, 5, 1, 5, 5, 5 };  
        System.out.println(mostNum(array));  
    }  
  
    public static int mostNum(int[] array) {  
        int count = 1, value = array[0];  
  
        for (int i = 1; i < array.length; i++) {  
            if (array[i] == value) {  
                count++; //如果下一个数字与之前保存的数字相同，则次数加1  
            } else {  
                count--; //如果不同，则次数减1  
            }  
            if (count == 0) {  
                value = array[i]; //如果次数为0，则需要保存下一个数字，并把次数设定为1  
                count = 1;  
            }  
        }  
        return value;  
    }  
}  
```  
  
时间复杂度为`O(n)`  
空间复杂度为`O(1)`  
  
2018-12-8  
