package com.ombillah.monitoring.bootstrap;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.ombillah.monitoring.factory.ApplicationConfig;
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
		
		String configFile = System.getProperty("monitoring.configLocation");
		Properties properties = new Properties();
		try {
			properties.load(new FileInputStream(configFile));
		} catch (IOException e) {
			e.printStackTrace();
		}
		injector = Guice.createInjector(new ApplicationConfig());
		setScheduledJobs();
		return injector;
	}
	
	private static void setScheduledJobs() {
		setPerformanceMetricsCollectorJob();
		setPerformanceMetricsPersisterJob();
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
	
}
