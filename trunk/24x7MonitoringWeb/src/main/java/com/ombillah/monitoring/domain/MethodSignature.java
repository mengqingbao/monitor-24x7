package com.ombillah.monitoring.domain;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * Domain object for method signatures table.
 * @author Oussama M Billah
 *
 */
public class MethodSignature extends BaseDomain {
	
	private static final long serialVersionUID = 1L;
	
	private String itemName;
	
	public MethodSignature() {
		// default constructor.
	}
	
	public MethodSignature(String itemName) {
		this.itemName = itemName;
	}
	
	public String getItemName() {
		return itemName;
	}

	public void setMethodName(String itemName) {
		this.itemName = itemName;
	}


	@Override
	public boolean equals(Object object) {
		if (!(object instanceof MethodSignature)) {
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
