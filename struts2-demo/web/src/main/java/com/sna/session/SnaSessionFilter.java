package com.sna.session;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class SnaSessionFilter implements Filter {
	private FilterConfig filterConfig;
	

	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		//如果request和response非法则直接执行filter链
		if (!(request instanceof HttpServletRequest && response instanceof HttpServletResponse)
				|| (request.getAttribute(getClass().getName()) != null)) {
			chain.doFilter(request, response);
			return;
		}
		request.setAttribute(getClass().getName(), Boolean.TRUE);
		
		
		//包装为SnaHttpServletXXX
		SnaHttpServletRequest snaHttpServletRequest = new SnaHttpServletRequest((HttpServletRequest) request);
		SnaHttpServletResponse snaHttpServletResponse = new SnaHttpServletResponse((HttpServletResponse) response);
		
		
		//构造SnaHttpContext
		SnaHttpContext context = new SnaHttpContext();
		context.setRequest(snaHttpServletRequest);
		context.setResponse((HttpServletResponse) snaHttpServletResponse);
		context.setServletContext(filterConfig.getServletContext());
		
		SnaSession session = getSession(context);
		snaHttpServletRequest.setSession(session);
		snaHttpServletResponse.setSession(session);

		chain.doFilter(snaHttpServletRequest, snaHttpServletResponse);

		session.commit();

	}

	@Override
	public void init(FilterConfig config) throws ServletException {
		this.filterConfig = config;
	}

	@Override
	public void destroy() {
	}

	private SnaSession getSession(SnaHttpContext context) {
		SnaSession session = new SnaSession(context);
		session.init();
		return session;
	}

}
