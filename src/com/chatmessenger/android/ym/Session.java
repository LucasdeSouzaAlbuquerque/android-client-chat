package com.chatmessenger.android.ym;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.chatmessenger.android.util.SystemException;
import com.chatmessenger.android.util.Utils;
import com.chatmessenger.android.util.ym.HttpDownloader;
import com.chatmessenger.android.util.ym.HttpResponse;
import com.chatmessenger.android.ym.YmsgPacket.Entry;



import android.util.Log;

/**
 * @author Teruhiko Kusunoki&lt;<a
 *         href="teru.kusu@gmail.com">teru.kusu@gmail.com</a>&gt;
 *
 */
public class Session implements Constants {

    private static final Pattern TAG_PATTERN = Pattern.compile("<[^>]*?>|\u001b\\[[^\u006d]*?\u006d");
    private User user;
    private BuddyList buddyList;
    private ConversationManager conversationManager = ConversationManager.getInstance();
    private Socket sock;
    private int sessionId;
    private Thread sockReceiveThread;
    private boolean isClosed = false;
    private final List<SessionListener> sessionListenerList = new ArrayList<SessionListener>();

    private final ExecutorService executorService = Executors.newSingleThreadExecutor(new ThreadFactory() {
        @Override
        public Thread newThread(Runnable r) {
            return new Thread(r, "session-task-executor");
        }
    });

    /**
	 *
	 */
    public Session() {
    }

    /**
     * ログインします。
     *
     * @param id ID
     * @param password パスワード
     * @param hidden ログイン状態を隠す場合は true
     */
    public void login(final String id, final String password, final boolean hidden) {
        this.executorService.execute(new Runnable() {

            @Override
            public void run() {
                try {
                    if (loginInternal(id, password, hidden)) {
                        fireOnLogin(null);
                    } else {
                        fireOnLoginFailuer(null);
                    }
                } catch (Exception e) {
                    Log.w(LOG_TAG, "login failed.", e);
                    Map<String, Object> params = new HashMap<String, Object>();
                    params.put("exception", e);
                    fireOnLoginFailuer(params);
                }
            }
        });
    }

