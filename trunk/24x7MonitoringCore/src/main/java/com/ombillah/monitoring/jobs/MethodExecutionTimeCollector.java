package com.ombillah.monitoring.jobs;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.inject.Inject;

import org.apache.commons.math.stat.descriptive.SummaryStatistics;

import com.ombillah.monitoring.domain.CollectedData;
import com.ombillah.monitoring.domain.MethodSignature;
import com.ombillah.monitoring.domain.MethodTracer;
import com.ombillah.monitoring.service.CollectorService;

public class MethodExecutionTimeCollector implements Runnable {
	
	@Inject
	private CollectedData collectedData;
	
	@Inject
	private CollectorService collectorService;
	
	public void run() {
		
		try {
			Map<String, List<Long>> tracers = collectedData.getMethodTracer();
			
			List<MethodTracer> list = new CopyOnWriteArrayList<MethodTracer>();
			Set<MethodSignature> methodSignatures = new HashSet<MethodSignature>();
			Date timestamp = new Date();

			for(String methodName : tracers.keySet()) {

				SummaryStatistics stats = new SummaryStatistics();
				List<Long> execTimes = tracers.get(methodName);
				for(int i = 0; i < execTimes.size(); i++) {
					Long execTime = execTimes.get(i);
					stats.addValue(execTime);
				}
				
				double average = stats.getMean();
				double max = stats.getMax();
				double min = stats.getMin();
				double count = execTimes.size();
				
				MethodTracer tracer = new MethodTracer(methodName, average, max, min, count, timestamp);
				list.add(tracer);
				
				methodSignatures.add(new MethodSignature(methodName));
				
			}
			if(tracers != null && !tracers.isEmpty()) {
				List<MethodSignature> currentList = collectorService.retrieveMethodSignatures();
				methodSignatures.addAll(currentList);
				collectorService.saveMethodTracingStatistics(list);
				collectorService.saveMethodSignatures(new ArrayList<MethodSignature>(methodSignatures));
				System.out.println("inserted " + list.size() + " items into methodTracerCache at " + timestamp);
				//System.out.println("inserted " + methodSignatures.size() + " items into methodSignatures at " + timestamp );
		    	tracers.clear();
			}
				    	
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		
	}

}
