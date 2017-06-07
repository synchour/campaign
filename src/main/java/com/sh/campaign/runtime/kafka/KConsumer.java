package com.sh.campaign.runtime.kafka;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRebalanceListener;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.PartitionInfo;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.WakeupException;

import com.sh.campaign.runtime.ActivityProcessor;

public class KConsumer implements Runnable{
    private static Scanner in;
    
    private Log log = LogFactory.getLog(KConsumer.class);
    
    private String topicName;
    private String myTopicName;
    private String groupId;
    private Consumer<String> c;
    private KafkaConsumer<String,String> kafkaConsumer;
    private KafkaProducerService producer;
    
    public KConsumer(String parentTopicName, String groupId, String myTopicName, KafkaProducerService producer, Consumer<String> c) {
    	 this.topicName = parentTopicName;
         this.groupId = groupId;
         this.c = c;
         this.producer = producer;
         this.myTopicName = myTopicName;
    }

    public static void main2(String[] argv)throws Exception{
        in = new Scanner(System.in);
        
        KConsumer c = new KConsumer("testTopic", "groupIdName", null, ActivityProcessor.producer, (s) -> {System.out.println(s);});
        Thread consumerRunnable = new Thread(c);
        consumerRunnable.start();
        String line = "";
        while (!line.equals("exit")) {
            line = in.next();
        }
        c.getKafkaConsumer().wakeup();
        System.out.println("Stopping consumer .....");
        consumerRunnable.join();
    }
    
    public KafkaConsumer<String,String> getKafkaConsumer(){
        return this.kafkaConsumer;
     }

	@Override
	public void run() {
		
		System.out.println("Group " + this.groupId + " try to subscribing to topic " + topicName);
		
		Properties configProperties = new Properties();
        configProperties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, Config.BOOTSTRAP_SERVERS_CONFIG);
        configProperties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        configProperties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        configProperties.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        
        //Figure out where to start processing messages from
        kafkaConsumer = new KafkaConsumer<String, String>(configProperties);

        /*
    	Map<String, List<PartitionInfo>> info = kafkaConsumer.listTopics();
    	for (String name : info.keySet()) {
			System.out.println("Key:" + name);
			List<PartitionInfo> p = info.get(name);
			for (PartitionInfo pInfo : p) {
				System.out.println(pInfo.partition() + ":" + pInfo.leader().host());
			}
		}*/
    	
    	for (PartitionInfo partition : kafkaConsumer.partitionsFor(topicName)) {
        	System.out.println(partition.topic() + " partition: " + partition.partition());
        }
    	
    	
    	//kafkaConsumer.seekToBeginning(partitions);
    	
    	
    	// where is the consumer group? from the consumer initialization
    	long offsets = 0;
    	TopicPartition tp = new TopicPartition(topicName, 0);
    	OffsetAndMetadata off = new OffsetAndMetadata(offsets);
    	Map<TopicPartition, OffsetAndMetadata> offset = new HashMap<TopicPartition, OffsetAndMetadata>();
    	offset.put(tp, off);
    	
    	Map<TopicPartition, Long> beginningOffsets = kafkaConsumer.beginningOffsets(Arrays.asList(tp));
    	
    	System.out.println("BeginningOffsets:");
    	for (TopicPartition topicP : beginningOffsets.keySet()) {
			System.out.println(topicP.partition() + ":" + beginningOffsets.get(topicP));
		}
    	
        kafkaConsumer.subscribe(Arrays.asList(topicName), new ConsumerRebalanceListener() {
            public void onPartitionsRevoked(Collection<TopicPartition> partitions) {
                System.out.printf("%s topic-partitions are revoked from this consumer\n", Arrays.toString(partitions.toArray()));
            }
            public void onPartitionsAssigned(Collection<TopicPartition> partitions) {
            	
            	//kafkaConsumer.seekToBeginning(Arrays.asList(tp));
            	System.out.println("Commit offset done");

                System.out.printf("%s topic-partitions are assigned to this consumer\n", Arrays.toString(partitions.toArray()));
                //kafkaConsumer.seekToBeginning(partitions);
                kafkaConsumer.seekToEnd(partitions);
            	System.out.println("Commit to beginning done");
            }
        });
        
        try {
            while (true) {
            	// Now I own the partition, I can/should reset to the beginning of partition
            	
            	//System.out.println("Group " + this.groupId + " start polling topic " + topicName);
                ConsumerRecords<String, String> records = kafkaConsumer.poll(1000);
            	//System.out.println(records.count() + " records for " + this.topicName + " group: " + this.groupId );
                
                for (ConsumerRecord<String, String> record : records) {
                	Map<String, Object> data = new HashMap<String, Object>();
                    data.put("partition", record.partition());
                    data.put("offset", record.offset());
                    data.put("value", record.value());
                    //System.out.println(data);
                    
                    //TODO: critical business logic here, it's currently a func and pass through
                    
                    // invoke the consume
                    this.c.accept(record.value());
                    // after the lamdba - send to my own topic so it's done and can be picked by next ones
                    if (myTopicName != null) {
                        this.producer.Send(myTopicName, record.value());                    	
                    }
                }
                
            }
        }catch(WakeupException | InterruptedException | ExecutionException ex){
            System.out.println("Exception caught " + ex.getMessage());
        }finally{
            kafkaConsumer.close();
            System.out.println("After closing KafkaConsumer");
        }
		
	}
}