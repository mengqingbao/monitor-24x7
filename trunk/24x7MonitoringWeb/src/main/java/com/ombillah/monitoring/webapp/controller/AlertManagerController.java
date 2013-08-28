package com.ombillah.monitoring.webapp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ombillah.monitoring.domain.ManagedAlert;
import com.ombillah.monitoring.service.AlertManagementService;

/**
 * Rest Client for Alerts Management
 * 
 * @author Oussama M Billah
 * 
 */
@Controller
public class AlertManagerController {

	@Autowired
	private AlertManagementService alertManagementService;
	
	@RequestMapping(value = "/json/saveAlert", method = RequestMethod.POST, produces = "application/json")
	@ResponseBody
	public String saveAlertSettings(
			@RequestBody ManagedAlert alert){

		alertManagementService.saveAlert(alert);
		return "success";
	}
	
	@RequestMapping(value = "/json/retrievAlertSettings/itemName/{itemName}/itemType/{itemType}", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public ManagedAlert getMonitoringInformation(
			@PathVariable("itemName") String itemName,
			@PathVariable("itemType") String itemType) {
		
		ManagedAlert alert = alertManagementService.retrieveAlert(itemName, itemType);
		return alert;
		
	}

}
