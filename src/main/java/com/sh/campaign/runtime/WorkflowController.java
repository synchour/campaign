package com.sh.campaign.runtime;

import java.util.concurrent.ExecutionException;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.sh.campaign.data.documentdb.DocumentDBService;
import com.sh.campaign.data.sql.KafkaOffsetRepository;
import com.sh.campaign.data.sql.WriteToSqlCommand;
import com.sh.campaign.runtime.kafka.KConsumer;

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
	
	// curl http://localhost:8080/testsize?size=100
	@GetMapping("/testsize")
	public void testMessage(int size) throws InterruptedException, ExecutionException {
		String content = StringUtils.repeat("*", size);
		runtime.producer.Send("testSize", content);
	}
	
	@GetMapping("/feedmany")
	public void FeedMany() throws Exception {
		runtime.init();
		for (Integer i=0; i<1000000; i++) {
			runtime.first.FeedManually(new ActivityItem(i.toString()));
		}
	}
	
	@GetMapping("/writesql")
	public void writesql(int n) throws Exception {
		for (Integer i=0; i<n; i++) {
			//new WriteToSqlCommand(repo, "testTopic", "testGroup", i).execute();
		}
	}
	
	@GetMapping("/writedoc")
	public void writedoc(int n) throws Exception {
		for (Integer i=0; i<n; i++) {
			//docservice.writeDocument("Hello" + i);
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
