package com.sna.session;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;

public class SnaCookieStoreProvider implements SnaSessionStoreProvider {

	/**
	 * ���δ������cookie value
	 */
	private Map<String, String> cookies;

	/**
	 * ����ѽ���������
	 */
	private Map<String, Object> attributes;

	/**
	 * ����޸Ĺ������
	 */
	private Set<String> dirty;

	private SnaSession session;

	private static SessionConfig sessionConfig;

	private Set<String> inits;// 

	static {
		sessionConfig = new DefaultSessionConfig();
		sessionConfig.init();
	}

	public SnaCookieStoreProvider() {
	}

	@Override
	public Object getAttribute(String name) {
		Object b = attributes.get(name);// ���ʽ���������ֵ
		if (b == null) {
			CookieConfig cc = sessionConfig.getCookieConfig(name);
			if (cc == null) {// ����鶼������
				return null;
			}
			if (inits.contains(cc.getCookieName())) {// �Ѿ���������
				return null;
			}
			String cookieValue = cookies.get(cc.getCookieName());
			Map<String, String> separateCookies = separateCookies(cookieValue);// key=value
			Iterator<String> it = cc.getAttributes().keySet().iterator();
			while (it.hasNext()) {
				String aname = it.next();
				if (!dirty.contains(aname)) { // ��������û�б�д���Ž��н��룬����ԭ��д���ֵ�ᱻ����
					String avalue = separateCookies.get(aname);
					String value = decodeValue(avalue); // value����Ϊnull
					attributes.put(aname, value);
				}
			}
			inits.add(cc.getCookieName());// ���cookie�Ѿ���������
			b = attributes.get(name);// ���ʽ���������ֵ
		}
		return b;
	}

	private String decodeValue(String value) {
		if (StringUtils.isBlank(value)) {
			return value;
		}

		try {
			value = URLDecoder.decode(value, "UTF-8");
		} catch (Exception e) {
			return value;
		}

		// if (configEntry.isEscapeJava()) {
		// value = StringEscapeUtils.unescapeJava(value);
		// } else if (configEntry.isEncrypt()) {
		// value = BlowfishUtils.decryptBlowfish(value,
		// getBlowfishKey(properties));
		// if (configEntry.isBase64()) {
		// value = Base64Utils.removeBase64Head(value);
		// }
		// } else if (configEntry.isBase64()) {
		// value = Base64Utils.decodeBase64(value);
		// }

		return value;
	}

	private Map<String, String> separateCookies(String cookieValue) {
		Map<String, String> separateCookies = new HashMap<String, String>();

		String[] contents = StringUtils.split(cookieValue, "&");
		if (!ArrayUtils.isEmpty(contents)) {
			for (String content : contents) {
				String[] keyValue = StringUtils.split(content, "=", 2);
				if (ArrayUtils.getLength(keyValue) == 2) {
					String key = keyValue[0];
					String value = keyValue[1];
					separateCookies.put(key, value);
				}
			}
		}

		return separateCookies;
	}

	@Override
	public void setAttribute(String name, Object value) {
		// XXX ע�⣬���������value�ϵ�toString()�����Ǳ���value������
		String v = ObjectUtils.toString(value, null);
		attributes.put(name, v);
		dirty.add(name);
	}

	public void init(SnaSession session) {
		this.session = session;
		cookies = new HashMap<String, String>();
		attributes = new HashMap<String, Object>();
		dirty = new HashSet<String>();
		inits = new HashSet<String>();

		// read cookie
		readCookie2Memory();

	}

	private void readCookie2Memory() {
		Cookie[] cookies = this.session.getContext().getRequest().getCookies();
		if (!ArrayUtils.isEmpty(cookies)) {
			for (Cookie cookie : cookies) {
				this.cookies.put(cookie.getName(), cookie.getValue());
			}
		}
	}

