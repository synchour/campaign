package com.sh.campaign.runtime;

import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.apache.commons.lang.StringUtils;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.sh.campaign.data.documentdb.DocumentDBService;
import com.sh.campaign.data.sql.KafkaOffsetRepository;
import com.sh.campaign.data.sql.WriteToSqlCommand;
import com.sh.campaign.runtime.kafka.Config;
import com.sh.campaign.runtime.kafka.KConsumer;
import com.sh.campaign.runtime.kafka.KafkaProducerService;

@RestController
public class WorkflowController {
	
	@Autowired
	WorkflowRuntime runtime;
	
	//@Autowired
	//KafkaOffsetRepository repo;
	
	//@Autowired
	//DocumentDBService docservice;
	
	@GetMapping("/init")
	@HystrixCommand
	public void init() throws Exception {
		runtime.init();
	}

	@GetMapping("/feed")
	@HystrixCommand
	public void Feed(String input) throws Exception {
		runtime.init();
		runtime.first.FeedManually(new ActivityItem(input));
	}
	
	// time curl http://localhost:8080/testsize?size=100&count=10000
	// check http://localhost:8080/hystrix/monitor?stream=http%3A%2F%2Flocalhost%3A8080%2Fhystrix.stream
	// http://localhost:8080/testsize?size=100000&count=100 -> The server disconnected before a response was received.
	// http://localhost:8080/testsize?size=1000000&count=1 -> The request included a message larger than the max message size the server will accept.
	// local build -> works
	// http://localhost:8080/testsize?size=10000000&count=10 -> 
	@GetMapping("/testsize")
	public int testMessage(int size, int count) throws InterruptedException, ExecutionException {
		String content = StringUtils.repeat("*", size);
		int i;
		for(i=0;i<count;i++) {
			long startTime = System.nanoTime();
			runtime.producer.Send("testSize", content);
			long endTime = System.nanoTime();

			long duration = (endTime - startTime)/1000000;  //divide by 1000000 to get milliseconds.
			System.out.println("takes " + duration);
		}
		return i;
	}
	
	@GetMapping("/feedmany")
	public void FeedMany() throws Exception {
		runtime.init();
		for (Integer i=0; i<1000000; i++) {
			runtime.first.FeedManually(new ActivityItem(i.toString()));
		}
	}
	
	@GetMapping("/test")
	public void test() {
		KConsumer c = new KConsumer("testTopic", "test", null, null, msg -> {System.out.println(msg);});
		new Thread(c).start();
	}
	
	@GetMapping("/hello")
	@HystrixCommand
	public String HelloWorld() {
		return "Hello, World";
	}
}
