package com.rpc.service.impl;

import java.util.Date;

import com.rpc.service.HelloWorldService;

/** @author:  v_chenyongshuai@:
  * @date:  2018年1月29日 下午3:16:53 
  * @version：   1.0.0
  * @describe:    
  */
public class HelloWorldServiceImpl implements HelloWorldService {

	public String sayHi(String name) {
		return "Hi!!!"+name+","+new Date();
	}

}
