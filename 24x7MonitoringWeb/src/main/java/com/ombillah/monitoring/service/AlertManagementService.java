package com.ombillah.monitoring.service;

import com.ombillah.monitoring.domain.ManagedAlert;

/**
 * Service for Alert Management functionality.
 * @author Oussama M Billah.
 *
 */
public interface AlertManagementService {
	
	public void saveAlert(ManagedAlert alert);
	
	public ManagedAlert retrieveAlert(String itemName, String itemType);
	
}
