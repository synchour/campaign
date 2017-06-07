package com.sh.campaign.runtime;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.sh.campaign.runtime.kafka.KafkaProducerService;

@Component
public class WorkflowRuntime {
	
	@Autowired
	private ApplicationContext context;
	
	@Autowired
	private CampaignDefinitionLoader loader;
	
	@Autowired
	KafkaProducerService producer;
	
	public ActivityProcessor first = null;
	
	public WorkflowRuntime() {
		System.out.println("WorkflowRuntime ctr");
	}
	
	private Map<String, ActivityProcessor> activityProcessorInstance = new HashMap<String, ActivityProcessor>();
	
	public void InitializeWorkflow(String workflowName) throws Exception {
		System.out.println("WorkflowRuntime - InitializeWorkflow :" + workflowName );
		// Load workflow definition, bootstrap, and spin a couple of services
		
		// This is the step of constructing the runtime, monitor processor performance
		// and potentially scale processor
		for (WorkflowActivity activity : loader.loadActivityOrdered()) {
			
			// bean name, bean activity
			ActivityProcessor ap = new ActivityProcessor(activity);
			activityProcessorInstance.put(activity.ActivityId, ap);

			if (activity.ParentActivityId != null) {
				ActivityProcessor parent = activityProcessorInstance.get(activity.ParentActivityId);
				if (parent == null) {
					throw new Exception("Parent cannot be found for " + activity.ActivityId);
				}
			}
			
			if (first == null) {
				first = ap;
			}
			
		}
		
	}

	private boolean init = false;
	
	public synchronized void init() throws Exception {
		if (init) {
			return;
		}
		init = true;
		InitializeWorkflow("sampleWorkflow");
	}
	
}
