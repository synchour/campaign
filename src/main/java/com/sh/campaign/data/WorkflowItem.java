package com.sh.campaign.data;

import java.util.List;

// Workitem that flow through the system, appending history
public class WorkflowItem {
	private String contactId;
	
	private List<WorkflowActivityProcessActivity> history;
}
