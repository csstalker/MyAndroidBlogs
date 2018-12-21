| Markdown版本笔记 | 我的GitHub首页 | 我的博客 | 我的微信 | 我的邮箱 |  
| :------------: | :------------: | :------------: | :------------: | :------------: |  
| [MyAndroidBlogs][Markdown] | [baiqiantao][GitHub] | [baiqiantao][博客] | bqt20094 | baiqiantao@sina.com |  
  
[Markdown]:https://github.com/baiqiantao/MyAndroidBlogs  
[GitHub]:https://github.com/baiqiantao  
[博客]:http://www.cnblogs.com/baiqiantao/  
  
领扣-70 爬楼梯 Climbing Stairs MD  
***  
目录  
===  

- [[爬楼梯 Climbing Stairs](https://leetcode-cn.com/problems/climbing-stairs/)](#爬楼梯-climbing-stairshttpsleetcode-cncomproblemsclimbing-stairs)
	- [题目](#题目)
	- [递归方式(最简单)](#递归方式最简单)
	- [递归方式优化(空间换时间)](#递归方式优化空间换时间)
	- [动态规划(推荐)](#动态规划推荐)
	- [动态规划优化(迭代法)](#动态规划优化迭代法)
  
# [爬楼梯 Climbing Stairs](https://leetcode-cn.com/problems/climbing-stairs/)  
动态规划  
  
## 题目  
假设你正在爬楼梯。需要 n 阶你才能到达楼顶。  
You are climbing a stair case. It takes _n_ steps to reach to the top.  
  
每次你可以爬 1 或 2 个台阶。你有多少种不同的方法可以爬到楼顶呢？  
Each time you can either climb 1 or 2 steps. In how many distinct ways can you climb to the top?  
  
注意：给定 n 是一个正整数。  
**Note:** Given _n_ will be a positive integer.  
  
示例 1：  
  
    输入： 2  
    输出： 2  
    解释： 有两种方法可以爬到楼顶。  
    1.  1 阶 + 1 阶  
    2.  2 阶  
      
示例 2：  
  
    输入： 3  
    输出： 3  
    解释： 有三种方法可以爬到楼顶。  
    1.  1 阶 + 1 阶 + 1 阶  
    2.  1 阶 + 2 阶  
    3.  2 阶 + 1 阶  
  
## 递归方式(最简单)  
这个题目跟斐波那契数列非常相似，假设梯子有n层，那么如何爬到第n层呢？  
因为每次只能爬1或2步，那么爬到第n层的方法要么是从第 `n-1` 层一步上来的，要不就是从 `n-2` 层2步上来的，所以递推公式非常容易的就得出了：`dp[n] = dp[n-1] + dp[n-2]`  
  
```java  
class Solution {  
    public int climbStairs(int n) {  
        if (n == 1) return 1;  
        if (n == 2) return 2;  
        return climbStairs(n - 1) + climbStairs(n - 2);  
    }  
}  
```  
  
提交到leetcode往往会提示超出时间限制，是因为在n大的时候，递归栈非常之深，每次去递归时间开销很大，所以会抛出超时。  
  
## 递归方式优化(空间换时间)  
在递归的时候，不断有重复的递归，比如10会有9和8，9会有8和7 这时8已经出现了两次，而下面更小的数字会有更多的重复，那么我们把这些数据存储下来，可以节省开销。  
```java  
class Solution {  
    private int[] memo;  
  
    public int climbStairs(int n) {  
        memo = new int[n + 1];  
        return getClimbStairs(n);  
    }  
  
    private int getClimbStairs(int n) {  
        if (n == 1) return 1;  
        if (n == 2) return 2;  
        if (memo[n] == 0) memo[n] = getClimbStairs(n - 1) + getClimbStairs(n - 2);  
        return memo[n];  
    }  
}  
```  
  
我们只需要声明一个 memo 数组，把递归中间过程的值存储下来就好，可以大大的提高效率，这时候提交给 leetcode 已经可以获得AC了  
  
## 动态规划(推荐)  
递归法是从后往前分析问题的，我们可以从前往后分析。  
既然`dp[n] = dp[n-1] + dp[n-2]`，那么肯定有：  
dp[3] = dp[1] + dp[2]  
dp[4] = dp[2] + dp[3]  
dp[5] = dp[3] + dp[4]  
所以我们从小到大逐个相加即可  
  
```java  
class Solution {  
  
    public int climbStairs(int n) {  
        int[] memo = new int[n + 1];  
        memo[0] = 1;  
        memo[1] = 2;  
  
        for (int i = 2; i < n; i++) {  
            memo[i] = memo[i - 1] + memo[i - 2];  
        }  
        return memo[n - 1];  
    }  
}  
```  
  
## 动态规划优化(迭代法)  
我们可以对空间进行优化，我们只用几个整型变量来存储过程值，来模拟上面累加的过程，而不用存储所有的值，参见代码如下：  
```java  
class Solution {  
    public int climbStairs(int n) {  
        if (n == 1) return 1;  
        if (n == 2) return 2;  
          
        int a1 = 1, a2 = 2, a3 = 0;  
        while (n-- > 2) {  
            a3 = a1 + a2;  
            a1 = a2;  
            a2 = a3;  
        }  
        return a3;  
    }  
}  
```  
  
上述逻辑还可以优化为：  
```java  
class Solution {  
    public int climbStairs(int n) {  
        if (n == 1) return 1;  
        if (n == 2) return 2;  
          
        int a = 1, b = 2;  
        while (n-- > 2) {  
            b += a; //相加的值  
            a = b - a; //原来的 b  
        }  
        return b;  
    }  
}  
```  
  
2018-12-7  
