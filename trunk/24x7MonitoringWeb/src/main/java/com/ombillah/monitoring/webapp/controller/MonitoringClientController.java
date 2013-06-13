package com.ombillah.monitoring.webapp.controller;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.math.stat.descriptive.SummaryStatistics;
import org.apache.commons.math.stat.descriptive.moment.Mean;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ombillah.monitoring.domain.MethodTracer;
import com.ombillah.monitoring.domain.MethodTracers;
import com.ombillah.monitoring.domain.MonitoredItem;
import com.ombillah.monitoring.domain.TracingFilter;
import com.ombillah.monitoring.service.ehcache.EhcacheManager;


/**
 * Rest Client for Monitoring Information
 * @author Oussama M Billah
 *
 */
@Controller
public class MonitoringClientController {

	private static final int DEFAULT_RESOLUTION = 30;
	private static final int TWENTY_FOUR_HOURS_IN_MIN = 1440;
	private static final int ONE_MINUTE = 60;
	
	@Resource(name="ehcacheManager")
	private EhcacheManager cacheManager;
	
	@RequestMapping(value="/json/getTracedMethods", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public Collection<MonitoredItem> getTracedMethods() {
		Set<String> methodSignatures = cacheManager.retrieveMethodSignatures();
		
		MonitoredItem root = new MonitoredItem("");
        for (String signature : methodSignatures)
        {
            root.Push(signature.split("\\."), 0);
        }
        Collection<MonitoredItem> monitoredItems = root.getSubItems();
		
		return monitoredItems;
		
	}
	
	@RequestMapping(value="/json/methodTracingInfo/{methodSignature:.+}", method = RequestMethod.POST, produces = "application/json")
	@ResponseBody
	public MethodTracers getMonitoringInformation(
			@PathVariable("methodSignature") String methodSignature,
			@RequestBody TracingFilter tracingFilter) {
		 
		 SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
		 int timeRangeinMins = tracingFilter.getTimeRangeInMins();
		 int resolutionInSec = tracingFilter.getResolutionInSecs();
		 
		 if(timeRangeinMins > TWENTY_FOUR_HOURS_IN_MIN) {
			 dateFormat = new SimpleDateFormat("MMM d HH:mm");
		 }
		 else if(timeRangeinMins > ONE_MINUTE) {
			 dateFormat = new SimpleDateFormat("HH:mm");
		 }

		 Date maxDate = new Date();
		 Calendar cal = Calendar.getInstance();
		 cal.setTime(maxDate);
		 cal.add(Calendar.MINUTE, -1 * timeRangeinMins);
		 Date minDate = cal.getTime();
		 
		 List<MethodTracer> tracersFromCache = cacheManager.retrieveMethodStatistics(methodSignature, 
				 null, 
				 null, 
				 minDate, 
				 maxDate);
		 List<MethodTracer> tracers = new ArrayList<MethodTracer>(tracersFromCache);
		 Map<String, List<MethodTracer>> map = groupMethodTracersToMap(tracers);
		 List<MethodTracer> methodTracersGrouped = extractMethodTracersFromMap(map);
		 
		 Map<String, List<MethodTracer>> tracersByResolution = groupTracersByResolution(
				dateFormat, resolutionInSec, maxDate, cal, minDate, tracers);
		 
		 MethodTracers result = new MethodTracers();
		 result.setTracersGrouped(methodTracersGrouped);
		 result.setTracersByResolution(tracersByResolution);

		return result;		
	}

	private Map<String, List<MethodTracer>> groupTracersByResolution(
			SimpleDateFormat dateFormat, int resolutionInSec, Date maxDate,
			Calendar cal, Date minDate, List<MethodTracer> tracers) {
		 Map<String, List<MethodTracer>> tracersByResolution = new LinkedHashMap<String, List<MethodTracer>>();
		 Date currentDate  = new Date(minDate.getTime());
		 
		 while(currentDate.compareTo(maxDate) <=0 ) {
			 cal.setTime(currentDate);
			 cal.add(Calendar.SECOND, resolutionInSec);
			 currentDate = cal.getTime();
			// System.out.println("Min=" + minDate + "Max=" +currentDate);
			 List<MethodTracer> tracersInRange = getTracersInRange(tracers, minDate, currentDate, resolutionInSec);
			 tracersByResolution.put(dateFormat.format(currentDate), tracersInRange);
		 }
		return tracersByResolution;
	}

	private List<MethodTracer> getTracersInRange(List<MethodTracer> tracers, Date minDate, Date maxDate, int resolutionInSec) {
		List<MethodTracer> list = new ArrayList<MethodTracer>();
		for(MethodTracer tracer : tracers) {
			Date date = tracer.getCreationDate();
			if(date.before(maxDate) && date.after(minDate)) {
				list.add(tracer);
			}
		}
		
		tracers.removeAll(list);
		return list;
	
		
//		if(resolutionInSec > DEFAULT_RESOLUTION) {
//			//adopt the data displayed to match the new interval.
//			 Map<String, List<MethodTracer>> map = groupMethodTracersToMap(tracers);
//			 List<MethodTracer> methodTracersGrouped = extractMethodTracersFromMap(map);
//			 tracers.removeAll(list);
//			 return methodTracersGrouped;
//		}
//		else {
//			tracers.removeAll(list);
//			return list;
//		}
	}

	private Map<String, List<MethodTracer>> groupMethodTracersToMap(
			List<MethodTracer> tracers) {
		Map<String, List<MethodTracer>> map = new HashMap<String, List<MethodTracer>>();
		 
		 for(MethodTracer tracer : tracers) {
			 List<MethodTracer> list = map.get(tracer.getMethodName());
			 if(list == null) {
				 list = new ArrayList<MethodTracer>();
			 }
			 list.add(tracer);
			 map.put(tracer.getMethodName(), list);
		 }
		return map;
	}

	private List<MethodTracer> extractMethodTracersFromMap(
			Map<String, List<MethodTracer>> map) {
		List<MethodTracer> methodTracersGrouped = new ArrayList<MethodTracer>(); 
		 for( String methodName : map.keySet()) {
			 List<MethodTracer> list = map.get(methodName);
			 MethodTracer tracerGrouped = computeTracingInfoByInterval(methodName, list);
			 methodTracersGrouped.add(tracerGrouped);
		 }
		return methodTracersGrouped;
	}

	private MethodTracer computeTracingInfoByInterval(String methodName,
			List<MethodTracer> list) {
		double[] averages = new double[list.size()];
		 double[] weights = new double[list.size()];

		 SummaryStatistics maxMinStats = new SummaryStatistics();
		 int  countTotal = 0;

		 for(MethodTracer tracer : list) {
			 averages = ArrayUtils.add(averages, tracer.getAverage());
			 weights = ArrayUtils.add(weights, tracer.getCount());
			 maxMinStats.addValue(tracer.getMax());
			 maxMinStats.addValue(tracer.getMin());
			 countTotal += tracer.getCount();
		 }
		 
		 Mean mean = new Mean();
		 Double weightedAverage = mean.evaluate(averages, weights);
		 
		 MethodTracer tracerGrouped = new MethodTracer();
		 tracerGrouped.setMethodName(methodName);
		 tracerGrouped.setAverage(Math.round(weightedAverage));
		 tracerGrouped.setCount(countTotal);
		 tracerGrouped.setMax(maxMinStats.getMax());
		 tracerGrouped.setMin(maxMinStats.getMin());
		return tracerGrouped;
	}
		
}
