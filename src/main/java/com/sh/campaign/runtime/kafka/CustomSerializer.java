package com.sh.campaign.runtime.kafka;

import java.io.IOException;
import java.io.Serializable;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class CustomSerializer {

	public static String ObjectToJson(Object obj) throws JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();
		return mapper.writeValueAsString(obj);
	}
	
	/*
	static <T> JsonToObject<T>(String jsonString, Class<?> target) {
		ObjectMapper mapper = new ObjectMapper();
		JavaType javaType = mapper.getTypeFactory().constructParametricType(BasicMessage.class, classType);
		T value = mapper.readValue(jsonString, javaType);
	}*/
	
	public static <T extends Serializable> Message<T> JsonToObject(String jsonString, Class<T> classType) throws JsonParseException, JsonMappingException, IOException {
		ObjectMapper mapper = new ObjectMapper();
        JavaType javaType = mapper.getTypeFactory().constructParametricType(Message.class, classType);
        return mapper.readValue(jsonString, javaType);
	}
	
	static class Anything implements Serializable {

		private static final long serialVersionUID = 1L;
		public String data;
		
		public Anything() {	
		}
		
		public Anything(String data) {
			this.data = data;
		}
		
		@Override
		public String toString() {
			return this.data;
		}
	}
	
	public static void main2(String[] args) throws IOException {
		Message<Anything> bla = new Message<Anything>();
		Anything a = new Anything("Good morning");
		bla.setPayload(a);
		
		String json = ObjectToJson(bla);
		
		System.out.println(json);
		
		Message<Anything> result = JsonToObject(json, Anything.class);
		System.out.println(result.toString());
	}
}
