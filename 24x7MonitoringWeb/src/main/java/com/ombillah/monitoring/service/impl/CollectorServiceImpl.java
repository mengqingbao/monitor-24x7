package com.ombillah.monitoring.service.impl;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ombillah.monitoring.dao.CollectorDAO;
import com.ombillah.monitoring.domain.MethodSignature;
import com.ombillah.monitoring.domain.MethodTracer;
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
	
	public List<MethodTracer> retrieveMethodStatistics(
			List<String> methodSignatures, 
			Long minExecTime, Long maxExecTime,
			Date minDate, Date maxDate) {
		
		return collectorDao.retrieveMethodStatistics(methodSignatures,
				minExecTime, maxExecTime,
				minDate, maxDate);
			
	}
	
	public List<MethodTracer> retrieveMethodStatisticsGroupedByMethodName(
			List<String> methodSignatures, 
			Long minExecTime, Long maxExecTime,
			Date minDate, Date maxDate) {
		
		return collectorDao.retrieveMethodStatisticsGroupedByMethodName(methodSignatures,
				minExecTime, maxExecTime,
				minDate, maxDate);
			
	}
	
	

}
