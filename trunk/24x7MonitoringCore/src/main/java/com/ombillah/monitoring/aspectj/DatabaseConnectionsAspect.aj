package com.ombillah.monitoring.aspectj;

import java.sql.Connection;

import com.google.inject.Injector;
import com.ombillah.monitoring.bootstrap.Bootstrap;
import com.ombillah.monitoring.domain.DbConnectionTracker;

public aspect DatabaseConnectionsAspect {

	private DbConnectionTracker dbConnectionTracker;
	
	public DatabaseConnectionsAspect() {
		bootstrap();		
	}

	private void bootstrap() {
		try {
			Injector injector = Bootstrap.init();
			dbConnectionTracker = injector.getInstance(DbConnectionTracker.class);
		} catch (Throwable ex) {
			ex.printStackTrace();
		}
	}
	
	  pointcut openConnection() : execution(public * javax.sql.DataSource.getConnection(..)) ;
	
	  after() returning(Connection connection)  : openConnection() {
		  dbConnectionTracker.addConnection(connection);
	  }

}
