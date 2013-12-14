package com.ombillah.monitoring.domain;

import java.util.Date;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * Domain class to represent a method tracer.
 * 
 * @author Oussama M Billah
 * 
 */
public class MonitoredItemTracer extends BaseDomain {

	private static final long serialVersionUID = 1L;

	private Long id;
	private String itemName;
	private String type;
	private Double average;
	private Double max;
	private Double min;
	private Integer count;
	private Date creationDate;
	
	public MonitoredItemTracer() {
		// default constructor();
	}
	
	public MonitoredItemTracer(String itemName, String type, Double average, Double max,
			Double min, Integer count, Date creationDate) {

		this.itemName = itemName;
		this.type = type;
		this.average = average;
		this.max = max;
		this.min = min;
		this.count = count;
		this.creationDate = creationDate;
	}
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getItemName() {
		return itemName;
	}

	public void setItemName(String itemName) {
		this.itemName = itemName;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void setAverage(Double average) {
		this.average = average;
	}

	public double getAverage() {
		return average;
	}

	public void setAverage(double average) {
		this.average = average;
	}

	public Double getMax() {
		return max;
	}

	public void setMax(Double max) {
		this.max = max;
	}

	
	public Double getMin() {
		return min;
	}

	public void setMin(Double min) {
		this.min = min;
	}

	public Integer getCount() {
		return count;
	}

	public void setCount(Integer count) {
		this.count = count;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public boolean equals(Object object) {
		if (!(object instanceof MonitoredItemTracer)) {
			return false;
		}

		return EqualsBuilder.reflectionEquals(this, object);
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return ToStringBuilder.reflectionToString(this,
				ToStringStyle.DEFAULT_STYLE);
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}

}
