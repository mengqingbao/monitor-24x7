package com.ombillah.monitoring.service.impl;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.title.LegendTitle;
import org.jfree.data.time.Minute;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.RectangleInsets;
import org.jfree.ui.Size2D;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ombillah.monitoring.domain.ChartProperties;
import com.ombillah.monitoring.domain.MethodTracer;
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
			
		TimeSeriesCollection data = getDataset(properties.getTitle(), searchFilter);
		JFreeChart chart = ChartFactory.createTimeSeriesChart(properties.getTitle(), // title
				properties.getxAxisLabel(), // x-axis label
				properties.getyAxisLabel(), // y-axis label
				data, true, // legend displayed
				true, // tooltips displayed
				false); // no URLs*/

		XYPlot plot = (XYPlot) chart.getPlot();
		plot.setBackgroundPaint(Color.lightGray);
		plot.setDomainGridlinePaint(Color.white);
		plot.setRangeGridlinePaint(Color.white);
		plot.setAxisOffset(new RectangleInsets(5.0, 5.0, 5.0, 5.0));
		plot.setDomainCrosshairVisible(true);
		plot.setRangeCrosshairVisible(true);
		XYItemRenderer r = plot.getRenderer();
		
		if (r instanceof XYLineAndShapeRenderer) {
			XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) r;
			renderer.setBaseShapesVisible(true);
			renderer.setBaseShapesFilled(true);
		}
		
		plot.setBackgroundPaint(Color.white);
		return chart;
	}

	private TimeSeriesCollection getDataset(String title, SearchFilter searchFilter) {
		// DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		// Run the SQL query and add the results to the JFreeChart dataset

		TimeSeriesCollection dataset = new TimeSeriesCollection();
		List<MethodTracer> tracers = collectorService.retrieveMethodStatistics(searchFilter);
		if (tracers == null || tracers.isEmpty()) {
			return dataset;
		}
		String currentMethodName = tracers.get(0).getMethodName();
		String methodShortName = getShortName(currentMethodName);
		TimeSeries series = new TimeSeries(methodShortName);
		for (MethodTracer tracer : tracers) {

			String methodName = tracer.getMethodName();
			methodShortName = getShortName(methodName);
			if (!StringUtils.equals(methodName, currentMethodName)) {
				dataset.addSeries(series);
				series = new TimeSeries(methodShortName);
				currentMethodName = methodName;
			}
			Number responseTime = tracer.getAverage();
			Date date = tracer.getCreationDate();
			series.addOrUpdate(new Minute(date), responseTime);
		}
		dataset.addSeries(series);

		return dataset;
	}

	private String getShortName(String methodName) {
		String[] array = methodName.split("\\.");
		int length = array.length;
		if (length < 2) {
			return methodName;
		}
		return array[length - 2] + "." + array[length - 1];
	}

}
