package com.chatmessenger.android.util;

import java.io.UnsupportedEncodingException;

import com.chatmessenger.android.ym.Constants;



import android.util.Log;

/**
 * 便利メソッドの詰め合わせ。
 * 
 * @author Teruhiko Kusunoki&lt;<a href="teru.kusu@gmail.com">teru.kusu@gmail.com</a>&gt;
 *
 */
public class Utils {
	
	/**
	 * 新しいオブジェクトを生成します。
	 */
	protected Utils() {
	}

	/**
	 * バイト列を16進ダンプ形式に整形します。
	 * 文字列部分のエンコーディングにはデフォルトエンコーディングを使用します。
	 * 
	 * @param data バイト列
	 * @return １６進ダンプ形式に整形された文字列
	 */
	public static String hexDump(byte[] data) {
		return hexDump(data, null);
	}

	/**
	 * バイト列を16進ダンプ形式に整形します。
	 * 
	 * @param data バイト列
	 * @param encoding 文字列部のエンコーディング
	 * @return １６進ダンプ形式に整形された文字列
	 */
	public static String hexDump(byte[] data, String encoding) {
		StringBuilder sb = new StringBuilder();

		if (encoding == null) {
			encoding = System.getProperty("file.encoding");
		}

		final int bytesPerLine = 16;

		int i = 0;
		for (i = 0; i < data.length; i++) {
			if (i % bytesPerLine == 0) {
				if (i > 0) {
					sb.append("  ");
					try {
						sb.append(new String(data, i - bytesPerLine, bytesPerLine, encoding));
					} catch (UnsupportedEncodingException e) {
						throw new SystemException(e.getMessage(), e);
					}
					sb.append("\n");
				}
				sb.append(String.format("%08x:", i));
			} else if (i % 4 == 0) {
				sb.append(' ');
			}

			sb.append(String.format(" %02x", data[i]));
		}
		
		Log.v(Constants.LOG_TAG, "data size: " + data.length + ", dumped size: " + i);

		if (i % bytesPerLine != 0) {
			if (i / bytesPerLine > 0) {
				for (int j = i; j % bytesPerLine != 0; j++) {
					if (j % 4 == 0) {
						sb.append(" ");
					}
					sb.append("   ");
				}
			}
			sb.append("  ");
			try {
				sb.append(new String(data, i - i % bytesPerLine, i % bytesPerLine, encoding));
			} catch (UnsupportedEncodingException e) {
				throw new SystemException(e.getMessage(), e);
			}
			sb.append("\n");
		}
		
		return sb.toString();
	}
}
