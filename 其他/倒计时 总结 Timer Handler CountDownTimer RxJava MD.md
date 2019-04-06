| Markdown版本笔记 | 我的GitHub首页 | 我的博客 | 我的微信 | 我的邮箱 |  
| :------------: | :------------: | :------------: | :------------: | :------------: |  
| [MyAndroidBlogs][Markdown] | [baiqiantao][GitHub] | [baiqiantao][博客] | bqt20094 | baiqiantao@sina.com |  
  
[Markdown]:https://github.com/baiqiantao/MyAndroidBlogs  
[GitHub]:https://github.com/baiqiantao  
[博客]:http://www.cnblogs.com/baiqiantao/  
  
倒计时 总结 Timer Handler RxJava  
***  
目录  
===  

- [利用系统API的几种实现方式](#利用系统API的几种实现方式)
	- [使用 CountDownTimer 实现 - 最简洁【推荐】](#使用-CountDownTimer-实现---最简洁【推荐】)
		- [CountDownTimer 简介](#CountDownTimer-简介)
		- [使用案例](#使用案例)
	- [使用 RxJava 实现 - 方便强大【推荐】](#使用-RxJava-实现---方便强大【推荐】)
	- [使用 Timer + Handler 实现 - 麻烦【不推荐】](#使用-Timer--Handler-实现---麻烦【不推荐】)
		- [Timer + 普通 Handler - 麻烦](#Timer--普通-Handler---麻烦)
		- [Timer + 静态 Handler - 更麻烦](#Timer--静态-Handler---更麻烦)
		- [Timer + runOnUiThread - 也麻烦](#Timer--runOnUiThread---也麻烦)
	- [使用纯 Handler 实现 - 特麻烦【强烈不建议】](#使用纯-Handler-实现---特麻烦【强烈不建议】)
- [开源框架 CountdownView 简介](#开源框架-CountdownView-简介)
- [在 RecyclerView 中实现倒计时](#在-RecyclerView-中实现倒计时)
	- [更改数据源方式 - 简单但不可靠](#更改数据源方式---简单但不可靠)
	- [让 System 帮我们倒计时 - 推荐](#让-System-帮我们倒计时---推荐)
	- [自己维护倒计时 - 既麻烦又低效](#自己维护倒计时---既麻烦又低效)
  
# 利用系统API的几种实现方式  
## 使用 CountDownTimer 实现 - 最简洁【推荐】  
### CountDownTimer 简介  
[文档](https://developer.android.google.cn/reference/android/os/CountDownTimer.html)  
  
> Schedule安排、清单 a countdown until a time in the future, with regular规律的 notifications on intervals间隔 along the way过程.   
  
在文本字段中显示一个30秒倒计时的示例：  
```java  
@BindView(R.id.send) Button send;//发送验证码  
new CountDownTimer(60000, 1000) {  
    @Override  
    public void onTick(long millisUntilFinished) {  
        send.setText(millisUntilFinished / 1000 + "S");  
    }  
    @Override  
    public void onFinish() {  
        send.setEnabled(true);  
        send.setText("重新发送");  
    }  
}.start();  
```  
  
> The calls to onTick(long) are synchronized同步 to this object so that one call to onTick(long) won't ever occur before the previous callback is complete.   
  
> This is only relevant相应、相关 when the implementation of onTick(long) takes an amount of一定数量的 time to execute执行 that is significant重大 compared to the countdown interval间隔.  
  
  
API数量非常少，但各个都极其有用  
构造方法  
```java  
CountDownTimer(long millisInFuture, long countDownInterval)  
```  
- millisInFuture: The number of millis in the future from the call to start() until the countdown is done and onFinish() is called.  
- countDownInterval: The interval along the way to receive onTick(long) callbacks.  
  
开启和结束方法  
```java  
final void cancel()  
final CountDownTimer start()  
```  
  
抽象(回调)方法  
```java  
abstract void onFinish()：Callback fired when the time is up.  
abstract void onTick(long millisUntilFinished)：Callback fired on regular interval. millisUntilFinished: The amount of time until finished.  
```  
  
### 使用案例  
```java  
@BindView(R.id.send) Button send;//发送验证码  
private CountDownTimer timer;//使用CountDownTimer  
​  
@OnClick({R.id.send, R.id.next})  
public void onClickIv(View v) {  
    switch (v.getId()) {  
        case R.id.send:  
            setTimer();//实际是要在点击之后判断如果符合倒计时条件才调用 setTimer 方法，要在失败后调用 destoryTimer  
            break;  
    }  
}  
​  
@Override  
protected void onDestroy() {  
    super.onDestroy();  
    destoryTimer();  
}  
​  
private void setTimer() {  
    send.setEnabled(false);  
    timer = new CountDownTimer(60 * 1000, 1000) {  
       @Override  
       public void onTick(long millisUntilFinished) {  
          int time = (int) (millisUntilFinished / 1000);  
          send.setText(time + "s");  
       }  
     
       @Override  
       public void onFinish() {  
          destoryTimer();  
       }  
    };  
    timer.start();  
}  
​  
private void destoryTimer() {  
    send.setEnabled(true);  
    send.setText("获取验证码");  
    if (timer != null) {  
        timer.cancel();  
        timer = null;  
    }  
}  
```  
  
## 使用 RxJava 实现 - 方便强大【推荐】  
可以使用 intervalRange 很方便的实现这个功能，也可以使用 repeat、repeatUntil、repeatWhen 间接实现类似功能。  
  
```java  
@BindView(R.id.send) Button send;//发送验证码  
private Disposable disposable;  
  
@OnClick({R.id.send, R.id.next})  
public void onClickIv(View v) {  
    switch (v.getId()) {  
        case R.id.send:  
            startCountDown();  
            break;  
    }  
}  
​  
@Override  
protected void onDestroy() {  
    super.onDestroy();  
    if (disposable != null && !disposable.isDisposed()) {  
        disposable.dispose();  
    }  
}  
  
private void startCountDown() {  
    send.setEnabled(false);  
    disposable = Observable.intervalRange(0, 10, 0, 1, TimeUnit.SECONDS) //起始值，发送总数量，初始延迟，固定延迟  
        .subscribeOn(Schedulers.io())  
        .observeOn(AndroidSchedulers.mainThread())  
        .subscribe(time -> send.setText((10 - time) + "s"),  
            Throwable::printStackTrace,  
            () -> {  
                send.setEnabled(true);  
                send.setText("获取验证码");  
            }  
        );  
}  
```  
  
## 使用 Timer + Handler 实现 - 麻烦【不推荐】  
### Timer + 普通 Handler - 麻烦  
```java  
@BindView(R.id.send) Button send;//发送验证码  
private int time = 60;//倒计时时间  
private Timer timer;  
​  
@OnClick({R.id.send, R.id.next})  
public void onClickIv(View v) {  
    switch (v.getId()) {  
        case R.id.send:  
            setTimer();//实际是要在点击之后判断如果符合倒计时条件才调用 setTimer 方法，要在失败后调用 destoryTimer  
            break;  
    }  
}  
​  
@Override  
protected void onDestroy() {  
    super.onDestroy();  
    destoryTimer();  
}  
​  
@SuppressLint("HandlerLeak")  
private Handler handler = new Handler() {  
    @Override  
    public void handleMessage(android.os.Message msg) {  
        switch (msg.what) {  
            case 1:  
                send.setText(time + "s");  
                break;  
            case 2:  
                destoryTimer();  
                break;  
        }  
    }  
};  
​  
//定时器  
private void setTimer() {  
    send.setEnabled(false);//不可点击  
    timer = new Timer();  
    TimerTask task = new TimerTask() {  
        @Override  
        public void run() {  
            time--;  
            if (time > 0) {  
                handler.sendEmptyMessage(1);  
            } else {  
                handler.sendEmptyMessage(2);  
            }  
        }  
    };  
    timer.schedule(task, 0, 1000);//每隔一秒钟执行一次  
}  
​  
private void destoryTimer() {  
    time = 60;//重新倒计时  
    send.setEnabled(true);//重新可点击  
    send.setText("重新发送");//重设文字  
    if (timer != null) {  
        timer.cancel();  
        timer = null;  
    }  
    if (handler!=null) {  
        handler.removeCallbacksAndMessages(null);  
    }  
}  
```  
  
### Timer + 静态 Handler - 更麻烦  
相比示例一，是将Handler定义为了静态内部类，以防止内存泄漏  
```java  
@BindView(R.id.send) Button send;//发送验证码  
private int time = 60;//倒计时时间  
private Timer timer;  
​  
@OnClick({R.id.send, R.id.next})  
public void onClickIv(View v) {  
    switch (v.getId()) {  
        case R.id.send:  
            setTimer();//实际是要在点击之后判断如果符合倒计时条件才调用 setTimer 方法，要在失败后调用 destoryTimer  
            break;  
    }  
}  
​  
@Override  
protected void onDestroy() {  
    super.onDestroy();  
    destoryTimer();  
}  
​  
private Handler handler = new MyHandler(this);  
private static class MyHandler extends Handler {  
   private SoftReference<ForgetPasswordActivity> mSoftReference;  
     
   MyHandler(ForgetPasswordActivity activity) {  
      mSoftReference = new SoftReference<>(activity);  
   }  
     
   @Override  
   public void handleMessage(Message msg) {  
      super.handleMessage(msg);  
      ForgetPasswordActivity activity = mSoftReference.get();  
      if (activity != null) {  
         switch (msg.what) {  
            case 1:  
               activity.send.setText(activity.time + "s");  
               break;  
            case 2:  
               activity.destoryTimer();  
               break;  
         }  
      }  
   }  
}  
​  
//定时器  
private void setTimer() {  
    send.setEnabled(false);//不可点击  
    timer = new Timer();  
    TimerTask task = new TimerTask() {  
        @Override  
        public void run() {  
            time--;  
            if (time > 0) {  
                handler.sendEmptyMessage(1);  
            } else {  
                handler.sendEmptyMessage(2);  
            }  
        }  
    };  
    timer.schedule(task, 0, 1000);//每隔一秒钟执行一次  
}  
​  
private void destoryTimer() {  
    time = 60;//重新倒计时  
    send.setEnabled(true);//重新可点击  
    send.setText("重新发送");//重设文字  
    if (timer != null) {  
        timer.cancel();  
        timer = null;  
    }  
    if (handler!=null) {  
        handler.removeCallbacksAndMessages(null);  
    }  
}  
```  
  
### Timer + runOnUiThread - 也麻烦  
可以不用Handler而用其他更精简的API：  
```java  
@BindView(R.id.send) Button send;//发送验证码  
private int time = 60;//倒计时时间  
private Timer timer;  
​  
@OnClick({R.id.send, R.id.next})  
public void onClickIv(View v) {  
    switch (v.getId()) {  
        case R.id.send:  
            setTimer();//实际是要在点击之后判断如果符合倒计时条件才调用 setTimer 方法，要在失败后调用 destoryTimer  
            break;  
    }  
}  
​  
@Override  
protected void onDestroy() {  
    super.onDestroy();  
    destoryTimer();  
}  
​  
private void setTimer() {  
   send.setEnabled(false);  
   TimerTask task = new TimerTask() {  
      @Override  
      public void run() {  
         runOnUiThread(() -> {  
            time--;  
            if (time > 0) {  
               send.setText(time + "s");  
            } else {  
               destoryTimer();  
            }  
         });  
      }  
   };  
   timer = new Timer();  
   timer.schedule(task, 0, 1000);//每隔一秒钟执行一次  
}  
​  
private void destoryTimer() {  
    time = 60;  
    send.setEnabled(true);  
    send.setText("获取验证码");  
    if (timer != null) {  
        timer.cancel();  
        timer = null;  
    }  
}  
```  
  
## 使用纯 Handler 实现 - 特麻烦【强烈不建议】  
倒计时通过用 Handler 发送 Delayed 消息来实现。核心代码为：  
```java  
handler.sendMessageDelayed(handler.obtainMessage(1), 1000);  
​  
final Handler handler = new Handler() {  
    public void handleMessage(Message msg) {  
        switch (msg.what) {  
        case 1:  
            time--;  
            if (time > 0) {  
                send.setText(time + "S");  
                handler.sendMessageDelayed(handler.obtainMessage(1), 1000);//循环发送  
            } else {  
                send.setEnabled(true);  
                send.setText("重新发送");  
            }  
        }  
    }  
};  
```  
  
倒计时通过用 Handler 发送 Delayed 的 Runnable 来实现，和上面原理是完全一样的。核心代码为：  
```java  
Handler handler = new Handler();   
handler.postDelayed(runnable, 1000);  
​  
Runnable runnable = new Runnable() {  
    @Override  
    public void run() {  
        time--;  
        if (time > 0) {  
            send.setText(time + "S");  
            handler.postDelayed(this, 1000);  
        } else {  
            send.setEnabled(true);  
            send.setText("重新发送");  
        }  
    }  
};  
```  
  
# 开源框架 CountdownView 简介  
GitHub上星星最多的倒计时控件：[CountdownView](https://github.com/iwgang/CountdownView)   
  
CountdownView：Android倒计时控件，使用Canvas绘制，支持多种样式  
  
```groovy  
compile 'com.github.iwgang:countdownview:2.1.3'  
```  
  
引用类名  
```  
cn.iwgang.countdownview.CountdownView  
```  
  
基本使用  
```java  
CountdownView mCountdownView = (CountdownView)findViewById(R.id.countdownView);  
mCountdownView.start(995550000); // 毫秒  
  
// 或者自己编写倒计时逻辑，然后调用 updateShow 来更新UI  
for (int time=0; time<1000; time++) {  
    mCountdownView.updateShow(time);  
}  
```  
  
其他用法  
- 动态设置自定义属性：`.dynamicShow(DynamicConfig)`  
- 倒计时结束后的回调：`.setOnCountdownEndListener(OnCountdownEndListener);`  
- 指定间隔时间的回调：`.setOnCountdownIntervalListener(long, OnCountdownIntervalListener);`  
  
# 在 RecyclerView 中实现倒计时  
## 更改数据源方式 - 简单但不可靠  
这种方案在数据量特别小（即List的size()特别小），且刷新item及计算倒计时耗费的时间特别短时适用，否则，将会产生巨大的时间延迟。  
  
```java  
//定时器，用于刷新 GridView 的数据源  
private void setQryTimer() {  
    cancelQryTimer(); //取消之前的定时器  
    qryTimer = new Timer(); //重新设置定时器  
    qryTimer.schedule(new TimerTask() {  
        @Override  
        public void run() {  
            runOnUiThread(() -> {  
                if (fixRpList != null && fixRpList.size() > 0) {  
                    for (FixRpBean item : fixRpList) {  
                        if (item.diff_time >= 0) item.diff_time = item.diff_time - 1000L; //更改数据源  
                    }  
                    if (fixRpDialog != null) fixRpDialog.upDate(fixRpList); //刷新页面  
                }  
            });  
        }  
    }, 0, 1000); //以固定的周期刷新  
}  
  
public void upDate(List<FixRpBean> redPacketList) {  
    list.clear(); //情况旧的数据  
    list.addAll(redPacketList); //设置新的数据(如果列表的数据和源数据是同一个集合，也可以直接更新)  
    mRecyclerView.getAdapter().notifyDataSetChanged();//建议使用RecyclerView的局部刷新功能  
}  
```  
  
## 让 System 帮我们倒计时 - 推荐  
核心思想为：利用`System.currentTimeMillis()`帮我们计算倒计时，并且在`onViewAttachedToWindow`时重新开始倒计时，在`onViewDetachedFromWindow`时关闭倒计时。  
  
```java  
public class RecyclerViewActivity extends Activity {  
    private List<ItemInfo> mDataList;  
      
    @Override  
    protected void onCreate(Bundle savedInstanceState) {  
        super.onCreate(savedInstanceState);  
        setContentView(R.layout.activity_recyclerview);  
          
        initData();  
          
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.rv);  
        recyclerView.setAdapter(new MyAdapter(this, mDataList));  
        recyclerView.setLayoutManager(new LinearLayoutManager(this));  
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));  
        recyclerView.setItemAnimator(new DefaultItemAnimator());  
    }  
      
    private void initData() {  
        mDataList = new ArrayList<>();  
        for (int i = 1; i < 20; i++) {  
            mDataList.add(new ItemInfo(i * 20 * 1000));  
        }  
        // 校对倒计时  
        long curTime = System.currentTimeMillis();  
        for (ItemInfo itemInfo : mDataList) {  
            itemInfo.endTime = curTime + itemInfo.countdown;  
        }  
    }  
      
    static class MyAdapter extends RecyclerView.Adapter<MyViewHolder> {  
        private Context mContext;  
        private List<ItemInfo> mDatas;  
          
        public MyAdapter(Context context, List<ItemInfo> datas) {  
            this.mContext = context;  
            this.mDatas = datas;  
        }  
          
        @Override  
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {  
            return new MyViewHolder(LayoutInflater.from(mContext).inflate(R.layout.list_item, parent, false));  
        }  
          
        @Override  
        public void onBindViewHolder(MyViewHolder holder, int position) {  
            holder.bindData(mDatas.get(holder.getAdapterPosition()));  
        }  
          
        @Override  
        public int getItemCount() {  
            return mDatas.size();  
        }  
          
        //******************************************** 关键代码 ↓↓ **********************************  
        @Override  
        public void onViewAttachedToWindow(MyViewHolder holder) {  
            super.onViewAttachedToWindow(holder);//父类中为空代码  
            holder.refreshTime(mDatas.get(holder.getAdapterPosition()).endTime - System.currentTimeMillis());  
        }  
          
        @Override  
        public void onViewDetachedFromWindow(MyViewHolder holder) {  
            super.onViewDetachedFromWindow(holder);  
            holder.countdownView.stop();  
        }  
        //******************************************** 关键代码 ↑↑ **********************************  
    }  
      
    static class MyViewHolder extends RecyclerView.ViewHolder {  
        public CountdownView countdownView;  
          
        public MyViewHolder(View itemView) {  
            super(itemView);  
            countdownView = (CountdownView) itemView.findViewById(R.id.countdownView);  
        }  
          
        public void bindData(ItemInfo itemInfo) {  
            refreshTime(itemInfo.endTime - System.currentTimeMillis());  
        }  
          
        public void refreshTime(long leftTime) {  
            if (leftTime > 0) {  
                countdownView.start(leftTime);  
            } else {  
                countdownView.stop();//停止计时器，mCustomCountDownTimer.stop();  
                countdownView.allShowZero();//所有计时清零，即mCountdown.setTimes(0, 0, 0, 0, 0);  
            }  
        }  
    }  
      
    static class ItemInfo {  
        public long countdown;  
        /*  
           根据服务器返回的countdown换算成手机对应的开奖时间 (毫秒)  
           [正常情况最好由服务器返回countdown字段，然后客户端再校对成该手机对应的时间，不然误差很大]  
         */  
        public long endTime;  
          
        public ItemInfo(long countdown) {  
            this.countdown = countdown;  
        }  
    }  
}  
```  
  
## 自己维护倒计时 - 既麻烦又低效  
自己维护倒计时，再调用 countdownView.updateShow 来刷新显示  
并且根据需要在 onResume 时开启倒计时，在 onPause 及 onDestroy 时关闭倒计时。  
  
```java  
//自己维护倒计时，再调用 countdownView.updateShow 来刷新显示  
public class RecyclerViewActivity2 extends AppCompatActivity {  
    private MyAdapter mMyAdapter;  
    private List<ItemInfo> mDataList;  
      
    @Override  
    protected void onCreate(Bundle savedInstanceState) {  
        super.onCreate(savedInstanceState);  
        setContentView(R.layout.activity_recyclerview);  
          
        initData();  
          
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.rv);  
        mMyAdapter = new RecyclerViewActivity2.MyAdapter(this, mDataList);  
        recyclerView.setAdapter(mMyAdapter);  
        recyclerView.setLayoutManager(new LinearLayoutManager(this));  
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));  
        recyclerView.setItemAnimator(new DefaultItemAnimator());  
    }  
      
    private void initData() {  
        mDataList = new ArrayList<>();  
        for (int i = 1; i < 20; i++) {  
            mDataList.add(new ItemInfo(1000 + i, "RecyclerView_测试标题_" + i, i * 20 * 1000));  
        }  
        // 校对倒计时  
        long curTime = System.currentTimeMillis();  
        for (ItemInfo itemInfo : mDataList) {  
            itemInfo.setEndTime(curTime + itemInfo.getCountdown());  
        }  
    }  
      
    @Override  
    protected void onResume() {  
        super.onResume();  
        if (null != mMyAdapter)   
    }  
      
    @Override  
    protected void onPause() {  
        super.onPause();  
        if (null != mMyAdapter)   
    }  
      
    @Override  
    public void onDestroy() {  
        super.onDestroy();  
        if (null != mMyAdapter)   
    }  
      
    static class MyAdapter extends RecyclerView.Adapter<MyViewHolder> {  
        private Context mContext;  
        private List<ItemInfo> mDatas;  
        private final SparseArray<MyViewHolder> mCountdownVHList;  
        private Handler mHandler = new Handler();  
        private Timer mTimer;  
        private boolean isCancel = true;  
          
        public MyAdapter(Context context, List<ItemInfo> datas) {  
            this.mContext = context;  
            this.mDatas = datas;  
            mCountdownVHList = new SparseArray<>();  
            startRefreshTime(); //开启倒计时  
        }  
          
        public void startRefreshTime() {  
            if (!isCancel) return;  
            if (null != mTimer) mTimer.cancel();  
              
            isCancel = false;  
            mTimer = new Timer();  
            mTimer.schedule(new TimerTask() {  
                @Override  
                public void run() {  
                    mHandler.post(mRefreshTimeRunnable);  
                }  
            }, 0, 10);  
        }  
          
        public void cancelRefreshTime() {  
            isCancel = true;  
            if (null != mTimer) {  
                mTimer.cancel();  
            }  
            mHandler.removeCallbacks(mRefreshTimeRunnable);  
        }  
          
        @Override  
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {  
            return new MyViewHolder(LayoutInflater.from(mContext).inflate(R.layout.list_item, parent, false));  
        }  
          
        @Override  
        public void onBindViewHolder(MyViewHolder holder, int position) {  
            ItemInfo curItemInfo = mDatas.get(position);  
            holder.bindData(curItemInfo);  
              
            // 处理倒计时  
            if (curItemInfo.getCountdown() > 0) {  
                synchronized (mCountdownVHList) {  
                    mCountdownVHList.put(curItemInfo.getId(), holder); //开启倒计时  
                }  
            }  
        }  
          
        @Override  
        public int getItemCount() {  
            return mDatas.size();  
        }  
          
        @Override  
        public void onViewRecycled(MyViewHolder holder) {  
            super.onViewRecycled(holder);  
              
            ItemInfo curAnnounceGoodsInfo = holder.getBean();  
            if (null != curAnnounceGoodsInfo && curAnnounceGoodsInfo.getCountdown() > 0) {  
                mCountdownVHList.remove(curAnnounceGoodsInfo.getId()); //移除  
            }  
        }  
          
        private Runnable mRefreshTimeRunnable = new Runnable() {  
            @Override  
            public void run() {  
                if (mCountdownVHList.size() == 0) return;  
                synchronized (mCountdownVHList) {  
                    long currentTime = System.currentTimeMillis();  
                    int key;  
                    for (int i = 0; i < mCountdownVHList.size(); i++) {  
                        key = mCountdownVHList.keyAt(i);  
                        MyViewHolder curMyViewHolder = mCountdownVHList.get(key);  
                        if (currentTime >= curMyViewHolder.getBean().getEndTime()) {  
                            curMyViewHolder.getBean().setCountdown(0);// 倒计时结束  
                            mCountdownVHList.remove(key);  
                            notifyDataSetChanged();  
                        } else {  
                            curMyViewHolder.refreshTime(currentTime); //刷新时间  
                        }  
                    }  
                }  
            }  
        };  
    }  
      
    static class MyViewHolder extends RecyclerView.ViewHolder {  
        private TextView mTvTitle;  
        private CountdownView mCvCountdownView;  
        private ItemInfo mItemInfo;  
          
        public MyViewHolder(View itemView) {  
            super(itemView);  
            mTvTitle = (TextView) itemView.findViewById(R.id.tv_title);  
            mCvCountdownView = (CountdownView) itemView.findViewById(R.id.cv_countdownView);  
        }  
          
        public void bindData(ItemInfo itemInfo) {  
            mItemInfo = itemInfo;  
            if (itemInfo.getCountdown() > 0) {  
                refreshTime(System.currentTimeMillis());  
            } else {  
                mCvCountdownView.allShowZero();  
            }  
            mTvTitle.setText(itemInfo.getTitle());  
        }  
          
        public void refreshTime(long curTimeMillis) {  
            if (null == mItemInfo || mItemInfo.getCountdown() <= 0) return;  
            mCvCountdownView.updateShow(mItemInfo.getEndTime() - curTimeMillis);  
        }  
          
        public ItemInfo getBean() {  
            return mItemInfo;  
        }  
    }  
      
    static class ItemInfo {  
        private int id;  
        private String title;  
        private long countdown;  
        /*  
           根据服务器返回的countdown换算成手机对应的开奖时间 (毫秒)  
           [正常情况最好由服务器返回countdown字段，然后客户端再校对成该手机对应的时间，不然误差很大]  
         */  
        private long endTime;  
          
        public ItemInfo(int id, String title, long countdown) {  
            this.id = id;  
            this.title = title;  
            this.countdown = countdown;  
        }  
        //get、set方法...  
    }  
      
}  
```  
2017-6-12  
  
  
  
  
  
  
  
  
  
