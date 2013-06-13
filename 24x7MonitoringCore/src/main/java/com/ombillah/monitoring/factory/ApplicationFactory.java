package com.ombillah.monitoring.factory;

import com.ombillah.monitoring.domain.CollectedData;
import com.ombillah.monitoring.storage.ehcache.EhcacheManager;
import com.ombillah.monitoring.storage.ehcache.impl.EhcacheManagerImpl;

public class ApplicationFactory {
	
	public static EhcacheManager ehCacheManager() {
		return EhcacheManagerImpl.getInstance();
	}
	
	public static CollectedData collectedData() {
		return CollectedData.getInstance();
	}
}
