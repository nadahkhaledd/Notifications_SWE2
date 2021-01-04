package net.codejava;


import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class NotificationController {

	Queue<Notification> SMS = new LinkedList<>();
	Queue<Notification> MAIL = new LinkedList<>();
	
	
	@Autowired
	private TemplateService service;
	@Autowired
	private SMSNotificationService serviceSMS;
	@Autowired
	private MailNotificationService serviceMAIL;
	
	public Notification prepareNotification(String msg, String[] values) {
		Notification obj = new Notification();
		int index = 0;
		int i = 0;
		while(true) {
			if(msg.indexOf("$", index) == -1) {
				break;
			}
			String temp = msg.substring(msg.indexOf("$", index), msg.indexOf("}", index)+1);
			index = msg.indexOf("}", index)+1;
			msg = msg.replace(temp, values[i]);
			i++;
		}
		obj.setContent(msg);
		return obj;
	}
	public String validateRequest(Request request) {
		String response = "Valid";
		if(!(request.getMethod().equalsIgnoreCase("mail") || request.getMethod().equalsIgnoreCase("sms"))) {
			response = "Method is not valid";
			return response;
		}
		if(request.getMethod().equalsIgnoreCase("sms")) {
			if (!request.getTarget().matches("[0-9]+")) {
				response = "target doesn't match method requirements, a number should be provided";
				return response;
			}
		}
		else {
			String regex = "^(.+)@(.+)$";
			Pattern pattern = Pattern.compile(regex);
			Matcher matcher = pattern.matcher(request.getTarget());
			if (!matcher.matches()) {
				response = "target doesn't match method requirements, an email should be provided";
				return response;
			}
		}
		return response;
	}
	
	public void saveNotification(String content, String method, String target) {
		
		SMS notifySMS = new SMS();
		MAIL notifyMAIL = new MAIL();
		
		if(method.equalsIgnoreCase("sms"))
		{
			notifySMS.setContent(content);
			notifySMS.setTarget(target);
			serviceSMS.save(notifySMS);
		}
		else
		{
			notifyMAIL.setContent(content);
			notifyMAIL.setTarget(target);
			serviceMAIL.save(notifyMAIL);
		}
	}
	
	@GetMapping("/templates3/{id}")
	public ResponseEntity<SMS> getSMSByID(@PathVariable Integer id) {
		try {
			SMS obj = serviceSMS.getByID(id);
			return new ResponseEntity<SMS>(obj, HttpStatus.OK);
		}
		catch(NoSuchElementException e) {
			return new ResponseEntity<SMS>(HttpStatus.NOT_FOUND);
		}
	}
	
	@GetMapping("/templates4/{id}")
	public ResponseEntity<MAIL> getMAILByID(@PathVariable Integer id) {
		try {
			MAIL obj = serviceMAIL.getByID(id);
			return new ResponseEntity<MAIL>(obj, HttpStatus.OK);
		}
		catch(NoSuchElementException e) {
			return new ResponseEntity<MAIL>(HttpStatus.NOT_FOUND);
		}
	}
	
	@DeleteMapping("/templates3/{id}")
	public void deleteSMS(@PathVariable int id) {
		serviceSMS.delete(id);
	}
	
	@DeleteMapping("/templates4/{id}")
	public void deleteMAIL(@PathVariable int id) {
		serviceMAIL.delete(id);
	}
	
	@GetMapping("/templates5/")
	public ResponseEntity<SMS> getFirstRowSMS()
	{
		
		try {
			SMS first = serviceSMS.getFirstID();
			return new ResponseEntity<SMS>(first, HttpStatus.OK);
		}
		catch(NoSuchElementException e) {
			return new ResponseEntity<SMS>(HttpStatus.NOT_FOUND);
		}
		
	}
	
	@GetMapping("/templates6/")
	public ResponseEntity<MAIL> getFirstRowMAIL()
	{
		
		try {
			MAIL first = serviceMAIL.getFirstID();
			return new ResponseEntity<MAIL>(first, HttpStatus.OK);
		}
		catch(NoSuchElementException e) {
			return new ResponseEntity<MAIL>(HttpStatus.NOT_FOUND);
		}
		
	}
	
	@GetMapping("/templates2/{type}/{category}")
	public ResponseEntity<?> getRequest(@PathVariable String type ,@PathVariable String category,@RequestBody Request request ) {
		try {
			 String response = validateRequest(request);
			 if(!response.equalsIgnoreCase("Valid")) {
				 return new ResponseEntity<String>(response, HttpStatus.BAD_REQUEST);
			 }
			 Template template = service.getByType(type, category);
		     Notification obj = new Notification();
			 obj = prepareNotification(template.getText(), request.getValues());
			 if(request.getMethod().equalsIgnoreCase("sms"))
			 {
				 SMS.add(obj);
			 }
			 else if(request.getMethod().equalsIgnoreCase("mail"))
			 {
				 MAIL.add(obj);
			 }
			 saveNotification(obj.getContent(), request.getMethod(), request.getTarget());
			 return new ResponseEntity<>(template, HttpStatus.OK);
		}
		catch(NoSuchElementException e) {
			return new ResponseEntity<Template>(HttpStatus.NOT_FOUND);
		}
	}
}
