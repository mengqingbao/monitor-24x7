package com.ombillah.monitoring.service;

import java.util.ArrayList;
import java.util.List;

import com.ombillah.monitoring.domain.MethodSignature;
import com.ombillah.monitoring.domain.MethodTracer;
import com.ombillah.monitoring.domain.QueryTracer;
import com.ombillah.monitoring.domain.SqlQuery;

/**
 * Service class for collecting performance data.
 * @author Oussama M Billah
 *
 */
public interface CollectorService {

	public List<MethodSignature> retrieveMethodSignatures();
	
	public void saveMethodSignatures(List<MethodSignature> arrayList);
	
	public void saveMethodTracingStatistics(List<MethodTracer> methodTracers);

	public List<SqlQuery> retrieveSqlQueries();

	public void saveQueryTracingStatistics(List<QueryTracer> list);

	public void saveSqlQueries(List<SqlQuery> arrayList);
}
