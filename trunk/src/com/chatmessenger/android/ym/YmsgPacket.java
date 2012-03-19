package com.chatmessenger.android.ym;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

import com.chatmessenger.android.util.SystemException;
import com.chatmessenger.android.util.Utils;



import android.util.Log;

/**
 * YMSGプロトコルで使用されるパケットを表すクラスです。
 * このクラスはスレッドセーフではありません。
 * 複数のスレッドから同時に更新を行った場合の結果は不定です。
 *
 * @author Teruhiko Kusunoki&lt;<a href="teru.kusu@gmail.com">teru.kusu@gmail.com</a>&gt;
 *
 */
public class YmsgPacket implements Constants {
	private int protocolVersion ;
	private int vendorId;
	private int service;
	private int status;
	private int sessionId;
	private final List<Entry> body = new ArrayList<Entry>();

	/**
	 * 新しいオブジェクトを生成します。
	 */
	public YmsgPacket() {
	}

	/**
	 * 新しいオブジェクトを生成します。
	 */
 	public YmsgPacket(InputStream in) throws IOException, YmsgException {
		load(in);
	}

	/**
	 * 入力ストリームからパケットを読み込みます。
	 * メソッド内では入力ストリームは閉じられません。
	 *
	 * @param in 入力ストリーム
	 * @throws IOException 入出力に問題が発生した場合
	 * @throws YmsgException プロトコルに関する問題が発生した場合
	 */
	public void load(InputStream in) throws SocketException, IOException, YmsgException {
		this.body.clear();

		byte[] buff = new byte[8192];
		DataInputStream dis = null;

		dis = new DataInputStream(in);
		dis.readFully(buff, 0, 4);

		String str = new String(buff, 0 ,4, CHARSET_ASCII);

		if (!YMSG_SIGNATURE.equals(str)) {
			throw new YmsgException("signature not match: " + str);
		}

		// header
		setProtocolVersion(dis.readShort());
		setVendorId(dis.readShort());
		int length = dis.readShort();
		setService(dis.readShort());
		setStatus(dis.readInt());
		setSessionId(dis.readInt());

		Log.v(Constants.LOG_TAG, "body length: " + length);

		// body
		byte[] data = new byte[length];

		dis.readFully(data);
		for (String dumpLine : Utils.hexDump(data).split("\\n")) {
			Log.v(Constants.LOG_TAG, dumpLine);
		}

		ByteArrayInputStream bais = null;
		try {
			bais = new ByteArrayInputStream(data);
			parseBody(bais);
		} finally {
			try { bais.close(); } catch (Exception ignored) {}
		}
	}

	/**
	 * 出力ストリームにYMSGパケットを書き出します。
	 *
	 * @param os 出力ストリームです。
	 * @throws IOException 入出力に問題が発生した場合
	 */
	public void writeTo(OutputStream os) throws IOException {
		DataOutputStream dos = null;

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		for (Entry entry : this.body) {
			baos.write(entry.getKey().getBytes(CHARSET_ASCII));
			baos.write(YMSG_DELIM);
			if (entry.getValue() != null) {
				baos.write(entry.getValue());
			}
			baos.write(YMSG_DELIM);
		}
		baos.close();

		byte[] data = baos.toByteArray();

		dos = new DataOutputStream(os);
		dos.write(YMSG_SIGNATURE.getBytes());
		dos.writeShort(getProtocolVersion());
		dos.writeShort(getVendorId());
		dos.writeShort(data.length);
		dos.writeShort(getService());
		dos.writeInt(getStatus());
		dos.writeInt(getSessionId());
		dos.write(data);

		dos.flush();
	}



	/**
	 * protocolVersion を取得します。
	 * @return protocolVersion
	 */
	public int getProtocolVersion() {
		return protocolVersion;
	}

	/**
	 * protocolVersion を設定します。
	 * @param protocolVersion セットする protocolVersion
	 */
	public void setProtocolVersion(int protocolVersion) {
		this.protocolVersion = protocolVersion;
	}

	/**
	 * clientVersion を取得します。
	 * @return clientVersion
	 */
	public int getVendorId() {
		return vendorId;
	}

	/**
	 * clientVersion を設定します。
	 * @param clientVersion セットする clientVersion
	 */
	public void setVendorId(int clientVersion) {
		this.vendorId = clientVersion;
	}

