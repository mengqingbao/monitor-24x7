package com.ombillah.monitoring.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.commons.lang.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.ombillah.monitoring.dao.CollectorDAO;
import com.ombillah.monitoring.domain.MethodSignature;
import com.ombillah.monitoring.domain.MethodTracer;


/**
 * Data Access Object for collecting performance data.
 * @author Oussama M Billah
 *
 */
@Repository
public class CollectorDAOImpl implements CollectorDAO {
	
	private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(BasicDataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }
	
	@Transactional(readOnly = true)
	public List<MethodSignature> retrieveMethodSignatures() {
		
		List<MethodSignature> list = this.jdbcTemplate.query(
	        "SELECT METHOD_SIGNATURE FROM METHOD_SIGNATURES",
	        new RowMapper<MethodSignature>() {
	            public MethodSignature mapRow(ResultSet rs, int rowNum) throws SQLException {
	            	MethodSignature signature = new MethodSignature();
	            	signature.setMethodName(rs.getString("METHOD_SIGNATURE"));
	                return signature;
	            }
	        });
			
		return list;
	}

	@Transactional(readOnly = true)
	public List<MethodTracer> retrieveMethodStatisticsGroupedByMethodName(
			List<String> methodSignatures, 
			Long minExecTime, Long maxExecTime,
			Date minDate, Date maxDate) {
		List<Object> params = new ArrayList<Object>();
		int[] types = new int[0];
		
		String query = "SELECT METHOD_NAME, ROUND(SUM(AVERAGE * COUNT) / SUM(COUNT)) AS AVERAGE, " +
				" MIN(MIN) AS MIN, MAX(MAX) AS MAX, SUM(COUNT) AS COUNT " + 
				" FROM METHOD_TRACER " + 
				" WHERE CREATION_DATE BETWEEN ? " +
				"	AND ? ";
		
		params.add(minDate);
		params.add(maxDate);
		types = ArrayUtils.add(types, Types.DATE);
		types = ArrayUtils.add(types, Types.DATE);
		
		if(minExecTime != null) {
			query += " AND AVERAGE > ? ";
			params.add(minExecTime);
			types = ArrayUtils.add(types, Types.DOUBLE);
		}
		
		if(maxExecTime != null) {
			query += " AND AVERAGE < ? ";
			params.add(maxExecTime);
			types = ArrayUtils.add(types, Types.DOUBLE);
		}
		query += "  AND ( ";
		for(int i = 0; i < methodSignatures.size(); i++) {
			String methodName = methodSignatures.get(i);
			params.add(methodName + "%");
			types = ArrayUtils.add(types, Types.VARCHAR);
			query += "METHOD_NAME LIKE ? " ;
			if(i != methodSignatures.size() - 1) {
				query += " OR ";
			}
		}
		query += " ) GROUP BY METHOD_NAME ORDER BY AVERAGE DESC";
		
		List<MethodTracer> result = this.jdbcTemplate.query(query, params.toArray(), types,
				 new RowMapper<MethodTracer>() {
           public MethodTracer mapRow(ResultSet rs, int rowNum) throws SQLException {
           	MethodTracer tracer = new MethodTracer();
				tracer.setMethodName(rs.getString("METHOD_NAME"));
				tracer.setAverage(rs.getDouble("AVERAGE"));
				tracer.setCount(rs.getLong("COUNT"));
				tracer.setMax(rs.getDouble("MAX"));
				tracer.setMin(rs.getDouble("MIN"));
				return tracer;
           }
       });
		return result;
	}

	public List<MethodTracer> retrieveMethodStatistics(
			List<String> methodSignatures, Long minExecTime, Long maxExecTime,
			Date minDate, Date maxDate) {
		
		List<Object> params = new ArrayList<Object>();
		int[] types = new int[0];
		
		String query = "SELECT ID, METHOD_NAME, AVERAGE, COUNT, MAX, MIN, CREATION_DATE" + 
				" FROM METHOD_TRACER " + 
				" WHERE CREATION_DATE BETWEEN ? " +
				"	AND ? ";
		
		params.add(minDate);
		params.add(maxDate);
		types = ArrayUtils.add(types, Types.DATE);
		types = ArrayUtils.add(types, Types.DATE);
		
		if(minExecTime != null) {
			query += " AND AVERAGE > ? ";
			params.add(minExecTime);
			types = ArrayUtils.add(types, Types.DOUBLE);
		}
		
		if(maxExecTime != null) {
			query += " AND AVERAGE < ? ";
			params.add(maxExecTime);
			types = ArrayUtils.add(types, Types.DOUBLE);
		}
		query += "  AND ( ";
		for(int i = 0; i < methodSignatures.size(); i++) {
			String methodName = methodSignatures.get(i);
			params.add(methodName + "%");
			types = ArrayUtils.add(types, Types.VARCHAR);
			query += "METHOD_NAME LIKE ? " ;
			if(i != methodSignatures.size() - 1) {
				query += " OR ";
			}
		}
		query += " )";
		

		List<MethodTracer> result = this.jdbcTemplate.query(query, params.toArray(), types,
				 new RowMapper<MethodTracer>() {
            public MethodTracer mapRow(ResultSet rs, int rowNum) throws SQLException {
            	MethodTracer tracer = new MethodTracer();
				tracer.setMethodName(rs.getString("METHOD_NAME"));
				tracer.setAverage(rs.getDouble("AVERAGE"));
				tracer.setCount(rs.getLong("COUNT"));
				tracer.setMax(rs.getDouble("MAX"));
				tracer.setMin(rs.getDouble("MIN"));
				tracer.setCreationDate(rs.getDate("CREATION_DATE"));
				return tracer;
            }
        });
		return result;
	}

}
