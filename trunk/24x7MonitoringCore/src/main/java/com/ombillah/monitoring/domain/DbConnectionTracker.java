package com.ombillah.monitoring.domain;

import java.sql.Connection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

public class DbConnectionTracker extends BaseDomain {


	private static final long serialVersionUID = 1L;
	private Map<String, Connection> connections = new ConcurrentHashMap<String, Connection>();

	public Map<String, Connection> getConnections() {
		return connections;
	}

	public void setConnections(Map<String, Connection> connections) {
		this.connections = connections;
	}
	
	public void addConnection(Connection connection) {
		//this.connections.put(connection.get, session);
	}
	
	public void removeConnection(Connection session) {
		//this.connections.remove(session.getId());
	}
	
	public int getActiveSessionCountAndClearExpiredSessions() {
		int sessionCount  = 0;
//		for(HttpSession session : connections.values()) {
//			try {
//				if(session.getAttribute("24x7monitored") != null) {
//					sessionCount++;
//				}
//				else {
//					connections.remove(session);
//				}
//			} catch (Exception ex) {
//				connections.remove(session);
//			}
//			
//		}
		return sessionCount;
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
