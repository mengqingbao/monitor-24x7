package com.ombillah.monitoring.service;

import java.util.List;

import com.ombillah.monitoring.domain.MethodSignature;
import com.ombillah.monitoring.domain.MethodTracer;
import com.ombillah.monitoring.domain.SearchFilter;

/**
 * Service class for collecting performance data.
 * @author Oussama M Billah
 *
 */
public interface CollectorService {

	public List<MethodSignature> retrieveMethodSignatures();

	public List<MethodTracer> retrieveMethodStatistics(SearchFilter searchFilter);
	
	public List<MethodTracer> retrieveMethodStatisticsGroupedByMethodName(SearchFilter searchFilter);
}
