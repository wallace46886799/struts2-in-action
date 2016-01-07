package com.sna.session;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.FastDateFormat;

/**
 * 
 * <p>
 * </p>
 * 
 * @author <a href="mailto:qingxu@taobao.com">����</a>
 * @since 2.0 2010-9-13����10:12:51
 * 
 */
public class SnaHttpServletResponse extends HttpServletResponseWrapper {
	private static final TimeZone GMT_TIME_ZONE = TimeZone.getTimeZone("GMT");
	private static final String COOKIE_DATE_PATTERN = "EEE, dd-MMM-yyyy HH:mm:ss 'GMT'";

	private static final FastDateFormat DATE_FORMAT = FastDateFormat
			.getInstance(COOKIE_DATE_PATTERN, GMT_TIME_ZONE, Locale.US);
	private SnaSession session;

	public SnaHttpServletResponse(HttpServletResponse response) {
		super(response);
	}

	public void setSession(SnaSession session) {
		this.session = session;
	}

	public SnaSession getSession() {
		return session;
	}

	@Override
	public ServletOutputStream getOutputStream() throws IOException {
		getSession().commit();
		return super.getOutputStream();
	}

	@Override
	public PrintWriter getWriter() throws IOException {
		getSession().commit();
		return super.getWriter();
	}

	public void addCookie(SnaCookie cookie) {
		if (!cookie.isHttpOnly()) {
			super.addCookie(cookie);
		} else {
			// ��Servlet 3.0��Ͳ���Ҫ��������δ����ˣ�����ֱ��cookie.setHttpOnly(true)
			// Ȼ��response.addCookie(cookie);
			String cookieString = buildHttpOnlyCookieString(cookie);
			addHeader("Set-Cookie", cookieString);
		}
	}

	private String buildHttpOnlyCookieString(SnaCookie cookie) {

		StringBuilder cookieBuilder = new StringBuilder();

		cookieBuilder.append(cookie.getName()).append("=").append(
				cookie.getValue());
		cookieBuilder.append(";");

		if (StringUtils.isNotBlank(cookie.getDomain())) {
			cookieBuilder.append("Domain").append("=").append(
					cookie.getDomain());
			cookieBuilder.append(";");
		}

		if (StringUtils.isNotBlank(cookie.getPath())) {
			cookieBuilder.append("Path").append("=").append(cookie.getPath());
			cookieBuilder.append(";");
		}

		if (cookie.getMaxAge() >= 0) {
			cookieBuilder.append("Expires").append("=").append(
					getCookieExpires(cookie.getMaxAge()));
			cookieBuilder.append(";");
		}

		cookieBuilder.append("HttpOnly");
		return cookieBuilder.toString();
	}

	private String getCookieExpires(int maxAge) {
		String result = null;
		if (maxAge > 0) {
			Calendar calendar = Calendar.getInstance();
			calendar.add(Calendar.SECOND, maxAge);
			result = DATE_FORMAT.format(calendar);
		} else { // maxAge == 0
			result = DATE_FORMAT.format(0); // maxAgeΪ0ʱ��ʾ��Ҫɾ����cookie����˽�ʱ����Ϊ��Сʱ�䣬��1970��1��1��
		}

		return result;
	}

}
