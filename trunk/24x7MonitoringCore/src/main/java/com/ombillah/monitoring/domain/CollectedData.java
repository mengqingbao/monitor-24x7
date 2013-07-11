package com.ombillah.monitoring.domain;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Domain to hold singleton Collected Data.
 * @author Oussama M Billah
 *
 */
public class CollectedData {
	
	private Map<String, List<Long>> tracer = new HashMap<String, List<Long>>();

	public Map<String, List<Long>> getTracer() {
		return tracer;
	}

	public void setTracer(Map<String, List<Long>> tracer) {
		this.tracer = tracer;
	}

}
