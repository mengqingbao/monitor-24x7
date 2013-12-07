package com.ombillah.monitoring.dao;

import java.util.List;

import com.ombillah.monitoring.domain.ManagedAlert;

/**
 * DAO to management Alert Management Data Access logic.
 * @author Oussama M Billah
 *
 */
public interface AlertManagementDAO {

	public void saveAlert(ManagedAlert alert);

	public ManagedAlert retrieveAlert(String itemName, String itemType);

	public List<ManagedAlert> getEnabledAlerts();
	
}
