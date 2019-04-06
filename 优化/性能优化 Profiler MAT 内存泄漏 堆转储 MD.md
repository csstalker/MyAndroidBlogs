| Markdown版本笔记 | 我的GitHub首页 | 我的博客 | 我的微信 | 我的邮箱 |  
| :------------: | :------------: | :------------: | :------------: | :------------: |  
| [MyAndroidBlogs][Markdown] | [baiqiantao][GitHub] | [baiqiantao][博客] | bqt20094 | baiqiantao@sina.com |  
  
[Markdown]:https://github.com/baiqiantao/MyAndroidBlogs  
[GitHub]:https://github.com/baiqiantao  
[博客]:http://www.cnblogs.com/baiqiantao/  
  
  
***  
目录  
===  

- [内存泄漏检测实践](#内存泄漏检测实践)
- [翻译：使用 Memory Profiler 查看 Java 堆和内存分配](#翻译：使用-Memory-Profiler-查看-Java-堆和内存分配)
	- [为什么应分析您的应用内存](#为什么应分析您的应用内存)
	- [Memory Profiler 概览](#Memory-Profiler-概览)
	- [如何计算内存](#如何计算内存)
	- [查看内存分配](#查看内存分配)
	- [在分析时提高应用程序性能](#在分析时提高应用程序性能)
	- [查看全局JNI引用](#查看全局JNI引用)
	- [捕获堆转储【重要】](#捕获堆转储【重要】)
	- [将堆转储另存为 HPROF](#将堆转储另存为-HPROF)
	- [分析内存的技巧](#分析内存的技巧)
- [MAT 工具详解](#MAT-工具详解)
	- [获取及打开 .hprof 文件](#获取及打开-hprof-文件)
	- [工具栏](#工具栏)
	- [主界面 Overview](#主界面-Overview)
	- [default_report 窗口](#default_report-窗口)
	- [两个重要概念](#两个重要概念)
		- [Shallow heap：本身占用内存](#Shallow-heap：本身占用内存)
		- [Retained Heap：引用占用内存](#Retained-Heap：引用占用内存)
	- [Histogram 和 Dominator Tree](#Histogram-和-Dominator-Tree)
	- [右键菜单：Query Browser](#右键菜单：Query-Browser)
  
# 内存泄漏检测实践  
  
1、给项目中集成`LeakCanary`进行内存泄漏检测，发现有内存泄漏后该工具会进行提示。  
  
2、有内存泄露后我们使用AS的`profiler`工具进行分析并获取到`.hprof`文件：  
- 打开 profiler 工具，点击进入 MEMORY 区域  
- 为了模拟泄漏场景，我们对有内存泄漏的页面进行反复操作，然后点击图中的垃圾桶进行`强制gc`，再点击垃圾桶旁边的按钮`获取堆栈信息`  
- 当堆栈信息获取完成后，会在下面弹出一个列表框，我们将排序方式选为`Arrange by package`，这样好定位我们自己的代码。找到我们的代码后我们真的发现，应该已经被回收了的 Activity 还占用这内存，为什么它没有被销毁，还有那些对象引用着它呢？这时我们就需要点击保存按钮，导出`.hprof`文件进行具体分析了  
  
3、导出以后我们会得到一个`.hprof`文件，但是这个不是 mat 工具用到的标准文件，我们需要使用 sdk 自带的`platform-tools/hprof-conv.exe`工具进行转换，命令：`hprof-conv -z 1.hprof 1_mat.hprof`  
  
4、下来我们就需要使用 mat 进行分析了  
- 打开以后我们点击 Histogram  
- 进入 Histogram 页面，选择`Group By package`以定位到我们自己的包，然后在搜索框输入我们想要找的类，然后右键选择 `merge shortest paths to Gc roots` -> `exclude all phantom/weak/soft etc.references`选项  
- 然后就得到了这个类的引用树，从树中我们分析出我们的 Activity 是被谁引用了。  
  
![](index_files/5f43491d-b8eb-4b6a-938d-73381207c803.png)  
  
可以看到我们的 Activity 是被一个叫 imageView 的对象引用导致无法被释放的，实际上是因为我们的这个 imageView 是静态导致的：  
```java  
static ImageView imageView; //静态View导致Activity内存泄漏  
```  
  
# 翻译：使用 Memory Profiler 查看 Java 堆和内存分配  
[官方文档原文地址](https://developer.android.google.cn/studio/profile/memory-profiler)  
  
Memory Profiler 是 [Android Profiler](https://developer.android.google.cn/studio/preview/features/android-profiler.html) 中的一个组件，可帮助您识别导致应用卡顿、冻结甚至崩溃的内存泄漏和流失[memory leaks and memory churn]。它显示一个应用内存使用量的实时图表[It shows a realtime graph of your app's memory use]，让您可以捕获堆转储[capture a heap dump]、强制执行垃圾回收[force garbage collections]以及跟踪内存分配[track memory allocations]。  
  
要打开 Memory Profiler，请按以下步骤操作：  
  
1.  点击 **View > Tool Windows > Android Profiler**（也可以点击工具栏中的 **Android Profiler** ![](https://developer.android.google.cn/studio/images/buttons/toolbar-android-profiler.png)）。  
2.  从 Android Profiler 工具栏中选择您想要分析的`设备和应用进程`。  
3.  点击 **MEMORY **时间线中的任意位置可打开 Memory Profiler。  
  
或者，您可以在命令行中使用 [dumpsys](https://developer.android.google.cn/studio/command-line/dumpsys.html) 检查您的应用内存，同时 [查看 logcat 中的 GC Event](https://developer.android.google.cn/studio/debug/am-logcat.html#memory-logs)。  
  
## 为什么应分析您的应用内存  
  
Android 提供一个 [托管内存环境(managed memory environment)](https://developer.android.google.cn/topic/performance/memory-overview.html) —当它确定您的应用不再使用某些对象时，垃圾回收器会将未使用的内存释放回堆中。 虽然 Android 查找未使用内存的方式在不断改进，但对于所有 Android 版本，**系统都必须在某个时间点短暂地暂停您的代码**。 大多数情况下，这些暂停难以察觉。 不过，如果您的应用分配内存的速度比系统回收内存的速度快，则当收集器释放足够的内存以满足您的分配需要时，您的应用可能会延迟。 此延迟可能会导致您的应用跳帧[skip frames]，并使系统明显变慢。  
  
尽管您的应用不会表现出变慢，但如果存在内存泄漏，则即使应用在后台运行也会保留该内存。 此行为会强制执行不必要的垃圾回收 Event，因而拖慢系统的内存性能。 最后，系统被迫终止您的应用进程以回收内存。 然后，当用户返回您的应用时，它必须完全重启。  
  
为帮助防止这些问题，您应使用 Memory Profiler 执行以下操作：  
  
*   在时间线[timeline]中查找可能会导致性能问题的不理想的内存分配模式[undesirable memory allocation patterns]。  
*   转储 Java 堆以查看在任何给定时间哪些对象耗尽了使用内存。 **长时间**进行多个堆转储可帮助识别内存泄漏。  
*   记录正常用户交互和极端用户交互期间的内存分配以准确识别您的代码在何处短时间分配了过多对象，或分配了泄漏的对象[allocating objects that become leaked]。  
  
如需了解可减少应用内存使用的编程做法，请阅读 [管理您的应用内存](https://developer.android.google.cn/topic/performance/memory.html)。  
  
## Memory Profiler 概览  
  
当您首次打开 Memory Profiler 时，您将看到一条表示应用内存使用量的详细时间线，并可访问用于强制执行垃圾回收、捕捉堆转储和记录内存分配的各种工具。  
  
![](https://developer.android.google.cn/studio/images/profile/memory-profiler-callouts_2x.png)  
  
**图 1.** Memory Profiler  
  
如图 1 所示，Memory Profiler 的默认视图包括以下各项：  
  
1.  用于强制执行垃圾回收 Event 的按钮。  
2.  [用于捕获堆转储的按钮](https://developer.android.google.cn/studio/profile/memory-profiler#capture-heap-dump)。  
3.  [用于记录内存分配情况的按钮](https://developer.android.google.cn/studio/profile/memory-profiler#record-allocations)。 此按钮仅在连接至运行 `Android 7.1` 或更低版本的设备时才会显示。  
4.  用于放大/缩小时间线的按钮。  
5.  用于跳转至实时内存数据的按钮。  
6.  Event 时间线，其显示 Activity 状态、用户输入 Event 和屏幕旋转 Event。  
7.  内存使用量时间线，其包含以下内容：  
    *   一个显示每个内存类别使用多少内存的堆叠图表[stacked graph]，如左侧的 y 轴以及顶部的彩色键所示。  
    *   虚线表示`分配的对象数`，如右侧的 y 轴所示。  
    *   用于表示每个垃圾回收 Event 的图标。  
  
不过，如果您使用的是运行 Android 7.1 或更低版本的设备，则默认情况下，并不是所有分析数据均可见。 如果您看到一条消息，其显示“Advanced profiling is unavailable for the selected process”，则需要 [启用高级分析](https://developer.android.google.cn/studio/preview/features/android-profiler.html#advanced-profiling) 以查看下列内容：  
  
*   Event 时间线  
*   分配的对象数  
*   垃圾回收 Event  
  
在 Android 8.0 及更高版本上，始终为可调试应用启用高级分析。  
  
## 如何计算内存  
  
您在 Memory Profiler（图 2）`顶部`看到的数字取决于您的应用根据 Android 系统机制所提交的所有私有内存页面数[private memory pages]。 此计数不包含与系统或其他应用共享的页面。  
  
![](https://developer.android.google.cn/studio/images/profile/memory-profiler-counts_2x.png)  
![](index_files/a70fdd57-bf16-4b09-b404-52e2f5530178.png)  
  
**图 2.** Memory Profiler 顶部的内存计数图例  
  
内存计数中的类别如下所示：  
  
*   **Java**：从 Java 或 Kotlin 代码分配的对象内存。  
*   **Native**：从 C 或 C++ 代码分配的对象内存。 即使您的应用中不使用 C++，您也可能会看到此处使用的一些原生内存，因为 Android 框架使用原生内存代表您处理各种任务[handle various tasks on your behalf]，如处理图像资源和其他图形时，即使您编写的代码采用 Java 或 Kotlin 语言。  
*   **Graphics**：图形缓冲区队列向屏幕显示像素（包括 GL 表面、GL 纹理等等）所使用的内存。 （请注意，这是与 CPU 共享的内存，不是 GPU 专用内存。）  
*   **Stack**： 您的应用中的原生堆栈和 Java 堆栈使用的内存。 这通常与您的应用运行多少线程有关。  
*   **Code**：您的应用用于处理代码和资源（如 dex 字节码、已优化或已编译的 dex 码、.so 库和字体）的内存。  
*   **Other**：您的应用使用的系统不确定如何分类的内存。  
*   **Allocated**：您的应用分配的 Java/Kotlin 对象数。 它没有计入 C 或 C++ 中分配的对象。  
> 当连接至运行 Android 7.1 及更低版本的设备时，此分配仅在 Memory Profiler 连接至您运行的应用时才开始计数。 因此，您开始分析之前分配的任何对象都不会被计入。 不过，Android 8.0 附带一个设备内置分析工具，该工具可记录所有分配，因此，在 Android 8.0 及更高版本上，此数字始终表示您的应用中待处理的 Java 对象总数。  
  
与以前的 Android Monitor 工具中的内存计数相比，新的 Memory Profiler 以不同的方式记录您的内存，因此，您的内存使用量现在看上去可能会更高些。 Memory Profiler 监控的类别更多，这会增加总的内存使用量，但如果您仅关心 `Java 堆内存`，则“Java”项的数字应与以前工具中的数值相似。  
  
然而，Java 数字可能与您在 Android Monitor 中看到的数字并非完全相同，这是因为应用的 Java 堆是从 `Zygote` 启动的，而新数字则计入了为它分配的所有物理内存页面。 因此，它可以准确反映您的应用实际使用了多少物理内存。  
  
> 注：目前，Memory Profiler 还会显示应用中的一些误报的原生内存使用量，而这些内存实际上是分析工具使用的。 对于大约 100000 个对象，最多会使报告的内存使用量增加 10MB。 在这些工具的未来版本中，这些数字将从您的数据中过滤掉。  
  
## 查看内存分配  
  
内存分配显示内存中每个对象是_如何_分配的。 具体而言，Memory Profiler 可为您显示有关对象分配的以下信息：  
  
*   分配哪些`类型`的对象以及它们使用`多少`空间。  
*   每个分配的堆叠追踪[stack trace]，包括在哪个`线程`中。  
*   对象在何时_被取消分配_（仅当使用运行 Android 8.0 或更高版本的设备时）。  
  
如果您的设备运行 Android 8.0 或更高版本，您可以随时按照下述方法查看您的对象分配： 只需点击并按住时间线，并`拖动`选择您想要查看分配的区域（如视频 1 中所示）。 不需要开始记录会话，因为 Android 8.0 及更高版本附带设备内置分析工具，可持续跟踪您的应用分配。  
  
https://storage.googleapis.com/androiddevelopers/videos/studio/memory-profiler-allocations-jvmti.mp4  
**视频 1.** 对于Android 8.0 及更高版本，选择一个现有时间线区域以查看对象分配  
  
如果您的设备运行 Android 7.1 或更低版本，则在 Memory Profiler 工具栏中点击 **Record memory allocations** ![](https://developer.android.google.cn/studio/images/buttons/profiler-record.png)。 记录时，Android Monitor 将跟踪您的应用中进行的所有分配。 操作完成后，点击 **Stop recording** ![](https://developer.android.google.cn/studio/images/buttons/profiler-record-stop.png)（同一个按钮；请参阅视频 2）以查看分配。  
  
https://storage.googleapis.com/androiddevelopers/videos/studio/memory-profiler-allocations-record.mp4  
**视频 2.** 对于 Android 7.1 及更低版本，您必须显式记录内存分配  
  
在选择一个时间线区域后（或当您使用运行 Android 7.1 或更低版本的设备完成记录会话时），已分配对象的列表将显示在时间线下方，按`类名称[class name]`进行分组，并按其`堆计数[heap count]`排序。  
  
> 注：在 Android 7.1 及更低版本上，您最多可以记录 65535 个分配。 如果您的记录会话超出此限值，则记录中仅保存最新的 65535 个分配。 （在 Android 8.0 及更高版本中，则没有实际的限制。）  
  
要检查分配记录，请按以下步骤操作：  
  
1.  浏览列表以查找堆计数**异常大**且可能存在泄漏的对象。 为帮助查找已知类，点击 **Class Name** 列标题以按字母顺序排序。 然后点击一个类名称。 此时在右侧将出现 **Instance View** 窗格，显示`该类的每个实例`，如图 3 中所示。  
2.  在 **Instance View** 窗格中，点击一个实例。 此时下方将出现 **Call Stack** 标签，显示该实例被分配到何处以及哪个线程中。  
3.  在 **Call Stack** 标签中，点击任意行以在编辑器中跳转到该代码。  
  
![](https://developer.android.google.cn/studio/images/profile/memory-profiler-allocations-detail_2x.png)  
  
**图 3.** 有关每个已分配对象的详情显示在右侧的 **Instance View** 中。  
  
默认情况下，左侧的分配列表按类名称排列。 在列表顶部，您可以使用右侧的下拉列表在以下排列方式之间进行切换：  
  
*   **Arrange by class**：基于类名称对所有分配进行分组。  
*   **Arrange by package**：基于软件包名称对所有分配进行分组。  
*   **Arrange by callstack**：将所有分配分组到其对应的调用堆栈[Groups all allocations into their corresponding call stack]。  
  
## 在分析时提高应用程序性能  
  
为了在分析时提高应用程序性能，内存分析器默认情况下会定期对内存分配进行采样[samples ]。 在运行API级别26或更高级别的设备上进行测试时，可以使用“Allocation Tracking”下拉列表更改此行为。  
  
![](index_files/782885b5-3e22-4015-92db-5e77f1d69efa.png)  
  
可用选项如下：  
- Full：捕获内存中的所有对象分配。 这是Android Studio 3.2及更早版本中的默认行为。 如果您有一个分配了大量对象的应用程序，您可能会在分析时观察到应用程序的可见速度下降[observe visible slowdowns]。  
- Sampled：定期在内存中采样对象分配。 这是默认选项，在分析时对应用程序性能的影响较小。 在很短的时间内分配大量对象的应用程序仍然可能会出现明显的减速。  
- Off：停止跟踪应用的内存分配。  
  
> 注意：默认情况下，Android Studio会在执行CPU录制时停止跟踪实时分配，并在CPU录制完成后重新打开。 您可以在CPU录制配置对话框中更改此行为。  
  
## 查看全局JNI引用  
  
Java Native Interface（JNI）是一个允许Java代码和 native code 相互调用的框架。  
  
JNI引用由 native code 手动管理，因此 native code 使用的Java对象可能会保持活动太长时间。如果在没有先明确删除[first  being explicitly deleted]的情况下丢弃JNI引用，Java堆上的某些对象可能无法访问。此外，可能耗尽[exhaust]全局JNI引用限制。  
  
要解决此类问题，请使用Memory Profiler中的 JNI heap 视图浏览所有全局JNI引用，并按Java类型和本机调用堆栈对其进行过滤。通过此信息，您可以找到创建和删除全局JNI引用的时间和位置。  
  
在您的应用程序运行时，选择要检查的时间轴的一部分，然后从 class list 上方的下拉菜单中选择JNI堆。然后，您可以像往常一样检查堆中的对象，然后双击 Allocation Call Stack 选项卡中的对象，以查看在代码中分配和释放JNI引用的位置，如图4所示。  
  
![](https://developer.android.google.cn/studio/images/memory-profiler-jni-heap_2x.png)  
  
要检查应用程序的JNI代码的内存分配，您必须将应用程序部署到运行Android 8.0或更高版本的设备。  
  
有关JNI的更多信息，请参阅 [JNI tips](https://developer.android.google.cn/training/articles/perf-jni)。  
  
## 捕获堆转储【重要】  
  
堆转储显示在您捕获堆转储时您的应用中`哪些对象正在使用内存`。 特别是在长时间的用户会话后，堆转储会显示您认为不应再位于内存中却仍在内存中的对象，从而帮助识别内存泄漏。 在捕获堆转储后，您可以查看以下信息：  
  
*   您的应用已分配哪些类型的对象，以及每个类型分配多少。  
*   每个对象正在使用多少内存。  
*   在代码中的何处仍在引用每个对象。  
*   对象所分配到的调用堆栈。（目前，如果您在记录分配时捕获堆转储，则只有在 Android 7.1 及更低版本中，堆转储才能使用调用堆栈。）  
  
![](https://developer.android.google.cn/studio/images/profile/memory-profiler-dump_2x.png)  
  
**图 4.** 查看堆转储  
  
要捕获堆转储，在 Memory Profiler 工具栏中点击 **Dump Java heap** ![](https://developer.android.google.cn/studio/images/buttons/profiler-heap-dump.png) 。  
  
> 在转储堆期间，Java 内存量可能会暂时增加。 这很正常，因为堆转储与您的应用发生在同一进程中，并需要一些内存来收集数据。  
  
堆转储显示在内存时间线下，显示堆中的所有类类型，如图 5 所示。  
  
> 注：如果您需要更精确地了解转储的创建时间，可以通过调用 [dumpHprofData()](https://developer.android.google.cn/reference/android/os/Debug.html#dumpHprofData(java.lang.String) 在应用代码的关键点创建堆转储。  
  
在类列表中，您可以查看以下信息：  
*   **Allocations**: 堆中分配数  
*   **Native Size**: 此对象类型使用的native内存总量。 此列仅适用于Android 7.0及更高版本。您将在这里看到一些用Java分配内存的对象，因为Android使用native内存来处理某些框架类，例如Bitmap。  
*   **Shallow Size**: 此对象类型使用的Java内存总量  
*   **Retained Size**: 因此类的所有实例而保留的内存总大小  
  
您可以使用已分配对象列表上方的两个菜单来选择要检查的堆转储以及如何组织数据。  
  
从左侧的菜单中，选择要检查的堆：  
*   **Default heap**：系统未指定堆时。  
*   **App heap**：您的应用在其中分配内存的主堆[primary heap]。  
*   **Image heap**：系统启动映像[system boot image]，包含启动期间预加载[preloaded]的类。 此处的分配保证绝不会移动或消失。  
*   **Zygote heap**：copy-on-write heap，其中的应用进程是从 Android 系统中派生[forked]的。  
  
从右侧菜单中选择如何排列分配：  
*   **Arrange by class**：基于类名称对所有分配进行分组。  
*   **Arrange by package**：基于软件包名称对所有分配进行分组。  
*   **Arrange by callstack**：将所有分配分组到其对应的调用堆栈。此选项仅在记录分配[recording allocations]期间捕获堆转储[capture the heap dump]时才有效。即使如此，堆中的对象也很可能是在您开始记录之前分配的，因此这些分配会首先显示，且只按类名称列出。  
  
默认情况下，此列表按 **Retained Size** 列排序。 您可以点击任意列标题以更改列表的排序方式。  
  
在 **Instance View** 中，每个实例都包含以下信息：  
*   **Depth**：从任意 GC root 到所选实例的最短 hops 数。  
*   **Native Size**: native内存中此实例的大小。此列仅适用于Android 7.0及更高版本。  
*   **Shallow Size**：此实例Java内存的大小。  
*   **Retained Size**：此实例支配[dominator]的内存大小（根据 [dominator 树](https://en.wikipedia.org/wiki/Dominator_(graph_theory)）。  
  
> 默认情况下，堆转储_不会_向您显示每个已分配对象的堆叠追踪。 要获取堆叠追踪，在点击 **Dump Java heap** 之前，您必须先开始 [记录内存分配](https://developer.android.google.cn/studio/profile/memory-profiler#record-allocations)。 然后，您可以在 **Instance View** 中选择一个实例，并查看 **Call Stack** 标签以及 **References** 标签，如图 5 所示。不过，在您开始记录分配之前，可能已分配一些对象，因此，调用堆栈不能用于这些对象。 包含调用堆栈的实例在图标 ![](https://developer.android.google.cn/studio/images/profile/memory-profiler-icon-stack.png) 上用一个“堆栈”标志表示。（遗憾的是，由于堆叠追踪需要您执行分配记录，因此，您目前无法在 Android 8.0 上查看堆转储的堆叠追踪。）  
  
![](https://developer.android.google.cn/studio/images/profile/memory-profiler-dump-stacktrace_2x.png)  
  
**图 5.** 捕获堆转储需要的持续时间标示在时间线中  
  
要检查您的堆，请按以下步骤操作：  
  
1、浏览列表以查找堆计数[heap counts]**异常大**且可能存在泄漏的对象。 为帮助查找已知类，点击 **Class Name** 列标题以按字母顺序排序。 然后点击一个类名称。 此时在右侧将出现 **Instance View** 窗格，显示该类的每个实例，如图 5 中所示。  
  
> 或者，您可以通过单击 Filter  或按 Control + F 并在搜索字段中输入`类名或包名`来快速定位对象。 也可以从下拉菜单中选择 Arrange by callstack 来按方法名称搜索。如果要使用正则表达式，请选中Regex旁边的框。如果您的搜索查询区分大小写，请选中匹配大小写旁边的框。  
  
2、在 **Instance View** 窗格中，点击一个实例。此时下方将出现 **References**，显示该对象的每个引用。或者，点击实例名称旁的`箭头`以查看其所有`字段`，然后点击一个字段名称查看其所有`引用`。 如果您要查看某个字段的实例详情，右键点击该字段并选择 **Go to Instance**。  
  
3、在 **References** 标签中，如果您发现某个引用可能在泄漏内存，则右键点击它并选择 **Go to Instance**。 这将从堆转储中选择对应的实例，显示您自己的实例数据。  
  
在您的堆转储中，请注意由下列任意情况引起的内存泄漏：  
  
*   长时间引用 `Activity`、`Context`、`View`、`Drawable` 和其他对象，可能会保持对 `Activity` 或 `Context`容器的引用。  
*   可以保持 `Activity` 实例的非静态内部类，如 `Runnable`。  
*   对象保持时间超出所需时间的缓存。  
  
## 将堆转储另存为 HPROF  
  
在捕获堆转储后，仅当分析器运行时才能在 Memory Profiler 中查看数据。 当您退出分析会话时，您将丢失堆转储。 因此，如果您要保存堆转储以供日后查看，可通过点击时间线下方工具栏中的 **Export heap dump as HPROF file**![](https://developer.android.google.cn/studio/images/buttons/profiler-export-hprof.png)，将堆转储导出到一个 HPROF 文件中。 在显示的对话框中，确保使用 `.hprof` 后缀保存文件。  
  
然后，通过将此文件拖到一个空的编辑器窗口（或将其拖到文件标签栏中），您可以在 Android Studio 中重新打开该文件。  
  
要使用其他 HPROF 分析器（如 [jhat](https://docs.oracle.com/javase/8/docs/technotes/tools/unix/jhat.html)），您需要将 HPROF 文件从 Android 格式转换为 `Java SE HPROF` 格式。 您可以使用 `android_sdk/platform-tools/` 目录中提供的 `hprof-conv` 工具执行此操作。 运行包括以下两个参数的 `hprof-conv` 命令：原始 HPROF 文件和转换后 HPROF 文件的写入位置。 例如：  
```  
hprof-conv heap-original.hprof heap-converted.hprof  
```  
  
**导入堆转储文件**  
要导入HPROF（.hprof）文件，请单击 Sessions 窗格中 Load from file，然后从文件浏览器中选择该文件。  
  
![](index_files/8cd6c243-a8f2-4a58-b9c2-d840a1888a49.png)  
  
您还可以通过将 HPROF 文件从文件浏览器拖到编辑器窗口中来导入HPROF文件。  
  
## 分析内存的技巧  
  
使用 Memory Profiler 时，您应对应用代码施加压力[stress your app code]并尝试`强制内存泄漏`。在应用中引发内存泄漏的一种方式是，先让其运行一段时间，然后再检查堆。泄漏在堆中可能逐渐汇聚到分配顶部[Leaks might trickle up to the top of the allocations in the heap]。不过，泄漏越小，您越需要运行更长时间才能看到泄漏。  
  
您还可以通过以下方式之一触发内存泄漏：  
  
*   将设备从纵向旋转为横向，然后在不同的 Activity 状态下反复操作多次。 旋转设备经常会导致应用泄漏 `Activity、Context、View` 对象，因为系统会重新创建 Activity，而如果您的应用在其他地方保持对这些对象之一的引用，系统将无法对其进行垃圾回收。  
*   处于不同的 Activity 状态时，在您的应用与另一个应用之间切换（导航到主屏幕，然后返回到您的应用）。  
  
> 提示：您还可以使 用monkeyrunner 测试框架执行上述步骤。  
  
# MAT 工具详解  
[参考1](https://www.androidperformance.com/2015/04/11/AndroidMemory-Usage-Of-MAT/)  
[参考2](https://www.androidperformance.com/2015/04/11/AndroidMemory-Usage-Of-MAT-Pro/)  
  
MAT，`Memory Analyzer Tool`，一个基于Eclipse的免费内存分析工具，是一个快速、功能丰富的 JAVA heap 分析工具，它可以帮助我们查找`内存泄漏`和查看`内存消耗`情况。使用内存分析工具从众多的对象中进行分析，快速的计算出在内存中对象的占用大小，看看是谁阻止了垃圾收集器的回收工作，并可以通过`报表`直观的查看到可能造成这种结果的对象。  
  
当然 MAT 也有独立的不依赖 Eclipse 的版本，只不过这个版本在调试 Android 内存的时候，需要将 DDMS 生成的文件进行转换，才可以在独立版本的 MAT 上打开。不过 Android SDK 中已经提供了这个工具，所以使用起来也是很方便的。  
  
MAT工具提供了三种选择的方式：  
- Update Site：在线安装Eclipse插件的方式  
- Archived Update Site：离线安装Eclipse插件的方式  
- Stand-alone Eclipse RCP Applications：独立安装方，[1.8.0 独立安装版下载地址](http://mirror.rise.ph/eclipse//mat/1.8/rcp/MemoryAnalyzer-1.8.0.20180604-win32.win32.x86_64.zip)  
  
## 获取及打开 .hprof 文件  
  
使用MAT既可以打开一个已有的堆快照，也可以通过MAT直接从活动Java程序中导出堆快照。  
  
HPROF文件是MAT能识别的文件，HPROF文件存储的是特定时间点，java进程的内存快照。有不同的格式来存储这些数据，总的来说包含了快照被触发时java对象和类在heap中的情况。由于快照只是一瞬间的事情，所以heap dump中无法包含一个对象在何时、何地（哪个方法中）被分配这样的信息。  
  
这个文件可以使用DDMS导出：DDMS中在Devices上面有一排按钮，选择一个进程后，点击Dump HPROF file 按钮，选择存储路径保存后就可以得到对应进程的HPROF文件。eclipse插件可以把上面的工作一键完成。只需要点击Dump HPROF file图标，然后MAT插件就会自动转换格式，并且在eclipse中打开分析结果。  
  
> 在使用使用Eclipse或者AndroidStudio抓内存之前，一定要手动点击 Initiate GC 按钮手动触发GC，这样抓到的内存使用情况就是不包括Unreachable对象的。  
> Unreachable指的是可以被垃圾回收器回收的对象，但是由于没有GC发生，所以没有释放，这时抓的内存使用中的Unreachable就是这些对象。  
> 点击Calculate Retained Size之后，会出现Retained Size这一列，可以看到Unreachable Object的对象其Retained Heap值都为0。  
  
![](index_files/cd6f4665-81a3-4f53-ae1e-8a70099062ec.jpg)  
  
如果HPROF文件是通过AndroidStudio的profile工具导出的，由于这个不是 mat 工具用到的标准文件，我们需要使用 sdk 自带的`platform-tools/hprof-conv.exe`工具进行转换，命令为：  
```c  
hprof-conv -z 1.hprof 1_mat.hprof  
```  
  
转换过后的`.hprof`文件即可使用MAT工具打开了。  
  
打开一个`.hprof`文件时，首先会显示`Getting Start Wizard`向导弹框，默认会选择了第一个，确定后会生成一个报告。这个无大碍。  
  
> 注意：最好将`.hprof`文件放在一个单独的文件夹内打开，因为你在操作过程中，会生成大量的临时文件。  
  
## 工具栏  
  
![](index_files/ac0abff5-d507-4655-b083-235fdf0a596c.png)  
- Overview：主界面  
- Histogram：直方图   
- Dominator Tree：支配树   
- OQL：Object Query Language studio  
- Thread OvewView：查看这个应用所有的Thread信息  
  
![](index_files/57640910-dfab-4d3a-9c9a-b06785b5d2b3.png)  
- Run Expert System Test：运行专家系统测试  
- Query Browser：查询浏览器  
- Find Object By Address  
  
![](index_files/e66466dd-cc07-4a5e-83a7-9803073280b8.png)  
- Group：在Histogram和Domiantor Tree界面，可以选择将结果用另一种Group的方式显示(默认是Group by Object)，切换到Group by package可以更好地查看具体是哪个包里的类占用内存大，也很容易定位到自己的应用程序。  
  
![](index_files/4a9b7750-783b-4e8c-8e93-c24c667d4012.png)  
- Calculate Retained Size：点击后，会出现Retained Size这一列  
  
## 主界面 Overview  
  
我们需要关注的是下面Actions区域，介绍4种分析方法：  
- Histogram: Lists number of instances per class 列出内存中的对象，对象的个数以及大小  
- Dominator Tree: List the biggest objects and what they keep alive. 列出最大的对象以及其依赖存活的Object，大小是以Retained Heap为标准排序的  
- Top Consumers: Print the most expensive objects grouped by class and by package. 通过图形列出最大的object  
- Duplicate Classes: Detect classes loaded by multiple class loaders. 通过MAT自动分析泄漏的原因  
  
## default_report 窗口  
该窗口列出了可能有问题的代码片段。点击每个问题中的Details可以查看相关的详情。  
  
详情页面包含如下内容  
- Description：问题简要描述  
- Shortest Paths To the Accumulation Point：在此列表中，我们可以追溯到问题代码的类树的结构，并找到自己代码中的类。  
- Accumulated Objects in Dominator Tree：在此列表中，我们可以看见创建的大量的对象  
- Accumulated Objects by Class in Dominator Tree：在此列表中，我们能看见创建大量对象相关的类。   
- All Accumulated Objects by Class：在此列表中，会按类别划分的所有累计对象。  
  
## 两个重要概念  
### Shallow heap：本身占用内存  
  
Shallow size就是对象本身占用内存的大小，不包含其引用的对象。  
- 常规对象（非数组）的Shallow size由其成员变量的数量和类型决定  
- 数组类型的对象的shallow size由数组元素的类型（对象类型、基本类型）和数组长度决定  
  
> 注意：因为不像c++的对象本身可以存放大量内存，java的对象成员都是些引用。真正的内存都在堆上，看起来是一堆原生的`byte[]`、`char[]`、`int[]`，所以我们如果只看对象本身的内存，那么数量都很小。所以我们看到 Histogram 图是以Shallow size进行排序的，排在第一位的一般都是`byte[]`。  
  
![](index_files/5b3cba09-3008-4f80-89a3-067ac10c160a.png)  
  
### Retained Heap：引用占用内存  
  
Retained Heap的概念，它表示如果一个对象被释放掉，那么该对象引用的所有对象，包括被递归引用的对象，被释放的内存。  
  
例如，如果一个对象的某个成员new了一大块int数组，那这个int数组也可以计算到这个对象中。与shallow heap比较，Retained heap可以更精确的反映一个对象实际占用的大小，因为如果该对象释放，retained heap都可以被释放。  
  
**但是，Retained Heap并不总是那么有效。**  
例如，我在A里new了一块内存，赋值给A的一个成员变量，同时我让B也指向这块内存。此时，因为A和B都引用到这块内存，所以A释放时，该内存不会被释放。所以这块内存**不会**被计算到A或者B的Retained Heap中。  
  
为了纠正这点，MAT中的 Leading Object（例如A或者B）不一定只是一个对象，也可以是多个对象。此时，(A,B)这个组合的Retained Set就包含那块大内存了。对应到MAT的UI中，在Histogram中，可以选择 Group By class, superclass or package来选择这个组。  
  
---  
  
为了计算Retained Memory，MAT引入了Dominator Tree。  
  
例如，对象A引用B和C，B和C又都引用到D，计算Retained Memory时：  
- A的包括A本身和B，C，D。  
- B和C因为共同引用D，所以B,C 的Retained Memory都只是他们本身。  
- D当然也只是自己。  
  
在这里例子中，树根是A，而B，C，D是他的三个儿子，B，C，D不再有相互关系。  
  
我觉得是为了加快计算的速度，MAT将`对象引用图`转换成`对象引用树`。把引用图变成引用树后，计算Retained Heap就会非常方便，显示也非常方便。对应到 MAT UI 上，在 dominator tree 这个view中，显示了每个对象的 shallow heap 和 retained heap。然后可以以该节点为树根，一步步的细化看看 retained heap 到底是用在什么地方了。  
  
这种从图到树的转换确实方便了内存分析，但有时候会让人有些疑惑。本来对象B是对象A的一个成员，但因为B还被C引用，所以B在树中并不在A下面，而很可能是平级。  
  
为了纠正这点，MAT中点击右键，可以 List objects 中选择 with outgoing references 和 with incoming references。这是个真正的引用图的概念，  
- outgoing references ：表示该对象的出节点（被该对象引用的对象）  
- incoming references ：表示该对象的入节点（引用到该对象的对象）  
  
---  
  
为了更好地理解 Retained Heap，下面引用一个例子来说明：  
  
把内存中的对象看成下图中的节点，并且对象和对象之间互相引用。这里有一个特殊的节点GC Roots，这就是reference chain(引用链)的起点:   
  
![](https://img-blog.csdn.net/20151127104049842)  
  
上图中蓝色节点代表仅仅只有通过obj1才能直接或间接访问的对象。因为可以通过GC Roots访问，所以上图的obj3不是蓝色节点。因此上图中obj1的retained size是obj1、obj2、obj4的shallow size总和。  
  
![](https://img-blog.csdn.net/20151127104034762)  
上图obj1的retained size是obj1、obj2、obj3、obj4的shallow size总和。而obj2的retained size是obj3、obj4的shallow size总和。  
  
## Histogram 和 Dominator Tree  
  
Histogram的主要作用是查看一个instance的数量，一般用来查看自己创建的类的实例的个数。  
  
可以很容易的找出占用内存最多的几个对象，根据百分比(Percentage)来排序。  
  
可以分不同维度来查看对象的Dominator Tree视图，Group by class、Group by class loader、`Group by package`  
  
Dominator Tree和Histogram的区别是站的角度不一样，Histogram是站在`类`的角度上去看，Dominator Tree是站的`对象实例`的角度上看，Dominator Tree可以更方便的看出其引用关系。  
  
通过查看Object的个数，结合代码就可以找出存在内存泄露的类（即可达但是无用的对象，或者是可以重用但是重新创建的对象）  
  
Histogram中还可以对对象进行Group，更方便查看自己Package中的对象信息。  
  
## 右键菜单：Query Browser  
![](index_files/68fc5e55-fb12-4a17-83b8-9b57d4a0f8d1.png)  
  
| 菜单名称 | 子选项 | 描述 |  
| --- | --- | --- |  
| List objects | With Outgoing References | 显示选中对象持有哪些对象 |  
| | With Incoming References | 显示选中对象被哪些对象持有。如果一个类有很多不需要的实例，那么可以找到哪些对象持有该对象，让这个对象没法被回收。 |  
| Show objects by class | With Outgoing References | 显示选中对象持有哪些对象, 这些对象按类合并在一起排序 |  
| |  With Incoming References | 显示选中对象被哪些对象持有，这些对象按类合并在一起排序 |  
| Merge Shortest Paths to GC Roots. |  | 从GC Roots节点到该对象的最短引用路径 |  
| | With all references | 显示选中对象到GC根节点的引用路径，包括所有类型引用。 |  
| | Exclude weak/soft/phantom references | 排除了弱/软/虚引用 |  
  
![](index_files/d030c998-30d9-4651-a7bb-f7513ee53dc1.png)  
  
| 菜单名称 | 子选项 | 描述 |  
| --- | --- | --- |  
| Java Basics | References Statistics | 显示引用和对象的统计信息 |  
| |  Class Loader Explorer | 列出类加载器，包括定义的类 |  
| |  Customized Retained Set | 计算选中对象的保留堆，排除指定的引用 |  
| |  Find Strings |  |  
| |  Group By Value |  |  
| |  Open in Dominator Tree | 对选中对象生成支配树 |  
| |  Show as Histogram | 展示任意对象的直方图 |  
| |  Thread Details | 显示线程的详细信息和属性 |  
| |  Thread Overview and Stacks | - |  
  
![](index_files/d140950a-5185-47a3-a625-ce4e5efe92b2.png)  
  
| 菜单名称 | 子选项 | 描述 |  
| --- | --- | --- |  
| Java Collections | Array Fill Ratio | 输出给定数组中，非基本类型、非null对象个数占比 |  
| |  Arrays Grouped by Size | 显示数组的直方图，按大小分组 |  
| |  Collection Fill Ratio | 输出给定集合中，非基本类型、非 null 对象个数占比 |  
| |  Collections Grouped by Size | 显示集合的直方图，按大小分组 |  
| |  Hash Entries | 展开显示指定HashMap或Hashtable中的键值对 |  
| |  Map Collision Ratio | 输出指定的映射集合的碰撞率 |  
| |  Primitive Arrays With a Constant Value | 列出基本数据类型的数组，这些数组是由一个常数填充的 |  
| Leak Identification | Component Report | 展示组件报告，分析可能的内存浪费或者低效使用的组件 |  
| | Top Consumers | 输出内存浪费最大的那个组件 |  
| |  |  |  
  
![](index_files/26082153-0429-4665-bdd4-e82c3afdbea0.png)  
  
Search Queries：搜索列出所有Queries选项的具体含义，包含搜索区、输入关键字后匹配的Queries选项列表区，点击选项后的具体含义解释区。  
  
可以看到，所有的命令其实就是配置不同的SQL查询语句，比如我们最常用的：  
- `List objects -> with incoming references`：查看这个对象持有的外部对象引用  
- `List objects -> with outcoming references`：查看这个对象被哪些外部对象引用  
- `Path To GC Roots -> exclude all phantim/weak/soft etc. references`：查看这个对象的GC Root，不包含虚、弱引用、软引用，剩下的就是强引用。从GC上说，除了强引用外，其他的引用在JVM需要的情况下是都可以 被GC掉的，如果一个对象始终无法被GC，就是因为强引用的存在，从而导致在GC的过程中一直得不到回收，因此就内存溢出了。  
- `Merge Shortest path to GC root`：找到从GC根节点到一个对象或一组对象的共同路径  
  
2019-3-20  
