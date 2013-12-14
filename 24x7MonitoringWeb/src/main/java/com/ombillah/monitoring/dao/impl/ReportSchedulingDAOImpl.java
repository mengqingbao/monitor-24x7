package com.ombillah.monitoring.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.ombillah.monitoring.dao.ReportSchedulingDAO;
import com.ombillah.monitoring.domain.ReportContent;
import com.ombillah.monitoring.domain.ReportSchedule;


/**
 * DAO for Report Scheduling Data Access logic.
 * @author Oussama M Billah
 *
 */
@Repository
@Transactional(readOnly = true)
public class ReportSchedulingDAOImpl implements ReportSchedulingDAO {

	private JdbcTemplate jdbcTemplate;
	
	private static final int SECONDS_IN_15_MINS = 900;
	private static final int SECONDS_IN_4_HOURS = 14400;
	private static final int SECONDS_IN_DAY = 86400;
	private static final int SECONDS_IN_WEEK = 604800;
	
	private static final int ONE_HOUR = 1;
	private static final int HOURS_IN_DAY = 24;
	private static final int HOURS_IN_WEEK = 189;
	private static final int HOURS_IN_MONTH = 720;
	
	@Resource(name="H2DataSource")
    public void setDataSource(BasicDataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }
	
    
	@Transactional(readOnly = false)
	public void saveReport(ReportSchedule report) {
		int enabled = report.isEnabled() ? 1 : 0; 
		
		ReportSchedule existingReport = this.retrieveReport(report.getItemName(), report.getItemType());
		String sql = "";
		if(existingReport == null) {
			sql = "INSERT INTO REPORT_SCHEDULE (ITEM_NAME, ITEM_TYPE, FREQUENCY, DAY_OF_MONTH, DAY_OF_WEEK, REPORT_HOUR," +
					" REPORT_MINUTE, REPORT_EMAIL,  ENABLED)" +
					"	VALUES ('%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s');";  
			
			sql = String.format(sql, report.getItemName(), report.getItemType(), report.getFrequency(), report.getDayOfMonth(),
					report.getDayOfWeek(), report.getHour(), report.getMinute(), report.getReportEmail(), enabled);

		} else {
			sql = "UPDATE REPORT_SCHEDULE SET " +
						" FREQUENCY = '%s', DAY_OF_MONTH = '%s', DAY_OF_WEEK = '%s', REPORT_HOUR = '%s'," + 
						" REPORT_MINUTE = '%s', REPORT_EMAIL = '%s', ENABLED = '%s' " +
						" WHERE ITEM_NAME = '%s' AND ITEM_TYPE = '%s' ;";
			
			sql = String.format(sql, report.getFrequency(), report.getDayOfMonth(), report.getDayOfWeek(), report.getHour(), 
					report.getMinute(), report.getReportEmail(), enabled, report.getItemName(), report.getItemType());
		}
		
		jdbcTemplate.execute(sql);

	}

	@Transactional(readOnly = true)
	public ReportSchedule retrieveReport(String itemName, String itemType) {
		List<String> params = new ArrayList<String>();
		int[] types = new int[0];
		String query = "SELECT * " +
				" FROM REPORT_SCHEDULE " + 
				" WHERE ITEM_NAME = ? " +
				" AND ITEM_TYPE = ? ;";
			
		params.add(itemName);
		params.add(itemType);
		types = ArrayUtils.add(types, Types.VARCHAR);
		types = ArrayUtils.add(types, Types.VARCHAR);
		
		List<ReportSchedule> result = this.jdbcTemplate.query(query, params.toArray(), types,
			new ReportRowMapper());
		
		if(result.isEmpty()) {
			return null;
		}
		return result.get(0);
	}


	@Transactional(readOnly = true)
	public List<ReportSchedule> getEnabledReports() {

		String query = "SELECT * " +
				" FROM REPORT_SCHEDULE " + 
				" WHERE ENABLED = 1;";
					
		List<ReportSchedule> result = this.jdbcTemplate.query(query, new ReportRowMapper());
		return result;
	}


