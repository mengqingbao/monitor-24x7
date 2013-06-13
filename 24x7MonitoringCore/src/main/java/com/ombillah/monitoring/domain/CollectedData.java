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
	
	private Map<String, List<Long>> methodTracer = new HashMap<String, List<Long>>();
	private static CollectedData collectedData;
	

	private CollectedData() {
		// private Constructor.
	}
	
	public static CollectedData getInstance() {
		if(collectedData == null) {
			collectedData = new CollectedData();
		}
		return collectedData;
	}

	public Map<String, List<Long>> getMethodTracer() {
		return methodTracer;
	}

	public void setMethodTracer(Map<String, List<Long>> methodTracer) {
		this.methodTracer = methodTracer;
	}

}
