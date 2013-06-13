package com.ombillah.monitoring.domain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

public class MonitoredItem extends BaseDomain {

	private static final long serialVersionUID = 1L;
	private String itemName;
	private String fullName;
	private Map<String, MonitoredItem> subItemsMap = new HashMap<String, MonitoredItem>();
	private Collection<MonitoredItem> subItems = new ArrayList<MonitoredItem>();

	public Collection<MonitoredItem> getSubItems() {
		return subItems;
	}

	public void setSubItems(Collection<MonitoredItem> subItems) {
		this.subItems = subItems;
	}

	public MonitoredItem(String itemName) {
		this.itemName = itemName;
	}

	public String getItemName() {
		return itemName;
	}

	public void setItemName(String itemName) {
		this.itemName = itemName;
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	@Override
	public boolean equals(Object object) {
		if (!(object instanceof MonitoredItem)) {
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

	public void Push(String[] classes, int index) {
		if (index >= classes.length) {
			return;
		}
		MonitoredItem item = subItemsMap.get(classes[index]);
		if (item != null) {
			item.Push(classes, index + 1);
			return;
		}

		MonitoredItem newChild = new MonitoredItem(classes[index]);
		String fullName = getFullName(classes, index);
		newChild.setFullName(fullName);
		newChild.Push(classes, index + 1);
		subItemsMap.put(classes[index], newChild);
		subItems.add(newChild);
	}

	private String getFullName(String[] classes, int index) {
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i <= index; i++) {
			builder.append(classes[i]);
			if (i != index) {
				builder.append(".");
			}
		}
		return builder.toString();
	}

}