    protected synchronized boolean loginInternal(String id, String password, boolean hidden) throws YmsgException {

        try {
            Log.v(Constants.LOG_TAG, "retrieve login server ip address: url=" + Constants.YMSG_AUTH_PRE_URL);
            HttpDownloader d = new HttpDownloader(Constants.YMSG_AUTH_PRE_URL);
            HttpResponse res = d.execute();

            ByteArrayInputStream bais = new ByteArrayInputStream(res.getBody());

            Properties p = new Properties();
            p.load(bais);

            String serverIp = p.getProperty("CS_IP_ADDRESS");

            Log.v(Constants.LOG_TAG, "serverIp: " + serverIp);

            YmsgPacket outPacket = new YmsgPacket();
            outPacket.setProtocolVersion(0x10);
            outPacket.setVendorId(0x64);
            outPacket.setService(0x57);
            outPacket.setStatus(0);
            outPacket.addValue("1", id);

            OutputStream os;
            InputStream is;

            InetAddress addr = InetAddress.getByName(serverIp);
            Socket sock = new Socket(addr, 5050);

            os = sock.getOutputStream();
            outPacket.writeTo(os);

            is = sock.getInputStream();
            YmsgPacket inPacket = new YmsgPacket(is);

            String challenge = inPacket.getValueAsString("94");
            int sessionId = inPacket.getSessionId();

            Map<String, String> params = new HashMap<String, String>();
            params.put("src", "ymsgr");
            params.put("ts", "");
            params.put("login", id);
            params.put("passwd", password);
            params.put("chal", challenge);

            HttpDownloader d2 = new HttpDownloader(Constants.YMSG_AUTH_TOKEN_URL, params);
            HttpResponse res2 = d2.execute();

            String res2BodyStr = new String(res2.getBody(), "8859_1");
            Log.v(Constants.LOG_TAG, "res2BodyStr: " + res2BodyStr);

            String line = null;
            BufferedReader br = new BufferedReader(new StringReader(res2BodyStr), 64);
            line = br.readLine();

            if (line == null || !"0".equals(line)) {
                Map<String, Object> errParams = new HashMap<String, Object>();
                errParams.put(ERROR_AUTH_CAUSE, ERROR_AUTH_INVALID_ACCOUNT);
                fireOnLoginFailuer(errParams);
                if (sock != null) {
                    try {
                        sock.shutdownInput();
                    } catch (Exception ignore) {
                    }
                    try {
                        sock.shutdownOutput();
                    } catch (Exception ignore) {
                    }
                    try {
                        sock.close();
                    } catch (Exception ignore) {
                    }
                }
                return false;
            }

            line = br.readLine();
            String ymsgr = line.substring(line.indexOf('=') + 1);
            Log.v(Constants.LOG_TAG, "ymsgr: " + ymsgr);

            line = br.readLine();
            String partnerId = line.substring(line.indexOf('=') + 1);
            Log.v(Constants.LOG_TAG, "partnerId: " + partnerId);

            br.close();

            params.clear();
            params.put("src", "ymsgr");
            params.put("ts", "");
            params.put("token", ymsgr);
            Log.v(Constants.LOG_TAG, Utils.hexDump(ymsgr.getBytes()));

            HttpDownloader d3 = new HttpDownloader(Constants.YMSG_AUTH_LOGIN_URL, params);
            HttpResponse res3 = d3.execute();

            String res3BodyStr = new String(res3.getBody(), "8859_1");
            Log.v(Constants.LOG_TAG, "res3Body: " + Utils.hexDump(res3.getBody()));

            br = new BufferedReader(new StringReader(res3BodyStr));

            line = br.readLine();

            if (line == null || !"0".equals(line)) {
                Map<String, Object> errParams = new HashMap<String, Object>();
                errParams.put(ERROR_AUTH_CAUSE, ERROR_AUTH_INVALID_TOKEN);
                fireOnLoginFailuer(errParams);
                if (sock != null) {
                    try {
                        sock.shutdownInput();
                    } catch (Exception ignore) {
                    }
                    try {
                        sock.shutdownOutput();
                    } catch (Exception ignore) {
                    }
                    try {
                        sock.close();
                    } catch (Exception ignore) {
                    }
                }
                return false;
            }

            line = br.readLine();
            String crumb = line.split("=")[1];
            Log.v(Constants.LOG_TAG, "crumb: " + crumb);

            line = br.readLine();
            String yCookie = line.substring(line.indexOf('=') + 1, line.indexOf(';'));
            Log.v(Constants.LOG_TAG, "yCookie: " + yCookie);

            line = br.readLine();
            String tCookie = line.substring(line.indexOf('=') + 1, line.indexOf(';'));
            Log.v(Constants.LOG_TAG, "tCookie: " + tCookie);

            String crypt = crumb + challenge;
            byte[] md5 = ChallengeResponseUtility.md5(crypt);
            String y64str = ChallengeResponseUtility.yahoo64(md5);

            YmsgPacket outPacket2 = new YmsgPacket();
            outPacket2.setProtocolVersion(0x10);
            outPacket2.setVendorId(0x64);
            outPacket2.setService(0x54);
            if (hidden) {
                outPacket2.setStatus(YMSG_STATUS_INVISIBLE);
            } else {
                outPacket2.setStatus(YMSG_STATUS_OFFLINE);
            }
            outPacket2.addValue("1", id);
            outPacket2.addValue("0", id);
            outPacket2.addValue("277", yCookie);
            outPacket2.addValue("278", tCookie);
            outPacket2.addValue("307", y64str);
            outPacket2.addValue("244", "4186047");
            outPacket2.addValue("2", id);
            outPacket2.addValue("2", "1");
            outPacket2.addValue("98", "jp");
            outPacket2.addValue("135", "9.0.0.1727");

            outPacket2.writeTo(os);
            os.flush();

            BuddyList buddyList = new BuddyList();
            User user = new User();

            YmsgPacket p1 = new YmsgPacket(is);
            if (p1.getService() == YMSG_SERVICE_LIST) {
                // ユーザー情報
                String primaryId = p1.getValueAsString("3");
                Log.v(LOG_TAG, "primaryId: " + primaryId);

                String currentId = p1.getValueAsString("2");
                Log.v(LOG_TAG, "currentId: " + currentId);

                String[] extraIds = p1.getValueAsString("89").split(",");
                List<String> extraIdList = Arrays.asList(extraIds);
                Log.v(LOG_TAG, "extraIdList: " + extraIdList);

                user.setId(primaryId);
                user.setPassword(password);
                user.setCurrentId(id); // ログインに使ったID
                user.addExtraId(extraIdList);
                if (hidden) {
                    user.setStatus(YMSG_STATUS_OFFLINE);
                } else {
                    user.setStatus(YMSG_STATUS_ONLINE);
                }

                this.user = user;
            }

            // 友達リスト
            YmsgPacket p2 = null;
            String groupName = null;
            while (true) {
                p2 = new YmsgPacket(is);
                Log.v(LOG_TAG, "YMSG received: p2: " + p2);
                if (p2.getService() != YMSG_SERVICE_LIST_V15) {
                    break;
                }

                for (Entry entry : p2.getEntries()) {
                    if ("65".equals(entry.getKey())) {
                        groupName = new String(entry.getValue());
                        Log.v(LOG_TAG, "group foud: " + groupName);
                    } else if ("7".equals(entry.getKey())) {
                        Buddy buddy = new Buddy();
                        buddy.setId(new String(entry.getValue()));
                        buddyList.addBuddy(groupName, buddy);
                        Log.v(LOG_TAG, "buddy ID foud: " + buddy.getId());
                    }
                }
            }

            YmsgPacket p3 = p2;
            Log.v(LOG_TAG, "YMSG use p2 as p3: " + p3);
            if (p3.getService() == YMSG_SERVICE_STATUS_V15) {
                // 友達の状態
                String buddyId = null;
                int status = YMSG_STATUS_OFFLINE;
                String statusMessage = null;
                for (Entry entry : p3.getEntries()) {
                    if ("7".equals(entry.getKey())) {
                        if (buddyId != null) {
                            Buddy buddy = buddyList.findById(buddyId);
                            if (buddy != null) {
                                buddy.setStatus(status);
                                buddy.setStatusMessage(statusMessage);
                            }
                            buddyId = null;
                            status = YMSG_STATUS_OFFLINE;
                            statusMessage = null;
                        }

                        buddyId = new String(entry.getValue());
                        Log.v(LOG_TAG, "buddy id found in status_v15: " + buddyId);
                    } else if ("10".equals(entry.getKey())) {
                        status = Integer.parseInt(new String(entry.getValue()));
                        Log.v(LOG_TAG, "status found in status_v15: " + new String(entry.getValue()));
                    } else if ("19".equals(entry.getKey())) {
                        statusMessage = new String(entry.getValue());
                        Log.v(LOG_TAG, "status message found in status_v15: " + new String(entry.getValue()));
                    }
                }

                if (buddyId != null) {
                    // 最後の一人
                    Buddy buddy = buddyList.findById(buddyId);
                    if (buddy != null) {
                        buddy.setStatus(status);
                        buddy.setStatusMessage(statusMessage);
                    }
                    buddyId = null;
                    status = 0;
                    statusMessage = null;
                }

                this.buddyList = buddyList;
            }

            if (!user.getCurrentId().equalsIgnoreCase(getConversationManager().getCurrentId())) {
                // 異なるIDでログインした場合は会話を削除
                getConversationManager().clear();
            }

            getConversationManager().setCurrentId(user.getCurrentId());

            this.sockReceiveThread = new Thread(new SocketHandler());
            this.sockReceiveThread.start();

            this.sock = sock;
            this.sessionId = sessionId;

        } catch (RuntimeException e) {
            throw e;
        } catch (YmsgException e) {
            throw e;
        } catch (Exception e) {
            throw new YmsgException("login failed", e);
        }

        return true;
    }

