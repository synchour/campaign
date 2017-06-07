package com.sh.campaign.data.sql;
import java.util.List;

//import org.springframework.data.repository.CrudRepository;

public interface KafkaOffsetRepository { //extends CrudRepository<KafkaOffsetInfo, Integer> {
	public List<KafkaOffsetInfo> findByTopicName(String topicName);
}
