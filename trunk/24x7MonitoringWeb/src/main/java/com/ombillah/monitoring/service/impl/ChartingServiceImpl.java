package com.ombillah.monitoring.service.impl;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.annotations.XYTitleAnnotation;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.labels.CustomXYToolTipGenerator;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.title.LegendTitle;
import org.jfree.chart.title.TextTitle;
import org.jfree.chart.title.Title;
import org.jfree.data.time.Second;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.RectangleAnchor;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.RectangleInsets;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ombillah.monitoring.domain.ChartProperties;
import com.ombillah.monitoring.domain.MonitoredItemTracer;
import com.ombillah.monitoring.domain.SearchFilter;
import com.ombillah.monitoring.service.ChartingService;
import com.ombillah.monitoring.service.CollectorService;

/**
 * Service class to interact with JFreeChart Library.
 * @author Oussama M Billah
 */
@Service
public class ChartingServiceImpl implements ChartingService{
	
	@Autowired
	private CollectorService collectorService;
	
	public JFreeChart generateChart(ChartProperties properties, SearchFilter searchFilter) {
		
		Map<String, List<MonitoredItemTracer>> map = new HashMap<String, List<MonitoredItemTracer>>();
		TimeSeriesCollection data = getDataset(map, properties.getTitle(), searchFilter);

		JFreeChart chart = ChartFactory.createTimeSeriesChart(properties.getTitle(), // title
				properties.getxAxisLabel(), // x-axis label
				properties.getyAxisLabel(), // y-axis label
				data, 
				true, // legend displayed
				true, // tooltips displayed
				false); // no URLs*/
		XYPlot plot = (XYPlot) chart.getPlot();
		if(StringUtils.equals(searchFilter.getMethodSignatures().get(0), "Memory")) {
			LegendTitle lt = new LegendTitle(plot);
			lt.setPosition(RectangleEdge.BOTTOM);
			XYTitleAnnotation ta = new XYTitleAnnotation(0.98, 0.02, lt,RectangleAnchor.BOTTOM_RIGHT);
			plot.addAnnotation(ta);
		}
		plot.setBackgroundPaint(Color.white);
		plot.setDomainGridlinePaint(Color.white);
        plot.setRangeGridlinePaint(Color.white);
        plot.setAxisOffset(new RectangleInsets(5.0, 5.0, 5.0, 5.0));
        plot.setDomainCrosshairVisible(true);
        plot.setRangeCrosshairVisible(true);
        List<Title> subTitles = new ArrayList<Title>();
        subTitles.add(new TextTitle("Hover over datapoints for details."));
        chart.setSubtitles(subTitles);
        DateAxis xaxis = (DateAxis) plot.getDomainAxis();
        xaxis.setDateFormatOverride(searchFilter.getDateFormat());

        XYItemRenderer r = plot.getRenderer();
        if (r instanceof XYLineAndShapeRenderer) {
            XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) r;
            renderer.setBaseShapesVisible(true);
            renderer.setBaseShapesFilled(true);
            CustomToolTipGenerator generator = new CustomToolTipGenerator(map);
            boolean test = true;
            if(test) {
            	renderer.setBaseToolTipGenerator(generator);
            }
            
        }
		
		return chart;
	}
	
	private TimeSeriesCollection getDataset(Map<String, List<MonitoredItemTracer>> tracersMap, String title, SearchFilter searchFilter) {
		
		List<MonitoredItemTracer> tracers = collectorService.retrieveItemStatistics(searchFilter);

		TimeSeriesCollection dataset = new TimeSeriesCollection();
		if (tracers == null || tracers.isEmpty()) {
			return dataset;
		}
		String currentMethodName = tracers.get(0).getItemName();
		tracersMap.put(currentMethodName, new ArrayList<MonitoredItemTracer>());
		
		TimeSeries series = new TimeSeries(currentMethodName);
		for (MonitoredItemTracer tracer : tracers) {
			List<MonitoredItemTracer> list = tracersMap.get(currentMethodName);
			String itemName = tracer.getItemName();
			if (!StringUtils.equals(itemName, currentMethodName)) {
				list = new ArrayList<MonitoredItemTracer>();
				dataset.addSeries(series);
				series = new TimeSeries(itemName);
				currentMethodName = itemName;
			}
			list.add(tracer);
			tracersMap.put(currentMethodName, list);
			Number responseTime = tracer.getAverage();
			Date date = tracer.getCreationDate();
			series.addOrUpdate(new Second(date), responseTime);
		}
		dataset.addSeries(series);

		return dataset;
	}
	
	 private static class CustomToolTipGenerator extends CustomXYToolTipGenerator {
		 
		 	private static final long serialVersionUID = 1L;
			private Map<String, List<MonitoredItemTracer>> tracers;
		 	
		 	public CustomToolTipGenerator(Map<String, List<MonitoredItemTracer>> tracers) {
		 		this.tracers  = tracers;
		 	}
		 	
	        @Override
	        public String generateToolTip(XYDataset data, int series, int item) {
	        	String itemName = (String) data.getSeriesKey(series);
	        	
	        	MonitoredItemTracer tracer = tracers.get(itemName).get(item);
	            StringBuilder builder = new StringBuilder();
	            builder.append(tracer.getItemName() + "\n");
	            builder.append(tracer.getCount() + " invocations \n");
	            String unit = "ms";
	            if(StringUtils.equals("Total Memory", itemName) || StringUtils.equals("Used Memory", itemName)) {
	            	unit = "MB";
	            }
	            builder.append("Average: " + tracer.getAverage() + " " + unit +"\n");
	            builder.append("Max: " + tracer.getMax() +  " " + unit +"\n");
	            builder.append("Min: " + tracer.getMin() +  " " + unit +"\n");
	            builder.append("at " + tracer.getCreationDate() + "\n");
	            
				return builder.toString();
	        }
	    }

 }
