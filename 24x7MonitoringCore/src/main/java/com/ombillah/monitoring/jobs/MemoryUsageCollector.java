package com.ombillah.monitoring.jobs;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import com.ombillah.monitoring.domain.MonitoredItemTracer;
import com.ombillah.monitoring.service.CollectorService;

public class MemoryUsageCollector implements Runnable {
	
	@Inject
	private CollectorService collectorService;
	
	public void run() {
		
		try {
			Long freeMemory = Runtime.getRuntime().freeMemory() / 1024L;
	        Long totalMemory  = Runtime.getRuntime().totalMemory() / 1024L;
	        Long usedMemory = totalMemory - freeMemory;
	        
	        MonitoredItemTracer totalMem = new MonitoredItemTracer("Total Memory", 
	        		"MEMORY", totalMemory, totalMemory, totalMemory, 1, new Date());
	        
	        MonitoredItemTracer usedMem = new MonitoredItemTracer("Used Memory", 
	    	        "MEMORY", usedMemory, usedMemory, usedMemory, 1, new Date());
	
	        List<MonitoredItemTracer> list = new ArrayList<MonitoredItemTracer>();
	        list.add(totalMem);
	        list.add(usedMem);
	        
	        collectorService.saveMonitoredItemTracingStatistics(list);
		}
		catch (Throwable ex) {
			ex.printStackTrace();
		}
        
	}

}
