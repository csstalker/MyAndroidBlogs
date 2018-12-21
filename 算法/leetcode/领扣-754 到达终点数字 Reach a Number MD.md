| Markdown版本笔记 | 我的GitHub首页 | 我的博客 | 我的微信 | 我的邮箱 |  
| :------------: | :------------: | :------------: | :------------: | :------------: |  
| [MyAndroidBlogs][Markdown] | [baiqiantao][GitHub] | [baiqiantao][博客] | bqt20094 | baiqiantao@sina.com |  
  
[Markdown]:https://github.com/baiqiantao/MyAndroidBlogs  
[GitHub]:https://github.com/baiqiantao  
[博客]:http://www.cnblogs.com/baiqiantao/  
  
领扣-754 到达终点数字 Reach a Number MD  
***  
目录  
===  

- [[到达终点数字 Reach a Number MD](https://leetcode-cn.com/problems/reach-a-number/)](#到达终点数字-reach-a-number-mdhttpsleetcode-cncomproblemsreach-a-number)
	- [题目](#题目)
	- [思路](#思路)
	- [代码实现](#代码实现)
  
# [到达终点数字 Reach a Number MD](https://leetcode-cn.com/problems/reach-a-number/)  
数学运算  
  
## 题目  
在一根无限长的数轴上，你站在0的位置。终点在target的位置。  
You are standing at position `0` on an infinite number line. There is a goal at position `target`.  
  
每次你可以选择向左 **或** 向右移动。第 n 次移动（从 1 开始），可以走 n 步。  
On each move, you can either go left or right. During the _n_-th move (starting from 1), you take _n_ steps.  
  
返回到达终点需要的最小移动次数。  
Return the minimum number of steps required to reach the destination.  
  
示例 1:  
  
    输入: target = 3  
    输出: 2  
    解释:  
    第一次移动，从 0 到 1 。  
    第二次移动，从 1 到 3 。  
  
示例 2:  
  
    输入: target = 2  
    输出: 3  
    解释:  
    第一次移动，从 0 到 1 。  
    第二次移动，从 1 到 -1 。  
    第三次移动，从 -1 到 2 。  
  
## 思路  
**首先**  
比如说目标值是4，那么如果我们一直向前走，直到累加步数 sum 正好`大于等于`target时，有：  
0 + 1 = 1  
1 + 2 = 3  
3 + 3 = 6  
我们发现 sum 超过了目标值 4，超过的距离为2，是`偶数`，那么实际上我们只要将加上距离为 1 的时候，不加 1，而是 -1，那么此时累加和就损失了 2，那么正好能到目标值 4，如下：  
0 - 1 = -1  
-1 + 2 = 1  
1 + 3 = 4  
所以这种情况下，最小的步数就是累加步数到 sum 所用的步数 n。  
  
---  
  
**然后**  
如果说目标值是 7，有：  
0 + 1 = 1  
1 + 2 = 3  
3 + 3 = 6  
6 + 4 = 10  
我们发现 sum 超过了目标值 7 的距离为3，是`奇数`，我现在可以这么处理这个奇数 3：  
3 = 2 * 1 + 1  
前面的偶数 2 可以按照上面的方式来处理，我只需要处理多出来的 1 即可。实际上 1 就很好处理了，我们只需再向前走 1 步，然后再向后走 1 步其实刚好就抵消了这个 1 。这个过程如下：  
0 - 1 = -1  
-1 + 2 = 1  
1 + 3 = 4  
4 + 4 = 8  //基准步数  
8 + 5 = 13  
13 - 6 = 7  
所以这种情况下，最小的步数就是累加步数到 sum 所用的步数 n + 2。  
  
---  
  
**然而**  
然而测试时发现，当 sum - target 为奇数时，某些情况下，我们这种方式的步数会比最优解大 1 ，就比如上面的目标值 7 。  
  
下面我们分析一下。  
target=7; sum=10; n=4;  
我们再走一步的话 sum=7+5=15; sum-target=8;  
  
看到没，我们再走一步之后 sum-target 变成偶数了，既然变成偶数了，我们当然就可以按照最初的方式来走了呀：  
0 + 1 = 1  
1 + 2 = 3  
3 + 3 = 6  
6 - 4 = 2  //基准步数  
2 + 5 = 7  
所以这种情况下，我们只需多走一步就可以了，不需要无脑的多走两步。  
  
---  
  
**所以**  
上面当 sum - target 为奇数时多走一步就可以的条件是：  
sum + (n+1) - target 为偶数  
因为我们已经知道 sum - target 为奇数了，所以 n+1 肯定也为奇数，也就是说 n 为偶数。  
也就是说，如果此时 n 为偶数，我们只需再多走一步就可以了。  
  
那如果 n 为奇数呢？  
  
我觉得有两种分析方式，一种就是我们上面最开始错误分析的那种，直接为 n + 2；另一种可以继续按照这种方式分析：  
如果 tem = sum - target 为奇数  
那么 tem + (n+1) + (n+2) = 2 (n+1) + (tem+1) 肯定为偶数，也即再多走 2 步 无论如何都是可以的了。  
  
当然，这只能保证是可以到达终点的路径，但不能百分之百保证这是最佳的路径！  
  
## 代码实现  
```java  
class Solution {  
    public int reachNumber(int target) {  
        target = Math.abs(target); //处理负值的情况  
        int count = 0;  
        int sum = 0;  
        while (sum < target) {  
            count++;  
            sum += count;  
        }  
        if ((sum - target) % 2 != 0) {  
            if (count % 2 == 0) {  
                count += 1;  
            } else {  
                count += 2;  
            }  
        }  
        return count;  
    }  
}  
```  
  
2018-12-7  
