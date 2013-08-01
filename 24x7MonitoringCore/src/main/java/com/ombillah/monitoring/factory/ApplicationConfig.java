package com.ombillah.monitoring.factory;

import java.util.concurrent.atomic.AtomicLong;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;
import com.google.inject.name.Names;
import com.ombillah.monitoring.dao.CollectorDAO;
import com.ombillah.monitoring.dao.impl.CollectorDAOImpl;
import com.ombillah.monitoring.domain.CollectedData;
import com.ombillah.monitoring.domain.SessionTracker;
import com.ombillah.monitoring.jobs.DatabaseAndSessionStatisticsCollector;
import com.ombillah.monitoring.jobs.MemoryUsageCollector;
import com.ombillah.monitoring.jobs.MethodExecutionTimeCollector;
import com.ombillah.monitoring.jobs.SQLQueryExecutionTimeCollector;
import com.ombillah.monitoring.service.CollectorService;
import com.ombillah.monitoring.service.impl.CollectorServiceImpl;

public class ApplicationConfig extends AbstractModule {

	protected void configure() {
		bind(MethodExecutionTimeCollector.class).in(Scopes.SINGLETON);
		bind(SQLQueryExecutionTimeCollector.class).in(Scopes.SINGLETON);
		bind(MemoryUsageCollector.class).in(Scopes.SINGLETON);
		bind(SessionTracker.class).in(Scopes.SINGLETON);
		bind(AtomicLong.class).annotatedWith(Names.named("ActiveConnectionCount")).to(AtomicLong.class).in(Scopes.SINGLETON );
		bind(AtomicLong.class).annotatedWith(Names.named("HttpSessionCount")).to(AtomicLong.class).in(Scopes.SINGLETON );
		bind(DatabaseAndSessionStatisticsCollector.class).in(Scopes.SINGLETON);
		bind(CollectedData.class).annotatedWith(Names.named("MethodCollector")).to(CollectedData.class).in(Scopes.SINGLETON );
		bind(CollectedData.class).annotatedWith(Names.named("SQLCollector")).to(CollectedData.class).in(Scopes.SINGLETON );
		bind(CollectorDAO.class).to(CollectorDAOImpl.class).in(Scopes.SINGLETON);
		bind(CollectorService.class).to(CollectorServiceImpl.class).in(Scopes.SINGLETON);
	}

}
