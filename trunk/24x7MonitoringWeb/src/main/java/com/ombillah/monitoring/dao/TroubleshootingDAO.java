package com.ombillah.monitoring.dao;

import java.util.List;

import com.ombillah.monitoring.domain.ExceptionLogger;
import com.ombillah.monitoring.domain.HttpRequestUrl;
import com.ombillah.monitoring.domain.MethodSignature;
import com.ombillah.monitoring.domain.MonitoredItemTracer;
import com.ombillah.monitoring.domain.SearchFilter;
import com.ombillah.monitoring.domain.SqlQuery;

/**
 * Data Access Object for retrieving performance data.
 * @author Oussama M Billah
 *
 */
public interface TroubleshootingDAO {
	
	public List<MethodSignature> retrieveMethodSignatures();
	
	public List<MonitoredItemTracer> retrieveItemStatisticsGroupedByMonitoredItem(SearchFilter searchFilter);

	public List<MonitoredItemTracer> retrieveItemStatistics(SearchFilter searchFilter);

	public List<SqlQuery> retrieveSqlQueries();
	
	public List<ExceptionLogger> retrieveExceptionLoggers(SearchFilter searchFilter);

	public List<HttpRequestUrl> retrieveHttpRequestUrls();
	
	public void saveMethodSignatures(List<MethodSignature> methodSignatures);
	
	public void saveMonitoredItemTracingStatistics(List<MonitoredItemTracer> monitoredItemTracers);

	public void saveSqlQueries(List<SqlQuery> queries);

	public void saveExceptions(List<ExceptionLogger> exceptions);

	public void saveHttpRequestUrls(List<HttpRequestUrl> requestUrls);

	public MonitoredItemTracer checkPerformanceDegredation(
			String monitoredItem, String itemType, Long timeToAlert,
			Long threshold);


}
