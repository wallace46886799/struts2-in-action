package com.sna.session;

import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionContext;

import org.apache.commons.lang.StringUtils;

@SuppressWarnings("deprecation")
public class SnaSession implements HttpSession {
	public static final String SESSION_ID = "sessionID";
	private SnaHttpContext context;
	private String sessionId;
	private long creationTime;

	private volatile int maxInactiveInterval = 1800; // 默认半个小时,单位： 秒

	private SnaSessionStoreProvider storeProvider;

	public SnaSession(SnaHttpContext context) {
		this.context = context;
		this.creationTime = System.currentTimeMillis();
	}

	public void init() {
		initStoreProvider();
		initSessionId();// init session id
	}

	private void initStoreProvider() {
		this.storeProvider = new SnaCookieStoreProvider();
		this.storeProvider.init(this);
	}

	private void initSessionId() {
		sessionId = (String) getAttribute(SESSION_ID);
		if (StringUtils.isBlank(sessionId)) {
			sessionId = StringUtils.remove(UUID.randomUUID().toString(), "-");
			setAttribute(SESSION_ID, sessionId);
		}
	}

	@Override
	public Object getAttribute(String name) {
		return storeProvider.getAttribute(name);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Enumeration getAttributeNames() {
		List<String> names = storeProvider.getAttributeNames();
		final Iterator<String> it = names.iterator();
		Enumeration<String> enumeration = new Enumeration<String>() {
			@Override
			public boolean hasMoreElements() {
				return it.hasNext();
			}

			@Override
			public String nextElement() {
				return it.next();
			}
		};
		return enumeration;
	}

	@Override
	public long getCreationTime() {
		return this.creationTime;
	}

	@Override
	public String getId() {
		return this.sessionId;
	}

	@Override
	public long getLastAccessedTime() {
		return this.creationTime;
	}

	@Override
	public int getMaxInactiveInterval() {
		return this.maxInactiveInterval;
	}

	@Override
	public ServletContext getServletContext() {
		return context.getServletContext();
	}

	@Override
	public HttpSessionContext getSessionContext() {
		throw new UnsupportedOperationException();// 不支持你了
	}

	@Override
	public Object getValue(String name) {
		return getAttribute(name);
	}

	@Override
	public String[] getValueNames() {
		List<String> names = storeProvider.getAttributeNames();
		return names.toArray(new String[names.size()]);
	}

	@Override
	public void invalidate() {
		
	}

	@Override
	public boolean isNew() {
		return true;
	}

	@Override
	public void putValue(String name, Object value) {
		setAttribute(name, value);
	}

	@Override
	public void removeAttribute(String name) {
		setAttribute(name, null);
	}

	@Override
	public void removeValue(String name) {
		removeAttribute(name);
	}

	@Override
	public void setAttribute(String name, Object value) {
		storeProvider.setAttribute(name, value);
	}

	@Override
	public void setMaxInactiveInterval(int interval) {
		this.maxInactiveInterval = interval;
	}

	public SnaHttpContext getContext() {
		return context;
	}

	/**
	 * 
	 * 
	 * <p>
	 * 属性设置完毕之后统一提交更改
	 * </p>
	 * 
	 * @author <a href="mailto:qingxu@taobao.com">清虚</a>
	 * @since 2.0 2010-8-10下午04:56:08
	 * 
	 */
	public void commit() {
		this.storeProvider.commit();
	}
}
