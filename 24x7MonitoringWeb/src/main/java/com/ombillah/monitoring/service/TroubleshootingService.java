package com.ombillah.monitoring.service;

import java.util.List;

import com.ombillah.monitoring.domain.ExceptionLogger;
import com.ombillah.monitoring.domain.HttpRequestUrl;
import com.ombillah.monitoring.domain.MethodSignature;
import com.ombillah.monitoring.domain.MonitoredItemTracer;
import com.ombillah.monitoring.domain.SearchFilter;
import com.ombillah.monitoring.domain.SqlQuery;

/**
 * Service class for retrieving performance data.
 * @author Oussama M Billah
 *
 */
public interface TroubleshootingService {

	public List<MethodSignature> retrieveMethodSignatures();

	public List<MonitoredItemTracer> retrieveItemStatistics(SearchFilter searchFilter);
	
	public List<MonitoredItemTracer> retrieveItemStatisticsGroupedByMonitoredItem(SearchFilter searchFilter);

	public List<SqlQuery> retrieveSqlQueries();
	
	public List<ExceptionLogger> retrieveExceptionLoggers(SearchFilter searchFilter);

	public List<HttpRequestUrl> retrieveHttpRequestUrls();
	
	public void saveMethodSignatures(List<MethodSignature> arrayList);
	
	public void saveMonitoredItemTracingStatistics(List<MonitoredItemTracer> MonitoredItemTracers);
	
	public void saveSqlQueries(List<SqlQuery> arrayList);
	
	public void saveHttpRequestUrls(List<HttpRequestUrl> requestUrls);
	
	public void saveExceptions(List<ExceptionLogger> exceptions);

	public MonitoredItemTracer checkPerformanceDegredation(
			String monitoredItem, String itemType, Long timeToAlert,
			Long threshold);


}
