
package com.dreamslab.session;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dreamslab.session.helper.CookieHelper;
import com.dreamslab.session.helper.SessionIdGenerator;

/**
 * 
 * Session管理器抽象实现
 *
 * @author gaofeng
 * @date Sep 18, 2013 2:26:52 PM
 * @id $Id$
 */
public abstract class AbstractSessionManager implements SessionManager {
    
    protected static final Logger log = LoggerFactory.getLogger(AbstractSessionManager.class);
    /** 本地的session容器 */
    private Map<String, DistributedSession> sessions;
    
    private SessionIdGenerator sessionIdGenerator;
    private ServletContext sc;
    
    /**
     * 构造方法
     * 
     * @param sc
     */
    public AbstractSessionManager() {
        if (sessions == null) {
            sessions = new ConcurrentHashMap<String, DistributedSession>();
        }
        if (sessionIdGenerator == null) {
            sessionIdGenerator = SessionIdGenerator.getInstance();
        }
    }
    
    public void close(){
    
        if (sessions != null) {
            for (HttpSession s : sessions.values()) {
                s.invalidate();
            }
            sessions.clear();
        }
        sessions = null;
    }
    
    @Override
    public HttpSession getHttpSession(String id, HttpServletRequest request) {
    
        return sessions.get(id);
    }
    
    @Override
    public ServletContext getServletContext() {
    
        return sc;
    }
    
    @Override
    public void setServletContext(ServletContext sc) {
    
        this.sc = sc;
    }
    
    @Override
    public String getNewSessionId(HttpServletRequest request) {
    
        if (sessionIdGenerator != null) {
            return sessionIdGenerator.newSessionId(request);
        }
        return null;
    }
    
    /**
     * 查找指定的Cookie
     * 
     * @param request
     * @return
     */
    @Override
    public String getRequestSessionId(HttpServletRequest request) {
    	//如果tc.tsid不为空则直接返回，否则从CookieHelper中获取tc.tsid
        if(request.getAttribute("tc.tsid") != null){
            return request.getAttribute("tc.tsid").toString();
        }
        return CookieHelper.findSessionId(request);
    }
    
    @Override
    public void removeHttpSession(DistributedSession session) {
    
        if (session != null) {
            String id = session.getId();
            if (StringUtils.isNotBlank(id)) {
                sessions.remove(id);
            }
        }
    }
    
    /**
     * 加入一个受管理的Session对象
     * 
     * @param session
     * @param request
     */
    @Override
    public void addHttpSession(DistributedSession session) {
    
        if (session == null) {
            return;
        }
        String id = session.getId();
        if (!sessions.containsKey(id)) {
            sessions.put(id, session);
        }
    }
    
}
