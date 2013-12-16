package com.ombillah.monitoring.dao;

import java.util.List;

import com.ombillah.monitoring.domain.ReportContent;
import com.ombillah.monitoring.domain.ReportSchedule;


/**
 * DAO for Report Scheduling Data Access logic.
 * @author Oussama M Billah
 *
 */
public interface ReportSchedulingDAO {

	public boolean saveReport(ReportSchedule report);
	
	public ReportSchedule retrieveReport(String itemName, String itemType);

	public List<ReportSchedule> getEnabledReports();

	public List<ReportContent> getReportContent(ReportSchedule report);
	
}
