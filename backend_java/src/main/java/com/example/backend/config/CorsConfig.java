package com.example.backend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.lang.NonNull;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(@NonNull CorsRegistry registry) {
        // 获取本机所有可能的局域网 IP 地址（如 192.168.x.x, 10.x.x.x, 172.16.x.x）
        Set<String> allowedOrigins = getAllowedOrigins();

        registry.addMapping("/api/**")
                .allowedOrigins(allowedOrigins.toArray(new String[0]))
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);

        registry.addMapping("/api1/**")
                .allowedOrigins(allowedOrigins.toArray(new String[0]))
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);

    }

    private Set<String> getAllowedOrigins() {
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