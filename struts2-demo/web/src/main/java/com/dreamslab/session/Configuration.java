
package com.dreamslab.session;

import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * TODO 类的功能描述。
 * 
 * @author gaofeng
 * @date Sep 12, 2013 4:31:52 PM
 * @id $Id$
 */
public final class Configuration {
    
    private static final Logger log = LoggerFactory.getLogger(Configuration.class);
    
    private static final String config_file = "session.properties";
    
    /** zk服务器地址 */
    private static String servers;
    
    /** 初始连接数 */
    private static int maxIdle;
    
    /** zk连接池中，最小的空闲连接数 */
    private static int initIdleCapacity;
    
    /** session的生命周期 单位分钟 */
    private static int sessionTimeout;
    
    /** 和zk服务器建立的连接的超时时间 单位秒 */
    private static int connectionTimeout;
    
    /** 检查任务的启动周期 单位秒 */
    private static int timeoutCheckInteval;
    
    /** cookie expiry */
    private static int cookieExpiry;
    
    private static final int defaultMaxIdle = 8;
    
    private static final int defaultInitIdleCapacity = 4;
    
    private static final int defaultSessionTimeout = 30;
    
    private static final int defaultConnectionTimeout = 60;
    
    private static final int defaultTimeoutCheckInterval = 30;
    
    private static final int defaultCookieExpiry = 0;
    
    static {
        InputStream in = Configuration.class.getClassLoader().getResourceAsStream(config_file);
        Properties props = new Properties();
        try {
            props.load(in);
            servers = props.getProperty("tc.session.servers");
            maxIdle = NumberUtils.toInt(props.getProperty("tc.session.max_idle"), defaultMaxIdle);
            initIdleCapacity = NumberUtils.toInt(props.getProperty("tc.session.init_idle_capacity"), defaultInitIdleCapacity);
            sessionTimeout = NumberUtils.toInt(props.getProperty("tc.session.session_timeout"), defaultSessionTimeout);
            connectionTimeout = NumberUtils.toInt(props.getProperty("tc.session.connection_timeout"), defaultConnectionTimeout);
            timeoutCheckInteval = NumberUtils.toInt(props.getProperty("tc.session.timeout_check_interval"), defaultTimeoutCheckInterval);
            cookieExpiry = NumberUtils.toInt(props.getProperty("tc.session.cookie_expiry"), defaultCookieExpiry);
        } catch (Exception e) {
            log.error("读取session配置文件时出错", e);
        }
    }
    
    private Configuration(){
        
    }
    
    public static String getServers() {
    
        return servers;
    }
    
    public static void setServers(String servers) {
    
        Configuration.servers = servers;
    }
    
    public static int getMaxIdle() {
    
        return maxIdle;
    }
    
    public static void setMaxIdle(int maxIdle) {
    
        Configuration.maxIdle = maxIdle;
    }
    
    public static int getInitIdleCapacity() {
    
        return initIdleCapacity;
    }
    
    public static void setInitIdleCapacity(int initIdleCapacity) {
    
        Configuration.initIdleCapacity = initIdleCapacity;
    }
    
    public static int getSessionTimeout() {
    
        return sessionTimeout;
    }
    
    public static void setSessionTimeout(int sessionTimeout) {
    
        Configuration.sessionTimeout = sessionTimeout;
    }
    
    public static int getConnectionTimeout() {
    
        return connectionTimeout;
    }
    
    public static void setConnectionTimeout(int connectionTimeout) {
    
        Configuration.connectionTimeout = connectionTimeout;
    }
    
    public static int getTimeoutCheckInteval() {
    
        return timeoutCheckInteval;
    }
    
    public static void setTimeoutCheckInteval(int timeoutCheckInteval) {
    
        Configuration.timeoutCheckInteval = timeoutCheckInteval;
    }
    
    public static int getCookieExpiry() {
    
        return cookieExpiry;
    }
    
    public static void setCookieExpiry(int cookieExpiry) {
    
        Configuration.cookieExpiry = cookieExpiry;
    }
    
}
