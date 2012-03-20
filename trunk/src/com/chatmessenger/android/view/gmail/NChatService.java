package com.chatmessenger.android.view.gmail;

import java.util.Collection;
import java.util.List;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.RosterListener;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.util.StringUtils;

import com.chatmessenger.android.gmail.ConnectionHelper;
import com.chatmessenger.android.gmail.Contact;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class NChatService extends Service implements MessageListener {
	
	static Roster roster;
	static List<Contact> contactList;
	
	
	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		roster = ConnectionHelper.getRoster();
		contactList = ConnectionHelper.getContactList();
		roster.addRosterListener(new RosterListener() {
			
			@Override
			public void presenceChanged(Presence arg0) {
				updateUser(arg0);			
			}
			
			@Override
			public void entriesUpdated(Collection<String> arg0) {
				// TODO Auto-generated method stub				
			}
			
			@Override
			public void entriesDeleted(Collection<String> arg0) {
				// TODO Auto-generated method stub				
			}
			
			@Override
			public void entriesAdded(Collection<String> arg0) {
				// TODO Auto-generated method stub				
			}
		});
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		
	}

	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
		
	}
	
	public void updateUser(Presence presence) {
		boolean newUser = true;
		Contact removeC = null;
		for(Contact c : contactList) {
			if(c.getRosterEntry().getUser().equals(StringUtils.parseBareAddress(presence.getFrom()))) {
				newUser = false;
				if (!(presence.isAvailable() || presence.isAway())) {
					removeC = c;
				}
				break;
			}
		}
		if(removeC != null) {
			contactList.remove(removeC);
		}
		if(newUser) {
			RosterEntry re = roster.getEntry(StringUtils.parseBareAddress(presence.getFrom()));
			Contact c = new Contact();
			if(re.getName() == null || re.getName().trim().equals(""))
				c.setName(re.getUser());
			else
				c.setName(re.getName());
			c.setStatus(presence.getStatus());
			c.setRosterEntry(re);
			contactList.add(c);
		}
	}

	@Override
	public void processMessage(Chat arg0, Message arg1) {
		
	}

}
