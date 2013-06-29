package com.ombillah.monitoring.service;

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
	
	public void saveMethodSignatures(List<MethodSignature> arrayList);
	
	public void saveMethodTracingStatistics(List<MethodTracer> methodTracers);
}
