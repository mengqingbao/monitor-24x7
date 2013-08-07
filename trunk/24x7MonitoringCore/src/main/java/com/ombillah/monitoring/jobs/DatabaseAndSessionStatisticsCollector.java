package com.ombillah.monitoring.jobs;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import com.ombillah.monitoring.domain.DbConnectionTracker;
import com.ombillah.monitoring.domain.MonitoredItemTracer;
import com.ombillah.monitoring.domain.SessionTracker;
import com.ombillah.monitoring.service.CollectorService;

public class DatabaseAndSessionStatisticsCollector implements Runnable {
	
	@Inject
	private CollectorService collectorService;

	@Inject
	private SessionTracker sessionTracker;
	
	@Inject
	private DbConnectionTracker connectionTracker;
	
	public void run() {
		
		try {
			Integer connectionCount = connectionTracker.getActiveConnectionCountAndClearClosedSessions();
			Integer sessionCount = sessionTracker.getActiveSessionCountAndClearExpiredSessions();
	
	        MonitoredItemTracer activeConnectionCount = new MonitoredItemTracer("ActiveConnectionCount", 
	        		"ACTIVE_CONNECTION", connectionCount, connectionCount, connectionCount, 1, new Date());
	        
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
