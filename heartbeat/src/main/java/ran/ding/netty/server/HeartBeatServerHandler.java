package ran.ding.netty.server;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleStateEvent;
import org.springframework.stereotype.Component;

/**
 * @author roiding
 * @date 2021/9/1 0001 8:49
 * @Description:
 */
@Component
@ChannelHandler.Sharable
public class HeartBeatServerHandler extends SimpleChannelInboundHandler<String> {
    // 记录读超时几次了，用来判断是否断开该连接
    int readIdleTimes = 0;
    String heartStr="";
    /*
     * Channel 收到消息后触发
     *
     * 注：心跳包说白了就是一个某些地方特殊的数据包
     * 	  所以这里我们规定，如果消息内容是 "Heartbeat Packet"，那么它就是一个心跳包
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String s) throws Exception {
        // 将收到的消息打印出来
        System.out.println(" ====== > [server] message received : " + s);
        // 如果消息内容是 Heartbeat Packet 则说明这是一个心跳包，我们返回 ok

        ctx.channel().writeAndFlush("ok");
        readIdleTimes=0;
        heartStr=s;
    }
    /**
     * 用户事件触发
     *
     * 当 IdleStateHandler 发现读超时后，会调用 fireUserEventTriggered() 去执行后一个 Handler 的 userEventTriggered 方法。
     * 所以，根据心跳检测状态去关闭连接的就写在这里！
     */
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {

        // 入站的消息就是 IdleStateEvent 具体的事件
        IdleStateEvent event = (IdleStateEvent) evt;

        String eventType = null;
        // 我们在 IdleStateHandler 中也看到了，它有读超时，写超时，读写超时等
        // 所以，这里我们需要判断事件类型
        switch (event.state()) {

            case READER_IDLE:
                eventType = "读空闲";
                readIdleTimes++; // 读空闲的计数加 1
                break;
            case WRITER_IDLE:
                eventType = "写空闲";
                break; // 不处理
            case ALL_IDLE:
                eventType = "读写空闲";
                break; // 不处理
        }

        // 打印触发了一次超时警告
        System.out.println(ctx.channel().remoteAddress() + "超时事件：" + eventType);

        // 当读超时超过 3 次，我们就端口该客户端的连接
        // 注：读超时超过 3 次，代表起码有 4 次 3s 内客户端没有发送心跳包或普通数据包
        if (readIdleTimes > 3) {

            System.out.println(" [server]读空闲超过3次，关闭连接，释放更多资源");
            ctx.channel().writeAndFlush("idle close");
            ctx.channel().close(); // 手动断开连接
            //TODO  设备置于离线状态

        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {

        System.err.println("=== " + ctx.channel().remoteAddress() + " is active ===");
    }
}
