package com.ombillah.monitoring.service.impl;

import java.util.List;

import javax.inject.Inject;

import com.ombillah.monitoring.dao.CollectorDAO;
import com.ombillah.monitoring.domain.MethodSignature;
import com.ombillah.monitoring.domain.MethodTracer;
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

	public void saveMethodTracingStatistics(List<MethodTracer> methodTracers) {
		collectorDao.saveMethodTracingStatistics(methodTracers);
	}

}
