package com.chatmessenger.android.ym;

/**
 * @author Teruhiko Kusunoki&lt;<a href="teru.kusu@gmail.com">teru.kusu@gmail.com</a>&gt;
 *
 */
public class SessionAdapter implements SessionListener {

	/* (non-Javadoc)
	 * @see org.terukusu.ahoomsgr.SessionListener#onLogin(org.terukusu.ahoomsgr.SessionEvent)
	 */
	@Override
	public void onLogin(SessionEvent event) {
	}

	/* (non-Javadoc)
	 * @see org.terukusu.ahoomsgr.SessionListener#onLoginFailure(org.terukusu.ahoomsgr.SessionEvent)
	 */
	@Override
	public void onLoginFailure(SessionEvent event) {
	}

	/* (non-Javadoc)
	 * @see org.terukusu.ahoomsgr.SessionListener#onLogout(org.terukusu.ahoomsgr.SessionEvent)
	 */
	@Override
	public void onLogout(SessionEvent event) {
	}

	/* (non-Javadoc)
	 * @see org.terukusu.ahoomsgr.SessionListener#onBuddyLogin(org.terukusu.ahoomsgr.SessionEvent)
	 */
	@Override
	public void onBuddyLogin(SessionEvent event) {
	}

	/* (non-Javadoc)
	 * @see org.terukusu.ahoomsgr.SessionListener#onBodyyLogout(org.terukusu.ahoomsgr.SessionEvent)
	 */
	@Override
	public void onBuddyLogout(SessionEvent event) {
	}

	/* (non-Javadoc)
     * @see org.terukusu.ahoomsgr.SessionListener#onBuddyStatusChange(org.terukusu.ahoomsgr.SessionEvent)
     */
    @Override
    public void onBuddyStatusChange(SessionEvent event) {
    }

    /* (non-Javadoc)
     * @see org.terukusu.ahoomsgr.SessionListener#onChangeStatusMessage(org.terukusu.ahoomsgr.SessionEvent)
     */
    @Override
    public void onChangeStatusMessage(SessionEvent event) {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see org.terukusu.ahoomsgr.SessionListener#onChangeStatusMessageFailure(org.terukusu.ahoomsgr.SessionEvent)
     */
    @Override
    public void onChangeStatusMessageFailure(SessionEvent event) {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see org.terukusu.ahoomsgr.SessionListener#onDisconnect(org.terukusu.ahoomsgr.SessionEvent)
     */
    @Override
    public void onDisconnect(SessionEvent event) {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
	 * @see org.terukusu.ahoomsgr.SessionListener#onSendMessageFailure(org.terukusu.ahoomsgr.SessionEvent)
	 */
	@Override
	public void onSendMessageFailure(SessionEvent event) {
	}

	/* (non-Javadoc)
	 * @see org.terukusu.ahoomsgr.SessionListener#onMessageReceived(org.terukusu.ahoomsgr.SessionEvent)
	 */
	@Override
	public void onMessageReceived(SessionEvent event) {
	}

    /* (non-Javadoc)
     * @see org.terukusu.ahoomsgr.SessionListener#onMessageSent(org.terukusu.ahoomsgr.SessionEvent)
     */
    @Override
    public void onMessageSent(SessionEvent event) {
    }
}
