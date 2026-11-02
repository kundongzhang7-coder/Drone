package com.example.backend.util;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;

/**
 * 网络工具类：收集本机允许跨域的来源地址
 */
public final class NetworkUtils {

    private NetworkUtils() {
    }

    /**
     * 获取允许跨域的 Origin 集合：
     * localhost / 127.0.0.1 以及本机所有局域网 IPv4 地址（前端默认 5173 端口）
     */
    public static Set<String> getAllowedOrigins() {
        Set<String> origins = new HashSet<>();
        // 固定允许 localhost 和 127.0.0.1（前端默认端口 5173）
        origins.add("http://localhost:5173");
        origins.add("http://127.0.0.1:5173");

        // 动态获取本机所有局域网 IPv4 地址（非回环、非虚拟网卡）
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface ni = interfaces.nextElement();
                // 跳过虚拟网卡、回环接口、未启用的接口
                if (ni.isLoopback() || !ni.isUp()) continue;
                Enumeration<InetAddress> addresses = ni.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    InetAddress addr = addresses.nextElement();
                    // 只取 IPv4 地址，并且是私有地址范围
                    if (addr.isSiteLocalAddress() && !addr.isLoopbackAddress()) {
                        String ip = addr.getHostAddress();
                        // 前端默认运行在 5173 端口
                        origins.add("http://" + ip + ":5173");
                    }
                }
            }
        } catch (SocketException e) {
            // 如果获取失败，至少保留 localhost
            e.printStackTrace();
        }
        return origins;
    }
}
