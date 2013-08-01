package com.ombillah.monitoring.domain;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.http.HttpSession;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

public class SessionTracker extends BaseDomain {


	private static final long serialVersionUID = 1L;
	private Map<String, HttpSession> sessions = new ConcurrentHashMap<String, HttpSession>();

	public Map<String, HttpSession> getSessions() {
		return sessions;
	}

	public void setSessions(Map<String, HttpSession> sessions) {
		this.sessions = sessions;
	}
	
	public void addSession(HttpSession session) {
		this.sessions.put(session.getId(), session);
	}
	
	public void removeSession(HttpSession session) {
		this.sessions.remove(session.getId());
	}
	
	public int getActiveSessionCountAndClearExpiredSessions() {
		int sessionCount  = 0;
		for(HttpSession session : sessions.values()) {
			try {
				if(session.getAttribute("24x7monitored") != null) {
					sessionCount++;
				}
				else {
					sessions.remove(session);
				}
			} catch (Exception ex) {
				sessions.remove(session);
			}
			
		}
		return sessionCount;
	}
	
	@Override
	public boolean equals(Object object) {
		if (!(object instanceof SessionTracker)) {
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
