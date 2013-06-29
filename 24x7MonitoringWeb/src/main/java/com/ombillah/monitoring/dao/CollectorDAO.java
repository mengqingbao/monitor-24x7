package com.ombillah.monitoring.dao;

import java.util.Date;
import java.util.List;

import com.ombillah.monitoring.domain.MethodSignature;
import com.ombillah.monitoring.domain.MethodTracer;

/**
 * Data Access Object for collecting performance data.
 * @author Oussama M Billah
 *
 */
public interface CollectorDAO {
	
	public List<MethodSignature> retrieveMethodSignatures();
	
	public List<MethodTracer> retrieveMethodStatisticsGroupedByMethodName(
			List<String> methodSignatures, 
			Long minExecTime, Long maxExecTime,
			Date minDate, Date maxDate);

	public List<MethodTracer> retrieveMethodStatistics(
			List<String> methodSignatures, Long minExecTime, Long maxExecTime,
			Date minDate, Date maxDate);
}