	/**
	 * service を取得します。
	 * @return service
	 */
	public int getService() {
		return service;
	}

	/**
	 * service を設定します。
	 * @param service セットする service
	 */
	public void setService(int service) {
		this.service = service;
	}

	/**
	 * status を取得します。
	 * @return status
	 */
	public int getStatus() {
		return status;
	}

	/**
	 * status を設定します。
	 * @param status セットする status
	 */
	public void setStatus(int status) {
		this.status = status;
	}

	/**
	 * sessionId を取得します。
	 * @return sessionId
	 */
	public int getSessionId() {
		return sessionId;
	}

	/**
	 * sessionId を設定します。
	 * @param sessionId セットする sessionId
	 */
	public void setSessionId(int sessionId) {
		this.sessionId = sessionId;
	}

	/**
	 * キーを指定して値を取得します。
	 * 一番最初に見つかった値を返します。
	 *
	 * @param key キー
	 * @return values 値
 	 */
	public byte[] getValue(String key) {
		Entry entry = getEntry(key);
		if (entry != null) {
			return entry.getValue();
		}

		return null;
	}

	/**
	 * キーを指定して値を取得します。
	 * 一番最初に見つかった値を返します。
	 *
	 * @param key キー
	 * @return values 値
 	 */
	public String getValueAsString(String key) {
		return getValueAsString(key, null);
	}

	/**
	 * キーを指定して値を取得します。
	 * 一番最初に見つかった値を返します。
	 *
	 * @param key キー
	 * @param encoding エンコーディング
	 * @return values 値
 	 */
	public String getValueAsString(String key, String encoding) {
		if (encoding == null) {
			encoding = System.getProperty("file.encoding");
		}

		Entry entry = getEntry(key);
		if (entry != null) {
			try {
				return new String(entry.getValue(), encoding);
			} catch (UnsupportedEncodingException e) {
				throw new SystemException(e.getMessage(), e);
			}
		}

		return null;
	}

	/**
	 * 指定したキーに対応する値を全て取得します。
	 *
	 * @param key キー
	 * @return values 値のリスト
 	 */
	public List<byte[]> getValues(String key) {
		List<byte[]> values = new ArrayList<byte[]>();

		for (Entry tmpEntry : this.body) {
			if (tmpEntry.getKey().equals(key)) {
				values.add(tmpEntry.getValue());
			}
		}

		return values;
	}

	/**
	 * 指定されたキーを持つボディ値のうち、一番最初に見つかったものを指定の値に設定します。
	 * 該当するキーが存在しなかった場合は新しく追加されます。
	 *
	 * @param key キー
	 * @param value 値
	 */
	public void setValue(String key, byte[] value) {
		Entry entry = getEntry(key);

		if (entry == null) {
			entry = new Entry(key);
			this.body.add(entry);
		}

		entry.setValue(value);
	}

	/**
	 * 指定されたキーを持つボディ値のうち、一番最初に見つかったものを指定の値に設定します。
	 * 該当するキーが存在しなかった場合は新しく追加されます。
	 *
	 * @param key キー
	 * @param value 値
	 * @param encoding エンコーディング
	 */
	public void setValue(String key, String value, String encoding) {
		if (encoding == null) {
			encoding = System.getProperty("file.encoding");
		}

		try {
			setValue(key, value.getBytes(encoding));
		} catch (UnsupportedEncodingException e) {
			throw new SystemException(e.getMessage(), e);
		}
	}

	/**
	 * 指定されたキーを持つボディ値のうち、一番最初に見つかったものを指定の値に設定します。
	 * 該当するキーが存在しなかった場合は新しく追加されます。
	 *
	 * @param key キー
	 * @param value 値
	 * @param encoding エンコーディング
	 */
	public void setValue(String key, String value) {
		setValue(key, value, null);
	}

	/**
	 * キーと値をボディに追加します。
	 * 値はデフォルト文字エンコーディングを使用してバイト列に変換されます。
	 *
	 * @param key キーです。
	 * @param value 値です。
	 */
	public void addValue(String key, String value) {
		addValue(key, value.getBytes());
	}

	/**
	 * キーと値をボディに追加します。
	 * 値は指定の文字エンコーディングを使用してバイト列に変換されます。
	 *
	 * @param key キーです。
	 * @param value 値です。
	 * @param
	 */
	public void addValue(String key, String value, String encoding) throws UnsupportedEncodingException {
		addValue(key, value.getBytes(encoding));
	}

