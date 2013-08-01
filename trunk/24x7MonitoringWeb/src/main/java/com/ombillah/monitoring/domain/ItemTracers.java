package com.ombillah.monitoring.domain;

import java.util.List;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

public class ItemTracers extends BaseDomain {

	private static final long serialVersionUID = 1L;
	private List<MonitoredItemTracer> monitoredItemTracersGrouped;
	

	public List<MonitoredItemTracer> getMonitoredItemTracersGrouped() {
		return monitoredItemTracersGrouped;
	}

	public void setMonitoredItemTracersGrouped(List<MonitoredItemTracer>monitoredItemTracersGrouped) {
		this.monitoredItemTracersGrouped = monitoredItemTracersGrouped;
	}

	@Override
	public boolean equals(Object object) {
		if (!(object instanceof ItemTracers)) {
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
