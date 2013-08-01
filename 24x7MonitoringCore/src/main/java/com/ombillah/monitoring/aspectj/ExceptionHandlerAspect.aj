package com.ombillah.monitoring.aspectj;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang.exception.ExceptionUtils;

import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;
import com.ombillah.monitoring.bootstrap.Bootstrap;
import com.ombillah.monitoring.domain.ExceptionLogger;
import com.ombillah.monitoring.service.CollectorService;

/**
 * Aspect to Capture all exceptions and log them to datasource.
 * @author Oussama M Billah.
 * 
 */
public aspect ExceptionHandlerAspect {

	private CollectorService collectorService;

	public ExceptionHandlerAspect() {
		bootstrap();
	}

	private void bootstrap() {
		try {
			Injector injector = Bootstrap.init();
			collectorService = injector.getInstance(CollectorService.class);
		} catch (Throwable ex) {
			ex.printStackTrace();
		}
	}

	after() throwing(Throwable ex) : execution(public * com.ombillah.ecom4j..*(..)) {
		String message = ExceptionUtils.getMessage(ex);
		String exceptionMessage = message + " at " + ex.getStackTrace()[0];
		String stackTrace = ExceptionUtils.getStackTrace(ex);
		
		ExceptionLogger logger = new ExceptionLogger();
		logger.setExceptionMessage(exceptionMessage);
		logger.setStacktrace(stackTrace);
		logger.setCreationDate(new Date());
		
		collectorService.saveException(logger);

	}
}
