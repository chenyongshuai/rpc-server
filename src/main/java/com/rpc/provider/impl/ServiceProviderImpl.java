package com.rpc.provider.impl;

import java.lang.reflect.Method;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

import com.rpc.protocol.MethodInvokeMeta;
import com.rpc.protocol.MethodInvokeMetaWarp;
import com.rpc.protocol.Result;
import com.rpc.protocol.ResultWarp;
import com.rpc.provider.ServiceProvider;
import com.rpc.registry.Registry;
import com.rpc.serializer.ObjectDecoder;
import com.rpc.serializer.ObjectEncoder;
import com.rpc.service.HelloWorldService;
import com.rpc.service.impl.HelloWorldServiceImpl;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;

/** @author:  v_chenyongshuai@:
  * @date:  2018年1月26日 下午3:34:26 
  * @version：   1.0.1
  * @describe:     服务端   服务提供方  接口实现类
  */
public class ServiceProviderImpl implements ServiceProvider {
	/**端口*/
    private int port = 9999;
    /**server端引导类*/
    private ServerBootstrap sbt;
    /**接受响应线程池*/
    private NioEventLoopGroup boss;
    /**读写IO线程池*/
    private NioEventLoopGroup worker;
    /**充当接口和实现类的工厂*/
    private Map<Class,Object>beanFactory;
    /**注册中心*/
    private Registry registry;
    
	public ServiceProviderImpl(int port) {
		this.port = port;
	}

	public Map<Class, Object> getBeanFactory() {
		return beanFactory;
	}

	public void setBeanFactory(Map<Class, Object> beanFactory) {
		this.beanFactory = beanFactory;
	}
	/**
	 * 初始化方法
	 */
	public void init() throws UnknownHostException {
		// 1.创建程序引导类
        sbt = new ServerBootstrap();
        // 2.创建线程池
        boss = new NioEventLoopGroup();
        worker = new NioEventLoopGroup();
        // 3.将引导类与线程池关联
        sbt.group(boss,worker);
        // 4.创建管道
        sbt.channel(NioServerSocketChannel.class);
        //===================
        beanFactory = new HashMap<Class, Object>();
        beanFactory.put(HelloWorldService.class, new HelloWorldServiceImpl());
        start();
	}
	/**
	 * 配置管道
	 */
	public void start() throws UnknownHostException {
		// 5.初始化管道配置
		sbt.childHandler(new ChannelInitializer<SocketChannel>() {
			protected void initChannel(SocketChannel socketChannel) throws Exception{
				// 5.1  获取管道
				ChannelPipeline pipeline = socketChannel.pipeline();
				// 5.2  解帧
				pipeline.addLast(new LengthFieldBasedFrameDecoder(65535,0,2,0,2));
				// 5.3 解码
				pipeline.addLast(new ObjectDecoder());
				// 5.4 设置帧头
				pipeline.addLast(new LengthFieldPrepender(2));
				// 5.5 编码
				pipeline.addLast(new ObjectEncoder());
				pipeline.addLast(new ChannelHandlerAdapter(){
					@Override
					/**
					 * 异常捕获方法
					 */
					public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
						System.out.println("Exception(服务器端):  "+cause.toString());
					}
					@Override
					/**
					 * 读取客户端消息
					 */
					public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
						System.out.println("@9999调用服务");
						System.out.println("Receive Message(服务器端收到消息):  "+msg);
						// (1)强转成MethodInvokeMetaWarp
						MethodInvokeMetaWarp mimw = (MethodInvokeMetaWarp)msg;
						// (2)获取MethodInvokeMeta
						MethodInvokeMeta methodInvokeMeta = mimw.getMethodInvokeMeta();
						// (3)获取接口名
						Class targetClass = methodInvokeMeta.getTargetClass();
						System.out.println(targetClass);
						// (4)通过工厂获取类对象
						Object object = beanFactory.get(targetClass);
						// (5)通过反射调用方法
						Method method = object.getClass().getDeclaredMethod(methodInvokeMeta.getMethodName(),methodInvokeMeta.getParameterTypes());
						if(!method.isAccessible()){
							method.setAccessible(true);
						}
						// (6)调用方法 ，注入结果
						Result result = new Result();
						try {
							Object res = method.invoke(object, methodInvokeMeta.getParameters());
							result.setResult(res);
						} catch (Exception e) {
							result.setException(e);
						}
						ResultWarp resultWarp = new ResultWarp();
						resultWarp.setResult(result);
						// ***写数据到客户端
						ChannelFuture channelFuture = ctx.writeAndFlush(resultWarp);
                        channelFuture.addListener(ChannelFutureListener.CLOSE);
                        channelFuture.addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
                        channelFuture.addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
					}
				});
			}
		});
		new Thread(){
            @Override
            public void run() {
                try {
                    System.out.println("服务器开始监听@"+port+"~!!~!~~!~");
                    //6.绑定端口号
                    ChannelFuture channelFuture = sbt.bind(port).sync();
                    //7. 等待服务关闭
                    channelFuture.channel().closeFuture().sync();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }.start();
	}
	/**
	 * 释放资源 
	 */
	public void close() throws UnknownHostException {
		System.out.println("服务端：close方法被调用！！！");
        //registry.deleteNode(IUserService.class,new HostAndPort(InetAddress.getLocalHost().getHostAddress(),port));
        if(boss!=null){
            boss.shutdownGracefully();
        }
        if(worker!=null){
            worker.shutdownGracefully();
        }
	}

}