    protected void handleYmsgPacket(YmsgPacket packet) {
        int service = packet.getService();
        Map<String, Object> params = new HashMap<String, Object>();
        String str = null;
        String id = null;
        Buddy b = null;
        int status = -1;

        switch (service) {
        case YMSG_SERVICE_LIST_V15:
            break;
        case YMSG_SERVICE_MESSAGE:
            //  メッセージが複数あるかもしれない
            Message msg = null;
            for (Entry entry : packet.getEntries()) {
                if (!(entry.getKey().equals("4") || entry.getKey().equals("14") || entry.getKey().equals("15"))) {
                    // 関係ない要素
                    continue;
                }

                if (msg == null) {
                    msg = new Message();
                    msg.setRecipientId(getUser().getCurrentId());
                }

                if (entry.getKey().equals("4")) {
                    // 送信者ID
                    msg.setSenderId(new String(entry.getValue()));
                } else if (entry.getKey().equals("14")) {
                    // 本文
                    // タグの除去
                    StringBuffer sb = new StringBuffer();
                    Matcher m = TAG_PATTERN.matcher(new String(entry.getValue()));
                    while (m.find()) {
                        m.appendReplacement(sb, "");
                    }
                    m.appendTail(sb);
                    msg.setText(sb.toString());
                } else if (entry.getKey().equals("15")) {
                    // 本文
                    String timeStr = new String(entry.getValue());
                    long timeLong = Long.parseLong(timeStr) * 1000;
                    Date date = new Date(timeLong);

                    msg.setDate(date);
                }

                if (msg.getText() != null && msg.getSenderId() != null && msg.getDate() != null) {
                    // 全部情報が揃ったので表示
                    Conversation c = getConversation(msg.getRecipientId(), msg.getSenderId(), true);
                    c.addMessage(msg);

                    params.clear();
                    params.put("message", msg);

                    updateBuddyLastContact(msg.getSenderId());

                    // メッセージオブジェクトをリセット
                    msg = null;

                    fireOnMessageReceived(params);
                }
            }

            if (msg != null) {
                // オフメではなく通常のメッセージはここを通る

                // 通常メッセージは時刻がサーバーから来ないので現在時刻を設定
                if (msg.getDate() == null) {
                    msg.setDate(new Date());
                }

                Conversation c = getConversation(msg.getRecipientId(), msg.getSenderId(), true);
                c.addMessage(msg);

                params.clear();
                params.put("message", msg);

                updateBuddyLastContact(msg.getSenderId());

                // メッセージオブジェクトをリセット
                msg = null;

                fireOnMessageReceived(params);
            }

            break;
        case YMSG_SERVICE_LOGOFF:
            id = packet.getValueAsString("7");
            if (id == null || id.length() == 0) {
                // 別マシンからのログインなどでこのセッションに対してログアウトを要求された
                try {
                    close();
                } catch (IOException ignored) {
                    Log.w(LOG_TAG, "session close failure.");
                }
                fireOnDisconnect(null);
            } else {
                b = getBuddyList().findById(id);
                if (b != null) {
                    b.setStatus(YMSG_STATUS_OFFLINE);
                    params.put("buddy", b);
                    fireOnBuddyLogout(params);
                }
            }
            break;
        case YMSG_SERVICE_STATUS_V15:
            // TODO複数件の一括変更対応
            id = packet.getValueAsString("7");
            status = Integer.parseInt(packet.getValueAsString("10"));
            b = getBuddyList().findById(id);
            if (b != null) {
                b.setStatus(status);
                if (b.getStatus() == YMSG_STATUS_ONLINE) {
                    // 友達がログインした
                    params.put("buddy", b);
                    fireOnBuddyLogin(params);
                }
            }
            break;
        case YMSG_SERVICE_STATUS_UPDATE:
            // TODO複数件の一括変更対応
            id = packet.getValueAsString("7");
            status = Integer.parseInt(packet.getValueAsString("10"));
            str = packet.getValueAsString("19");
            b = getBuddyList().findById(id);
            if (b != null) {
                b.setStatus(status);
                // TODO 多忙などに対応
                b.setStatusMessage(str);
                params.put("buddy", b);
                fireOnBuddyStatusChange(params);
            }
            break;
        case YMSG_SERVICE_DISCONNECT:
            fireOnDisconnect(null);
            break;
        }
    }

