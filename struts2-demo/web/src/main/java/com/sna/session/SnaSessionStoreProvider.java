package com.sna.session;

import java.util.List;

public interface SnaSessionStoreProvider {
	/**
	 * 
	 * 
	 * <p>
	 * ��ȡ����ֵ
	 * </p>
	 * 
	 * @author <a href="mailto:qingxu@taobao.com">����</a>
	 * @since 2.0 2010-8-6����03:04:38
	 * 
	 * @param name
	 * @return
	 */
	Object getAttribute(String name);

	/**
	 * 
	 * 
	 * <p>
	 * ��ʼ��storeProvider
	 * </p>
	 * 
	 * @author <a href="mailto:qingxu@taobao.com">����</a>
	 * @since 2.0 2010-8-6����03:26:14
	 * 
	 * @param session
	 */
	public void init(SnaSession session);

	/**
	 * 
	 * 
	 * <p>
	 * ����name��������ֵ
	 * </p>
	 * 
	 * @author <a href="mailto:qingxu@taobao.com">����</a>
	 * @since 2.0 2010-8-6����03:04:55
	 * 
	 * @param name
	 * @param value
	 */
	void setAttribute(String name, Object value);

	/**
	 * 
	 * 
	 * <p>
	 * �ύ�޸�
	 * </p>
	 * 
	 * @author <a href="mailto:qingxu@taobao.com">����</a>
	 * @since 2.0 2010-8-6����03:05:40
	 * 
	 */
	void commit();

	/**
	 * 
	 * 
	 * <p>
	 *  ��ȡ����������
	 * </p>
	 * 
	 * @author <a href="mailto:qingxu@taobao.com">����</a>
	 * @since 2.0 2010-8-10����04:35:16
	 * 
	 * @return  û���򷵻س���Ϊ0�Ŀ��б�
	 */
	List<String> getAttributeNames();
}
