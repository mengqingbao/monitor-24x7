package com.ombillah.monitoring.dao;

import java.util.List;

import com.ombillah.monitoring.domain.MethodSignature;
import com.ombillah.monitoring.domain.MethodTracer;
import com.ombillah.monitoring.domain.QueryTracer;
import com.ombillah.monitoring.domain.SqlQuery;

/**
 * Data Access Object for collecting performance data.
 * @author Oussama M Billah
 *
 */
public interface CollectorDAO {
	
	public List<MethodSignature> retrieveMethodSignatures();
	
	public void saveMethodSignatures(List<MethodSignature> methodSignatures);
	
	public void saveMethodTracingStatistics(List<MethodTracer> methodTracers);

	public List<SqlQuery> retrieveSqlQueries();

	public void saveQueryTracingStatistics(List<QueryTracer> tracers);

	public void saveSqlQueries(List<SqlQuery> queries);
}
