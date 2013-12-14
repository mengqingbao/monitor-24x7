package com.ombillah.monitoring.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.ombillah.monitoring.dao.TroubleshootingDAO;
import com.ombillah.monitoring.domain.ExceptionLogger;
import com.ombillah.monitoring.domain.HttpRequestUrl;
import com.ombillah.monitoring.domain.MethodSignature;
import com.ombillah.monitoring.domain.MonitoredItemTracer;
import com.ombillah.monitoring.domain.SearchFilter;
import com.ombillah.monitoring.domain.SqlQuery;
import com.ombillah.monitoring.service.TroubleshootingService;

/**
 * Service class for retrieving performance data.
 * @author Oussama M Billah
 *
 */
@Service("troubleshootingService")
public class TroubleshootingServiceImpl implements TroubleshootingService {
	
	@Resource(name="troubleshootingDAO")
	private TroubleshootingDAO troubeshootingDao;

	public List<MethodSignature> retrieveMethodSignatures() {
		return troubeshootingDao.retrieveMethodSignatures();
	}
	
	public List<MonitoredItemTracer> retrieveItemStatistics(SearchFilter searchFilter) {
		return troubeshootingDao.retrieveItemStatistics(searchFilter);
	}
	
	public List<MonitoredItemTracer> retrieveItemStatisticsGroupedByMonitoredItem(SearchFilter searchFilter) {
		return troubeshootingDao.retrieveItemStatisticsGroupedByMonitoredItem(searchFilter);
	}

	public List<SqlQuery> retrieveSqlQueries() {
		return troubeshootingDao.retrieveSqlQueries();
	}

	public List<ExceptionLogger> retrieveExceptionLoggers(SearchFilter searchFilter) {
		return troubeshootingDao.retrieveExceptionLoggers(searchFilter);
	}

	public List<HttpRequestUrl> retrieveHttpRequestUrls() {
		return troubeshootingDao.retrieveHttpRequestUrls();
	}

	public MonitoredItemTracer checkPerformanceDegredation(
			String monitoredItem, String itemType, Long timeToAlert, Long threshold) {
		return troubeshootingDao.checkPerformanceDegredation(monitoredItem, 
				itemType,
				timeToAlert,
				threshold);
	}

	public void saveMethodSignatures(List<MethodSignature> methodSignatures) {
		troubeshootingDao.saveMethodSignatures(methodSignatures);

	}

	public void saveMonitoredItemTracingStatistics(List<MonitoredItemTracer> monitoredItemTracers) {
		troubeshootingDao.saveMonitoredItemTracingStatistics(monitoredItemTracers);
	}

	public void saveSqlQueries(List<SqlQuery> queries) {
		troubeshootingDao.saveSqlQueries(queries);
	}

	public void saveExceptions(List<ExceptionLogger> exceptions) {
		troubeshootingDao.saveExceptions(exceptions);
		
	}

	public void saveHttpRequestUrls(List<HttpRequestUrl> requestUrls) {
		troubeshootingDao.saveHttpRequestUrls(requestUrls);
	}

}
