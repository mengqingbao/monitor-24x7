package com.ombillah.monitoring.config;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;
import com.ombillah.monitoring.domain.CollectedData;
import com.ombillah.monitoring.storage.ehcache.EhcacheManager;
import com.ombillah.monitoring.storage.ehcache.impl.EhcacheManagerImpl;

public class ApplicationConfig extends AbstractModule {

	protected void configure() {
		bind(EhcacheManager.class).to(EhcacheManagerImpl.class).in(Scopes.SINGLETON);
		bind(CollectedData.class).in(Scopes.SINGLETON );
	}

}
