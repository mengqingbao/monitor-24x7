package com.ombillah.monitoring.dao.impl;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import com.google.inject.persist.Transactional;
import com.ombillah.monitoring.dao.CollectorDAO;
import com.ombillah.monitoring.domain.ExceptionLogger;
import com.ombillah.monitoring.domain.HttpRequestUrl;
import com.ombillah.monitoring.domain.ManagedAlert;
import com.ombillah.monitoring.domain.MethodSignature;
import com.ombillah.monitoring.domain.MonitoredItemTracer;
import com.ombillah.monitoring.domain.ReportSchedule;
import com.ombillah.monitoring.domain.SqlQuery;


/**
 * Data Access Object for collecting performance data.
 * @author Oussama M Billah
 *
 */
public class CollectorDAOImpl implements CollectorDAO {
	
	@Inject
    private Provider<EntityManager> entityManager;
	
	@Transactional
	public List<MethodSignature> retrieveMethodSignatures() {
		String sql = "SELECT m FROM MethodSignature m ";
		List<MethodSignature> list = entityManager.get().createQuery(sql, MethodSignature.class).getResultList();
		return list; 
	}
	
	@Transactional
	public void saveMethodSignatures(List<MethodSignature> methodSignatures) {
		for(MethodSignature signature : methodSignatures) {
			MethodSignature result = entityManager.get().find(MethodSignature.class, signature.getMethodName());
			if(result == null) {
				entityManager.get().persist(signature);
			}	
		}
		
	}
	
	@Transactional
	public void saveMonitoredItemTracingStatistics(MonitoredItemTracer tracer) {
		entityManager.get().persist(tracer);
	}

	@Transactional
	public List<SqlQuery> retrieveSqlQueries() {
		String sql = "SELECT q FROM SqlQuery q ";
		List<SqlQuery> list = entityManager.get().createQuery(sql, SqlQuery.class).getResultList();
		return list;
	}

	@Transactional
	public void saveSqlQueries(List<SqlQuery> queries) {
		for(SqlQuery query : queries) {
			SqlQuery result = entityManager.get().find(SqlQuery.class, query.getSqlQuery());
			if(result == null) {
				entityManager.get().persist(query);
			}	
		}
		
	}

	@Transactional
	public void saveException(ExceptionLogger logger) {
		entityManager.get().persist(logger);
	}

	@Transactional
	public List<HttpRequestUrl> retrieveHttpRequestUrls() {
		String sql = "SELECT r FROM HttpRequestUrl r ";
		List<HttpRequestUrl> list = entityManager.get().createQuery(sql, HttpRequestUrl.class).getResultList();
		return list;
	}

	@Transactional
	public void saveHttpRequestUrls(List<HttpRequestUrl> requestUrls) {
		for(HttpRequestUrl request : requestUrls) {
			HttpRequestUrl result = entityManager.get().find(HttpRequestUrl.class, request.getRequestUrl());
			if(result == null) {
				entityManager.get().persist(request);
			}	
		}
		
	}

	@Transactional
	public List<ManagedAlert> getEnabledAlerts() {
		String sql = "SELECT alert FROM ManagedAlert alert where alert.enabled = 1 ";
		List<ManagedAlert> list = entityManager.get().createQuery(sql, ManagedAlert.class).getResultList();
		return list;
	}

	@Transactional
	public MonitoredItemTracer checkPerformanceDegredation(
			String monitoredItem, String itemType, Long timeToAlert,
			Long threshold) {
			
		String sql = "SELECT ITEM_NAME, ROUND(AVG(AVERAGE), 2) AS AVG, MAX(MAX) AS MX FROM MONITORED_ITEM_TRACER"
				+ " WHERE type = :type" 
				+ " AND ITEM_NAME = :name"
				+ " AND CREATION_DATE > DATE_SUB(NOW(), INTERVAL :timeToAlert MINUTE)" 
				+ " GROUP BY ITEM_NAME"
				+ " HAVING AVG > :threshold";
		
		try {
			Query query = entityManager.get().createNativeQuery(sql);
			query.setParameter("type", itemType);
			query.setParameter("name", monitoredItem);
			query.setParameter("timeToAlert", timeToAlert);
			query.setParameter("threshold", threshold);
			Object result =query.getSingleResult();
			Object[] resultArray = (Object[]) result;
			
			MonitoredItemTracer tracer = new MonitoredItemTracer();
			tracer.setItemName(resultArray[0].toString());
			tracer.setAverage(Double.valueOf(resultArray[1].toString()));
			tracer.setMax(Double.valueOf(resultArray[2].toString()));
			
			return tracer;
		} catch (NoResultException ex) {
			// ignore exception when no result is returned.
			return null;
		}
		
	}

	public List<ReportSchedule> getScheduledReports() {
		// TODO Auto-generated method stub
		return null;
	}


}
