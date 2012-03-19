package com.chatmessenger.android.view.ym;

import java.io.IOException;
import java.util.List;

import com.chatmessenger.android.R;
import com.chatmessenger.android.ym.Buddy;
import com.chatmessenger.android.ym.BuddyList;
import com.chatmessenger.android.ym.Constants;
import com.chatmessenger.android.ym.Conversation;
import com.chatmessenger.android.ym.Message;
import com.chatmessenger.android.ym.Session;
import com.chatmessenger.android.ym.SessionAdapter;
import com.chatmessenger.android.ym.SessionEvent;
import com.chatmessenger.android.ym.User;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

/**
 * @author Teruhiko Kusunoki&lt;<a
 *         href="teru.kusu@gmail.com">teru.kusu@gmail.com</a>&gt;
 *
 */
public class YmsgService extends Service implements Constants {

    private Session session;

    private final Handler handler = new Handler();

    // local binder
    private final IBinder binder = new LocalBinder();

    // 現在表示中の会話相手
    private String currentRecipientId;


    public class LocalBinder extends Binder {
        YmsgService getService() {
            return YmsgService.this;
        }
    }

    /**
     * 自分のステータスメッセージを変更します。
     * @param status ステータスコード
     * @param statusMessage ステータスメッセージ
     */
    public void changeStatus(int status, String statusMessage) {
        Session session = getSession();
        if (session != null) {
            session.changeStatus(status, statusMessage);
        }
    }

    /**
     * ログアウトします
     */
    public void logout() {
        Session session = getSession();
        if (session != null) {
           try { session.close(); } catch (Exception ignored) {}
           session = null;
        }
    }


    /*
     * (non-Javadoc)
     *
     * @see android.app.Service#onBind(android.content.Intent)
     */
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    /*
     * (non-Javadoc)
     *
     * @see android.app.Service#onCreate()
     */
    @Override
    public void onCreate() {
//        Toast.makeText(this, "Service start", Toast.LENGTH_LONG).show();
    }

    /*
     * (non-Javadoc)
     *
     * @see android.app.Service#onDestroy()
     */
    @Override
    public void onDestroy() {
//        Toast.makeText(this, "Service onDestroy() called", Toast.LENGTH_LONG).show();
        Session session = getSession();
        if (session != null) {
            try {
                session.close();
            } catch (IOException e) {
                Log.v(LOG_TAG, "ymsg session close failed.", e);
            }
        }

        super.onDestroy();
    }

    /**
     * session を設定します。
     *
     * @param session セットする session
     */
    public void setSession(Session session) {
        this.session = session;
    }

    /**
     *
     * @return
     */
    protected Session getSession() {
        return this.session;
    }

    /**
     * ログインします。
     *
     * @param id ID
     * @param password パスワード
     * @param hidden ログイン状態を隠す場合は true
     */
    public void login(String id, String password, boolean hidden) {
        Session session = getSession();
        if (session != null) {
           try { session.close(); } catch (Exception ignored) {}
        }
        session = new Session();
        session.addSessionListener(new MySessionListener());
        setSession(session);

        session.login(id, password, hidden);
    }

    public void sendMessage(Message message) {
        getSession().sendMessage(message);
    }

    public BuddyList getBuddyList() {
        return getSession().getBuddyList();
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
        Conversation c = getSession().getConversation(senderId, recipientId, create);
        return c;
    }

    protected class MySessionListener extends SessionAdapter {
        /**
         * 新しいオブジェクトを生成します。
         *
         */
        public MySessionListener() {
            super();
        }

        /*
         * (non-Javadoc)
         *
         * @see
         * org.terukusu.ahoomsgr.SessionAdapter#onLogin(org.terukusu.ahoomsgr
         * .SessionEvent)
         */
        @Override
        public void onLogin(SessionEvent event) {
            Intent i = new Intent(ACTION_LOGIN);
            sendBroadcast(i);
        }

        /* (non-Javadoc)
         * @see org.terukusu.ahoomsgr.SessionAdapter#onMessageSent(org.terukusu.ahoomsgr.SessionEvent)
         */
        @Override
        public void onMessageSent(SessionEvent event) {
            Intent i = new Intent(ACTION_MESSAGE_SENT);
            sendBroadcast(i);
        }

        /*
         * (non-Javadoc)
         *
         * @see
         * org.terukusu.ahoomsgr.SessionAdapter#onLoginFailure(org.terukusu.
         * ahoomsgr.SessionEvent)
         */
        @Override
        public void onLoginFailure(SessionEvent event) {
            Intent i = new Intent(ACTION_LOGIN_FAILED);
            sendBroadcast(i);
        }

