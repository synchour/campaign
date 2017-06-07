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
	}

}
