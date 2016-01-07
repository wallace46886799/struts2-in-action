package com.sna.session;

import javax.servlet.http.Cookie;

/**
 * 
 * <p>
 * </p>
 * 
 * @author <a href="mailto:qingxu@taobao.com">清虚</a>
 * @since 2.0 2010-9-13上午10:29:49
 * 
 */
public class SnaCookie extends Cookie {
	private boolean httpOnly = false;// 如果为true，js无法直接访问cookie

	public SnaCookie(String name, String value) {
		super(name, value);
	}

	public void setHttpOnly(boolean httpOnly) {
		this.httpOnly = httpOnly;
	}

	public boolean isHttpOnly() {
		return httpOnly;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((getDomain() == null) ? 0 : getDomain().hashCode());
		result = prime * result + (httpOnly ? 1231 : 1237);
		result = prime * result + getMaxAge();
		result = prime * result
				+ ((getName() == null) ? 0 : getName().hashCode());
		result = prime * result
				+ ((getPath() == null) ? 0 : getPath().hashCode());
		result = prime * result
				+ ((getValue() == null) ? 0 : getValue().hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SnaCookie other = (SnaCookie) obj;
		if (getDomain() == null) {
			if (other.getDomain() != null)
				return false;
		} else if (!getDomain().equals(other.getDomain()))
			return false;
		if (httpOnly != other.httpOnly)
			return false;
		if (getMaxAge() != other.getMaxAge())
			return false;
		if (getName() == null) {
			if (other.getName() != null)
				return false;
		} else if (!getName().equals(other.getName()))
			return false;
		if (getPath() == null) {
			if (other.getPath() != null)
				return false;
		} else if (!getPath().equals(other.getPath()))
			return false;
		if (getValue() == null) {
			if (other.getValue() != null)
				return false;
		} else if (!getValue().equals(other.getValue()))
			return false;
		return true;
	}
}
