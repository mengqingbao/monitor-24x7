package com.ombillah.monitoring.webapp.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.jfree.chart.ChartRenderingInfo;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.entity.StandardEntityCollection;
import org.jfree.chart.servlet.ServletUtilities;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ombillah.monitoring.domain.ChartProperties;
import com.ombillah.monitoring.domain.ExceptionLogger;
import com.ombillah.monitoring.domain.ItemTracers;
import com.ombillah.monitoring.domain.MethodSignature;
import com.ombillah.monitoring.domain.MonitoredItemTracer;
import com.ombillah.monitoring.domain.MonitoredItem;
import com.ombillah.monitoring.domain.SearchFilter;
import com.ombillah.monitoring.domain.TracingFilter;
import com.ombillah.monitoring.service.ChartingService;
import com.ombillah.monitoring.service.CollectorService;

/**
 * Rest Client for Monitoring Information
 * 
 * @author Oussama M Billah
 * 
 */
@Controller
public class MonitoringClientController {

	private static final int TWENTY_FOUR_HOURS_IN_MIN = 1440;
	private static final int ONE_MINUTE = 60;
	
	@Autowired
	private CollectorService collectorService;

	@Autowired
	private ChartingService chartingService;

	@RequestMapping(value = "/json/getMonitoredItems", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public Map<String, Object> getMonitoredItems() {
		
		Map<String, Object> map = new HashMap<String, Object>();
		Collection<MonitoredItem> tracedMethods = getTracedMethods();
		List<String> tracedQueries = getTracedQueries();
		List<String> tracedRequests = getTracedHttpRequests();
		map.put("tracedMethods", tracedMethods);
		map.put("tracedQueries", tracedQueries);
		map.put("HttpRequestUrls", tracedRequests);
		return map;

	}

	private List<String> getTracedHttpRequests() {
		List<String> httpRequestUrls = collectorService.retrieveHttpRequestUrls();
		return httpRequestUrls;
	}

	private Collection<MonitoredItem> getTracedMethods() {
		List<MethodSignature> methodSignatures = collectorService
				.retrieveMethodSignatures();

		MonitoredItem root = new MonitoredItem("");
		for (MethodSignature signature : methodSignatures) {
			root.Push(signature.getItemName().split("\\."), 0);
		}
		Collection<MonitoredItem> monitoredItems = root.getSubItems();
		return monitoredItems;
	}

	private List<String> getTracedQueries() {
		List<String> sqlQueries = collectorService.retrieveSqlQueries();
		return sqlQueries;
	}
	
	@RequestMapping(value = "/json/retrieveTracingInfo/{tracedItem:.+}", method = RequestMethod.POST, produces = "application/json")
	@ResponseBody
	public ItemTracers getMonitoringInformation(
			@PathVariable("tracedItem") String tracedItem,
			@RequestBody TracingFilter tracingFilter) throws ParseException {

		Integer timeRangeinMins = tracingFilter.getTimeRangeInMins();
		String fromRange = tracingFilter.getFromRange();
		String toRange = tracingFilter.getToRange();

	
		SearchFilter searchFilter = createSearchFilter(tracedItem,
				timeRangeinMins, tracingFilter.getSearchedItems(), fromRange,
				toRange);
		
		ItemTracers result = new ItemTracers();
		List<MonitoredItemTracer> monitoredItemTracersGrouped = collectorService.retrieveItemStatisticsGroupedByMonitoredItem(searchFilter);
		result.setMonitoredItemTracersGrouped(monitoredItemTracersGrouped);

		return result;
	}

	private SearchFilter createSearchFilter(String methodSignature,
			Integer timeRangeinMins, List<String> methodSignatures,
			String fromRange, String toRange) throws ParseException {

		Date minDate;
		Date maxDate;

		if (StringUtils.isNotEmpty(fromRange)) {
			SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy hh:mm a");
			minDate = format.parse(fromRange);
			maxDate = format.parse(toRange);
			Long differenceinMs = maxDate.getTime() - minDate.getTime();
			timeRangeinMins = (int) TimeUnit.MILLISECONDS
					.toMinutes(differenceinMs);

		} else {

			maxDate = new Date();
			Calendar cal = Calendar.getInstance();
			cal.setTime(maxDate);
			cal.add(Calendar.MINUTE, -1 * timeRangeinMins);
			minDate = cal.getTime();
		}

		SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
		if (timeRangeinMins > TWENTY_FOUR_HOURS_IN_MIN) {
			dateFormat = new SimpleDateFormat("MMM d HH:mm");
		} else if (timeRangeinMins > ONE_MINUTE) {
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

	@RequestMapping(value = "/json/retrieveExceptionLogs", method = RequestMethod.POST, produces = "application/json")
	@ResponseBody
	public List<ExceptionLogger> retrieveExceptionLogs(
			@RequestBody TracingFilter tracingFilter) throws ParseException {

		Integer timeRangeinMins = tracingFilter.getTimeRangeInMins();
		String fromRange = tracingFilter.getFromRange();
		String toRange = tracingFilter.getToRange();

	
		SearchFilter searchFilter = createSearchFilter("",
				timeRangeinMins, new ArrayList<String>(), fromRange,
				toRange);
		
		List<ExceptionLogger> exceptionLogger = collectorService.retrieveExceptionLoggers(searchFilter);
		return exceptionLogger;
	}
	
	@RequestMapping(value="getchart.do")
	public void getChart(HttpServletResponse response, String monitoredItem, Integer timeRangeInMins, 
			String fromRange, String toRange,
			String[] searchedItems) throws ParseException, IOException {

		PrintWriter writer = response.getWriter();
		SearchFilter searchFilter = createSearchFilter(monitoredItem,
				timeRangeInMins, new LinkedList<String>(Arrays.asList(searchedItems)), 
				fromRange, toRange);

		ChartProperties properties = new ChartProperties();
		String yLabel = "Average Response Time (ms)";
		String title = "Response Time";
		
		if(StringUtils.equals(monitoredItem, "Memory")) {
			yLabel = "Memory Size (MB)";
			title = "Memory Utilization";
		}
		else if(StringUtils.equals(monitoredItem, "Database Connections")) {
			yLabel = "Active DB Connections";
			title = "Active Database Connections";
		}
		else if(StringUtils.equals(monitoredItem, "Active Sessions")) {
			yLabel = "Active Sessions";
			title = "Active Sessions";
		}
		properties.setTitle(title);
		properties.setxAxisLabel("Date");
		properties.setyAxisLabel(yLabel);

	    writer.println("<html>");
		writer.println("<body>");
		JFreeChart chart = chartingService.generateChart(properties, searchFilter);
		int width = 900;
		int height = 500;
		
		ChartRenderingInfo info = new ChartRenderingInfo(
				new StandardEntityCollection());
		String filename = ServletUtilities.saveChartAsPNG(chart, width, height, info, null);
		ChartUtilities.writeImageMap(writer, filename, info, false);
		String graphURL = "DisplayChart?filename=" + filename;

		writer.println("<img id='chart' src=\"" + graphURL + "\" width=" + width + " height=" + height + " border=0 usemap=\"#" +  filename + "\">");
		writer.println("</body>");
		writer.println("</html>");
		
	}
	
}
