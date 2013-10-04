package com.ombillah.monitoring.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * Domain class for Alerts.
 * @author Oussama M Billah
 *
 */
@Entity
@Table(name="MANAGED_ALERTS")
public class ManagedAlert extends BaseDomain {

	private static final long serialVersionUID = 1L;
	@Id
	private Integer id;

	@Column(name="ITEM_NAME")
	private String itemName;
	
	@Column(name="ITEM_TYPE")
	private String itemType;
	
	@Column
	private Long threshold;
	
	@Column(name="TIME_TO_ALERT_IN_MINS")
	private Long timeToAlertInMins;
	
	@Column(name="ALERT_EMAIL")
	private String alertEmail;
	
	@Column
	private boolean enabled;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getItemName() {
		return itemName;
	}

	public void setItemName(String itemName) {
		this.itemName = itemName;
	}

	public String getItemType() {
		return itemType;
	}

	public void setItemType(String itemType) {
		this.itemType = itemType;
	}

	public Long getThreshold() {
		return threshold;
	}

	public void setThreshold(Long threshold) {
		this.threshold = threshold;
	}

	public Long getTimeToAlertInMins() {
		return timeToAlertInMins;
	}

	public void setTimeToAlertInMins(Long timeToAlertInMins) {
		this.timeToAlertInMins = timeToAlertInMins;
	}

	public String getAlertEmail() {
		return alertEmail;
	}

	public void setAlertEmail(String alertEmail) {
		this.alertEmail = alertEmail;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	@Override
	public boolean equals(Object object) {
		if (!(object instanceof ManagedAlert)) {
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
