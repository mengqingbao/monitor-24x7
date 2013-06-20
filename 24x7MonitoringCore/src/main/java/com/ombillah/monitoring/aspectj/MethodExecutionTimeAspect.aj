
package com.ombillah.monitoring.aspectj;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;


import com.ombillah.monitoring.domain.CollectedData;
import com.ombillah.monitoring.factory.ApplicationFactory;
import com.ombillah.monitoring.jobs.CollectorQuartzJob;

public aspect MethodExecutionTimeAspect {
	
	private CollectedData collectedData;
	 
	public MethodExecutionTimeAspect() throws SchedulerException {
		collectedData = ApplicationFactory.collectedData();
		scheduleCollector();
	}
	
	private void scheduleCollector() throws SchedulerException {
		JobDetail job = JobBuilder.newJob(CollectorQuartzJob.class)
				.withIdentity("collectorJob", "group1").build();
		
		Calendar cal = Calendar.getInstance(); 
	    cal.setTime(new Date());
	    cal.add(Calendar.SECOND, 5); // adds 30 secs
	    
		Trigger trigger = TriggerBuilder
				.newTrigger()
				.withIdentity("collectorTrigger", "group1")
				.startAt(cal.getTime())
				.withSchedule(
						SimpleScheduleBuilder.simpleSchedule()
						.withIntervalInSeconds(10).repeatForever())
				.build();

		Scheduler scheduler = new StdSchedulerFactory().getScheduler();
    	scheduler.start();
    	scheduler.scheduleJob(job, trigger);
		
	}

	pointcut publicOperation() : execution(public * com.ombillah.ecom4j..*(..)) || execution(public * java.util.concurrent.CopyOnWriteArrayList..*(..));
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
