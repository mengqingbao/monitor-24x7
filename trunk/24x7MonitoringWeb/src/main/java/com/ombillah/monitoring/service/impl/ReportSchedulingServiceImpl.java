package com.ombillah.monitoring.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.quartz.CronTriggerBean;
import org.springframework.scheduling.quartz.MethodInvokingJobDetailFactoryBean;
import org.springframework.stereotype.Service;

import com.ombillah.monitoring.dao.ReportSchedulingDAO;
import com.ombillah.monitoring.domain.ReportContent;
import com.ombillah.monitoring.domain.ReportSchedule;
import com.ombillah.monitoring.jobs.ReportScheduledJob;
import com.ombillah.monitoring.service.ReportSchedulingService;

/**
 * Service for Report Scheduling functionality.
 * 
 * @author Oussama M Billah.
 * 
 */
@Service
public class ReportSchedulingServiceImpl implements ReportSchedulingService {

	@Autowired
	private ReportSchedulingDAO reportSchedulingDao;

	@Autowired
	private Scheduler scheduler;
	
	@Autowired
	private JavaMailSender mailSender;

	/**
	 * Reschedule the enabled Reports on Server restart.
	 */
	@PostConstruct
	public void reScheduleAllEnabledReportsOnStartup() {
		List<ReportSchedule> enabledReports = reportSchedulingDao.getEnabledReports();
		for(ReportSchedule report : enabledReports) {
			scheduleReport(report);
		}
	}
	
	public void saveReport(ReportSchedule report) {
		reportSchedulingDao.saveReport(report);
	}

	public ReportSchedule retrieveReport(String itemName, String itemType) {
		return reportSchedulingDao.retrieveReport(itemName, itemType);
	}

	public void scheduleReport(ReportSchedule report) {
		List<ReportContent> reportsContent = reportSchedulingDao.getReportContent(report);
		
		ReportScheduledJob task = new ReportScheduledJob();
		task.setReportSchedule(report);
		task.setMailSender(mailSender);
		task.setReports(reportsContent);
		
		MethodInvokingJobDetailFactoryBean jobDetail = new MethodInvokingJobDetailFactoryBean();
		Map<String, String> cronExpressionMap = new HashMap<String, String>();
		cronExpressionMap.put("hourly", "0 0 0/1 1/1 * ? *");
		cronExpressionMap.put("daily", String.format("	0 %s %s 1/1 * ? *", report.getMinute(), report.getHour()));
		cronExpressionMap.put("weekly", String.format("0 %s %s ? * %s *", report.getMinute(), report.getHour(), report.getDayOfWeek()));
		cronExpressionMap.put("monthly", String.format("0 %s %s %s 1/1 ? *", report.getMinute(), report.getHour(), report.getDayOfMonth()));
		try {

			// create JOB
			jobDetail.setTargetObject(task);
			jobDetail.setTargetMethod("sendReport");
			jobDetail.setName(report.getItemName());
			jobDetail.setGroup(report.getItemName() + "Group");
			jobDetail.setConcurrent(false);
			
			jobDetail.afterPropertiesSet();
			// create CRON Trigger
			CronTriggerBean cronTrigger = new CronTriggerBean();
			cronTrigger.setBeanName(report.getItemName());

			String expression = cronExpressionMap.get(report.getFrequency());
			cronTrigger.setCronExpression(expression);
			cronTrigger.afterPropertiesSet();
			
			scheduler.scheduleJob(jobDetail.getObject(), cronTrigger);
			
			if(!scheduler.isStarted()) {
				scheduler.start();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}
	
	public void unscheduleReport(String jobDetailName) {
		try {
			scheduler.deleteJob(jobDetailName, jobDetailName + "Group");
		} catch (SchedulerException e) {
			e.printStackTrace();
		}
	}


}
