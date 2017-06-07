package com.sh.campaign.runtime;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.reactivestreams.Processor;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

import com.sh.campaign.data.WorkflowItem;
import com.sh.campaign.runtime.kafka.KConsumer;
import com.sh.campaign.runtime.kafka.KafkaProducerService;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Cancellable;
import io.reactivex.functions.Consumer;
import io.reactivex.processors.FlowableProcessor;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;

// It's an fully-purposed activity processor
// It's an instance of a service which can "scale up" by spinning more
// types of processor:
// 0 input + 1 external stream -> func() + state -> Flux() (eg. segmentation)
// 1 input -> func() success -> Flux() output (email, splitter, crmactivity)
// 1 input + 1 external stream -> func() + state + time -> Flux including branch (trigger, scheduler)
// all output will will feed all following activities via topic, 
//   and activities will only process the one with corresponding branch

public class ActivityProcessor {
	
	private String name;
	
	public static KafkaProducerService producer = new KafkaProducerService();
	
	KConsumer kconsumer;
	
	private Log log = LogFactory.getLog(ActivityProcessor.class);
	
	// constructor inject all the configuration (boot-strapped campaign definition) that tells
	// 1. where to pull data (WorkflowItem) from (topic)
	// 2. what kind of processing need to happen (inline or call out)
	// 3. after the processing, where to send to (next topic(s)?)
	public ActivityProcessor(WorkflowActivity activity) {
		//bootstrapped from activityName, load definition
		this.name = activity.ActivityId;
		String parentId = activity.ParentActivityId;
		log.info(" ActivityProcessor " + activity.ActivityId + " parent: " + activity.ParentActivityId);

		// listen on parent's 
		
		// send to own namespace
		
		if (parentId != null) {
			//TODO: we need to inject activity's behavior into the consumer
			
			KConsumer c = new KConsumer(parentId, this.name, this.name, ActivityProcessor.producer, (s) -> {
				log.info(this.name + " received " + s);
				});			
			
			new Thread(c).start();
		}

		/*
		// the thing is we don't know which is the next one
		ActivityProcessor nextProcessor = null; //new ActivityProcessor("logger");
		
		// TODO: for different activity different behaviors
		// eg. process, and finally it will need to publish again to next one
		subject.subscribe(new Consumer<ActivityItem>() {

				@Override
				public void accept(ActivityItem item) throws Exception {
					System.out.println("Activity " + activity.ActivityId + " Got " + item.toString());
					//blabla
					System.out.println(item);
					if (nextProcessor != null) {
						System.out.println("Feeding to next");
						// or maybe there are many "next", so it's actually wrong
						nextProcessor.FeedManually(item);
					} else {
						System.out.println("Done");
					}
				}
			}, Throwable::printStackTrace);
			*/
		// 
	}
	
	public void FeedManually(ActivityItem item) throws InterruptedException, ExecutionException {
		System.out.println("Feeding " + this.name + " with " + item);
		producer.Send(this.name, item.toString());
	}
	
}
