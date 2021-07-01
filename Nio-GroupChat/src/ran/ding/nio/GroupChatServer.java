package ran.ding.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;

public class GroupChatServer {

    private Selector selector;
    private ServerSocketChannel listenChannel;
    private static final int PORT=6667;

    public GroupChatServer(){
        try {
            //选择器
            selector=Selector.open();
            listenChannel = ServerSocketChannel.open();
            //绑定端口
            listenChannel.socket().bind(new InetSocketAddress(PORT));
            //非阻塞
            listenChannel.configureBlocking(false);
            //注册到selector
            listenChannel.register(selector, SelectionKey.OP_ACCEPT);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void listen(){
        try {
            while (true){
                int count = selector.select();
                if(count>0){//有事件
                    Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                    while (iterator.hasNext()){
                        //取出selectionKey
                        SelectionKey key = iterator.next();
                        //监听到accept
                        if(key.isAcceptable()){
                            SocketChannel sc = listenChannel.accept();
                            sc.configureBlocking(false);
                            //把sc重新注册到selector
                            sc.register(selector,SelectionKey.OP_READ);
                            //提示
                            System.out.println(sc.getRemoteAddress()+" 上线 ");
                        }
                        if(key.isReadable()){//通道中是读事件
                            readDate(key);
                        }
                        //删除当前key
                        iterator.remove();
                    }
                }else {
                    System.out.println("等待......");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            //发生异常处理...
        }
    }
    //读取客户端消息
    public void readDate(SelectionKey key) {
        SocketChannel channel = null;
        try {
            //得到 channel
            channel = (SocketChannel) key.channel();
            //创建 buffer
            ByteBuffer buffer = ByteBuffer.allocate(1024);
            int count = channel.read(buffer);
            //根据 count 的值做处理
            if (count > 0) {
                //把缓存区的数据转成字符串
                String msg = new String(buffer.array());
                //输出该消息
                System.out.println("form客户端:" + msg);
                //向其它的客户端转发消息(去掉自己),专门写一个方法来处理
                sendInfoToOtherClients(msg, channel);
            }
        } catch (IOException e) {
            try {
                System.out.println(channel.getRemoteAddress() + "离线了..");
                //取消注册
                key.cancel();
                //关闭通道
                channel.close();
            } catch (IOException e2) {
                e2.printStackTrace();
            }
        }
    }
    //转发消息给其它客户(通道)
    public void sendInfoToOtherClients(String msg, SocketChannel self) throws IOException {
        System.out.println("服务器转发消息中...");
        //遍历所有注册到 selector 上的 SocketChannel,并排除 self
        for (SelectionKey key : selector.keys()) {
            //通过 key 取出对应的 SocketChannel
            Channel targetChannel = key.channel();
            //排除自己
            if (targetChannel instanceof SocketChannel && targetChannel != self) {
                //转型
                SocketChannel dest = (SocketChannel) targetChannel;
                //将 msg 存储到 buffer
                ByteBuffer buffer = ByteBuffer.wrap(msg.getBytes());
                //将 buffer 的数据写入通道
                dest.write(buffer);
            }
        }
    }
    public static void main(String[] args) {
        //创建服务器对象
        GroupChatServer groupChatServer = new GroupChatServer();
        groupChatServer.listen();
    }
}
