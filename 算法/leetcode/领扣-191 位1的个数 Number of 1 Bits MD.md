| Markdown版本笔记 | 我的GitHub首页 | 我的博客 | 我的微信 | 我的邮箱 |  
| :------------: | :------------: | :------------: | :------------: | :------------: |  
| [MyAndroidBlogs][Markdown] | [baiqiantao][GitHub] | [baiqiantao][博客] | bqt20094 | baiqiantao@sina.com |  
  
[Markdown]:https://github.com/baiqiantao/MyAndroidBlogs  
[GitHub]:https://github.com/baiqiantao  
[博客]:http://www.cnblogs.com/baiqiantao/  
  
领扣-191 位1的个数 Number of 1 Bits MD  
***  
目录  
===  

- [[位1的个数 Number of 1 Bits](https://leetcode-cn.com/problems/number-of-1-bits/)](#位1的个数-number-of-1-bitshttpsleetcode-cncomproblemsnumber-of-1-bits)
	- [问题](#问题)
	- [方式一：字符比较](#方式一：字符比较)
	- [方式二：取余运算或位运算](#方式二：取余运算或位运算)
  
# [位1的个数 Number of 1 Bits](https://leetcode-cn.com/problems/number-of-1-bits/)  
位运算  
  
## 问题  
编写一个函数，输入是一个无符号整数，返回其二进制表达式中数字位数为 ‘1’ 的个数（也被称为汉明重量）。  
Write a function that takes an unsigned integer and returns the number of '1' bits it has (also known as the [Hamming weight](http://en.wikipedia.org/wiki/Hamming_weight)).  
  
示例 :  
  
    输入: 11  
    输出: 3  
    解释: 整数 11 的二进制表示为 00000000000000000000000000001011  
   
  
示例 2:  
  
    输入: 128  
    输出: 1  
    解释: 整数 128 的二进制表示为 00000000000000000000000010000000  
  
这个问题，不管是用哪种方式实现，都是最简单的一道题了。  
  
## 方式一：字符比较  
```java  
class Solution {  
    public int hammingWeight(int n) {  
        int count = 0;  
        for (char c : Integer.toBinaryString(n).toCharArray()) {  
            if (c == '1') {  
                count++;  
            }  
        }  
        return count;  
    }  
}  
```  
  
## 方式二：取余运算或位运算  
```java  
class Solution {  
    public int hammingWeight(int n) {  
        int count = 0;  
        long l = n & 0xFFFFFFFFL; //转换为 long 类型，防止负数问题  
        while (l > 0) {  
            //if (l % 2 == 1) count++; //取余运算  
            if ((l & 1) == 1) count++; //位运算，速度相比取余运算会快一点  
            l = l >> 1; //不需要使用 >>>   
        }  
        return count;  
    }  
}  
```  
  
类似的实现1：  
```java  
class Solution {  
    public int hammingWeight(int n) {  
        int count = 0;  
        while (n != 0) {  
            if ((n & 1) == 1) count++;  
            n = n >>> 1; //必须使用 >>> ，而不能使用 >>  
        }  
        return count;  
    }  
}  
```  
  
类似的实现2：  
```java  
class Solution {  
    public int hammingWeight(int n) {  
        int count = 0;  
        for (int i = 0; i < 32; ++i) {  
            if ((n & 1) == 1) count++;  
            n = n >> 1; //不需要使用 >>>   
        }  
        return count;  
    }  
}  
```  
  
2018-12-8  
