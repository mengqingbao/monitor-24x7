package com.ombillah.monitoring.domain;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * Domain class for hold search criteria.
 * 
 * @author Oussama M Billah
 * 
 */
public class SearchFilter extends BaseDomain {

	private static final long serialVersionUID = 1L;
	private List<String> methodSignatures;
	private Integer resolutionInSecs;
	private Long minExecTime;
	private Long maxExecTime;
	private Date minDate;
	private Date maxDate;
	private SimpleDateFormat dateFormat;

	public List<String> getMethodSignatures() {
		return methodSignatures;
	}

	public void setMethodSignatures(List<String> methodSignatures) {
		this.methodSignatures = methodSignatures;
	}

	public Integer getResolutionInSecs() {
		return resolutionInSecs;
	}

	public void setResolutionInSecs(Integer timeRangeinMins) {
		this.resolutionInSecs = timeRangeinMins;
	}

	public Long getMinExecTime() {
		return minExecTime;
	}

	public void setMinExecTime(Long minExecTime) {
		this.minExecTime = minExecTime;
	}

	public Long getMaxExecTime() {
		return maxExecTime;
	}

	public void setMaxExecTime(Long maxExecTime) {
		this.maxExecTime = maxExecTime;
	}

	public Date getMinDate() {
		return minDate;
	}

	public void setMinDate(Date minDate) {
		this.minDate = minDate;
	}

	public Date getMaxDate() {
		return maxDate;
	}

	public void setMaxDate(Date maxDate) {
		this.maxDate = maxDate;
	}

	public SimpleDateFormat getDateFormat() {
		return dateFormat;
	}

	public void setDateFormat(SimpleDateFormat dateFormat) {
		this.dateFormat = dateFormat;
	}

	/**
	 * @see java.lang.Object#equals(Object)
	 */
	@Override
	public boolean equals(Object object) {
		if (!(object instanceof SearchFilter)) {
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
