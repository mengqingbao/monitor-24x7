package com.ombillah.monitoring.dao;

import java.util.List;

import com.ombillah.monitoring.domain.ExceptionLogger;
import com.ombillah.monitoring.domain.MethodSignature;
import com.ombillah.monitoring.domain.MonitoredItemTracer;
import com.ombillah.monitoring.domain.SearchFilter;

/**
 * Data Access Object for retrieving performance data.
 * @author Oussama M Billah
 *
 */
public interface TroubleshootingDAO {
	
	public List<MethodSignature> retrieveMethodSignatures();
	
	public List<MonitoredItemTracer> retrieveItemStatisticsGroupedByMonitoredItem(SearchFilter searchFilter);

	public List<MonitoredItemTracer> retrieveItemStatistics(SearchFilter searchFilter);

	public List<String> retrieveSqlQueries();
	
	public List<ExceptionLogger> retrieveExceptionLoggers(SearchFilter searchFilter);

	public List<String> retrieveHttpRequestUrls();

}
