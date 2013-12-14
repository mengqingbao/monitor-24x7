package com.ombillah.monitoring.aspectj;

/**
 * See AbstractExceptionHandlerAspect for detail .
 * using abstract aspect to allow reading pointcut from aop.xml
 * @author Oussama M Billah.
 * 
 */
public aspect ExceptionHandlerAspect extends AbstractExceptionHandlerAspect {

    protected pointcut ExceptionPointcut() : ExceptionMonitoringTarget() && execution(* *(..));
	
}
