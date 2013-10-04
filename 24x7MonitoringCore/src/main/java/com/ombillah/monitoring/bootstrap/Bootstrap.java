package com.ombillah.monitoring.bootstrap;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.persist.PersistService;
import com.google.inject.persist.jpa.JpaPersistModule;
import com.ombillah.monitoring.factory.ApplicationConfig;
import com.ombillah.monitoring.jobs.AlertManagerJob;
import com.ombillah.monitoring.jobs.DatabaseAndSessionStatisticsCollector;
import com.ombillah.monitoring.jobs.MemoryUsageCollector;
import com.ombillah.monitoring.jobs.MethodAndHttpRequestExecutionTimeCollector;
import com.ombillah.monitoring.jobs.SQLQueryExecutionTimeCollector;

/**
 * Bootstrap class for the application initialization
 * @author Oussama M Billah
 *
 */
public class Bootstrap {
	
	private static Injector injector;
	/**
	 * @param args
	 */
	public static Injector init() {
		if(injector != null) {
			return injector;
		}
		injector = Guice.createInjector(new ApplicationConfig(), new JpaPersistModule("24x7monitoring"));
		PersistService persistService = injector.getInstance(PersistService.class);
		persistService.start();
		setScheduledJobs();
		return injector;
	}
	
	private static void setScheduledJobs() {
		setMethodExecutionTimeScheduledJob();
		setSQLQueryScheduledJob();
		setDatabaseMonitorScheduledJob();
		setMemoryAndCPUScheduledJob();
		setAlertManagerScheduledJob();
	}

	private static void setDatabaseMonitorScheduledJob() {
		ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(10);
		Runnable collectorJob = injector.getInstance(DatabaseAndSessionStatisticsCollector.class);
		scheduler.scheduleAtFixedRate(collectorJob, 1, 1, TimeUnit.SECONDS);
		
	}

	private static void setSQLQueryScheduledJob() {
		ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(10);
		Runnable collectorJob = injector.getInstance(SQLQueryExecutionTimeCollector.class);
		scheduler.scheduleAtFixedRate(collectorJob, 30, 30, TimeUnit.SECONDS);
	}

	private static void setMethodExecutionTimeScheduledJob() {
		ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(10);
		Runnable collectorJob = injector.getInstance(MethodAndHttpRequestExecutionTimeCollector.class);
		scheduler.scheduleAtFixedRate(collectorJob, 30, 30, TimeUnit.SECONDS);
	}
	
	private static void setMemoryAndCPUScheduledJob() {
		ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(10);
		Runnable collectorJob = injector.getInstance(MemoryUsageCollector.class);
		scheduler.scheduleAtFixedRate(collectorJob, 1, 1, TimeUnit.SECONDS);
	}
	
	private static void setAlertManagerScheduledJob() {
		ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(10);
		Runnable collectorJob = injector.getInstance(AlertManagerJob.class);
		scheduler.scheduleAtFixedRate(collectorJob, 10, 10, TimeUnit.SECONDS);
		
	}


}
