package com.chatmessenger.android.util;

/**
 * 回復不可能な例外を表すクラスです。
 *
 * @author Teruhiko Kusunoki&lt;<a href="teru.kusu@gmail.com">teru.kusu@gmail.com</a>&gt;
 *
 */
public class SystemException extends RuntimeException {
	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = -1838491750295932808L;

	/**
	 * 新しいオブジェクトを生成します。
	 */
	public SystemException() {
		super();
	}

	/**
	 * メッセージと原因を指定して新しいオブジェクトを生成します。
	 *
	 * @param message メッセージ
	 * @param cause 原因
	 */
	public SystemException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * メッセージを指定して新しいオブジェクトを生成します。
	 *
	 * @param message メッセージ
	 */
	public SystemException(String message) {
		super(message);
	}

	/**
	 * 原因を指定して新しいオブジェクトを生成します。
	 *
	 * @param cause 原因
	 */
	public SystemException(Throwable cause) {
		super(cause);
	}
}