    /**
     * ログイン状態の可視状態を設定します。
     * このメソッドは非同期で実行されます。
     *
     * @param visible ログイン状態を隠すならばfalse
     */
    public void setVisibility(final boolean visible) {
        this.executorService.execute(new Runnable() {

            @Override
            public void run() {
                try {
                    YmsgPacket p = new YmsgPacket();
                    p.setProtocolVersion(YMSG_PROTOCOL_VERSION);
                    p.setVendorId(YMSG_VENDER_ID);
                    p.setService(YMSG_SERVICE_VISIBILITY_TOGGLE);
                    p.setSessionId(getSessionId());
                    if (visible) {
                        p.addValue("13", "1");
                    }  else {
                        p.addValue("13", "2");
                    }
                    sendPacket(p);
                } catch (IOException ignored) {
                    Log.w(LOG_TAG, "visibility toggle failed: ", ignored);
                }
            }
        });

    }

    /**
     * 友達の最近アクセスした時間を現在に設定します。
     *
     * @param id 友達のID
     */
    protected void updateBuddyLastContact(String id) {
        Buddy buddy = getBuddyList().findById(id);
        if (buddy != null) {
            buddy.setLastContactDate(new Date());
        }
    }

    protected void fireOnBuddyLogin(Map<String, Object> params) {
        fireEvent("onBuddyLogin", params);
    }

