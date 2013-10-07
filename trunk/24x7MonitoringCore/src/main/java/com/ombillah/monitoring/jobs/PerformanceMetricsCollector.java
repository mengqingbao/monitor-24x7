package com.ombillah.monitoring.jobs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import com.google.inject.name.Named;
import com.ombillah.monitoring.domain.CollectedData;
import com.ombillah.monitoring.domain.DbConnectionTracker;
import com.ombillah.monitoring.domain.MonitoredItemTracer;
import com.ombillah.monitoring.domain.SessionTracker;
import com.ombillah.monitoring.service.CollectorService;
import com.ombillah.monitoring.utils.Constants;

public class PerformanceMetricsCollector implements Runnable {
	
	@Inject
	private SessionTracker sessionTracker;
	
	@Inject
	private DbConnectionTracker connectionTracker;
	
	@Inject
	private CollectedData performanceMetrics;
	
	private static final Long BYTES_IN_MB = 1024L * 1000L;

	public void run() {
		
		try {
			Map<String, List<Long>> tracers = performanceMetrics.getTracer();
			collectActiveDBConnections(tracers);
			collectActiveSessions(tracers);
			collectMemoryUsage(tracers);
			
		}
		catch (Throwable ex) {
			ex.printStackTrace();
		}
        
	}

	private void collectActiveSessions(Map<String, List<Long>> tracers) {
		Long sessionCount = sessionTracker.getActiveSessionCountAndClearExpiredSessions();

		List<Long> sessionCounts = tracers.get("ACTIVE_SESSION||" + Constants.ACTIVE_SESSION);
		if(sessionCounts == null) {
			sessionCounts = Collections.synchronizedList(new ArrayList<Long>());
		}
		sessionCounts.add(sessionCount);
		tracers.put("ACTIVE_SESSION||" + Constants.ACTIVE_SESSION, sessionCounts);
	}

	private void collectActiveDBConnections(Map<String, List<Long>> tracers) {
		Long connectionCount = connectionTracker.getActiveConnectionCountAndClearClosedSessions();
		
		List<Long> connectionCounts = tracers.get("ACTIVE_CONNECTION||" + Constants.ACTIVE_CONNECTION);
		if(connectionCounts == null) {
			connectionCounts = Collections.synchronizedList(new ArrayList<Long>());
		}
		connectionCounts.add(connectionCount);
		tracers.put("ACTIVE_CONNECTION||" + Constants.ACTIVE_CONNECTION, connectionCounts);
	}
	
	private void collectMemoryUsage(Map<String, List<Long>> tracers) {
		
		Long freeMemory = Runtime.getRuntime().freeMemory() / BYTES_IN_MB;
        Long totalMemory  = Runtime.getRuntime().totalMemory() / BYTES_IN_MB;
        Long usedMemory = totalMemory - freeMemory;
        
		List<Long> totalMemoryList = tracers.get("MEMORY||" + Constants.TOTAL_MEMORY);
		if(totalMemoryList == null) {
			totalMemoryList = Collections.synchronizedList(new ArrayList<Long>());
		}
		totalMemoryList.add(totalMemory);
		tracers.put("MEMORY||" + Constants.TOTAL_MEMORY, totalMemoryList);
		
		List<Long> usedMemoryList = tracers.get("MEMORY||" + Constants.USED_MEMORY);
		if(usedMemoryList == null) {
			usedMemoryList = Collections.synchronizedList(new ArrayList<Long>());
		}
		usedMemoryList.add(usedMemory);
		tracers.put("MEMORY||" + Constants.USED_MEMORY, usedMemoryList);

		
	}
}
