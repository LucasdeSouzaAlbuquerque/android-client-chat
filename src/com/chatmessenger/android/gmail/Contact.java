package com.chatmessenger.android.gmail;

import org.jivesoftware.smack.RosterEntry;

import android.graphics.drawable.Drawable;

public class Contact {

	private String name;
	private String status;
	private Drawable image;
	private RosterEntry rosterEntry;
	
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public Drawable getImage() {
		return image;
	}
	public void setImage(Drawable image) {
		this.image = image;
	}
	public RosterEntry getRosterEntry() {
		return rosterEntry;
	}
	public void setRosterEntry(RosterEntry rosterEntry) {
		this.rosterEntry = rosterEntry;
	}
	
	
}
