package com.ombillah.monitoring.domain;

import java.util.List;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

public class TracingFilter extends BaseDomain {

	private static final long serialVersionUID = 1L;
	private Integer timeRangeInMins;
	private Integer resolutionInSecs;
	private String fromRange;
	private String toRange;
	private List<String> searchedItems;

	public Integer getTimeRangeInMins() {
		return timeRangeInMins;
	}

	public void setTimeRangeInMins(Integer timeRangeInMins) {
		this.timeRangeInMins = timeRangeInMins;
	}

	public Integer getResolutionInSecs() {
		return resolutionInSecs;
	}

	public void setResolutionInSecs(Integer resolutionInSecs) {
		this.resolutionInSecs = resolutionInSecs;
	}

	public String getFromRange() {
		return fromRange;
	}

	public void setFromRange(String fromRange) {
		this.fromRange = fromRange;
	}

	public String getToRange() {
		return toRange;
	}

	public void setToRange(String toRange) {
		this.toRange = toRange;
	}

	public List<String> getSearchedItems() {
		return searchedItems;
	}

	public void setSearchedItems(List<String> searchedItems) {
		this.searchedItems = searchedItems;
	}

	/**
	 * @see java.lang.Object#equals(Object)
	 */
	@Override
	public boolean equals(Object object) {
		if (!(object instanceof TracingFilter)) {
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
