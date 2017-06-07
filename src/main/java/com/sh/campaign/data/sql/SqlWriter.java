package com.sh.campaign.data.sql;

import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.stereotype.Service;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;

@Service
@EnableCircuitBreaker
public class SqlWriter {

	@HystrixCommand
	public void WriteOffset(String topicName, String groupId, long offset) {
		
	}
}
