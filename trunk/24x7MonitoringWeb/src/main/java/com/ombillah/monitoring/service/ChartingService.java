package com.ombillah.monitoring.service;

import org.jfree.chart.JFreeChart;

import com.ombillah.monitoring.domain.ChartProperties;
import com.ombillah.monitoring.domain.SearchFilter;

/**
 * Service class to interact with JFreeChart Library.
 * @author Oussama M Billah
 */
public interface ChartingService {

	public JFreeChart generateChart(ChartProperties properties, SearchFilter searchFilter);
}
