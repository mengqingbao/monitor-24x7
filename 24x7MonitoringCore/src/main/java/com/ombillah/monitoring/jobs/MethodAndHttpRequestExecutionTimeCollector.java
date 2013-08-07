package com.ombillah.monitoring.jobs;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.inject.Inject;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.math.stat.descriptive.SummaryStatistics;

import com.google.inject.name.Named;
import com.ombillah.monitoring.domain.CollectedData;
import com.ombillah.monitoring.domain.HttpRequestUrl;
import com.ombillah.monitoring.domain.MethodSignature;
import com.ombillah.monitoring.domain.MonitoredItemTracer;
import com.ombillah.monitoring.service.CollectorService;

public class MethodAndHttpRequestExecutionTimeCollector implements Runnable {
	
	@Inject
	@Named("MethodAndHttpRequestCollector")
	private CollectedData collectedData;
	
	@Inject
	private CollectorService collectorService;
	
	public void run() {
		
		try {
			Map<String, List<Long>> tracers = collectedData.getTracer();
			
			List<MonitoredItemTracer> list = new CopyOnWriteArrayList<MonitoredItemTracer>();
			Set<MethodSignature> methodSignatures = new HashSet<MethodSignature>();
			Set<HttpRequestUrl> httpRequests = new HashSet<HttpRequestUrl>();

			Date timestamp = new Date();

			for(String itemName : tracers.keySet()) {

				SummaryStatistics stats = new SummaryStatistics();
				List<Long> execTimes = tracers.get(itemName);
				for(int i = 0; i < execTimes.size(); i++) {
					Long execTime = execTimes.get(i);
					stats.addValue(execTime);
				}
				
				double average = stats.getMean();
				double max = stats.getMax();
				double min = stats.getMin();
				double count = execTimes.size();
				
				String type;
				if(StringUtils.contains(itemName, "|")) {
					type = "HTTP_REQUEST";
					httpRequests.add(new HttpRequestUrl(itemName));
				} else {
					type = "java";
					methodSignatures.add(new MethodSignature(itemName));
				}
				
				MonitoredItemTracer tracer = new MonitoredItemTracer(itemName, type, average, max, min, count, timestamp);
				list.add(tracer);
				
				
			}
			if(tracers != null && !tracers.isEmpty()) {
				List<MethodSignature> currentList = collectorService.retrieveMethodSignatures();
				methodSignatures.addAll(currentList);
				collectorService.saveMethodSignatures(new ArrayList<MethodSignature>(methodSignatures));

				List<HttpRequestUrl> currentRequestList = collectorService.retrieveHttpRequestUrls();
				httpRequests.addAll(currentRequestList);
				collectorService.saveHttpRequestUrls(new ArrayList<HttpRequestUrl>(httpRequests));

				collectorService.saveMonitoredItemTracingStatistics(list);

				System.out.println("inserted " + list.size() + " items into method Tracer Table at " + timestamp);
		    	tracers.clear();
			}
				    	
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		
	}

}
