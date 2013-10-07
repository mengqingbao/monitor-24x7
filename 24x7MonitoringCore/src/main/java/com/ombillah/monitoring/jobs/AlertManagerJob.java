package com.ombillah.monitoring.jobs;

import java.util.List;

import javax.inject.Inject;

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
					String subject = "Alert for " + itemType + "Slowness!";
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
					
					System.out.println(body);
					
				}
				continue;
				
			} 
		} catch (Throwable ex) {
				ex.printStackTrace();
		}
	}
		

}
