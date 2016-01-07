package com.sna.session;

import java.util.List;

public interface SnaSessionStoreProvider {
	/**
	 * 
	 * 
	 * <p>
	 * 获取属性值
	 * </p>
	 * 
	 * @author <a href="mailto:qingxu@taobao.com">清虚</a>
	 * @since 2.0 2010-8-6下午03:04:38
	 * 
	 * @param name
	 * @return
	 */
	Object getAttribute(String name);

	/**
	 * 
	 * 
	 * <p>
	 * 初始化storeProvider
	 * </p>
	 * 
	 * @author <a href="mailto:qingxu@taobao.com">清虚</a>
	 * @since 2.0 2010-8-6下午03:26:14
	 * 
	 * @param session
	 */
	public void init(SnaSession session);

	/**
	 * 
	 * 
	 * <p>
	 * 根据name设置属性值
	 * </p>
	 * 
	 * @author <a href="mailto:qingxu@taobao.com">清虚</a>
	 * @since 2.0 2010-8-6下午03:04:55
	 * 
	 * @param name
	 * @param value
	 */
	void setAttribute(String name, Object value);

	/**
	 * 
	 * 
	 * <p>
	 * 提交修改
	 * </p>
	 * 
	 * @author <a href="mailto:qingxu@taobao.com">清虚</a>
	 * @since 2.0 2010-8-6下午03:05:40
	 * 
	 */
	void commit();

	/**
	 * 
	 * 
	 * <p>
	 *  获取所有属性名
	 * </p>
	 * 
	 * @author <a href="mailto:qingxu@taobao.com">清虚</a>
	 * @since 2.0 2010-8-10下午04:35:16
	 * 
	 * @return  没有则返回长度为0的空列表
	 */
	List<String> getAttributeNames();
}
