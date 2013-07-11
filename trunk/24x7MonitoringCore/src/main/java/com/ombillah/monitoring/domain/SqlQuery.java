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
 * Domain object for SQL Queries table.
 * @author Oussama M Billah
 *
 */
@Entity
@Table(name="SQL_QUERIES")
public class SqlQuery extends BaseDomain {
	
	private static final long serialVersionUID = 1L;
	
	@Id
	@Column(name="SQL_QUERY")
	private String sqlQuery;
	
	public SqlQuery() {
		// default constructor.
	}
	
	public SqlQuery(String sqlQuery) {
		this.sqlQuery = sqlQuery;
	}
	
	public String getSqlQuery() {
		return sqlQuery;
	}

	public void setSqlQuery(String sqlQuery) {
		this.sqlQuery = sqlQuery;
	}


	@Override
	public boolean equals(Object object) {
		if (!(object instanceof SqlQuery)) {
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
