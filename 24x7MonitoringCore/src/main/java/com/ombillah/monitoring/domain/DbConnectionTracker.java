package com.ombillah.monitoring.domain;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

public class DbConnectionTracker extends BaseDomain {


	private static final long serialVersionUID = 1L;
	private Map<Connection, Connection> connections = new ConcurrentHashMap<Connection, Connection>();
	private List<Integer> connectionCounts = new ArrayList<Integer>();
	
	public List<Integer> getConnectionCounts() {
		return connectionCounts;
	}

	public void addConnection(Connection connection) {
		this.connections.put(connection, connection);
	}
	
	public void addConnectionCount(Integer count) {
		this.connectionCounts.add(count);
	}
	
	public void clearConnectionCountList() {
		this.connectionCounts.clear();
	}
	
	public int getActiveConnectionCountAndClearClosedSessions() {		
		int connectionCount  = 0;
		for(Connection connection : connections.values()) {
			try {
				if(!connection.isClosed()) {
					connectionCount++;
				}
				else {
					connections.remove(connection);
				}
			} catch (Exception ex) {
				connections.remove(connection);
			}
			
		}
		return connectionCount;
	}
	 	
	@Override
	public boolean equals(Object object) {
		if (!(object instanceof DbConnectionTracker)) {
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
