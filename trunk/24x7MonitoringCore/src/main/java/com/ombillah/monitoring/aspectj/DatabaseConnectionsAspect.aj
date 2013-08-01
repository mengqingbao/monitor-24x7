package com.ombillah.monitoring.aspectj;

import java.util.concurrent.atomic.AtomicLong;

import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.name.Names;
import com.ombillah.monitoring.bootstrap.Bootstrap;

public aspect DatabaseConnectionsAspect {

	private AtomicLong connectionCount;
	
	public DatabaseConnectionsAspect() {
		bootstrap();		
	}

	private void bootstrap() {
		try {
			Injector injector = Bootstrap.init();
			connectionCount = injector.getInstance(Key.get(AtomicLong.class, Names.named("ActiveConnectionCount")));
			
		} catch (Throwable ex) {
			ex.printStackTrace();
		}
	}
	
	  pointcut openConnection() : execution(public * javax.sql.DataSource.getConnection(..)) ;
	
	  after() : openConnection() {
		  connectionCount.incrementAndGet();
	  }
	  
	  pointcut closeConnection() : execution(public * java.sql.Connection.close()) ;
		
	  after() : closeConnection() {
		  connectionCount.decrementAndGet();
	  }
}
