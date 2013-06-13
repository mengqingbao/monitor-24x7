package com.ombillah.monitoring.service.ehcache;

import java.util.Date;
import java.util.List;
import java.util.Set;

import com.ombillah.monitoring.domain.MethodTracer;


import net.sf.ehcache.Cache;


/**
 * CacheManager for ehCache operations.
 * @author Oussama M Billah
 *
 */
public interface EhcacheManager {
	
	public Cache getCacheInstance(String cacheName);
		
	public List<MethodTracer> retrieveMethodStatistics(
			String methodSignature,
			Long minExecutionTime,
			Long maxExecutionTime,
			Date startDate,
			Date endDate);
	
	public Set<String> retrieveMethodSignatures();

}
