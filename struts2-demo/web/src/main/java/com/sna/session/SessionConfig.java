package com.sna.session;

import java.util.List;

public interface SessionConfig {
	/**
	 * 
	 * 
	 * <p>
	 *   初始化session配置
	 * </p>
	 * 
	 * @author <a href="mailto:qingxu@taobao.com">清虚</a>
	 * @since 2.0 2010-9-13下午12:21:42
	 * 
	 */
	void init();
	

	List<String> getAllAttributes();

	/**
	 * 
	 * 
	 * <p>
	 * 根据属性名获取所在cookie的配置
	 * </p>
	 * 
	 * @author <a href="mailto:qingxu@taobao.com">清虚</a>
	 * @since 2.0 2010-8-10下午05:05:20
	 * 
	 * @param attrName
	 * @return
	 */
	CookieConfig getCookieConfig(String attrName);
}
