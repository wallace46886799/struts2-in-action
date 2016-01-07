package com.sna.session;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;

/**
 * 
 * 
 * <p>
 * �������ȫ����������
 * </p>
 * 
 * @author <a href="mailto:qingxu@taobao.com">����</a>
 * @since 2.0 2010-8-10����05:28:37
 * 
 */
public class DefaultSessionConfig implements SessionConfig {
	private Map<String, CookieConfig> cookies = new HashMap<String, CookieConfig>();
	private List<String> attris = new ArrayList<String>();

	@Override
	public List<String> getAllAttributes() {
		return attris;
	}

	@Override
	public CookieConfig getCookieConfig(String attrName) {
		for (CookieConfig cc : cookies.values()) {
			Attribute ab = cc.getAttribute(attrName);
			if (ab != null) {
				return cc;
			}
		}
		return null;
	}

	@Override
	public void init() {
		try {
			InputStream is = null;

			is = this.getClass().getResourceAsStream("/session.xml");

			if (is == null) {
				throw new RuntimeException("no session config");
			}
			String sessionText = null;
			ReadableByteChannel rc = null;
			WritableByteChannel wc = null;
			try {
				rc = Channels.newChannel(is);
				ByteArrayOutputStream os = new ByteArrayOutputStream();
				wc = Channels.newChannel(os);
				ByteBuffer bb = ByteBuffer.allocate(1024);
				while (rc.read(bb) != -1) {
					bb.flip();
					wc.write(bb);
					bb.clear();
				}
				sessionText = os.toString("GBK");
			} catch (Exception e) {
				throw new RuntimeException(e);
			} finally {
				if (rc != null) {
					rc.close();
				}
				if (wc != null) {
					wc.close();
				}
			}
			if (sessionText == null) {
				throw new RuntimeException("read session config exception");
			}

			Document doc = DocumentHelper.parseText(sessionText);
			Element root=doc.getRootElement();
//			List<Node> nodes = root.selectNodes("/cookies/cookie");
			
			CookieConfig cookieConfig = new CookieConfig();
			cookieConfig.setCookieName("sessionID");
			cookieConfig.setDomain("");
			cookieConfig.setPath("/");
			cookieConfig.setHttpOnly(true);
			cookieConfig.setEncrypt(true);
			
			Attribute attr = new Attribute();
			attr.setName("sessionID");
			cookieConfig.setAttribute(attr);
			
			cookies.put(cookieConfig.getCookieName(), cookieConfig);
			
			/**if (nodes != null) {
				Set<String> set = new HashSet<String>();
				for (Node node : nodes) {
					CookieConfig cookieConfig = new CookieConfig();
					Node key = node.selectSingleNode("key");
					if (key == null || key.getText() == null) {
						continue;
					}
					cookieConfig.setCookieName(key.getText());
					Node domain = node.selectSingleNode("domain");
					if (domain != null) {
						cookieConfig.setDomain(domain.getText());
					}
					Node path = node.selectSingleNode("path");
					if (path != null) {
						cookieConfig.setPath(path.getText());
					}
					Node httponly = node.selectSingleNode("httponly");
					if (httponly != null) {
						cookieConfig.setHttpOnly(true);
					}
					Node encrypt = node.selectSingleNode("encrypt");
					if (encrypt != null) {
						cookieConfig.setEncrypt(true);
					}
					

					List<Node> attributes = node
							.selectNodes("attributes/attribute");
					if (attributes == null || attributes.isEmpty()) {
						continue;
					}
					// ��������
					for (Node attribute : attributes) {
						if (attribute.getNodeType() == Node.ELEMENT_NODE) {
							Element el = (Element) attribute;

							String aname = (String) el.attributeValue("name");
							if (StringUtils.isBlank(aname)) {
								continue;
							}
							Attribute attr = new Attribute();
							attr.setName(aname);
							if (!set.add(aname)) {
								throw new RuntimeException(
										"cookie��s attributeName duplicate key="
												+ aname);
							}
							cookieConfig.setAttribute(attr);
						}
					}
					cookies.put(cookieConfig.getCookieName(), cookieConfig);
				}
			}**/
			
			
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
