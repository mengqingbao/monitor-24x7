package com.ombillah.monitoring.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * Domain to hold singleton Collected Data.
 * @author Oussama M Billah
 *
 */
public class CollectedData extends BaseDomain {
	
	private static final long serialVersionUID = 1L;
	
	private Map<String, List<Long>> tracer = new HashMap<String, List<Long>>();
	private List<ExceptionLogger> loggedExceptions = new ArrayList<ExceptionLogger>();

	public Map<String, List<Long>> getTracer() {
		return tracer;
	}

	public void setTracer(Map<String, List<Long>> tracer) {
		this.tracer = tracer;
	}
	
	public void clearTracer() {
		tracer.clear();
	}
	
	public List<ExceptionLogger> getLoggedExceptions() {
		return loggedExceptions;
	}

	public void addExceptionLogger(ExceptionLogger exception) {
		loggedExceptions.add(exception);
	}
	
	public void cleaLoggedExceptions() {
		loggedExceptions.clear();
	}
	
	@Override
	public boolean equals(Object object) {
		if (!(object instanceof CollectedData)) {
			return false;
		}

		return EqualsBuilder.reflectionEquals(this, object);
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this,
				ToStringStyle.DEFAULT_STYLE);
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}

}
