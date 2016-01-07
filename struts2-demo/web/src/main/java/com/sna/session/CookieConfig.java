package com.sna.session;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * 
 * <p>
 * 格式： cookie key:key cookie value:
 * attrName=value&attrName=value&attrName=value&attrName=value
 * </p>
 * 
 * @author <a href="mailto:qingxu@taobao.com">清虚</a>
 * @since 2.0 2010-8-6下午05:47:04
 * 
 */
public class CookieConfig {
	private String cookieName;// cookie key,最终是cookie的名称
	private boolean httpOnly;
	private String domain;
	private String path;// cookie path
	private boolean encrypt;// 是否加密
	private Map<String, Attribute> attributes = new HashMap<String, Attribute>();

	public void setCookieName(String cookieName) {
		this.cookieName = cookieName;
	}

	public String getCookieName() {
		return cookieName;
	}

	public boolean isHttpOnly() {
		return httpOnly;
	}

	public void setHttpOnly(boolean httpOnly) {
		this.httpOnly = httpOnly;
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public Map<String, Attribute> getAttributes() {
		return attributes;
	}

	public void setAttributes(Map<String, Attribute> attributes) {
		this.attributes = attributes;
	}

	public void setAttribute(Attribute attribute) {
		this.attributes.put(attribute.getName(), attribute);
	}

	public Attribute getAttribute(String name) {
		return this.attributes.get(name);
	}
	
	public void setEncrypt(boolean encrypt) {
		this.encrypt = encrypt;
	}
	
    public boolean isEncrypt() {
		return encrypt;
	}
}
