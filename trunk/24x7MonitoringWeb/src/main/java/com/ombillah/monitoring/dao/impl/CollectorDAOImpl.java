package com.ombillah.monitoring.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.ombillah.monitoring.dao.CollectorDAO;
import com.ombillah.monitoring.domain.ExceptionLogger;
import com.ombillah.monitoring.domain.MethodSignature;
import com.ombillah.monitoring.domain.MonitoredItemTracer;
import com.ombillah.monitoring.domain.SearchFilter;


/**
 * Data Access Object for collecting performance data.
 * @author Oussama M Billah
 *
 */
@Repository
@Transactional(readOnly = true)
public class CollectorDAOImpl implements CollectorDAO {
	
	private JdbcTemplate jdbcTemplate;
	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Autowired
    public void setDataSource(BasicDataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }
	
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

	public List<MonitoredItemTracer> retrieveItemStatisticsGroupedByMonitoredItem(SearchFilter searchFilter) {
		List<Object> params = new ArrayList<Object>();
		int[] types = new int[0];
		
		String query = "SELECT ITEM_NAME, ROUND(SUM(AVERAGE * COUNT) / SUM(COUNT)) AS AVERAGE, " +
				" MIN(MIN) AS MIN, MAX(MAX) AS MAX, SUM(COUNT) AS COUNT " + 
				" FROM MONITORED_ITEM_TRACER " + 
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
			String itemName = methodSignatures.get(i);
			String[] queryTypes = {"SELECT", "UPDATE", "INSERT", "DELETE" };
			if(StringUtils.equals(itemName, "SQL")) {
				query += "TYPE = ? " ;
				params.add("SQL");
				types = ArrayUtils.add(types, Types.VARCHAR);
			} 
			else if(StringUtils.equals(itemName, "OTHER")) {
				query += "( TYPE = ? AND ITEM_NAME NOT LIKE ? AND ITEM_NAME NOT LIKE ? " ;
				query += "AND ITEM_NAME NOT LIKE ? AND ITEM_NAME NOT LIKE ? )";
				params.add("SQL");
				params.add("SELECT%");
				params.add("UPDATE%");
				params.add("DELETE%");
				params.add("INSERT%");
				types = ArrayUtils.add(types, Types.VARCHAR);
				types = ArrayUtils.add(types, Types.VARCHAR);
				types = ArrayUtils.add(types, Types.VARCHAR);
				types = ArrayUtils.add(types, Types.VARCHAR);
				types = ArrayUtils.add(types, Types.VARCHAR);
			}
			else if(ArrayUtils.contains(queryTypes, itemName)) {
				query += "(TYPE = ? AND ITEM_NAME LIKE ? ) " ;
				params.add("SQL");
				params.add(itemName + "%");
				types = ArrayUtils.add(types, Types.VARCHAR);
				types = ArrayUtils.add(types, Types.VARCHAR);
			}
			else if(StringUtils.equals(itemName, "HTTP Requests")) {
				query += "TYPE = ? " ;
				params.add("HTTP_REQUEST");
				types = ArrayUtils.add(types, Types.VARCHAR);
			} else {
				params.add(itemName + "%");
				types = ArrayUtils.add(types, Types.VARCHAR);
				query += "ITEM_NAME LIKE ? " ;
			}
			
			if(i != methodSignatures.size() - 1) {
				query += " OR ";
			}
		}
		query += " ) GROUP BY ITEM_NAME ORDER BY AVERAGE DESC";
		
