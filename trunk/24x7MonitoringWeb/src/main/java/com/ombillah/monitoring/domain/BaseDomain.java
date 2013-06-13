package com.ombillah.monitoring.domain;

import java.io.Serializable;

/**
 * Base abstract domain to ensure overriding default methods.
 * @author  Oussama M Billah
 *
 */
public abstract class BaseDomain implements Serializable {

	private static final long serialVersionUID = 1L;

	public abstract String toString();
	public abstract boolean equals(Object o);
	public abstract int hashCode();
	

}
