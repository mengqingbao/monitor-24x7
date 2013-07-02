package com.ombillah.monitoring.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

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
import com.ombillah.monitoring.domain.SearchFilter;


/**
 * Data Access Object for collecting performance data.
 * @author Oussama M Billah
 *
 */
@Repository
public class CollectorDAOImpl implements CollectorDAO {
	
	private JdbcTemplate jdbcTemplate;
	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

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
	public List<MethodTracer> retrieveMethodStatisticsGroupedByMethodName(SearchFilter searchFilter) {
		List<Object> params = new ArrayList<Object>();
		int[] types = new int[0];
		
		String query = "SELECT METHOD_NAME, ROUND(SUM(AVERAGE * COUNT) / SUM(COUNT)) AS AVERAGE, " +
				" MIN(MIN) AS MIN, MAX(MAX) AS MAX, SUM(COUNT) AS COUNT " + 
				" FROM METHOD_TRACER " + 
				" WHERE CREATION_DATE BETWEEN ? " +
				"	AND ? ";
		
		params.add(DATE_FORMAT.format(searchFilter.getMinDate()));
		params.add(DATE_FORMAT.format(searchFilter.getMaxDate()));
		types = ArrayUtils.add(types, Types.VARCHAR);
		types = ArrayUtils.add(types, Types.VARCHAR);
		
		if(searchFilter.getMinExecTime() != null) {
			query += " AND AVERAGE > ? ";
			params.add(searchFilter.getMinExecTime());
			types = ArrayUtils.add(types, Types.DOUBLE);
		}
		
		if(searchFilter.getMaxExecTime() != null) {
			query += " AND AVERAGE < ? ";
			params.add(searchFilter.getMaxExecTime());
			types = ArrayUtils.add(types, Types.DOUBLE);
		}
		query += "  AND ( ";
		
		List<String> methodSignatures = searchFilter.getMethodSignatures();
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

	@Transactional(readOnly = true)
	public List<MethodTracer> retrieveMethodStatistics(SearchFilter searchFilter) {
		
		List<Object> params = new ArrayList<Object>();
		int[] types = new int[0];
		String query = "SELECT CREATION_DATE, METHOD_NAME, ROUND(SUM(AVERAGE * COUNT) / SUM(COUNT)) AS AVERAGE, " +
				" MIN(MIN) AS MIN, MAX(MAX) AS MAX, SUM(COUNT) AS COUNT " + 
				" FROM METHOD_TRACER " + 
				" WHERE CREATION_DATE BETWEEN ? " +
				"	AND ? ";
			
		params.add(DATE_FORMAT.format(searchFilter.getMinDate()));
		params.add(DATE_FORMAT.format(searchFilter.getMaxDate()));
		types = ArrayUtils.add(types, Types.VARCHAR);
		types = ArrayUtils.add(types, Types.VARCHAR);
		
		if(searchFilter.getMinExecTime() != null) {
			query += " AND AVERAGE > ? ";
			params.add(searchFilter.getMinExecTime());
			types = ArrayUtils.add(types, Types.DOUBLE);
		}
		
		if(searchFilter.getMaxExecTime() != null) {
			query += " AND AVERAGE < ? ";
			params.add(searchFilter.getMaxExecTime());
			types = ArrayUtils.add(types, Types.DOUBLE);
		}
		query += "  AND ( ";
		
		List<String> methodSignatures = searchFilter.getMethodSignatures();
		for(int i = 0; i < methodSignatures.size(); i++) {
			String methodName = methodSignatures.get(i);
			params.add(methodName + "%");
			types = ArrayUtils.add(types, Types.VARCHAR);
			query += "METHOD_NAME LIKE ? " ;
			if(i != methodSignatures.size() - 1) {
				query += " OR ";
			}
		}
		
		query += " ) GROUP BY UNIX_TIMESTAMP(CREATION_DATE) DIV " + searchFilter.getResolutionInSecs()
				+ ", METHOD_NAME ORDER BY METHOD_NAME";
		
		List<MethodTracer> result = this.jdbcTemplate.query(query, params.toArray(), types,
				 new RowMapper<MethodTracer>() {
          public MethodTracer mapRow(ResultSet rs, int rowNum) throws SQLException {
          		MethodTracer tracer = new MethodTracer();
				tracer.setMethodName(rs.getString("METHOD_NAME"));
				tracer.setAverage(rs.getDouble("AVERAGE"));
				tracer.setCount(rs.getLong("COUNT"));
				tracer.setMax(rs.getDouble("MAX"));
				tracer.setMin(rs.getDouble("MIN"));
				tracer.setCreationDate(rs.getTimestamp("CREATION_DATE"));

				return tracer;
          }
      });
		
		return result;
	}

}
