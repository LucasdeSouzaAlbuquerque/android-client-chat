package com.chatmessenger.android.ym;

import java.util.ArrayList;
import java.util.List;

/**
 * ユーザーを表すクラスです。
 * 
 * @author Teruhiko Kusunoki&lt;<a href="teru.kusu@gmail.com">teru.kusu@gmail.com</a>&gt;
 *
 */
public class User {
	private String id;
	private String currentId;
	private String password;
	private final List<String> extraId = new ArrayList<String>();
	private int status;
	private String statusMessage;
	
	/**
	 * 
	 */
	public User() {
		super();
	}

	/**
	 * @param id
	 * @param password
	 */
	public User(String id, String password) {
		super();
		this.id = id;
		this.password = password;
	}

	/**
	 * id を取得します。
	 * @return id
	 */
	public String getId() {
		return id;
	}

	/**
	 * currentId を取得します。
	 * @return currentId
	 */
	public String getCurrentId() {
		return currentId;
	}

	/**
	 * currentId を設定します。
	 * @param currentId セットする currentId
	 */
	public void setCurrentId(String currentId) {
		this.currentId = currentId;
	}

	/**
	 * id を設定します。
	 * @param id セットする id
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * password を取得します。
	 * @return password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * password を設定します。
	 * @param password セットする password
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * extraId を取得します。
	 * @return extraId
	 */
	public List<String> getExtraId() {
		return extraId;
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
	 * statusMessage を取得します。
	 * @return statusMessage
	 */
	public String getStatusMessage() {
		return statusMessage;
	}

	/**
	 * statusMessage を設定します。
	 * @param statusMessage セットする statusMessage
	 */
	public void setStatusMessage(String statusMessage) {
		this.statusMessage = statusMessage;
	}
	
	public void addExtraId(List<String> ids) {
		this.extraId.addAll(ids);
	}
	
	
}
