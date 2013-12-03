package com.ombillah.monitoring.webapp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ombillah.monitoring.domain.ReportSchedule;
import com.ombillah.monitoring.service.ReportSchedulingService;

/**
 * Rest Client for Report Scheduling
 * 
 * @author Oussama M Billah
 * 
 */
@Controller
public class ReportScheduleController {

	@Autowired
	private ReportSchedulingService reportSchedulingService;
	
	@RequestMapping(value = "/json/saveReport", method = RequestMethod.POST, produces = "application/json")
	@ResponseBody
	public String saveReportSettings(
			@RequestBody ReportSchedule report){
		
		reportSchedulingService.saveReport(report);
		if(report.isEnabled()) {
			reportSchedulingService.scheduleReport(report);
		} else {
			reportSchedulingService.unscheduleReport(report.getItemName());
		}
		return "success";
	}
	
	@RequestMapping(value = "/json/retrieveReportSettings/itemName/{itemName}/itemType/{itemType}", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public ReportSchedule getReportInformation(
			@PathVariable("itemName") String itemName,
			@PathVariable("itemType") String itemType) {
		
		ReportSchedule report = reportSchedulingService.retrieveReport(itemName, itemType);
		return report;
		
	}

}
