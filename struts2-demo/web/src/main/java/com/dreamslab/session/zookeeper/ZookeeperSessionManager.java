
package com.dreamslab.session.zookeeper;

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

/**
 * 
 * 基于Zooker实现的SessionManager
 * 
 * @author gaofeng
 * @date Sep 12, 2013 3:47:38 PM
 * @id $Id$
 */
public class ZookeeperSessionManager extends AbstractSessionManager {
    
    private static final Logger log = LoggerFactory.getLogger(ZookeeperSessionManager.class);
    
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
    public ZookeeperSessionManager() {
    
        executor = Executors.newSingleThreadExecutor();
        executor.submit(new TimeoutCheckTask());
        
        client = ZookeeperSessionClient.getInstance();
        // 建立Zookeeper的根节点
        client.createSession(null);
        
        if (log.isInfoEnabled()) {
            log.info("创建SESSIONS组节点完成");
        }
        
    }
    
    /**
     * 这个方法存在线程安全性问题，必须加上同步机制 {@inheritDoc}
     */
    @Override
    public HttpSession getHttpSession(String id, HttpServletRequest request) {
    	//根据SessionId先从本地缓存中返回Session
    	DistributedSession session = (DistributedSession) super.getHttpSession(id, request);
        
    	//再根据SessionId调用Client的getSession方法返回远端的SessionMetaData
    	SessionMetaData metadata = client.getSession(id);
        
    	//远端不存在有效session的情况
        if (metadata == null || !metadata.isValid()) { 
            try {
                sessionLock.lock();
                if (session != null) { // 远端不存在有效session的情况，如果本地存在Session，需要做清理
                    session.invalidate(); // 此处存在Race Condition!!!
                }
                return null;
            } finally {
                sessionLock.unlock();
            }
        }
        //远端存在有效的session，则更新远端的Session
        client.updateSession(metadata);
        
        if (session == null) { // 远端存在有效session的情况，但本地还没有
        	DistributedSession sess = new DistributedSession(this, id, request); // 避免继续使用session变量
            sess.access();
            addHttpSession(sess);
            return sess;
        } else {
            session.setRequest(request); // 对于服务器端已存在的Session代理类，也需要重设Request对象！
            return session;
        }
    }
    
    @Override
    public HttpSession newHttpSession(HttpServletRequest request, HttpServletResponse response) {
    	// 获取新的Session ID
        String id = getNewSessionId(request); 
        // 构造一个Session
        DistributedSession sess = new DistributedSession(this, id, request);
        // 将SessionId写入Cookie
        Cookie cookie = CookieHelper.writeSessionIdToCookie(id, request, response, COOKIE_EXPIRY);
        // 如果Cookie不为null,则将其打印出来。
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
    
        ZookeeperPoolManager.getInstance().close();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public SessionClient getSessionClient() {
    
        return this.client;
    }
}
