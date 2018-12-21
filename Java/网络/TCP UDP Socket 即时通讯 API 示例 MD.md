| Markdown版本笔记 | 我的GitHub首页 | 我的博客 | 我的微信 | 我的邮箱 |  
| :------------: | :------------: | :------------: | :------------: | :------------: |  
| [MyAndroidBlogs][Markdown] | [baiqiantao][GitHub] | [baiqiantao][博客] | bqt20094 | baiqiantao@sina.com |  
  
[Markdown]:https://github.com/baiqiantao/MyAndroidBlogs  
[GitHub]:https://github.com/baiqiantao  
[博客]:http://www.cnblogs.com/baiqiantao/  
  
TCP Socket 即时通讯 API 示例  
***  
目录  
===  

- [TCP 案例](#tcp-案例)
	- [SocketActivity](#socketactivity)
	- [服务端 Server](#服务端-server)
	- [客户端 Client](#客户端-client)
- [UDP 案例](#udp-案例)
	- [SocketActivity](#socketactivity)
	- [Client](#client)
- [常见异常](#常见异常)
- [API](#api)
	- [ServerSocket](#serversocket)
	- [Socket](#socket)
	- [DatagramSocket](#datagramsocket)
	- [DatagramPacket](#datagrampacket)
- [WebSocket](#websocket)
	- [简介](#简介)
	- [服务端](#服务端)
	- [客户端](#客户端)
  
# TCP 案例  
## SocketActivity  
```java  
public class SocketActivity extends ListActivity implements Client.MsgListener {  
    public static final int PORT = 11232;  
    public static final String HOST = "192.168.1.187"; //此 IP 为内网 IP ，所有只有在同一局域网下才能通讯(连接同一WIFI即可)  
      
    private TextView msgTextView;  
    private EditText editText;  
      
    protected void onCreate(Bundle savedInstanceState) {  
        super.onCreate(savedInstanceState);  
        String[] array = {"开启服务器",  
            "开启客户端",  
            "客户端发消息",  
            "客户端下线"};  
        setListAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, Arrays.asList(array)));  
          
        editText = new EditText(this);  
        getListView().addFooterView(editText);  
          
        msgTextView = new TextView(this);  
        getListView().addFooterView(msgTextView);  
    }  
      
    @Override  
    protected void onListItemClick(ListView l, View v, int position, long id) {  
        switch (position) {  
            case 0:  
                Server.SINGLETON.startServer(PORT);  
                break;  
            case 1:  
                Client.SINGLETON.startClient(HOST, PORT);  
                Client.SINGLETON.setListener(this);  
                break;  
            case 2:  
                Client.SINGLETON.sendMsg(editText.getText().toString());  
                break;  
            case 3:  
                Client.SINGLETON.exit();  
                break;  
            default:  
                break;  
        }  
    }  
      
    private SpannableString getSpannableString(String string, int color) {  
        SpannableString mSpannableString = new SpannableString(string);  
        ForegroundColorSpan colorSpan = new ForegroundColorSpan(color);  
        mSpannableString.setSpan(colorSpan, 0, mSpannableString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);  
        return mSpannableString;  
    }  
      
    @Override  
    public void onReveiveMsg(String message) {  
        runOnUiThread(() -> msgTextView.append(getSpannableString(message + "\n", Color.BLUE)));  
    }  
      
    @Override  
    public void onSendMsg(String message) {  
        runOnUiThread(() -> {  
            String text = Client.SINGLETON.getName() + " : " + editText.getText().toString() + "\n";  
            msgTextView.append(getSpannableString(text, Color.RED));  
        });  
    }  
}  
```  
  
## 服务端 Server  
```java  
public enum Server {  
    SINGLETON;  
      
    public static final int MAX_TEXT_SIZE = 1024;  
    public static final String CLIENT_EXIT_CMD = "拜拜";  
    public static final String CHARSET = "GBK";  
      
    private Set<Socket> socketSet;  
    private boolean serverExit = false;  
    private ServerSocket server;//服务器对象  
      
    Server() {  
        socketSet = new HashSet<>();//用户集合  
    }  
      
    public void startServer(int port) {  
        if (server != null && !server.isClosed()) {  
            System.out.println("服务器已开启，不需要重复开启");  
        } else {  
            new Thread(() -> {  
                try {  
                    server = new ServerSocket(port);  
                } catch (IOException e) {  
                    e.printStackTrace();  
                    System.out.println("服务器启动失败");  
                    return;  
                }  
                  
                System.out.println("服务器启动成功");  
                //循环监听  
                while (!serverExit) {  
                    try {  
                        Socket socket = server.accept();//获取连接过来的客户端对象。阻塞方法  
                        socketSet.add(socket);  
                        sendUserMsg(socket, getName(socket) + " 上线了, 当前 " + socketSet.size() + " 人在线");  
                        listenerUserMsg(socket); //监听客户端发送的消息，并转发给其他用户  
                    } catch (IOException e) {//用户下线  
                        e.printStackTrace();  
                    }  
                }  
                System.out.println("服务器已关闭");  
            }).start();  
        }  
    }  
      
    private void listenerUserMsg(Socket socket) {  
        new Thread(() -> {  
            try {  
                byte[] bytes = new byte[MAX_TEXT_SIZE];  
                int count;  
                boolean clinetExit = false;  
                while (!serverExit && !clinetExit && !socket.isClosed() && (count = socket.getInputStream().read(bytes)) != -1) {  
                    String text = new String(bytes, 0, count, CHARSET);  
                    System.out.println("服务器已收到【" + getName(socket) + "】发送的信息【" + text + "】");  
                    clinetExit = CLIENT_EXIT_CMD.equals(text);  
                    sendUserMsg(socket, getName(socket) + " : " + text);  
                }  
            } catch (IOException e) {//关闭与此用户相关的流  
                e.printStackTrace();  
                System.out.println(getName(socket) + " 异常掉线");  
            } finally {  
                if (socket != null) {  
                    try {  
                        socket.close();  
                    } catch (IOException e) {  
                        e.printStackTrace();  
                    }  
                    socketSet.remove(socket);  
                    sendUserMsg(socket, getName(socket) + " 下线了, 当前 " + socketSet.size() + " 人在线");  
                }  
            }  
        }).start();  
    }  
      
    private void sendUserMsg(Socket socket, String text) {  
        for (Socket otherSocket : socketSet) {//遍历所有的在线用户  
            if (otherSocket != null && !otherSocket.isClosed() && !otherSocket.equals(socket)) {  
                try {  
                    OutputStream outputStream = otherSocket.getOutputStream();//获取相应的输出流，把信息发送过去  
                    outputStream.write(text.getBytes(CHARSET));  
                    outputStream.flush();  
                    System.out.println("服务器已转发信息【" + text + "】给【" + getName(otherSocket) + "】");  
                } catch (IOException e) {  
                    e.printStackTrace();  
                    System.out.println(getName(socket) + " 异常");  
                }  
            }  
        }  
    }  
      
    private String getName(Socket socket) {  
        return "用户" + socket.getInetAddress().getHostAddress() + "_" + socket.getPort();  
    }  
}  
```  
  
## 客户端 Client  
```java  
public enum Client {  
      
    SINGLETON;  
      
    Client() {  
    }  
      
    public static final int MAX_TEXT_SIZE = 1024;  
    public static final String CLIENT_EXIT_CMD = "拜拜";  
    public static final String CHARSET = "GBK";  
      
    private boolean exit = false;  
    private Socket socket;  
      
    private MsgListener listener;  
      
    public void startClient(String host, int port) {  
        if (socket != null && !socket.isClosed()) {  
            System.out.println("客户端已开启，不需要重复开启");  
        } else {  
            new Thread(() -> {  
                try {  
                    socket = new Socket(host, port);//创建客户端对象  
                    listenerUserMsg(); //监听消息  
                    System.out.println("客户端已开启成功");  
                } catch (IOException e) {  
                    e.printStackTrace();  
                    System.out.println("客户端已开启失败");  
                }  
            }).start();  
        }  
    }  
      
    private void listenerUserMsg() {  
        new Thread(() -> {  
            try {  
                byte[] bytes = new byte[MAX_TEXT_SIZE];  
                int count;  
                while (!exit && socket != null && !socket.isClosed() && (count = socket.getInputStream().read(bytes)) != -1) {  
                    String text = new String(bytes, 0, count, CHARSET);  
                    System.out.println(getName() + " 收到信息【" + text + "】");  
                    if (listener != null) {  
                        listener.onReveiveMsg(text);  
                    }  
                }  
            } catch (IOException e) {  
                e.printStackTrace();  
            }  
        }).start();  
    }  
      
    public void sendMsg(String text) {  
        new Thread(() -> {  
            if (socket != null && !socket.isClosed()) {  
                try {  
                    socket.getOutputStream().write(text.getBytes(CHARSET));//获取socket流中的输出流将指定的数据写出去  
                    if (listener != null) {  
                        listener.onSendMsg(text);  
                    }  
                } catch (IOException e) {  
                    e.printStackTrace();  
                }  
            }  
        }).start();  
    }  
      
    public void exit() {  
        new Thread(() -> {  
            exit = true;  
            if (socket != null && !socket.isClosed()) {  
                try {  
                    socket.getOutputStream().write(CLIENT_EXIT_CMD.getBytes(CHARSET));  
                    socket.close();  
                    System.out.println("客户端下线成功");  
                } catch (IOException e) {  
                    e.printStackTrace();  
                    System.out.println("客户端下线异常");  
                }  
            } else {  
                System.out.println("客户端已下线，不需要重复下线");  
            }  
        }).start();  
    }  
      
    public String getName() {  
        return "用户" + socket.getInetAddress().getHostAddress() + "_" + socket.getPort();  
    }  
      
    public void setListener(MsgListener listener) {  
        this.listener = listener;  
    }  
      
    public interface MsgListener {  
        void onReveiveMsg(String message);  
          
        void onSendMsg(String message);  
    }  
}  
```  
  
# UDP 案例  
## SocketActivity  
```java  
public class SocketActivity extends ListActivity implements Client.MsgListener {  
    private boolean tag = true;  
    public static final int PORT1 = 11233;  
    public static final int PORT2 = 11234;  
    public static final String HOST1 = "192.168.2.124";  
    public static final String HOST2 = "192.168.2.177";  
      
    private TextView msgTextView;  
    private EditText editText;  
      
    protected void onCreate(Bundle savedInstanceState) {  
        super.onCreate(savedInstanceState);  
        String[] array = {"切换配置",  
            "开启客户端",  
            "客户端发消息",  
            "客户端下线"};  
        setListAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, Arrays.asList(array)));  
          
        editText = new EditText(this);  
        getListView().addFooterView(editText);  
          
        msgTextView = new TextView(this);  
        getListView().addFooterView(msgTextView);  
    }  
      
    @Override  
    protected void onListItemClick(ListView l, View v, int position, long id) {  
        switch (position) {  
            case 0:  
                tag = !tag;  
                break;  
            case 1:  
                Client.SINGLETON.startClient(tag ? PORT1 : PORT2);  
                Client.SINGLETON.setListener(this);  
                break;  
            case 2:  
                try {  
                    //getLocalHost()，getByName("127.0.0.1")，getByName("192.168.0.255")，getByAddress(new byte[]{127,0,0,1})  
                    InetAddress address = InetAddress.getByName(tag ? HOST2 : HOST1);  
                    Client.SINGLETON.sendMsg(editText.getText().toString(), address, tag ? PORT2 : PORT1);  
                } catch (UnknownHostException e) {  
                    e.printStackTrace();  
                }  
                break;  
            case 3:  
                Client.SINGLETON.exit();  
                break;  
            default:  
                break;  
        }  
    }  
      
    private SpannableString getSpannableString(String string, int color) {  
        SpannableString mSpannableString = new SpannableString(string);  
        ForegroundColorSpan colorSpan = new ForegroundColorSpan(color);  
        mSpannableString.setSpan(colorSpan, 0, mSpannableString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);  
        return mSpannableString;  
    }  
      
    @Override  
    public void onReveiveMsg(String message) {  
        runOnUiThread(() -> msgTextView.append(getSpannableString(message + "\n", Color.BLUE)));  
    }  
      
    @Override  
    public void onSendMsg(String message) {  
        runOnUiThread(() -> {  
            String text = Client.SINGLETON.getName() + " : " + editText.getText().toString() + "\n";  
            msgTextView.append(getSpannableString(text, Color.RED));  
        });  
    }  
}  
```  
  
## Client  
```java  
public enum Client {  
      
    SINGLETON;  
      
    Client() {  
    }  
      
    public static final int MAX_TEXT_SIZE = 1024;  
    public static final String CHARSET = "GBK";  
      
    private boolean exit = false;  
    private DatagramSocket send;  
    private DatagramSocket receiver;  
    private MsgListener listener;  
      
    public void startClient(int port) {  
        if (send != null && !send.isClosed()) {  
            System.out.println("客户端" + port + "已开启，不需要重复开启");  
        } else {  
            new Thread(() -> {  
                try {  
                    send = new DatagramSocket();  
                    receiver = new DatagramSocket(port);  
                    listenerUserMsg(); //监听消息  
                    System.out.println("客户端" + port + "已开启成功");  
                } catch (IOException e) {  
                    e.printStackTrace();  
                    System.out.println("客户端" + port + "已开启失败");  
                }  
            }).start();  
        }  
    }  
      
    private void listenerUserMsg() {  
        new Thread(() -> {  
            try {  
                byte[] bytes = new byte[MAX_TEXT_SIZE];  
                DatagramPacket packet = new DatagramPacket(bytes, bytes.length);  
                while (!exit && receiver != null && !receiver.isClosed()) {  
                    receiver.receive(packet);//持续接收数据  
                    String text = new String(packet.getData(), 0, packet.getLength(), CHARSET);  
                    System.out.println(getName() + " 收到信息【" + text + "】");  
                    if (listener != null) {  
                        listener.onReveiveMsg(text);  
                    }  
                }  
            } catch (IOException e) {  
                e.printStackTrace();  
            }  
        }).start();  
    }  
      
    public void sendMsg(String text, InetAddress address, int port) {  
        new Thread(() -> {  
            if (send != null && !send.isClosed()) {  
                try {  
                    String content = getName() + " : " + text;  
                    byte[] buf = content.getBytes(CHARSET);  
                    DatagramPacket packet = new DatagramPacket(buf, buf.length, address, port);  
                    send.send(packet);  
                    if (listener != null) {  
                        listener.onSendMsg(text);  
                    }  
                } catch (IOException e) {  
                    e.printStackTrace();  
                }  
            }  
        }).start();  
    }  
      
    public void exit() {  
        new Thread(() -> {  
            exit = true;  
            if (send != null && !send.isClosed()) {  
                send.close();  
            }  
            if (receiver != null && !receiver.isClosed()) {  
                receiver.close();  
            }  
            System.out.println("客户端下线成功");  
        }).start();  
    }  
      
    public String getName() {  
        return "用户" + receiver.getLocalPort();  
    }  
      
    public void setListener(MsgListener listener) {  
        this.listener = listener;  
    }  
      
    public interface MsgListener {  
        void onReveiveMsg(String message);  
          
        void onSendMsg(String message);  
    }  
}  
```  
  
# 常见异常  
- `BindException:Address already in use: JVM_Bind` 该异常发生在服务器端进行`new ServerSocket(port)`操作时。异常的原因是此port端口已经被占用。此时用`netstat –an`命令，可以看到一个Listending状态的端口。只需要找一个没有被占用的端口就能解决这个问题。   
- `ConnectException: Connection refused: connect` 该异常发生在客户端进行`new Socket(ip, port)`操作时，该异常发生的原因是或者具有此ip地址的机器不能找到，或者是该ip存在但找不到指定的端口进行监听。出现该问题，首先检查客户端的ip和port是否写错了，如果正确则从客户端`ping`一下服务器看是否能ping通，如果能ping通再看在服务器端的监听指定端口的程序是否启动。   
- `Socket is closed` 该异常在客户端和服务器均可能发生。异常的原因是连接已被关闭后(调用了Socket的close方法)再对网络连接进行读写操作。   
-  `SocketException:(Connection reset 或者 Connect reset by peer:Socket write error)` 该异常在客户端和服务器端均有可能发生，引起该异常的原因有两个，第一个就是如果一端的Socket被关闭，另一端仍发送数据，发送的第一个数据包引发该异常(Connect reset by peer)。另一个是一端退出，但退出时并未关闭该连接，另一端如果在从连接中读数据则抛出该异常（Connection reset）。简单的说就是在连接断开后的读和写操作引起的。   
- `SocketException: Broken pipe` 该异常在客户端和服务器均有可能发生。在上面那种异常的第一种情况中(也就是抛出SocketExcepton:Connect reset by peer:Socket write error)，如果再继续写数据则抛出该异常。前两个异常的解决方法是首先确保程序退出前关闭所有的网络连接，其次是要检测对方的关闭连接操作，发现对方关闭连接后自己也要关闭该连接。  
  
# API  
## ServerSocket  
**构造方法**  
- ServerSocket()  创建非绑定服务器套接字。  
- `ServerSocket(int port)`  创建绑定到特定端口的服务器套接字。  
- ServerSocket(int port, int backlog)  利用指定的 backlog 创建服务器套接字并将其绑定到指定的本地端口号。  
- ServerSocket(int port, int backlog, InetAddress bindAddr)  使用指定的端口、侦听 backlog 和要绑定到的本地 IP 地址创建服务器。  
  
**常用方法**  
- `Socket accept()`  侦听并接受到此套接字的连接。  
- void bind(SocketAddress endpoint)  将 ServerSocket 绑定到特定地址（IP 地址和端口号）。  
- void bind(SocketAddress endpoint, int backlog)  将 ServerSocket 绑定到特定地址（IP 地址和端口号）。  
- `void close()`  关闭此套接字。  
- ServerSocketChannel getChannel()  返回与此套接字关联的唯一 ServerSocketChannel 对象（如果有）。  
- InetAddress getInetAddress()  返回此服务器套接字的本地地址。  
- `int getLocalPort()`  返回此套接字在其上侦听的端口。  
- SocketAddress getLocalSocketAddress()  返回此套接字绑定的端点的地址，如果尚未绑定则返回 null。  
- int getReceiveBufferSize()  获取此 ServerSocket 的 SO_RCVBUF 选项的值，该值是将用于从此 ServerSocket 接受的套接字的建议缓冲区大小。  
- boolean getReuseAddress()  测试是否启用 SO_REUSEADDR。  
- int getSoTimeout()  获取 SO_TIMEOUT 的设置。  
- protected  void implAccept(Socket s)  ServerSocket 的子类使用此方法重写 accept()   以返回它们自己的套接字子类。  
- boolean isBound()  返回 ServerSocket 的绑定状态。  
- `boolean isClosed()`  返回 ServerSocket 的关闭状态。  
- void setPerformancePreferences(int connectionTime, int latency, int bandwidth)  设置此 ServerSocket 的性能首选项。  
- void setReceiveBufferSize(int size)  为从此 ServerSocket 接受的套接字的 SO_RCVBUF 选项设置默认建议值。  
- void setReuseAddress(boolean on)  启用/禁用 SO_REUSEADDR 套接字选项。  
- static void setSocketFactory(SocketImplFactory fac)  为应用程序设置服务器套接字实现工厂。  
- void setSoTimeout(int timeout)  通过指定超时值启用/禁用 SO_TIMEOUT，以毫秒为单位。  
- String toString()  作为 String 返回此套接字的实现地址和实现端口。  
  
  
## Socket  
**构造方法**  
- Socket()  通过系统默认类型的 SocketImpl 创建未连接套接字  
- `Socket(InetAddress address, int port)`  创建一个流套接字并将其连接到指定IP地址的指定端口号  
- Socket(InetAddress address, int port, InetAddress localAddr, int localPort)  创建一个套接字并将其连接到指定远程地址上的指定远程端口。  
- Socket(Proxy proxy)  创建一个未连接的套接字并指定代理类型（如果有），该代理不管其他设置如何都应被使用。  
- Socket(SocketImpl impl)  使用用户指定的 SocketImpl 创建一个未连接 Socket。  
- `Socket(String host, int port)`  创建一个流套接字并将其连接到指定主机上的指定端口号。  
- Socket(String host, int port, InetAddress localAddr, int localPort)  创建一个套接字并将其连接到指定远程主机上的指定远程端口。  
  
**常用方法**  
- void bind(SocketAddress bindpoint)  将套接字绑定到本地地址。  
- `void close()`  关闭此套接字。  
- void connect(SocketAddress endpoint)  将此套接字连接到服务器。  
- void connect(SocketAddress endpoint, int timeout)  将此套接字连接到服务器，并指定一个超时值  
- SocketChannel getChannel()  返回与此数据报套接字关联的唯一 SocketChannel 对象（如果有）。  
- `InetAddress getInetAddress()`  返回套接字连接的地址。  
- `InputStream getInputStream()`  返回此套接字的输入流。  
- boolean getKeepAlive()  测试是否启用 SO_KEEPALIVE。  
- InetAddress getLocalAddress()  获取套接字绑定的本地地址。  
- `int getLocalPort()`  返回此套接字绑定到的本地端口。  
- SocketAddress getLocalSocketAddress()  返回此套接字绑定的端点的地址，如果尚未绑定则返回 null。  
- boolean getOOBInline()  测试是否启用 OOBINLINE。  
- `OutputStream getOutputStream()`  返回此套接字的输出流。  
- `int getPort()`  返回此套接字连接到的远程端口。  
- int getReceiveBufferSize()  获取此 Socket 的 SO_RCVBUF 选项的值，该值是平台在 Socket 上输入时使用的缓冲区大小。  
- SocketAddress getRemoteSocketAddress()  返回此套接字连接的端点的地址，如果未连接则返回 null。  
- boolean getReuseAddress()  测试是否启用 SO_REUSEADDR。  
- int getSendBufferSize()  获取此 Socket 的 SO_SNDBUF 选项的值，该值是平台在 Socket 上输出时使用的缓冲区大小。  
- int getSoLinger()  返回 SO_LINGER 的设置。  
- int getSoTimeout()  返回 SO_TIMEOUT 的设置。  
- boolean getTcpNoDelay()  测试是否启用 TCP_NODELAY。  
- int getTrafficClass()  为从此 Socket 上发送的包获取 IP 头中的流量类别或服务类型。  
- boolean isBound()  返回套接字的绑定状态。  
- `boolean isClosed()`  返回套接字的关闭状态。  
- `boolean isConnected()`  返回套接字的连接状态。  
- boolean isInputShutdown()  返回是否关闭套接字连接的半读状态 (read-half)  。  
- boolean isOutputShutdown()  返回是否关闭套接字连接的半写状态 (write-half)  。  
- void sendUrgentData(int data)  在套接字上发送一个紧急数据字节。  
- void setKeepAlive(boolean on)  启用/禁用 SO_KEEPALIVE。  
- void setOOBInline(boolean on)  启用/禁用 OOBINLINE（TCP 紧急数据的接收者） 默认情况下，此选项是禁用的，即在套接字上接收的 TCP 紧急数据被静默丢弃。  
- void setPerformancePreferences(int connectionTime, int latency, int bandwidth)  设置此套接字的性能偏好。  
- void setReceiveBufferSize(int size)  将此 Socket 的 SO_RCVBUF 选项设置为指定的值。  
- void setReuseAddress(boolean on)  启用/禁用 SO_REUSEADDR 套接字选项。  
- void setSendBufferSize(int size)  将此 Socket 的 SO_SNDBUF 选项设置为指定的值。  
- static void setSocketImplFactory(SocketImplFactory fac)  为应用程序设置客户端套接字实现工厂。  
- void setSoLinger(boolean on, int linger)  启用/禁用具有指定逗留时间（以秒为单位）的 SO_LINGER。  
- void setSoTimeout(int timeout)  启用/禁用带有指定超时值的 SO_TIMEOUT，以毫秒为单位。  
- void setTcpNoDelay(boolean on)  启用/禁用 TCP_NODELAY（启用/禁用 Nagle 算法）。  
- void setTrafficClass(int tc)  为从此 Socket 上发送的包在 IP 头中设置流量类别 (traffic class)   或服务类型八位组 (type-of-service octet)  。  
- void shutdownInput()  此套接字的输入流置于“流的末尾”。  
- void shutdownOutput()  禁用此套接字的输出流。  
- String toString()  将此套接字转换为 String。  
  
## DatagramSocket  
**构造方法**  
- `DatagramSocket()` 构造数据报套接字并将其绑定到本地主机上任何可用的端口。  
- `DatagramSocket(int port)` 创建数据报套接字并将其绑定到本地主机上的指定端口。  
- DatagramSocket(int port, InetAddress laddr) 绑定到指定的本地地址。  
- DatagramSocket(SocketAddress bindaddr)  绑定到指定的本地套接字地址。  
- protected DatagramSocket(DatagramSocketImpl impl) 创建带有指定impl 的未绑定数据报套接字  
  
**常用方法**  
- void bind(SocketAddress addr)   将此 DatagramSocket 绑定到特定的地址和端口。  
- `void close()`  关闭此数据报套接字。  
- void connect(InetAddress address, int port)  将套接字连接到此套接字的远程地址。  
- void connect(SocketAddress addr)  将此套接字连接到远程套接字地址（IP 地址 + 端口号）。  
- void disconnect()   断开套接字的连接。  
- boolean getBroadcast()  检测是否启用了 SO_BROADCAST。  
- DatagramChannel getChannel()   返回与此数据报套接字关联的唯一 DatagramChannel 对象（如果有）。  
- InetAddress getInetAddress() 返回此套接字连接的地址。  
- InetAddress getLocalAddress()  获取套接字绑定的本地地址。  
- `int getLocalPort()`  返回此套接字绑定的本地主机上的端口号。  
- SocketAddress getLocalSocketAddress() 返回此套接字绑定的端点的地址，如果尚未绑定则返回 null。  
- `int getPort()` 返回此套接字的端口。  
- int getReceiveBufferSize()   获取此 DatagramSocket 的 SO_RCVBUF 选项的值，该值是平台在 DatagramSocket 上输入时使用的缓冲区大小。  
- SocketAddress getRemoteSocketAddress()  返回此套接字连接的端点的地址，如果未连接则返回 null。  
- boolean getReuseAddress()  检测是否启用了 SO_REUSEADDR。  
- int getSendBufferSize()   获取此 DatagramSocket 的 SO_SNDBUF 选项的值，该值是平台在 DatagramSocket 上输出时使用的缓冲区大小。  
- int getSoTimeout()  获取 SO_TIMEOUT 的设置。  
- int getTrafficClass()  为从此 DatagramSocket 上发送的包获取 IP 数据报头中的流量类别或服务类型  
- boolean isBound() 返回套接字的绑定状态。  
- `boolean isClosed()`  返回是否关闭了套接字。  
- `boolean isConnected()` 返回套接字的连接状态。  
- `void receive(DatagramPacket p)` 从此套接字接收数据报包。  
- `void send(DatagramPacket p)` 从此套接字发送数据报包。  
- void setBroadcast(boolean on) 启用/禁用 SO_BROADCAST。  
- static void setDatagramSocketImplFactory(DatagramSocketImplFactory fac) 为应用程序设置数据报套接字实现工厂  
- void setReceiveBufferSize(int size) 将此DatagramSocket的 SO_RCVBUF 选项设置为指定的值  
- void setReuseAddress(boolean on)   启用/禁用 SO_REUSEADDR 套接字选项。  
- void setSendBufferSize(int size)  将此 DatagramSocket 的 SO_SNDBUF 选项设置为指定的值  
- void setSoTimeout(int timeout) 启用/禁用带有指定超时值的 SO_TIMEOUT，以毫秒为单位。  
- void setTrafficClass(int tc)   为从此 DatagramSocket 上发送的数据报在 IP 数据报头中设置流量类别 (traffic class) 或服务类型八位组 (type-of-service octet)。  
  
## DatagramPacket   
**构造方法**  
接收  
- `DatagramPacket(byte[] buf, int length)` 构造 DatagramPacket，用来`接收`长度为 length 的数据包。  
- DatagramPacket(byte[] buf, int offset, int length)  构造 DatagramPacket，用来`接收`长度为 length 的包，在缓冲区中指定了偏移量。  
  
发送到 InetAddress  
- `DatagramPacket(byte[] buf, int length, InetAddress address, int port)` 构造数据报包，用来将长度为 length 的包 buf `发送`到`指定主机address`上的`指定端口号port`。  
- DatagramPacket(byte[] buf, int offset, int length, InetAddress address, int port) 构造数据报包，用来将长度为 length 偏移量为 offset 的包`发送`到指定主机上的指定端口号。  
  
发送到 SocketAddress  
- DatagramPacket(byte[] buf, int offset, int length, SocketAddress address) 构造数据报包，用来将长度为 length 偏移量为 offset 的包`发送`到指定主机上的指定端口号。  
- DatagramPacket(byte[] buf, int length, SocketAddress address) 构造数据报包，用来将长度为 length 的包`发送`到指定主机上的指定端口号。  
  
**常用方法**  
get 方法  
- `InetAddress getAddress()`   返回某台机器的 IP 地址，此数据报将要发往该机器或者是从该机器接收到的  
- `byte[] getData()` 返回数据缓冲区。  
- `int getLength()` 返回将要发送或接收到的数据的长度。  
- `int getOffset()` 返回将要发送或接收到的数据的偏移量。  
- `int getPort()` 返回某台远程主机的端口号，此数据报将要发往该主机或者是从该主机接收到的。  
- SocketAddress getSocketAddress()  获取要将此包发送到的或发出此数据报的远程主机的 SocketAddress  
  
set 方法  
- void setAddress(InetAddress iaddr) 设置要将此数据报发往的那台机器的 IP 地址。  
- void setData(byte[] buf) 为此包设置数据缓冲区。  
- void setData(byte[] buf, int offset, int length) 为此包设置数据缓冲区。  
- void setLength(int length) 为此包设置长度。  
- void setPort(int iport)  设置要将此数据报发往的远程主机上的端口号。  
- void setSocketAddress(SocketAddress address)  设置要将此数据报发往的远程主机的 SocketAddress（通常为 IP 地址 + 端口号）。  
  
# WebSocket  
## 简介  
[参考](https://www.cnblogs.com/xdp-gacl/p/5193279.html)  
  
随着互联网的发展，传统的HTTP协议已经很难满足Web应用日益复杂的需求了。近年来，随着HTML5的诞生，WebSocket协议被提出，它实现了浏览器与服务器的`全双工通信`，扩展了浏览器与服务端的通信功能，使`服务端也能主动向客户端发送数据`。  
  
我们知道，传统的HTTP协议是`无状态`的，每次请求（request）都要由客户端主动发起，服务端进行处理后返回response结果，而服务端很难主动向客户端发送数据；这种客户端是主动方，服务端是被动方的传统Web模式 对于信息变化不频繁的Web应用来说造成的麻烦较小，而对于涉及实时信息的Web应用却带来了很大的不便，如带有即时通信、实时数据、订阅推送等功能的应用。在WebSocket规范提出之前，开发人员若要实现这些实时性较强的功能，经常会使用折衷的解决方法：`轮询（polling）和Comet技术`。其实后者本质上也是一种轮询，只不过有所改进。  
  
轮询是最原始的实现实时Web应用的解决方案。轮询技术要求客户端`以设定的时间间隔周期性地向服务端发送请求`，频繁地查询是否有新的数据改动。明显地，这种方法会导致过多不必要的请求，浪费流量和服务器资源。  
  
Comet技术又可以分为`长轮询和流技术`。长轮询改进了上述的轮询技术，减小了无用的请求。它会为某些数据设定`过期时间`，当数据过期后才会向服务端发送请求；这种机制适合数据的改动不是特别频繁的情况。流技术通常是指客户端`使用一个隐藏的窗口与服务端建立一个HTTP长连接，服务端会不断更新连接状态以保持HTTP长连接存活`；这样的话，服务端就可以通过这条长连接主动将数据发送给客户端；流技术在大并发环境下，可能会考验到服务端的性能。  
  
这两种技术都是基于`请求-应答`模式，都不算是真正意义上的实时技术；它们的每一次请求、应答，都浪费了一定流量在`相同的头部信息`上，并且开发复杂度也较大。  
  
伴随着HTML5推出的WebSocket，真正实现了Web的实时通信，`使B/S模式具备了C/S模式的实时通信能力`。WebSocket的工作流程是这样的：浏览器通过JavaScript向服务端发出建立WebSocket连接的请求，在WebSocket连接建立成功后，客户端和服务端就可以通过 `TCP` 连接传输数据。因为WebSocket连接本质上是TCP连接，`不需要每次传输都带上重复的头部数据`，所以它的数据传输量比轮询和Comet技术小 了很多。本文不详细地介绍WebSocket规范，主要介绍下WebSocket在Java Web中的实现。  
  
JavaEE 7中出了JSR-356:Java API for WebSocket规范。不少Web容器，如Tomcat,Nginx,Jetty等都支持WebSocket。Tomcat从7.0.27开始支持 WebSocket，从7.0.47开始支持JSR-356，下面的Demo代码也是需要部署在Tomcat7.0.47以上的版本才能运行。  
  
## 服务端  
```java  
import java.io.IOException;  
import java.util.concurrent.CopyOnWriteArraySet;  
  
import javax.websocket.*;  
import javax.websocket.server.ServerEndpoint;  
  
/**  
 * @ServerEndpoint 注解是一个类层次的注解，它的功能主要是将目前的类定义成一个websocket服务器端，  
 * 注解的值将被用于监听用户连接的终端访问URL地址，客户端可以通过这个URL来连接到WebSocket服务器端  
 */  
@ServerEndpoint("/websocket")  
public class WebSocketTest {  
      
    private static int onlineCount = 0; //当前在线连接数  
  
    //存放每个客户端对应的WebSocket对象  
    private static CopyOnWriteArraySet<WebSocketTest> webSocketSet = new CopyOnWriteArraySet<WebSocketTest>();  
    private Session session;//与某个客户端的连接会话，需要通过它来给客户端发送数据  
  
    /**  
     * 连接建立成功调用的方法  
     * @param session  可选的参数。session为与某个客户端的连接会话，需要通过它来给客户端发送数据  
     */  
    @OnOpen  
    public void onOpen(Session session){  
        this.session = session;  
        webSocketSet.add(this);     //加入set中  
        onlineCount++;           //在线数加1  
        System.out.println("有新连接加入！当前在线人数为" + getOnlineCount());  
    }  
  
    /**  
     * 连接关闭调用的方法  
     */  
    @OnClose  
    public void onClose(){  
        webSocketSet.remove(this);  //从set中删除  
        onlineCount--;           //在线数减1  
        System.out.println("有一连接关闭！当前在线人数为" + getOnlineCount());  
    }  
  
    /**  
     * 收到客户端消息后调用的方法  
     * @param message 客户端发送过来的消息  
     * @param session 可选的参数  
     */  
    @OnMessage  
    public void onMessage(String message, Session session) {  
        System.out.println("来自客户端的消息:" + message);  
        //群发消息  
        for(WebSocketTest item: webSocketSet){  
            try {  
                item.sendMessage(message);  
            } catch (IOException e) {  
                e.printStackTrace();  
                continue;  
            }  
        }  
    }  
  
    /**  
     * 发生错误时调用  
     * @param session  
     * @param error  
     */  
    @OnError  
    public void onError(Session session, Throwable error){  
        System.out.println("发生错误");  
        error.printStackTrace();  
    }  
  
    /**  
     * 这个方法与上面几个方法不一样。没有用注解，是根据自己需要添加的方法。  
     * @param message  
     * @throws IOException  
     */  
    public void sendMessage(String message) throws IOException{  
        this.session.getBasicRemote().sendText(message);  
        //this.session.getAsyncRemote().sendText(message);  
    }  
  
    public static synchronized int getOnlineCount() {  
        return onlineCount;  
    }  
  
    public static synchronized void subOnlineCount() {  
        WebSocketTest.onlineCount--;  
    }  
}  
```  
  
## 客户端  
```java  
public enum WebClient {  
    SINGLETON;  
      
    private static final String CHARSET = "UTF-8";  
    private WebSocketClient client = null;  
    private MsgListener listener;  
      
    public void startClient(URI serverUri) {  
        if (client != null && !client.isClosed()) {  
            System.out.println("客户端" + getName() + "已开启，不需要重复开启");  
        } else {  
            new Thread(() -> {  
                client = new WebSocketClient(serverUri) {  
                    @Override  
                    public void onOpen(ServerHandshake handshakedata) {  
                        System.out.println("onOpen，握手");  
                        Iterator<String> it = handshakedata.iterateHttpFields();  
                        while (it.hasNext()) {  
                            String key = it.next();  
                            System.out.println(key + ":" + handshakedata.getFieldValue(key));  
                        }  
                    }  
                      
                    @Override  
                    public void onMessage(String message) {  
                        System.out.println("onMessage，接收到消息【" + message + "】");  
                        if (listener != null) {  
                            listener.onReveiveMsg(message);  
                        }  
                    }  
                      
                    @Override  
                    public void onClose(int code, String reason, boolean remote) {  
                        System.out.println("onClose，关闭：code=" + code + "，reason=" + reason + "，remote=" + remote);  
                    }  
                      
                    @Override  
                    public void onError(Exception ex) {  
                        ex.printStackTrace();  
                    }  
                };  
                //client.connect(); //非阻塞方法  
                try {  
                    boolean success = client.connectBlocking();//阻塞方法  
                    System.out.println("客户端" + getName() + "连接" + (success ? "成功" : "失败"));  
                } catch (InterruptedException e) {  
                    e.printStackTrace();  
                    System.out.println("客户端" + getName() + "连接异常");  
                }  
            }).start();  
        }  
    }  
      
    public void sendMsg(String text) {  
        new Thread(() -> {  
            if (client != null && !client.isClosed()) {  
                try {  
                    String content = getName() + " : " + text;  
                    client.send(content.getBytes(CHARSET));  
                    if (listener != null) {  
                        listener.onSendMsg(text);  
                    }  
                } catch (IOException e) {  
                    e.printStackTrace();  
                }  
            }  
        }).start();  
    }  
      
    public void exit() {  
        new Thread(() -> {  
            if (client != null && !client.isClosed()) {  
                client.close();  
            }  
            System.out.println("客户端下线成功");  
        }).start();  
    }  
      
    public String getName() {  
        return "用户" + client.getLocalSocketAddress().getPort();  
    }  
      
    public void setListener(MsgListener listener) {  
        this.listener = listener;  
    }  
      
    public interface MsgListener {  
        void onReveiveMsg(String message);  
          
        void onSendMsg(String message);  
    }  
}  
```  
  
2018-11-23  
