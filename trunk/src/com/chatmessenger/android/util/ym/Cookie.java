package com.chatmessenger.android.util.ym;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * @author Teruhiko Kusunoki&lt;<a href="teru.kusu@gmail.com">teru.kusu@gmail.com</a>&gt;
 *
 */
public class Cookie {
    private String key;
    private String value;
    private String path;
    private Date expire;
    private String domain;
    private boolean secure = false;

    /**
     * Set-Cookie ヘッダの値を解析してクッキーオブジェクトを生成します。
     * 値のデコードは行いません。
     *
     * @param cookieHeader Set-Cookieヘッダの値
     * @return クッキーオブジェクト
     * @throws ParseException 解析に失敗した場合
     */
    public static Cookie parseCookie(String cookieHeader) {
        return parseCookie(cookieHeader, null);
    }

    /**
     * Set-Cookie ヘッダの値を解析してクッキーオブジェクトを生成します。
     *
     * @param cookieHeader Set-Cookieヘッダの値
     * @param encode cookie値のエンコード. nullを指定するとデコードを行いません。
     * @return クッキーオブジェクト
     * @throws ParseException 解析に失敗した場合
     */
    public static Cookie parseCookie(String cookieHeader, String encode) {

        Cookie c = new Cookie();
        for (String str : cookieHeader.split("\\s*;\\s*")) {
            int index = str.indexOf('=');
            if (index < 0) {
                if("secure".equals(str.toLowerCase())) {
                    c.setSecure(true);
                }
                continue;
            }

            String key = str.substring(0, index).toLowerCase();
            String value = str.substring(index + 1);

            if ("expires".equals(key.toLowerCase())) {
                SimpleDateFormat sdf = new SimpleDateFormat("EEE, d-MMM-yyyy HH:mm:ss ZZZ", Locale.US);
                Date date = null;
                try {
                    date = sdf.parse(value);
                } catch (ParseException e) {
                    throw new RuntimeException(e.getMessage(), e);
                }
                c.setExpire(date);
            } else if ("domain".equals(key.toLowerCase())) {
                c.setDomain(value);
            } else if ("path".equals(key.toLowerCase())) {
                c.setPath(value);
            } else {
                c.setKey(key);
                if (StringUtils.isBlank(encode)) {
                    c.setValue(value);
                } else {
                    try {
                        c.setValue(URLDecoder.decode(value, encode));
                    } catch (UnsupportedEncodingException e) {
                        throw new RuntimeException(e.getMessage(), e);
                    }
                }
            }
        }

        return c;
    }

    /**
     * key を取得します。
     * @return key
     */
    public String getKey() {
        return key;
    }
    /**
     * key を設定します。
     * @param key セットする key
     */
    public void setKey(String key) {
        this.key = key;
    }
    /**
     * value を取得します。
     * @return value
     */
    public String getValue() {
        return value;
    }
    /**
     * value を設定します。
     * @param value セットする value
     */
    public void setValue(String value) {
        this.value = value;
    }
    /**
     * path を取得します。
     * @return path
     */
    public String getPath() {
        return path;
    }
    /**
     * path を設定します。
     * @param path セットする path
     */
    public void setPath(String path) {
        this.path = path;
    }
    /**
     * expire を取得します。
     * @return expire
     */
    public Date getExpire() {
        return expire;
    }
    /**
     * expire を設定します。
     * @param expire セットする expire
     */
    public void setExpire(Date expire) {
        this.expire = expire;
    }
    /**
     * domain を取得します。
     * @return domain
     */
    public String getDomain() {
        return domain;
    }
    /**
     * domain を設定します。
     * @param domain セットする domain
     */
    public void setDomain(String domain) {
        this.domain = domain;
    }

    /**
     * secure を取得します。
     * @return secure
     */
    public boolean isSecure() {
        return secure;
    }

    /**
     * secure を設定します。
     * @param secure セットする secure
     */
    public void setSecure(boolean secure) {
        this.secure = secure;
    }
}
