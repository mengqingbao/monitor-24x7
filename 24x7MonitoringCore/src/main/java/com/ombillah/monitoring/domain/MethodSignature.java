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
 * Domain object for method signatures table.
 * @author Oussama M Billah
 *
 */
@Entity
@Table(name="METHOD_SIGNATURES")
public class MethodSignature extends BaseDomain {
	
	private static final long serialVersionUID = 1L;
	
	@Id
	@Column(name="METHOD_SIGNATURE")
	private String methodName;
	
	public MethodSignature() {
		// default constructor.
	}
	
	public MethodSignature(String methodName) {
		this.methodName = methodName;
	}
	
	public String getMethodName() {
		return methodName;
	}

	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}


	@Override
	public boolean equals(Object object) {
		if (!(object instanceof MethodTracer)) {
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
