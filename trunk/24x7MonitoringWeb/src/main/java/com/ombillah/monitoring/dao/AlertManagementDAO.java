package com.ombillah.monitoring.dao;

import com.ombillah.monitoring.domain.ManagedAlert;

/**
 * DAO to management Alert Management Data Access logic.
 * @author Oussama M Billah
 *
 */
public interface AlertManagementDAO {

	public void saveAlert(ManagedAlert alert);

	public ManagedAlert retrieveAlert(String itemName, String itemType);
	
}
