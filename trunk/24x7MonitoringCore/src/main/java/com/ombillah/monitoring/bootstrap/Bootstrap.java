package com.ombillah.monitoring.bootstrap;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.persist.PersistService;
import com.google.inject.persist.jpa.JpaPersistModule;
import com.ombillah.monitoring.factory.ApplicationConfig;
import com.ombillah.monitoring.jobs.AlertManagerJob;
import com.ombillah.monitoring.jobs.PerformanceMetricsCollector;
import com.ombillah.monitoring.jobs.PerformanceMetricsPersisterJob;

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
		
		JpaPersistModule jpaModule = new JpaPersistModule("24x7monitoring");
		String configFile = System.getProperty("monitoring.configLocation");
		Properties properties = new Properties();
		try {
			properties.load(new FileInputStream(configFile));
		} catch (IOException e) {
			e.printStackTrace();
		}
		JpaPersistModule module = jpaModule.properties(properties);
		injector = Guice.createInjector(new ApplicationConfig(), module);
		PersistService persistService = injector.getInstance(PersistService.class);
		persistService.start();
		setScheduledJobs();
		return injector;
	}
	
	private static void setScheduledJobs() {
		setPerformanceMetricsCollectorJob();
		setPerformanceMetricsPersisterJob();
		setAlertManagerScheduledJob();
	}

	private static void setPerformanceMetricsCollectorJob() {
		ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(10);
		Runnable collectorJob = injector.getInstance(PerformanceMetricsCollector.class);
		scheduler.scheduleAtFixedRate(collectorJob, 1, 1, TimeUnit.SECONDS);
		
	}

	private static void setPerformanceMetricsPersisterJob() {
		ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(10);
		Runnable collectorJob = injector.getInstance(PerformanceMetricsPersisterJob.class);
		scheduler.scheduleAtFixedRate(collectorJob, 30, 30, TimeUnit.SECONDS);
	}
	
	private static void setAlertManagerScheduledJob() {
		ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(10);
		Runnable collectorJob = injector.getInstance(AlertManagerJob.class);
		scheduler.scheduleAtFixedRate(collectorJob, 1, 10, TimeUnit.MINUTES);
		
	}


}
