package com.chatmessenger.android.ym;

import java.util.ArrayList;
import java.util.List;

/**
 * 会話管理クラス。
 *
 * @author Teruhiko Kusunoki&lt;<a href="teru.kusu@gmail.com">teru.kusu@gmail.com</a>&gt;
 *
 */
public class ConversationManager {

    private static final ConversationManager instance = new ConversationManager();

    private String currentId;
    private final List<Conversation> conversationList = new ArrayList<Conversation>();

    /**
     *
     * @return
     */
    public static ConversationManager getInstance() {
        return instance;
    }

    /**
     * 新しいオブジェクトを生成します。
     *
     */
    public ConversationManager() {
        super();
    }

    /**
     * 会話を取得します。
     *
     * @param senderId 送信に使用するID
     * @param recipientId 会話相手のID
     * @param create 会話が存在しなければ新しく作る場合は true
     * @return 会話です
     */
    public synchronized Conversation getConversation(String senderId, String recipientId, boolean create) {
        List<Conversation> convList = getConversationList();
        for (Conversation c : convList) {
            if (recipientId.equals(c.getRecipientId()) && senderId.equals(c.getSenderId())) {
                return c;
            }
        }

        // 新しく作成
        if (!create) {
            return null;
        }

        Conversation c = new Conversation(senderId, recipientId);
        convList.add(c);

        return c;
    }

    /**
     * 保持している会話を全て破棄します。
     */
    public synchronized void clear() {
        getConversationList().clear();
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
     * conversationList を取得します。
     * @return conversationList
     */
    public List<Conversation> getConversationList() {
        return conversationList;
    }
}
