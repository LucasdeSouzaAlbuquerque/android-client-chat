package com.chatmessenger.android.view.ym;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.chatmessenger.android.R;
import com.chatmessenger.android.ym.Buddy;
import com.chatmessenger.android.ym.Constants;
import com.chatmessenger.android.ym.Conversation;
import com.chatmessenger.android.ym.Message;
import com.chatmessenger.android.ym.User;


import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

/**
 * @author Teruhiko Kusunoki&lt;<a
 *         href="teru.kusu@gmail.com">teru.kusu@gmail.com</a>&gt;
 *
 */
public class MessageListActivity extends Activity implements Constants {
    private YmsgService ymsgService;
    private MessageListAdapter messageListAdapter;
    private final Handler handler = new Handler();
    private final List<Message> msgListData = new ArrayList<Message>();
    private String recipientId;

    private final BroadcastReceiver ymsgServiceReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.v(LOG_TAG, "onRecieve() called with action: " + intent.getAction());
            String action = intent.getAction();
            if (ACTION_MESSAGE_SENT.equals(action)) {
                updateList();
            } else if (ACTION_BUDDY_STATUS_CHANGE.equals(action)) {
                updateList();
            } else if (ACTION_MESSAGE_RECEIVED.equals(action)) {
                Message m = (Message) intent.getSerializableExtra("message");
                if (m.getSenderId().equals(getRecipientId())) {
                    // 現在の会話相手
                    updateList();
                } else {
                    // Tostでお知らせ
                    String str = getText(R.string.msg_list_newmessage).toString();
                    Toast.makeText(MessageListActivity.this, m.getSenderId() + str + m.getText(), Toast.LENGTH_LONG).show();
                }
            } else if (ACTION_DISCONNECT.equals(action)) {
                finish();
            }
        }
    };

    private final ServiceConnection ymsgServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {
            setYmsgService(null);
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            YmsgService ymsgService = ((YmsgService.LocalBinder)service).getService();
            setYmsgService(ymsgService);
            ymsgService.setCurrentRecipientId(getRecipientId());
            updateList();
        }
    };

    /**
	 * 新しいオブジェクトを生成します。
	 */
	public MessageListActivity() {
		super();
	}

	protected void onResume() {
        super.onResume();
        Log.v(LOG_TAG, MessageListActivity.class.getName() + ": onResume() called");
        YmsgService ymsgService = getYmsgService();
        if (ymsgService != null) {
            Log.v(LOG_TAG, "setting current recipientId to ymsgService: recipientId = " + getRecipientId());
            ymsgService.setCurrentRecipientId(getRecipientId());
        }

        setTitle(createWindowTitle());
    }

	protected String createWindowTitle() {
        // タイトル設定
        StringBuilder sb = new StringBuilder();
        sb.append(getRecipientId());
        if (ymsgService != null) {
            Buddy buddy = ymsgService.getBuddyList().findById(getRecipientId());
            if (buddy != null) {
                String statusMessage = buddy.getStatusMessage();
                if (statusMessage != null && statusMessage.length() > 0) {
                    sb.append(" - ");
                    sb.append(statusMessage);
                }
            }
        }

        return sb.toString();
	}

    protected void onPause() {
        super.onPause();
        Log.v(LOG_TAG, MessageListActivity.class.getName() + ": onPause() called");
        YmsgService ymsgService = getYmsgService();
        if (ymsgService != null) {
            Log.v(LOG_TAG, "setting current recipientId on ymsgService to null");
            ymsgService.setCurrentRecipientId(null);
        }
    }




    /*
	 * (non-Javadoc)
	 *
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.msg_list);

		String recipientId = getIntent().getStringExtra(EXTRA_RECIPIENT_ID);
		setRecipientId(recipientId);

		//  サービス設定
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_MESSAGE_SENT);
        intentFilter.addAction(ACTION_MESSAGE_RECEIVED);
        intentFilter.addAction(ACTION_DISCONNECT);
        intentFilter.addAction(ACTION_BUDDY_STATUS_CHANGE);
        registerReceiver(getYmsgServiceReceiver(), intentFilter);

        Intent serviceIntent = new Intent(this, YmsgService.class);
        bindService(serviceIntent, getYmsgServiceConnection(), BIND_AUTO_CREATE);


		MessageListAdapter adapter = new MessageListAdapter(
				this, R.layout.msg_list, this.msgListData);
		setMessageListAdapter(adapter);

		ListView msgList = (ListView) findViewById(R.id.msgList);
		msgList.setAdapter(adapter);

		Button ok = (Button) findViewById(R.id.sendButton);

		ok.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
			    sendMessage();
			}
		});

        EditText msgEdit = (EditText) findViewById(R.id.msgEdit);
        msgEdit.setOnEditorActionListener(new OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                switch (actionId) {
                case EditorInfo.IME_NULL:
                case EditorInfo.IME_ACTION_DONE:
                    sendMessage();
                    return true;
                }
                return false;
            }
        });
	}

	public void sendMessage() {
        EditText msgEdit = (EditText) findViewById(R.id.msgEdit);
        final String msg = msgEdit.getText().toString();
        Log.v(Constants.LOG_TAG, "input text: " + msg);
        msgEdit.setText(null);

        if (msg == null || "".equals(msg)) {
            return;
        }

        YmsgService y = getYmsgService();
        if (y == null) {
            return;
        }

        User u = y.getUser();
        Message message = new Message(u.getCurrentId(), getRecipientId(), new Date(), msg);
        y.sendMessage(message);
	}

	public void updateList() {
	    getHandler().post(new Runnable() {
            @Override
            public void run() {
                YmsgService ymsgService = getYmsgService();
                if (ymsgService == null) {
                    return;
                }

                User user = ymsgService.getUser();
                Conversation c  =ymsgService.getConversation(user.getCurrentId(), getRecipientId(), true);
                List<Message> tmpMessageList = c.getMessageList();
                getMsgListData().clear();
                getMsgListData().addAll(tmpMessageList);

                getMessageListAdapter().notifyDataSetChanged();

                setTitle(createWindowTitle());
            }
        });
	}

    /* (non-Javadoc)
     * @see android.app.Activity#onDestroy()
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(getYmsgServiceReceiver());
        unbindService(getYmsgServiceConnection());
        setYmsgService(null);
    }

    /**
     * recipientId を取得します。
     * @return recipientId
     */
    protected String getRecipientId() {
        return recipientId;
    }

    /**
     * recipientId を設定します。
     * @param recipientId セットする recipientId
     */
    protected void setRecipientId(String recipientId) {
        this.recipientId = recipientId;
    }

    /**
     * msgListData を取得します。
     * @return msgListData
     */
    protected List<Message> getMsgListData() {
        return msgListData;
    }

    /**
     * ymsgService を取得します。
     * @return ymsgService
     */
    protected YmsgService getYmsgService() {
        return ymsgService;
    }

    /**
     * ymsgService を設定します。
     * @param ymsgService セットする ymsgService
     */
    protected void setYmsgService(YmsgService ymsgService) {
        this.ymsgService = ymsgService;
    }

    /**
     * ymsgServiceReceiver を取得します。
     * @return ymsgServiceReceiver
     */
    protected BroadcastReceiver getYmsgServiceReceiver() {
        return ymsgServiceReceiver;
    }

    /**
     * ymsgServiceConnection を取得します。
     * @return ymsgServiceConnection
     */
    protected ServiceConnection getYmsgServiceConnection() {
        return ymsgServiceConnection;
    }

    /**
     * handler を取得します。
     * @return handler
     */
    protected Handler getHandler() {
        return handler;
    }

    /**
     * messageListAdapter を取得します。
     * @return messageListAdapter
     */
    protected MessageListAdapter getMessageListAdapter() {
        return messageListAdapter;
    }

    /**
     * messageListAdapter を設定します。
     * @param messageListAdapter セットする messageListAdapter
     */
    protected void setMessageListAdapter(MessageListAdapter messageListAdapter) {
        this.messageListAdapter = messageListAdapter;
    }

    private class MessageListAdapter extends ArrayAdapter<Message> {

        private final SimpleDateFormat sdf = new SimpleDateFormat("h:mm a");
        private final LayoutInflater layoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);

        /**
         * 新しいオブジェクトを生成します。
         *
         * @param context
         * @param textViewResourceId
         * @param objects
         */
        public MessageListAdapter(Context context, int textViewResourceId, List<Message> objects) {
            super(context, textViewResourceId, objects);
        }

        /* (non-Javadoc)
         * @see android.widget.ArrayAdapter#getView(int, android.view.View, android.view.ViewGroup)
         */
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = convertView;
            if (view == null) {
                view = layoutInflater.inflate(R.layout.msg_list_item, null);
            }

            Message m = getItem(position);

            TextView messageFromText = (TextView) view.findViewById(R.id.messageFromText);
            messageFromText.setText(m.getSenderId() + " (" + sdf.format(m.getDate()) + ")");

            TextView messageText = (TextView) view.findViewById(R.id.messageText);
            messageText.setText(m.getText());

            return view;
        }


    }
}
