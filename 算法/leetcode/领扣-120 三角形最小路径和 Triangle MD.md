| Markdown版本笔记 | 我的GitHub首页 | 我的博客 | 我的微信 | 我的邮箱 |  
| :------------: | :------------: | :------------: | :------------: | :------------: |  
| [MyAndroidBlogs][Markdown] | [baiqiantao][GitHub] | [baiqiantao][博客] | bqt20094 | baiqiantao@sina.com |  
  
[Markdown]:https://github.com/baiqiantao/MyAndroidBlogs  
[GitHub]:https://github.com/baiqiantao  
[博客]:http://www.cnblogs.com/baiqiantao/  
  
领扣-120 三角形最小路径和 Triangle MD  
***  
目录  
===  

- [[三角形最小路径和 Triangle](https://leetcode-cn.com/problems/triangle/)](#三角形最小路径和-Trianglehttpsleetcode-cncomproblemstriangle)
	- [问题](#问题)
	- [动态规划(基础)](#动态规划基础)
		- [分析](#分析)
		- [代码](#代码)
	- [动态规划(逆向)](#动态规划逆向)
		- [分析](#分析)
		- [代码](#代码)
	- [动态规划(逆向 + 优化)](#动态规划逆向--优化)
  
# [三角形最小路径和 Triangle](https://leetcode-cn.com/problems/triangle/)  
数组 动态规划  
  
## 问题  
给定一个三角形，找出自顶向下的`最小路径和`。每一步只能移动到`下一行中相邻的结点`上。  
  
例如，给定三角形：  
  
         [2],  
        [3,4],  
       [6,5,7],  
      [4,1,8,3]  
  
自顶向下的最小路径和为 11（即，2 + 3 + 5 + 1 = 11）。  
  
方法声明：  
```java  
class Solution {  
    public int minimumTotal(List<List<Integer>> triangle) {  
          
    }  
}  
```  
  
## 动态规划(基础)  
### 分析  
如果只是简单分析的话，这道题和其他动态规划没啥区别，问题是代码写起来可能稍有点麻烦。  
  
用动态规划分析的关键是发现递推式，我们定义一个二维数组 dp，其中 dp[i][j] 的含义为：  
  
    dp[i][j]：到达三角形第 i 行 第 j 那个元素所需要的最小步数  
  
那么我们就可以分析出 dp[i][j] 的值的规律，所以很明显，递推式是这样的：  
  
    dp[i][j] = V(i,j) + min(dp[i-1][j-1] ,dp[i-1][j])  
  
当然，针对边界元素我们需要单独处理一下，具体的代码实现如下：  
  
  
### 代码  
```java  
class Solution {  
  
    public int minimumTotal(List<List<Integer>> triangle) {  
        int len = triangle.size();  
        int[][] total = new int[len][len]; //保存的是到达最后一行各个元素的最短距离  
        total[0][0] = triangle.get(0).get(0);  
  
        for (int i = 1; i < len; i++) {  
            for (int j = 0; j <= i; j++) {  
                if (j == 0) {  
                    total[i][j] = triangle.get(i).get(j) + total[i - 1][j];  
                } else if (j == i) {  
                    total[i][j] = triangle.get(i).get(j) + total[i - 1][j - 1];  
                } else {  
                    total[i][j] = triangle.get(i).get(j) + Math.min(total[i - 1][j], total[i - 1][j - 1]);  
                }  
            }  
        }  
  
        int min = Integer.MAX_VALUE;  
        for (int i = 0; i < len; i++) {  
            min = Math.min(min, total[len - 1][i]);  
        }  
        System.out.println(Arrays.toString(total[len - 1]));  
  
        return min;  
    }  
}  
```  
  
时间复杂度 `O(n^2)`  
空间复杂度 `O(n^2)`  
  
## 动态规划(逆向)  
### 分析  
假如我们是从倒数第二行(统一称为第 i 行)开始走的话，那么从倒数第二行的第 j 个元素走到最后一行所需要的最小步数为：  
  
    P(j) = V(i,j) + min(V(i+1,j),V(i+1,j+1))  
  
我们对倒数第二行的所有元素都进行此计算，并将结果保存到集合 array 中，且 array 的第 j 个元素的值为 P(j)。  
  
我们知道 P(j) 代表的含义为`倒数第二行的第 j 个元素`走到`最后一行`所需要的最小步数，所以如果让我们计算从`倒数第二行`走到`最后一行`所需要的最小步数，那么我们只需从 array 中选择值最小的那个元素即可。  
  
同样道理，如果让我们从倒数第三行开始走的话，那么我们只需按同样的方式计算从`倒数第三行的第 j 个元`素走到 `array 列` 所需要的最小步数即可，而不需要关注倒数第二行和倒数第三行的数了。  
  
经过此步骤后，我们发现问题的规模变小了，动态规划思想也就体现出来了。  
  
---  
  
遍历过程：  
  
         [2],  
        [3,4],  
       [6,5,7],  
      [4,1,8,3]  
  
         [2],  
        [3,4],  
       [7,6,10],  
  
         [2],  
        [9,10],  
  
         [11]  
  
### 代码  
```java  
class Solution {  
  
    public int minimumTotal(List<List<Integer>> triangle) {  
        int[][] dp = new int[triangle.size() + 1][triangle.size() + 1];// 加1可以不用初始化最后一层  
        for (int i = triangle.size() - 1; i >= 0; i--) {  
            List<Integer> curTr = triangle.get(i);  
            for (int j = 0; j < curTr.size(); j++) {  
                dp[i][j] = Math.min(dp[i + 1][j], dp[i + 1][j + 1]) + curTr.get(j);  
            }  
        }  
        return dp[0][0];  
    }  
}  
```  
  
时间复杂度 `O(n^2)`  
空间复杂度 `O(n^2)`  
  
## 动态规划(逆向 + 优化)  
对于上面那种方式，我们可以继续优化。  
  
我们发现在计算倒数第三行时，array 的长度和倒数第二行的长度相同，且有 `替换` 倒数第二行的效果，所以，我们可以不必再定义一个 array ，而只需把计算的结果赋给倒数第二行即可。  
  
而在计算过程中，我们需要的 array 的长度会越来越小，这没关系，我们只需要关心自己需要的那些元素即可。  
  
```java  
class Solution {  
  
    public int minimumTotal(List<List<Integer>> triangle) {  
        int[] dp = new int[triangle.size() + 1]; // 加1可以不用初始化最后一层  
  
        for (int i = triangle.size() - 1; i >= 0; i--) {  
            List<Integer> curTr = triangle.get(i);  
            for (int j = 0; j < curTr.size(); j++) {  
                dp[j] = curTr.get(j) + Math.min(dp[j], dp[j + 1]); //这里的dp[j] 使用的时候默认是上一层的，赋值之后变成当前层  
            }  
        }  
        return dp[0];  
    }  
}  
```  
  
时间复杂度 `O(n^2)`  
空间复杂度 `O(n)`  
  
实际测试会发现，虽然空间复杂度大大降低了，但是总耗时却增加了。这也很好解释，因为时间复杂度并没有变，而空间复杂度减小又是在建立在增加了计算的基础上的，所以总时间当然会增加了。  
  
2018-12-13  
