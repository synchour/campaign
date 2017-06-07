package com.sh.campaign.data.sql;


import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;

public class WriteToSqlCommand extends HystrixCommand<Long>{

	private KafkaOffsetRepository repo;
	private String topicName;
	private String groupId;
	private long offset;
	
	public WriteToSqlCommand(KafkaOffsetRepository repo, String topicName, String groupId, long offset) {
		super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("WriteToSql")));
		this.repo = repo;
		this.topicName = topicName;
		this.groupId = groupId;
		this.offset = offset;
	}
	@Override
	protected Long run() throws Exception {
		KafkaOffsetInfo info = new KafkaOffsetInfo();
		info.setGroupId(groupId);
		info.setTopicName(topicName);
		info.setOffset(offset);
		//repo.save(info);
		return 1L;
	}

}
