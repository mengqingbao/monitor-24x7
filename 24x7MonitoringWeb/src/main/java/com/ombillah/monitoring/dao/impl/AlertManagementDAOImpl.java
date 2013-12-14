package com.ombillah.monitoring.dao.impl;


import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.commons.lang.ArrayUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.ombillah.monitoring.dao.AlertManagementDAO;
import com.ombillah.monitoring.domain.ManagedAlert;

/**
 * DAO to management Alert Management Data Access logic.
 * @author Oussama M Billah
 *
 */
@Repository
@Transactional(readOnly = true)
public class AlertManagementDAOImpl implements AlertManagementDAO {

	private JdbcTemplate jdbcTemplate;

    @Resource(name="H2DataSource")
    public void setDataSource(BasicDataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }
	
    
	@Transactional(readOnly = false)
	public void saveAlert(ManagedAlert alert) {
		int enabled = alert.isEnabled() ? 1 : 0; 
		
		ManagedAlert existingAlert = this.retrieveAlert(alert.getItemName(), alert.getItemType());
		String sql = "";
		if(existingAlert == null) {
			sql = "INSERT INTO MANAGED_ALERTS (ITEM_NAME, ITEM_TYPE, THRESHOLD, TIME_TO_ALERT_IN_MINS, ALERT_EMAIL, ENABLED)" +
					"	VALUES ('" + alert.getItemName() + "', '" + alert.getItemType() + "', '" + alert.getThreshold() +
					"', '" + alert.getTimeToAlertInMins() + "', '" + alert.getAlertEmail() + "', '" + enabled + "');";  
		} else {
			sql = "UPDATE MANAGED_ALERTS SET " +
						"   THRESHOLD = '" + alert.getThreshold() + 
						"', TIME_TO_ALERT_IN_MINS = '" + alert.getTimeToAlertInMins() + 
						"', ALERT_EMAIL = '" + alert.getAlertEmail() + 
						"', ENABLED = '" + enabled + "' WHERE ITEM_NAME = '" + alert.getItemName() + 
						"' AND ITEM_TYPE = '" + alert.getItemType() + "';";
		}
		
		jdbcTemplate.execute(sql);
	}

	@Transactional
	public ManagedAlert retrieveAlert(String itemName, String itemType) {
		List<String> params = new ArrayList<String>();
		int[] types = new int[0];
		String query = "SELECT * " +
				" FROM MANAGED_ALERTS " + 
				" WHERE ITEM_NAME = ? " +
				" AND ITEM_TYPE = ? ;";
			
		params.add(itemName);
		params.add(itemType);
		types = ArrayUtils.add(types, Types.VARCHAR);
		types = ArrayUtils.add(types, Types.VARCHAR);
		
		List<ManagedAlert> result = this.jdbcTemplate.query(query, params.toArray(), types,
			new AlertRowMapper());
		
		if(result.isEmpty()) {
			return null;
		}
		return result.get(0);
	}


	@Transactional
	public List<ManagedAlert> getEnabledAlerts() {
		String sql = "SELECT * FROM MANAGED_ALERTS WHERE ENABLED = 1";
		List<ManagedAlert> result = this.jdbcTemplate.query(sql, new AlertRowMapper());
		return result;
	}
	
	private class AlertRowMapper implements RowMapper<ManagedAlert> {

		public ManagedAlert mapRow(ResultSet rs, int rowNum)
				throws SQLException {
			
			int enabledInt = rs.getInt("ENABLED");
			boolean enabled = enabledInt == 1 ? true : false;
			
			ManagedAlert alert = new ManagedAlert();
			alert.setItemName(rs.getString("ITEM_NAME"));
        	alert.setItemType(rs.getString("ITEM_TYPE"));
        	alert.setAlertEmail(rs.getString("ALERT_EMAIL"));
        	alert.setEnabled(enabled);
        	alert.setThreshold(rs.getLong("THRESHOLD"));
        	alert.setTimeToAlertInMins(rs.getLong("TIME_TO_ALERT_IN_MINS"));
        	
            return alert;
		}
}


}
