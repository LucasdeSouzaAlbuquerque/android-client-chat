package com.chatmessenger.android.ym;

/**
 * @author Teruhiko Kusunoki&lt;<a href="teru.kusu@gmail.com">teru.kusu@gmail.com</a>&gt;
 *
 */
public interface SessionListener {
	void onLogin(SessionEvent event);
	void onLoginFailure(SessionEvent event);
	void onLogout(SessionEvent event);
	void onBuddyLogin(SessionEvent event);
	void onBuddyLogout(SessionEvent event);
    void onBuddyStatusChange(SessionEvent event);
    void onChangeStatusMessage(SessionEvent event);
    void onChangeStatusMessageFailure(SessionEvent event);
    /**
     * 他の環境からログインしたためにサーバーから切断された場合に呼び出されます。
     *
     * @param event イベント
     */
    void onDisconnect(SessionEvent event);
	void onSendMessageFailure(SessionEvent event);
	void onMessageReceived(SessionEvent event);
    void onMessageSent(SessionEvent event);
}
