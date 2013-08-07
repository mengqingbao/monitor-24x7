package com.ombillah.monitoring.dao;

import java.util.List;

import com.ombillah.monitoring.domain.ExceptionLogger;
import com.ombillah.monitoring.domain.HttpRequestUrl;
import com.ombillah.monitoring.domain.MethodSignature;
import com.ombillah.monitoring.domain.MonitoredItemTracer;
import com.ombillah.monitoring.domain.SqlQuery;

/**
 * Data Access Object for collecting performance data.
 * @author Oussama M Billah
 *
 */
public interface CollectorDAO {
	
	public List<MethodSignature> retrieveMethodSignatures();
	
	public void saveMethodSignatures(List<MethodSignature> methodSignatures);
	
	public void saveMonitoredItemTracingStatistics(List<MonitoredItemTracer> MonitoredItemTracers);

	public List<SqlQuery> retrieveSqlQueries();

	public void saveSqlQueries(List<SqlQuery> queries);

	public void saveException(ExceptionLogger logger);

	public List<HttpRequestUrl> retrieveHttpRequestUrls();

	public void saveHttpRequestUrls(List<HttpRequestUrl> requestUrls);
}
