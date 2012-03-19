package com.chatmessenger.android.ym;

import java.util.ArrayList;
import java.util.List;

/**
 * 会話を表すクラスです。
 * このクラスはスレッドセーフでは有りません。
 *
 * @author Teruhiko Kusunoki&lt;<a href="teru.kusu@gmail.com">teru.kusu@gmail.com</a>&gt;
 *
 */
public class Conversation {
	private final List<Message> messageList = new ArrayList<Message>();
	private String senderId;
	private String recipientId;

	/**
	 *
	 * 新しいオブジェクトを生成します。
	 *
	 */
	public Conversation() {
		super();
	}

	/**
	 * @param senderId
	 * @param recipientId
	 */
	public Conversation(String senderId, String recipientId) {
		super();
		this.senderId = senderId;
		this.recipientId = recipientId;
	}

	/**
	 * メッセージを追加します。
	 * 最大件数を超えた場合は最初のメッセージを削除してから追加します。
	 *
	 * @param message メッセージです。
	 */
	public synchronized void addMessage(Message message) {
	    List<Message> messageList = getMessageList();
	    if (messageList.size() > Constants.MAX_MESSAGE_COUNT) {
	        messageList.remove(0);
	    }
		messageList.add(message);
	}

	/**
	 * messageList を取得します。
	 * @return messageList
	 */
	public List<Message> getMessageList() {
		return messageList;
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
    protected void setRecipientId(String recipientId) {
        this.recipientId = recipientId;
    }

    public synchronized Message getLasMessage() {
        List<Message> list = getMessageList();
        if (!list.isEmpty()) {
            return  list.get(list.size() - 1);
        }

        return null;
    }
}
