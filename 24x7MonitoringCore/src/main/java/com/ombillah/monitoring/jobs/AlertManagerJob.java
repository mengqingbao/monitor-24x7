package com.ombillah.monitoring.jobs;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

import javax.inject.Inject;


import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;

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
								+ "Current Average: %.2f \n Current Max value: %.2f \n Threshold: %d. " +
								"\n\n Automated Alert Email.";
					body = String.format(body, 
							monitoredItem, 
							slowOperation.getAverage(), 
							slowOperation.getMax(),
							threshold);
					
					String from = "DoNotReplay@localhost";
					String to = email;
										
					//sendFromGMail(from, USER_NAME, PASSWORD, to , subject, body);
					
				}
				
			} 
		} catch (Throwable ex) {
				ex.printStackTrace();
		}
	}


    private static void sendFromGMail(String from, String userName, String pass, String to, String subject, String body) throws EmailException {
    	String configFile = System.getProperty("monitoring.configLocation");
		Properties properties = new Properties();
		try {
			properties.load(new FileInputStream(configFile));
		} catch (IOException e) {
			e.printStackTrace();
		}
		String hostName = properties.getProperty("SMTP_HOST");
		String port = properties.getProperty("SMTP_PORT");
		String userId = properties.getProperty("SMTP_USERNAME");
		String password = properties.getProperty("SMTP_USERNAME");
        Email email = new SimpleEmail();
        email.setHostName(hostName);
        email.setSmtpPort(Integer.parseInt(port));
        email.setAuthenticator(new DefaultAuthenticator(userId, password));
        email.setSSLOnConnect(true);
        email.setFrom(from);
        email.setSubject(subject);
        email.setMsg(body);
        email.addTo(to);
        email.send();
    }
		

}
