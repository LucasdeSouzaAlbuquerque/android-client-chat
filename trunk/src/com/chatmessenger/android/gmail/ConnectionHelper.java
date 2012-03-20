package com.chatmessenger.android.gmail;

import java.util.ArrayList;
import java.util.Collection;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Presence;

import com.chatmessenger.android.R;

import android.content.res.Resources;

public class ConnectionHelper {

	static XMPPConnection conn = new XMPPConnection(new ConnectionConfiguration(
			"talk.google.com", 5222, "google.com"));
	
	static Roster roster;
	
	public static void connect() throws XMPPException {
		conn.connect();
	}
	
	public static XMPPConnection getConnection() {
		return conn;
	}
	
	public static void login(String user, String pass) throws XMPPException {
		conn.login(user, pass);
		roster = conn.getRoster();
	}
	
	public static Roster getRoster() {
		return roster;
	}
	
	public static ArrayList<Contact> getContactList(Resources res) {
		ArrayList<Contact> contactList = new ArrayList<Contact>();
		Collection<RosterEntry> rosterCollection = roster.getEntries();
		Presence presence;
		for (RosterEntry re : rosterCollection) {
			presence = roster.getPresence(re.getUser());
			if (presence.isAvailable()) {
				Contact c = new Contact();
				if(re.getName() == null || re.getName().trim().equals(""))
					c.setName(re.getUser());
				else
					c.setName(re.getName());
				c.setStatus(presence.getStatus());
				c.setRosterEntry(re);
				if (presence.getMode() == null)
					c.setImage(res.getDrawable(R.drawable.available));
				else if (presence.getMode().equals(Presence.Mode.chat))
					c.setImage(res.getDrawable(R.drawable.available));
				else if (presence.getMode().equals(Presence.Mode.away)
						|| presence.getMode().equals(Presence.Mode.xa))
					c.setImage(res.getDrawable(R.drawable.idle));
				else if (presence.getMode().equals(Presence.Mode.dnd))
					c.setImage(res.getDrawable(R.drawable.busy));
				else
					c.setImage(res.getDrawable(R.drawable.available));
				contactList.add(c);
			}
		}
		return contactList;
	}
	
	public static ArrayList<Contact> getContactList() {
		ArrayList<Contact> contactList = new ArrayList<Contact>();
		Collection<RosterEntry> rosterCollection = roster.getEntries();
		Presence presence;
		for (RosterEntry re : rosterCollection) {
			presence = roster.getPresence(re.getUser());
			if (presence.isAvailable()) {
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
		return contactList;
	}
	
}
