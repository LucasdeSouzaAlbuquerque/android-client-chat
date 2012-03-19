package com.chatmessenger.android.ym;

/**
 * @author Teruhiko Kusunoki&lt;<a href="teru.kusu@gmail.com">teru.kusu@gmail.com</a>&gt;
 *
 */
public interface Constants {
	public static final String LOG_TAG = "AHOO";
	public static final String CHARSET_ASCII = "8859_1";
	public static final String CHARSET_UTF8 = "UTF-8";
	public static final String DEFAULT_CHARSET = CHARSET_UTF8;
	public static final int MAX_MESSAGE_COUNT = 200;
	public static final String EXTRA_RECIPIENT_ID = "EXTRA_RECIPIENT_ID";

	public static final String PREF_LOGIN_REMEMBER_ID = "PREF_REMEMBER_ID";
	public static final String PREF_LOGIN_AUTO = "PREF_LOGIN_AUTO";
	public static final String PREF_LOGIN_HIDDEN = "PREF_LOGIN_HIDDEN";
	public static final String PREF_LOGIN_ID = "PREF_LOGIN_ID";
	public static final String PREF_LOGIN_PASSWORD = "PREF_LOGIN_PASSWORD";
	public static final String PREF_BUDDYLIST_SHOW_OFFLINE = "PREF_BUDDYLIST_SHOW_OFFLINE";

	public static final int YMSG_PROTOCOL_VERSION = 0x10;
	public static final int YMSG_VENDER_ID = 0x64;
	public static final String YMSG_SIGNATURE = "YMSG";
	public static final byte[] YMSG_DELIM = {(byte) 0xC0, (byte) 0x80};
	public static final String YMSG_AUTH_PRE_URL = "http://vcs1.msg.yahoo.com/capacity";
	public static final String YMSG_AUTH_TOKEN_URL = "https://login.yahoo.com/config/pwtoken_get";
	public static final String YMSG_AUTH_LOGIN_URL = "https://login.yahoo.com/config/pwtoken_login";

	public static final int YMSG_STATUS_OFFLINE = 0x5a55aa56;
    public static final int YMSG_STATUS_ONLINE = 0x0;
    public static final int YMSG_STATUS_INVISIBLE = 0x0C; //12
    public static final int YMSG_STATUS_IDLE = 0x0;
    public static final int YMSG_STATUS_SOON_BACK = 0x1;
    public static final int YMSG_STATUS_BUSY = 0x2;
    public static final int YMSG_STATUS_AWAY = 0x4;
    public static final int YMSG_STATUS_CUSTOM = 0x63; //99

    public static final int YMSG_SERVICE_LIST = 0x55;
	public static final int YMSG_SERVICE_LIST_V15 = 0xF1;
	public static final int YMSG_SERVICE_STATUS_V15 = 0xF0;
	public static final int YMSG_SERVICE_MESSAGE = 0x06;
    public static final int YMSG_SERVICE_STATUS_UPDATE = 0xC6;
    public static final int YMSG_SERVICE_LOGOFF = 0x02;
    public static final int YMSG_SERVICE_DISCONNECT = 0x7d1;
    public static final int YMSG_SERVICE_VISIBILITY_TOGGLE = 0xC5; //197

	public static final String ACTION_LOGIN = "ACTION_LOGIN";
    public static final String ACTION_LOGIN_FAILED = "ACTION_LOGIN_FAILED";
    public static final String ACTION_MESSAGE_SENT = "ACTION_MESSAGE_SENT";
    public static final String ACTION_MESSAGE_RECEIVED = "ACTION_MESSAGE_RECEIVED";
    public static final String ACTION_BUDDY_ONLINE = "ACTION_BUDDY_ONLINE";
    public static final String ACTION_BUDDY_OFFLINE = "ACTION_BUDDY_OFFLINE";
    public static final String ACTION_BUDDY_STATUS_CHANGE = "ACTION_BUDDY_STATUS_CHANGE";
    public static final String ACTION_DISCONNECT = "ACTION_DISCONNECT";
    public static final String ACTION_MY_STATUS_CHANGE = "ACTION_MY_STATUS_CHANGE";

	public static final String ERROR_AUTH_CAUSE="CAUSE";
	public static final int ERROR_AUTH_INVALID_ACCOUNT=1;
	public static final int ERROR_AUTH_INVALID_TOKEN=2;
}
