package com.ombillah.monitoring.factory;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;
import com.ombillah.monitoring.dao.CollectorDAO;
import com.ombillah.monitoring.dao.impl.CollectorDAOImpl;
import com.ombillah.monitoring.domain.CollectedData;
import com.ombillah.monitoring.jobs.MethodExecutionTimeCollector;
import com.ombillah.monitoring.service.CollectorService;
import com.ombillah.monitoring.service.impl.CollectorServiceImpl;

public class ApplicationConfig extends AbstractModule {

	protected void configure() {
		bind(CollectedData.class).in(Scopes.SINGLETON );
		bind(CollectorDAO.class).to(CollectorDAOImpl.class).in(Scopes.SINGLETON);
		bind(CollectorService.class).to(CollectorServiceImpl.class).in(Scopes.SINGLETON);
		bind(MethodExecutionTimeCollector.class).in(Scopes.SINGLETON);
	}

}
