package com.sna.session;

import java.util.List;

public interface SessionConfig {
	/**
	 * 
	 * 
	 * <p>
	 *   ��ʼ��session����
	 * </p>
	 * 
	 * @author <a href="mailto:qingxu@taobao.com">����</a>
	 * @since 2.0 2010-9-13����12:21:42
	 * 
	 */
	void init();
	

	List<String> getAllAttributes();

	/**
	 * 
	 * 
	 * <p>
	 * ������������ȡ����cookie������
	 * </p>
	 * 
	 * @author <a href="mailto:qingxu@taobao.com">����</a>
	 * @since 2.0 2010-8-10����05:05:20
	 * 
	 * @param attrName
	 * @return
	 */
	CookieConfig getCookieConfig(String attrName);
}
