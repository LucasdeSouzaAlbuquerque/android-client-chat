package com.chatmessenger.android.ym;

import java.io.Serializable;
import java.util.Date;

/**
 * 友達を表すクラスです。
 * このクラスはスレッドセーフでは有りません。
 *
 * @author Teruhiko Kusunoki&lt;<a href="teru.kusu@gmail.com">teru.kusu@gmail.com</a>&gt;
 *
 */
public class Buddy implements Serializable {
	private String id;
	private int status = Constants.YMSG_STATUS_OFFLINE;
	private String statusMessage;
	private Date lastContactDate;
	private boolean addRequestPending = false;

	/**
	 * 新オブジェクトを生成します。
	 */
	public Buddy() {
		super();
	}

	/**
	 * 新しいオブエジェクトを生成します。
	 *
	 * @param id
	 * @param statusMessage
	 * @param status
	 * @param 最後にコンタクトを取った日時
	 */
	public Buddy(String id, String statusMessage, int status, Date lastContactDate) {
		super();
		this.id = id;
		this.statusMessage = statusMessage;
		this.status = status;
		this.lastContactDate = lastContactDate;
	}


	/**
	 * addRequestPending を取得します。
	 * @return addRequestPending
	 */
	public boolean isAddRequestPending() {
		return addRequestPending;
	}

	/**
	 * addRequestPending を設定します。
	 * @param addRequestPending セットする addRequestPending
	 */
	public void setAddRequestPending(boolean addRequestPending) {
		this.addRequestPending = addRequestPending;
	}

	/**
	 * id を取得します。
	 * @return id
	 */
	public String getId() {
		return id;
	}

	/**
	 * id を設定します。
	 * @param id セットする id
	 */
	public void setId(String id) {
		this.id = id;
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
	 * lastContactDate を取得します。
	 * @return lastContactDate
	 */
	public Date getLastContactDate() {
		return lastContactDate;
	}

	/**
	 * lastContactDate を設定します。
	 * @param lastContactDate セットする lastContactDate
	 */
	public void setLastContactDate(Date lastContactDate) {
		this.lastContactDate = lastContactDate;
	}
}