    protected void fireOnBuddyLogout(Map<String, Object> params) {
        fireEvent("onBuddyLogout", params);
    }

    protected void fireOnBuddyStatusChange(Map<String, Object> params) {
        fireEvent("onBuddyStatusChange", params);
    }

    protected void fireOnChangeStatusMessage(Map<String, Object> params) {
        fireEvent("onChangeStatusMessage", params);
    }

    protected void fireOnChangeStatusMessageFailure(Map<String, Object> params) {
        fireEvent("onChangeStatusMessageFailure", params);
    }

    protected void fireOnDisconnect(Map<String, Object> params) {
        fireEvent("onDisconnect", params);
    }

    protected void fireOnMessageReceived(Map<String, Object> params) {
        fireEvent("onMessageReceived", params);
    }

    protected void fireOnMessageSent(Map<String, Object> params) {
        fireEvent("onMessageSent", params);
    }

    protected void fireOnSendMessageFailure(Map<String, Object> params) {
        fireEvent("onSendMessageFailure", params);
    }

    protected void fireOnLogin(Map<String, Object> params) {
        fireEvent("onLogin", params);
    }

    protected void fireOnLoginFailuer(Map<String, Object> params) {
        fireEvent("onLoginFailure", params);
    }

    protected synchronized void fireEvent(String methodName, Map<String, Object> params) {
        SessionEvent event = new SessionEvent();
        if (params != null) {
            event.putAll(params);
        }
        for (SessionListener listener : this.sessionListenerList) {
            if (listener != null) {
                try {
                    Method m = SessionListener.class.getMethod(methodName, SessionEvent.class);
                    m.invoke(listener, event);
                } catch (SecurityException e) {
                    throw new SystemException(e.getMessage(), e);
                } catch (NoSuchMethodException e) {
                    throw new SystemException(e.getMessage(), e);
                } catch (InvocationTargetException e) {
                    throw new SystemException(e.getMessage(), e);
                } catch (IllegalArgumentException e) {
                    throw new SystemException(e.getMessage(), e);
                } catch (IllegalAccessException e) {
                    throw new SystemException(e.getMessage(), e);
                }
            }
        }
    }

