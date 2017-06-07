package com.sh.campaign.data.documentdb;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;

public class DocumentDBWriteCommand extends HystrixCommand<Long>{

	
	protected DocumentDBWriteCommand(HystrixCommandGroupKey group) {
		super(group);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected Long run() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
	
	

}
