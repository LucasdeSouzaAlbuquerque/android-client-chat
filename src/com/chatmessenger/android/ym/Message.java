package com.chatmessenger.android.ym;

import java.io.Serializable;
import java.util.Date;

/**
 * メッセージを表すクラスです。
 *
 * @author Teruhiko Kusunoki&lt;<a href="teru.kusu@gmail.com">teru.kusu@gmail.com</a>&gt;
 *
 */
public class Message implements Serializable {
	private String senderId;
	private String recipientId;
	private Date date;
	private String text;

	/**
	 * 新しいオブジェクトを生成します。
	 */
	public Message() {
		super();
	}

	/**
	 * @param senderId
	 * @param recipientId
	 * @param date
	 * @param text
	 */
	public Message(String senderId, String recipientId, Date date, String text) {
		super();
		this.senderId = senderId;
		this.recipientId = recipientId;
		this.date = date;
		this.text = text;
	}

	/**
	 * receiveDate を取得します。
	 * @return receiveDate
	 */
	public Date getDate() {
		return date;
	}

	/**
	 * receiveDate を設定します。
	 * @param receiveDate セットする receiveDate
	 */
	public void setDate(Date receiveDate) {
		this.date = receiveDate;
	}

	/**
	 * text を取得します。
	 * @return text
	 */
	public String getText() {
		return text;
	}

	/**
	 * text を設定します。
	 * @param text セットする text
	 */
	public void setText(String text) {
		this.text = text;
	}

	/**
	 * senderId を取得します。
	 * @return senderId
	 */
	public String getSenderId() {
		return senderId;
	}

	/**
	 * senderId を設定します。
	 * @param senderId セットする senderId
	 */
	public void setSenderId(String senderId) {
		this.senderId = senderId;
	}

	/**
	 * recipientId を取得します。
	 * @return recipientId
	 */
	public String getRecipientId() {
		return recipientId;
	}

	/**
	 * recipientId を設定します。
	 * @param recipientId セットする recipientId
	 */
	public void setRecipientId(String recipientId) {
		this.recipientId = recipientId;
	}
}
