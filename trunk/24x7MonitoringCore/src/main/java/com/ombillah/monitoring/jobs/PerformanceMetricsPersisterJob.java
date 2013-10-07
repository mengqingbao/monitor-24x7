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

import com.ombillah.monitoring.domain.CollectedData;
import com.ombillah.monitoring.domain.HttpRequestUrl;
import com.ombillah.monitoring.domain.MethodSignature;
import com.ombillah.monitoring.domain.MonitoredItemTracer;
import com.ombillah.monitoring.domain.SqlQuery;
import com.ombillah.monitoring.service.CollectorService;

public class PerformanceMetricsPersisterJob implements Runnable {
	
	@Inject
	private CollectorService collectorService;
	
	@Inject
	private CollectedData performanceMetrics;
	
	public void run() {
		
		try {

			Map<String, List<Long>> tracers = performanceMetrics.getTracer();
			List<MonitoredItemTracer> list = new CopyOnWriteArrayList<MonitoredItemTracer>();
			Set<MethodSignature> methodSignatures = new HashSet<MethodSignature>();
			Set<HttpRequestUrl> httpRequests = new HashSet<HttpRequestUrl>();
			Set<SqlQuery> sqlQueries = new HashSet<SqlQuery>();

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
				
				String[] nameTypeArray = itemName.split("\\|\\|");
				String type = nameTypeArray[0];
				String monitoredItemName = nameTypeArray[1];

				if(StringUtils.equals(type, "HTTP_REQUEST")) {
					httpRequests.add(new HttpRequestUrl(monitoredItemName));
				} 
				else if(StringUtils.equals(type, "JAVA")) {
					methodSignatures.add(new MethodSignature(monitoredItemName));
				}
				else if(StringUtils.equals(type, "SQL")) {
					sqlQueries.add(new SqlQuery(monitoredItemName));
				}
				
				MonitoredItemTracer tracer = new MonitoredItemTracer(monitoredItemName, type, average, max, min, count, timestamp);
				list.add(tracer);
			}
			if(tracers != null && !tracers.isEmpty()) {
				List<SqlQuery> sqlQueriesFromDB = collectorService.retrieveSqlQueries();
				sqlQueries.addAll(sqlQueriesFromDB);
				collectorService.saveSqlQueries(new ArrayList<SqlQuery>(sqlQueries));
				
				List<MethodSignature> methodSignaturesFromDB = collectorService.retrieveMethodSignatures();
				methodSignatures.addAll(methodSignaturesFromDB);
				collectorService.saveMethodSignatures(new ArrayList<MethodSignature>(methodSignatures));

				List<HttpRequestUrl> httpRequestsFromDB = collectorService.retrieveHttpRequestUrls();
				httpRequests.addAll(httpRequestsFromDB);
				collectorService.saveHttpRequestUrls(new ArrayList<HttpRequestUrl>(httpRequests));
				
				collectorService.saveMonitoredItemTracingStatistics(list);
				
				System.out.println("inserted " + list.size() + " items into Monitoring table at " + timestamp);
		    	tracers.clear();
			}
		}
		catch (Throwable ex) {
			ex.printStackTrace();
		}
		
	}

}
