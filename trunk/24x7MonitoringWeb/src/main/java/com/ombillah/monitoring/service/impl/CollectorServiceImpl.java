package com.ombillah.monitoring.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ombillah.monitoring.dao.CollectorDAO;
import com.ombillah.monitoring.domain.MethodSignature;
import com.ombillah.monitoring.domain.MethodTracer;
import com.ombillah.monitoring.domain.SearchFilter;
import com.ombillah.monitoring.service.CollectorService;

/**
 * Service class for collecting performance data.
 * @author Oussama M Billah
 *
 */
@Service
public class CollectorServiceImpl implements CollectorService {
	
	@Autowired
	private CollectorDAO collectorDao;
	
	public List<MethodSignature> retrieveMethodSignatures() {
		return collectorDao.retrieveMethodSignatures();
	}
	
	public List<MethodTracer> retrieveMethodStatistics(SearchFilter searchFilter) {
		return collectorDao.retrieveMethodStatistics(searchFilter);
	}
	
	public List<MethodTracer> retrieveMethodStatisticsGroupedByMethodName(SearchFilter searchFilter) {
		return collectorDao.retrieveMethodStatisticsGroupedByMethodName(searchFilter);
			
	}
	
	

}
