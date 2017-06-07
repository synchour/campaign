package com.sh.campaign.data.sql;

import java.io.Serializable;

public class KafkaOffsetKey implements Serializable {
	String topicName;
	String groupId;
}
