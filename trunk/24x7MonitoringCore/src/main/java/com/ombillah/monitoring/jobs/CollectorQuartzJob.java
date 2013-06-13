package com.ombillah.monitoring.jobs;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.commons.math.stat.descriptive.SummaryStatistics;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.ombillah.monitoring.domain.CollectedData;
import com.ombillah.monitoring.domain.MethodTracer;
import com.ombillah.monitoring.factory.ApplicationFactory;
import com.ombillah.monitoring.storage.ehcache.EhcacheManager;

/**
 * Quartz Job to collect and stored performance information on a pre-defined interval.
 * @author Oussama M Billah
 *
 */
public class CollectorQuartzJob implements Job {

	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		
		try {
			EhcacheManager cacheManager = ApplicationFactory.ehCacheManager();
			CollectedData collectedData = ApplicationFactory.collectedData();
			
			Map<String, List<Long>> tracers = collectedData.getMethodTracer();
			
			List<MethodTracer> list = new CopyOnWriteArrayList<MethodTracer>();
			Set<String> methodSignatures = new HashSet<String>();
			Date timestamp = new Date();

			for(String methodName : tracers.keySet()) {

				SummaryStatistics stats = new SummaryStatistics();
				List<Long> execTimes = tracers.get(methodName);
				for(int i = 0; i < execTimes.size(); i++) {
					Long execTime = execTimes.get(i);
					if(execTime == null) {
						//System.out.println(methodName + " have null exectime");
					}
					stats.addValue(execTime);
				}
				
				double average = stats.getMean();
				double max = stats.getMax();
				double min = stats.getMin();
				double count = execTimes.size();
				
				MethodTracer tracer = new MethodTracer(methodName, average, max, min, count, timestamp);
				list.add(tracer);
				
				methodSignatures.add(methodName);
				
			}
			if(tracers != null && !tracers.isEmpty()) {
				Set<String> storedMethodSignatures = cacheManager.retrieveMethodSignatures();
				methodSignatures.addAll(storedMethodSignatures);
				
				cacheManager.writeMethodTracersToCache(list);
				cacheManager.writeMethodSignaturesToCache(methodSignatures);
				System.out.println("inserted " + list.size() + " items into methodTracerCache at " + timestamp);
				//System.out.println("inserted " + methodSignatures.size() + " items into methodSignatures at " + timestamp );
		    	tracers.clear();
			}
				    	
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		
	}
}
