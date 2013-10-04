package com.ombillah.monitoring.service.impl;

import java.util.List;

import javax.inject.Inject;

import com.ombillah.monitoring.dao.CollectorDAO;
import com.ombillah.monitoring.domain.ExceptionLogger;
import com.ombillah.monitoring.domain.HttpRequestUrl;
import com.ombillah.monitoring.domain.ManagedAlert;
import com.ombillah.monitoring.domain.MethodSignature;
import com.ombillah.monitoring.domain.MonitoredItemTracer;
import com.ombillah.monitoring.domain.SqlQuery;
import com.ombillah.monitoring.service.CollectorService;

/**
 * Service class for collecting performance data.
 * @author Oussama M Billah
 *
 */
public class CollectorServiceImpl implements CollectorService {
	
	@Inject
	private CollectorDAO collectorDao;
	
	public List<MethodSignature> retrieveMethodSignatures() {
		return collectorDao.retrieveMethodSignatures();
	}

	public void saveMethodSignatures(List<MethodSignature> methodSignatures) {
		collectorDao.saveMethodSignatures(methodSignatures);

	}

	public void saveMonitoredItemTracingStatistics(List<MonitoredItemTracer> MonitoredItemTracers) {
		collectorDao.saveMonitoredItemTracingStatistics(MonitoredItemTracers);
	}

	public List<SqlQuery> retrieveSqlQueries() {
		return collectorDao.retrieveSqlQueries();
	}

	public void saveSqlQueries(List<SqlQuery> queries) {
		collectorDao.saveSqlQueries(queries);
	}

	public void saveException(ExceptionLogger logger) {
		collectorDao.saveException(logger);
		
	}

	public List<HttpRequestUrl> retrieveHttpRequestUrls() {
		return collectorDao.retrieveHttpRequestUrls();
	}

	public void saveHttpRequestUrls(List<HttpRequestUrl> requestUrls) {
		collectorDao.saveHttpRequestUrls(requestUrls);
	}

	public List<ManagedAlert> getEnabledAlerts() {
		return collectorDao.getEnabledAlerts();
	}

	public MonitoredItemTracer checkPerformanceDegredation(String monitoredItem, 
			String itemType, Long timeToAlert, Long threshold) {
		return collectorDao.checkPerformanceDegredation(monitoredItem, itemType, timeToAlert, threshold);
	}

}
