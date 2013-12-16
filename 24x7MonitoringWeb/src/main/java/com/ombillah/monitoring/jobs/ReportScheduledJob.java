package com.ombillah.monitoring.jobs;

import java.util.List;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import com.ombillah.monitoring.dao.ReportSchedulingDAO;
import com.ombillah.monitoring.domain.ReportContent;
import com.ombillah.monitoring.domain.ReportSchedule;

/**
 * Quartz Job that will send the scheduled report to the user.
 * 
 * @author Oussama M Billah
 * 
 */
@Component
@Scope("prototype")
public class ReportScheduledJob {

	@Autowired
	private JavaMailSender mailSender;
	
	@Autowired
	private ReportSchedulingDAO reportSchedulingDao;
	
	private ReportSchedule reportSchedule;

	public void setMailSender(JavaMailSender mailSender) {
		this.mailSender = mailSender;
	}

	public void setReportSchedule(ReportSchedule reportSchedule) {
		this.reportSchedule = reportSchedule;
	}
 

	public void sendReport() throws MessagingException {
		
		List<ReportContent> reportContents = reportSchedulingDao.getReportContent(reportSchedule);
		
		MimeMessage message = mailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message);
		helper.setTo(reportSchedule.getReportEmail());
		helper.setSubject(reportSchedule.getFrequency() + " Report for: " + reportSchedule.getItemName());
		if(reportContents.isEmpty()) {
			String body = "No data available for the given report!";
			helper.setText(body, false);
			mailSender.send(message);
			return;
		}
		String[] typesWithCount = {"JAVA", "SQL", "HTTP_REQUEST"};
		String body = "<b>" + reportSchedule.getItemName() + "</b><br/><br/>";
		body += "<tabe>" +
					"<tr>" +
						"<th style='padding:0 15px 0 10px;'>Time</th>" +
						"<th style='padding:0 15px 0 10px;'>Item Name</th>" +
						"<th style='padding:0 15px 0 10px;'>Average</th>";
		if(ArrayUtils.contains(typesWithCount, reportSchedule.getItemType())) {
			body += "<th style='padding:0 15px 0 10px;'>Count</th>";
		}
				
			body += "<th style='padding:0 15px 0 10px;'>Max</th>" +
					"<th style='padding:0 15px 0 10px;'>min</th>" +
					"</tr>";
		
		for(ReportContent report : reportContents) {
			body += "<tr>" +
						"<td style='padding:0 15px 0 10px;'>" + report.getReportTime() + "</td>" +
						"<td style='padding:0 15px 0 10px;'>" + report.getItemName() + "</td>" +
						"<td style='padding:0 15px 0 10px;'>" + report.getAverage() + "</td>";
			if(ArrayUtils.contains(typesWithCount, reportSchedule.getItemType())) {
				body += "<td style='padding:0 15px 0 10px;'>" + report.getCount() + "</td>";

			}
				body += "<td style='padding:0 15px 0 10px;'>" + report.getMax() + "</td>" +
						"<td style='padding:0 15px 0 10px;'>" + report.getMin() + "</td>" +
					"</tr>";
		}
		body += "</table>";
		helper.setText(body, true);
		mailSender.send(message);
	}
}
