package com.ombillah.monitoring.service;

import java.util.List;

import com.ombillah.monitoring.domain.ExceptionLogger;
import com.ombillah.monitoring.domain.MethodSignature;
import com.ombillah.monitoring.domain.MonitoredItemTracer;
import com.ombillah.monitoring.domain.SearchFilter;

/**
 * Service class for retrieving performance data.
 * @author Oussama M Billah
 *
 */
public interface TroubleshootingService {

	public List<MethodSignature> retrieveMethodSignatures();

	public List<MonitoredItemTracer> retrieveItemStatistics(SearchFilter searchFilter);
	
	public List<MonitoredItemTracer> retrieveItemStatisticsGroupedByMonitoredItem(SearchFilter searchFilter);

	public List<String> retrieveSqlQueries();
	
	public List<ExceptionLogger> retrieveExceptionLoggers(SearchFilter searchFilter);

	public List<String> retrieveHttpRequestUrls();
	
	public MonitoredItemTracer checkPerformanceDegredation(
			String monitoredItem, String itemType, Long timeToAlert,
			Long threshold);

}
