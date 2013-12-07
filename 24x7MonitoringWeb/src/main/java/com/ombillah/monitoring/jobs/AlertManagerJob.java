package com.ombillah.monitoring.jobs;

import java.util.List;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.ombillah.monitoring.domain.ManagedAlert;
import com.ombillah.monitoring.domain.MonitoredItemTracer;
import com.ombillah.monitoring.service.AlertManagementService;
import com.ombillah.monitoring.service.TroubleshootingService;

@Component
public class AlertManagerJob {
	
	@Autowired
	private AlertManagementService alertService;
	
	@Autowired
	private TroubleshootingService troubleshootingService;
	
	@Autowired
	private JavaMailSender mailSender;
	
	@Scheduled(fixedDelayString = "${ALERT_JOB_FREQUENCY_IN_MS}")
    public void process() {
		try {

			List<ManagedAlert> enabledAlerts = alertService.getEnabledAlerts();
		
			for(ManagedAlert alert : enabledAlerts) {
				Long threshold = alert.getThreshold();
				String monitoredItem = alert.getItemName();
				String itemType = alert.getItemType();
				Long timeToAlert = alert.getTimeToAlertInMins();
				String email = alert.getAlertEmail();
				
				MonitoredItemTracer slowOperation = troubleshootingService.checkPerformanceDegredation(
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
										
					sendAlertEmail(from, to, subject, body);
					
				}
				
			} 
		} catch (Throwable ex) {
				ex.printStackTrace();
		}
    }
	
	public void sendAlertEmail(String from, String to, String subject, String body) throws MessagingException {

		MimeMessage message = mailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message);
		helper.setFrom(from);
		helper.setTo(to);
		helper.setSubject(subject);
		helper.setText(body, false);
		mailSender.send(message);
	}
	
}