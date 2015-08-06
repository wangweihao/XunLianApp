import com.sun.xml.internal.ws.api.message.Packet;
import io.netty.buffer.*;
import io.netty.channel.*;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.util.ReferenceCountUtil;
import org.json.JSONObject;

import java.net.SocketAddress;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SocketChannel;
import java.sql.Connection;
import java.util.Base64;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by wwh on 15-7-25.
 */
//public class XlServerHandler extends ChannelHandlerAdapter {
public class XlServerHandler extends ChannelHandlerAdapter{
    private static final Logger logger = Logger.getLogger(
            XlServerHandler.class.getName()
    );

    XlServerHandler(){
        System.out.println("ServerHandler被创建");
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg)throws Exception{
        System.out.println("发送msg");
        ByteBuf buf = (ByteBuf)msg;
        byte[] req = new byte[buf.readableBytes()];
        buf.readBytes(req);
        String body = new String(req, "UTF-8");
        System.out.println("server收到：" + body);
        String s = "hello world\n\0";
        //Connection con  = XlDbPoll.getConnection();
        ByteBuf resp = Unpooled.copiedBuffer(req);
        //ctx.write(resp);
        byte[] bt = s.getBytes();
        ByteBuf rett = Unpooled.copiedBuffer(bt);
        ctx.write(rett);
    }

/*
    public void channelRead(ChannelHandlerContext ctx, Object msg)throws Exception{
        ByteBuf in = (ByteBuf)msg;
        String s = "";
        try{
            while(in.isReadable()){
                s += ((char)in.readByte());
                System.out.flush();
            }
        }finally {
            ReferenceCountUtil.release(msg);
        }
        System.out.println(s);
    }
*/
    /* 将write数据先写到缓冲区中，等到complete状态再刷新缓冲区 */
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception{
        ctx.flush();
    }

    /* 有错误时打印错误并且关闭handler*/
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause){
        logger.log(Level.WARNING, "unexcepted exception from downstream", cause);
        ctx.close();
    }

    /* 用户连接到服务器时处理，说明是Active的 */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception{
        System.out.println("用户连接服务器");
    }

    /* 判断用户连接是否断开*/
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception{
        ctx.fireChannelInactive();
        System.out.println("用户断开连接");
    }

}