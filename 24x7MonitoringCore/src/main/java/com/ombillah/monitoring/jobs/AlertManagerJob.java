package com.ombillah.monitoring.jobs;

import java.util.List;
import java.util.Properties;

import javax.inject.Inject;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import com.ombillah.monitoring.domain.ManagedAlert;
import com.ombillah.monitoring.domain.MonitoredItemTracer;
import com.ombillah.monitoring.service.CollectorService;

/**
 * Scheduled Job to run periodically and check for Alerts.
 * @author Oussama M Billah
 *
 */
public class AlertManagerJob implements Runnable {
	
	@Inject
	private CollectorService collectorService;
	
	public void run() {
		try {
			List<ManagedAlert> enabledAlerts = collectorService.getEnabledAlerts();
			final String username = "obillah@gmail.com";
			final String password = "barca4ever86";
	 
			Properties props = new Properties();
			props.put("mail.smtp.auth", "true");
			props.put("mail.smtp.starttls.enable", "true");
			props.put("mail.smtp.host", "smtp.gmail.com");
			props.put("mail.smtp.port", "587");
			
			Session session = Session.getInstance(props,
					  new javax.mail.Authenticator() {
						protected PasswordAuthentication getPasswordAuthentication() {
							return new PasswordAuthentication(username, password);
						}
					  });
			
			for(ManagedAlert alert : enabledAlerts) {
				Long threshold = alert.getThreshold();
				String monitoredItem = alert.getItemName();
				String itemType = alert.getItemType();
				Long timeToAlert = alert.getTimeToAlertInMins();
				String email = alert.getAlertEmail();
				
				MonitoredItemTracer slowOperation = collectorService.checkPerformanceDegredation(
						monitoredItem, itemType, timeToAlert, threshold);
				
				if(slowOperation != null) {
					String subject = "Alert for " + itemType + " Slowness!";
					String body = "the following item :\n\n %s \n\n is slower than usual \n\n"
								+ "Current Average: %f \n Current Max value: %f \n Threshold: %d. " +
								"\n\n Automated Alert Email.";
					body = String.format(body, 
							monitoredItem, 
							slowOperation.getAverage(), 
							slowOperation.getMax(),
							threshold);
					
					String from = "DoNotReplay@localhost";
					String to = email;
					
					try {
						 
						Message message = new MimeMessage(session);
						message.setFrom(new InternetAddress("support@24x7monitoring.com"));
						message.setRecipients(Message.RecipientType.TO,
							InternetAddress.parse("obillah@gmail.com"));
						message.setSubject(subject);
						message.setText(body);
			 
						//Transport.send(message);
			 
						System.out.println("Alert!");
			 
					} catch (MessagingException e) {
						throw new RuntimeException(e);
					}
					
				}
				
			} 
		} catch (Throwable ex) {
				ex.printStackTrace();
		}
	}
		

}
