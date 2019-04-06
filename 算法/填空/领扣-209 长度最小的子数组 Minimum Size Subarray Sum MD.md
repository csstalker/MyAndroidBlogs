| Markdown版本笔记 | 我的GitHub首页 | 我的博客 | 我的微信 | 我的邮箱 |  
| :------------: | :------------: | :------------: | :------------: | :------------: |  
| [MyAndroidBlogs][Markdown] | [baiqiantao][GitHub] | [baiqiantao][博客] | bqt20094 | baiqiantao@sina.com |  
  
[Markdown]:https://github.com/baiqiantao/MyAndroidBlogs  
[GitHub]:https://github.com/baiqiantao  
[博客]:http://www.cnblogs.com/baiqiantao/  
  
领扣-209 长度最小的子数组 Minimum Size Subarray Sum MD  
***  
目录  
===  

- [[长度最小的子数组 Minimum Size Subarray Sum -209](https://leetcode-cn.com/problems/minimum-size-subarray-sum/)](#长度最小的子数组-Minimum-Size-Subarray-Sum--209httpsleetcode-cncomproblemsminimum-size-subarray-sum)
	- [问题](#问题)
	- [暴力法](#暴力法)
	- [暴力法稍加改进(然并卵)](#暴力法稍加改进然并卵)
	- [双指针法(推荐)](#双指针法推荐)
	- [二分查找法(不懂)](#二分查找法不懂)
  
# [长度最小的子数组 Minimum Size Subarray Sum -209](https://leetcode-cn.com/problems/minimum-size-subarray-sum/)  
  
## 问题  
给定一个含有 n 个正整数的数组和一个正整数 s ，找出该数组中满足其和 `≥ s` 的`长度最小的连续子数组`。如果不存在符合条件的连续子数组，返回 `0`。  
  
示例:   
  
    输入: s = 7, nums = [2,3,1,2,4,5]  
    输出: 2  
    解释: 子数组 [4,5] 是该条件下的长度最小的连续子数组。  
  
进阶:  
如果你已经完成了 `O(n)` 时间复杂度的解法, 请尝试 `O(n log n)` 时间复杂度的解法。  
  
## 暴力法  
```java  
class Solution {  
    public int minSubArrayLen(int s, int[] nums) {  
        int len = Integer.MAX_VALUE, tem;  
        for (int i = 0; i < nums.length; i++) {  
            tem = 0;  
            for (int j = i; j < nums.length; j++) {  
                tem += nums[j];  
                if (tem >= s) {  
                    len = Math.min(len, j - i + 1);  
                    break;  
                }  
            }  
        }  
        return len == Integer.MAX_VALUE ? 0 : len;  
    }  
}  
```  
  
时间复杂度：`O(n^2)`  
空间复杂度：`O(1)`  
  
## 暴力法稍加改进(然并卵)  
```java  
class Solution {  
    public int minSubArrayLen(int s, int[] nums) {  
        int len = Integer.MAX_VALUE, tem;  
        for (int i = 0; i < nums.length; i++) {  
            tem = 0;  
            for (int j = i; j < nums.length && j < i + len - 1; j++) {  
                tem += nums[j];  
                if (tem >= s) {  
                    len = j - i + 1;  
                    break;  
                }  
            }  
        }  
        return len == Integer.MAX_VALUE ? 0 : len;  
    }  
}  
```  
  
时间复杂度：`O(n^2)`  
空间复杂度：`O(1)`  
  
## 双指针法(推荐)  
我们需要定义两个指针left和right，分别记录子数组的左右的边界位置，然后我们让right向右移，直到子数组和大于等于给定值或者right达到数组末尾，此时我们更新最短距离，并且将left向右移一位，然后再sum中减去移去的值，然后重复上面的步骤，直到right到达末尾，且left到达临界位置，即要么到达边界，要么再往右移动，和就会小于给定值  
  
```java  
class Solution {  
    public int minSubArrayLen(int s, int[] nums) {  
        int len = Integer.MAX_VALUE, left = 0, sum = 0;  
        for (int right = 0; right < nums.length; right++) {  
            sum += nums[right]; //左指针不动，右指针一直向右移动  
            while (sum >= s) {  
                len = Math.min(len, right - left + 1); //当满足条件时先记录一下当前需要的长度  
                sum -= nums[left++]; //然后左指针向右移动(移动过程中如果满足条件则长度-1)，直到不满足条件(继续移动右指针)  
            }  
        }  
        return len == Integer.MAX_VALUE ? 0 : len;  
    }  
}  
```  
  
时间复杂度：`O(n)`  
空间复杂度：`O(1)`  
  
## 二分查找法(不懂)  
思路是，我们建立一个比原数组长一位的sums数组，其中`sums[i]`表示nums数组中`[0, i - 1]`的和，然后我们对于sums中每一个值`sums[i]`，用二分查找法找到子数组的右边界位置，使该子数组之和大于`sums[i] + s`，然后我们更新最短长度的距离即可。  
  
```java  
class Solution {  
    public int minSubArrayLen(int s, int[] nums) {  
        int len = nums.length, res = len + 1;  
        int[] sums = new int[len + 1];  
        for(int i = 1; i<len+1; i++){  
            sums[i] = sums[i-1] + nums[i-1];  
        }  
        for(int i = 0; i<len+1; i++){  
            int right = searchRight(i+1, len, sums[i]+s, sums);  
            if(right == len + 1) break;  
            if(res > right - i) res = right - i;  
        }  
        return res == len + 1?0:res;  
    }  
  
    private int searchRight(int left, int right, int key, int sums[]){  
        while(left <= right){  
            int mid = (left + right)/2;  
            if(sums[mid] >= key){  
                right = mid - 1;  
            }else{  
                left = mid + 1;  
            }  
        }  
        return left;  
    }  
}  
```  
  
2016-12-28  
2018-12-16  
