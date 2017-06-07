package com.sh.campaign.runtime.kafka;

import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;

@Service
@EnableCircuitBreaker
public class KafkaProducerService {

	private org.apache.kafka.clients.producer.Producer<String, String> producer;

	public KafkaProducerService() {
	  Properties configProperties = new Properties();
      configProperties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, Config.BOOTSTRAP_SERVERS_CONFIG);
      configProperties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,"org.apache.kafka.common.serialization.ByteArraySerializer");
      configProperties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,"org.apache.kafka.common.serialization.StringSerializer");
      configProperties.put(ProducerConfig.MAX_REQUEST_SIZE_CONFIG, "10485760");
      
      producer = new KafkaProducer<String, String>(configProperties);
	}
  
	@HystrixCommand
  	public void Send(String topic, String content) throws InterruptedException, ExecutionException {
  		//System.out.println("Sending " + content + " to " + topicName + ">>>>>");
		/*
		ProducerRecord<String, String> rec = new ProducerRecord<String, String>(topic, content);
		Future<RecordMetadata> meta = producer.send(rec);
		meta.get();
		*/
		new SendCommand(producer, topic, content).execute();
		//System.out.println("Sending " + content + " to " + topicName + " done <<<<<");
		//System.out.println(meta.get().timestamp());
	}

}