
package com.ombillah.monitoring.aspectj;

import com.google.inject.Injector;
import com.ombillah.monitoring.domain.CollectedData;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import com.ombillah.monitoring.bootstrap.Bootstrap;

public aspect SQLQueriesAspect {
	
	private CollectedData collectedData;
	private Map<PreparedStatement, String> sqlMap = new HashMap<PreparedStatement, String>();
	
	public SQLQueriesAspect() {
		bootstrap();		
	}

	private void bootstrap() {
		try {
			Injector injector = Bootstrap.init();
			collectedData = injector.getInstance(CollectedData.class);

		} catch (Throwable ex) {
			ex.printStackTrace();
		}
	}
	
	pointcut statementExec() : execution(public * java.sql.*Statement.execute*(..)) ;
	
	Object around() : statementExec() {

	    Long start = System.currentTimeMillis();
	    Object ret = proceed();
	    Long end = System.currentTimeMillis();
	  
	    if(collectedData == null) {
	    	bootstrap();
	    }
	    if(collectedData == null) {
	    	return ret;
	    }
	    
	    Long executionTime = (end-start);
	    
	    
	    Object[] args = thisJoinPoint.getArgs();
	    String sqlQuery = "";
	    if(args.length > 0) {
	    	sqlQuery = args[0].toString();
	    }
	    else {
	    	// in case of PreparedStatement.
	    	sqlQuery = sqlMap.get(thisJoinPoint.getTarget());
	    	sqlMap.remove(thisJoinPoint.getTarget());
	    }
	    
	    Statement statement = (Statement) thisJoinPoint.getTarget();
	    String catalog = "";
		try {
			catalog = statement.getConnection().getCatalog();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	    
	    if(StringUtils.isEmpty(sqlQuery) || StringUtils.equals(catalog, "24x7monitoring")) {
	    	// don't record empty SQLs or SQLs from Monitoring Aspects.
	    	return ret;
	    }
	    
	    // remove SQL Comments from query.
	    Pattern commentPattern = Pattern.compile("/\\*.*?\\*/", Pattern.DOTALL);
	    sqlQuery = commentPattern.matcher(sqlQuery).replaceAll("");
	    
		Map<String, List<Long>> tracers = collectedData.getTracer();
     	List<Long> execTimes = tracers.get("SQL||" + sqlQuery);
     	if(execTimes == null) {
     		execTimes = Collections.synchronizedList(new ArrayList<Long>());
     	}
     	execTimes.add(executionTime);
     	tracers.put("SQL||" + sqlQuery, execTimes);
     	collectedData.setTracer(tracers);
	   
	    return ret;
	  }
	
	  pointcut statementPrepare() : execution(public * java.sql.Connection.prepareStatement(..)) ;
	  
	  /**
	   * In case of Prepared Statement store the SQL query in a Map Use it in the around
	   * advice, as preparedStatements don't pass SQL query as parameter to execute method.
	   * @param statement
	   */
	  after() returning (PreparedStatement statement) : statementPrepare() {
		  Object[] args = thisJoinPoint.getArgs();
		  if(args.length > 0) {
			  sqlMap.put(statement, args[0].toString());
		  }
	  }
}
