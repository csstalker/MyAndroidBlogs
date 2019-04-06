| Markdown版本笔记 | 我的GitHub首页 | 我的博客 | 我的微信 | 我的邮箱 |  
| :------------: | :------------: | :------------: | :------------: | :------------: |  
| [MyAndroidBlogs][Markdown] | [baiqiantao][GitHub] | [baiqiantao][博客] | bqt20094 | baiqiantao@sina.com |  
  
[Markdown]:https://github.com/baiqiantao/MyAndroidBlogs  
[GitHub]:https://github.com/baiqiantao  
[博客]:http://www.cnblogs.com/baiqiantao/  
  
LruCache DiskLruCache 缓存 简介 案例 MD  
***  
目录  
===  

- [DiskLruCache 简介](#DiskLruCache-简介)
	- [缓存路径的选择](#缓存路径的选择)
	- [获取实例及初始化](#获取实例及初始化)
	- [MD5生成文件名](#MD5生成文件名)
	- [写入缓存](#写入缓存)
	- [读取缓存](#读取缓存)
	- [其他API](#其他API)
	- [日志文件](#日志文件)
- [LruCache 源码分析](#LruCache-源码分析)
  
# DiskLruCache 简介  
[GitHub](https://github.com/JakeWharton/DiskLruCache)  
  
```  
compile 'com.jakewharton:disklrucache:2.0.2'  
```  
  
LruCache 只是管理了内存中图片的存储与释放，如果图片从内存中被移除的话，那么又需要从网络上重新加载一次图片，这显然非常耗时，对此，Google 又提供了一套硬盘缓存的解决方案：DiskLruCache(其实是 JakeWharton 编写的，但获得了 Google 官方认证)。  
  
由于 DiskLruCache 并不是由 Google 官方编写的，所以这个类并没有被包含在 Android API 当中，我们需要将这个类从网上下载下来，然后手动添加到项目当中。  
  
## 缓存路径的选择  
虽然我们可以自由的设定使用 DiskLruCache 时数据的缓存位置，但是通常情况下都会将缓存的位置选择为 `/sdcard/Android/data/包名/cache` 目录下，因为  
- 默认这是存储在SD卡上的  
- 这被Android系统认定为是应用程序的缓存路径，可以被系统或其他缓存清理工具清除  
- 当程序被卸载的时候，这里的数据会自动一起被清除掉  
  
另外，我们也需要考虑如果这个手机没有SD卡，或者SD正好被移除了的情况。  
```java  
/**  
* 根据传入的uniqueName（子目录）获取硬盘缓存的路径地址  
*/  
public File getDiskCacheDir(Context context, String uniqueName) {  
    String cachePath;  
    //当SD卡【存在】或者SD卡【不可被移除】时  
    if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()) || !Environment.isExternalStorageRemovable()) {  
        cachePath = context.getExternalCacheDir().getPath();//【SDCard/Android/data/包名/cache/】目录  
    } else cachePath = context.getCacheDir().getPath();//【/data/data/包名/cache/】目录  
    return new File(cachePath + File.separator + uniqueName);  
}  
```  
  
## 获取实例及初始化  
如果我们要创建一个它的实例，需要调用它的open()方法：  
```java  
public static DiskLruCache open(File directory, int appVersion, int valueCount, long maxSize)  
```  
参数：  
- 数据的缓存地址  
- 当前应用程序的版本号【context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode】  
- 同一个key可以对应多少个缓存文件(传1即可)  
- 最多可以缓存多少字节的数据。  
  
需要注意的是，每当版本号改变，缓存路径下存储的所有数据都会被清除掉，因为DiskLruCache认为当应用程序有版本更新的时候，所有的数据都应该从网上重新获取。  
  
一个标准的open()方法如下：  
```java  
// 获取图片缓存路径  
File cacheDir = getDiskCacheDir(context, "thumb");  
if (!cacheDir.exists()) cacheDir.mkdirs();  
// 创建DiskLruCache实例，初始化缓存数据  
try {  
    mDiskLruCache = DiskLruCache.open(cacheDir, getAppVersion(context), 1, 10 * 1024 * 1024);  
} catch (IOException e) {  
    e.printStackTrace();  
}  
```  
  
## MD5生成文件名  
我们将网络图片保存到本地时，需要指定一个文件名，并且此文件名必须要和图片的URL是一 一对应的，那么用什么方式实现呢？  
  
直接使用URL来作为key？不合适，因为图片URL中可能包含一些特殊字符，这些字符有可能在命名文件时是不合法的。  
  
最简单的做法就是将图片的URL进行MD5编码，编码后的字符串肯定是唯一的，并且只会包含0-F这样的字符，完全符合文件的命名规则。  
```java  
/**  
* 使用MD5算法对传入的key进行加密并返回。  
*/  
public String hashKeyForDisk(String key) {  
    String cacheKey;  
    try {  
        //为应用程序提供信息摘要算法的功能，如 MD5 或 SHA 算法。信息摘要是安全的单向哈希函数，它接收任意大小的数据，并输出固定长度的哈希值。  
        MessageDigest mDigest = MessageDigest.getInstance("MD5");  
        mDigest.update(key.getBytes());//使用指定的 byte 数组更新摘要  
        byte[] bytes = mDigest.digest();//通过执行诸如填充之类的最终操作完成哈希计算，返回存放哈希值结果的 byte 数组  
        StringBuilder sb = new StringBuilder();  
        for (int i = 0; i < bytes.length; i++) {  
        String hex = Integer.toHexString(0xFF & bytes[i]);//以十六进制无符号整数形式返回一个整数参数的字符串表示形式  
        if (hex.length() == 1) sb.append('0');  
        sb.append(hex);  
        }  
        cacheKey = sb.toString();  
    } catch (NoSuchAlgorithmException e) {  
        cacheKey = String.valueOf(key.hashCode());  
    }  
    return cacheKey;  
}  
```  
  
## 写入缓存  
写入的操作是借助`DiskLruCache.Editor`这个类完成的，需要调用DiskLruCache的edit()方法来获取实例。  
  
接口如下：  
```java  
public Editor edit(String key) throws IOException    
```  
参数key即为缓存文件的文件名  
  
一般，我们这么写：  
```java  
public DiskLruCache.Editor editor(String key) {  
    try {  
        return mDiskLruCache.edit(hashKeyForDisk(key));  
    } catch (IOException e) {  
        e.printStackTrace();  
    }  
    return null;  
}  
```  
  
有了`DiskLruCache.Editor`的实例之后，我们可以调用它的`newOutputStream()`方法来创建一个输出流，`newOutputStream()`方法接收一个index参数，由于前面在设置valueCount的时候指定的是1，所以这里index传0就可以了。  
  
在写入操作执行完之后，我们还需要调用一下`commit()`方法进行提交才能使写入生效，调用`abort()`方法的话则表示放弃此次写入。  
  
保存 `字符串` 到缓存中  
```java  
public void put(String key, String value) {  
    DiskLruCache.Editor edit = null;  
    BufferedWriter bw = null;  
    try {  
        edit = editor(key);  
        if (edit == null) return;  
        OutputStream os = edit.newOutputStream(0);  
        bw = new BufferedWriter(new OutputStreamWriter(os));  
        bw.write(value);  
        edit.commit();//write CLEAN  
    } catch (IOException e) {  
        e.printStackTrace();  
        try {  
            edit.abort();//write REMOVE  
        } catch (IOException e1) {  
            e1.printStackTrace();  
        }  
    } finally {  
        Util.closeQuietly(bw);  
    }  
}  
```  
  
保存 `byte数组` 到缓存中  
```java  
public void put(String key, byte[] value) {  
    OutputStream out = null;  
    DiskLruCache.Editor editor = null;  
    try {  
        editor = editor(key);  
        if (editor == null) return;  
        out = editor.newOutputStream(0);  
        out.write(value);  
        out.flush();  
        editor.commit();//write CLEAN  
    } catch (Exception e) {  
        e.printStackTrace();  
        try {  
            editor.abort();//write REMOVE  
        } catch (IOException e1) {  
            e1.printStackTrace();  
        }  
    } finally {  
        Util.closeQuietly(out);  
    }  
}  
```  
  
## 读取缓存  
读取的操作是借助`DiskLruCache.Snapshot`这个类完成的，需要调用DiskLruCache的`get()`方法来获取实例。  
  
接口如下：  
```java  
public synchronized Snapshot get(String key) throws IOException  
```  
参数就是将图片URL进行MD5编码后的值  
  
通过Snapshot的getInputStream()方法可以得到缓存文件的输入流。  
  
同样地，getInputStream()方法也需要传一个index参数，这里传入0就好。  
```java  
public InputStream get(String key) {  
    try {  
        DiskLruCache.Snapshot snapshot = mDiskLruCache.get(hashKeyForDisk(key));  
        if (snapshot == null) {//not find entry , or entry.readable = false  
            Log.e(TAG, "not find entry , or entry.readable = false");  
            return null;  
        }  
        //write READ  
        return snapshot.getInputStream(0);  
    } catch (IOException e) {  
        e.printStackTrace();  
        return null;  
    }  
}  
```  
  
## 其他API  
```java  
size()  
```  
这个方法会返回当前缓存路径下所有缓存数据的总字节数，以byte为单位，如果应用程序中需要在界面上显示当前缓存数据的总大小，就可以通过调用这个方法计算出来。  
  
```java  
flush()  
```  
这个方法用于将内存中的操作记录同步到日志文件（也就是journal文件）当中。  
这个方法非常重要，因为DiskLruCache能够正常工作的前提就是要依赖于journal文件中的内容。并不是每次写入缓存都要调用一次flush()方法的，频繁地调用并不会带来任何好处，只会额外增加同步journal文件的时间。比较标准的做法就是在Activity的onPause()方法中去调用一次flush()方法就可以了。  
  
```java  
close()  
```  
这个方法用于将DiskLruCache关闭掉，是和open()方法对应的一个方法。  
关闭掉了之后就不能再调用DiskLruCache中任何操作缓存数据的方法，通常只应该在Activity的onDestroy()方法中去调用close()方法。  
  
```java  
delete()  
```  
这个方法用于将所有的缓存数据全部删除，比如很多应用中的那个手动清理缓存功能，其实只需要调用一下DiskLruCache的delete()方法就可以实现了。  
  
```java  
remove(String key)  
```  
这个方法用于移除指定的缓存，我们并不应该经常去调用它，因为你完全不需要担心缓存的数据过多从而占用SD卡太多空间的问题，DiskLruCache会根据我们在调用open()方法时设定的缓存最大值来自动删除多余的缓存。只有你确定某个key对应的缓存内容已经过期，需要从网络获取最新数据的时候才应该调用remove()方法来移除缓存。  
  
## 日志文件  
缓存目录下会自动生成一个名为`journal`的日志文件，程序对每张图片的操作记录都存放在这个文件中，基本上看到journal这个文件就标志着该程序使用了DiskLruCache技术了。  
  
其内容片断如下  
![](index_files/5d2e652b-ac55-4902-838a-7b9603d06e42.png)  
  
前五行被称为journal文件的头。  
- 第一行是个固定的字符串"libcore.io.DiskLruCache"，标志着我们使用的是DiskLruCache技术。  
- 第二行是DiskLruCache的版本号，这个值是恒为1的。  
- 第三行是应用程序的版本号，我们在open()方法里传入的版本号是什么这里就会显示什么。  
- 第四行是valueCount，这个值也是在open()方法中传入的，通常情况下都为1。  
- 第五行是一个空行。  
  
第六行是以一个DIRTY前缀开始的，后面紧跟着缓存图片的key。  
通常我们看到DIRTY这个字样都不代表着什么好事情，意味着这是一条脏数据。  
  
没错，每当我们调用一次DiskLruCache的edit()方法时，都会向journal文件中写入一条DIRTY记录，表示我们正准备写入一条缓存数据，但不知结果如何。然后调用commit()方法表示写入缓存成功，这时会向journal中写入一条CLEAN记录，意味着这条“脏”数据被“洗干净了”，调用abort()方法表示写入缓存失败，这时会向journal中写入一条REMOVE记录。  
  
也就是说，每一行DIRTY的key，后面都应该有一行对应的CLEAN或者REMOVE的记录，否则这条数据就是“脏”的，会被自动删除掉。  
  
另外，DiskLruCache会在每一行CLEAN记录的最后加上该条缓存数据的大小，以字节为单位。前面我们所学的size()方法可以获取到当前缓存路径下所有缓存数据的总字节数，其实它的工作原理就是把journal文件中所有CLEAN记录的字节数相加，求出的总合再把它返回而已。  
  
每当我们调用get()方法去读取一条缓存数据时，就会向journal文件中写入一条READ记录。  
  
那么你可能会担心了，如果我不停频繁操作的话，就会不断地向journal文件中写入数据，那这样journal文件岂不是会越来越大？  
  
这倒不必担心，DiskLruCache中使用了一个redundantOpCount变量来记录用户操作的次数，每执行一次写入、读取或移除缓存的操作，这个变量值都会加1，当变量值达到2000的时候就会触发重构journal的事件，这时会自动把journal中一些多余的、不必要的记录全部清除掉，保证journal文件的大小始终保持在一个合理的范围内。  
  
# LruCache 源码分析  
```java  
package android.util;  
import java.util.LinkedHashMap;  
import java.util.Map;  
/**  
 * A cache that holds strong references to a limited number of values. Each time  
 * a value is accessed, it is moved to the head of a queue. When a value is  
 * added to a full cache, the value at the end of that queue is evicted and may  
 * become eligible for garbage collection.  
 * Cache保存一个强引用来限制内容数量，每当Item被访问的时候，此Item就会移动到队列的头部。  
 * 当cache已满的时候加入新的item时，在队列尾部的item会被回收。  
 *  
 * <p>If your cached values hold resources that need to be explicitly released,  
 * override {@link #entryRemoved}.  
 * 如果你cache的某个值需要明确释放，重写entryRemoved()  
 *  
 * <p>If a cache miss should be computed on demand for the corresponding keys,  
 * override {@link #create}. This simplifies the calling code, allowing it to  
 * assume a value will always be returned, even when there's a cache miss.  
 * 如果key相对应的item丢掉啦，重写create().这简化了调用代码，即使丢失了也总会返回。  
 *  
 * <p>By default, the cache size is measured in the number of entries. Override  
 * {@link #sizeOf} to size the cache in different units. For example, this cache  
 * is limited to 4MiB of bitmaps:  
 * 默认cache大小是测量的item的数量，重写sizeof计算不同item的大小。  
 *  
 * <pre>   {@code  
 *   int cacheSize = 4 * 1024 * 1024; // 4MiB  
 *   LruCache<String, Bitmap> bitmapCache = new LruCache<String, Bitmap>(cacheSize) {  
 *       protected int sizeOf(String key, Bitmap value) {  
 *           return value.getByteCount();  
 *       }  
 *   }}</pre>  
 *  
 * <p>This class is thread-safe. Perform multiple cache operations atomically by  
 * synchronizing on the cache: <pre>   {@code  
 *   synchronized (cache) {  
 *     if (cache.get(key) == null) {  
 *         cache.put(key, value);  
 *     }  
 *   }}</pre>  
 *  
 * <p>This class does not allow null to be used as a key or value. A return  
 * value of null from {@link #get}, {@link #put} or {@link #remove} is  
 * unambiguous: the key was not in the cache.  
 * 不允许key或者value为null。当get()，put()，remove()返回值为null时，key相应的项不在cache中  
 */  
public class LruCache<K, V> {  
    private final LinkedHashMap<K, V> map;  
    /** Size of this cache in units. Not necessarily the number of elements. */  
    private int size; //已经存储的大小  
    private int maxSize; //规定的最大存储空间  
    private int putCount; //put的次数  
    private int createCount; //create的次数  
    private int evictionCount; //回收的次数  
    private int hitCount; //命中的次数  
    private int missCount; //丢失的次数  
    /**  
     * @param maxSize for caches that do not override {@link #sizeOf}, this is  
     *     the maximum number of entries in the cache. For all other caches,  
     *     this is the maximum sum of the sizes of the entries in this cache.  
     */  
    public LruCache(int maxSize) {  
        if (maxSize <= 0) {  
            throw new IllegalArgumentException("maxSize <= 0");  
        }  
        this.maxSize = maxSize;  
        this.map = new LinkedHashMap<K, V>(0, 0.75f, true);  
    }  
    /**  
     * Returns the value for {@code key} if it exists in the cache or can be  
     * created by {@code #create}. If a value was returned, it is moved to the  
     * head of the queue. This returns null if a value is not cached and cannot be created.  
     * 通过key返回相应的item，或者创建返回相应的item。相应的item会移动到队列的头部，  
     * 如果item的value没有被cache或者不能被创建，则返回null。  
     */  
    public final V get(K key) {  
        if (key == null) {  
            throw new NullPointerException("key == null");  
        }  
        V mapValue;  
        synchronized (this) {  
            mapValue = map.get(key);  
            if (mapValue != null) {  
                hitCount++; //命中  
                return mapValue;  
            }  
            missCount++; //丢失  
        }  
        /*  
         * Attempt to create a value. This may take a long time, and the map may  
         * be different when create() returns. If a conflicting value was added  
         * to the map while create() was working, we leave that value in the map  
         * and release the created value. 如果丢失了就试图创建一个item  
         */  
        V createdValue = create(key);  
        if (createdValue == null) {  
            return null;  
        }  
        synchronized (this) {  
            createCount++;//创建++  
            mapValue = map.put(key, createdValue);  
            if (mapValue != null) {  
                // There was a conflict so undo that last put  
                //如果前面存在oldValue，那么撤销put（）  
                map.put(key, mapValue);  
            } else {  
                size += safeSizeOf(key, createdValue);  
            }  
        }  
        if (mapValue != null) {  
            entryRemoved(false, key, createdValue, mapValue);  
            return mapValue;  
        } else {  
            trimToSize(maxSize);  
            return createdValue;  
        }  
    }  
    /**  
     * Caches {@code value} for {@code key}. The value is moved to the head of  
     * the queue.  
     *  
     * @return the previous value mapped by {@code key}.  
     */  
    public final V put(K key, V value) {  
        if (key == null || value == null) {  
            throw new NullPointerException("key == null || value == null");  
        }  
        V previous;  
        synchronized (this) {  
            putCount++;  
            size += safeSizeOf(key, value);  
            previous = map.put(key, value);  
            if (previous != null) { //返回的先前的value值  
                size -= safeSizeOf(key, previous);  
            }  
        }  
        if (previous != null) {  
            entryRemoved(false, key, previous, value);  
        }  
        trimToSize(maxSize);  
        return previous;  
    }  
    /**  
     * @param maxSize the maximum size of the cache before returning. May be -1  
     *     to evict even 0-sized elements.  
     *  清空cache空间  
     */  
    private void trimToSize(int maxSize) {  
        while (true) {  
            K key;  
            V value;  
            synchronized (this) {  
                if (size < 0 || (map.isEmpty() && size != 0)) {  
                    throw new IllegalStateException(getClass().getName() + ".sizeOf() is reporting inconsistent results!");  
                }  
                if (size <= maxSize) {  
                    break;  
                }  
                Map.Entry<K, V> toEvict = map.eldest();  
                if (toEvict == null) {  
                    break;  
                }  
                key = toEvict.getKey();  
                value = toEvict.getValue();  
                map.remove(key);  
                size -= safeSizeOf(key, value);  
                evictionCount++;  
            }  
            entryRemoved(true, key, value, null);  
        }  
    }  
    /**  
     * Removes the entry for {@code key} if it exists.  
     * 删除key相应的cache项，返回相应的value  
     * @return the previous value mapped by {@code key}.  
     */  
    public final V remove(K key) {  
        if (key == null) {  
            throw new NullPointerException("key == null");  
        }  
        V previous;  
        synchronized (this) {  
            previous = map.remove(key);  
            if (previous != null) {  
                size -= safeSizeOf(key, previous);  
            }  
        }  
        if (previous != null) {  
            entryRemoved(false, key, previous, null);  
        }  
        return previous;  
    }  
    /**  
     * Called for entries that have been evicted or removed. This method is  
     * invoked when a value is evicted to make space, removed by a call to  
     * {@link #remove}, or replaced by a call to {@link #put}. The default  
     * implementation does nothing.  
     * 当item被回收或者删掉时调用。改方法当value被回收释放存储空间时被remove调用，  
     * 或者替换item值时put调用，默认实现什么都没做。  
     * <p>The method is called without synchronization: other threads may  
     * access the cache while this method is executing.  
     *  
     * @param evicted true if the entry is being removed to make space, false  
     *     if the removal was caused by a {@link #put} or {@link #remove}.  
     * true---为释放空间被删除；false---put或remove导致  
     * @param newValue the new value for {@code key}, if it exists. If non-null,  
     *     this removal was caused by a {@link #put}. Otherwise it was caused by  
     *     an eviction or a {@link #remove}.  
     */  
    protected void entryRemoved(boolean evicted, K key, V oldValue, V newValue) {  
    }  
    /**  
     * Called after a cache miss to compute a value for the corresponding key.  
     * Returns the computed value or null if no value can be computed. The  
     * default implementation returns null.  
     * 当某Item丢失时会调用到，返回计算的相应的value或者null  
     * <p>The method is called without synchronization: other threads may  
     * access the cache while this method is executing.  
     *  
     * <p>If a value for {@code key} exists in the cache when this method  
     * returns, the created value will be released with {@link #entryRemoved}  
     * and discarded. This can occur when multiple threads request the same key  
     * at the same time (causing multiple values to be created), or when one  
     * thread calls {@link #put} while another is creating a value for the same  
     * key.  
     */  
    protected V create(K key) {  
        return null;  
    }  
    private int safeSizeOf(K key, V value) {  
        int result = sizeOf(key, value);  
        if (result < 0) {  
            throw new IllegalStateException("Negative size: " + key + "=" + value);  
        }  
        return result;  
    }  
    /**  
     * Returns the size of the entry for {@code key} and {@code value} in  
     * user-defined units.  The default implementation returns 1 so that size  
     * is the number of entries and max size is the maximum number of entries.  
     * 返回用户定义的item的大小，默认返回1代表item的数量，最大size就是最大item值  
     * <p>An entry's size must not change while it is in the cache.  
     */  
    protected int sizeOf(K key, V value) {  
        return 1;  
    }  
    /**  
     * Clear the cache, calling {@link #entryRemoved} on each removed entry.  
     * 清空cacke  
     */  
    public final void evictAll() {  
        trimToSize(-1); // -1 will evict 0-sized elements  
    }  
    /**  
     * For caches that do not override {@link #sizeOf}, this returns the number  
     * of entries in the cache. For all other caches, this returns the sum of  
     * the sizes of the entries in this cache.  
     */  
    public synchronized final int size() {  
        return size;  
    }  
    /**  
     * For caches that do not override {@link #sizeOf}, this returns the maximum  
     * number of entries in the cache. For all other caches, this returns the  
     * maximum sum of the sizes of the entries in this cache.  
     */  
    public synchronized final int maxSize() {  
        return maxSize;  
    }  
    /**  
     * Returns the number of times {@link #get} returned a value that was  
     * already present in the cache.  
     */  
    public synchronized final int hitCount() {  
        return hitCount;  
    }  
    /**  
     * Returns the number of times {@link #get} returned null or required a new  
     * value to be created.  
     */  
    public synchronized final int missCount() {  
        return missCount;  
    }  
    /**  
     * Returns the number of times {@link #create(Object)} returned a value.  
     */  
    public synchronized final int createCount() {  
        return createCount;  
    }  
    /**  
     * Returns the number of times {@link #put} was called.  
     */  
    public synchronized final int putCount() {  
        return putCount;  
    }  
    /**  
     * Returns the number of values that have been evicted.  
     * 返回被回收的数量  
     */  
    public synchronized final int evictionCount() {  
        return evictionCount;  
    }  
    /**  
     * Returns a copy of the current contents of the cache, ordered from least  
     * recently accessed to most recently accessed. 返回当前cache的副本，从最近最少访问到最多访问  
     */  
    public synchronized final Map<K, V> snapshot() {  
        return new LinkedHashMap<K, V>(map);  
    }  
    @Override  
    public synchronized final String toString() {  
        int accesses = hitCount + missCount;  
        int hitPercent = accesses != 0 ? (100 * hitCount / accesses) : 0;  
        return String.format("LruCache[maxSize=%d,hits=%d,misses=%d,hitRate=%d%%]", maxSize, hitCount, missCount, hitPercent);  
    }  
}  
```  
  
2018-12-20  
