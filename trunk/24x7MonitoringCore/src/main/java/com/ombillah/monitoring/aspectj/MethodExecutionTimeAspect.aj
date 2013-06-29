
package com.ombillah.monitoring.aspectj;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.StringUtils;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.ombillah.monitoring.bootstrap.Bootstrap;
import com.ombillah.monitoring.domain.CollectedData;
import com.ombillah.monitoring.jobs.MethodExecutionTimeCollector;

public aspect MethodExecutionTimeAspect {
	
	@Inject
	private CollectedData collectedData;
	
	public MethodExecutionTimeAspect() {
		Injector injector = Bootstrap.init();
		ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
		Runnable collectorJob = injector.getInstance(MethodExecutionTimeCollector.class);
		scheduler.scheduleAtFixedRate(collectorJob, 1, 1, TimeUnit.SECONDS);
		collectedData = injector.getInstance(CollectedData.class);

	}
	
	pointcut publicOperation() : execution(public * com.ombillah.ecom4j..*(..));
	Object around() : publicOperation() {

	    Long start = System.currentTimeMillis();
	    Object ret = proceed();
	    Long end = System.currentTimeMillis();

	    Long executionTime = (end-start);
	    String signature = thisJoinPointStaticPart.getSignature().getName();
	    String className = thisJoinPointStaticPart.getSignature().getDeclaringTypeName();
	    String methodName = className + "." + signature + "()";

	    // ignore spring proxies as the original call will also be intercepted.
	    if(!StringUtils.contains(className, "$Proxy")) {
	    	Map<String, List<Long>> tracers = collectedData.getMethodTracer();
	    	List<Long> execTimes = tracers.get(methodName);
	    	if(execTimes == null) {
	    		execTimes = Collections.synchronizedList(new ArrayList<Long>());
	    	}
	    	
	    	boolean added = execTimes.add(executionTime);
	    	if(!added) {
				System.out.println(methodName + " not added");
			}
	    	tracers.put(methodName, execTimes);
	    	collectedData.setMethodTracer(tracers);
	    }

	    return ret;
	  }
}