    public synchronized void close() throws IOException {
        isClosed = true;
        try {
            sock.shutdownInput();
        } catch (Exception ignored) {
        }
        try {
            sock.shutdownOutput();
        } catch (Exception ignored) {
        }
        try {
            sock.close();
        } catch (Exception ignored) {
        }
        try {
            this.executorService.shutdown();
        } catch (Exception ignored) {
        }

        this.sock = null;
    }

    public boolean isLogin() {
        // 接続中ならばログイン済
        return (sock != null && sock.isConnected());
    }

    public void sendMessage(final Message message) {

        this.executorService.execute(new Runnable() {

            @Override
            public void run() {
                try {
                    if (!isIpAdderValid()) {
                        Log.v(Constants.LOG_TAG, "network is invalid: logging out..");
                        close();
                        fireOnDisconnect(null);
                        return;
                    }

                    YmsgPacket p = new YmsgPacket();
                    p.setProtocolVersion(YMSG_PROTOCOL_VERSION);
                    p.setVendorId(YMSG_VENDER_ID);
                    p.setService(YMSG_SERVICE_MESSAGE);
                    p.setSessionId(getSessionId());
                    p.addValue("0", message.getSenderId());
                    p.addValue("1", message.getSenderId());
                    p.addValue("5", message.getRecipientId());
                    p.addValue("14", message.getText());
                    sendPacket(p);
                } catch (Exception e) {
                    Map<String, Object> params = new HashMap<String, Object>();
                    params.put("exception", e);
                    fireOnSendMessageFailure(params);
                    return;
                }

                Conversation c = getConversation(message.getSenderId(), message.getRecipientId(), true);
                c.addMessage(message);

                updateBuddyLastContact(message.getRecipientId());

                fireOnMessageSent(null);
            }
        });
    }

    public void changeStatus(final int status, final String statusMessage) {

        setVisibility(status != YMSG_STATUS_OFFLINE);
        this.executorService.execute(new Runnable() {

            @Override
            public void run() {
                try {
                    YmsgPacket p = new YmsgPacket();
                    p.setProtocolVersion(YMSG_PROTOCOL_VERSION);
                    p.setVendorId(YMSG_VENDER_ID);
                    p.setService(YMSG_SERVICE_STATUS_UPDATE);
                    p.setSessionId(getSessionId());
                    if (statusMessage != null && statusMessage.length() > 0) {
                        p.addValue("10", Integer.toString(status));
                    } else {
                        p.addValue("10", Integer.toString(YMSG_STATUS_ONLINE));
                    }
                    p.addValue("47","0");
                    p.addValue("19", statusMessage);
                    p.addValue("97","1");
                    p.addValue("187","0");
                    sendPacket(p);
                } catch (IOException e) {
                    Map<String, Object> params = new HashMap<String, Object>();
                    params.put("exception", e);
                    fireOnChangeStatusMessageFailure(params);
                }

                User user = getUser();
                user.setStatusMessage(statusMessage);
                user.setStatus(status);

                Map<String, Object> params = new HashMap<String, Object>();
                params.put("user", user);
                fireOnChangeStatusMessage(params);
            }
        });
    }

