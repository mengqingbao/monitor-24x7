package com.ombillah.monitoring.webapp.controller;

import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.StringUtils;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.title.LegendTitle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ombillah.monitoring.domain.ChartProperties;
import com.ombillah.monitoring.domain.MethodSignature;
import com.ombillah.monitoring.domain.MethodTracer;
import com.ombillah.monitoring.domain.MethodTracers;
import com.ombillah.monitoring.domain.MonitoredItem;
import com.ombillah.monitoring.domain.SearchFilter;
import com.ombillah.monitoring.domain.TracingFilter;
import com.ombillah.monitoring.service.ChartingService;
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
	
	@Autowired 
	private ChartingService chartingService;
	
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
		 
		 Integer timeRangeinMins = tracingFilter.getTimeRangeInMins();
		 String fromRange = tracingFilter.getFromRange();
		 String toRange = tracingFilter.getToRange();
		 
		 SearchFilter searchFilter = createSearchFilter(methodSignature,
				timeRangeinMins, tracingFilter.getSearchedItems(), fromRange, toRange);

		 List<MethodTracer> methodTracersGrouped = collectorService.retrieveMethodStatisticsGroupedByMethodName(searchFilter);
		 
		 MethodTracers result = new MethodTracers();
		 result.setTracersGrouped(methodTracersGrouped);
		 
		 return result;		
	}

	private SearchFilter createSearchFilter(String methodSignature,
		    Integer timeRangeinMins, List<String> methodSignatures,
			String fromRange, String toRange) throws ParseException {

		 Date minDate;
		 Date maxDate;
		 
		 if(StringUtils.isNotEmpty(fromRange)) {
			 SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy hh:mm a");
			 minDate = format.parse(fromRange);
			 maxDate = format.parse(toRange);
			 Long differenceinMs = maxDate.getTime() - minDate.getTime();
			 timeRangeinMins = (int) TimeUnit.MILLISECONDS.toMinutes(differenceinMs);
		
		 } else {

			 maxDate = new Date();
			 Calendar cal = Calendar.getInstance();
			 cal.setTime(maxDate);
			 cal.add(Calendar.MINUTE, -1 * timeRangeinMins);
			 minDate = cal.getTime();
		 }
		 
         SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
		 if(timeRangeinMins > TWENTY_FOUR_HOURS_IN_MIN) {
			 dateFormat = new SimpleDateFormat("MMM d HH:mm");
		 }
		 else if(timeRangeinMins > ONE_MINUTE) {
			 dateFormat = new SimpleDateFormat("HH:mm");
		 }
		 
		 methodSignatures.add(methodSignature);
		 SearchFilter searchFilter = new SearchFilter();
		 searchFilter.setResolutionInSecs(timeRangeinMins);
		 searchFilter.setMaxDate(maxDate);
		 searchFilter.setMinDate(minDate);
		 searchFilter.setMethodSignatures(methodSignatures);
		 searchFilter.setDateFormat(dateFormat);
		 
		return searchFilter;
	}
	
	@RequestMapping("viewchart.png")
	public void renderChart(
			String methodSignature,
			Integer timeRangeInMins, 
			Integer resolutionInSec,
			String fromRange,
			String toRange,
			ArrayList<String> searchedItems,
			OutputStream stream)
			throws Exception {
		
		SearchFilter searchFilter = createSearchFilter(methodSignature,
				timeRangeInMins, searchedItems, fromRange, toRange);
		
		ChartProperties properties = new ChartProperties();
		properties.setTitle("Average Response Time");
		properties.setxAxisLabel("Date");
		properties.setyAxisLabel("Response Time");
		
		JFreeChart chart = chartingService.generateChart(properties, searchFilter);
		LegendTitle legend = chart.getLegend();
		int width = 900;
		int height = 500;
		ChartUtilities.writeChartAsPNG(stream, chart, width, height);
		stream.close();
	}		
}
