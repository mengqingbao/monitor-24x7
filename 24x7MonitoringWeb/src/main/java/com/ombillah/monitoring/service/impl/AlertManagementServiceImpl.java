package com.ombillah.monitoring.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ombillah.monitoring.dao.AlertManagementDAO;
import com.ombillah.monitoring.domain.ManagedAlert;
import com.ombillah.monitoring.service.AlertManagementService;

/**
 * Service for Alert Management functionality.
 * @author Oussama M Billah.
 *
 */
@Service
public class AlertManagementServiceImpl implements AlertManagementService {
	
	@Autowired
	private AlertManagementDAO alertManagementDao;
	
	public void saveAlert(ManagedAlert alert) {
		alertManagementDao.saveAlert(alert);
	}

	public ManagedAlert retrieveAlert(String itemName, String itemType) {
		return alertManagementDao.retrieveAlert(itemName, itemType);
	}

}
