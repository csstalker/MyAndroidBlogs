| Markdown版本笔记 | 我的GitHub首页 | 我的博客 | 我的微信 | 我的邮箱 |  
| :------------: | :------------: | :------------: | :------------: | :------------: |  
| [MyAndroidBlogs][Markdown] | [baiqiantao][GitHub] | [baiqiantao][博客] | bqt20094 | baiqiantao@sina.com |  
  
[Markdown]:https://github.com/baiqiantao/MyAndroidBlogs  
[GitHub]:https://github.com/baiqiantao  
[博客]:http://www.cnblogs.com/baiqiantao/  
  
领扣-55 跳跃游戏 Jump Game MD  
***  
目录  
===  

- [[跳跃游戏-55 Jump Game](https://leetcode-cn.com/problems/jump-game/)](#跳跃游戏-55-jump-gamehttpsleetcode-cncomproblemsjump-game)
	- [问题](#问题)
	- [贪心算法(逻辑简单且效率高)](#贪心算法逻辑简单且效率高)
		- [形式一(正宗算法)](#形式一正宗算法)
		- [形式二(逆向思维，更简单)](#形式二逆向思维，更简单)
	- [动态规划(逻辑复杂且耗时长)](#动态规划逻辑复杂且耗时长)
		- [形式一(双循环)](#形式一双循环)
		- [形式二(单循环)](#形式二单循环)
- [[跳跃游戏-45](https://leetcode-cn.com/problems/jump-game-ii/) - 最小步数(高难预警)](#跳跃游戏-45httpsleetcode-cncomproblemsjump-game-ii---最小步数高难预警)
	- [题目](#题目)
	- [贪婪算法一(不太明白)](#贪婪算法一不太明白)
		- [问题分析](#问题分析)
		- [代码实现-1](#代码实现-1)
		- [代码实现-2](#代码实现-2)
	- [贪婪算法二(不太明白)](#贪婪算法二不太明白)
		- [问题分析](#问题分析)
		- [代码实现](#代码实现)
  
# [跳跃游戏-55 Jump Game](https://leetcode-cn.com/problems/jump-game/)  
数组 贪心算法  
  
## 问题  
给定一个非负整数数组，你最初位于数组的第一个位置。  
  
数组中的每个元素代表你在该位置可以跳跃的`最大长度`。  
  
判断你是否能够到达最后一个位置。  
  
示例 1:  
  
    输入: [2,3,1,1,4]  
    输出: true  
    解释: 从位置 0 到 1 跳 1 步, 然后跳 3 步到达最后一个位置。  
  
示例 2:  
  
    输入: [3,2,1,0,4]  
    输出: false  
    解释: 无论怎样，你总会到达索引为 3 的位置。但该位置的最大跳跃长度是 0 ， 所以你永远不可能到达最后一个位置。  
  
## 贪心算法(逻辑简单且效率高)  
### 形式一(正宗算法)  
我们只对`到达`位置 i 时其`最远能到达的位置`感兴趣，只要存在某个位置 i，当到达 i 时其`最远能到达的位置`可以到达终点，那么我们就可以从这个位置走到终点；如果不存在，那就是到达不了终点。  
  
所以我们可以维护一个变量 max，其代表的就是当我`到达`位置 i 时其最远能到达的位置。仔细分析后可以发现，这个值与两个因素有关系：  
- 到达位置 i 时位置 i 上的值  
- 到达位置 i 时之前的 max 的值  
  
具体的做法为，从第 0 个位置开始逐步遍历：  
- 如果当前坐标 i 大于之前的 max，说明之前的位置没有一个可以到达当前坐标位置，所以 max 就是最远能到达的位置  
- 如果 max 已经抵达最后一个位置，则可以直接结束了  
- 否则就更新 max 的值为其和`i + nums[i]`中的较大值，其中`i + nums[i]`表示当前位置能到达的最大位置  
  
```java  
class Solution {  
    public boolean canJump(int[] nums) {  
        int max = nums[0];  
        for (int i = 0; i < nums.length - 1; i++) {  
            if (i > max) return false; //这个条件是很难想到的，同时也是必不可少的，少了这个判断，下面的逻辑就崩塌了  
            if (max >= nums.length - 1) return true; //满足条件直接结束，这个条件可有可无  
            max = Math.max(max, nums[i] + i); //贪心算法核心代码  
        }  
        return max >= nums.length - 1;  
    }  
}  
```  
  
时间复杂度为`O(n)`  
空间复杂度为`O(1)`  
  
### 形式二(逆向思维，更简单)  
这种思维的依据是：  
  
    如果可以到达位置 i ，那么肯定可以到达 [0,i-1] 区间内的任何一个点  
  
因此我们要做的事情就是：  
  
    从后往前找，直到找到一个【可以一步跳跃到 i 点】的点 j  
  
根据上面的`依据`，我们知道，不管这个点 j 是哪个点，我们肯定还找一个`可以一步跳跃到 j 点`的点 k ...  
  
所以当最后满足 index == 0 时，表示可以走到终点。  
  
```java  
class Solution {  
  
    public boolean canJump(int[] nums) {  
        if (null == nums || nums.length <= 0) return false;  
  
        int meetIndex = nums.length - 1; //从后往前找一个可以一步跳跃到终点的点 i  
        for (int i = nums.length - 2; i >= 0; i--) {  
            if (i + nums[i] >= meetIndex) { //表明从 i 点可以一步跳到 meetIndex 点  
                meetIndex = i; //如果找到的话，继续通过前面的步骤找 i  
            }  
        }  
        return meetIndex == 0;  
    }  
}  
```  
  
时间复杂度为`O(n)`  
空间复杂度为`O(1)`  
  
## 动态规划(逻辑复杂且耗时长)  
### 形式一(双循环)  
我们可以这么考虑，什么条件下可以跳到第 i 个位置呢？稍加分析我们就可以总结出这么的条件：  
  
    从 0 到 i-1 区间的某个点 j 可以直接跳到 i 位置  
  
然后我们再继续分析，这个 j 需要满足什么条件呢？  
- 首先，这个 j 必须也要满足 i 所满足的条件，即：从 0 到 j-1 区间的某个点 k 可以直接跳到 j 位置，很明显这就是递归和动态规划的基本形态了。  
- 其次，从这个 j 位置可以直接跳到 i 位置，也即 `j + dp[j] >= i`  
  
总结一下就是：  
  
    dp[i] = dp[j] and (j + nums[j] >= i)，其中 j 为小于 i 的某一值  
  
具体代码实现  
```java  
class Solution {  
    public boolean canJump(int[] nums) {  
        if (null == nums || nums.length <= 0) return false;  
  
        boolean canJumps[] = new boolean[nums.length]; //canJumps[i] 代表最远能否跳到第 i 个位置  
        canJumps[0] = true; //因为一开始就在第一个元素，所以一定可以跳到第一个位置  
        for (int i = 1; i < nums.length; i++) {  
            for (int j = 0; j < i; j++) { //能跳到第 i 个位置的条件是：从 0 到 i-1 区间的某个点 j 可以直接跳到 i 位置  
                if (canJumps[j] && j + nums[j] >= i) { //前提条件是首先要能否跳到第 j 位置，然后是 j 位置可以直接跳到 i 位置  
                    canJumps[i] = true; //保存这个值，供后续复用  
                    break;  
                }  
            }  
        }  
        return canJumps[nums.length - 1];  
    }  
}  
```  
  
时间复杂度为`O(n^2)`  
空间复杂度为`O(n)`  
  
### 形式二(单循环)  
我们维护一个数组dp，其中`dp[i]`表示`到达 i 位置时剩余的步数`，其代表了`以 i-1 位置为起点能到达的最远位置`。  
> 注意：  
这里的`dp[i]`并不代表`跳到` i 位置时剩余的步数，比如，对于 1,2,0,3，如果`跳到`了 0，那么剩余的步数肯定是 0 了，那就是到不了终点了。  
而实际上，我们的意思是，当`到达` 0 时的剩余步数，比如上例中从 2 开始跳，当`到达` 0 时我的剩余步数是 1，因为我还可以继续往后跳。  
  
到达当前位置的剩余步数跟什么有关呢？我们思考后会发现，其实仅跟`上一个位置的剩余步数`和`上一个位置的值`有关。  
> 注意，我们定义的这个`dp[i]`的值，和当前位置的值没有任何关系，因为我们认为，能否到达当前位置只与 i-1 的状态有关  
  
所以我们就有状态转移方程了  
  
    dp[i] = max(dp[i - 1], nums[i - 1]) - 1  
  
如果当某一个位置 dp 的值为负了，说明无法抵达当前位置，则直接返回 false。最后我们判断 dp 数组最后一位是否为非负数即可知道是否能抵达该位置。  
  
```java  
class Solution {  
  
    public boolean canJump(int[] nums) {  
        int[] dp = new int[nums.length];  
        for (int i = 1; i < nums.length; ++i) {  
            dp[i] = Math.max(dp[i - 1], nums[i - 1]) - 1;  
            if (dp[i] < 0) return false;  
        }  
        return dp[nums.length - 1] >= 0;  
    }  
}  
```  
  
时间复杂度为`O(n)`  
空间复杂度为`O(n)`  
  
# [跳跃游戏-45](https://leetcode-cn.com/problems/jump-game-ii/) - 最小步数(高难预警)  
数组 贪心算法  
  
## 题目  
给定一个非负整数数组，你最初位于数组的第一个位置。  
  
数组中的每个元素代表你在该位置可以跳跃的最大长度。  
  
你的目标是使用最少的跳跃次数到达数组的最后一个位置。  
  
示例:  
  
    输入: [2,3,1,1,4]  
    输出: 2  
    解释: 从下标为 0 跳到下标为 1 的位置，跳 1 步，然后跳 3 步到达数组的最后一个位置。  
  
说明:  
假设你总是可以到达数组的最后一个位置。  
  
## 贪婪算法一(不太明白)  
### 问题分析  
此题的核心方法是利用贪婪算法Greedy的思想来解，想想为什么呢？  
  
为了较快的跳到末尾，我们想知道`每一步能跳的范围`，这里贪婪并不是要在能跳的范围中选跳力最远的那个位置，因为这样选下来不一定是最优解，我们这里贪的是一个`能到达的最远范围`。  
  
我们遍历`当前跳跃能到的所有位置`，然后根据`该位置上的跳力`来`预测下一步能跳到的最远距离`，贪出一个最远的范围，一旦当这个范围到达末尾时，当前所用的步数一定是最小步数。  
  
我们需要两个变量 cur 和 tem 分别来保存`当前的能到达的最远位置`和`之前能到达的最远位置`，只要 cur 未达到最后一个位置则循环继续  
  
tem 先赋值为 cur 的值，表示上一次循环后能到达的最远位置。  
  
如果`当前位置 i `小于等于 tem，说明还是在上一跳能到达的范围内，我们根据当前位置加跳力来更新 cur。更新 cur 的方法是比较当前的 cur 和 `i + nums[i]` 之中的较大值。  
  
如果题目中未说明是否能到达末尾，我们还可以判断此时 tem 和 cur 是否相等，如果相等说明 cur 没有更新，即无法到达末尾位置，返回-1。  
  
### 代码实现-1  
```java  
class Solution {  
    public int jump(int[] nums) {  
        int step = 0; //所用步数  
        int cur = 0;//当前的能到达的最远位置  
        int i = 0; //当前位置  
  
        while (cur < nums.length - 1) { //遍历到终点结束  
            step++;  
            int tem = cur; //临时保存之前能到达的最远位置  
            for (; i <= tem; i++) { //这里的循环条件不太理解，如果每次都从0循环的话会超时  
                cur = Math.max(cur, nums[i] + i);//这里的比较不太理解  
            }  
            if (tem == cur) return -1; //如果相等说明 cur 没有更新，即无法到达终点  
        }  
        return step;  
    }  
}  
```  
  
### 代码实现-2  
如果把全部局部变量定义在循环外，则为如下形式：  
```java  
class Solution {  
    public int jump(int[] nums) {  
        int begin = 0, end = 0, step = 0, farthest;  
        while (end < nums.length - 1) {  
            farthest = end;  
            for (int i = begin; i <= end; i++) {  
                farthest = Math.max(farthest, i + nums[i]);  
            }  
            begin = end + 1;  
            end = farthest;  
            step = step + 1;  
        }  
        return step;  
    }  
}  
```  
  
## 贪婪算法二(不太明白)  
### 问题分析  
还有一种写法，跟上面那解法略有不同，但是本质的思想还是一样的。  
  
这里 cur 是当前能到达的最远位置，last 是上一步能到达的最远位置。我们遍历数组，首先用`i + nums[i]`更新 cur，这个在上面解法中讲过了，然后判断如果当前位置到达了 last，即上一步能到达的最远位置，说明需要再跳一次了，我们将 last 赋值为 cur，并且步数 step 自增 1。如果 cur 到达末尾了，直接 break 掉即可，  
  
### 代码实现  
```java  
class Solution {  
    public int jump(int[] nums) {  
        int step = 0;//所用步数  
        int cur = 0;//当前能到达的最远位置  
        int last = 0;//上一步能到达的最远位置  
  
        for (int i = 0; i < nums.length - 1; ++i) {  
            cur = Math.max(cur, nums[i] + i);//这里的比较不太理解  
            if (i == last) {  
                last = cur;  
                ++step;  
                if (cur >= nums.length - 1) break;  
            }  
        }  
        return step;  
    }  
}  
```  
  
2018-12-12  
