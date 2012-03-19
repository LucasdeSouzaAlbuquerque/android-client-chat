package com.chatmessenger.android.ym;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * 友達のグループ。
 * このクラスはスレッドセーフではありません。
 *
 * @author Teruhiko Kusunoki&lt;<a href="teru.kusu@gmail.com">teru.kusu@gmail.com</a>&gt;
 *
 */
public class BuddyGroup {
	private String name;
	private final List<Buddy> buddyList = new ArrayList<Buddy>();

	/**
	 *
	 */
	public BuddyGroup() {
		super();
	}

	/**
	 * @param name
	 */
	public BuddyGroup(String name) {
		super();
		this.name = name;
	}

	public void addBuddy(Buddy buddy) {
		getBuddyList().add(buddy);
		Collections.sort(getBuddyList(), new Comparator<Buddy>() {
			/* (non-Javadoc)
			 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
			 */
			@Override
			public int compare(Buddy object1, Buddy object2) {
				return object1.getId().compareTo(object2.getId());
			}
		});
	}

	/**
	 * name を取得します。
	 * @return name
	 */
	public String getName() {
		return name;
	}

	/**
	 * name を設定します。
	 * @param name セットする name
	 */
	public void setName(String name) {
		this.name = name;
	}

	public List<Buddy> getBuddyList() {
		return buddyList;
	}
}
