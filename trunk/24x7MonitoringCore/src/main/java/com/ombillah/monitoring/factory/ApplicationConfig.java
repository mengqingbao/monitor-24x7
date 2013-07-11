package com.ombillah.monitoring.factory;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;
import com.google.inject.name.Names;
import com.ombillah.monitoring.dao.CollectorDAO;
import com.ombillah.monitoring.dao.impl.CollectorDAOImpl;
import com.ombillah.monitoring.domain.CollectedData;
import com.ombillah.monitoring.jobs.MethodExecutionTimeCollector;
import com.ombillah.monitoring.jobs.SQLQueryExecutionTimeCollector;
import com.ombillah.monitoring.service.CollectorService;
import com.ombillah.monitoring.service.impl.CollectorServiceImpl;

public class ApplicationConfig extends AbstractModule {

	protected void configure() {
		bind(CollectorDAO.class).to(CollectorDAOImpl.class).in(Scopes.SINGLETON);
		bind(MethodExecutionTimeCollector.class).in(Scopes.SINGLETON);
		bind(SQLQueryExecutionTimeCollector.class).in(Scopes.SINGLETON);
		bind(CollectedData.class).annotatedWith(Names.named("MethodCollector")).to(CollectedData.class).in(Scopes.SINGLETON );
		bind(CollectedData.class).annotatedWith(Names.named("SQLCollector")).to(CollectedData.class).in(Scopes.SINGLETON );
		bind(CollectorService.class).to(CollectorServiceImpl.class).in(Scopes.SINGLETON);
	}

}