	@Transactional(readOnly = true)
	public List<ReportContent> getReportContent(ReportSchedule report) {
		String frequency = report.getFrequency();
		String dateFormat = "%H %i";
		Integer timeRange = SECONDS_IN_15_MINS;
		Integer timeInterval = ONE_HOUR;
		
		if(StringUtils.equals("daily", frequency)) {
			dateFormat = "%b %D %H:00";
			timeRange = SECONDS_IN_4_HOURS;
			timeInterval = HOURS_IN_DAY;
		}
		else if(StringUtils.equals("weekly", frequency)) {
			dateFormat = "%M %D";
			timeRange = SECONDS_IN_DAY;
			timeInterval = HOURS_IN_WEEK;
		}
		else if(StringUtils.equals("monthly", frequency)) {
			dateFormat = "%b %D";
			timeRange = SECONDS_IN_WEEK;
			timeInterval = HOURS_IN_MONTH;
		}
		
		String sql = "SELECT DATE_FORMAT(CREATION_DATE, ?) AS CREATION_DATE " +
				" ,ITEM_NAME" + 
				" ,ROUND(AVG(AVERAGE), 2) AS AVERAGE " + 
				" ,MAX(MAX) AS MAX " + 
				" ,MIN(MIN) AS MIN " + 
				" ,SUM(COUNT) AS COUNT " + 
				" FROM MONITORED_ITEM_TRACER" + 
				" WHERE ITEM_NAME = ? AND  CREATION_DATE > DATE_SUB(NOW(), INTERVAL ? HOUR) " + 
				" GROUP BY ROUND(DATEDIFF(SECOND, '1970-01-01', CREATION_DATE) / ?)" + 
				" ORDER BY MONITORED_ITEM_TRACER.CREATION_DATE ASC";
		
		List<Object> params = new ArrayList<Object>();
		int[] types = new int[0];
		params .add(dateFormat);
		params.add(report.getItemName());
		params .add(timeInterval);
		params .add(timeRange);
		types = ArrayUtils.add(types, Types.VARCHAR);
		types = ArrayUtils.add(types, Types.VARCHAR);
		types = ArrayUtils.add(types, Types.INTEGER);
		types = ArrayUtils.add(types, Types.INTEGER);
		
		List<ReportContent> result = this.jdbcTemplate.query(sql, params.toArray(), types,
			new RowMapper<ReportContent>() {

				public ReportContent mapRow(ResultSet rs, int rowNum)
						throws SQLException {
					
					ReportContent report = new ReportContent();
					report.setReportTime(rs.getString("CREATION_DATE"));
					report.setItemName(rs.getString("ITEM_NAME"));
					report.setAverage(rs.getDouble("AVERAGE"));
					report.setCount(rs.getInt("COUNT"));
					report.setMax(rs.getDouble("MAX"));
					report.setMin(rs.getDouble("MIN"));
					return report;
				}
		});
		
		return result;
	}
	
	
	class ReportRowMapper implements RowMapper<ReportSchedule> {

		public ReportSchedule mapRow(ResultSet rs, int rowNum) throws SQLException {
			int enabledInt = rs.getInt("ENABLED");
			boolean enabled = enabledInt == 1 ? true : false;
			
			ReportSchedule report = new ReportSchedule();
			report.setItemName(rs.getString("ITEM_NAME"));
        	report.setItemType(rs.getString("ITEM_TYPE"));
        	report.setFrequency(rs.getString("FREQUENCY"));
        	report.setDayOfMonth(rs.getInt("DAY_OF_MONTH"));
        	report.setDayOfWeek(rs.getInt("DAY_OF_WEEK"));
        	report.setHour(rs.getInt("REPORT_HOUR"));
        	report.setMinute(rs.getInt("REPORT_MINUTE"));
        	report.setReportEmail(rs.getString("REPORT_EMAIL"));
        	report.setEnabled(enabled);
        	
            return report;
		}
		
	}

}
