
package com.dreamslab.session.local;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dreamslab.session.SessionClient;
import com.dreamslab.session.SessionMetaData;

/**
 * 
 * 基于本地内存的session访问客户端实现
 * 
 * @author Frank
 * @date Sep 13, 2013 9:17:40 AM
 * @id $Id$
 */
public final class LocalSessionClient implements SessionClient {
    
    
    /** 日志 */
    private static final Logger log = LoggerFactory.getLogger(LocalSessionClient.class);
    
    /** 单例对象 */
    private static SessionClient instance;
    
    
    private Map<String,Object> sessions = new WeakHashMap<String,Object>();
    
    
    /**
     * 构造方法
     */
    private LocalSessionClient() {
    
    }
    
    /**
     * 返回单例方法
     * 
     * @return
     */
    public static synchronized SessionClient getInstance() {
    	instance = new LocalSessionClient();
    	return instance;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public SessionMetaData getSession(String sessionid) {
    	return (SessionMetaData) sessions.get(sessionid);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean updateSession(SessionMetaData metadata) {
		return false;
    	
    }
    
    /**
     * 
     * 如果metadata不为空，建立对应的session节点。如果为空，则建立根节点
     */
    @Override
    public boolean createSession(SessionMetaData metadata) {
    	return false;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean setAttribute(String sessionid, String key, Serializable value) {
		return false;}
    
    /**
     * {@inheritDoc}
     */
    @Override
    public Object getAttribute(String sessionid, String key) {
		return key;}
    
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean removeAttribute(String sessionid, String key) {
		return false;}
    
    /**
     * {@inheritDoc}
     */
    @Override
    public List<String> getAttributeNames(String sessionid) {
		return null;}
    
    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, Object> removeSession(String sessionid) {
		return null;}
    
    /** 
     * {@inheritDoc}
     */
    @Override
    public List<String> getSessions() {
		return null;}

    
}