	@Override
	public void commit() {

		String[] originalDirty = dirty.toArray(new String[dirty.size()]);
		for (String key : originalDirty) {
			if (dirty.contains(key)) { // ��key�����Ѿ���֮ǰ�����cookie�д���������Ҫ�ȼ���Ƿ���dirty��
				CookieConfig cc = sessionConfig.getCookieConfig(key);
				if (cc == null)
					continue;
				String compressValue = buildCompressValue(cc.getAttributes()
						.keySet().iterator());
				addCookieToResponse(cc.getCookieName(), compressValue,
						StringUtils.isBlank(compressValue), cc);

			}
		}
	}

	private void addCookieToResponse(String name, String value, boolean remove,
			CookieConfig cookieConfig) {
		// ֻ�h���Ѵ��ڵ�cookie
		if (remove && !cookies.containsKey(name)) {
			return;
		}
		String domain = cookieConfig.getDomain();
		int maxAge = !remove ? session.getMaxInactiveInterval() : 0;
		String path = cookieConfig.getPath();
		boolean httpOnly = cookieConfig.isHttpOnly();

		addCookieToResponse(name, value, domain, maxAge, path, httpOnly);
	}

	private void addCookieToResponse(String name, String value, String domain,
			int maxAge, String path, boolean httpOnly) {
		HttpServletResponse response = session.getContext().getResponse();
		SnaCookie cookie = new SnaCookie(name, value);
		if (StringUtils.isNotBlank(domain)) {
			cookie.setDomain(domain);
		}
		if (StringUtils.isNotBlank(path)) {
			cookie.setPath(path);
		} else {
			cookie.setPath("/");
		}
		cookie.setHttpOnly(httpOnly);
		cookie.setMaxAge(maxAge);
		response.addCookie(cookie);

	}

	private String buildCompressValue(Iterator<String> names) {
		StringBuilder compressBuilder = new StringBuilder();
		boolean first = true;
		// ConfigEntry configEntry = null;
		while (names.hasNext()) {
			String aname = names.next();
			// �Ȱ��ɰ���������ٰ��°�д��ȥ
			Object attribute = getAttribute(aname); // Ϊnullʱ��ʾ�������ѱ�ɾ��
			String value = attribute != null ? attribute.toString() : null;
			value = encodeValue(value);

			// ��ֹ��α���cookie
			dirty.remove(aname);

			if (value == null) { // �������ѱ�ɾ���������ʱ���Ե�ǰ����
				continue;
			}

			if (first) {
				first = false;
			} else {
				compressBuilder.append("&");
			}
			compressBuilder.append(aname).append("=").append(value);
		}

		return compressBuilder.toString();
	}

	private String encodeValue(String value) {
		// String key = configEntry.getKey();
		// String value1 = value;
		if (StringUtils.isEmpty(value)) {
			return value;
		}

		// if (configEntry.isEscapeJava()) {
		// value = StringEscapeUtils.escapeJava(value);
		// } else if (configEntry.isEncrypt()) {
		// if (configEntry.isBase64()) {
		// value = Base64Utils.addBase64Head(value);
		// }
		// value = BlowfishUtils.encryptBlowfish(value,
		// getBlowfishKey(properties));
		// } else if (configEntry.isBase64()) {
		// value = Base64Utils.encodeBase64(value);
		// }

		try {
			// XXX ���Ǳ��룬��ԭʵ�ֲ�ͬ��ԭʵ�������cookieʱֻ�Լ��ܵ����Ա���
			value = URLEncoder.encode(value, "UTF-8");
		} catch (Exception e) {

			// MonitorLog.addStat("TbSession", "CookieStore",
			// "singleCookie����ʧ��", 0, 1);
			// ����ʧ��ʱ�����ش����ǣ��Ҳ����浽cookies��
			return null;
		}
		/*
		 * if (StringUtil.isNotBlank(key) && StringUtil.isNotBlank(value) &&
		 * affectTradeList.contains(key) ){
		 * userTrackLog.error(configEntry.getKey
		 * ()+"����: "+" OrignalV: "+value1+"--EncodeV: "+value); }
		 */
		return value;
	}

	@Override
	public List<String> getAttributeNames() {
		return this.sessionConfig.getAllAttributes();
	}

}
