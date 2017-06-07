package com.sh.campaign.data.sql;

import java.io.Serializable;

//import javax.persistence.Entity;
//import javax.persistence.GeneratedValue;
//import javax.persistence.GenerationType;
//import javax.persistence.Id;
//import javax.persistence.IdClass;
//import javax.persistence.Table;

//@Entity
//@Table(name = "offset")
//@IdClass(KafkaOffsetKey.class)
public class KafkaOffsetInfo implements Serializable {

	//@Id	
	private String topicName;
	//@Id 
	private String groupId;
	private long offset;
	
	public String getTopicName() {
		return topicName;
	}
	public void setTopicName(String topicName) {
		this.topicName = topicName;
	}
	public String getGroupId() {
		return groupId;
	}
	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}
	public long getOffset() {
		return offset;
	}
	public void setOffset(long offset) {
		this.offset = offset;
	}
}
