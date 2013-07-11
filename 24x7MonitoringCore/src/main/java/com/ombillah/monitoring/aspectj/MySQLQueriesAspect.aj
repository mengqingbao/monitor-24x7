
package com.ombillah.monitoring.aspectj;

import com.google.inject.Injector;
import com.mysql.jdbc.StringUtils;
import com.ombillah.monitoring.domain.CollectedData;
import com.ombillah.monitoring.jobs.SQLQueryExecutionTimeCollector;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import com.google.inject.Key;
import com.google.inject.name.Names;
import com.ombillah.monitoring.bootstrap.Bootstrap;

public aspect MySQLQueriesAspect {
	
	private CollectedData collectedData;
	
	public MySQLQueriesAspect() {
		bootstrap();		
	}

	private void bootstrap() {
		try {
			Injector injector = Bootstrap.init();
			ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
			Runnable collectorJob = injector.getInstance(SQLQueryExecutionTimeCollector.class);
			scheduler.scheduleAtFixedRate(collectorJob, 30, 30, TimeUnit.SECONDS);
			collectedData = injector.getInstance(Key.get(CollectedData.class, Names.named("SQLCollector")));

		} catch (Throwable ex) {
			ex.printStackTrace();
		}
	}
	
	pointcut publicOperation() : execution(public * java.sql.Connection.prepareStatement(String)) ;
	Object around() : publicOperation() {

	    Long start = System.currentTimeMillis();
	    Object ret = proceed();
	    Long end = System.currentTimeMillis();
	  
	    if(collectedData == null) {
	    	bootstrap();
	    }
	    if(collectedData == null) {
	    	return ret;
	    }
	    
	    Long executionTime = (end-start);
	    
	    Object[] args = thisJoinPoint.getArgs();
	    String sqlQuery = args[0].toString();
	    // remove SQL Comments from query.
	    Pattern commentPattern = Pattern.compile("/\\*.*?\\*/", Pattern.DOTALL);
	    sqlQuery = commentPattern.matcher(sqlQuery).replaceAll("");
	    
	    boolean isSelect = StringUtils.startsWithIgnoreCase(sqlQuery, "SELECT");
	    boolean isInsert = StringUtils.startsWithIgnoreCase(sqlQuery, "INSERT");
	    boolean isUpdate = StringUtils.startsWithIgnoreCase(sqlQuery, "UPDATE");
	    boolean isDelete = StringUtils.startsWithIgnoreCase(sqlQuery, "DELETE");
	    
	    if(isSelect || isInsert || isUpdate || isDelete) {
	    	 Map<String, List<Long>> tracers = collectedData.getTracer();
	     	List<Long> execTimes = tracers.get(sqlQuery);
	     	if(execTimes == null) {
	     		execTimes = Collections.synchronizedList(new ArrayList<Long>());
	     	}
	     	execTimes.add(executionTime);
	     	tracers.put(sqlQuery, execTimes);
	     	collectedData.setTracer(tracers);
	    }
	   
	    return ret;
	  }
}
