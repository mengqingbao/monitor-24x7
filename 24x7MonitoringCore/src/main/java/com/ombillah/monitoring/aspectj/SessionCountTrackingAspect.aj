package com.ombillah.monitoring.aspectj;

import javax.servlet.http.HttpSession;

import com.google.inject.Injector;
import com.ombillah.monitoring.bootstrap.Bootstrap;
import com.ombillah.monitoring.domain.SessionTracker;

public aspect SessionCountTrackingAspect {

	private SessionTracker sessionTracker;
	
	public SessionCountTrackingAspect() {
		bootstrap();		
	}

	private void bootstrap() {
		try {
			Injector injector = Bootstrap.init();
			sessionTracker = injector.getInstance(SessionTracker.class);
			
		} catch (Throwable ex) {
			ex.printStackTrace();
		}
	}
	
	pointcut creationSession() : call(public * javax.servlet.http.HttpServletRequest.getSession(..)) ;
	
	  after() returning (HttpSession session) : creationSession() {
		  if(session != null && session.isNew()) {
			  session.setAttribute("24x7monitored", true);
			  sessionTracker.addSession(session);
		  }
		  
	  }
}
