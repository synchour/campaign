package com.sh.campaign.runtime.kafka;

import java.util.concurrent.Future;

import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandKey;

public class SendCommand extends HystrixCommand<Long>{

	private String topic;
	private String content;
	private Producer<String, String> producer;
	
	public SendCommand(Producer<String, String> producer, String topic, String content) {
		super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("SendCommand"))
                .andCommandKey(HystrixCommandKey.Factory.asKey(topic)));
		this.producer = producer;
		this.topic = topic;
		this.content = content;
	}
	
	@Override
	protected Long run() throws Exception {
		//System.out.println("Size of content " + content.getBytes().length);
		ProducerRecord<String, String> rec = new ProducerRecord<String, String>(topic, content);
		Future<RecordMetadata> meta = producer.send(rec);
		RecordMetadata rmeta = meta.get();
		//System.out.println("Success, Offset " + rmeta.offset());
		return rmeta.offset();
		//return meta.get().timestamp();
	}

}
