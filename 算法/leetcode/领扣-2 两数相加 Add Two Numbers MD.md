| Markdown版本笔记 | 我的GitHub首页 | 我的博客 | 我的微信 | 我的邮箱 |  
| :------------: | :------------: | :------------: | :------------: | :------------: |  
| [MyAndroidBlogs][Markdown] | [baiqiantao][GitHub] | [baiqiantao][博客] | bqt20094 | baiqiantao@sina.com |  
  
[Markdown]:https://github.com/baiqiantao/MyAndroidBlogs  
[GitHub]:https://github.com/baiqiantao  
[博客]:http://www.cnblogs.com/baiqiantao/  
  
领扣-2 两数相加 Add Two Numbers MD.md  
***  
目录  
===  

- [[两数相加 Add Two Numbers](https://leetcode-cn.com/problems/add-two-numbers/)](#两数相加-add-two-numbershttpsleetcode-cncomproblemsadd-two-numbers)
	- [题目](#题目)
	- [解决方案](#解决方案)
	- [代码](#代码)
  
# [两数相加 Add Two Numbers](https://leetcode-cn.com/problems/add-two-numbers/)  
链表  
  
## 题目  
给出两个 **非空** 的链表用来表示两个非负的整数。其中，它们各自的位数是按照 **逆序** 的方式存储的，并且它们的每个节点只能存储 **一位** 数字。  
You are given two **non-empty** linked lists representing two non-negative integers. The digits are stored in **reverse order** and each of their nodes contain a single digit.   
  
如果，我们将这两个数相加起来，则会返回一个新的链表来表示它们的和。  
Add the two numbers and return it as a linked list.  
  
您可以假设除了数字 0 之外，这两个数都不会以 0 开头。  
You may assume the two numbers do not contain any leading zero, except the number 0 itself.  
  
示例：  
  
    输入：(2 -> 4 -> 3) + (5 -> 6 -> 4)  
    输出：7 -> 0 -> 8  
    原因：342 + 465 = 807  
  
方法声明：  
```java  
class Solution {  
    public ListNode addTwoNumbers(ListNode l1, ListNode l2) {  
  
    }  
}  
  
class ListNode {  
    int val;  
    ListNode next;  
  
    ListNode(int x) {  
        val = x;  
    }  
}  
```  
  
## 解决方案  
**思路**  
我们使用变量来`跟踪进位`，并从包含最低有效位的表头开始模拟逐位相加的过程。  
![](https://leetcode-cn.com/problems/add-two-numbers/Figures/2/2_add_two_numbers.svg)  
  
**算法**  
就像你在纸上计算两个数字的和那样，我们首先从最低有效位也就是列表 l1 和 l2 的表头开始相加。由于每位数字都应当处于 0…9 的范围内，我们计算两个数字的和时可能会出现“溢出”。例如，5 + 7 = 12。在这种情况下，我们会将当前位的数值设置为 2，并将进位 carry = 1 带入下一次迭代。进位 carry 必定是 0 或 1，这是因为两个数字相加（考虑到进位）可能出现的最大和为 9 + 9 + 1 = 19。  
  
请注意，我们使用哑结点来简化代码。如果没有哑结点，则必须编写额外的条件语句来初始化表头的值。  
  
**请特别注意以下情况**  
  
| 测试用例 |  说明  |  
| ------------ | ------------ |  
| l1=[0,1] l2=[0,1,2] | 当一个列表比另一个列表长时 |  
| l1=[] l2=[0,1] | 当一个列表为空时，即出现空列表时 |  
| l1=[9,9] l2=[1] | 求和运算最后可能出现额外的进位，这一点很容易被遗忘 |  
  
## 代码  
```java  
class Solution {  
    public ListNode addTwoNumbers(ListNode l1, ListNode l2) {  
        ListNode dummyHead = new ListNode(0); //【核心1】定义一个虚拟头(哑结点)  
        ListNode curr = dummyHead;  
  
        int carry = 0; // 使用变量来跟踪进位  
        while (l1 != null || l2 != null) {  
            int val1 = l1 == null ? 0 : l1.val;  
            int val2 = l2 == null ? 0 : l2.val;  
            int add = val1 + val2 + carry; //【核心2】这里要加上进位值  
            int val = add > 9 ? add % 10 : add; //当前结点的值  
            carry = add > 9 ? 1 : 0; //当前节点给next结点的进位值  
  
            curr.next = new ListNode(val);//确定每一个结点的值  
  
            curr = curr.next;//【核心3】指向下一个结点  
            l1 = l1 == null ? null : l1.next;  
            l2 = l2 == null ? null : l2.next;  
        }  
        if (carry > 0) {  
            curr.next = new ListNode(carry); //为了解决最后一次 add 时有进位，但是没有 next 处理此进位的问题  
        }  
        return dummyHead.next; //【核心4】返回虚拟头的next结点  
    }  
}  
  
class ListNode {  
    int val;  
    ListNode next;  
  
    ListNode(int x) {  
        val = x;  
    }  
}  
```  
时间复杂度：`O(max(m,n))`，假设 m 和 n 分别表示 l1 和 l2 的长度，上面的算法最多重复 `max(m,n)` 次。  
空间复杂度：`O(max(m,n))`，新列表的长度最多为 `max(m,n) + 1`。  
  
2018-12-6  
