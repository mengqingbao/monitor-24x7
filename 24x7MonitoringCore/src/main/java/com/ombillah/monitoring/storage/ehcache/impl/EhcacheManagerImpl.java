package com.ombillah.monitoring.storage.ehcache.impl;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

import com.ombillah.monitoring.domain.MethodTracer;
import com.ombillah.monitoring.storage.ehcache.EhcacheManager;

/**
 * CacheManager for ehCache operations.
 * @author Oussama M Billah
 *
 */
public class EhcacheManagerImpl implements EhcacheManager {
		
	private CacheManager cacheManager;
	private static EhcacheManager ehcacheManager;
	private static final String METHOD_SIGNATURE_CACHE = "methodSignaturesCache";
	private static final String METHOD_TRACER_CACHE = "methodTracerCache";

	private EhcacheManagerImpl() {
		URL url = getClass().getResource("/ehcache.xml");
    	cacheManager = CacheManager.create(url);
	}
	
	public static EhcacheManager getInstance() {
		if(ehcacheManager == null) {
			ehcacheManager = new EhcacheManagerImpl();
		}
		return ehcacheManager;
	}
	
	public Cache getCacheInstance(String cacheName) {
        return cacheManager.getCache(cacheName);
    }

	public void writeMethodTracersToCache(List<MethodTracer> methodTracers) {
		List<Element> elements = new ArrayList<Element>();
		Cache cache = this.getCacheInstance(METHOD_TRACER_CACHE);
		Integer cacheSize = cache.getKeys().size();
		Integer cacheKey = cacheSize + 1;
		for(MethodTracer tracer : methodTracers) {
			Element element = new Element(cacheKey.toString(), tracer);
			elements.add(element);
			cacheKey++;
		}
		cache.putAll(elements);
	}
	
	public void writeMethodSignaturesToCache(Set<String> methodSignatures) {
		Cache cache = this.getCacheInstance(METHOD_SIGNATURE_CACHE);
		Element element = new Element("MethodSignatures", methodSignatures);
		cache.put(element);
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