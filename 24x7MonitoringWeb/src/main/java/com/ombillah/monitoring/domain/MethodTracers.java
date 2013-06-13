package com.ombillah.monitoring.domain;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

public class MethodTracers extends BaseDomain {

	private static final long serialVersionUID = 1L;
	private Map<String, List<MethodTracer>> tracersByResolution;
	private List<MethodTracer> tracersGrouped;


	public Map<String, List<MethodTracer>> getTracersByResolution() {
		return tracersByResolution;
	}

	public void setTracersByResolution(Map<String, List<MethodTracer>> tracersByResolution) {
		this.tracersByResolution = tracersByResolution;
	}

	public List<MethodTracer> getTracersGrouped() {
		return tracersGrouped;
	}

	public void setTracersGrouped(List<MethodTracer> tracersGrouped) {
		this.tracersGrouped = tracersGrouped;
	}

	@Override
	public boolean equals(Object object) {
		if (!(object instanceof MethodTracers)) {
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
