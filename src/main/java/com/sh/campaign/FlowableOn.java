package com.sh.campaign;

import java.util.LinkedList;
import java.util.List;

import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;

public class FlowableOn implements FlowableOnSubscribe<String>{

	//public FlowableEmitter<String> Emitter;
	private List<FlowableEmitter<String>> Emitters = new LinkedList<FlowableEmitter<String>>();
	
	@Override
	public void subscribe(FlowableEmitter<String> e) throws Exception {
		Emitters.add(e);
	}

	public void onNext(String value) {
		
		System.out.println("Handling value " + value);
		
		for (FlowableEmitter<String> flowableEmitter : Emitters) {
			flowableEmitter.onNext(value);
		}
	}
	
}
