package com.rpc.provider;

import java.net.UnknownHostException;

/** @author:  v_chenyongshuai@:
  * @date:  2018年1月26日 下午3:30:36 
  * @version：   1.0.1
  * @describe:    服务端   服务提供方  接口
  */
public interface ServiceProvider {
	/**
	 * 初始化方法
	 * @throws UnknownHostException
	 */
	void init() throws UnknownHostException;
	/**
	 * 开启服务方法
	 * @throws UnknownHostException
	 */
    void start() throws UnknownHostException;
    /**
     * 释放资源
     * @throws UnknownHostException
     */
    void close() throws UnknownHostException;
}
