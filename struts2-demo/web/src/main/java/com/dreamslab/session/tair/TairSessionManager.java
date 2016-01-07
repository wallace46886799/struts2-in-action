
package com.dreamslab.session.tair;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dreamslab.session.AbstractSessionManager;
import com.dreamslab.session.Configuration;
import com.dreamslab.session.DistributedSession;
import com.dreamslab.session.SessionClient;
import com.dreamslab.session.SessionMetaData;
import com.dreamslab.session.helper.CookieHelper;
import com.dreamslab.session.zookeeper.TimeoutCheckTask;
import com.dreamslab.session.zookeeper.ZookeeperSessionClient;

/**
 * 
 * 基于Local实现的SessionManager
 * 
 * @author gaofeng
 * @date Sep 12, 2013 3:47:38 PM
 * @id $Id$
 */
public class TairSessionManager extends AbstractSessionManager {
    
    private static final Logger log = LoggerFactory.getLogger(TairSessionManager.class);
    
    /** ZK客户端操作 */
    private SessionClient client;
    
    /** 定时任务执行器 */
    private ExecutorService executor;
    
    private Lock sessionLock = new ReentrantLock();
    
    /**
     * 构造方法
     * N.B. 访问权限设置为public是为了让core包通过反射后初始化
     * 
     * @param config
     */
    public TairSessionManager() {
    
        executor = Executors.newSingleThreadExecutor();
        executor.submit(new TimeoutCheckTask());
        if (log.isInfoEnabled()) {
            log.info("创建SESSIONS组节点完成");
        }
        
    }
    
    /**
     * 这个方法存在线程安全性问题，必须加上同步机制 {@inheritDoc}
     */
    @Override
    public HttpSession getHttpSession(String id, HttpServletRequest request) {
    	return null;
    }
    
    @Override
    public HttpSession newHttpSession(HttpServletRequest request, HttpServletResponse response) {
    
        String id = getNewSessionId(request); // 获取新的Session ID
        DistributedSession sess = new DistributedSession(this, id, request);
        
        // 写cookie
        Cookie cookie = CookieHelper.writeSessionIdToCookie(id, request, response, COOKIE_EXPIRY);
        if (cookie != null) {
            if (log.isInfoEnabled()) {
                log.info("Write tsid to Cookie,name:[" + cookie.getName() + "],value:[" + cookie.getValue() + "]");
            }
        }
        // 创建元数据
        SessionMetaData metadata = new SessionMetaData();
        metadata.setId(id);
        int sessionTimeout = Configuration.getSessionTimeout();
        metadata.setMaxIdle(sessionTimeout * 60 * 1000); // 转换成毫秒
        // 在ZooKeeper服务器上创建session节点，节点名称为Session ID
        client.createSession(metadata);
        
        addHttpSession(sess);
        return sess;
    }
    
    @Override
    public void close() {
    
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public SessionClient getSessionClient() {
    
        return this.client;
    }
}
