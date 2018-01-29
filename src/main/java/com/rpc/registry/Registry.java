package com.rpc.registry;


import java.io.IOException;
import java.util.List;

import com.rpc.common.HostAndPort;

/**
 * Created by Max Chen on 2017/12/1 14:16.
 * 注册中心接口  zookeeper  redis....
 *
 */
public interface Registry {
    public static final String PREFIX = "/RPC";
    public static final String SUFFIX = "/providers";
    /**
     * 1. 查找
     * 2. 注册
     * 3. 监控
     */
    public List<HostAndPort> searchService(Class targetClass);
    public void registerService(Class targetClass, HostAndPort hostAndPort);
    public List<HostAndPort> subscrible(Class targetClass, List<HostAndPort> hostAndPorts) throws IOException;
    public void deleteNode(Class targetClass, HostAndPort hostAndPort);
    public void close();
}
