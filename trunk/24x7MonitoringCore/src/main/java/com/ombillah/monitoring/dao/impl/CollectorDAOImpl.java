package com.ombillah.monitoring.dao.impl;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.persistence.EntityManager;

import org.hibernate.ejb.HibernateEntityManagerFactory;
import org.hibernate.internal.SessionFactoryImpl;
import org.hibernate.service.jdbc.connections.internal.DatasourceConnectionProviderImpl;

import com.google.inject.persist.Transactional;
import com.ombillah.monitoring.dao.CollectorDAO;
import com.ombillah.monitoring.domain.ExceptionLogger;
import com.ombillah.monitoring.domain.MethodSignature;
import com.ombillah.monitoring.domain.MonitoredItemTracer;
import com.ombillah.monitoring.domain.SqlQuery;


/**
 * Data Access Object for collecting performance data.
 * @author Oussama M Billah
 *
 */
public class CollectorDAOImpl implements CollectorDAO {
	
	@Inject
    private Provider<EntityManager> entityManager;
	
	@SuppressWarnings("unchecked")
	@Transactional
	public List<MethodSignature> retrieveMethodSignatures() {
		String sql = "SELECT m FROM MethodSignature m ";
		List<MethodSignature> list = entityManager.get().createQuery(sql).getResultList();
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
	public void saveMonitoredItemTracingStatistics(List<MonitoredItemTracer> monitoredItemTracers) {
		for(MonitoredItemTracer tracer : monitoredItemTracers) {
			entityManager.get().persist(tracer);
		}
	}

	@Transactional
	@SuppressWarnings("unchecked")
	public List<SqlQuery> retrieveSqlQueries() {
		String sql = "SELECT q FROM SqlQuery q ";
		List<SqlQuery> list = entityManager.get().createQuery(sql).getResultList();
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

}
