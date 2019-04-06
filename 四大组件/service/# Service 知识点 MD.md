
	- [Service 启动 Activity](#Service-启动-Activity)
	- [Service 启动 Dialog](#Service-启动-Dialog)
## Service 启动 Activity  
在 Service 或 BroadcastReceiver 中启动 Activity 必须添加 FLAG_ACTIVITY_NEW_TASK ，否则他  
  
```java  
Intent intent = new Intent(context, YourActivity.class);     
intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);   //必须添加，注意不要用setFlags  
context.startActivity(dialogIntent);  
```  
  
## Service 启动 Dialog  
由于 Dialog 是依赖于 Activity 存在的，所以对于从 Service 启动 Dialog 主要有一下几种方法：  
使用 Activity 来仿写一个 Dialog(或先启动一个透明的Activity，然后在Activity里启动Dialog)  
使用 WindowManager 实现。  
  
  
  
  
  
