
package com.dreamslab.session;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;
import javax.servlet.http.HttpSessionContext;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * HttpSession的代理类。实际的操作通过远端访问完成
 * 
 * @author gaofeng
 * @date Sep 12, 2013 4:12:46 PM
 * @id $Id$
 */
@SuppressWarnings("deprecation")
public class DistributedSession implements HttpSession {
    
    private static final Logger log = LoggerFactory.getLogger(DistributedSession.class);
    
    /** Session管理器 */
    private SessionManager sessionManager;
    /** Session ID */
    private String id;
    /** Session创建时间 */
    private long createTime;
    /** Session最后一次访问时间 */
    private long lastAccessTime;
    /** Session的最大空闲时间间隔 */
    private int maxInactiveInterval;
    /** 是否是新建Session */
    private boolean isNew;
    
    private HttpServletRequest request;
    
    private WeakHashMap<HttpServletRequest, Map<String, Object>> cache;
    
    private static final String NULL_VALUE = "__NULL_";
    
    /**
     * 构造方法,指定ID
     * 
     * @param sessionManager
     * @param id
     */
    public DistributedSession(SessionManager sessionManager, String id, HttpServletRequest request) {
    
        this.sessionManager = sessionManager;
        this.createTime = System.currentTimeMillis();
        this.lastAccessTime = this.createTime;
        this.isNew = true;
        this.request = request;
        cache = new WeakHashMap<HttpServletRequest, Map<String, Object>>();
        this.id = id;
    }
    
    @Override
    public long getCreationTime() {
    
        return createTime;
    }
    
    @Override
    public String getId() {
    
        return id;
    }
    
    @Override
    public long getLastAccessedTime() {
    
        return lastAccessTime;
    }
    
    @Override
    public ServletContext getServletContext() {
    
        return sessionManager.getServletContext();
    }
    
    @Override
    public void setMaxInactiveInterval(int interval) {
    
        this.maxInactiveInterval = interval;
    }
    
    @Override
    public int getMaxInactiveInterval() {
    
        return maxInactiveInterval;
    }
    
    @Override
    public HttpSessionContext getSessionContext() {
    
        return null;
    }
    
    @Override
    public Object getValue(String name) {
    
        return getAttribute(name);
    }
    
    @Override
    public void putValue(String name, Object value) {
    
        setAttribute(name, value);
    }
    
    @Override
    public void removeValue(String name) {
    
        removeAttribute(name);
    }
    
    public void setRequest(HttpServletRequest request) {
    
        this.request = request;
    }
    
    @SuppressWarnings("rawtypes")
    @Override
    public String[] getValueNames() {
    
        List<String> names = new ArrayList<String>();
        Enumeration n = getAttributeNames();
        while (n.hasMoreElements()) {
            names.add((String) n.nextElement());
        }
        return names.toArray(new String[] {});
    }
    
    @Override
    public boolean isNew() {
    
        return isNew;
    }
    
    /**
     * 被访问
     */
    public void access() {
    
        this.isNew = false;
        this.lastAccessTime = System.currentTimeMillis();
    }
    
    /**
     * 触发Session的事件
     * 
     * @param value
     */
    protected void fireHttpSessionBindEvent(String name, Object value) {
    
        // 处理Session的监听器
        if (value instanceof HttpSessionBindingListener) {
            HttpSessionBindingEvent event = new HttpSessionBindingEvent(this, name, value);
            ((HttpSessionBindingListener) value).valueBound(event);
        }
    }
    
    /**
     * 触发Session的事件
     * 
     * @param value
     */
    protected void fireHttpSessionUnbindEvent(String name, Object value) {
    
        // 处理Session的监听器
        if (value instanceof HttpSessionBindingListener) {
            HttpSessionBindingEvent event = new HttpSessionBindingEvent(this, name, value);
            ((HttpSessionBindingListener) value).valueUnbound(event);
        }
    }
    
    public boolean isValid() {
    
        return lastAccessTime + maxInactiveInterval > System.currentTimeMillis();
    }
    
