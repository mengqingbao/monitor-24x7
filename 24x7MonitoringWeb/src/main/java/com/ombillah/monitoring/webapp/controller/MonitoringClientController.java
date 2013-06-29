package com.ombillah.monitoring.webapp.controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ombillah.monitoring.domain.MethodSignature;
import com.ombillah.monitoring.domain.MethodTracer;
import com.ombillah.monitoring.domain.MethodTracers;
import com.ombillah.monitoring.domain.MonitoredItem;
import com.ombillah.monitoring.domain.TracingFilter;
import com.ombillah.monitoring.service.CollectorService;


/**
 * Rest Client for Monitoring Information
 * @author Oussama M Billah
 *
 */
@Controller
public class MonitoringClientController {

	private static final int TWENTY_FOUR_HOURS_IN_MIN = 1440;
	private static final int ONE_MINUTE = 60;
	private static final int DEFAULT_RESOLUTION = 30;
	
	@Autowired
	private CollectorService collectorService;
	
	@RequestMapping(value="/json/getTracedMethods", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public Collection<MonitoredItem> getTracedMethods() {
		List<MethodSignature> methodSignatures = collectorService.retrieveMethodSignatures();
		
		MonitoredItem root = new MonitoredItem("");
        for (MethodSignature signature : methodSignatures)
        {
            root.Push(signature.getMethodName().split("\\."), 0);
        }
        Collection<MonitoredItem> monitoredItems = root.getSubItems();
		
		return monitoredItems;
		
	}
	
	@RequestMapping(value="/json/methodTracingInfo/{methodSignature:.+}", method = RequestMethod.POST, produces = "application/json")
	@ResponseBody
	public MethodTracers getMonitoringInformation(
			@PathVariable("methodSignature") String methodSignature,
			@RequestBody TracingFilter tracingFilter) throws ParseException {
		 
		 SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
		 Integer timeRangeinMins = tracingFilter.getTimeRangeInMins();
		 Integer resolutionInSec = tracingFilter.getResolutionInSecs();
		 String fromRange = tracingFilter.getFromRange();
		 String toRange = tracingFilter.getToRange();
		 
		 Date minDate;
		 Date maxDate;
		 
		 if(StringUtils.isNotEmpty(fromRange)) {
			 SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy hh:mm a");
			 minDate = format.parse(fromRange);
			 maxDate = format.parse(toRange);
			 Long differenceinMs = maxDate.getTime() - minDate.getTime();
			 timeRangeinMins = (int) TimeUnit.MILLISECONDS.toMinutes(differenceinMs);
			 resolutionInSec = timeRangeinMins;
			 if(resolutionInSec < DEFAULT_RESOLUTION) {
				 resolutionInSec = DEFAULT_RESOLUTION;
			 }
		 } else {

			 maxDate = new Date();
			 Calendar cal = Calendar.getInstance();
			 cal.setTime(maxDate);
			 cal.add(Calendar.MINUTE, -1 * timeRangeinMins);
			 minDate = cal.getTime();
		 }
		 
		 
		 if(timeRangeinMins > TWENTY_FOUR_HOURS_IN_MIN) {
			 dateFormat = new SimpleDateFormat("MMM d HH:mm");
		 }
		 else if(timeRangeinMins > ONE_MINUTE) {
			 dateFormat = new SimpleDateFormat("HH:mm");
		 }

		 List<String> methodSignatures = tracingFilter.getSearchedItems();
		 methodSignatures.add(methodSignature);
		 List<MethodTracer> methodTracersGrouped = collectorService.retrieveMethodStatisticsGroupedByMethodName(
				 methodSignatures, 
				 null, 
				 null, 
				 minDate, 
				 maxDate);
		 
		 Map<String, List<MethodTracer>> tracersByResolution = groupTracersByResolution(
				methodSignatures,
				dateFormat, 
				resolutionInSec, 
				maxDate, 
				minDate);
		 
		 MethodTracers result = new MethodTracers();
		 result.setTracersGrouped(methodTracersGrouped);
		 result.setTracersByResolution(tracersByResolution);

		return result;		
	}

	private Map<String, List<MethodTracer>> groupTracersByResolution(
			List<String> methodSignatures,
			SimpleDateFormat dateFormat, 
			int resolutionInSec, 
			Date maxDate,
		    Date minDate
		    ) {
		 Map<String, List<MethodTracer>> tracersByResolution = new LinkedHashMap<String, List<MethodTracer>>();
		 Date currentDate  = new Date(minDate.getTime());
		 Calendar cal = Calendar.getInstance();
		 while(currentDate.compareTo(maxDate) <=0 ) {
			 cal.setTime(currentDate);
			 cal.add(Calendar.SECOND, resolutionInSec);
			 currentDate = cal.getTime();
			// System.out.println("Min=" + minDate + "Max=" +currentDate);
//			 List<MethodTracer> tracersInRange = collectorService.retrieveMethodStatistics(methodSignatures, 
//					 null, 
//					 null, 
//					 minDate, 
//					 currentDate);
//			 tracersByResolution.put(dateFormat.format(currentDate), tracersInRange);
		 }
		return tracersByResolution;
	}

		
}
