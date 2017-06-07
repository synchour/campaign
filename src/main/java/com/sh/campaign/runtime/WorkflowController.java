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
	
	// curl http://localhost:8080/testsize?size=100&count=10000
	// check http://localhost:8080/hystrix/monitor?stream=http%3A%2F%2Flocalhost%3A8080%2Fhystrix.stream
	
	@GetMapping("/testsize")
	public void testMessage(int size, int count) throws InterruptedException, ExecutionException {
		String content = StringUtils.repeat("*", size);
		for(int i=0;i<count;i++) {
			runtime.producer.Send("testSize", content);			
		}
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
