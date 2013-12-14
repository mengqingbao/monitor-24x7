package com.ombillah.monitoring.aspectj;

import java.util.Date;

import org.apache.commons.lang.exception.ExceptionUtils;

import com.google.inject.Injector;
import com.ombillah.monitoring.bootstrap.Bootstrap;
import com.ombillah.monitoring.domain.CollectedData;
import com.ombillah.monitoring.domain.ExceptionLogger;

/**
 * Aspect to Capture all exceptions and log them to datasource.
 * @author Oussama M Billah.
 * 
 */
public abstract aspect AbstractExceptionHandlerAspect {

	private CollectedData collectedData;

	public AbstractExceptionHandlerAspect() {
		bootstrap();
	}

	private void bootstrap() {
		try {
			Injector injector = Bootstrap.init();
			collectedData = injector.getInstance(CollectedData.class);
		} catch (Throwable ex) {
			ex.printStackTrace();
		}
	}
	
	protected pointcut ExceptionMonitoringTarget();   
    protected pointcut ExceptionPointcut() : ExceptionMonitoringTarget() && execution(* *(..));
	
	
	after() throwing(Throwable ex) : ExceptionPointcut() {
		String message = ExceptionUtils.getMessage(ex);
		String exceptionMessage = message + " at " + ex.getStackTrace()[0];
		String stackTrace = ExceptionUtils.getStackTrace(ex);
		
		ExceptionLogger logger = new ExceptionLogger();
		logger.setExceptionMessage(exceptionMessage);
		logger.setStacktrace(stackTrace);
		logger.setCreationDate(new Date());
		
		collectedData.addExceptionLogger(logger);

	}
}
