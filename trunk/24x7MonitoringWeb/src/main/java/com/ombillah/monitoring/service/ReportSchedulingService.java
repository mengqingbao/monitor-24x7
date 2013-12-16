package com.ombillah.monitoring.service;

import com.ombillah.monitoring.domain.ReportSchedule;

/**
 * Service for Report Scheduling functionality.
 * @author Oussama M Billah.
 *
 */
public interface ReportSchedulingService {
	
	public boolean saveReport(ReportSchedule report);
	
	public ReportSchedule retrieveReport(String itemName, String itemType);
	
	public void scheduleReport(ReportSchedule report);
	
	public void unscheduleReport(String jobDetailName);
	
}