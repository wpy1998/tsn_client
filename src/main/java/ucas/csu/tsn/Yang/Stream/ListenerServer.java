package ucas.csu.tsn.Yang.Stream;

import ucas.csu.tsn.NettyAPI.RpcDecoder;
import ucas.csu.tsn.NettyAPI.RpcEncoder;
import ucas.csu.tsn.NettyAPI.RpcRequest;
import ucas.csu.tsn.NettyAPI.RpcResponse;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.Builder;
import lombok.NonNull;
import ucas.csu.tsn.RestfulAPI.RestfulDeleteInfo;
import ucas.csu.tsn.RestfulAPI.RestfulPutInfo;

public class ListenerServer {
    private final int port;
    private Header header;
    private String url;
    private Thread serverThread;
    private ListenerServerHandler handler;
    private boolean isRegister;

    @Builder
    public ListenerServer(@NonNull int port,
                          Header header,
                          String url) {
        this.port = port;
        this.header = header;
        this.url = url;
        if (this.header == null || this.url == null){
            this.isRegister = false;
        }else {
            this.isRegister = true;
        }
    }

    private void initServer(){
        handler = new ListenerServerHandler();
        serverThread = new Thread(new Runnable() {
            EventLoopGroup bossGroup, workerGroup;
            ChannelFuture future;

            @Override
            public void run() {
                try{
                    int resultCode = join_listener();

                    if(resultCode >= 400 || resultCode < 100){
                        System.out.println("<TSN Client listenerServer> listener register failed.");
                        return;
                    }

                    bossGroup = new NioEventLoopGroup();
                    workerGroup = new NioEventLoopGroup();

                    ServerBootstrap b = new ServerBootstrap();
                    b.group(bossGroup, workerGroup)
                            .channel(NioServerSocketChannel.class)
                            .childOption(ChannelOption.SO_KEEPALIVE, true)
                            .option(ChannelOption.SO_BACKLOG, 128)
                            .childHandler(new ChannelInitializer<SocketChannel>() {
                                @Override
                                protected void initChannel(SocketChannel socketChannel){
                                    socketChannel.pipeline()
                                            .addLast(new RpcDecoder(RpcRequest.class))
                                            .addLast(new RpcEncoder(RpcResponse.class))
                                            .addLast(handler);
                                }
                            });
                    future = b.bind(port).sync();
                    if (future.isSuccess()){
                        System.out.println("<TSN Client listenerServer> Server Start Successful.");
                    }else {
                        System.out.println("<TSN Client listenerServer> Server Start Failure.");
                        stopServer();
                    }
                    future.channel().closeFuture().sync();
                }catch (InterruptedException e){
                    stopServer();
                    System.out.println("<TSN Client listenerServer> Thread: ServerThread" +
                            " interrupted.");
                }
            }

            public void stopServer(){
                if (bossGroup != null)
                    bossGroup.shutdownGracefully();
                if (workerGroup != null)
                    workerGroup.shutdownGracefully();
                leave_listener();
            }
        });
    }

    public void start(){
        if (serverThread == null){
            initServer();
        }
        serverThread.start();
    }

    public void stop(){
        if (serverThread != null){
            serverThread.interrupt();
        }
    }

    private int join_listener(){
        if (!this.isRegister){
            return 200;
        }
        String url = this.url + header.getKey();
//        System.out.println(url);
        RestfulPutInfo restfulPutInfo = RestfulPutInfo.builder().url(url).build();

        JSONObject joinStream = header.getJSONObject(true, false,
                true, false, false,
                true, true);
        JSONArray streams = new JSONArray();
        streams.add(joinStream);
        JSONObject device = new JSONObject();
        device.put("stream-list", streams);
        System.out.println("<TSN Client listenerServer> register listener to controller.");
        return restfulPutInfo.putInfo(device.toString());
    }

    private int leave_listener(){
        if (!this.isRegister){
            return 200;
        }
        String url = this.url + header.getKey();
        RestfulDeleteInfo restfulDeleteInfo = RestfulDeleteInfo.builder().url(url).build();
        System.out.println("<TSN Client listenerServer> remove listener from controller.");
        return restfulDeleteInfo.deleteInfo();
    }
}
