package com.ombillah.monitoring.bootstrap;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.persist.PersistService;
import com.google.inject.persist.jpa.JpaPersistModule;
import com.ombillah.monitoring.factory.ApplicationConfig;

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
		return injector;
	}

}
