package com.ombillah.monitoring.jobs;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import javax.inject.Inject;

import com.google.inject.name.Named;
import com.ombillah.monitoring.domain.MonitoredItemTracer;
import com.ombillah.monitoring.domain.SessionTracker;
import com.ombillah.monitoring.service.CollectorService;

public class DatabaseAndSessionStatisticsCollector implements Runnable {
	
	
	@Inject
	private CollectorService collectorService;
	
	@Inject
	@Named("ActiveConnectionCount")
	private AtomicLong activeConnectionCount;
	
	@Inject
	private SessionTracker sessionTracker;
	
	public void run() {
		
		try {
			Long activeCount = activeConnectionCount.get();
			Integer sessionCount = sessionTracker.getActiveSessionCountAndClearExpiredSessions();
			
			if(activeCount < 0) {
				activeCount = 0L;
			}
			
	        MonitoredItemTracer activeConnectionCount = new MonitoredItemTracer("ActiveConnectionCount", 
	        		"ACTIVE_CONNECTION", activeCount, activeCount, activeCount, 1, new Date());
	        
	        MonitoredItemTracer activeSessionCount = new MonitoredItemTracer("HttpSessionCount", 
	        		"ACTIVE_SESSION", sessionCount, sessionCount, sessionCount, 1, new Date());
	        
	        List<MonitoredItemTracer> list = new ArrayList<MonitoredItemTracer>();
	        list.add(activeConnectionCount);
	        list.add(activeSessionCount);
	        
	        collectorService.saveMonitoredItemTracingStatistics(list);
		}
		catch (Throwable ex) {
			ex.printStackTrace();
		}
        
	}
}