	/**
	 * キーと値をボディに値を追加します。
	 *
	 * @param key キー
	 * @param value 値
	 */
	public void addValue(String key, byte[] value) {
		this.body.add(new Entry(key, value));
	}

	/**
	 * ボディの内容を取得します。
	 * 順序は読み込んだ順になります。
	 *
	 * @return ボディの内容を表すリスト
	 */
	public List<Entry>  getEntries() {
		return this.body;
	}

	/**
	 * 指定されたキーを持つ最初のボディ要素を返します。
	 *
	 * @param key キー
	 * @return 指定されたキーを持つ最初のボディ要素。該当するものがなければ null
	 */
	public Entry getEntry(String key) {
		for (Entry entry : this.body) {
			if(entry.getKey().equals(key)) {
				return entry;
			}
		}

		return null;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Class: ").append(getClass().getName());
		sb.append(", ");
		sb.append("protocolVersion=0x").append(Integer.toHexString(getProtocolVersion())).append(", ");
		sb.append("vendorId=0x").append(Integer.toHexString(getVendorId())).append(", ");
		sb.append("service=0x").append(Integer.toHexString(getService())).append(", ");
		sb.append("status=0x").append(Integer.toHexString(getStatus())).append(", ");
		sb.append("sessionId=0x").append(Integer.toHexString(getSessionId())).append(", ");
		for (Entry entry : this.body) {
			// TODO バイト列の出力形式を再検討
			sb.append(entry.getKey()).append("=").append(new String(entry.getValue())).append(", ");
		}

		return sb.toString();
	}

	/**
	 * YMSGパケットのボディ部(データ部)を解析します。
	 * 入力ストリームはメソッド内では閉じられません。
	 *
	 * @param in 入力ストリーム
	 * @throws IOException 入出力で問題が発生した場合
	 * @throws YmsgException プロトコル上の問題があった場合
	 */
	public void parseBody(InputStream in) throws IOException, YmsgException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		try {
			int delimCursor = 0;
			String key = null;
			byte[] data = null;
			for (int i = 0, count = 0; (i = in.read()) >= 0; count++) {
				if (i == (YMSG_DELIM[delimCursor] & 0xFF)) {
					if (delimCursor == YMSG_DELIM.length - 1) {
						// セパレータ発見

						// バッファを処理
						baos.flush();
						data = baos.toByteArray();

						if (key == null) {
							key = new String(data);
						} else {
							this.body.add(new Entry(key, data));
							key = null;
						}

						baos.reset();

						// セパレータの比較位置をリセット
						delimCursor = 0;
					} else {
						// まだ完全なセパレータではない
						delimCursor++;
					}
				} else {
					if (delimCursor > 0) {
						// セパレータではなかった
						baos.write(YMSG_DELIM, 0, delimCursor);
						delimCursor = 0;
					} else {
						baos.write(i);
					}
				}
			}

			// 余りがあれば処理
			baos.flush();
			if (baos.size() > 0 && key != null) {
				this.body.add(new Entry(key, data));
				key = null;
			}
		} finally {
			if (in != null) {
				try { in.close(); } catch (Exception ignored) {}
			}

			if (baos != null) {
				try { baos.close(); } catch (Exception ignored) {}
			}
		}
	}

	/**
	 * データ部のキーと値のペアを格納するためのクラスです。
	 *
	 * @author Teruhiko Kusunoki&lt;<a href="teru.kusu@gmail.com">teru.kusu@gmail.com</a>&gt;
	 *
	 */
	public static class Entry {
		private final String key;
		private byte[] value;

		/**
		 * キーと値を指定してオブジェクトを生成します。
		 *
		 * @param key キー
		 * @param value 値
		 */
		public Entry(String key, byte[] value) {
			this.key = key;
			this.value = value;
		}

		/**
		 * キーを指定してオブジェクトを生成します。
		 *
		 * @param key キー
		 */
		public Entry(String key) {
			this(key, null);
		}

		/**
		 * key を取得します。
		 * @return key
		 */
		public String getKey() {
			return key;
		}

		/**
		 * value を取得します。
		 * @return value
		 */
		public byte[] getValue() {
			return value;
		}

		/**
		 * value を設定します。
		 * @param value セットする value
		 */
		public void setValue(byte[] value) {
			this.value = value;
		}
	}
}
