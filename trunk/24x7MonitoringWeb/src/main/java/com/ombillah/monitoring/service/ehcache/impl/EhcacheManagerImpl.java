package com.ombillah.monitoring.service.ehcache.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import net.sf.ehcache.search.Attribute;
import net.sf.ehcache.search.Query;
import net.sf.ehcache.search.Result;
import net.sf.ehcache.search.Results;
import net.sf.ehcache.search.expression.Criteria;

import javax.annotation.Resource;
import com.ombillah.monitoring.domain.MethodTracer;
import com.ombillah.monitoring.service.ehcache.EhcacheManager;

/**
 * CacheManager for ehCache operations.
 * @author Oussama M Billah
 *
 */
@Service("ehcacheManager")
public class EhcacheManagerImpl implements EhcacheManager {
	
	@Resource(name="cacheManager")
	private CacheManager cacheManager;
	private static final String METHOD_SIGNATURE_CACHE = "methodSignaturesCache";
	private static final String METHOD_TRACER_CACHE = "methodTracerCache";

	
	public Cache getCacheInstance(String cacheName) {
        return cacheManager.getCache(cacheName);
    }
	
	public List<MethodTracer> retrieveMethodStatistics(String methodSignature,
			List<String> searchedItems,
			Long minExecutionTime,
			Long maxExecutionTime,
			Date startDate,
			Date endDate) {
		
		Cache cache = this.getCacheInstance(METHOD_TRACER_CACHE);
		Attribute<String> methodName = cache.getSearchAttribute("methodName");
		Attribute<Long> executionTime = cache.getSearchAttribute("average");
		Attribute<Date> creationDate = cache.getSearchAttribute("creationDate");
		
		Query query = cache.createQuery();
		if(!CollectionUtils.isEmpty(searchedItems)) {
			Criteria criteria = methodName.ilike(methodSignature + "*");
			for(String item : searchedItems) {
				criteria.or(methodName.ilike(item + "*"));
				query.addCriteria(criteria);
			}
		//	query.addCriteria(criteria);
		}
		else {
			query.addCriteria(methodName.ilike(methodSignature + "*"));
		}
		
		query.addCriteria(creationDate.between(startDate, endDate));

		if(minExecutionTime != null) {
			query.addCriteria(executionTime.ge(minExecutionTime));
		}
		
		if(maxExecutionTime != null) {
			query.addCriteria(executionTime.le(maxExecutionTime));
		}

		Results results = query
				.includeKeys()
				.execute();
		
		List<MethodTracer> methodTracers = new ArrayList<MethodTracer>();
		
		for(Result result : results.all()) {
			if(result.getKey() != null) {
				Element element = cache.get(result.getKey());
				methodTracers.add((MethodTracer) element.getObjectValue());
			}
		}
			
		return methodTracers;
	}

	@SuppressWarnings("unchecked")
	public Set<String> retrieveMethodSignatures() {		
		Cache cache = this.getCacheInstance(METHOD_SIGNATURE_CACHE);
		Element classNames = cache.get("MethodSignatures");
		if(classNames != null) {
			return (Set<String>) classNames.getObjectValue();
		}
		return new HashSet<String>();
	}


	
	
}
