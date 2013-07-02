package com.ombillah.monitoring.dao;

import java.util.List;

import com.ombillah.monitoring.domain.MethodSignature;
import com.ombillah.monitoring.domain.MethodTracer;
import com.ombillah.monitoring.domain.SearchFilter;

/**
 * Data Access Object for collecting performance data.
 * @author Oussama M Billah
 *
 */
public interface CollectorDAO {
	
	public List<MethodSignature> retrieveMethodSignatures();
	
	public List<MethodTracer> retrieveMethodStatisticsGroupedByMethodName(SearchFilter searchFilter);

	public List<MethodTracer> retrieveMethodStatistics(SearchFilter searchFilter);

}
