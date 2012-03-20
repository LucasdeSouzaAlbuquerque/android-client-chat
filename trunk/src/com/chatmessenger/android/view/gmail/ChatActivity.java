package com.chatmessenger.android.view.gmail;

import java.util.List;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManager;
import org.jivesoftware.smack.ChatManagerListener;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;

import com.chatmessenger.android.R;
import com.chatmessenger.android.gmail.ConnectionHelper;
import com.chatmessenger.android.gmail.Contact;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class ChatActivity extends Activity {

	protected Contact user;
	private Chat chat;
	TextView chatMessages;

	public Contact getUser() {
		return user;
	}

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.chat_gmail);
		chatMessages = (TextView) findViewById(R.id.chatMessages);
		if (getIntent().getExtras() != null) {
			String usrEmail = getIntent().getExtras().getString("user");
			List<Contact> list = ConnectionHelper.getContactList();
			for (Contact c : list) {
				if (c.getRosterEntry().getUser().equals(usrEmail)) {
					this.user = c;
					break;
				}
			}
			// Toast.makeText(ChatActivity.this,usrEmail,
			// Toast.LENGTH_SHORT).show();
		}
		RosterEntry re = user.getRosterEntry();
		ChatManager chatManager = ConnectionHelper.getConnection()
				.getChatManager();
		chat = chatManager.createChat(re.getUser(), new MessageListener() {

			@Override
			public void processMessage(Chat arg0, Message arg1) {
				//Toast.makeText(ChatActivity.this,arg1.getBody(),Toast.LENGTH_SHORT).show();
				new GetMessage().execute(arg1.getBody() + "\n");
			}
		});

		Button btnSend = (Button) findViewById(R.id.btnSend);
		btnSend.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				TextView txtMsg = (TextView) findViewById(R.id.txfMessage);
				String msg = txtMsg.getText().toString();
				try {
					chat.sendMessage(msg);
					chatMessages.append(msg+"\n");
					txtMsg.setText("");
					txtMsg.clearFocus();
				} catch (XMPPException e) {
					e.printStackTrace();
				}
			}
		});

	}
	
	class GetMessage extends AsyncTask<String, String, String> {

		@Override
		protected String doInBackground(String... messages) {
			String result = "";
			for(String msg : messages)
				result  = result + msg + "\n";
			return result;
		}
		
		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			chatMessages.append(result);
		}
		
	}
}
