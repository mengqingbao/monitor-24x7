package com.ombillah.monitoring.storage.ehcache;

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
	
	public void writeMethodTracersToCache(List<MethodTracer> methodTracers);
	
	public void writeMethodSignaturesToCache(Set<String> methodSignatures);
	
	public Set<String> retrieveMethodSignatures();
}
