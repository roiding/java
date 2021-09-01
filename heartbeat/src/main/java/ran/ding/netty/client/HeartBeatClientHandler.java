package ran.ding.netty.client;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.springframework.stereotype.Component;

/**
 * @author roiding
 * @date 2021/9/1 0001 9:25
 * @Description:
 */
@Component
public class HeartBeatClientHandler extends SimpleChannelInboundHandler<String> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
        System.out.println(" client received :" + msg);
        if (msg != null && msg.equals("idle close")) {

            System.out.println(" 服务端关闭连接，客户端也关闭");
            ctx.channel().closeFuture();
        }
    }
}
