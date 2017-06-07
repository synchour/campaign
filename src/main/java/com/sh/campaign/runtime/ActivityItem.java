package com.sh.campaign.runtime;

public class ActivityItem {
	
	public ActivityItem(String itemId) {
		this.ItemId = itemId;
	}
	
	private String ItemId;
	private String ActivityTypeId;
	private String Title;
	private String ActivityItemId;
	private String Name;
	
	@Override
	public String toString() {
		return "Contact " + ItemId;
	}
}
