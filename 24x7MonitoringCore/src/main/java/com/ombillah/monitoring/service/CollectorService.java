package com.ombillah.monitoring.service;

import java.util.List;

import com.ombillah.monitoring.domain.ExceptionLogger;
import com.ombillah.monitoring.domain.HttpRequestUrl;
import com.ombillah.monitoring.domain.MethodSignature;
import com.ombillah.monitoring.domain.MonitoredItemTracer;
import com.ombillah.monitoring.domain.SqlQuery;

/**
 * Service class for collecting performance data.
 * @author Oussama M Billah
 *
 */
public interface CollectorService {

	public List<MethodSignature> retrieveMethodSignatures();
	
	public void saveMethodSignatures(List<MethodSignature> arrayList);
	
	public void saveMonitoredItemTracingStatistics(List<MonitoredItemTracer> MonitoredItemTracers);

	public List<SqlQuery> retrieveSqlQueries();

	public void saveSqlQueries(List<SqlQuery> arrayList);
	
	public void saveException(ExceptionLogger logger);

	public List<HttpRequestUrl> retrieveHttpRequestUrls();

	public void saveHttpRequestUrls(List<HttpRequestUrl> requestUrls);
	
}
