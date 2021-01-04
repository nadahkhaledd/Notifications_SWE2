package net.codejava;

import java.util.HashMap;
import java.util.Map;

import org.springframework.web.client.RestTemplate;

public class Client {
	
	private static final String GET_SMS_BY_ID = "http://localhost:8080/templates3/{id}";
	private static final String GET_FIRST_ID_SMS = "http://localhost:8080/templates5/";
	private static final String DELETE_SMS = "http://localhost:8080/templates3/{id}";
	private static final String GET_MAIL_BY_ID = "http://localhost:8080/templates4/{id}";
	private static final String GET_FIRST_ID_MAIL = "http://localhost:8080/templates6/";
	private static final String DELETE_MAIL = "http://localhost:8080/templates4/{id}";
	
	static RestTemplate restTemplate = new RestTemplate();
	
	public static void getSMSByID() {
		Map<String, Integer> param = new HashMap<>();
		param.put("id", 1);
		SMS obj = restTemplate.getForObject(GET_SMS_BY_ID, SMS.class, param);
		System.out.println(obj.getContent());
		System.out.println(obj.getTarget());
	}
	
	public static void dequeueSMS()
	{
		SMS obj = restTemplate.getForObject(GET_FIRST_ID_SMS, SMS.class);
		System.out.println("\nNotification sent : " +obj.getContent() + "\n");
		delete_SMS(obj.getId());
		
		
	}
	
	public static void delete_SMS(int id) {
		Map<String, Integer> param = new HashMap<>();
		param.put("id", id);
		restTemplate.delete(DELETE_SMS, param);
	}
	
	public static void getMAILByID() {
		Map<String, Integer> param = new HashMap<>();
		param.put("id", 1);
		MAIL obj = restTemplate.getForObject(GET_MAIL_BY_ID, MAIL.class, param);
		System.out.println(obj.getContent());
		System.out.println(obj.getTarget());
	}
	
	public static void dequeueMAIL()
	{
		MAIL obj = restTemplate.getForObject(GET_FIRST_ID_MAIL, MAIL.class);
		System.out.println("\nNotification sent: " + obj.getContent() + "\n");
		delete_MAIL(obj.getId());
		
		
	}
	
	public static void delete_MAIL(int id) {
		Map<String, Integer> param = new HashMap<>();
		param.put("id", id);
		restTemplate.delete(DELETE_MAIL, param);
	}

}