        /* (non-Javadoc)
         * @see org.terukusu.ahoomsgr.SessionAdapter#onBuddyLogin(org.terukusu.ahoomsgr.SessionEvent)
         */
        @Override
        public void onBuddyLogin(SessionEvent event) {
            Buddy b = (Buddy) event.getParam("buddy");
            Intent intent = new Intent(ACTION_BUDDY_ONLINE);
            intent.putExtra("buddy", b);
            sendBroadcast(intent);

            final String msg = b.getId() + getText(R.string.sysmsg_buddy_login);
            getHandler().post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(YmsgService.this, msg, Toast.LENGTH_SHORT).show();
                }
            });
       }

        /* (non-Javadoc)
         * @see org.terukusu.ahoomsgr.SessionAdapter#onBuddyLogout(org.terukusu.ahoomsgr.SessionEvent)
         */
        @Override
        public void onBuddyLogout(SessionEvent event) {
            Buddy b = (Buddy) event.getParam("buddy");
            Intent intent = new Intent(ACTION_BUDDY_OFFLINE);
            intent.putExtra("buddy", b);
            sendBroadcast(intent);

            final String msg = b.getId() + getText(R.string.sysmsg_buddy_logoff);
            getHandler().post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(YmsgService.this, msg, Toast.LENGTH_SHORT).show();
                }
            });
        }

        /* (non-Javadoc)
         * @see org.terukusu.ahoomsgr.SessionAdapter#onBuddyStatusChange(org.terukusu.ahoomsgr.SessionEvent)
         */
        @Override
        public void onBuddyStatusChange(SessionEvent event) {
            Buddy b = (Buddy) event.getParam("buddy");
            Intent intent = new Intent(ACTION_BUDDY_STATUS_CHANGE);
            intent.putExtra("buddy", b);
            sendBroadcast(intent);
        }

        /* (non-Javadoc)
         * @see org.terukusu.ahoomsgr.SessionAdapter#onLogout(org.terukusu.ahoomsgr.SessionEvent)
         */
        @Override
        public void onLogout(SessionEvent event) {
            // TODO Auto-generated method stub
            super.onLogout(event);
        }

        /* (non-Javadoc)
         * @see org.terukusu.ahoomsgr.SessionAdapter#onChangeStatusMessage(org.terukusu.ahoomsgr.SessionEvent)
         */
        @Override
        public void onChangeStatusMessage(SessionEvent event) {
            Intent intent = new Intent(ACTION_MY_STATUS_CHANGE);
            sendBroadcast(intent);
        }

        /* (non-Javadoc)
         * @see org.terukusu.ahoomsgr.SessionAdapter#onChangeStatusMessageFailure(org.terukusu.ahoomsgr.SessionEvent)
         */
        @Override
        public void onChangeStatusMessageFailure(SessionEvent event) {
            Log.e(LOG_TAG, "onChangeStatusMessageFailure() called: cause=" + event.getParam("exception"));
            // TODO Auto-generated method stub
            super.onChangeStatusMessageFailure(event);
        }

        /* (non-Javadoc)
         * @see org.terukusu.ahoomsgr.SessionAdapter#onDisconnect(org.terukusu.ahoomsgr.SessionEvent)
         */
        @Override
        public void onDisconnect(SessionEvent event) {
            final String msg = getText(R.string.app_name).toString() + getText(R.string.sysmsg_disconnect).toString();

            getHandler().post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(YmsgService.this, msg, Toast.LENGTH_LONG).show();
                }
            });

            Intent intent = new Intent(ACTION_DISCONNECT);
            sendBroadcast(intent);
        }

        /*
         * (non-Javadoc)
         *
         * @see
         * org.terukusu.ahoomsgr.SessionAdapter#onSendMessageFailure(org.terukusu
         * .ahoomsgr.SessionEvent)
         */
        @Override
        public void onSendMessageFailure(SessionEvent event) {
            // TODO Auto-generated method stub
            super.onSendMessageFailure(event);
        }

        /*
         * (non-Javadoc)
         *
         * @see
         * org.terukusu.ahoomsgr.SessionAdapter#onMessageReceived(org.terukusu
         * .ahoomsgr.SessionEvent)
         */
        @Override
        public void onMessageReceived(SessionEvent event) {
            Message m = (Message)event.getParam("message");

            // 会話相手
            String recipientId = getCurrentRecipientId();
            if (recipientId == null || !recipientId.equals(m.getSenderId())) {
                NotificationManager notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);

                Intent notifyIntent = new Intent(YmsgService.this, BuddyListActivity.class);
                notifyIntent.putExtra("notifyId", R.string.app_name);
                notifyIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                PendingIntent pendingIntent = PendingIntent.getActivity(YmsgService.this, 0, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                Notification notification = new Notification(android.R.drawable.stat_notify_chat, m.getSenderId() + getText(R.string.notify_message_received), System.currentTimeMillis());
                notification.defaults |= Notification.DEFAULT_ALL;
                notification.defaults |= Notification.DEFAULT_LIGHTS;
                notification.setLatestEventInfo(
                        getApplicationContext(),
                        getText(R.string.app_name),
                        m.getSenderId() + getText(R.string.notify_message_received),
                        pendingIntent);

                notificationManager.notify(notifyIntent.getIntExtra("notifyId", -1), notification);
            }

            Intent intent = new Intent(ACTION_MESSAGE_RECEIVED);
            intent.putExtra("message", m);
            sendBroadcast(intent);
        }
    }

    public boolean isLogin() {
        return (getSession() != null && getSession().isLogin());
    }

    /**
     * handler を取得します。
     *
     * @return handler
     */
    protected Handler getHandler() {
        return handler;
    }

    protected User getUser() {
        return getSession().getUser();
    }

    /**
     * currentRecipientId を取得します。
     * @return currentRecipientId
     */
    protected String getCurrentRecipientId() {
        return currentRecipientId;
    }

    /**
     * 会話リストを取得します。
     * @return 会話リスト
     */
    public List<Conversation> getConversationList() {
        if (isLogin()) {
            return getSession().getConversationList();
        }

        return null;
    }

    /**
     * currentRecipientId を設定します。
     * @param currentRecipientId セットする currentRecipientId
     */
    protected void setCurrentRecipientId(String currentRecipientId) {
        this.currentRecipientId = currentRecipientId;
    }
}
