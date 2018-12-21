| Markdown版本笔记 | 我的GitHub首页 | 我的博客 | 我的微信 | 我的邮箱 |  
| :------------: | :------------: | :------------: | :------------: | :------------: |  
| [MyAndroidBlogs][Markdown] | [baiqiantao][GitHub] | [baiqiantao][博客] | bqt20094 | baiqiantao@sina.com |  
  
[Markdown]:https://github.com/baiqiantao/MyAndroidBlogs  
[GitHub]:https://github.com/baiqiantao  
[博客]:http://www.cnblogs.com/baiqiantao/  
  
领扣-5 最长回文子串 Longest Palindromic Substring MD  
***  
目录  
===  

- [[最长回文子串 Longest Palindromic Substring](https://leetcode-cn.com/problems/longest-palindromic-substring/)](#最长回文子串-longest-palindromic-substringhttpsleetcode-cncomproblemslongest-palindromic-substring)
	- [问题](#问题)
	- [暴力循环法(最基础的方式)](#暴力循环法最基础的方式)
		- [暴力循环法1](#暴力循环法1)
		- [暴力循环法2](#暴力循环法2)
	- [动态规划法(主流方式)](#动态规划法主流方式)
	- [技巧性型方法](#技巧性型方法)
		- [中心扩展算法(逻辑清晰)](#中心扩展算法逻辑清晰)
		- [最长公共子串法(简单但不推荐)](#最长公共子串法简单但不推荐)
		- [Manacher算法(逆天但看不懂)](#manacher算法逆天但看不懂)
  
# [最长回文子串 Longest Palindromic Substring](https://leetcode-cn.com/problems/longest-palindromic-substring/)  
动态规划  
  
## 问题  
给定一个字符串 s，找到 s 中最长的回文子串。你可以假设 s 的最大长度为 1000。  
  
示例 1：  
  
    输入: "babad"  
    输出: "bab"  
    注意: "aba" 也是一个有效答案。  
  
示例 2：  
  
    输入: "cbbd"  
    输出: "bb"  
  
## 暴力循环法(最基础的方式)  
### 暴力循环法1  
循环遍历所有子串，同时判断子串是不是回文。  
  
```java  
class Solution {  
    public String longestPalindrome(String s) {  
        if (s == null || s.length() == 0) return s;  
          
        String palindrome = s.substring(0, 1);  
        for (int i = 0; i < s.length(); i++) {  
            for (int j = i + 1; j < s.length(); j++) {  
                char[] array = s.substring(i, j + 1).toCharArray();  
                for (int k = 0; k < array.length / 2; k++) {  
                    if (array[k] != array[array.length - 1 - k]) {  
                        break;  
                    }  
                    if (k == array.length / 2 - 1) {  
                        String sub = s.substring(i, j + 1);  
                        palindrome = palindrome.length() > sub.length() ? palindrome : sub;  
                    }  
                }  
            }  
        }  
        return palindrome;  
    }  
}  
```  
  
提交时提示超出时间限制  
  
时间复杂度：`O(n^3)`  
空间复杂度：`O(1)`  
  
### 暴力循环法2  
遍历的时候借助 Java 中的缓冲对象，虽然时间复杂度为`O(n^2)`，但是其实耗时比上一种还厉害，因为每次构造 StringBuilder并反转然后比较字符串 所消耗的时间要比遍历char数组还要长。  
  
```java  
class Solution {  
    public String longestPalindrome(String s) {  
        if (s == null || s.length() == 0) return s;  
  
        String candidate = s.substring(s.length() - 1);  
        if (s.equals(new StringBuilder(s).reverse().toString())) {  
            return s;  
        } else {  
            int len = s.length();  
            for (int i = 0; i < len; i++) {  
                for (int j = i + 1; j < len; j++) {  
                    String str = s.substring(i, j + 1);  
                    if (str.equals(new StringBuilder(str).reverse().toString())) {  
                        if (str.length() > candidate.length()) {  
                            candidate = str;  
                        }  
                    }  
                }  
            }  
            return candidate;  
        }  
    }  
}  
```  
  
时间复杂度：`O(n^2)`  
空间复杂度：`O(1)`  
  
这种方式同样超时！  
  
## 动态规划法(主流方式)  
为了改进暴力法，我们首先观察如何避免在验证回文时进行`不必要的重复计算`。  
  
考虑 “ababa” 这个示例。如果我们已经知道 “bab” 是回文，那么很明显，“ababa” 一定是回文，因为它的`左首字母和右尾字母`是相同的。  
  
我们给出 P(i,j) 的定义如下：  
  
P(i,j) =   
\begin{cases}  
{true, 如果子串 S_i ... S_j 是回文子串}\\   
{false, 其它情况}   
\end{cases}    
  
因此：  
  
    P(i, j) = P(i+1, j-1) and S(i) == S(j)  
  
基本示例如下：  
  
    P(i,i) = true  
    P(i,i+1) = S(i) == S(i+1)  
  
第一行的意思是： P(i,i) 的结果一定为 true  
第一行的意思是： P(i,i+1) 的结果为 true 的条件为第 i 个字符和第 i+1 个字符相同  
  
这产生了一个直观的`动态规划递推式`，我们首先初始化一个字母和二个字母的回文，然后找到所有三个字母回文，并依此类推…  
  
```java  
class Solution {  
    public String longestPalindrome(String s) {  
        if (s == null || s.length() == 0) return s;  
  
        int len = s.length(), left = 0, right = 0;  
        boolean[][] dp = new boolean[len][len];//动态规划一般必不可少的一步就是创建数组，dp[i][j] 表示字符串区间 [i, j] 是否为回文串  
        for (int j = 0; j < len; j++) { //注意这里遍历时的 i 和 j 和平常我们遍历的定义刚好是相反的  
            for (int i = 0; i <= j; i++) { //这里只需遍历到  i==j 即可，因为 j<i 时根本不存在这样的字串  
                dp[i][j] = s.charAt(i) == s.charAt(j) && (j - i < 2 || dp[i + 1][j - 1]);  
                if (dp[i][j] && j - i > right - left) {//如果是回文字符串，并且比之前的回文字符串要长，更新字符串长度，记录字符串  
                    left = i;  
                    right = j;  
                }  
            }  
        }  
        return s.substring(left, right + 1);  
    }  
}  
```  
  
时间复杂度：`O(n^2)`  
空间复杂度：`O(n^2)`，因为该方法要使用 O(n^2) 的空间来存储表  
  
## 技巧性型方法  
  
### 中心扩展算法(逻辑清晰)  
事实上，只需使用恒定的空间，我们就可以在 O(n^2) 的时间内解决这个问题。  
  
我们观察到回文中心的两侧互为镜像。因此，回文可以从它的`中心`展开，`向两边扩散`来寻找回文串。  
  
注意奇偶情况，由于回文串的长度可奇可偶，比如"bob"是奇数形式的回文，"noon"就是偶数形式的回文，两种形式的回文都要搜索。  
- 对于`奇数`形式的，我们就从遍历到的位置为`中心`，向两边进行扩散  
- 对于`偶数`情况，我们就把`当前位置和下一个位置`当作回文的最中间两个字符，然后向两边进行搜索  
  
```java  
class Solution {  
    public String longestPalindrome(String s) {  
        if (s == null || s.length() == 0) return s;  
  
        int start = 0, end = 0;  
        for (int i = 0; i < s.length(); i++) {  
            int len1 = expandAroundCenter(s, i, i); //以一个点为中心向外扩展，直到不是回文为止  
            int len2 = expandAroundCenter(s, i, i + 1); //以两个点为中心向外扩展，直到不是回文为止  
            int len = Math.max(len1, len2); //找出两个中的最大回文长度  
            if (len > end - start) { //如果大于保存的最大回文长度，则计算并更新最大回文的位置  
                start = i - (len - 1) / 2;  
                end = i + len / 2;  
            }  
        }  
        return s.substring(start, end + 1);  
    }  
  
    private int expandAroundCenter(String s, int left, int right) {  
        int L = left, R = right;  
        while (L >= 0 && R < s.length() && s.charAt(L) == s.charAt(R)) {  
            L--; //向左扩展  
            R++; //向右扩展  
        }  
        return R - L - 1;  
    }  
}  
```  
  
时间复杂度：`O(n^2)`  
空间复杂度：`O(1)`  
  
  
### 最长公共子串法(简单但不推荐)  
有些人会忍不住提出一个快速的解决方案，不幸的是，这个解决方案有缺陷(但是可以很容易地纠正)：  
  
反转 S，使之变成 S' 。找到 S 和 S' 之间最长的公共子串，这也必然是最长的回文子串。  
  
让我们尝试一下这个例子：S =“abacdfgdcaba” , S'  =“abacdgfdcaba”：  
  
S 以及 S' 之间的最长公共子串为 “abacd”，显然，这不是回文。  
  
我们可以看到，当 S 的其他部分中存在非回文子串的反向副本时，最长公共子串法就会失败。  
  
为了纠正这一点，每当我们找到最长的公共子串的候选项时，都需要检查`子串的索引`是否与`反向子串的原始索引`相同。如果相同，那么我们尝试更新目前为止找到的最长回文子串；如果不是，我们就跳过这个候选项并继续寻找下一个候选。  
  
```java  
class Solution {  
    public String longestPalindrome(String s) {  
        if (s == null || s.length() == 0) return s;  
  
        String s2 = new StringBuilder(s).reverse().toString();  
        String candidate = s.substring(s.length() - 1);  
        int len = s.length();  
        for (int i = 0; i < len; i++) {  
            for (int j = i + 1; j < len; j++) {  
                String str = s.substring(i, j + 1);  
                if (str.length() > candidate.length() && s2.contains(str) && s.indexOf(str) == s.indexOf(new StringBuilder(str).reverse().toString())) {  
                    candidate = str;  
                }  
            }  
        }  
        return candidate;  
    }  
}  
```  
  
依旧超时！  
  
时间复杂度为 `O(n^2)`  
  
### Manacher算法(逆天但看不懂)  
最后要来的就是大名鼎鼎的马拉车算法`Manacher's Algorithm`，这个算法的神奇之处在于将时间复杂度提升到了 O(n) 这种逆天的地步，而算法本身也设计的很巧妙。  
  
然而这是一个非同寻常的算法，在有限的时间内想到这个算法将会是一个不折不扣的挑战  
  
```java  
class Solution {  
    public String longestPalindrome(String s) {  
        List<Character> s_new = new ArrayList<>();  
        for (int i = 0; i < s.length(); i++) {  
            s_new.add('#');  
            s_new.add(s.charAt(i));  
        }  
        s_new.add('#');  
        List<Integer> Len = new ArrayList<>();  
        String sub = "";//最长回文子串  
        int sub_midd = 0;//表示在i之前所得到的Len数组中的最大值所在位置  
        int sub_side = 0;//表示以sub_midd为中心的最长回文子串的最右端在S_new中的位置  
        Len.add(1);  
        for (int i = 1; i < s_new.size(); i++) {  
            if (i < sub_side) {//i < sub_side时，在Len[j]和sub_side - i中取最小值，省去了j的判断  
                int j = 2 * sub_midd - i;  
                if (j >= 2 * sub_midd - sub_side && Len.get(j) <= sub_side - i) {  
                    Len.add(Len.get(j));  
                } else Len.add(sub_side - i + 1);  
            } else //i >= sub_side时，从头开始匹配  
            Len.add(1);  
            while ((i - Len.get(i) >= 0 && i + Len.get(i) < s_new.size()) && (s_new.get(i - Len.get(i)) == s_new.get(i + Len.get(i))))  
                Len.set(i, Len.get(i) + 1);//s_new[i]两端开始扩展匹配，直到匹配失败时停止  
            if (Len.get(i) >= Len.get(sub_midd)) {//匹配的新回文子串长度大于原有的长度  
                sub_side = Len.get(i) + i - 1;  
                sub_midd = i;  
            }  
        }  
        sub = s.substring((2 * sub_midd - sub_side) / 2, sub_side / 2);//在s中找到最长回文子串的位置  
        return sub;  
    }  
}  
```  
  
时间复杂度：`O(n)`  
  
2018-12-10  
