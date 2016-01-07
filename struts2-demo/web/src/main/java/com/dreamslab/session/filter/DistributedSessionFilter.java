
package com.dreamslab.session.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dreamslab.session.SessionManager;
import com.dreamslab.session.servlet.RemotableRequestWrapper;

/**
 * 
 * session拦截器。<br />
 * 1. 加载SessionManager具体实现类 2. 替换ServletRequest的实现，完成代理模式
 * 
 * @author gaofeng
 * @date Sep 18, 2013 1:27:10 PM
 * @id $Id$
 */
public class DistributedSessionFilter implements Filter {
    
    private static final Logger LOG = LoggerFactory.getLogger(DistributedSessionFilter.class);
    private SessionManager sessionManager;
    
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    
        try {
            this.sessionManager = (SessionManager) Class.forName("com.tc.session.local.LocalSessionManager").newInstance();
            this.sessionManager.setServletContext(filterConfig.getServletContext());
        } catch (ClassNotFoundException e) {
            LOG.error("过滤器初始化失败", e);
        } catch (InstantiationException e) {
            LOG.error("过滤器初始化失败", e);
        } catch (IllegalAccessException e) {
            LOG.error("过滤器初始化失败", e);
        }
        
        if (LOG.isInfoEnabled()){
            LOG.info("TCSessionFilter.init completed.");
        }
            
        
    }
    
    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
            FilterChain chain) throws IOException, ServletException {
    
        RemotableRequestWrapper req = new RemotableRequestWrapper((HttpServletRequest) request, sessionManager);
        req.setResponse((HttpServletResponse)response);
        chain.doFilter(req, response);
    }
    
    @Override
    public void destroy() {
    
        if (sessionManager != null) {
            try {
                sessionManager.close();
            } catch (Exception ex) {
                LOG.error("关闭Session管理器时发生异常，", ex);
            }
        }
        
        if (LOG.isInfoEnabled()) {
            LOG.info("TCSessionFilter.destroy completed.");
        }
    }
}
