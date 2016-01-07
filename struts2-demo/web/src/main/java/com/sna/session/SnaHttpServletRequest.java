package com.sna.session;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpSession;

public class SnaHttpServletRequest extends HttpServletRequestWrapper {

	private SnaSession session;

	public SnaHttpServletRequest(HttpServletRequest request) {
		super(request);
	}

	public void setSession(SnaSession session) {
		this.session = session;
	}
	
	@Override
	public HttpSession getSession(boolean create) {
		return this.session;
	}

	@Override
	public HttpSession getSession() {
		return this.session;
	}

}
