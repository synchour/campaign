package com.sh.campaign;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.netflix.hystrix.dashboard.EnableHystrixDashboard;

@SpringBootApplication
@EnableHystrixDashboard
@EnableCircuitBreaker
public class CampaignApplication {

	public static void main(String[] args) {
		
		/*
		FlowableOn fo = new FlowableOn();
		Flowable<String> f = Flowable.create(fo, BackpressureStrategy.BUFFER);
		
		f.subscribe(new Consumer<String>() {

			@Override
			public void accept(String t) throws Exception {
				System.out.println(t + "emmm");
			}
		});
		
		f.subscribe(t -> {System.out.println(t + "2emmm");});
		
		fo.onNext("Hello");
		fo.onNext("World");
		*/
		SpringApplication.run(CampaignApplication.class, args);
	}
}
