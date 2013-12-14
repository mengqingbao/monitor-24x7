package com.ombillah.monitoring.factory;


import com.google.inject.AbstractModule;
import com.google.inject.Scopes;
import com.ombillah.monitoring.domain.CollectedData;
import com.ombillah.monitoring.domain.DbConnectionTracker;
import com.ombillah.monitoring.domain.SessionTracker;
import com.ombillah.monitoring.jobs.PerformanceMetricsCollector;
import com.ombillah.monitoring.jobs.PerformanceMetricsPersisterJob;
import com.ombillah.monitoring.domain.SigarCpuLoadMonitor;

public class ApplicationConfig extends AbstractModule {

	protected void configure() {
		bind(CollectedData.class).in(Scopes.SINGLETON );
		bind(SessionTracker.class).in(Scopes.SINGLETON);
		bind(DbConnectionTracker.class).in(Scopes.SINGLETON);
		bind(SigarCpuLoadMonitor.class).in(Scopes.SINGLETON);
		bind(PerformanceMetricsCollector.class).in(Scopes.SINGLETON);
		bind(PerformanceMetricsPersisterJob.class).in(Scopes.SINGLETON);
	}

}
