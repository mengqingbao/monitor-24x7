package com.ombillah.monitoring.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ombillah.monitoring.dao.TroubleshootingDAO;
import com.ombillah.monitoring.domain.ExceptionLogger;
import com.ombillah.monitoring.domain.MethodSignature;
import com.ombillah.monitoring.domain.MonitoredItemTracer;
import com.ombillah.monitoring.domain.SearchFilter;
import com.ombillah.monitoring.service.TroubleshootingService;

/**
 * Service class for retrieving performance data.
 * @author Oussama M Billah
 *
 */
@Service
public class TroubleshootingServiceImpl implements TroubleshootingService {
	
	@Autowired
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

	public List<String> retrieveSqlQueries() {
		return troubeshootingDao.retrieveSqlQueries();
	}

	public List<ExceptionLogger> retrieveExceptionLoggers(SearchFilter searchFilter) {
		return troubeshootingDao.retrieveExceptionLoggers(searchFilter);
	}

	public List<String> retrieveHttpRequestUrls() {
		return troubeshootingDao.retrieveHttpRequestUrls();
	}

}
