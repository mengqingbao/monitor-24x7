package com.ombillah.monitoring.webapp.rest;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.math.stat.descriptive.SummaryStatistics;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ombillah.monitoring.domain.CollectedData;
import com.ombillah.monitoring.domain.ExceptionLogger;
import com.ombillah.monitoring.domain.HttpRequestUrl;
import com.ombillah.monitoring.domain.MethodSignature;
import com.ombillah.monitoring.domain.MonitoredItemTracer;
import com.ombillah.monitoring.domain.SqlQuery;
import com.ombillah.monitoring.service.TroubleshootingService;

/**
 * Rest Service to store the collected performance
 * in the datastore
 * @author Oussama.
 *
 */
@Controller
@RequestMapping(value="/rest/")
public class MetricsPersisterRestClient {
	
	@Resource(name="troubleshootingService")
	private TroubleshootingService troubleshootingService;
	
	@RequestMapping(value="processCollectedData", method = RequestMethod.POST)
	@ResponseBody
	public String processCollectedData(@RequestBody CollectedData collectedData) {	
		
		Map<String, List<Long>> tracers = collectedData.getTracer();
		List<MonitoredItemTracer> preparedMetrics = new CopyOnWriteArrayList<MonitoredItemTracer>();
		List<MethodSignature> methodSignatures = new ArrayList<MethodSignature>();
		List<HttpRequestUrl> httpRequests = new ArrayList<HttpRequestUrl>();
		List<SqlQuery> sqlQueries = new ArrayList<SqlQuery>();

		Date timestamp = new Date();
		
		for(String itemName : tracers.keySet()) {

			MonitoredItemTracer tracer = prepareCollectedData(tracers, timestamp, itemName);
			preparedMetrics.add(tracer);
			if(StringUtils.equals(tracer.getType(), "HTTP_REQUEST")) {
				httpRequests.add(new HttpRequestUrl(tracer.getItemName()));
			} 
			else if(StringUtils.equals(tracer.getType(), "JAVA")) {
				methodSignatures.add(new MethodSignature(tracer.getItemName()));
			}
			else if(StringUtils.equals(tracer.getType(), "SQL")) {
				sqlQueries.add(new SqlQuery(tracer.getItemName()));
			}
		}
		if(tracers != null && !tracers.isEmpty()) {
			storeCollectedMetrics(preparedMetrics, methodSignatures, httpRequests, sqlQueries);
		}
		
		if(!collectedData.getLoggedExceptions().isEmpty()) {
			List<ExceptionLogger> exceptions = collectedData.getLoggedExceptions();
			troubleshootingService.saveExceptions(exceptions);
		}
		return "done";
	}


	private void storeCollectedMetrics(List<MonitoredItemTracer> list,
			List<MethodSignature> methodSignatures,
			List<HttpRequestUrl> httpRequests, List<SqlQuery> sqlQueries) {
		List<SqlQuery> sqlQueriesFromDB = troubleshootingService.retrieveSqlQueries();
		sqlQueries.removeAll(sqlQueriesFromDB); // remove existing entries.
		troubleshootingService.saveSqlQueries(sqlQueries);
		
		List<MethodSignature> methodSignaturesFromDB = troubleshootingService.retrieveMethodSignatures();
		methodSignatures.removeAll(methodSignaturesFromDB); // remove existing entries.
		troubleshootingService.saveMethodSignatures(methodSignatures);

		List<HttpRequestUrl> httpRequestsFromDB = troubleshootingService.retrieveHttpRequestUrls();
		httpRequests.removeAll(httpRequestsFromDB); // remove existing entries.
		troubleshootingService.saveHttpRequestUrls(new ArrayList<HttpRequestUrl>(httpRequests));
		
		troubleshootingService.saveMonitoredItemTracingStatistics(list);
	}


	private MonitoredItemTracer prepareCollectedData(
			Map<String, List<Long>> tracers, 
			Date timestamp, 
			String itemName) {
		SummaryStatistics stats = new SummaryStatistics();
		List<Long> execTimes = tracers.get(itemName);
		for(int i = 0; i < execTimes.size(); i++) {
			Long execTime = execTimes.get(i);
			stats.addValue(execTime);
		}
		
		Double average = stats.getMean();
		Double max = stats.getMax();
		Double min = stats.getMin();
		Integer count = execTimes.size();
		
		String[] nameTypeArray = itemName.split("\\|\\|");
		String type = nameTypeArray[0];
		String monitoredItemName = nameTypeArray[1];
		
		MonitoredItemTracer tracer = new MonitoredItemTracer(monitoredItemName, type, average, max, min, count, timestamp);
		return tracer;
	}
	
}
