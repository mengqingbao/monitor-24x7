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

import com.google.inject.name.Named;
import com.ombillah.monitoring.domain.CollectedData;
import com.ombillah.monitoring.domain.MethodSignature;
import com.ombillah.monitoring.domain.MethodTracer;
import com.ombillah.monitoring.domain.QueryTracer;
import com.ombillah.monitoring.domain.SqlQuery;
import com.ombillah.monitoring.service.CollectorService;

public class SQLQueryExecutionTimeCollector implements Runnable {
	
	@Inject
	@Named("SQLCollector")
	private CollectedData collectedData;
	
	@Inject
	private CollectorService collectorService;
	
	public void run() {
		
		try {
			Map<String, List<Long>> tracers = collectedData.getTracer();
			
			List<QueryTracer> list = new CopyOnWriteArrayList<QueryTracer>();
			Set<SqlQuery> sqlQueries = new HashSet<SqlQuery>();
			Date timestamp = new Date();

			for(String query : tracers.keySet()) {

				SummaryStatistics stats = new SummaryStatistics();
				List<Long> execTimes = tracers.get(query);
				for(int i = 0; i < execTimes.size(); i++) {
					Long execTime = execTimes.get(i);
					stats.addValue(execTime);
				}
				
				double average = stats.getMean();
				double max = stats.getMax();
				double min = stats.getMin();
				double count = execTimes.size();
				
				QueryTracer tracer = new QueryTracer(query, average, max, min, count, timestamp);
				list.add(tracer);
				
				sqlQueries.add(new SqlQuery(query));
				
			}
			if(tracers != null && !tracers.isEmpty()) {
				List<SqlQuery> currentList = collectorService.retrieveSqlQueries();
				sqlQueries.addAll(currentList);
				collectorService.saveQueryTracingStatistics(list);
				collectorService.saveSqlQueries(new ArrayList<SqlQuery>(sqlQueries));
				System.out.println("inserted " + list.size() + " items into Query Tracer table at " + timestamp);
		    	tracers.clear();
			}
				    	
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		
	}

}
