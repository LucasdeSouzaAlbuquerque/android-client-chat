/**
 *
 */
package com.chatmessenger.android.util.ym;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Teruhiko Kusunoki&lt;<a href="teru.kusu@gmail.com">teru.kusu@gmail.com</a>&gt;
 *
 */
public class HttpResponse {
    private int statusCode;
    private long contentLength;
    private String characterEncoding;
	private final Map<String, String> headers = new HashMap<String, String>();
	private final Map<String, Cookie> cookies = new HashMap<String, Cookie>();
	private byte[] body;

	/**
	 *
	 */
	public HttpResponse() {
	}

	public void setHeader(String name, String value) {
		this.headers.put(name, value);
	}

	/**
	 * header を取得します。
	 *
	 * @return headers
	 */
	public String getHeader(String name) {
		return this.headers.get(name);
	}

	/**
	 * headers を取得します。
	 * @return headers
	 */
	public Map<String, String> getHeaders() {
		return headers;
	}

	/**
	 * クッキーを取得します。
	 *
	 * @param name 名前
	 * @return クッキー
	 */
	public Cookie getCookie(String name) {
	    return this.cookies.get(name);
	}

	/**
	 * クッキーを設定します。
	 *
	 * @param name 名前
	 * @param cookie クッキー
	 */
	public void setCookie(String name, Cookie cookie) {
		this.cookies.put(name, cookie);
	}

	/**
	 * cookies を取得します。
	 * @return cookies
	 */
	public Map<String, Cookie> getCookies() {
		return cookies;
	}

	/**
	 * body を取得します。
	 * @return body
	 */
	public byte[] getBody() {
		return body;
	}

	/**
	 * body を設定します。
	 * @param body セットする body
	 */
	public void setBody(byte[] body) {
		this.body = body;
	}

    /**
     * statusCode を取得します。
     * @return statusCode
     */
    public int getStatusCode() {
        return statusCode;
    }

    /**
     * statusCode を設定します。
     * @param statusCode セットする statusCode
     */
    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    /**
     * contentLength を取得します。
     * @return contentLength
     */
    public long getContentLength() {
        return contentLength;
    }

    /**
     * contentLength を設定します。
     * @param contentLength セットする contentLength
     */
    public void setContentLength(long contentLength) {
        this.contentLength = contentLength;
    }

    /**
     * characterEncoding を取得します。
     * コンテントタイプが text/* で charset が指定されているときに設定されます。
     * @return characterEncoding
     */
    public String getCharacterEncoding() {
        return characterEncoding;
    }

    /**
     * characterEncoding を設定します。
     * @param characterEncoding セットする characterEncoding
     */
    public void setCharacterEncoding(String characterEncoding) {
        this.characterEncoding = characterEncoding;
    }
}
