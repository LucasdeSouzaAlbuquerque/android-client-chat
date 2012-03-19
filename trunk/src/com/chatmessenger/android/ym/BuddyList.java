package com.chatmessenger.android.ym;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * 友達リスト。
 * このクラスはスレッドセーフではありません。
 * 
 * @author Teruhiko Kusunoki&lt;<a href="teru.kusu@gmail.com">teru.kusu@gmail.com</a>&gt;
 *
 */
public class BuddyList {
	private final ArrayList<BuddyGroup> buddyGroupList = new ArrayList<BuddyGroup>();
	
	/**
	 * 
	 */
	public BuddyList() {
		super();
	}

	/**
	 * グループを作成します。既に同名のグループが存在する場合は作成されません。
	 * 
	 * @param name グループ名
	 * @return 作成されたグループ。同名が存在した場合はそのグループ
	 */
	public BuddyGroup createBuddyGroup(String name) {
		for (BuddyGroup buddyGroup : getBuddyGroupList()) {
			if (buddyGroup.getName().equals(name)) {
				return  buddyGroup;
			}
		}

		BuddyGroup buddyGroup = new BuddyGroup(name);
		getBuddyGroupList().add(buddyGroup);
		Collections.sort(getBuddyGroupList(), new Comparator<BuddyGroup>() {
			/* (non-Javadoc)
			 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
			 */
			@Override
			public int compare(BuddyGroup object1, BuddyGroup object2) {
				return object1.getName().compareTo(object2.getName());
			}
		});
		
		return buddyGroup;
	}
	
	public void addBuddy(String groupName, Buddy buddy) {
		 createBuddyGroup(groupName).addBuddy(buddy);
	}
	
	/**
	 * IDから友達を検索します。
	 * 
	 * @param id ID
	 * @return 友達
	 */
	public Buddy findById(String id) {
		for (BuddyGroup buddyGroup : getBuddyGroupList()) {
			for (Buddy buddy : buddyGroup.getBuddyList()) {
				if (buddy.getId().equals(id)) {
					return buddy;
				}
			}
		}
		
		return null;
	}
	
	/**
	 * グループを指定してい友達のリストを取得します。
	 * 
	 * @param groupName グループ名
	 * @return 友達のリスト。該当グループが存在しなければnull。存在するが友達が居ない場合は空リスト。
	 */
	public List<Buddy> findByGroup(String groupName) {
		for (BuddyGroup buddyGroup : getBuddyGroupList()) {
			if (buddyGroup.getName().equals(groupName)) {
				return buddyGroup.getBuddyList();
			}
		}

		return null;
	}

	/**
	 * 全ての友達を取得します。
	 * @return 全ての友達
	 */
	public List<Buddy> getAll() {
		List<Buddy> result = new ArrayList<Buddy>();
		for (BuddyGroup buddyGroup : getBuddyGroupList()) {
			result.addAll(buddyGroup.getBuddyList());
		}
		return result;
	}

	/**
	 * 全てのオンラインの友達を取得します。
	 * 
	 * @return すべてのオンラインの友達
	 */
	public List<Buddy> getAllOnline() {
		List<Buddy> result = new ArrayList<Buddy>();
		for (BuddyGroup buddyGroup : getBuddyGroupList()) {
			for (Buddy buddy : buddyGroup.getBuddyList()) {
				if (buddy.getStatus() != Constants.YMSG_STATUS_OFFLINE) {
					result.add(buddy);
				}
			}
		}
		return result;
	}

	/**
	 * buddyGroupList を取得します。
	 * @return buddyGroupList
	 */
	public ArrayList<BuddyGroup> getBuddyGroupList() {
		return buddyGroupList;
	}
}
