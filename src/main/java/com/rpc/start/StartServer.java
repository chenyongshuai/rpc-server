package com.rpc.start;

import java.io.IOException;
import java.net.UnknownHostException;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.rpc.provider.ServiceProvider;
import com.rpc.provider.impl.ServiceProviderImpl;

/** @author:  v_chenyongshuai@:
  * @date:  2018年1月29日 下午1:54:00 
  * @version：   1.0.0
  * @describe:    
  */
public class StartServer {
	public static void main(String[] args) throws IOException {
		ApplicationContext ctx = new ClassPathXmlApplicationContext("applicationContext.xml");
		System.in.read();
	}
}
