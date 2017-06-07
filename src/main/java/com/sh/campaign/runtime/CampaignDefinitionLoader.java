package com.sh.campaign.runtime;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

@Component
public class CampaignDefinitionLoader {

	private Map<String, WorkflowActivity> allActivity = new HashMap<String, WorkflowActivity>();
	
	private List<WorkflowActivity> orderedActivity = new LinkedList<WorkflowActivity>();
	
	// read boot-strapped activity
	WorkflowActivity loadActivity(String activityName) {
		return allActivity.get(activityName);
	}
	
	public Collection<WorkflowActivity> loadActivityOrdered() {
		return orderedActivity;
	}
	
	// I am hacking it so I am composing the boot-strapped in memory 
	public CampaignDefinitionLoader() {
		WorkflowActivity segment = new WorkflowActivity();
		segment.ActivityTypeId = "segment";
		segment.ParentActivityId = null;
		segment.ActivityId = "segment-1";
		AddActivity(segment);
		
		WorkflowActivity email = new WorkflowActivity();
		email.ActivityTypeId = "email";
		email.ParentActivityId = "segment-1";
		email.ActivityId = "april-fool-promotion-email";
		AddActivity(email);

		WorkflowActivity scheduler = new WorkflowActivity();
		scheduler.ActivityTypeId = "scheduler";
		scheduler.ParentActivityId = "april-fool-promotion-email";
		scheduler.ActivityId = "scheduler-delay-10s";
		AddActivity(scheduler);
		

		WorkflowActivity task = new WorkflowActivity();
		task.ActivityTypeId = "task";
		task.ParentActivityId = "april-fool-promotion-email";
		task.ActivityId = "create-task";
		AddActivity(task);
		
		WorkflowActivity trigger = new WorkflowActivity();
		trigger.ActivityTypeId = "trigger";
		trigger.ParentActivityId = "scheduler-delay-10s";
		trigger.ActivityId = "email-opened-within-100s";
		AddActivity(trigger);
		
		WorkflowActivity triggerYesEmail = new WorkflowActivity();
		triggerYesEmail.ActivityTypeId = "email";
		triggerYesEmail.ParentActivityId = "email-opened-within-100s";
		triggerYesEmail.ActivityId = "yes-email";
		triggerYesEmail.ParentBranchId = 0;
		//AddActivity(triggerYesEmail);
		
		WorkflowActivity triggerNoEmail = new WorkflowActivity();
		triggerNoEmail.ActivityTypeId = "email";
		triggerNoEmail.ParentActivityId = "email-opened-within-100s";
		triggerNoEmail.ActivityId = "no-email";
		triggerNoEmail.ParentBranchId = 1;
		//AddActivity(triggerNoEmail);
	}
	
	private void AddActivity(WorkflowActivity activity) {
		if (allActivity.containsKey(activity.ActivityId)) {
			System.err.println("Activity exist: " + activity.ActivityId);
		}
		
		if (!allActivity.containsKey(activity.ParentActivityId)) {
			System.err.println("Activity parent does not exist for: " + activity.ActivityId);
		}
		
		allActivity.put(activity.ActivityId, activity);
		orderedActivity.add(activity);
	}
}
