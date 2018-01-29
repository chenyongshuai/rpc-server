package com.rpc.start;

import java.io.IOException;
import java.net.UnknownHostException;

import com.rpc.provider.ServiceProvider;
import com.rpc.provider.impl.ServiceProviderImpl;

/** @author:  v_chenyongshuai@:
  * @date:  2018年1月29日 下午1:54:00 
  * @version：   1.0.0
  * @describe:    
  */
public class StartServer {
	public static void main(String[] args) throws IOException {
		ServiceProvider sp = new ServiceProviderImpl(9999);
		sp.init();
		System.in.read();
		sp.close();
	}
}
