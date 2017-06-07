package com.sh.campaign;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.sh.campaign.data.sql.KafkaOffsetInfo;
import com.sh.campaign.data.sql.KafkaOffsetRepository;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CampaignApplicationTests {

	@Autowired
	KafkaOffsetRepository repo;
	
	@Test
	public void SqlTest() {
		KafkaOffsetInfo offset = new KafkaOffsetInfo();
		offset.setTopicName("testTopic");
		offset.setGroupId("groupId");
		offset.setOffset(1000);
		//repo.save(offset);
		
		KafkaOffsetInfo offset2 = new KafkaOffsetInfo();
		offset2.setTopicName("testTopic");
		offset2.setGroupId("groupId");
		offset2.setOffset(1001);
		//repo.save(offset2);
		
		List<KafkaOffsetInfo> info = repo.findByTopicName("testTopic");
		assert info.size() == 1;
		assert info.get(0).getOffset() == 1001;
		System.out.println(info.get(0).getOffset());
	}

}
