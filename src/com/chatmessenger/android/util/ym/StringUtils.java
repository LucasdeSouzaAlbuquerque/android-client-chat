package com.chatmessenger.android.util.ym;

import java.util.regex.Pattern;

/**
 * 文字列に関するユーティリティメソッドを提供するクラスです。
 *
 * @author Teruhiko Kusunoki&lt;<a href="teru.kusu@gmail.com">teru.kusu@gmail.com</a>&gt;
 *
 */
public class StringUtils {

    /**
     * 空文字列もしくは空白文字だけで構成される文字列を表すパターン
     */
    private static final Pattern BLANK_PATTERN = Pattern.compile("^\\s*$");

    /**
     * 文字列が空であるかを判定します。
     *
     * @param str 判定対象の文字列
     * @return 空もしくはnullならばtrue。それ以外はfalse
     */
    public static boolean isEmpty(String str) {
        return (str == null || str.length() == 0);
    }

    /**
     * nullまたは空文字列または空白文字だけで構成される文字列かどうかを判定します。
     *
     * @param str 判定対象の文字列
     * @return nullまたは空文字列または空白文字だけで構成される文字列ならば true。それ以外はfalse。
     */
    public static boolean isBlank(String str) {
        return (str == null || BLANK_PATTERN.matcher(str).matches());
    }

    /**
     * プラットフォームのデフォルト文字エンコーディング名を取得します。
     *
     * @return プラットフォームのデフォルト文字エンコーディング名
     */
    public static String getDefaultEncoding() {
        return System.getProperty("file.encoding");
    }

    /**
     * Base64エンコードされた数値の文字列表現をインターネットアドレスに変換します。
     * 例： Mzg1OTQyMjczMA== => 3859422730 => 0xE60A1E0A => 10.30.10.230
     *
     * @param str Base64エンコードされた数値の文字列表現
     * @return インターネットアドレス
     */
    public static String base64toInetAddress(String str) {
        // TODO 実装
        throw new UnsupportedOperationException();
//        String numStr = new String(Base64.decode(str, Base64.DEFAULT));
//        int num = Integer.parseInt(numStr);
//
//        String ipAddr = (num & 0xFF) + "." + (num >> 8 & 0xFF) + "."
//            + (num >> 16 & 0xFF) + "." + (num >> 24 & 0xFF);
//
//        return ipAddr;
    }
}