		List<MonitoredItemTracer> result = this.jdbcTemplate.query(query, params.toArray(), types,
				 new RowMapper<MonitoredItemTracer>() {
           public MonitoredItemTracer mapRow(ResultSet rs, int rowNum) throws SQLException {
           		MonitoredItemTracer tracer = new MonitoredItemTracer();
				tracer.setItemName(rs.getString("ITEM_NAME"));
				tracer.setAverage(rs.getDouble("AVERAGE"));
				tracer.setCount(rs.getLong("COUNT"));
				tracer.setMax(rs.getDouble("MAX"));
				tracer.setMin(rs.getDouble("MIN"));
				return tracer;
           }
       });
		return result;
	}

	public List<MonitoredItemTracer> retrieveItemStatistics(SearchFilter searchFilter) {
		
		List<Object> params = new ArrayList<Object>();
		int[] types = new int[0];
		String query = "SELECT CREATION_DATE, ITEM_NAME, ROUND(SUM(AVERAGE * COUNT) / SUM(COUNT)) AS AVERAGE, " +
				" MIN(MIN) AS MIN, MAX(MAX) AS MAX, SUM(COUNT) AS COUNT " + 
				" FROM MONITORED_ITEM_TRACER " + 
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
			String itemName = methodSignatures.get(i);
			String[] queryTypes = {"SELECT", "UPDATE", "INSERT", "DELETE" };
			
			if(StringUtils.equals(itemName, "Memory")) {
				query += "TYPE = ? " ;
				params.add("MEMORY");
				types = ArrayUtils.add(types, Types.VARCHAR);
			}
			else if(StringUtils.equals(itemName, "Database Connections")) {
				query += "TYPE = ? " ;
				params.add("ACTIVE_CONNECTION");
				types = ArrayUtils.add(types, Types.VARCHAR);
			}
			else if(StringUtils.equals(itemName, "Active Sessions")) {
				query += "TYPE = ? " ;
				params.add("ACTIVE_SESSION");
				types = ArrayUtils.add(types, Types.VARCHAR);
			}
			else if(StringUtils.equals(itemName, "SQL")) {
				query += "TYPE = ? " ;
				params.add("SQL");
				types = ArrayUtils.add(types, Types.VARCHAR);
			}
			else if(StringUtils.equals(itemName, "OTHER")) {
				query += "( TYPE = ? AND ITEM_NAME NOT LIKE ? AND ITEM_NAME NOT LIKE ? " ;
				query += "AND ITEM_NAME NOT LIKE ? AND ITEM_NAME NOT LIKE ? )";
				params.add("SQL");
				params.add("SELECT%");
				params.add("UPDATE%");
				params.add("DELETE%");
				params.add("INSERT%");
				types = ArrayUtils.add(types, Types.VARCHAR);
				types = ArrayUtils.add(types, Types.VARCHAR);
				types = ArrayUtils.add(types, Types.VARCHAR);
				types = ArrayUtils.add(types, Types.VARCHAR);
				types = ArrayUtils.add(types, Types.VARCHAR);
			}
			else if(ArrayUtils.contains(queryTypes, itemName)) {
				query += "(TYPE = ? AND ITEM_NAME LIKE ? ) " ;
				params.add("SQL");
				params.add(itemName + "%");
				types = ArrayUtils.add(types, Types.VARCHAR);
				types = ArrayUtils.add(types, Types.VARCHAR);
			}
			else if(StringUtils.equals(itemName, "HTTP Requests")) {
				query += "TYPE = ? " ;
				params.add("HTTP_REQUEST");
				types = ArrayUtils.add(types, Types.VARCHAR);
			}
			else {
				params.add(itemName + "%");
				types = ArrayUtils.add(types, Types.VARCHAR);
				query += "ITEM_NAME LIKE ? " ;
			}
			
			if(i != methodSignatures.size() - 1) {
				query += " OR ";
			}
		}
		
		query += " ) GROUP BY UNIX_TIMESTAMP(CREATION_DATE) DIV " + searchFilter.getResolutionInSecs()
				+ ", ITEM_NAME ORDER BY ITEM_NAME, CREATION_DATE";
		
		List<MonitoredItemTracer> result = this.jdbcTemplate.query(query, params.toArray(), types,
				 new RowMapper<MonitoredItemTracer>() {
          public MonitoredItemTracer mapRow(ResultSet rs, int rowNum) throws SQLException {
          		MonitoredItemTracer tracer = new MonitoredItemTracer();
				tracer.setItemName(rs.getString("ITEM_NAME"));
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
	
	public List<String> retrieveSqlQueries() {
		String query = "SELECT DISTINCT SQL_QUERY FROM SQL_QUERIES";
		List<String> list = jdbcTemplate.queryForList(query, String.class);
		return list;
	}

	public List<ExceptionLogger> retrieveExceptionLoggers(SearchFilter searchFilter) {
		List<Object> params = new ArrayList<Object>();
		int[] types = new int[0];
		String query = "SELECT EXCEPTION_MESSAGE, STACKTRACE, COUNT(1) AS COUNT " +
				" FROM EXCEPTION_LOGGER " + 
				" WHERE CREATION_DATE BETWEEN ? AND ? " +
				" GROUP BY STACKTRACE";
			
		params.add(DATE_FORMAT.format(searchFilter.getMinDate()));
		params.add(DATE_FORMAT.format(searchFilter.getMaxDate()));
		types = ArrayUtils.add(types, Types.VARCHAR);
		types = ArrayUtils.add(types, Types.VARCHAR);
		
		List<ExceptionLogger> result = this.jdbcTemplate.query(query, params.toArray(), types,
				 new RowMapper<ExceptionLogger>() {
	          public ExceptionLogger mapRow(ResultSet rs, int rowNum) throws SQLException {
	        	  	ExceptionLogger logger = new ExceptionLogger();
	        	  	logger.setCount(rs.getInt("COUNT"));
	        	  	logger.setExceptionMessage(rs.getString("EXCEPTION_MESSAGE"));
	        	  	logger.setStacktrace(rs.getString("STACKTRACE"));
					return logger;
	          }
		});
		return result;
	}

	public List<String> retrieveHttpRequestUrls() {
		String query = "SELECT DISTINCT REQUEST FROM HTTP_REQUESTS";
		List<String> list = jdbcTemplate.queryForList(query, String.class);
		return list;
	}


}