    @Override
    public Object getAttribute(String name) {
    
        access();
        Map<String, Object> rCache = cache.get(request);
        if (rCache != null) {
            Object v = rCache.get(this.id + "_" + name);
            if (v != null) {
                return NULL_VALUE.equals(v.toString()) ? null : v;
            }
        } else {
            rCache = new HashMap<String, Object>();
            cache.put(request, rCache);
        }
        
        // 获取session ID
        String sessionId = getId();
        if (StringUtils.isNotBlank(sessionId)) {
            // 返回Session节点下的数据
            SessionClient client = sessionManager.getSessionClient();
            Object o = client.getAttribute(sessionId, name);
            rCache.put(sessionId + "_" + name, o == null ? NULL_VALUE : o);
            return o;
            
        }
        rCache.put(this.id + "_" + name, NULL_VALUE);
        return null;
    }
    
    @SuppressWarnings("rawtypes")
    @Override
    public Enumeration getAttributeNames() {
    
        access();
        // 获取session ID
        String sessionId = getId();
        if (StringUtils.isNotBlank(sessionId)) {
            // 返回Session节点下的数据名字
            try {
                SessionClient client = sessionManager.getSessionClient();
                List<String> names = client.getAttributeNames(sessionId);
                if (names != null) {
                    return Collections.enumeration(names);
                }
            } catch (Exception ex) {
                log.error("调用getAttributeNames方法时发生异常，", ex);
            }
        }
        return null;
    }
    
    @Override
    public void setAttribute(String name, Object value) {
    
        // 没有实现序列化接口的直接返回
        if (!(value instanceof Serializable)) {
            log.warn("对象[" + value + "]没有实现Serializable接口，无法保存到分布式Session中");
            return;
        }
        access();
        // 获取session ID
        String sessionId = getId();
        if (StringUtils.isNotBlank(sessionId)) {
            // 将数据添加到ZooKeeper服务器上
            SessionClient client = sessionManager.getSessionClient();
            client.setAttribute(sessionId, name, (Serializable) value);
        }
        clearCache(name);
        // 处理Session的监听器
        fireHttpSessionBindEvent(name, value);
    }
    
    @Override
    public void removeAttribute(String name) {
    
        access();
        Object value = null;
        // 获取session ID
        String sessionId = getId();
        if (StringUtils.isNotBlank(sessionId)) {
            // 删除Session节点下的数据
            SessionClient client = sessionManager.getSessionClient();
            client.removeAttribute(sessionId, name);
            this.request.setAttribute(this.id + "_" + name, NULL_VALUE);
        }
        clearCache(name);
        // 处理Session的监听器
        fireHttpSessionUnbindEvent(name, value);
    }
    
    private void clearCache(String attrName){
        Map<String, Object> rCache = cache.get(request);
        if (rCache != null) {
            Object v = rCache.remove(this.id + "_" + attrName);
            if (v != null) {
                log.debug("remove object from cache successfully. attrName: " + attrName);
            }
        }
    }
    
    @Override
    public void invalidate() {
    
        // 获取session ID
        String sessionId = getId();
        if (StringUtils.isNotBlank(sessionId)) {
            // 删除Session节点
            try {
                SessionClient client = sessionManager.getSessionClient();
                Map<String, Object> sessionMap = client.removeSession(sessionId);
                if (sessionMap != null && sessionMap.size() > 0) {
                    for(Map.Entry<String, Object> entry : sessionMap.entrySet()){
                        String key = entry.getKey();
                        Object value = entry.getValue();
                        fireHttpSessionUnbindEvent(key, value);
                    }
                }
                if(log.isInfoEnabled()){
                    log.info("Invalidate session: " + sessionId + " successfully");
                }
            } catch (Exception ex) {
                log.error("调用invalidate方法时发生异常，", ex);
            }
        }
        // 删除本地容器中的Session对象
        sessionManager.removeHttpSession(this);
    }
    
}
