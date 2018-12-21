| Markdown版本笔记 | 我的GitHub首页 | 我的博客 | 我的微信 | 我的邮箱 |  
| :------------: | :------------: | :------------: | :------------: | :------------: |  
| [MyAndroidBlogs][Markdown] | [baiqiantao][GitHub] | [baiqiantao][博客] | bqt20094 | baiqiantao@sina.com |  
  
[Markdown]:https://github.com/baiqiantao/MyAndroidBlogs  
[GitHub]:https://github.com/baiqiantao  
[博客]:http://www.cnblogs.com/baiqiantao/  
  
领扣-62 不同路径 Unique Paths MD  
***  
目录  
===  

- [[不同路径 Unique Paths](https://leetcode-cn.com/problems/unique-paths/)](#不同路径-unique-pathshttpsleetcode-cncomproblemsunique-paths)
	- [问题](#问题)
	- [递归法](#递归法)
		- [暴力递归](#暴力递归)
		- [递归优化-HashMap](#递归优化-hashmap)
		- [递归优化-二维数组](#递归优化-二维数组)
	- [动态规划](#动态规划)
		- [二维数组](#二维数组)
		- [一维数组](#一维数组)
  
# [不同路径 Unique Paths](https://leetcode-cn.com/problems/unique-paths/)  
数组 动态规划  
  
## 问题  
一个机器人位于一个 m x n 网格的左上角 （起始点在下图中标记为“Start” ）。  
  
机器人每次只能向下或者向右移动一步。机器人试图达到网格的右下角（在下图中标记为“Finish”）。  
  
问总共有多少条不同的路径？  
  
![](https://assets.leetcode-cn.com/aliyun-lc-upload/uploads/2018/10/22/robot_maze.png)  
例如，上图是一个7 x 3 的网格。有多少可能的路径？  
  
说明：m 和 n 的值均不超过 100。  
  
示例 1:  
  
    输入: m = 3, n = 2  
    输出: 3  
    解释:  
    从左上角开始，总共有 3 条路径可以到达右下角。  
    1. 向右 -> 向右 -> 向下  
    2. 向右 -> 向下 -> 向右  
    3. 向下 -> 向右 -> 向右  
  
示例 2:  
  
    输入: m = 7, n = 3  
    输出: 28  
  
## 递归法  
### 暴力递归  
这个问题和 70 爬楼梯 的思路基本一样。  
  
我们知道，不管前面是怎么怎么走的，最后一步肯定是从 (m-1 , n) 或 (m , n-1) 过来的，并且我们可以确定，从 (0,0) 到 (m-1 , n) 和 从 (0,0) 到 (m , n-1) 的路径肯定没有完全重合的，所以 P(m,n) 可以转化为 P(m-1 , n) + P(m , n-1)  
  
```java  
class Solution {  
    public int uniquePaths(int m, int n) {  
        if (m == 1 || n == 1) return 1;  
        return uniquePaths(m - 1, n) + uniquePaths(m, n - 1);  
    }  
}  
```  
  
和 70 一样，提交到LeetCode往往会提示超出时间限制。  
  
### 递归优化-HashMap  
优化方式就是把递归过程中计算出的数据存储下来。  
  
```java  
class Solution {  
    HashMap<String, Integer> maps = new HashMap<>();  
  
    public int uniquePaths(int m, int n) {  
        if (m == 1 || n == 1) return 1;  
        Integer val = maps.get(m + "-" + n);  
        if (val == null) {  
            Integer val1 = maps.get((m - 1) + "-" + n); //不能定义为成员变量，因为这样的话它的值会被多个函数调用同时修改  
            if (val1 == null) {  
                val1 = uniquePaths(m - 1, n);  
                maps.put((m - 1) + "-" + n, val1);  
            }  
  
            Integer val2 = maps.get(m + "-" + (n - 1));  
            if (val2 == null) {  
                val2 = uniquePaths(m, n - 1);  
                maps.put(m + "-" + (n - 1), val2);  
            }  
  
            val = val1 + val2;  
            maps.put(m + "-" + n, val);  
        }  
        return val;  
    }  
}  
```  
  
可以通过了，但是这种方式估计会让面试官感到惊愕，能这么做也是人才了。  
  
### 递归优化-二维数组  
正确的方式当然是用数组了。  
  
```java  
class Solution {  
    int[][] array;  
  
    public int uniquePaths(int m, int n) {  
        array = new int[m + 1][n + 1];  
        return help(m, n);  
    }  
  
    public int help(int m, int n) {  
        if (m == 1 || n == 1) return 1;  
        int val = array[m][n];  
        if (val == 0) {  
            int val1 = array[m - 1][n]; //不能定义为成员变量，因为这样的话它的值会被多个函数调用同时修改  
            if (val1 == 0) {  
                val1 = help(m - 1, n);  
                array[m - 1][n] = val1;  
            }  
  
            int val2 = array[m][n - 1];  
            if (val2 == 0) {  
                val2 = help(m, n - 1);  
                array[m][n - 1] = val2;  
            }  
  
            val = val1 + val2;  
            array[m][n] = val;  
        }  
        return val;  
    }  
}  
```  
  
## 动态规划  
### 二维数组  
和之前不太一样的是，这里要用一个二维数组存储过程数据。  
  
```java  
class Solution {  
  
    public int uniquePaths(int m, int n) {  
        int[][] array = new int[m][n];  
        for (int i = 0; i < m; i++) {  
            for (int j = 0; j < n; j++) {  
                if (i == 0 || j == 0) {  
                    array[i][j] = 1;  
                } else {  
                    array[i][j] = array[i - 1][j] + array[i][j - 1];  
                }  
            }  
        }  
        return array[m - 1][n - 1];  
    }  
}  
```  
  
### 一维数组  
我们也可以使用一维数组一行一行的刷新  
  
```java  
class Solution {  
  
    public int uniquePaths(int m, int n) {  
        int[] array = new int[n];  
        for (int i = 0; i < m; i++) {  
            for (int j = 0; j < n; j++) {  
                if (i == 0 || j == 0) {  
                    array[j] = 1;  
                } else {  
                    array[j] += array[j - 1];// 累加  
                }  
            }  
        }  
        return array[n - 1];  
    }  
}  
```  
  
2018-12-9  