    public void sendPacket(YmsgPacket packet) throws IOException {
        Log.v(Constants.LOG_TAG, "YMSG sending: " + packet);
        OutputStream os = getSock().getOutputStream();
        packet.writeTo(os);
        os.flush();
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
        List<Conversation> convList = getConversationManager().getConversationList();
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
     * 会話リストを取得します。
     *
     * @return 会話リスト
     */
    public List<Conversation> getConversationList() {
        return getConversationManager().getConversationList();
    }

    /**
     * user を取得します。
     *
     * @return user
     */
    public User getUser() {
        return user;
    }

    /**
     * user を設定します。
     *
     * @param user セットする user
     */
    public void setUser(User user) {
        this.user = user;
    }

    /**
     * buddyList を取得します。
     *
     * @return buddyList
     */
    public BuddyList getBuddyList() {
        return buddyList;
    }

    /**
     * buddyList を設定します。
     *
     * @param buddyList セットする buddyList
     */
    public void setBuddyList(BuddyList buddyList) {
        this.buddyList = buddyList;
    }

    /**
     * sessionId を取得します。
     *
     * @return sessionId
     */
    public int getSessionId() {
        return sessionId;
    }

    /**
     * sessionId を設定します。
     *
     * @param sessionId セットする sessionId
     */
    public void setSessionId(int sessionId) {
        this.sessionId = sessionId;
    }

    protected Socket getSock() {
        return this.sock;
    }

    protected void setSock(Socket sock) {
        this.sock = sock;
    }

    public synchronized void addSessionListener(SessionListener listener) {
        if (!this.sessionListenerList.contains(listener)) {
            this.sessionListenerList.add(listener);
        }
    }

    public synchronized boolean removeSessionListener(SessionListener listener) {
        return this.sessionListenerList.remove(listener);
    }

    /**
     * conversationManager を取得します。
     * @return conversationManager
     */
    public ConversationManager getConversationManager() {
        return conversationManager;
    }

    /**
     * conversationManager を設定します。
     * @param conversationManager セットする conversationManager
     */
    public void setConversationManager(ConversationManager conversationManager) {
        this.conversationManager = conversationManager;
    }

    /**
     * ソケットが関連付けられているIPと現在のIPが同じかどうかを判定する。
     * @return IPが同じならtrue
     */
    public boolean isIpAdderValid() throws SocketException {
        Socket sock = getSock();
        if (sock == null) {
            return false;
        }

        InetAddress sockAddr = sock.getLocalAddress();
        if (sockAddr == null) {
            return false;
        }

        for (Enumeration<NetworkInterface> enume = NetworkInterface.getNetworkInterfaces(); enume != null && enume.hasMoreElements(); ) {
            NetworkInterface ni = enume.nextElement();
            for (Enumeration<InetAddress> enumeInet = ni.getInetAddresses(); enumeInet.hasMoreElements(); ) {
                InetAddress ifAddr = enumeInet.nextElement();
                if (ifAddr.equals(sockAddr)) {
                    return true;
                }
            }
        }

        return false;
    }

    protected class SocketHandler implements Runnable {
        /*
         * (non-Javadoc)
         *
         * @see java.lang.Thread#run()
         */
        @Override
        public void run() {
            while (true) {
                synchronized (Session.this) {
                    if (isClosed) {
                        break;
                    }
                }
                try {
                    if (!isIpAdderValid()) {
                        Log.v(Constants.LOG_TAG, "network is invalid: logging out..");
                        close();
                        fireOnDisconnect(null);
                        break;
                    }

                    final YmsgPacket tmpPacket = new YmsgPacket();
                    tmpPacket.load(Session.this.sock.getInputStream());
                    Log.v(Constants.LOG_TAG, "YMSG received: " + tmpPacket);
                    if (tmpPacket.getService() == YMSG_SERVICE_DISCONNECT) {
                        Log.v(Constants.LOG_TAG, "YMSG disconnect request packet received. closing connection: "
                                + tmpPacket);
                        close();
                        fireOnDisconnect(null);
                        break;
                    } else {
                        Session.this.executorService.execute(new Runnable() {
                            /*
                             * (non-Javadoc)
                             *
                             * @see java.lang.Runnable#run()
                             */
                            @Override
                            public void run() {
                                handleYmsgPacket(tmpPacket);
                            }
                        });
                    }
                } catch (Exception e) {
                    if(!isClosed) {
                        // 例外が発生した場合はログオフさせる
                        Log.v(Constants.LOG_TAG, "something wrong whith reading ymsg packet.", e);
                        try { close(); } catch (Exception ignore) {}
                        fireOnDisconnect(null);
                        break;
                    }
                }
            }
        }

    }
}
