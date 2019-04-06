| Markdown版本笔记 | 我的GitHub首页 | 我的博客 | 我的微信 | 我的邮箱 |  
| :------------: | :------------: | :------------: | :------------: | :------------: |  
| [MyAndroidBlogs][Markdown] | [baiqiantao][GitHub] | [baiqiantao][博客] | bqt20094 | baiqiantao@sina.com |  
  
[Markdown]:https://github.com/baiqiantao/MyAndroidBlogs  
[GitHub]:https://github.com/baiqiantao  
[博客]:http://www.cnblogs.com/baiqiantao/  
  
领扣-1/167 两数之和 Two Sum MD  
***  
目录  
===  

- [[1-两数之和 Two Sum](https://leetcode-cn.com/problems/two-sum/)](#1-两数之和-Two-Sumhttpsleetcode-cncomproblemstwo-sum)
	- [题目](#题目)
	- [方法一：暴力法](#方法一：暴力法)
	- [方法二：两遍哈希表](#方法二：两遍哈希表)
	- [方法三：一遍哈希表(推荐)](#方法三：一遍哈希表推荐)
- [[167-两数之和 - 输入有序数组](https://leetcode-cn.com/problems/two-sum-ii-input-array-is-sorted/)](#167-两数之和---输入有序数组httpsleetcode-cncomproblemstwo-sum-ii-input-array-is-sorted)
	- [题目](#题目)
	- [本题可以使用上题任意一种解法！](#本题可以使用上题任意一种解法！)
	- [两层遍历](#两层遍历)
	- [两层遍历 + 二分法](#两层遍历--二分法)
	- [二分查找](#二分查找)
	- [两个指针相遇(推荐)](#两个指针相遇推荐)
  
# [1-两数之和 Two Sum](https://leetcode-cn.com/problems/two-sum/)  
数组 哈希表  
  
## 题目  
给定一个整数数组 `nums` 和一个目标值 `target`，请你在该数组中找出和为目标值的那 **两个** 整数，并返回他们的数组下标。  
  
你可以假设每种输入只会对应一个答案。但是，你不能重复利用这个数组中同样的元素。  
  
示例:  
  
    给定 nums = [2, 7, 11, 15], target = 9  
    因为 nums[0] + nums[1] = 2 + 7 = 9  
    所以返回 [0, 1]  
  
方法声明：  
```java  
class Solution {  
    public int[] twoSum(int[] nums, int target) {  
          
    }  
}  
```  
  
## 方法一：暴力法  
暴力法很简单。遍历每个元素 x，并查找是否存在一个值与 target−x 相等的目标元素。  
```java  
class Solution {  
    public int[] twoSum(int[] nums, int target) {  
        for (int i = 0; i < nums.length; i++) {  
            for (int j = i + 1; j < nums.length; j++) { // j 从 i + 1 开始  
                if (nums[i] + nums[j] == target) {  
                    return new int[] { i, j };  
                }  
            }  
        }  
        throw new IllegalArgumentException("No two sum solution");  
    }  
}  
```  
  
时间复杂度：`O(n^2)`， 对于每个元素，我们试图通过遍历数组的`其余`部分来寻找它所对应的目标元素，这将耗费 `O(n)` 的时间。因此时间复杂度为 `O(n^2)`  
空间复杂度：`O(1)`  
  
## 方法二：两遍哈希表  
为了对运行时间复杂度进行优化，我们需要一种更有效的方法来检查数组中是否存在目标元素。如果存在，我们需要找出它的索引。保持数组中的每个元素与其索引相互对应的最好方法是什么？哈希表。  
  
通过`以空间换取时间`的方式，我们可以将查找时间从 `O(n)` 降低到 `O(1)`。哈希表正是为此目的而构建的，它支持以 *近似* 恒定的时间进行快速查找。我用“近似”来描述，是因为一旦出现冲突，查找用时可能会退化到 `O(n)`。但只要你仔细地挑选哈希函数，在哈希表中进行查找的用时应当被摊销为 `O(1)`。  
  
一个简单的实现使用了两次迭代。在第一次迭代中，我们将每个元素的值和它的索引`添加`到表中。然后，在第二次迭代中，我们将`检查`每个元素所对应的目标元素`target−nums[i]`是否存在于表中。注意，该目标元素不能是 `nums[i]` 本身！  
  
```java  
class Solution {  
    public int[] twoSum(int[] nums, int target) {  
        Map<Integer, Integer> map = new HashMap<>();  
        for (int i = 0; i < nums.length; i++) {  
            map.put(nums[i], i);  
        }  
        for (int i = 0; i < nums.length; i++) {  
            int complement = target - nums[i];  
            if (map.containsKey(complement) && map.get(complement) != i) {  
                return new int[] { i, map.get(complement) };  
            }  
        }  
        throw new IllegalArgumentException("No two sum solution");  
    }  
}  
```  
  
时间复杂度：`O(n)`， 我们把包含有 n 个元素的列表`遍历两次`。由于哈希表将查找时间缩短到 `O(1)` ，所以时间复杂度为 `O(n)`。  
空间复杂度：`O(n)`， 所需的额外空间取决于哈希表中存储的元素数量，该表中存储了 n 个元素。   
  
## 方法三：一遍哈希表(推荐)  
事实证明，我们可以一次完成。在进行迭代并将元素插入到表中的同时，我们还会回过头来检查表中是否已经存在当前元素所对应的目标元素。如果它存在，那我们已经找到了对应解，并立即将其返回。  
  
```java  
class Solution {  
    public int[] twoSum(int[] nums, int target) {  
        Map<Integer, Integer> map = new HashMap<>();  
        for (int i = 0; i < nums.length; i++) {  
            int complement = target - nums[i];  
            if (map.containsKey(complement)) {  
                return new int[] { map.get(complement), i };  
            }  
            map.put(nums[i], i);  
        }  
        throw new IllegalArgumentException("No two sum solution");  
    }  
}  
```  
  
时间复杂度：`O(n)`， 我们只遍历了包含有 n 个元素的列表一次。在表中进行的每次查找只花费 `O(1)` 的时间。  
空间复杂度：`O(n)`， 所需的额外空间取决于哈希表中存储的元素数量，该表最多需要存储 n 个元素。  
  
# [167-两数之和 - 输入有序数组](https://leetcode-cn.com/problems/two-sum-ii-input-array-is-sorted/)  
数组 双指针 二分查找  
## 题目  
给定一个已按照 **_升序排列_** 的有序数组，找到两个数使得它们相加之和等于目标数。  
  
函数应该返回这两个下标值index1 和 index2，其中 index1 必须小于 index2_。_  
  
**说明:**  
  
*   返回的下标值（index1 和 index2）不是从零开始的。  
*   你可以假设每个输入只对应唯一的答案，而且你不可以重复使用相同的元素。  
  
**示例:**  
  
    输入: numbers = [2, 7, 11, 15], target = 9  
    输出: [1,2]  
    解释: 2 与 7 之和等于目标数 9 。因此 index1 = 1, index2 = 2 。  
  
## 本题可以使用上题任意一种解法！  
  
## 两层遍历  
```java  
class Solution {  
    public int[] twoSum(int[] nums, int target) {  
        int i = 0, j = 1;  
        while (i < nums.length - 1) {  
            while (j < nums.length) {  
                if (nums[i] + nums[j] < target) {  
                    j++;  
                } else if (nums[i] + nums[j] == target) {  
                    return new int[] { i + 1, j + 1 };  
                } else if (nums[i] + nums[j] > target) {  
                    break;  
                }  
            }  
            i++;  
            j = i + 1;  
        }  
        throw new IllegalArgumentException("No two sum solution");  
    }  
}  
```  
  
时间复杂度：`O(n^2)`  
空间复杂度：`O(1)`  
  
## 两层遍历 + 二分法  
```java  
class Solution {  
    public int[] twoSum(int[] nums, int target) {  
        int i = 0, left, right, mid;  
        while (i < nums.length - 1) {  
            left = i + 1;  
            right = nums.length;  
            while (left < right) {  
                mid = left + (right - left) / 2; //中间的数  
                if (nums[i] + nums[mid] == target) {  
                    return new int[] { i + 1, mid + 1 }; //找到直接返回  
                } else if (nums[i] + nums[mid] < target) {  
                    left = mid + 1; //直接跳过若干小数  
                } else {  
                    right = mid; //直接跳过若干大数  
                }  
            }  
            i++;  
        }  
        throw new IllegalArgumentException("No two sum solution");  
    }  
}  
```  
  
时间复杂度：`O(nlgn)`  
空间复杂度：`O(1)`  
  
## 二分查找  
```java  
class Solution {  
    public int[] twoSum(int[] nums, int target) {  
        int i = 0, j;  
        while (i < nums.length - 1) {  
            j = Arrays.binarySearch(nums, i + 1, nums.length, target - nums[i]);//从 i + 1 开始查找  
            if (j > 0) { //说明找到了  
                return new int[] { i + 1, j + 1 };  
            } else {  //没找到  
                i++;  
            }  
        }  
        throw new IllegalArgumentException("No two sum solution");  
    }  
}  
```  
  
## 两个指针相遇(推荐)  
我们只需要两个指针，一个指向开头，一个指向末尾，然后向中间遍历；如果指向的两个数相加正好等于target的话，直接返回两个指针的位置即可；若小于target，左指针右移一位，若大于target，右指针左移一位。以此类推直至两个指针相遇停止。  
  
```java  
class Solution {  
    public int[] twoSum(int[] nums, int target) {  
        int left = 0, right = nums.length - 1, sum;  
        while (left < right) {  
            sum = nums[left] + nums[right];  
            if (sum == target) {  
                return new int[] { left + 1, right + 1 };  
            } else if (sum > target) { //若大于target，右指针左移一位  
                right--;  
            } else { //若小于target，左指针右移一位  
                left++;  
            }  
        }  
        throw new IllegalArgumentException("No two sum solution");  
    }  
}  
```  
  
时间复杂度：`O(n)`  
空间复杂度：`O(1)`  
  
2018-12-6  
