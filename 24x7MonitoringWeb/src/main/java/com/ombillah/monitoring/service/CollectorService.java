package com.ombillah.monitoring.service;

import java.util.Date;
import java.util.List;

import com.ombillah.monitoring.domain.MethodSignature;
import com.ombillah.monitoring.domain.MethodTracer;

/**
 * Service class for collecting performance data.
 * @author Oussama M Billah
 *
 */
public interface CollectorService {

	public List<MethodSignature> retrieveMethodSignatures();

	public List<MethodTracer> retrieveMethodStatistics(
			List<String> methodSignatures, 
			Long minExecTime, Long maxExecTime,
			Date minDate, Date maxDate);
			
	public List<MethodTracer> retrieveMethodStatisticsGroupedByMethodName(
			List<String> methodSignature,
			Long minExecTime, 
			Long maxExecTime,
			Date minDate, Date maxDate);
}
